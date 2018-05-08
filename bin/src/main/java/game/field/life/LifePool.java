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
import game.field.StaticFoothold;
import game.field.life.heapbase.CompareCtrlMax;
import game.field.life.heapbase.CompareCtrlMin;
import game.field.life.mob.Mob;
import game.field.life.mob.MobGen;
import game.field.life.mob.MobTemplate;
import game.field.life.npc.Npc;
import game.field.life.npc.NpcTemplate;
import game.user.User;
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
    
    public void init(Field field, WzProperty mapData) {
        if (getField() != field || mapData == null) {
            return;
        }
        
        this.mobCapacityMin = 0;
        this.mobCapacityMax = 0;
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
    
    public void update(long time) {
        
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
        /*index = 0;
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
        }*/
        // Assume final reiteration from head of each heap.
        index = 0;
        while (true) {
            Controller pCtrlMin = ctrlMin.getHeap().get(index);
            Controller pCtrlMax = ctrlMax.getHeap().get(index);
            
            if (pCtrlMax.getCtrlCount() <= pCtrlMin.getCtrlCount() || pCtrlMax.getCtrlCount() <= 20) {
                return;
            }
            
            if (!pCtrlMax.getCtrlNpc().isEmpty()) {
                Npc npc = pCtrlMax.getCtrlNpc().remove(0);
                updateCtrlHeap(pCtrlMax);
                pCtrlMin.getCtrlNpc().add(npc);
                npc.setController(pCtrlMin);
                updateCtrlHeap(pCtrlMin);
                npc.sendChangeControllerPacket(pCtrlMax.getUser(), false);
                npc.sendChangeControllerPacket(pCtrlMin.getUser(), true);
                continue;
            }
            
            if (pCtrlMax.getCtrlMob().isEmpty() || pCtrlMax.getCtrlMob().get(0) == null)
                break;
            /*
            Mob mob = pCtrlMax.getCtrlMob().get(0);
            if (mob == null || mob.getTemplateID() == Mob.FixedMobID) {
                for (Mob p : pCtrlMax.getCtrlMob()) {
                    if (p != null && p.getTemplateID() != Mob.FixedMobID) {
                        mob = p;
                        break;
                    }
                }
            }
            if (mob == null || mob.getTemplateID() == Mob.FixedMobID)
                break;
            pCtrlMax.getCtrlMob().remove(mob);
            updateCtrlHeap(pCtrlMax);
            pCtrlMin.getCtrlMob().add(mob);
            mob.setController(pCtrlMin);
            updateCtrlHeap(pCtrlMin);
            mob.sendChangeControllerPacket(pCtrlMax.getUser(), 0);
            mob.sendChangeControllerPacket(pCtrlMin.getUser(), MobCtrl.Active_Int);
            */
        }
    }
    
    public boolean changeMobController(int characterID, Mob mobWanted, boolean chase) {
        return true;
    }
    
    public void tryCreateMob(boolean reset) {
        
    }
    
    public boolean createMob() {
        return true;
    }
    
    public void removeMob(Mob mob) {
        
    }
    
    public void insertMobGen(MobGen mobGen) {
        
    }
    
    public void onMobPacket(User user, byte type, InPacket packet) {
        
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
