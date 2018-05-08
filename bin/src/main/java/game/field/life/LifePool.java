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
package game.field.life;

import game.field.Field;
import game.field.FieldOpt;
import game.field.StaticFoothold;
import game.field.life.heapbase.CompareCtrlMax;
import game.field.life.heapbase.CompareCtrlMin;
import game.field.life.mob.Mob;
import game.field.life.mob.MobCtrl;
import game.field.life.mob.MobGen;
import game.field.life.mob.MobTemplate;
import game.field.life.npc.Npc;
import game.field.life.npc.NpcTemplate;
import game.user.User;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import network.packet.ClientPacket;
import network.packet.InPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;
import util.Range;
import util.Rect;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class LifePool {
    private final Field field;
    private final CompareCtrlMin ctrlMin;
    private final CompareCtrlMax ctrlMax;
    private final Map<Integer, Controller> controllers;
    private final Map<Integer, Mob> mobs;
    private final Map<Integer, Npc> npcs;
    private final Controller ctrlNull;
    private int mobCapacityMin;
    private int mobCapacityMax;
    private long lastCreateMobTime;
    private final List<MobGen> mobGen;
    private int initMobGenCount;
    private int mobGenCount;
    private boolean mobGenEnable;
    private final List<Integer> mobGenExcept;
    private int subMobCount;
    
    public LifePool(Field field) {
        this.field = field;
        this.ctrlMin = new CompareCtrlMin(16);
        this.ctrlMax = new CompareCtrlMax(16);
        this.ctrlNull = new Controller(null);
        this.controllers = new HashMap<>();
        this.mobs = new HashMap<>();
        this.npcs = new HashMap<>();
        this.mobGen = new ArrayList<>();
        this.mobGenExcept = new ArrayList<>();
        this.lastCreateMobTime = System.currentTimeMillis();
    }
    
    public Field getField() {
        return field;
    }
    
    public Npc createNpc(WzProperty prop, int templateID, int x, int y) {
        short fh;
        byte f;
        Range horz = new Range();
        if (prop != null) {
            templateID = WzUtil.getInt32(prop.getNode("id"), templateID);
            x = WzUtil.getInt32(prop.getNode("x"), x);
            y = WzUtil.getInt32(prop.getNode("cy"), y);
            fh = WzUtil.getShort(prop.getNode("fh"), (short) 0);
            horz.low = WzUtil.getInt32(prop.getNode("rx0"), 0);
            horz.high = WzUtil.getInt32(prop.getNode("rx1"), 0);
            f = WzUtil.getByte(prop.getNode("f"), (byte) 0);
            if (f == 0) {
                f = 1;
            }
        } else {
            StaticFoothold pfh = field.getSpace2D().getFootholdUnderneath(x, y, new Pointer<>(x));
            if (pfh == null) {
                Logger.logError("Cannot found foothold (%d, %d) in map(%d)", x, y, field.getFieldID());
                return null;
            }
            f = 1;
            fh = (short) pfh.getSN();
            horz.low = x - 50;
            horz.high = x + 50;
        }
        
        NpcTemplate template = NpcTemplate.getNpcTemplate(templateID);
        if (template != null) {
            Npc npc = Npc.createNpc(template, x, y);
            npc.setField(field);
            npc.setHorz(horz);
            npc.setMovePosition(x, y, f, fh);
            npc.setController(ctrlNull);
            npcs.put(npc.getGameObjectID(), npc);
            ctrlNull.getCtrlNpc().add(npc);
            field.splitRegisterFieldObj(x, y, 2, npc);
            return npc;
        } else {
            Logger.logError("Failed in creating Npc(%d) in map(%d)", templateID, field.getFieldID());
            return null;
        }
    }
    
    public boolean createMob(int templateID, MobGen pmg, int x, int y, short fh, boolean noDropPriority, byte left, int mobType, Controller owner) {
        if (owner == null) {
            if (ctrlMin.getCount() != 0) {
                owner = ctrlMin.getHeap().get(0);
            }
        }
        if (owner != null && owner.getCtrlCount() >= 50) {
            if (mobType != 2 || !giveUpMobController(owner)) {
                owner = null;
            }
        }
        if (pmg != null && pmg.regenInterval < 0) {
            owner = ctrlNull;
        }
        MobTemplate template = MobTemplate.getMobTemplate(templateID);
        if (template != null && owner != null) {
            if (owner.getUser().isHide()) {
                return false;
            }
            Mob mob = new Mob(field, template, noDropPriority);
            mob.init(pmg, fh);
            byte moveAction;
            if (template.getMoveAbility() == MoveAbility.Fly) {
                moveAction = MoveActionType.Fly1;
            } else {
                moveAction = template.getMoveAbility() == MoveAbility.Stop ? MoveActionType.Stand : MoveActionType.Move;
            }
            mob.setMovePosition(x, y, (byte) ((left & 1 | 2 * moveAction) & 0xFF), fh);
            //mob.setMoveRect(new Rect(x, y, x, y));
            mob.setController(owner);
            
            if (mobType == 1) {
                if (subMobCount < 0)
                    subMobCount = 0;
                ++subMobCount;
                mob.setMobType(1);
            }
            if (mobType == 2)
                mob.setMobType(2);
            
            Point pt = field.makePointInSplit(x, y);
            field.splitRegisterFieldObj(pt.x, pt.y, 1, mob);
            
            mobs.put(mob.getGameObjectID(), mob);
            mob.sendChangeControllerPacket(owner.getUser(), MobCtrl.Active_Int);
            owner.getCtrlMob().add(mob);
            updateCtrlHeap(owner);
            return true;
        }
        return false;
    }
    
    public boolean giveUpMobController(Controller ctrl) {
        Logger.logError("* GiveUpMobController Happened");
        if (ctrl != null && !ctrl.getCtrlMob().isEmpty()) {
            Mob ctrlMob = ctrl.getCtrlMob().get(0);//pHead
            if (ctrlMob != null) {
                for (Mob mob : ctrl.getCtrlMob()) {
                    if (mob != null) {
                        ctrlMob = mob;
                    }
                }
            }
            if (ctrlMob != null) {
                ctrl.getCtrlMob().remove(ctrlMob);
                updateCtrlHeap(ctrl);
                ctrlNull.getCtrlMob().add(ctrlMob);
                ctrlMob.setController(ctrlNull);
                updateCtrlHeap(ctrlNull);//func literally ignores pCtrlNull but they call it anyway, k.
                ctrlMob.sendChangeControllerPacket(ctrl.getUser(), (byte) 0);
                return true;
            }
        }
        return false;
    }
    
    public void init(Field field, WzProperty mapData) {
        if (getField() != field || mapData == null) {
            return;
        }
        
        int mapWidth = Math.max(field.getMapSize().cx, Field.WvsScreenWidth);
        int mapHeight = Math.max(field.getMapSize().cy - Field.ScreenHeightOffset, Field.WvsScreenHeight);
        int mobCapacity = Math.min(40, Math.max(1, (int) ((double) (mapHeight * mapWidth) * field.getMobRate() * 0.0000078125d)));
        
        this.mobCapacityMin = mobCapacity;
        this.mobCapacityMax = 1 << mobCapacity;
        for (WzProperty life : mapData.getNode("life").getChildNodes()) {
            String id = WzUtil.getString(life.getNode("id"), "");
            String type = WzUtil.getString(life.getNode("type"), "");
            
            if (id.isEmpty() || type.isEmpty()) continue;
            
            if (type.equalsIgnoreCase("n")) {
                if (field.lock()) {
                    try {
                        createNpc(life, Integer.parseInt(id), 0, 0);
                    } finally {
                        field.unlock();
                    }
                }
            } else {
                MobTemplate template = MobTemplate.getMobTemplate(Integer.parseInt(id));
                if (template == null) {
                    Logger.logError("Invalid mob template ID(%s) on map(%d)", id, field.getFieldID());
                    continue;
                }
                MobGen pmg = new MobGen();
                pmg.templateID = template.getTemplateID();
                pmg.x = WzUtil.getShort(life.getNode("x"), (short) 0);
                
                short y = WzUtil.getShort(life.getNode("y"), (short) 0);
                short cy = WzUtil.getShort(life.getNode("cy"), (short) 0);
                short fh = WzUtil.getShort(life.getNode("fh"), (short) 0);
                
                pmg.regenInterval = 1000 * WzUtil.getInt32(life.getNode("mobTime"), 0);
                pmg.regenAfter = 0;
                pmg.f = WzUtil.getByte(life.getNode("f"), (byte) 0);
                
                if (template.getMoveAbility() == MoveAbility.Fly) {
                    pmg.y = y;
                    pmg.fh = 0;
                } else {
                    pmg.y = cy;
                    pmg.fh = fh;
                }
                
                if (pmg.regenInterval > 0) {
                    int interval = (pmg.regenInterval / 10);
                    if (6 * interval > 0) {
                        pmg.regenAfter = System.currentTimeMillis() + interval + Rand32.getRand(6 * interval, 0);
                    }
                }
                pmg.mobCount.set(0);
                
                this.mobGen.add(pmg);
                this.initMobGenCount++;
            }
        }
        
        this.mobGenCount = this.initMobGenCount;
        this.tryCreateMob(true);
    }
    
    public void removeAllMob() {
        if (field.lock()) {
            try {
                List<Mob> mob = new ArrayList<>(mobs.values());
                for (Mob m : mob) {
                    m.setForcedDead(true);
                    removeMob(m);
                }
                mob.clear();
            } finally {
                field.unlock();
            }
        }
    }
    
    public void update(long time) {
        if (field.lock(1200)) {
            try {
                //for (Npc npc : npcs.values()) {
                //    npc.update(time);
                //}
                List<Mob> mob = new ArrayList<>(mobs.values());
                for (Mob m : mob) {
                    m.update(time);
                }
                mob.clear();
            } finally {
                field.unlock();
            }
        }
        tryCreateMob(false);
    }
    
    public void insertController(User user) {
        if (field.lock()) {
            try {
                Controller t = new Controller(user);
                if (!user.isHide()) {
                    t.setPosMinHeap(ctrlMin.insert(t));
                    t.setPosMaxHeap(ctrlMax.insert(t));
                }
                controllers.put(user.getCharacterID(), t);
                redistributeLife();
            } finally {
                field.unlock();
            }
        }
    }
    
    public void removeController(User user) {
        if (field.lock()) {
            try {
                Controller ctrl = controllers.remove(user.getCharacterID());
                if (ctrl != null) {
                    // TODO
                }
            } finally {
                field.unlock();
            }
        }
    }
    
    public void redistributeLife() {
        if (ctrlMin.getCount() == 0) {
            return;
        }
        Controller ctrl;
        Controller controller = ctrlNull;
        
        // It's hard to tell if Nexon ever truly increments nIndex.
        // I think they truly continue to force the controller on
        // whoever has the least amount of controllers and then on
        // each new controller they update the heap. Each new adjustment
        // causes them to shift upward, allowing the new user with the
        // least controllers to be next in line.
        int index = 0;
        for (Iterator<Npc> it = controller.getCtrlNpc().iterator(); it.hasNext();) {
            Npc npc = it.next();
            ctrl = ctrlMin.getHeap().get(index);
            if (ctrl.getCtrlCount() >= 50) {
                break;
            }
            ctrl.getCtrlNpc().add(npc);
            npc.setController(ctrl);
            updateCtrlHeap(ctrl);
            npc.sendChangeControllerPacket(ctrl.getUser(), true);
            it.remove();
        }
        // Assume reiteration from head.
        index = 0;
        for (Iterator<Mob> it = controller.getCtrlMob().iterator(); it.hasNext();) {
            Mob mob = it.next();
            ctrl = ctrlMin.getHeap().get(index);
            if (ctrl.getCtrlCount() >= 50) {
                break;
            }
            ctrl.getCtrlMob().add(mob);
            mob.setController(ctrl);
            updateCtrlHeap(ctrl);
            mob.sendChangeControllerPacket(ctrl.getUser(), MobCtrl.Active_Int);
            it.remove();
        }
        // Assume final reiteration from head of each heap.
        index = 0;
        while (true) {
            Controller minCtrl = ctrlMin.getHeap().get(index);
            Controller maxCtrl = ctrlMax.getHeap().get(index);
            
            if (maxCtrl.getCtrlCount() <= minCtrl.getCtrlCount() || maxCtrl.getCtrlCount() <= 20) {
                return;
            }
            
            if (!maxCtrl.getCtrlNpc().isEmpty()) {
                Npc npc = maxCtrl.getCtrlNpc().remove(0);
                updateCtrlHeap(maxCtrl);
                minCtrl.getCtrlNpc().add(npc);
                npc.setController(minCtrl);
                updateCtrlHeap(minCtrl);
                npc.sendChangeControllerPacket(maxCtrl.getUser(), false);
                npc.sendChangeControllerPacket(minCtrl.getUser(), true);
                continue;
            }
            
            if (maxCtrl.getCtrlMob().isEmpty() || maxCtrl.getCtrlMob().get(0) == null)
                break;
            Mob mob = maxCtrl.getCtrlMob().get(0);
            if (mob == null) {
                for (Mob p : maxCtrl.getCtrlMob()) {
                    if (p != null) {
                        mob = p;
                        break;
                    }
                }
            }
            if (mob == null)
                break;
            maxCtrl.getCtrlMob().remove(mob);
            updateCtrlHeap(maxCtrl);
            minCtrl.getCtrlMob().add(mob);
            mob.setController(minCtrl);
            updateCtrlHeap(minCtrl);
            mob.sendChangeControllerPacket(maxCtrl.getUser(), (byte) 0);
            mob.sendChangeControllerPacket(minCtrl.getUser(), MobCtrl.Active_Int);
        }
    }
    
    public boolean changeMobController(int characterID, Mob mobWanted, boolean chase) {
        return true;
    }
    
    public void setMobGen(boolean mobGen, Integer mobTemplateID) {
        if (mobTemplateID != 0) {
            if (mobGen) {
                mobGenExcept.remove(mobTemplateID);
            } else {
                if (!mobGenExcept.contains(mobTemplateID)) {
                    mobGenExcept.add(mobTemplateID);
                }
            }
        } else {
            mobGenEnable = mobGen;
        }
    }
    
    public void tryCreateMob(boolean reset) {
        if (reset) {
            setMobGen(true, 0);
        }
        if (mobGen.isEmpty() || mobGenCount == 0) {
            return;
        }
        long time = System.currentTimeMillis();
        if (reset || (time - lastCreateMobTime) >= 7000) {
            int mobCapacity;
            if (ctrlMin.getCount() > mobCapacityMin / 2) {
                mobCapacity = ctrlMin.getCount() < 2 * mobCapacityMin ? mobCapacityMin + (mobCapacityMax - mobCapacityMin) * (2 * ctrlMin.getCount() - mobCapacityMin) / (3 * mobCapacityMin) : mobCapacityMax;
            } else {
                mobCapacity = mobCapacityMin;
            }
            int mobCount = mobCapacity - mobs.size();
            if (mobCount <= 0) {
                return;
            }
            if (field.lock()) {
                try {
                    final List<Mob> mob = new ArrayList<>(mobs.values());
                    final List<Point> points = new ArrayList<>();
                    final List<MobGen> mobGens = new ArrayList<>();
                    int count = 0;
                    for (Iterator<Mob> it = mob.iterator(); it.hasNext();) {
                        Mob m = it.next();
                        if (m != null) {
                            if (m.getTemplateID() != 9999999) {
                                points.add(new Point(m.getCurrentPos().x, m.getCurrentPos().y));
                            }
                        } else {
                            it.remove();
                        }
                    }
                    boolean checkArea = false;
                    for (MobGen pmg : mobGen) {
                        if (!mobGenEnable) {
                            break;
                        }
                        if (!mobGenExcept.contains(pmg.templateID)) {
                            if (pmg.regenInterval == 0) {
                                checkArea = true;
                            } else {
                                if (pmg.regenInterval >= 0 || reset) {
                                    if (pmg.mobCount.get() == 0 && time - pmg.regenAfter >= 0) {
                                        mobGens.add(pmg);
                                        points.add(new Point(pmg.x, pmg.y));
                                    }
                                }
                            }
                        }
                        if (checkArea) {
                            Rect rc = new Rect(pmg.x - 100, pmg.y - 100, pmg.x + 100, pmg.y + 100);
                            boolean add = true;
                            int i = 0;
                            while (i < points.size()) {
                                Point pt = points.get(i);
                                if (pt == null) {
                                    break;
                                }
                                if (rc.ptInRect(pt)) {
                                    add = false;
                                    break;
                                }
                                ++i;
                            }
                            if (add) {
                                mobGens.add(pmg);
                                points.add(new Point(pmg.x, pmg.y));
                            }
                            checkArea = false;
                        }
                        ++count;
                        if (count >= mobGenCount) {
                            break;
                        }
                    }
                    if (!mobGens.isEmpty()) {
                        int index;
                        while (true) {
                            if (mobGens.isEmpty())
                                break;
                            index = (int) (Rand32.getInstance().random() % mobGens.size());
                            MobGen pmg = mobGens.remove(index);
                            if (pmg == null)
                                continue;
                            if (createMob(pmg.templateID, pmg, pmg.x, pmg.y, pmg.fh, false, pmg.f, 0, null))
                                --mobCount;
                            if (mobCount <= 0) {
                                break;
                            }
                        }
                    }
                    mob.clear();
                    points.clear();
                    mobGens.clear();
                } finally {
                    field.unlock();
                }
                lastCreateMobTime = time;
            }
        }
    }
    
    public void removeMob(Mob mob) {
        
    }
    
    public void insertMobGen(MobGen mobGen) {
        
    }
    
    public void onMobPacket(User user, byte type, InPacket packet) {
        int mobID = packet.decodeInt();
        if (mobs.containsKey(mobID)) {
            Mob mob = mobs.get(mobID);
            switch (type) {
                case ClientPacket.MobMove:
                    field.onMobMove(user, mob, packet);
                    break;
            }
        } else {
            if (type == ClientPacket.MobMove) {
                Mob.sendReleaseControlPacket(user, mobID);
            }
        }
    }
    
    public void onPacket(User user, byte type, InPacket packet) {
        if (type >= ClientPacket.BEGIN_MOB && type <= ClientPacket.END_MOB) {
            onMobPacket(user, type, packet);
        } else if (type >= ClientPacket.BEGIN_NPC && type <= ClientPacket.END_NPC) {
            onNpcPacket(user, type, packet);
        }
    }
    
    public void onNpcPacket(User user, byte type, InPacket packet) {
        int npcID = packet.decodeInt();
        if (npcs.containsKey(npcID)) {
            Npc npc = npcs.get(npcID);
            switch (type) {
                case ClientPacket.NpcMove:
                    field.onNpcMove(user, npc, packet);
                    break;
            }
        }
    }
    
    public void removeNpc(Npc npc) {
        int templateID = npc.getTemplateID();
        
        npcs.remove(npc.getGameObjectID());
        npc.getController().getCtrlNpc().remove(npc);
        updateCtrlHeap(npc.getController());
        npc.sendChangeControllerPacket(npc.getController().getUser(), false);
        npc.getField().splitUnregisterFieldObj(2, npc);
        
        //Not sure why Nexon seems to re-create the Npc after removal..
        NpcTemplate template = NpcTemplate.getNpcTemplate(templateID);
        if (template != null) {
            // Parse the Map that the Npc is currently in
            // Iterate the life
            // Check if it's a Npc and if the ID is equal to the one removed
            // CreateNpc(pData, 0, 0, 0);
        }
        
        redistributeLife();
    }
    
    public void reset() {
        mobGenExcept.clear();
        //removeAllMob(false);
        tryCreateMob(true);
    }
    
    public void updateCtrlHeap(Controller ctrl) {
        if (ctrl != ctrlNull) {
            ctrlMin.updateAt(ctrl.getPosMinHeap());
            ctrlMax.updateAt(ctrl.getPosMaxHeap());
        }
    }
}
