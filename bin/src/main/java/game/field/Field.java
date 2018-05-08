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
package game.field;

import game.field.MovePath.Elem;
import game.field.drop.DropPool;
import game.field.life.LifePool;
import game.field.life.npc.Npc;
import game.field.life.npc.NpcPool;
import game.field.portal.PortalMap;
import game.user.User;
import game.user.UserRemote;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Rect;
import util.Size;

/**
 *
 * @author Eric
 */
public class Field {
    public static final int
            // Field IDs
            Henesys          = 100000000,
            Basic            = 104000000,
            Invalid          = 999999999,
            MapleIsland_Max  = 9999999,
            // The default screen dimensions
            WvsScreenWidth      = 800,
            WvsScreenHeight     = 600,
            // The formula to construct the screen width/height offsets
            ScreenWidthOffset   = (WvsScreenWidth * 75) / 100,
            ScreenHeightOffset  = (WvsScreenHeight * 75) / 100
    ;
    
    private final AtomicInteger fieldObjIdCounter;
    private final int field;
    private int fieldReturn;
    private int forcedReturn;
    private String streetName;
    private String mapName;
    private Point leftTop;
    private Size map;
    private double mobRate;
    private double recoveryRate;
    private int option;
    private int autoDecHP;
    private int autoDecMP;
    private boolean town;
    private boolean clock;
    private boolean swim;
    private int splitRowCount;
    private int splitColCount;
    private double incRateEXP;
    private double incRateDrop;
    private final WvsPhysicalSpace2D space2D;
    private final PortalMap portal;
    private final LifePool lifePool;
    private final DropPool dropPool;
    private final Lock lock;
    private FieldSplit splitStart;
    private FieldSplit splitEnd;
    private List<FieldSplit> fieldSplit;
    private final Map<Integer, User> users;
    
    public Field(int fieldID) {
        this.fieldObjIdCounter = new AtomicInteger(30000);
        this.field = fieldID;
        this.space2D = new WvsPhysicalSpace2D();
        this.portal = new PortalMap();
        this.lifePool = new LifePool(this);
        this.dropPool = new DropPool(this);
        this.lock = new ReentrantLock();
        this.users = new HashMap<>();
    }
    
    public DropPool getDropPool() {
        return dropPool;
    }
    
    public PortalMap getPortal() {
        return portal;
    }
    
    public LifePool getLifePool() {
        return lifePool;
    }
    
    public WvsPhysicalSpace2D getSpace2D() {
        return space2D;
    }
    
    public void getEncloseSplit(FieldSplit p, List<FieldSplit> split) {
        split.clear();
        for (int i = 0; i < 9; i++)
            split.add(i, null);
        // =============
        // [0]  [1]  [2]    [Row - 1]
        // [3]  {4}  [5]    [Row + 0]
        // [6]  [7]  [8]    [Row + 1]
        // =============
        int row = p.getRow();
        int col = p.getCol();
        if (row != 0) {
            if (col != 0) {
                split.set(0, fieldSplit.get((col - 1) + (row - 1) * splitColCount));
            }
            split.set(1, fieldSplit.get(col + (row - 1) * splitColCount));
            if (col < splitColCount - 1) {
                split.set(2, fieldSplit.get((col + 1) + (row - 1) * splitColCount));
            }
        }
        if (col != 0) {
            split.set(3, fieldSplit.get((col - 1) + row * splitColCount));
        }
        split.set(4, p);
        if (col < splitColCount - 1) {
            split.set(5, fieldSplit.get((col + 1) + row * splitColCount));
        }
        if (row < splitRowCount - 1) {
            if (col != 0) {
                split.set(6, fieldSplit.get((col - 1) + (row + 1) * splitColCount));
            }
            split.set(7, fieldSplit.get(col + (row + 1) * splitColCount));
            if (col < splitColCount - 1) {
                split.set(8, fieldSplit.get((col + 1) + (row + 1) * splitColCount));
            }
        }
    }
    
    public int getFieldID() {
        return this.field;
    }
    
    public int getFieldType() {
        return FieldType.Default;
    }
    
    public int getForcedReturnFieldID() {
        return this.forcedReturn;
    }
    
    public int getOption() {
        return option;
    }
    
    public int getReturnFieldID() {
        return this.fieldReturn;
    }
    
    public List<User> getUsers() {
        synchronized (users) {
            return new ArrayList<>(users.values());
        }
    }
    
    public List<User> getUsers(boolean exceptAdmin) {
        List<User> users = getUsers();
        
        if (exceptAdmin) {
            for (Iterator<User> it = users.iterator(); it.hasNext();) {
                User user = it.next();
                if (user.isGM()) {
                    it.remove();
                }
            }
        }
        return users;
    }
    
