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
package game.field.life.mob;

import game.field.drop.Reward;
import game.field.drop.RewardInfo;
import game.field.life.MoveAbility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzNodeType;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzSAXProperty;
import util.wz.WzUtil;
import util.wz.WzXML;

/**
 *
 * @author Eric
 */
public class MobTemplate implements WzXML {
    private static final WzPackage mobDir = new WzFileSystem().init("Mob").getPackage();
    private static final Map<Integer, MobTemplate> templates = new HashMap<>();
    private static final Lock lockMob = new ReentrantLock();
    private int templateID;
    private String name;
    private boolean bodyAttack;
    private byte moveAbility;
    private boolean boss;
    private byte level;
    private short maxHP;
    private short maxMP;
    private byte speed;
    private short pad;
    private short pdd;
    private short mad;
    private short mdd;
    private byte acc;
    private byte eva;
    private int exp;
    private int pushedDamage;
    private final List<Integer> damagedElemAttr;
    private short hpRecovery;
    private short mpRecovery;
    private boolean undead;
    private final List<MobAttackInfo> attackInfo;
    private final List<RewardInfo> rewardInfo;
    
    public MobTemplate() {
        this.damagedElemAttr = new ArrayList<>(AttackElem.Count);
        this.attackInfo = new ArrayList<>();
        this.rewardInfo = new ArrayList<>();
        
        for (int i = 0; i < AttackElem.Count; i++) {
            this.damagedElemAttr.add(i, AttackElemAttr.None);
        }
    }
    
    public static MobTemplate getMobTemplate(int templateID) {
        lockMob.lock();
        try {
            return templates.get(templateID);
        } finally {
            lockMob.unlock();
        }
    }
    
    public static void load(boolean useSAX) {
        Logger.logReport("Loading Mob Attributes");
        if (useSAX) {
            for (WzSAXProperty mobData : mobDir.getSAXEntries().values()) {
                registerMob(Integer.parseInt(mobData.getFileName().replaceAll(".img.xml", "")), mobData);
            }
        } else {
            for (WzProperty mobData : mobDir.getEntries().values()) {
                registerMob(Integer.parseInt(mobData.getNodeName().replaceAll(".img", "")), mobData);
            }
        }
    }

