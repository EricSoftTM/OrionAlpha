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

import game.field.Creature;
import game.field.Field;
import game.field.GameObjectType;
import game.field.MovePath;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.field.life.Controller;
import game.field.life.MoveAbility;
import game.field.life.mob.MobDamageLog.Info;
import game.user.User;
import game.user.skill.SkillEntry;
import game.user.skill.SkillLevelData;
import game.user.skill.Skills.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;

/**
 *
 * @author Eric
 */
public class Mob extends Creature {
    private final MobTemplate template;
    private MobGen mobGen;
    private MobStat stat;
    private int mobType;
    private int templateID;
    private short homeFoothold;
    private boolean noDropPriority;
    private Controller controller;
    private boolean nextAttackPossible;
    private int hp;
    private int mp;
    private MobDamageLog damageLog;
    private Point curPos;
    private byte moveAction;
    private short footholdSN;
    private final List<Reward> rewardPicked;
    private boolean alreadyStealed;
    private int itemID_Stolen;
    private boolean forcedDead;
    private long lastAttack;
    private long lastMove;
    private long create;
    private final Map<Integer, Long> attackers;
    
    public Mob(Field field, MobTemplate template, boolean noDropPriority) {
        super();
        setField(field);
        
        this.template = template;
        this.noDropPriority = noDropPriority;
        this.mobType = 0;//MobSpecies.Beast
        this.controller = null;
        this.nextAttackPossible = false;
        this.damageLog = new MobDamageLog();
        this.footholdSN = 0;
        this.rewardPicked = new ArrayList<>();
        this.alreadyStealed = false;
        this.itemID_Stolen = 0;
        this.attackers = new HashMap<>();
        this.hp = getMaxHP();
        this.mp = getMaxMP();
        this.forcedDead = false;
        this.templateID = template.getTemplateID();
        this.stat = new MobStat();
        long time = System.currentTimeMillis();
        this.lastAttack = time;
        this.lastMove = time;
        this.create = time;
        this.stat.setFrom(template);
    }
    
    public double alterEXPbyLevel(byte level, double incEXP) {
        return incEXP;
    }
    
    public boolean checkIsPossibleMoveStart(User user, MovePath mp, Pointer<Boolean> result) {
        // TODO: Calculate this later
        
        return true;
    }
    
    public int distributeExp(Pointer<Integer> lastDamageCharacterID) {
        // TODO: Exp distribution handling
        
        return 0;
    }
    
    public void encodeInitData(OutPacket packet) {
        packet.encodeInt(getGameObjectID());
        packet.encodeInt(getTemplateID());
        packet.encodeShort(curPos.x);
        packet.encodeShort(curPos.y);
        packet.encodeByte(moveAction);
        packet.encodeShort(footholdSN);
        packet.encodeShort(homeFoothold);
        stat.encodeTemporary(packet, 0);
    }
    
    public Controller getController() {
        return controller;
    }
    
    public Point getCurrentPos() {
        return curPos;
    }
    