    public int incrementIdCounter() {
        if (lock(1100)) {
            try {
                if (fieldObjIdCounter.get() > 2000000000) {
                    Logger.logError("The FieldObjID counter has exceeded 2billion objects, resetting to 30000.");
                    fieldObjIdCounter.set(30000);
                }
                return fieldObjIdCounter.incrementAndGet();
            } finally {
                unlock();
            }
        }
        return Integer.MIN_VALUE;
    }
    
    public boolean isSwim() {
        return swim;
    }
    
    public boolean isUserExist(int characterID) {
        if (lock()) {
            try {
                return users.containsKey(characterID);
            } finally {
                unlock();
            }
        }
        return false;
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
    
    public Point makePointInSplit(int x, int y) {
        x = Math.min(Math.max(x, leftTop.x), (leftTop.x + ScreenWidthOffset * splitColCount) - 1);
        y = Math.min(Math.max(y, leftTop.y), (leftTop.y + ScreenHeightOffset * splitRowCount) - 1);
        return new Point(x, y);
    }
    
    public void makeSplit() {
        this.splitColCount = (map.cx + (ScreenWidthOffset - 1)) / ScreenWidthOffset;
        this.splitRowCount = (map.cy + (ScreenHeightOffset - 1)) / ScreenHeightOffset;
        this.fieldSplit = new ArrayList<>(splitColCount * splitRowCount);
        for (int i = 0; i < splitRowCount; ++i) {
            for (int j = 0; j < splitColCount; ++j) {
                fieldSplit.add(new FieldSplit(i, j, j + i * splitColCount));
            }
        }
        this.splitStart = fieldSplit.get(0);
        this.splitEnd = fieldSplit.get(fieldSplit.size() - 1);
    }
    
    public boolean onEnter(final User user) {
        if (lock(1500)) {
            try {
                if (!dropPool.onEnter(user)) {
                    return false;
                }
                if (!splitRegisterFieldObj(user.getCurrentPosition().x, user.getCurrentPosition().y, 0, user)) {
                    Logger.logError("Incorrect field position [%09d]", field);
                    return false;
                }
                splitRegisterUser(null, user.getSplit(), user);
                user.setPosMap(this.field);
                users.put(user.getCharacterID(), user);
                lifePool.insertController(user);
                // Weather
                // Jukebox
                if (clock) {
                    
                }
                return true;
            } finally {
                unlock();
            }
        }
        return false;
    }
    
    public void onLeave(User user) {
        if (lock(1100)) {
            try {
                users.remove(user.getCharacterID());
                splitRegisterUser(user.getSplit(), null, user);
                splitUnregisterFieldObj(0, user);
            } finally {
                unlock();
            }
        }
    }
    
    public void onNpcMove(User ctrl, Npc npc, InPacket packet) {
        if (npc.getController() != null && npc.getController().getUser() == ctrl) {
            byte action = packet.decodeByte();
            byte chatIdx = packet.decodeByte();
            MovePath mp = null;
            if (npc.getNpcTemplate().isMove()) {
                mp = new MovePath();
                mp.decode(packet);
                if (!mp.getElem().isEmpty()) {
                    Elem tail = mp.getElem().getLast();
                    if (tail != null) {
                        FieldSplit split = npc.getSplit();
                        FieldSplit centerSplit = null;
                        int x = ScreenHeightOffset * split.getCol() + leftTop.x - 100;
                        int y = ScreenHeightOffset * split.getRow() + leftTop.y - 75;
                        if (tail.getX() < x || tail.getX() > (x + WvsScreenWidth) || tail.getY() < y || tail.getY() > (y + WvsScreenHeight)) {
                            centerSplit = splitFromPoint(tail.getX(), tail.getY());
                            if (centerSplit == null) {
                                Logger.logError("Incorrect field position from NPC [%d]", npc.getTemplateID());
                                return;
                            }
                        }
                        if (tail.getX() < npc.getOriginalPos().x - 50 || tail.getX() > npc.getOriginalPos().x + 50 || tail.getY() < npc.getOriginalPos().y - 50 || tail.getY() > npc.getOriginalPos().y + 50) {
                            Logger.logError("Invalid NPC Position [ id : %d, field : %d, pos : %d, %d ]", npc.getTemplateID(), field, tail.getX(), tail.getY());
                            lifePool.removeNpc(npc);
                            return;
                        }
                        npc.setMovePosition(tail.getX(), tail.getY(), (byte) (tail.getMoveAction() & 0xFF), tail.getFh());
                        if (centerSplit != null) {
                            splitMigrateFieldObj(centerSplit, FieldSplit.Npc, npc);
                        }
                    }
                }
            }
            splitSendPacket(ctrl.getSplit(), NpcPool.onMove(npc.getGameObjectID(), action, chatIdx, mp), ctrl);
            ctrl.sendPacket(NpcPool.onMove(npc.getGameObjectID(), action, chatIdx, mp));
            if (mp != null) {
                mp.getElem().clear();
            }
        }
    }
    
    public void onPacket(User user, byte type, InPacket packet) {
        if (type >= ClientPacket.BEGIN_LIFEPOOL && type <= ClientPacket.END_LIFEPOOL) {
            lifePool.onPacket(user, type, packet);
        } else if (type >= ClientPacket.BEGIN_DROPPOOL && type <= ClientPacket.END_DROPPOOL) {
            dropPool.onPacket(user, type, packet);
        }
    }
    
    public void onUserMove(User user, InPacket packet, Rect move) {
        packet.decodeByte(); // Unknown -> v3 = *(_BYTE *)(dword_60423C + 180);
        
        MovePath mp = new MovePath();
        mp.decode(packet);
        if (mp.getElem().isEmpty()) {
            Logger.logError("Received Empty Move Path [%s]", user.getCharacterName());
            return;
        }
        
        Elem tail = mp.getElem().getLast();
        // Nexon applies the pTail coordinates here, however that will
        // cause an incorrect result on fieldsplits if the movement's
        // element is ever wrong. Since the current coordinates are of
        // the portal destination, they are never wrong.
        final int xPos = user.getCurrentPosition().x;//pTail.x
        final int yPos = user.getCurrentPosition().y;//pTail.y
        user.setMovePosition(tail.getX(), tail.getY(), (byte) (tail.getMoveAction() & 0xFF), tail.getFh());
        
        FieldSplit splitOld = user.getSplit();
        int x = ScreenWidthOffset * splitOld.getCol() + leftTop.x - 100;
        int y = ScreenHeightOffset * splitOld.getRow() + leftTop.y - 75;
        if (tail.getX() < x || tail.getX() > (x + WvsScreenWidth) || tail.getY() < y || tail.getY() > (y + WvsScreenHeight)) {
            FieldSplit splitNew = splitFromPoint(xPos, yPos);
            if (splitNew == null) {
                /*pUser.postTransferField(pUser.getField().getFieldID(), "", true);
                long tCur = System.currentTimeMillis();
                if ((tCur - pUser.tLastIncorrectFieldPositionTime) <= 60000) {
                    ++pUser.nIncorrectFieldPositionCount;
                    if (pUser.nIncorrectFieldPositionCount >= 5) {
                        Logger.logError("Incorrect field position (%d,%d)/(%d,%d,%d,%d)/(Map:%d)");
                        if (!pUser.isGM()) {
                            //pUser.closeSocket();
                            return;
                        }
                    }
                } else {
                    pUser.nIncorrectFieldPositionCount = 1;
                }
                pUser.tLastIncorrectFieldPositionTime = tCur;*/
                return;
            }
            splitMigrateFieldObj(splitNew, FieldSplit.User, user);
            splitRegisterUser(splitOld, splitNew, user);
        }
        splitSendPacket(user.getSplit(), UserRemote.onMove(user.getCharacterID(), mp), user);
        mp.getElem().clear();
    }
    
    public void setLeftTop(Point pt) {
        this.leftTop = pt;
    }
    
    public void setMapSize(Size sz) {
        this.map = sz;
    }

    public void setFieldReturn(int fieldReturn) {
        this.fieldReturn = fieldReturn;
    }

    public void setForcedReturn(int forcedReturn) {
        this.forcedReturn = forcedReturn;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setMobRate(double mobRate) {
        this.mobRate = mobRate;
    }

    public void setRecoveryRate(double recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public void setAutoDecHP(int autoDecHP) {
        this.autoDecHP = autoDecHP;
    }

    public void setAutoDecMP(int autoDecMP) {
        this.autoDecMP = autoDecMP;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public void setClock(boolean clock) {
        this.clock = clock;
    }

    public void setSwim(boolean swim) {
        this.swim = swim;
    }

    public void setIncRateEXP(double incRateEXP) {
        this.incRateEXP = incRateEXP;
    }

    public void setIncRateDrop(double incRateDrop) {
        this.incRateDrop = incRateDrop;
    }
    
    public FieldSplit splitFromPoint(int x, int y) {
        if ((x = x - leftTop.x) < 0 || (y = y - leftTop.y) < 0
                || (x = x / ScreenWidthOffset) >= splitColCount
                || (y = y / ScreenHeightOffset) >= splitRowCount) {
            return null;
        } else {
            return fieldSplit.get(x + y * splitColCount);
        }
    }
    
    public void splitMigrateFieldObj(FieldSplit centerSplit, int foc, FieldObj obj) {
        List<FieldSplit> src = new ArrayList<>();
        getEncloseSplit(centerSplit, src);
        int i;
        for (FieldSplit split : src) {
            i = 0;
            for (FieldSplit objSplit : obj.getSplits()) {
                if (split == objSplit)
                    break;
                ++i;
            }
            if (i >= 9) {
                splitRegisterFieldObj(split, foc, obj, obj.makeEnterFieldPacket());
            } else {
                obj.getSplits().set(i, null);
            }
        }
        for (FieldSplit split : obj.getSplits()) {
            if (split != null) {
                splitUnregisterFieldObj(split, foc, obj, obj.makeLeaveFieldPacket());
            }
        }
        obj.getSplits().clear();
        obj.getSplits().addAll(src);
        obj.getPosSplit().clear();
    }
    
    public void splitNotifyFieldObj(FieldSplit split, OutPacket packet, FieldObj obj) {
        if (split != null && lock()) {
            try {
                for (FieldObj pNew : split.getFieldObj()) {
                    if (pNew != null && (pNew instanceof User)) {
                        User user = (User) pNew;
                        if (obj == null || obj.isShowTo(user)) {
                            user.sendPacket(packet);
                        }
                    }
                }
            } finally {
                unlock();
            }
        }
    }
    
    public void splitRegisterFieldObj(FieldSplit split, int foc, FieldObj objNew, OutPacket packetEnter) {
        if (split != null && lock()) {
            try {
                for (User user : split.getUser()) {
                    if (user != null) {
                        if (objNew.isShowTo(user)) {
                            user.sendPacket(packetEnter);
                        }
                    }
                }
                split.getFieldObj().add(foc, objNew);
            } finally {
                unlock();
            }
        }
    }
    
    public boolean splitRegisterFieldObj(int x, int y, int foc, FieldObj obj) {
        FieldSplit centerSplit = splitFromPoint(x, y);
        if (centerSplit != null) {
            getEncloseSplit(centerSplit, obj.getSplits());
            for (FieldSplit split : obj.getSplits()) {
                splitRegisterFieldObj(split, foc, obj, obj.makeEnterFieldPacket());
            }
            return true;
        }
        return false;
    }
    
    public void splitRegisterUser(FieldSplit splitOld, FieldSplit splitNew, User user) {
        if (lock()) {
            try {
                if (splitOld != null) {
                    for (FieldObj obj : splitOld.getFieldObj()) {
                        if (splitNew == null || !splitNew.getFieldObj().contains(obj)) {
                            if (obj != null) {
                                user.sendPacket(obj.makeLeaveFieldPacket());
                            }
                        }
                    }
                }
                if (splitNew != null) {
                    for (FieldObj obj : splitNew.getFieldObj()) {
                        if (splitOld == null || !splitOld.getFieldObj().contains(obj)) {
                            if (obj != null && obj.isShowTo(user)) {
                                user.sendPacket(obj.makeEnterFieldPacket());
                            }
                        }
                    }
                }
                if (splitOld != splitNew) {
                    if (splitOld != null) {
                        splitOld.getUser().remove(user);
                    }
                    if (splitNew != null) {
                        splitNew.getUser().add(user);
                    }
                }
            } finally {
                unlock();
            }
        }
    }
    
    public void splitSendPacket(FieldSplit split, OutPacket packet, User except) {
        // TODO: Improve the threading here, possible deadlock.
        if (split != null && lock(1200)) {
            try {
                for (FieldObj obj : split.getFieldObj()) {
                    if (obj != null && (obj instanceof User)) {
                        User user = (User) obj;
                        if (user != except) {
                            user.sendPacket(packet);
                        }
                    }
                }
            } finally {
                unlock();
            }
        }
    }
    
    public void splitUnregisterFieldObj(FieldSplit split, int foc, FieldObj posObj, OutPacket packetLeave) {
        if (split != null && lock(1000)) {
            try {
                for (User user : split.getUser()) {
                    if (user != null) {
                        user.sendPacket(packetLeave);
                    }
                }
                split.getFieldObj().remove(posObj);
            } finally {
                unlock();
            }
        }
    }
    
    public void splitUnregisterFieldObj(int foc, FieldObj obj) {
        for (FieldSplit split : obj.getSplits()) {
            if (split != null)
                splitUnregisterFieldObj(split, foc, obj, obj.makeLeaveFieldPacket());
        }
    }
    
    public final void unlock() {
        lock.unlock();
    }
    
    public void update(long time) {
        lifePool.update(time);
        dropPool.tryExpire(false);
    }
}
