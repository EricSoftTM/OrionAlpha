/*
 * This file is part of OrionAlpha, a MapleStory Emulator Project.
 * Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game.user;

import common.user.CharacterData;
import common.user.CharacterStat;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
import game.field.Creature;
import game.field.Field;
import game.field.FieldMan;
import game.field.GameObjectType;
import game.field.Stage;
import game.field.portal.Portal;
import game.field.portal.PortalMap;
import game.user.WvsContext.Request;
import game.user.stat.SecondaryStat;
import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.database.GameDB;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Rect;

/**
 *
 * @author Eric
 */
public class User extends Creature {
    private static final Lock lockUser = new ReentrantLock();
    private static final Map<Integer, User> users = new LinkedHashMap<>();
    private static final Map<String, User> userByName = new LinkedHashMap<>();
    
    private int accountID;
    private int characterID;
    private int gradeCode;
    private int localSocketSN;
    private byte channelID;
    private byte worldID;
    private ClientSocket socket;
    private final AvatarLook avatarLook;
    private final CharacterData character;
    private final SecondaryStat secondaryStat;
    private String characterName;
    private final Lock lock;
    private final Lock lockSocket;
    private boolean hide;
    private boolean adminHide;
    private Point curPos;
    private byte moveAction;
    private short footholdSN;
    private int characterDataModFlag;
    
    protected User(int characterID) {
        super();
        
        this.worldID = 0;
        this.channelID = 0;
        
        this.curPos = new Point(0, 0);
        this.lock = new ReentrantLock();
        this.lockSocket = new ReentrantLock();
        this.secondaryStat = new SecondaryStat();
        this.avatarLook = new AvatarLook();
        this.character = GameDB.rawLoadCharacter(characterID);
    }
    
    public User(ClientSocket socket) {
        this(socket.getCharacterID());
        
        this.socket = socket;
        this.localSocketSN = socket.getLocalSocketSN();
        
        this.characterID = character.getCharacterStat().getCharacterID();
        this.characterName = character.getCharacterStat().getName();
    }
    
    public final void destructUser() {
        //flushCharacterData(0, true);
        Logger.logReport("User logout");
        
    }
    
    public static final void broadcast(OutPacket packet) {
        lockUser.lock();
        try {
            for (User user : users.values()) {
                if (user != null) {
                    user.sendPacket(packet);
                }
            }
        } finally {
            lockUser.unlock();
        }
    }
    
    public static final void broadcastGMPacket(OutPacket packet) {
        lockUser.lock();
        try {
            for (User user : users.values()) {
                if (user != null && user.isGM()) {
                    user.sendPacket(packet);
                }
            }
        } finally {
            lockUser.unlock();
        }
    }
    
    public static synchronized final User findUser(int characterID) {
        lockUser.lock();
        try {
            if (users.containsKey(characterID)) {
                User user = users.get(characterID);
                if (user != null) {
                    return user;
                }
            }
            return null;
        } finally {
            lockUser.unlock();
        }
    }
    
    public static synchronized final User findUserByName(String name, boolean makeLower) {
        lockUser.lock();
        try {
            if (makeLower)
                name = name.toLowerCase();
            if (userByName.containsKey(name)) {
                User user = userByName.get(name);
                if (user != null) {
                    return user;
                }
            }
            return null;
        } finally {
            lockUser.unlock();
        }
    }
    