    public int getEXP() {
        return template.getEXP();
    }
    
    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.Mob;
    }
    
    public int getHP() {
        return hp;
    }
    
    public final byte getLevel() {
        return template.getLevel();
    }
    
    public final int getMaxHP() {
        return template.getMaxHP();
    }
    
    public final int getMaxMP() {
        return template.getMaxMP();
    }
    
    public MobGen getMobGen() {
        return mobGen;
    }
    
    public void getMobStat(MobStat mobStat) {
        
    }
    
    public int getMP() {
        return mp;
    }
    
    public MobTemplate getTemplate() {
        return template;
    }
    
    @Override
    public int getTemplateID() {
        return templateID;
    }
    
    public void giveReward(int ownerID, Point hit, short delay, boolean steal) {
        if (!alreadyStealed || !steal) {
            User user = User.findUser(ownerID);
            int ownerDropRate = 1;
            int ownerDropRate_Ticket = 1;
            if (user != null) {
                ownerDropRate = 1;//getMesoRate
                ownerDropRate_Ticket = 1;//getDropRate
            }
            List<Reward> rewards = Reward.create(template.getRewardInfo(), ownerDropRate, ownerDropRate_Ticket);
            if (rewards == null || rewards.isEmpty()) {
                return;
            }
            if (steal) {
                Reward reward = rewards.get((int) (Rand32.getInstance().random() % rewards.size()));
                if (reward.getItem() != null) {
                    itemID_Stolen = reward.getItem().getItemID();
                }
                if (reward.getType() == RewardType.Money) {
                    reward.setMoney(reward.getMoney() / 2);
                }
                rewards.clear();
                rewards.add(reward);
                alreadyStealed = true;
            } else {
                if (alreadyStealed && itemID_Stolen != 0) {
                    for (Iterator<Reward> it = rewards.iterator(); it.hasNext();) {
                        Reward reward = it.next();
                        if (reward.getItem() != null && reward.getItem().getItemID() == itemID_Stolen) {
                            it.remove();
                        }
                    }
                }
            }
            int x2 = hit.x + rewards.size() * -10;
            for (Reward reward : rewards) {
                getField().getDropPool().create(reward, ownerID, getGameObjectID(), hit.x, hit.y, x2, 0, delay, false, 0);
                x2 += 20;
            }
            if (steal) {
                if (rewards.get(0).getItem() != null)
                    itemID_Stolen = rewards.get(0).getItem().getItemID();
            }
        }
    }
    
    public void heal(int min, int max) {
        int decHP;
        if (max == min)
            decHP = Rand32.genRandom().intValue();
        else
            decHP = min + Rand32.genRandom().intValue() % (max - min);
        setMobHP(Math.min(decHP + getHP(), getMaxHP()));
    }
    
    public void init(MobGen mobGen, short fh) {
        this.damageLog.vainDamage = 0;
        this.damageLog.fieldID = getField().getFieldID();
        this.damageLog.initHP = getHP();
        if (mobGen != null && mobGen.regenInterval != 0)
            mobGen.mobCount.incrementAndGet();
        this.mobGen = mobGen;
        this.homeFoothold = template.getMoveAbility() != MoveAbility.Fly ? fh : 0;
    }
    
    public boolean isNextAttackPossible() {
        return nextAttackPossible;
    }
    
    public boolean isTimeToRemove(long time, boolean fixedMob) {
        if (fixedMob) {
            return System.currentTimeMillis() - create >= 30000;
        }
        // removeAfter doesn't exist yet, actually..
        return false;
    }
    
    @Override
    public OutPacket makeEnterFieldPacket() {
        return MobPool.onMobEnterField(this);
    }
    
    @Override
    public OutPacket makeLeaveFieldPacket() {
        byte deadType = MobLeaveField.RemainHP;
        if (hp <= 0 || isTimeToRemove(System.currentTimeMillis(), false) || forcedDead) {
            deadType = MobLeaveField.ETC;
        }
        return MobPool.onMobLeaveField(getGameObjectID(), deadType);
    }
    
    public void onMobDead(Point hit, short delay) {
        Pointer<Integer> lastDamageCharacterID = new Pointer<>(0);
        int ownerID = distributeExp(lastDamageCharacterID);
        if (ownerID != 0) {
            giveReward(ownerID, hit, delay, false);
        }
    }
    
    public boolean onMobHit(User user, int damage, byte attackType) {
        int characterID = user.getCharacterID();
        long time = System.currentTimeMillis();
        if (time - lastAttack > 5000 && (controller == null || user != controller.getUser()) && !nextAttackPossible)
            getField().getLifePool().changeMobController(characterID, this, true);
        attackers.put(characterID, time);
        if (damage > 0) {
            if (damage > 9999) {
                Logger.logError("Invalid Mob Damage. Name : %s, Damage: %d", user.getCharacterName(), damage);
                return false;
            }
            if (hp > 0) {
                damage = Math.min(damage, hp);
                damageLog.addLog(characterID, damage, time);
                setMobHP(hp - damage);
                return hp <= 0;
            }
        }
        return false;
    }
    
    public boolean onMobMove(boolean nextAttackPossible, byte action, int data) {
        long time = System.currentTimeMillis();
        lastMove = time;
        if (action >= 0) {//>= MobAct.Move
            // Wait, mob attacks don't exist yet because there's no MobSkill..
        }
        if (time - lastAttack > 5000) {
            int characterID = 0;
            if (controller != null && controller.getUser() != null)
                characterID = controller.getUser().getCharacterID();
            Info infoAdd = null;
            for (Info info : damageLog.getLog()) {
                if (info.characterID == characterID) {
                    infoAdd = info;
                }
            }
            if (infoAdd == null || time - infoAdd.time > 5000) {
                for (Iterator<Info> it = damageLog.getLog().descendingIterator(); it.hasNext();) {
                    Info info = it.next();
                    if (time - info.time > 5000)
                        break;
                    if (info.characterID != characterID && getField().getLifePool().changeMobController(info.characterID, this, true))
                        return false;
                }
            }
            this.lastAttack = time;
        }
        this.nextAttackPossible = nextAttackPossible;
        return true;
    }
    
    public void onMobStatChangeSkill(User user, SkillEntry skill, byte slv, int damageSum) {
        SkillLevelData level = skill.getLevelData(slv);
        int prop = level.getProp();
        int skillID = skill.getSkillID();
        if (prop == 0) {
            prop = 100;
        }
        if (Rand32.genRandom() % 100 >= prop) {
            return;
        }
        
        switch (skillID) {
            case Wizard2.ColdBeam: {
                break;
            }
            case Hunter.ArrowBomb:
            case Thief.Steal: {
                break;
            }
            case Wizard1.PoisonBreath: {
                break;
            }
            case Wizard1.Slow:
            case Wizard2.Slow: {
                break;
            }
            case Page.Threaten:
            case Rogue.Disorder: {
                break;
            }
        }
    }
    
    public void sendChangeControllerPacket(User user, byte level) {
        if (user != null) {
            if (level != 0) {
                user.sendPacket(MobPool.onMobChangeController(this, 0, level));
            } else {
                sendReleaseControlPacket(user, getGameObjectID());
            }
        }
    }
    
    public static void sendReleaseControlPacket(User user, int mobID) {
        if (user != null) {
            user.sendPacket(MobPool.onMobChangeController(null, mobID, (byte) 0));
        }
    }
    
    public void setController(Controller ctrl) {
        this.nextAttackPossible = false;
        //this.skillCommand = 0;
        this.controller = ctrl;
        long time = System.currentTimeMillis();
        this.lastAttack = time;
        this.lastMove = time;
    }
    
    public void setForcedDead(boolean forced) {
        this.forcedDead = forced;
    }
    
    public void setMobHP(int hp) {
        if (hp >= 0 && hp <= getMaxHP() && hp != getHP()) {
            this.hp = hp;
        }
    }
    
    public void setMobType(int type) {
        this.mobType = type;
    }
    
    public boolean setMovePosition(int x, int y, byte moveAction, short sn) {
        byte action = (byte) (moveAction >> 1);
        if (action < 1 || action >= 17) {
            return false;
        } else {
            if (this.curPos == null) {
                this.curPos = new Point(0, 0);
            }
            this.curPos.x = x;
            this.curPos.y = y;
            this.moveAction = moveAction;
            this.footholdSN = template.getMoveAbility() != MoveAbility.Fly ? sn : 0;
            return true;
        }
    }
    
    public void setRemoved() {
        GameObjectBase.unregisterGameObject(this);
        if (mobGen != null) {
            if (mobGen.regenInterval != 0) {
                int mobCount = mobGen.mobCount.decrementAndGet();
                if (mobCount == 0) {
                    long regen = 0;
                    int delay = 7 * mobGen.regenInterval / 10;
                    if (delay != 0)
                        regen = 13 * mobGen.regenInterval / 10 + Rand32.getInstance().random() % delay;
                    mobGen.regenAfter = regen + System.currentTimeMillis();
                }
            }
        }
    }
    
    public void update(long time) {
        if (hp <= 0)
            return;
        if (time - lastMove > 5000) {
            getField().getLifePool().changeMobController(0, this, false);
            lastAttack = time;
            lastMove = time;
        }
        for (Iterator<Long> it = attackers.values().iterator(); it.hasNext();) {
            long attackTime = it.next();
            if (time - attackTime > 1000 * 5)
                it.remove();
        }
    }
}
