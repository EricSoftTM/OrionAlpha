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

import common.item.BodyPart;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
import game.GameApp;
import game.field.Creature;
import game.field.Field;
import game.field.FieldMan;
import game.field.GameObjectType;
import game.field.Stage;
import game.field.drop.Drop;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.field.life.AttackIndex;
import game.field.life.AttackInfo;
import game.field.life.mob.Mob;
import game.field.portal.Portal;
import game.field.portal.PortalMap;
import game.user.WvsContext.Request;
import game.user.item.ChangeLog;
import game.user.item.InventoryManipulator;
import game.user.skill.SkillEntry;
import game.user.skill.Skills.*;
import game.user.skill.UserSkill;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.SecondaryStat;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.database.GameDB;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;
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
    // Account/Character Names
    private String nexonClubID;
    private String characterName;
    // Misc. Variables
    private final Lock lock;
    private final Lock lockSocket;
    private long lastSelectNPCTime;
    private boolean onTransferField;
    private int incorrectFieldPositionCount;
    private long lastCharacterDataFlush;
    private long nextGeneralItemCheck;
    private long nextCheckCashItemExpire;
    private String community;
    // Skills
    private final UserSkill userSkill;
    private long lastAttack;
    private long lastAttackTime;
    private long lastAttackDelay;
    private long finalAttackDelay;
    private int attackCheckIgnoreCnt;
    private int attackSpeedErr;
    // Hide
    private boolean hide;
    private boolean adminHide;
    // User Emotions
    private int emotion;
    // User-specific rates
    private double incExpRate = 1.0d;
    private double incMesoRate = 1.0d;
    private double incDropRate = 1.0d;
    private double incDropRate_Ticket = 1.0d;
    private int incEXPRate = 100;
    // Trade Limits
    private int tradeMoneyLimit;
    private int tempTradeMoney;
    // Cheat Inspector
    // private CheatInspector cheatInspector;
    private int invalidTryRepeatCount;
    private int invalidUserActionCount;
    private int invalidMobMoveCount;
    private int invalidHitPointCount;
    private int skipWarpCount;
    private int warpCheckedCount;
    private int invalidDamageCount;
    // Character Data
    private final CharacterData character;
    private int characterDataModFlag;
    // Avatar Look
    private final AvatarLook avatarLook;
    private int avatarModFlag;
    // Basic Stat
    // private final BasicStat basicStat;
    // Secondary Stat
    private final SecondaryStat secondaryStat;
    // User RNG's
    private final Rand32 rndActionMan;
    // Mini Rooms
    // private UserMiniRoom userMR;
    // private MiniRoomBase miniRoom;
    private boolean miniRoomBalloon;
    // MSMessenger
    // private UserMessenger msm;
    private boolean msMessenger;
    // ScriptVM
    // private ScriptVM runningVM;
    // Client
    private ClientSocket socket;
    private long loginTime;
    private long logoutTime;
    private boolean closeSocketNextTime;
    private boolean temporaryLogging;
    // Movement
    private Point curPos;
    private byte moveAction;
    private short footholdSN;
    
    protected User(int characterID) {
        super();
        
        this.worldID = 0;
        this.channelID = 0;
        
        this.hide = false;
        this.adminHide = false;
        this.onTransferField = false;
        this.closeSocketNextTime = false;
        
        this.emotion = 0;
        this.invalidHitPointCount = 0;
        this.invalidMobMoveCount = 0;
        this.skipWarpCount = 0;
        this.warpCheckedCount = 0;
        this.invalidDamageCount = 0;
        this.tradeMoneyLimit = 0;
        this.tempTradeMoney = 0;
        this.accountID = -1;
        this.incorrectFieldPositionCount = 0;
        this.avatarModFlag = 0;
        this.characterDataModFlag = 0;
        this.lastSelectNPCTime = 0;
        
        long time = System.currentTimeMillis();
        this.lastCharacterDataFlush = time;
        this.nextCheckCashItemExpire = time;
        this.nexonClubID = "";
        this.characterName = "";
        this.community = "#TeamEric";
        
        this.curPos = new Point(0, 0);
        this.lock = new ReentrantLock();
        this.lockSocket = new ReentrantLock();
        this.secondaryStat = new SecondaryStat();
        this.avatarLook = new AvatarLook();
        this.rndActionMan = new Rand32();
        this.userSkill = new UserSkill(this);
        // TODO: Nexon-like user caching to avoid DB load upon each login/migrate.
        this.character = GameDB.rawLoadCharacter(characterID);
    }
    
    public User(ClientSocket socket) {
        this(socket.getCharacterID());
        
        this.socket = socket;
        this.localSocketSN = socket.getLocalSocketSN();
        
        this.characterID = character.getCharacterStat().getCharacterID();
        this.characterName = character.getCharacterStat().getName();
        
        this.validateStat(true);
    }
    
    public final void destructUser() {
        flushCharacterData(0, true);
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
    
    ///////////////////////////// CQWUser START /////////////////////////////
    public boolean canStatChange(int inc, int dec) {
        // TODO
        
        return false;
    }
    
    public byte getLevel() {
        return character.getCharacterStat().getLevel();
    }
    
    public boolean incAP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int ap = character.getCharacterStat().getAP();
            if (onlyFull && (ap + inc < 0 || ap + inc > 999)) {
                return false;
            }
            int newSP = Math.max(Math.min(ap + inc, 999), 0);
            character.getCharacterStat().setAP((short) newSP);
            if (newSP == ap) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public boolean incDEX(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int DEX = character.getCharacterStat().getDEX();
            if (onlyFull && (DEX + inc < 0 || DEX + inc > 999)) {
                return false;
            }
            int newDEX = Math.max(Math.min(DEX + inc, 999), 0);
            character.getCharacterStat().setDEX((short) newDEX);
            if (newDEX == DEX) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public int incEXP(int inc, boolean onlyFull) {
        // TODO
        
        return 0;
    }
    
    public boolean incHP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int hp = character.getCharacterStat().getHP();
            int mhp = character.getCharacterStat().getMHP();
            if (hp == 0) {
                return false;
            }
            if (onlyFull && (inc < 0 && hp + inc < 0 || inc > 0 && hp + inc > mhp)) {
                return false;
            }
            int newHP = Math.max(Math.min(hp + inc, mhp), 0);
            character.getCharacterStat().setHP((short) newHP);
            if (newHP == 0) {
                //cheatInspector.initUserDamagedTime(0, false);
                onUserDead();
            }
            if (newHP == hp) {
                return false;
            }
            characterDataModFlag |= DBChar.Character;
            return true;
        } finally {
            unlock();
        }
    }
    
    public boolean incINT(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int INT = character.getCharacterStat().getINT();
            if (onlyFull && (INT + inc < 0 || INT + inc > 999)) {
                return false;
            }
            int newINT = Math.max(Math.min(INT + inc, 999), 0);
            character.getCharacterStat().setINT((short) newINT);
            if (newINT == INT) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public boolean incLUK(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int LUK = character.getCharacterStat().getLUK();
            if (onlyFull && (LUK + inc < 0 || LUK + inc > 999)) {
                return false;
            }
            int newLUK = Math.max(Math.min(LUK + inc, 999), 0);
            character.getCharacterStat().setLUK((short) newLUK);
            if (newLUK == LUK) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public boolean incMHP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int mhp = character.getCharacterStat().getMHP();
            if (onlyFull && (mhp + inc < 50 || mhp + inc > 30000)) {
                return false;
            }
            int newMHP = Math.max(Math.min(mhp + inc, 30000), 50);
            character.getCharacterStat().setMHP((short) newMHP);
            if (newMHP == mhp) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public boolean incMMP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int mmp = character.getCharacterStat().getMMP();
            if (onlyFull && (mmp + inc < 5 || mmp + inc > 30000)) {
                return false;
            }
            int newMMP = Math.max(Math.min(mmp + inc, 30000), 5);
            character.getCharacterStat().setMMP((short) newMMP);
            if (newMMP == mmp) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public boolean incMP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            if (inc < 0 && getHP() <= 0) {
                return false;
            }
            int mp = character.getCharacterStat().getMP();
            int mmp = character.getCharacterStat().getMMP();
            if (onlyFull && (inc < 0 && mp + inc < 0 || inc > 0 && mp + inc > mmp)) {
                return false;
            }
            int newMP = Math.max(Math.min(mp + inc, mmp), 0);
            character.getCharacterStat().setMP((short) newMP);
            if (newMP == mp) {
                return false;
            }
            characterDataModFlag |= DBChar.Character;
            return true;
        } finally {
            unlock();
        }
    }
    
    public boolean incMoney(int inc, boolean onlyFull) {
        return incMoney(inc, onlyFull, false);
    }
    
    public boolean incMoney(int inc, boolean onlyFull, boolean totalMoneyChange) {
        lock.lock();
        try {
            if (InventoryManipulator.rawIncMoney(character, inc, onlyFull)) {
                characterDataModFlag |= DBChar.Character;
                return true;
            }
            return false;
        } finally {
            unlock();
        }
    }
    
    public boolean incPOP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int pop = character.getCharacterStat().getPOP();
            if (onlyFull && (pop + inc < -30000 || pop + inc > 30000)) {
                return false;
            }
            int newPOP = Math.min(Math.max(pop + inc, -30000), 30000);
            character.getCharacterStat().setPOP((short) newPOP);
            if (newPOP == pop) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public boolean incSP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int sp = character.getCharacterStat().getSP();
            if (onlyFull && (sp + inc < 0 || sp + inc > 999)) {
                return false;
            }
            int newSP = Math.max(Math.min(sp + inc, 999), 0);
            character.getCharacterStat().setSP((short) newSP);
            if (newSP == sp) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public boolean incSTR(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int STR = character.getCharacterStat().getSTR();
            if (onlyFull && (STR + inc < 0 || STR + inc > 999)) {
                return false;
            }
            int newSTR = Math.max(Math.min(STR + inc, 999), 0);
            character.getCharacterStat().setSTR((short) newSTR);
            if (newSTR == STR) {
                return false;
            } else {
                characterDataModFlag |= DBChar.Character;
                validateStat(false);
                return true;
            }
        } finally {
            unlock();
        }
    }
    
    public int initEXP() {
        if (lock()) {
            try {
                if (character.getCharacterStat().getLevel() >= 200 && character.getCharacterStat().getEXP() > 0) {
                    character.getCharacterStat().setEXP(0);
                    characterDataModFlag |= DBChar.Character;
                    return CharacterStatType.EXP;
                }
            } finally {
                unlock();
            }
        }
        return 0;
    }
    
    public boolean isValidStat(int STR, int DEX, int INT, int LUK, int remainAP) {
        // TODO
        return false;
    }
    
    public void setFace(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setFace(val);
            validateStat(false);
        } finally {
            unlock();
        }
    }
    
    public void setGender(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setGender((byte) val);
            validateStat(false);
        } finally {
            unlock();
        }
    }
    
    public void setHair(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setHair(val);
            validateStat(false);
        } finally {
            unlock();
        }
    }
    
    public void setJob(int val) {
        // TODO
    }
    
    public void setSkin(int val) {
        lock.lock();
        try {
            characterDataModFlag |= DBChar.Character;
            character.getCharacterStat().setSkin((byte) val);
            validateStat(false);
        } finally {
            unlock();
        }
    }
    
    public void statChange(int inc, int dec, short incHP, short incMP) {
        lock.lock();
        try {
            switch (inc) {
                case CharacterStatType.STR:
                    incSTR(1, true);
                    break;
                case CharacterStatType.DEX:
                    incDEX(1, true);
                    break;
                case CharacterStatType.INT:
                    incINT(1, true);
                    break;
                case CharacterStatType.LUK:
                    incLUK(1, true);
                    break;
            }
            switch (inc) {
                case CharacterStatType.STR:
                    incSTR(-1, true);
                    break;
                case CharacterStatType.DEX:
                    incDEX(-1, true);
                    break;
                case CharacterStatType.INT:
                    incINT(-1, true);
                    break;
                case CharacterStatType.LUK:
                    incLUK(-1, true);
                    break;
            }
            if (inc == CharacterStatType.MHP || dec == CharacterStatType.MHP)
                character.getCharacterStat().setMHP((short) (character.getCharacterStat().getMHP() + incHP));
            if (inc == CharacterStatType.MMP || dec == CharacterStatType.MMP)
                character.getCharacterStat().setMMP((short) (character.getCharacterStat().getMMP() + incMP));
        } finally {
            unlock();
        }
    }
    ///////////////////////////// CQWUser END ///////////////////////
    
    public void addCharacterDataMod(int flag) {
        this.characterDataModFlag |= flag;
    }
    
    public boolean canAttachAdditionalProcess() {
        if (socket != null && !onTransferField && getHP() > 0) {
            
        }
        return false;
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
    
    public boolean isAdminHide() {
        return adminHide;
    }
    
    public boolean isHide() {
        return hide;
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
    
    public String getCommunity() {
        return community;
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
        if (character.getCharacterStat().getLevel() >= 200 && character.getCharacterStat().getEXP() > 0) {
            initEXP();
        }
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
            case ClientPacket.UserEmotion:
                onEmotion(packet);
                break;
            case ClientPacket.UserDropMoneyRequest:
                onDropMoneyRequest(packet);
                break;
            case ClientPacket.UserSkillUpRequest:
                userSkill.onSkillUpRequest(packet);
                break;
            case ClientPacket.UserHit:
                onHit(packet);
                break;
            case ClientPacket.UserMeleeAttack:
            case ClientPacket.UserShootAttack:
            case ClientPacket.UserMagicAttack:
                onAttack(type, packet);
                break;
            case ClientPacket.UserSkillUseRequest:
                userSkill.onSkillUseRequest(packet);
                break;
            case ClientPacket.UserCharacterInfoRequest:
                onCharacterInfoRequest(packet);
                break;
            default: {
                if (type >= ClientPacket.BEGIN_FIELD && type <= ClientPacket.END_FIELD) {
                    onFieldPacket(type, packet);
                } else {
                    Logger.logReport("[Unidentified Packet] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
                }
            }
        }
    }
    
    public void onAttack(byte type, InPacket packet) {
        byte attackInfo = packet.decodeByte();//nDamagePerMob | 16 * nMobCount
        byte damagePerMob = (byte) (attackInfo & 0xF);
        byte mobCount = (byte) ((attackInfo >>> 4) & 0xF);
        int skillID = packet.decodeInt();
        
        this.lastAttack = System.currentTimeMillis();
        int bulletItemID = 0;
        
        boolean darkSight = false;//TODO: SecondaryStat checking
        if (darkSight) {
            //sendTemporaryStatReset(secondaryStat.resetByCTS(CharacterTemporaryStat.DarkSight));
        }
        
        byte action = packet.decodeByte();//((_BYTE)bLeft << 7) | nAction & 0x7F
        byte left = (byte) ((action >> 7) & 1);
        byte attackActionType = packet.decodeByte();
        
        if (getHP() > 0 && getField() != null) {
            if (skillID == Fighter.FinalAttack || skillID == Fighter.FinalAttackEx || skillID == Page.FinalAttack || skillID == Page.FinalAttackEx 
                    || skillID == Spearman.FinalAttack || skillID == Spearman.FinalAttackEx || skillID == Hunter.FinalAttack_Bow || skillID == Crossbowman.FinalAttack_Crossbow)
                    this.lastAttackDelay = this.finalAttackDelay;
            long attackTime = System.currentTimeMillis();
            if (attackCheckIgnoreCnt <= 0) {
                if (attackTime - lastAttackTime >= lastAttackDelay) {
                    attackSpeedErr = 0;
                } else {
                    if (attackSpeedErr == 2) {
                        Logger.logError("[ User ] user's attack speed is abnormally fast [ name=%s, actionNo=%d, skillID=%d, userDelay=%d < minDelay=%d, finalAttack=%d, boosterLevel=%d, fieldid=%d ]");
                        return;
                    }
                    attackSpeedErr++;
                }
            } else {
                attackCheckIgnoreCnt--;
            }
            lastAttackTime = attackTime;
            lastAttackDelay = 0;
            finalAttackDelay = 0;
            byte bulletItemPos = 0;
            byte slv = 0;
            
            if (type == ClientPacket.UserShootAttack) {
                
            }
            
            List<AttackInfo> attack = new ArrayList<>(mobCount);
            for (int i = 0; i < mobCount; i++) {
                AttackInfo info = new AttackInfo();
                info.mobID = packet.decodeInt();
                Mob mob = null;//TODO: getField().getLifePool().getMob(info.mobID);
                if (mob != null) {
                    info.templateID = mob.getTemplateID();
                }
                info.hitAction = packet.decodeByte();
                info.hit.x = packet.decodeShort();
                info.hit.y = packet.decodeShort();
                info.delay = packet.decodeShort();
                info.attackCount = damagePerMob;
                for (int j = 0; j < damagePerMob; j++) {
                    info.damageCli.add(packet.decodeShort());
                }
                attack.add(info);
            }
            
            Point ballStart = new Point();
            if (type == ClientPacket.UserShootAttack) {
                // ptStart? TODO Check ShootAttack decodes
            }
            
            if (bulletItemPos > 0) {
                
            }
            
            byte attackType;
            if (type == ClientPacket.UserMagicAttack) {
                attackType = LoopbackPacket.UserMagicAttack;
            } else if (type == ClientPacket.UserShootAttack) {
                attackType = LoopbackPacket.UserShootAttack;
            } else if (type == ClientPacket.UserMeleeAttack) {
                attackType = LoopbackPacket.UserMeleeAttack;
            } else {
                Logger.logError("Attack Type Error %d", type);
                return;
            }
            
            SkillEntry skill = null;
            int maxCount = 1;
            if (skillID > 0) {
                slv = 0;
                skill = null;
                if (skill != null) {
                    //maxCount = skillData.mobCount
                }
            }
            if (maxCount <= 1)
                maxCount = 1;
            if (maxCount < mobCount) {
                Logger.logError("Invalid mob count (Skill:%d,Lv:%d,Mob:%d)", skillID, slv, -1);
            } else {
                if (getField().getLifePool().onUserAttack(this, attackType, mobCount, damagePerMob, skill, slv, action, left, bulletItemID, attack, ballStart)) {
                    
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
            if (arg[0].equals("!fixme")) {
                sendPacket(WvsContext.onStatChanged(Request.Excl, null, null, 0));
            }
        }
        
        getField().splitSendPacket(getSplit(), UserCommon.onChat(characterID, text), null);
    }
    
    public void onCharacterInfoRequest(InPacket packet) {
        User target = User.findUser(packet.decodeInt());
        if (target == null || target.isGM()) {
            sendCharacterStat(Request.Excl, 0);
        } else {
            target.lock.lock();
            try {
                sendPacket(WvsContext.onCharacterInfo(target));
            } finally {
                target.unlock();
            }
        }
    }
    
    public void onEmotion(InPacket packet) {
        if (getField() != null) {
            emotion = packet.decodeInt();
            if (emotion < 8) {
                getField().splitSendPacket(getSplit(), UserRemote.onEmotion(this.characterID, emotion), this);
            }
        }
    }
    
    public void onDropMoneyRequest(InPacket packet) {
        if (getHP() == 0) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        int amount = packet.decodeInt();
        if (amount >= 10 && amount <= 50000) {
            if (character.getCharacterStat().getLevel() <= 15) {
                // not sure if this even exists actually
            }
            if (getField() != null) {
                Pointer<Integer> y2 = new Pointer<>(0);
                if (getField().getSpace2D().getFootholdUnderneath(getCurrentPosition().x, getCurrentPosition().y, y2) != null) {
                    if (!incMoney(-amount, true, true)) {
                        return;
                    }
                    sendCharacterStat(Request.Excl, CharacterStatType.Money);
                    Reward reward = new Reward();
                    reward.setMoney(amount);
                    reward.setType(RewardType.Money);
                    reward.setPeriod(0);
                    int x = getCurrentPosition().x;
                    int y1 = getCurrentPosition().y;
                    getField().getDropPool().create(reward, this.characterID, 0, x, y1, x, y2.get(), 0, false, 0);
                }
            }
        }
    }
    
    public void onHit(InPacket packet) {
        byte mobAttackIdx = packet.decodeByte();
        int obstacleData = 0;
        int clientDamage = 0;
        int mobTemplateID = 0;
        byte left = 0;
        byte reflect = 0;
        int mobID = 0;
        byte hitAction = 0;
        Point hit = new Point(0, 0);
        if (mobAttackIdx <= AttackIndex.Counter) {
            obstacleData = packet.decodeInt();
        } else {
            clientDamage = packet.decodeInt();
            mobTemplateID = packet.decodeInt();
            left = packet.decodeByte();
            reflect = packet.decodeByte();
            if (reflect != 0) {
                mobID = packet.decodeInt();
                hitAction = packet.decodeByte();
                hit.x = packet.decodeShort();
                hit.y = packet.decodeShort();
            }
        }
        if (getField() == null) {
            return;
        }
        if (lock()) {
            try {
                if (getHP() > 0) {
                    // TODO: Implement all the checks here..
                    // For now, we'll just simply deduct whatever damage was received.
                    
                    incHP(-clientDamage, false);
                }
            } finally {
                unlock();
            }
        }
        getField().splitSendPacket(getSplit(), UserRemote.onHit(this.characterID, mobAttackIdx, clientDamage, mobTemplateID, left, reflect, mobID, hitAction, hit), this);
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
                avatarModFlag = 0;
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
    
    public void sendCharacterStat(byte request, int flag) {
        lock.lock();
        try {
            character.getCharacterStat().setMoney(character.getCharacterStat().getMoney() - character.getMoneyTrading());
            sendPacket(WvsContext.onStatChanged(request, character.getCharacterStat(), null, flag));
            character.getCharacterStat().setMoney(character.getCharacterStat().getMoney() + character.getMoneyTrading());
        } finally {
            unlock();
        }
    }
    
    public boolean sendDropPickUpResultPacket(Drop pr, byte onExclRequest) {
        boolean pickUp = false;
        lock.lock();
        try {
            if (pr == null) {
                //Inventory.sendInventoryOperation(this, onExclRequest, null);
                return pickUp;
            }
            List<ChangeLog> changeLog = new ArrayList<>();
            Pointer<Integer> incRet = new Pointer<>(0);
            if (pr.isMoney()) {
                int money = pr.getMoney();
                if (pr.getMoney() == 0) {
                    if (pr.getItem() != null)
                        money = pr.getItem().getItemID();//wtf u doing here nexon
                    else
                        money = 0;
                }
                incMoney(money, false, true);
                sendCharacterStat(onExclRequest, CharacterStatType.Money);
                pickUp = true;
            } else {
                if (pr.getItem() != null) {
                    ItemSlotBase item = pr.getItem().makeClone();
                    int ti = item.getItemID() / 1000000;
                    /* TODO: Handle inventories
                    if (InventoryManipulator.RawAddItem(character, nTI, pItem, aChangeLog, nIncRet)) {
                            pr.pItem.SetItemNumber(pr.pItem.GetItemNumber() - nIncRet.Get());//-= nIncRet

                            if (ItemConstants.IsTreatSingly(pr.pItem) || pr.pItem.GetItemNumber() <= 0) {
                                bPickUp = true;
                            }
                            usCharacterDataModFlag |= ItemConstants.get_item_type_from_typeindex(nTI);
                            if (ItemConstants.is_javelin_item(pr.pItem.nItemID) && nIncRet.Get() == 0)
                                bConsumeOnPickup = true;
                            if (pr.dwSourceID == 0 && pr.nOwnType == Drop.UserOwn) {
                                User pUser = User.FindUser(pr.nOwnType == Drop.UserOwn ? pr.dwOwnerID : pr.dwOwnPartyID);
                                if (pUser != null) {
                                    pUser.FlushCharacterData(0, true);
                                }
                            }
                        }
                    */
                }
                //Inventory.sendInventoryOperation(this, onExclRequest, changeLog);
                changeLog.clear();
            }
            if (pr.isMoney() || incRet.get() > 0) {
                // TODO: Find OnDropPickUpMessage T_T
            }
            return pickUp;
        } finally {
            lock.unlock();
        }
    }
    
    public void sendDropPickUpFailPacket(byte onExclRequest) {
        sendCharacterStat(onExclRequest, 0);
        // TODO: WvsContext.OnDropPickUpMessage, does it exist? 
    }
    
    public void sendSetFieldPacket(boolean characterData) {
        if (characterData) {
            int s1 = Rand32.getInstance().random().intValue();
            int s2 = Rand32.getInstance().random().intValue();
            int s3 = Rand32.getInstance().random().intValue();
            
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
    
    public void setCommunity(String name) {
        this.community = name;
    }
    
    public void setPosMap(int map) {
        character.getCharacterStat().setPosMap(map);
    }
    
    public void setPortal(byte portal) {
        character.getCharacterStat().setPortal(portal);
    }
    
    public boolean isWearItemOnNeed(int necessaryItemID) {
        int i = 0;
        while (character.getEquipped().get(i) == null || character.getEquipped().get(i).getItemID() != necessaryItemID) {
            ++i;
            if (i > BodyPart.BP_Count)
                return false;
        }
        return true;
    }
    
    private void onUserDead() {
        
    }
    
    public void onLevelUp() {
        onUserEffect(true, true, UserEffect.LevelUp);
    }
    
    public void update(long time) {
        resetTemporaryStat(time, 0);
        flushCharacterData(time, false);
        checkCashItemExpire(time);
        checkGeneralItemExpire(time);
    }
    
    public void checkGeneralItemExpire(long time) {
        // TODO: Item Expirations
    }
    
    public void checkCashItemExpire(long time) {
        if (time - nextCheckCashItemExpire >= 0) {
            // TODO: Cash Item Expiration
        }
    }
    
    public void flushCharacterData(long time, boolean force) {
        if (lock()) {
            try {
                if (force || time - lastCharacterDataFlush >= 300000) {
                    // Best way to constantly update the ItemSN's without over-saving
                    // is by simply updating the SN upon each user save. This is either
                    // every 5 minutes, or whenever a user logs out.
                    GameApp.getInstance().updateItemInitSN();
                    if (characterDataModFlag != 0) {
                        if ((characterDataModFlag & DBChar.Character) != 0) {
                            GameDB.rawSaveCharacter(character.getCharacterStat());
                        }
                        if ((characterDataModFlag & DBChar.SkillRecord) != 0) {
                            GameDB.rawSaveSkillRecord(getCharacterID(), character.getSkillRecord());
                        }
                        if ((characterDataModFlag & DBChar.ItemSlotEquip) != 0) {
                            GameDB.rawUpdateItemEquip(characterID, character.getEquipped(), character.getEquipped2(), character.getItemSlot().get(ItemType.Equip));
                        }
                        if ((characterDataModFlag & DBChar.ItemSlotConsume) != 0 || (characterDataModFlag & DBChar.ItemSlotInstall) != 0 
                                || (characterDataModFlag & DBChar.ItemSlotEtc) != 0) {
                            GameDB.rawUpdateItemBundle(characterID, character.getItemSlot());
                        }
                        characterDataModFlag = 0;
                    }
                    //if (miniRoom != null)
                    //  miniRoom.save();
                    lastCharacterDataFlush = time;
                }
            } finally {
                unlock();
            }
        }
    }
    
    public void resetTemporaryStat(long time, int reasonID) {
        lock.lock();
        try {
            int reset;
            if (reasonID > 0)
                reset = 0;//TODO: secondaryStat.resetByReasonID(reasonID);
            else
                reset = 0;//TODO: secondaryStat.resetByTime(time);
            if (reset != 0) {
                validateStat(false);
                sendTemporaryStatReset(reset);
            }
        } finally {
            unlock();
        }
    }
    
    public void sendTemporaryStatReset(int reset) {
        if (reset != 0) {
            lock.lock();
            try {
                sendPacket(WvsContext.onTemporaryStatReset(reset));
                // TODO: SecondaryStat.FilterForRemote, SplitSendPacket.
            } finally {
                unlock();
            }
        }
    }
    
    public void onUserEffect(boolean local, boolean remote, byte effect) {
        if (remote) {
            getField().splitSendPacket(getSplit(), UserRemote.onEffect(getCharacterID(), effect, 0, 0), this);
        }
        if (local) {
            sendPacket(UserLocal.onEffect(effect, 0, 0));
        }
    }
    
    public final void validateStat(boolean calledByConstructor) {
        lock.lock();
        try {
            avatarLook.load(character.getCharacterStat(), character.getEquipped(), character.getEquipped2());
            
            int flag = 0;
            
            if (!calledByConstructor) {
                if (flag != 0)
                    sendCharacterStat(Request.None, flag);
            }
            if (isGM() && character.getSkillRecord().isEmpty()) {
                // TODO: Max all skills
            }
        } finally {
            unlock();
        }
    }
    
    public class UserEffect {
        public static final byte
                LevelUp         = 0,
                SkillUse        = 1,
                SkillAffected   = 2
        ;
    }
}