    public static final Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }
    
    public static synchronized final boolean registerUser(User user) {
        lockUser.lock();
        try {
            if (users.containsKey(user.characterID)) {
                return false;
            } else {
                users.put(user.characterID, user);
                userByName.put(user.characterName.toLowerCase(), user);
                return true;
            }
        } finally {
            lockUser.unlock();
        }
    }
    
    public static final void unregisterUser(User user) {
        lockUser.lock();
        try {
            users.remove(user.characterID);
            userByName.remove(user.characterName.toLowerCase());
        } finally {
            lockUser.unlock();
        }
    }
    
    public void destroyAdditionalProcess() {
        lock.lock();
        try {
            // TODO: Close, Reset, Leave, and Destroy active processes.
        } finally {
            unlock();
        }
    }
    
    public void closeSocket() {
        lockSocket.lock();
        try {
            if (socket != null) {
                socket.postClose();
            }
        } finally {
            lockSocket.unlock();
        }
    }
    
    public final boolean lock() {
        return lock(700);
    }
    
    public final boolean lock(long timeout) {
        try {
            return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public final void unlock() {
        lock.unlock();
    }
    
    public AvatarLook getAvatarLook() {
        return avatarLook;
    }
    
    public byte getChannelID() {
        return channelID;
    }
    
    public CharacterData getCharacter() {
        return character;
    }
    
    public int getCharacterID() {
        return characterID;
    }
    
    public String getCharacterName() {
        return characterName;
    }
    
    public short getHP() {
        return character.getCharacterStat().getHP();
    }
    
    public int getLocalSocketSN() {
        return localSocketSN;
    }
    
    public int getPosMap() {
        return character.getCharacterStat().getPosMap();
    }
    
    public byte getPortal() {
        return character.getCharacterStat().getPortal();
    }
    
    public SecondaryStat getSecondaryStat() {
        return secondaryStat;
    }
    
    public ClientSocket getSocket() {
        return socket;
    }
    
    public byte getWorldID() {
        return worldID;
    }
    
    public boolean isGM() {
        return gradeCode >= 3;//TODO: Proper GradeCode
    }
    
    public void leaveField() {
        if (getField() != null) {
            getField().onLeave(this);
            if (getField().getForcedReturnFieldID() == Field.Invalid) {
                
            } else {
                
            }
        }
    }
    
    @Override
    public boolean isShowTo(User user) {
        if (hide && user.gradeCode >= gradeCode) {
            return true;
        } else if ((hide && adminHide) && user.isGM()) {
            return false;
        }
        return !hide || hide && user.isGM();
    }

    @Override
    public OutPacket makeLeaveFieldPacket() {
        return UserPool.onUserLeaveField(characterID);
    }

    @Override
    public OutPacket makeEnterFieldPacket() {
        return UserPool.onUserEnterField(this);
    }
    
    public boolean setMovePosition(int x, int y, byte moveAction, short sn) {
        if (lock()) {
            try {
                this.curPos.x = x;
                this.curPos.y = y;
                this.moveAction = moveAction;
                this.footholdSN = sn;
                return true;
            } finally {
                unlock();
            }
        }
        return false;
    }
    
    public Point getCurrentPosition() {
        return curPos;
    }
    
    public byte getMoveAction() {
        return moveAction;
    }
    
    public short getFootholdSN() {
        return footholdSN;
    }
    
    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.User;
    }
    
    @Override
    public String toString() {
        return character.getCharacterStat().getName();
    }
    
    public void onMigrateInSuccess() {
        Logger.logReport("User login from (%s)", socket.getAddr());
        if (getPosMap() / 10000000 == 99) {
            setPosMap(990001100);
        }
        Field field = FieldMan.getInstance().getField(getPosMap(), false);
        setField(field);
        if (getHP() == 0) {
            character.getCharacterStat().setHP((short) 50);
        }
        if (getField() == null) {
            field = FieldMan.getInstance().getField(104000000, false);
            setField(field);
            setPosMap(field.getFieldID());
            setPortal(field.getPortal().getRandStartPoint().getPortalIdx());
        }
        characterDataModFlag |= DBChar.Character;
        // TODO: Check level, initialize exp
        PortalMap portal = getField().getPortal();
        int count = 0;
        if (!portal.getPortal().isEmpty()) {
            count = portal.getPortal().size();
        }
        byte portalIdx = getPortal();
        if (portalIdx <= 0)
            portalIdx = 0;
        int idx = count - 1;
        if (portalIdx < idx)
            idx = portalIdx;
        if (portal.getPortal().get(idx).getPortalType() > 0) {
            idx = portal.getRandStartPoint().getPortalIdx();
        }
        setPortal((byte) idx);
        curPos.x = portal.getPortal().get(idx).getPortalPos().x;
        curPos.y = portal.getPortal().get(idx).getPortalPos().y;
        moveAction = 0;
        footholdSN = 0;
        sendSetFieldPacket(true);
        // TODO: Load anything else i'm missing
        if (getField() != null && isGM()) {
            if (!hide) {
                //setHide(true, isCreator());
            }
        }
        if (getField().onEnter(this)) {
            // TODO: Load messenger and other stuff
        } else {
            Logger.logError("Failed in entering field");
            closeSocket();
        }
    }
    
    public void onSocketDestroyed(boolean migrate) {
        if (lock()) {
            try {
                destroyAdditionalProcess();
                leaveField();
                GameObjectBase.unregisterGameObject(this);
                User.unregisterUser(this);
            } finally {
                unlock();
            }
        }
        lockSocket.lock();
        try {
            socket = null;
        } finally {
            lockSocket.unlock();
        }
    }
    
    public void onFieldPacket(byte type, InPacket packet) {
        if (getField() != null) {
            getField().onPacket(this, type, packet);
        }
    }
    
    public void onPacket(byte type, InPacket packet) {
        switch (type) {
            case ClientPacket.UserTransferFieldRequest:
                onTransferFieldRequest(packet);
                break;
            case ClientPacket.UserMove:
                onMove(packet);
                break;
            case ClientPacket.UserChat:
                onChat(packet);
                break;
            default: {
                if (type >= ClientPacket.BEGIN_FIELD && type <= ClientPacket.END_FIELD) {
                    onFieldPacket(type, packet);
                }
            }
        }
    }
    
    public void onChat(InPacket packet) {
        if (getField() == null) {
            return;
        }
        String text = packet.decodeString();
        
        if (text.startsWith("!")) {
            // TODO: Command processing system
            
            String[] arg = text.split(" ");
            if (arg[0].equals("!level")) {
                if (arg.length > 1) {
                    character.getCharacterStat().setLevel(Byte.parseByte(arg[1]));
                } else {
                    character.getCharacterStat().setLevel((byte) 99);
                }
                sendPacket(WvsContext.onStatChanged(Request.None, character.getCharacterStat(), secondaryStat, CharacterStatType.LEV));
            }
            if (arg[0].equals("!job")) {
                if (arg.length > 1) {
                    character.getCharacterStat().setJob(Short.parseShort(arg[1]));
                    sendPacket(WvsContext.onStatChanged(Request.None, character.getCharacterStat(), secondaryStat, CharacterStatType.Job));
                }
            }
        }
        
        getField().splitSendPacket(getSplit(), UserCommon.onChat(characterID, text), null);
    }
    
    public void onTransferFieldRequest(InPacket packet) {
        int fieldID = packet.decodeInt();
        String portal = packet.decodeString();
        
        if (fieldID == -1) {
            Portal pt = getField().getPortal().findPortal(portal);
            if (pt == null || pt.tmap == Field.Invalid) {
                return;
            }
            fieldID = pt.tmap;
            portal = pt.tname;
        }
        
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field != null) {
            Portal pt = field.getPortal().findPortal(portal);
            if (portal.isEmpty() || pt == null) {
                pt = field.getPortal().getRandStartPoint();
            }
            getField().onLeave(this);
            lock.lock();
            try {
                setField(field);
                setPosMap(field.getFieldID());
                setPortal(pt.getPortalIdx());
                setMovePosition(pt.getPortalPos().x, pt.getPortalPos().y, (byte) 0, (short) 0);
                characterDataModFlag |= DBChar.Character;
                //avatarModFlag = 0;
            } finally {
                unlock();
            }
            sendSetFieldPacket(false);
            if (getField().onEnter(this)) {
                
            } else {
                Logger.logError("Failed in entering field");
                closeSocket();
            }
        }
    }
    
    public void onMove(InPacket packet) {
        if (getField() != null) {
            Rect move = new Rect();
            getField().onUserMove(this, packet, move);
        }
    }
    
    public void sendCharacterHidePacket() {
        // Actually, AdminResult probably doesn't exist yet..
    }
    
    public void sendSetFieldPacket(boolean characterData) {
        if (characterData) {
            int s1 = 1;
            int s2 = 2;
            int s3 = 3;
            
            sendPacket(Stage.onSetField(this, true, s1, s2, s3));
        } else {
            sendPacket(Stage.onSetField(this, false, -1, -1, -1));
        }
        if (hide) {
            sendCharacterHidePacket();
        }
    }
    
    public void sendPacket(OutPacket packet) {
        lockSocket.lock();
        try {
            if (socket != null) {
                socket.sendPacket(packet, false);
            }
        } finally {
            lockSocket.unlock();
        }
    }
    
    public void setPosMap(int map) {
        character.getCharacterStat().setPosMap(map);
    }
    
    public void setPortal(byte portal) {
        character.getCharacterStat().setPortal(portal);
    }
    
    public void update(long time) {
        
    }
    
    public class UserEffect {
        public static final byte
                LevelUp         = 0,
                SkillUse        = 1,
                SkillAffected   = 2
        ;
    }
}
