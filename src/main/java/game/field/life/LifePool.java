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

import common.item.BodyPart;
import common.item.ItemType;
import game.field.Field;
import game.field.FieldSplit;
import game.field.StaticFoothold;
import game.field.life.mob.Mob;
import game.field.life.mob.MobCtrl;
import game.field.life.mob.MobGen;
import game.field.life.mob.MobTemplate;
import game.field.life.npc.Npc;
import game.field.life.npc.NpcTemplate;
import game.user.User;
import game.user.skill.AttackType;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillEntry;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.HeapBase;
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
    private final HeapBase<Controller, CompareCtrlMin> heapCtrlMin;
    private final HeapBase<Controller, CompareCtrlMax> heapCtrlMax;
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
        this.heapCtrlMin = new HeapBase<>(new CompareCtrlMin());
        this.heapCtrlMax = new HeapBase<>(new CompareCtrlMax());
        this.ctrlNull = new Controller(null);
        this.controllers = new ConcurrentHashMap<>();
        this.mobs = new ConcurrentHashMap<>();
        this.npcs = new ConcurrentHashMap<>();
        this.mobGen = new ArrayList<>();
        this.mobGenExcept = new ArrayList<>();
        this.lastCreateMobTime = System.currentTimeMillis();
    }
    
    public boolean changeMobController(User user, int mobIdWanted, boolean chase) {
        if (mobs.containsKey(mobIdWanted)) {
            Mob mobWanted = mobs.get(mobIdWanted);
            if (mobWanted != null) {
                return changeMobController(user.getCharacterID(), mobWanted, chase);
            }
        }
        return false;
    }
    
    public boolean changeMobController(int characterID, Mob mobWanted, boolean chase) {
        Controller oldCtrl = mobWanted.getController();
        
        Controller newCtrl = null;
        if (characterID != 0) {
            if ((newCtrl = controllers.get(characterID)) == null) return false;
            if (oldCtrl == newCtrl) return true;
        } else {
            var posNext = new Pointer<Controller>();
            for (var pos = heapCtrlMin.getHeadPosition(); pos != null; ) {
                posNext.set(pos);
                newCtrl = heapCtrlMin.getNext(posNext);
                if (newCtrl != oldCtrl && newCtrl.getCtrlCount() < LifePerUser.MAX) break;
                else newCtrl = null;
                pos = posNext.get();
            }
            if (newCtrl == null) return false;
        }
        
        List<Mob> newCtrlMob = newCtrl.getCtrlMob();
        List<Mob> oldCtrlMob = oldCtrl.getCtrlMob();
        oldCtrlMob.remove(mobWanted);
        updateCtrlHeap(oldCtrl);
        newCtrlMob.add(mobWanted);
        updateCtrlHeap(newCtrl);
        mobWanted.setController(newCtrl);
        mobWanted.sendChangeControllerPacket(oldCtrl.getUser(), (byte) 0);
        mobWanted.sendChangeControllerPacket(newCtrl.getUser(), chase ? MobCtrl.Active_Req : MobCtrl.Active_Int);
        return true;
    }
    
    public Mob createMob(int templateID) {
        MobTemplate template = MobTemplate.getMobTemplate(templateID);
        if (template != null) {
            return new Mob(field, MobTemplate.getMobTemplate(templateID), false);
        }
        return null;
    }
    
    public boolean createMob(Mob mob, Point pt) {
        return createMob(mob.getTemplateID(), mob.getMobGen(), pt.x, pt.y, (short) field.getSpace2D().getFootholdUnderneath(pt.x, pt.y).getSN(), false, (byte) 0, 0, null);
    }
    
    public boolean createMob(int templateID, MobGen pmg, int x, int y, short fh, boolean noDropPriority, byte left, int mobType, Controller owner) {
        Controller ctrl = (owner != null) ? owner : (heapCtrlMin.isEmpty() ? null : heapCtrlMin.get());
        if (ctrl != null && ctrl.getCtrlCount() >= LifePerUser.MAX) {
            if (mobType == MobType.BOSSMOB) {
                if (!giveUpMobController(ctrl)) ctrl = null;
            }
            else ctrl = null;
        }
        if (pmg != null && pmg.regenInterval < 0) ctrl = ctrlNull;
        
        MobTemplate template = MobTemplate.getMobTemplate(templateID);
        if (template == null || ctrl == null) return false;
        
        Mob mob = new Mob(field, template, noDropPriority);
        mob.init(pmg, fh);
        byte moveAction;
        if (template.getMoveAbility() == MoveAbility.Fly) {
            moveAction = MoveActionType.Fly1;
        } else {
            moveAction = template.getMoveAbility() == MoveAbility.Stop ? MoveActionType.Stand : MoveActionType.Move;
        }
        mob.setMovePosition(x, y, (byte) ((left & 1 | 2 * moveAction) & 0xFF), fh);
        mob.setController(ctrl);
        
        if (mobType == MobType.SUBMOB) {
            if (subMobCount < 0) subMobCount = 0;
            subMobCount++;
            mob.setMobType(MobType.SUBMOB);
        }
        if (mobType == MobType.BOSSMOB) {
            mob.setMobType(MobType.BOSSMOB);
        }
        
        field.makePointInSplit(x, y);
        field.splitRegisterFieldObj(x, y, FieldSplit.Mob, mob);
        
        mobs.put(mob.getGameObjectID(), mob);
        mob.sendChangeControllerPacket(ctrl.getUser(), MobCtrl.Active_Int);
        
        ctrl.getCtrlMob().add(mob);
        updateCtrlHeap(ctrl);
        return true;
    }
    
    public Npc createNpc(WzProperty prop, int templateID, int x, int y) {
        short fh;
        byte f;
        Range horz = new Range();
        if (prop != null) {
            templateID = WzUtil.getInt32(prop.getNode("id"), templateID);
            x = WzUtil.getInt32(prop.getNode("x"), x);
            y = WzUtil.getInt32(prop.getNode("cy"), y);
            fh = WzUtil.getShort(prop.getNode("fh"), 0);
            horz.low = WzUtil.getInt32(prop.getNode("rx0"), 0);
            horz.high = WzUtil.getInt32(prop.getNode("rx1"), 0);
            f = WzUtil.getByte(prop.getNode("f"), 0);
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
            field.splitRegisterFieldObj(x, y, FieldSplit.Npc, npc);
            return npc;
        } else {
            Logger.logError("Failed in creating Npc(%d) in map(%d)", templateID, field.getFieldID());
            return null;
        }
    }
    
    public Field getField() {
        return field;
    }
    
    public Mob getMob(int mobID) {
        return mobs.get(mobID);
    }
    
    public Mob getMobByTemplateID(int templateID) {
        for (Mob mob : mobs.values()) {
            if (mob.getTemplateID() == templateID) {
                return mob;
            }
        }
        return null;
    }
    
    public int getMobCount(int mobID) {
        int count = 0;
        for (Mob mob : mobs.values()) {
            if (mob != null && mob.getTemplateID() == mobID) {
                ++count;
            }
        }
        return count;
    }
    
    public int getMobGenCount() {
        return mobGenCount;
    }
    
    public int getMobHP(int mobID) {
        for (Mob mob : mobs.values()) {
            if (mob != null && mob.getTemplateID() == mobID) {
                return mob.getHP();
            }
        }
        return -1;
    }
    
    public Npc getNpc(String name) {
        for (Npc npc : npcs.values()) {
            if (npc.getNpcTemplate().getName().equals(name)) {
                return npc;
            }
        }
        return null;
    }
    
    public Npc getNpc(int id) {
        return npcs.get(id);
    }
    
    public Npc getNpcByTemplateID(int templateID) {
        for (Npc npc : npcs.values()) {
            if (npc.getTemplateID() == templateID) {
                return npc;
            }
        }
        return null;
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
                createNpc(life, Integer.parseInt(id), 0, 0);
            } else {
                MobTemplate template = MobTemplate.getMobTemplate(Integer.parseInt(id));
                if (template == null) {
                    Logger.logError("Invalid mob template ID(%s) on map(%d)", id, field.getFieldID());
                    continue;
                }
                MobGen pmg = new MobGen();
                pmg.templateID = template.getTemplateID();
                pmg.x = WzUtil.getShort(life.getNode("x"), 0);
                
                short y = WzUtil.getShort(life.getNode("y"), 0);
                short cy = WzUtil.getShort(life.getNode("cy"), 0);
                short fh = WzUtil.getShort(life.getNode("fh"), 0);
                
                pmg.regenInterval = 1000 * WzUtil.getInt32(life.getNode("mobTime"), 0);
                pmg.regenAfter = 0;
                pmg.f = WzUtil.getByte(life.getNode("f"), 0);
                
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
    
    public void insertController(User user) {
        Controller ctrl = new Controller(user);
        heapCtrlMax.insert(ctrl);
        heapCtrlMin.insert(ctrl);
        controllers.put(user.getCharacterID(), ctrl);
        redistributeLife();
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
    
    public void onMobStatChangeSkill(User user, int mobID, SkillEntry skill, byte slv) {
        if (mobID != 0 && mobs.containsKey(mobID)) {
            Mob mob = mobs.get(mobID);
            if (mob != null) {
                mob.onMobStatChangeSkill(user, skill, slv, 0);
            }
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
    
    public void onPacket(User user, byte type, InPacket packet) {
        if (type >= ClientPacket.BEGIN_MOB && type <= ClientPacket.END_MOB) {
            onMobPacket(user, type, packet);
        } else if (type >= ClientPacket.BEGIN_NPC && type <= ClientPacket.END_NPC) {
            onNpcPacket(user, type, packet);
        }
    }
    
    public void redistributeLife() {
        if (heapCtrlMin.isEmpty()) return;
        
        List<Mob> ctrlMob = ctrlNull.getCtrlMob();
        List<Npc> ctrlNpc = ctrlNull.getCtrlNpc();
        
        for (Iterator<Npc> pos = ctrlNpc.iterator(); pos.hasNext(); ) {
            Npc npc = pos.next();
            Controller ctrl = heapCtrlMin.get();
            if (ctrl.getCtrlCount() >= LifePerUser.MAX) {
                break;
            }
            ctrl.getCtrlNpc().add(npc);
            npc.setController(ctrl);
            updateCtrlHeap(ctrl);
            npc.sendChangeControllerPacket(ctrl.getUser(), true);
            pos.remove();
        }
        
        for (Iterator<Mob> pos = ctrlMob.iterator(); pos.hasNext(); ) {
            Mob mob = pos.next();
            Controller ctrl = heapCtrlMin.get();
            if (ctrl.getCtrlCount() >= LifePerUser.MAX) {
                break;
            }
            ctrl.getCtrlMob().add(mob);
            mob.setController(ctrl);
            updateCtrlHeap(ctrl);
            mob.sendChangeControllerPacket(ctrl.getUser(), MobCtrl.Active_Int);
            pos.remove();
        }
        
        while (true) {
            Controller ctrlMin = heapCtrlMin.get();
            Controller ctrlMax = heapCtrlMax.get();
            if (ctrlMax.getCtrlCount() <= ctrlMin.getCtrlCount() + 1
                    || ctrlMax.getCtrlCount() <= LifePerUser.PROPER_MANY) {
                break;
            }
            if (!ctrlMax.getCtrlNpc().isEmpty()) {
                Npc npc = ctrlMax.getCtrlNpc().remove(0);
                updateCtrlHeap(ctrlMax);
                ctrlMin.getCtrlNpc().add(npc);
                npc.setController(ctrlMin);
                updateCtrlHeap(ctrlMin);
                npc.sendChangeControllerPacket(ctrlMax.getUser(), false);
                npc.sendChangeControllerPacket(ctrlMin.getUser(), true);
            } else {
                Mob mob = null;
                Iterator<Mob> posSave = null;
                for (Iterator<Mob> pos = ctrlMax.getCtrlMob().iterator(); pos.hasNext(); ) {
                    posSave = pos;
                    mob = pos.next();
                }
                if (mob == null) break;
                
                posSave.remove();
                updateCtrlHeap(ctrlMax);
                ctrlMin.getCtrlMob().add(mob);
                mob.setController(ctrlMin);
                updateCtrlHeap(ctrlMin);
                mob.sendChangeControllerPacket(ctrlMax.getUser(), (byte) 0);
                mob.sendChangeControllerPacket(ctrlMin.getUser(), MobCtrl.Active_Int);
            }
        }
    }
    
    public void removeAllMob() {
        for (Mob mob : mobs.values()) {
            mob.setForcedDead(true);
            removeMob(mob);
        }
    }
    
    public void removeController(User user) {
        Controller ctrlRemove = controllers.get(user.getCharacterID());
        if (ctrlRemove == null) {
            return;
        }
        
        List<Mob> ctrlMob = new ArrayList<>(ctrlRemove.getCtrlMob());
        List<Npc> ctrlNpc = new ArrayList<>(ctrlRemove.getCtrlNpc());
        
        controllers.remove(user.getCharacterID());
        heapCtrlMin.removeAt(ctrlRemove);
        heapCtrlMax.removeAt(ctrlRemove);
        
        for (Mob mob : ctrlMob) {
            Controller ctrl;
            if (heapCtrlMin.isEmpty()) {
                ctrl = ctrlNull;
            } else {
                ctrl = heapCtrlMin.get();
                if (ctrl.getCtrlCount() >= LifePerUser.MAX) {
                    ctrl = ctrlNull;
                }
            }
            ctrl.getCtrlMob().add(mob);
            updateCtrlHeap(ctrl);
            mob.setController(ctrl);
            mob.sendChangeControllerPacket(ctrl.getUser(), MobCtrl.Active_Int);
        }
        
        for (Npc npc : ctrlNpc) {
            Controller ctrl;
            if (heapCtrlMin.isEmpty()) {
                ctrl = ctrlNull;
            } else {
                ctrl = heapCtrlMin.get();
                if (ctrl.getCtrlCount() >= LifePerUser.MAX) {
                    ctrl = ctrlNull;
                }
            }
            ctrl.getCtrlNpc().add(npc);
            updateCtrlHeap(ctrl);
            npc.setController(ctrl);
            npc.sendChangeControllerPacket(ctrl.getUser(), true);
        }
    }
    
    public void removeMob(Mob mob) {
        mobs.remove(mob.getGameObjectID());
        
        Controller ctrl = mob.getController();
        List<Mob> ctrlMob = ctrl.getCtrlMob();
        ctrlMob.remove(mob);
        updateCtrlHeap(ctrl);
        mob.sendChangeControllerPacket(ctrl.getUser(), (byte) 0);
        
        field.splitUnregisterFieldObj(FieldSplit.Mob, mob);
        mob.setRemoved();
        
        if (mob.getMobType() == MobType.SUBMOB) {
            subMobCount--;
        }
    }
    
    public void removeMob(int mobTemplateID) {
        for (Mob mob : mobs.values()) {
            if (mob.getTemplateID() == mobTemplateID) {
                mob.setForcedDead(true);
                removeMob(mob);
            }
        }
    }
    
    public void removeNpc(Npc npc) {
        int templateID = npc.getTemplateID();
        
        npcs.remove(npc.getGameObjectID());
        npc.getController().getCtrlNpc().remove(npc);
        updateCtrlHeap(npc.getController());
        npc.sendChangeControllerPacket(npc.getController().getUser(), false);
        npc.getField().splitUnregisterFieldObj(FieldSplit.Npc, npc);
        
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
    
    public void removeNpcByTemplate(int templateID) {
        for (Npc npc : npcs.values()) {
            if (npc.getTemplateID() == templateID) {
                removeNpc(npc);
            }
        }
    }
    
    public void reset() {
        mobGenExcept.clear();
        removeAllMob();
        tryCreateMob(true);
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
        if (!reset && time - lastCreateMobTime < 7000) return;
        
        int ctrlCount = heapCtrlMin.getCount();
        int capacity;
        if (ctrlCount <= mobCapacityMin / 2) {
            capacity = mobCapacityMin;
        } else {
            capacity = ctrlCount >= mobCapacityMin * 2 ? mobCapacityMax : (2 * ctrlCount - mobCapacityMin) * (mobCapacityMax - mobCapacityMin) / (3 * mobCapacityMin) + mobCapacityMin;
        }
        int remainCapacity = capacity - mobs.size();
        if (remainCapacity <= 0) {
            return;
        }
        
        List<MobGen> mobGenPossible = new ArrayList<>();
        List<Point> points = new LinkedList<>();
        for (Mob mob : mobs.values()) {
            points.add(mob.getCurrentPos());
        }
        for (int i = 0; i < mobGenCount; i++) {
            if (!mobGenEnable) break;
            MobGen mg = mobGen.get(i);
            
            if (mobGenExcept.contains(mg.templateID)) {
                continue;
            }
            
            if (mg.regenInterval == 0) {
                Rect rc = new Rect(mg.x - 100, mg.y - 100, mg.x + 100, mg.y + 100);
                boolean create = true;
                for (Point pt : points) {
                    if (rc.ptInRect(pt)) {
                        create = false;
                        break;
                    }
                }
                if (!create) continue;
                mobGenPossible.add(mg);
            } else if (mg.regenInterval < 0) {
                if (!reset) continue;
                mobGenPossible.add(0, mg);
            } else {
                if (mg.mobCount.get() != 0 || time - mg.regenAfter < 0) continue;
                mobGenPossible.add(0, mg);
            }
            
            Point newMob = new Point(mg.x, mg.y);
            points.add(newMob);
        }
        points.clear();
        
        while (remainCapacity > 0 && !mobGenPossible.isEmpty()) {
            int idx = mobGenPossible.get(0).regenInterval != 0 ? 0 : Rand32.getRand(mobGenPossible.size(), 0).intValue();
            MobGen pmg = mobGenPossible.get(idx);
            mobGenPossible.remove(idx);
            
            if (createMob(pmg.templateID, pmg, pmg.x, pmg.y, pmg.fh, false, pmg.f, 0, null)) --remainCapacity;
        }
        mobGenPossible.clear();
        
        lastCreateMobTime = time;
    }
    
    public void update(long time) {
        for (Npc npc : npcs.values()) {
            npc.update(time);
        }
        for (Mob mob : mobs.values()) {
            mob.update(time);
        }
        tryCreateMob(false);
    }
    
    public void updateCtrlHeap(Controller ctrl) {
        if (ctrl == ctrlNull) return;
        heapCtrlMin.updateAt(ctrl);
        heapCtrlMax.updateAt(ctrl);
    }
    
    public boolean onUserAttack(User user, byte type, byte attackType, byte mobCount, byte damagePerMob, SkillEntry skill, byte slv, byte action, byte left, byte speedDegree, int bulletItemID, List<AttackInfo> attack, Point ballStart) {
        if (user.lock()) {
            try {
                int skillID = (skill != null) ? skill.getSkillID() : 0;
                int weaponItemID = user.getCharacter().getItem(ItemType.Equip, -BodyPart.Weapon).getItemID();
            
                if (mobCount > 0) {
                    attack.removeIf(info -> {
                        if (mobs.containsKey(info.mobID)) {
                            Mob mob = mobs.get(info.mobID);
                            if (mob != null) {
                                if (type == ClientPacket.UserMagicAttack) {
                                    user.getCalcDamage().MDamage(user.getCharacter(), user.getBasicStat(), user.getSecondaryStat(), mob.getMobStat(), damagePerMob, weaponItemID, action, skill, slv, info.damageCli, mobCount);
                                } else {
                                    user.getCalcDamage().PDamage(user.getCharacter(), user.getBasicStat(), user.getSecondaryStat(), mob.getMobStat(), damagePerMob, weaponItemID, bulletItemID, attackType, action, skill, slv, info.damageCli);
                                }
                            } else {
                                user.getCalcDamage().skip();
                            }
                            return false;
                        }
                        return true;
                    });
                }
            
                OutPacket packet = new OutPacket(LoopbackPacket.UserMeleeAttack + (attackType - AttackType.MELEE));
                packet.encodeInt(user.getCharacterID());
                packet.encodeByte((mobCount << 4) | damagePerMob);
                packet.encodeByte(slv);
                if (slv != 0) {
                    packet.encodeInt(skillID);
                }
                packet.encodeByte(action);
                packet.encodeByte(speedDegree);
                packet.encodeByte(SkillAccessor.getWeaponMastery(user.getCharacter(), weaponItemID, attackType, null));
                packet.encodeInt(bulletItemID);
                for (AttackInfo info : attack) {
                    packet.encodeInt(info.mobID);
                    if (info.mobID == 0) {
                        continue;
                    }
                    packet.encodeByte(info.hitAction);
                    for (int i = 0; i < damagePerMob; i++) {
                        packet.encodeShort(info.damageCli.get(i));
                    }
                }
                getField().splitSendPacket(user.getSplit(), packet, user);
            
                if (mobCount > 0) {
                    attack.removeIf(info -> {
                        if (mobs.containsKey(info.mobID)) {
                            Mob mob = mobs.get(info.mobID);
                            
                            Rect hitPoint = new Rect(mob.getCurrentPos().x, mob.getCurrentPos().y, mob.getCurrentPos().x, mob.getCurrentPos().y);
                            hitPoint.inflateRect(100, 100);
                            if (!hitPoint.ptInRect(info.hit)) {
                                info.hit = mob.getCurrentPos();
                            }
                            
                            int damageSum = 0;
                            if (damagePerMob > 0) {
                                for (int damage : info.damageCli) {
                                    damageSum += damage;
                                    if (mob.onMobHit(user, damage, attackType)) {
                                        removeMob(mob);
                                        mob.onMobDead(info.hit, info.delay);
                                        break;
                                    }
                                }
                            }
                            
                            if (skill != null && slv > 0) {
                                if (mob.getHP() > 0 && damageSum > 0) {
                                    mob.onMobStatChangeSkill(user, skill, slv, damageSum);
                                }
                            }
                            return false;
                        }
                        return true;
                    });
                }
            
                return mobCount != 0;
            } finally {
                user.unlock();
            }
        }
        return false;
    }

    public void onUserAttack(User user, int mobID, int damage, Point hit, short delay) {
        if (mobID != 0 && mobs.containsKey(mobID)) {
            Mob mob = mobs.get(mobID);
            if (mob.onMobHit(user, damage, (byte) 0)) {
                removeMob(mob);
                mob.onMobDead(hit, delay);
            }
        }
    }
    
    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    private static class CompareCtrlMin implements Comparator<Controller> {
        @Override
        public int compare(Controller p, Controller q) {
            if (p.getCtrlCount() > q.getCtrlCount()) {
                return 1;
            }
            return -1;
        }
    }
    
    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    private static class CompareCtrlMax implements Comparator<Controller> {
        @Override
        public int compare(Controller p, Controller q) {
            if (p.getCtrlCount() < q.getCtrlCount()) {
                return 1;
            }
            return -1;
        }
    }
}