    private static void registerMob(int templateID, WzProperty prop) {
        WzProperty info = prop.getNode("info");
        if (info == null) {
            return;
        }
        MobTemplate template = new MobTemplate();
        template.templateID = templateID;
        
        boolean jump = prop.getNode("jump") != null;
        boolean move = prop.getNode("move") != null;
        boolean fly = prop.getNode("fly") != null;
        if (fly) {
            template.moveAbility = MoveAbility.Fly;
        } else {
            if (jump) {
                if (!move) {
                    template.moveAbility = MoveAbility.Jump;
                }
            } else {
                template.moveAbility = move ? MoveAbility.Walk : MoveAbility.Stop;
            }
        }
        template.name = WzUtil.getString(info.getNode("name"), "NULL");
        template.bodyAttack = WzUtil.getBoolean(info.getNode("bodyAttack"), false);
        template.boss = WzUtil.getBoolean(info.getNode("boss"), false);
        template.level = WzUtil.getByte(info.getNode("level"), 1);
        template.maxHP = WzUtil.getShort(info.getNode("maxHP"), 0);
        template.maxMP = WzUtil.getShort(info.getNode("maxMP"), 0);
        template.speed = (byte) Math.min(140, Math.max(0, WzUtil.getByte(info.getNode("speed"), 0)));
        template.pad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("PADamage"), 0)));
        template.pdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("PDDamage"), 0)));
        template.mad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("MADamage"), 0)));
        template.mdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(info.getNode("MDDamage"), 0)));
        template.acc = WzUtil.getByte(info.getNode("acc"), 0);
        template.eva = WzUtil.getByte(info.getNode("eva"), 0);
        template.exp = WzUtil.getInt32(info.getNode("exp"), 0);
        template.pushedDamage = WzUtil.getInt32(info.getNode("pushed"), 1);
        
        String elemAttr = WzUtil.getString(info.getNode("elemAttr"), null);
        if (elemAttr != null && !elemAttr.isEmpty()) {
            for (int i = 0; i < elemAttr.length(); i += 2) {
                int elem = AttackElem.getElementAttribute(elemAttr.charAt(i));
                int attr = elemAttr.charAt(i + 1) - '0';
                
                template.damagedElemAttr.set(elem, attr);
            }
        }
        
        template.hpRecovery = WzUtil.getShort(info.getNode("hpRecovery"), 0);
        template.mpRecovery = WzUtil.getShort(info.getNode("mpRecovery"), 0);
        template.undead = WzUtil.getBoolean(info.getNode("undead"), false);
        
        for (int i = 1; ; i++) {
            WzProperty attack = prop.getNode(String.format("attack%d", i));
            if (attack == null) {
                break;
            }
            WzProperty attackInfo = attack.getNode("info");
            if (attackInfo != null) {
                byte type = WzUtil.getByte(attackInfo.getNode("type"), 0);
                short conMP = WzUtil.getShort(attackInfo.getNode("conMP"), 0);
                boolean magic = WzUtil.getBoolean(attackInfo.getNode("magic"), false);
                
                template.attackInfo.add(new MobAttackInfo(type, conMP, magic));
            }
        }
        
        Reward.loadReward(templateID, template.rewardInfo);
        
        templates.put(templateID, template);
    }
    
    private static void registerMob(int templateID, WzSAXProperty prop) {
        MobTemplate template = new MobTemplate();
        template.templateID = templateID;
        
        prop.addEntity(template);
        prop.parse();
        
        Reward.loadReward(templateID, template.rewardInfo);
        templates.put(templateID, template);
    }
    
    public static void unload() {
        templates.clear();
    }
    
    @Override
    public void parse(String root, String name, String value, WzNodeType type) {
        if (type.equals(WzNodeType.IMGDIR)) {
            switch (name) {
                case "fly":
                    this.moveAbility = MoveAbility.Fly;
                    break;
                case "jump":
                    this.moveAbility = MoveAbility.Jump;
                    break;
                case "move":
                    this.moveAbility = MoveAbility.Walk;
                    break;
                default: {
                    if (name.startsWith("attack") && !name.equals("attackF")) {
                        this.attackInfo.add(new MobAttackInfo());
                    }
                }
            }
        } else if (type.equals(WzNodeType.INT)) {
            if (!this.attackInfo.isEmpty()) {
                MobAttackInfo attack = this.attackInfo.get(this.attackInfo.size() - 1);
                switch (name) {
                    case "type":
                        attack.type = WzUtil.getByte(value, 0);
                        break;
                    case "conMP":
                        attack.conMP = WzUtil.getShort(value, 0);
                        break;
                    case "magic":
                        attack.magicAttack = WzUtil.getBoolean(value, false);
                        break;
                }
            }
        }
        switch (name) {
            case "name":
                this.name = value;
                if (this.name == null) {
                    this.name = "NULL";
                }
                break;
            case "bodyAttack":
                this.bodyAttack = WzUtil.getBoolean(value, false);
                break;
            case "boss":
                this.boss = WzUtil.getBoolean(value, false);
                break;
            case "level":
                this.level = WzUtil.getByte(value, 1);
                break;
            case "maxHP":
                this.maxHP = WzUtil.getShort(value, 0);
                break;
            case "maxMP":
                this.maxMP = WzUtil.getShort(value, 0);
                break;
            case "speed":
                this.speed = (byte) Math.min(140, Math.max(0, WzUtil.getByte(value, 0)));
                break;
            case "PADamage":
                this.pad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "PDDamage":
                this.pdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "MADamage":
                this.mad = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "MDDamage":
                this.mdd = (short) Math.min(1999, Math.max(0, WzUtil.getShort(value, 0)));
                break;
            case "acc":
                this.acc = WzUtil.getByte(value, 0);
                break;
            case "eva":
                this.eva = WzUtil.getByte(value, 0);
                break;
            case "exp":
                this.exp = WzUtil.getInt32(value, 0);
                break;
            case "pushed":
                this.pushedDamage = WzUtil.getInt32(value, 1);
                break;
            case "elemAttr": {
                String elemAttr = value;
                for (int i = 0; i < elemAttr.length(); i += 2) {
                    int elem = AttackElem.getElementAttribute(elemAttr.charAt(i));
                    int attr = elemAttr.charAt(i + 1) - '0';

                    this.damagedElemAttr.set(elem, attr);
                }
                break;
            }
            case "hpRecovery":
                this.hpRecovery = WzUtil.getShort(value, 0);
                break;
            case "mpRecovery":
                this.mpRecovery = WzUtil.getShort(value, 0);
                break;
            case "undead":
                this.undead = WzUtil.getBoolean(value, false);
                break;
        }
    }
    
    public int getTemplateID() {
        return templateID;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isBodyAttack() {
        return bodyAttack;
    }
    
    public byte getMoveAbility() {
        return moveAbility;
    }
    
    public boolean isBoss() {
        return boss;
    }
    
    public byte getLevel() {
        return level;
    }
    
    public short getMaxHP() {
        return maxHP;
    }
    
    public short getMaxMP() {
        return maxMP;
    }
    
    public byte getSpeed() {
        return speed;
    }
    
    public short getPAD() {
        return pad;
    }
    
    public short getPDD() {
        return pdd;
    }
    
    public short getMAD() {
        return mad;
    }
    
    public short getMDD() {
        return mdd;
    }
    
    public byte getACC() {
        return acc;
    }
    
    public byte getEVA() {
        return eva;
    }
    
    public int getEXP() {
        return exp;
    }
    
    public int getPushedDamage() {
        return pushedDamage;
    }
    
    public short getHPRecovery() {
        return hpRecovery;
    }
    
    public short getMPRecovery() {
        return mpRecovery;
    }
    
    public boolean isUndead() {
        return undead;
    }
    
    public List<Integer> getDamagedElemAttr() {
        return damagedElemAttr;
    }
    
    public List<MobAttackInfo> getAttackInfo() {
        return attackInfo;
    }
    
    public List<RewardInfo> getRewardInfo() {
        return rewardInfo;
    }
}
