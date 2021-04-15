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

import common.Request;
import game.field.Creature;
import game.field.Field;
import game.field.GameObjectType;
import game.field.MovePath;
import game.field.MovePath.Elem;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;
import util.Rect;

/**
 *
 * @author Eric
 */
public class Mob extends Creature {
    private final MobTemplate template;
    private MobGen mobGen;
    private MobStat stat;
    private byte mobType;
    private byte summonType;
    private int templateID;
    private short homeFoothold;
    private boolean noDropPriority;
    private Controller controller;
    private boolean nextAttackPossible;
    private boolean experiencedMoveStateChange;
    private int hp;
    private int mp;
    private MobDamageLog damageLog;
    private Point curPos;
    private byte moveAction;
    private short footholdSN;
    private final List<Reward> rewardPicked;
    private boolean alreadyStealed;
    private int itemID_Stolen;
    private final List<Rect> moves;
    private final Lock lockMoveRect;
    private short lastX;
    private short lastY;
    private boolean forcedDead;
    private long lastAttack;
    private long lastMove;
    private long create;
    private long lastRecovery;
    private long lastUpdatePoison;
    private final Map<Integer, Long> attackers;
    
    public Mob(Field field, MobTemplate template, boolean noDropPriority) {
        super();
        setField(field);
        
        this.template = template;
        this.noDropPriority = noDropPriority;
        this.mobType = 0;//MobSpecies.Beast
        this.controller = null;
        this.nextAttackPossible = false;
        this.lastUpdatePoison = 0;
        this.damageLog = new MobDamageLog();
        this.footholdSN = 0;
        this.rewardPicked = new ArrayList<>();
        this.alreadyStealed = false;
        this.itemID_Stolen = 0;
        this.moves = new ArrayList<>();
        this.lockMoveRect = new ReentrantLock();
        this.lastX = 0;
        this.lastY = 0;
        this.experiencedMoveStateChange = false;
        this.attackers = new HashMap<>();
        this.hp = getMaxHP();
        this.mp = getMaxMP();
        this.forcedDead = false;
        this.templateID = template.getTemplateID();
        this.stat = new MobStat();
        long time = System.currentTimeMillis();
        this.lastRecovery = time;
        this.lastAttack = time;
        this.lastMove = time;
        this.create = time;
        this.stat.setFrom(template);
    }
    
    public double alterEXPbyLevel(byte level, double incEXP) {
        return incEXP;
    }
    
    public boolean checkIsPossibleMoveStart(User user, MovePath mp, Pointer<Boolean> result) {
        Point moveStart = new Point(0, 0);
        boolean suddenMove = true;
        Elem head = mp.getElem().getFirst();
        Elem tail = mp.getElem().getLast();
        short x = head.getX();
        short y = head.getY();
        
        moveStart.x = x;
        moveStart.y = y;
        if (lastX == 0 || lastY == 0 || Math.abs(lastX - x) <= 500)
            suddenMove = false;
        lastX = tail.getX();
        lastY = tail.getY();
        if (experiencedMoveStateChange || Rand32.genRandom() % 100 >= 20)
            return false;
        lockMoveRect.lock();
        try {
            if (moves.isEmpty() || moves.size() < 2) {
                return false;
            }
            Rect rc = moves.get(moves.size() - 1);//arcMove.a -> pTail?
            Rect move = new Rect();
            move.left = rc.left;
            move.top = rc.top;
            move.right = rc.right;
            move.bottom = rc.bottom;
            for (int i = 1; i < moves.size(); i++) {
                move = move.unionRect(moves.get(i));
            }
            move.inflateRect(300, 300);
            
            boolean bMove = move.ptInRect(moveStart);
            if (bMove || !suddenMove)
                bMove = true;
            result.set(bMove);
            
            return true;
        } finally {
            lockMoveRect.unlock();
        }
    }
    
    public int distributeExp(Pointer<Integer> lastDamageCharacterID) {
        int damageSum = damageLog.vainDamage;
        int maxDamage = 0;
        int characterID = 0;
        for (Info info : damageLog.getLog()) {
            damageSum += info.damage;
            if (maxDamage < info.damage) {
                maxDamage = info.damage;
                characterID = info.characterID;
            }
            lastDamageCharacterID.set(info.characterID);
        }
        if (damageSum >= damageLog.initHP) {
            if (getEXP() != 0) {
                int idx = damageLog.getLog().size();
                for (Info info : damageLog.getLog()) {
                    if (info.damage > 0) {
                        User user = getField().findUser(info.characterID);
                        if (user == null || user.getField() == null || user.getField().getFieldID() != damageLog.fieldID) {
                            continue;
                        }
                        double lastSum = 0.0d;
                        if (idx == 0) {
                            lastSum = (double) getEXP() * 0.2d;
                        }
                        if (user.lock(1600)) {
                            try {
                                double incEXP = (((double) getEXP() * (double) info.damage) * 0.8 / (double) damageSum + lastSum);
                                incEXP = alterEXPbyLevel(user.getCharacter().getCharacterStat().getLevel(), incEXP);
                                incEXP *= user.getExpRate();
                                incEXP *= getField().getIncEXPRate();
                                incEXP = Math.max(1.0, incEXP);
                                
                                int flag = user.incEXP((int) incEXP, false);
                                if (flag != 0) {
                                    user.sendCharacterStat(Request.None, flag);
                                }
                            } finally {
                                user.unlock();
                            }
                        }
                        idx--;
                    }
                }
            }
            return characterID;
        } else {
            Logger.logError("Mob damaged HP is less than initial HP");
        }
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
        packet.encodeByte(summonType);
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
    
    public MobStat getMobStat() {
        return stat;
    }
    
    public void getMobStat(MobStat mobStat) {
        
    }
    
    public int getMP() {
        return mp;
    }
    
    public byte getSummonType() {
        return summonType;
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
            User user = getField().findUser(ownerID);
            int ownerDropRate = 1;
            int ownerDropRate_Ticket = 1;
            if (user != null) {
                ownerDropRate = (int) user.getMesoRate();
                ownerDropRate_Ticket = (int) user.getTicketDropRate();
            }
            List<Reward> rewards = Reward.create(template.getRewardInfo(), false, ownerDropRate, ownerDropRate_Ticket);
            if (rewards == null || rewards.isEmpty()) {
                return;
            }
            if (steal) {
                Reward reward = rewards.get((int) (Rand32.getInstance().random() % rewards.size()));
                if (reward.getItem() != null) {
                    itemID_Stolen = reward.getItem().getItemID();
                }
                if (reward.getType() == RewardType.MONEY) {
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
            //int flagReset = stat.resetTemporary(time);
            if (hp > 0) {
                damage = Math.min(damage, hp);
                damageLog.addLog(characterID, damage, time);
                setMobHP(hp - damage);
                //if (hp > 0) {
                    //if (attackType == 1 && stat.getStatOption(MobStats.Stun) != 0) {
                        //flagReset |= stat.reset(MobStats.Stun);
                    //}
                //}
                //sendMobTemporaryStatReset(flagReset);
                return hp <= 0;
            }
        }
        return false;
    }
    
    public int onMobMPSteal(int prop, int percent) {
        int decMP = 0;
        if (template.isBoss()) {
            decMP = Math.min(percent * getMaxMP() / 100, getMP());
            if (Rand32.genRandom() % 100 >= prop || decMP < 0)
                decMP = 0;
            this.mp -= decMP;
        }
        return decMP;
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
                    break;
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
        int x = level.getX();
        int y = level.getY();
        int skillID = skill.getSkillID();
        if (prop == 0) {
            prop = 100;
        }
        long time = System.currentTimeMillis();
        long duration = time + 1000 * level.getTime();
        if (Rand32.genRandom() % 100 >= prop || template.isBoss()) {
            return;
        }
        
        MobStatOption opt = new MobStatOption();
        opt.setOption(x);
        opt.setReason(skillID);
        opt.setDuration(duration);
        
        int flag = 0;
        switch (skillID) {
            case Wizard2.ColdBeam: {
                int attr;
                if ((attr = template.getDamagedElemAttr().get(AttackElem.Ice)) == AttackElemAttr.Damage0 || attr == AttackElemAttr.Damage50) {
                    return;
                }
                opt.setOption(1);
                flag |= stat.setStat(MobStats.Freeze, opt);
                this.experiencedMoveStateChange = true;
                break;
            }
            case Hunter.ArrowBomb:
            case Thief.Steal: {
                if (skillID == Hunter.ArrowBomb && damageSum < template.getPushedDamage() && (Rand32.genRandom() % 3) != 0) {
                    return;
                }
                opt.setOption(1);
                flag |= stat.setStat(MobStats.Stun, opt);
                this.experiencedMoveStateChange = true;
                break;
            }
            case Wizard1.PoisonBreath: {
                int attr;
                if ((attr = template.getDamagedElemAttr().get(AttackElem.Poison)) == AttackElemAttr.Damage0 || attr == AttackElemAttr.Damage50) {
                    return;
                }
                opt.setOption(Math.max(Math.min(Short.MAX_VALUE, getMaxHP() / (70 - slv)), level.getMAD()));
                opt.setModOption(user.getCharacterID());
                this.lastUpdatePoison = time;
                flag |= stat.setStat(MobStats.Poison, opt);
                break;
            }
            case Wizard1.Slow:
            case Wizard2.Slow: {
                flag |= stat.setStat(MobStats.Speed, opt);
                break;
            }
            case Page.Threaten:
            case Rogue.Disorder: {
                flag |= stat.setStat(MobStats.PAD, opt);
                flag |= stat.setStat(MobStats.PDD, new MobStatOption(y, skillID, duration));
                break;
            }
            default: {
                return;
            }
        }
        if (flag != 0) {
            sendMobTemporaryStatSet(flag, 0);
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
    
    public void sendMobTemporaryStatReset(int reset) {
        if (reset != 0) {
            //getField().splitSendPacket(getSplit(), MobPool.onStatSet(this, reset, 0, (short) 0), null);
        }
    }
    
    public void sendMobTemporaryStatSet(int flag, int delay) {
        if (flag != 0) {
            getField().splitSendPacket(getSplit(), MobPool.onStatSet(this, flag, stat.getStatReason(flag), (short) delay), null);
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
        this.mobType = (byte) type;
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
    
    public void setSummonType(int type) {
        this.summonType = (byte) type;
    }
    
    public void update(long time) {
        if (hp <= 0)
            return;
        if (stat.getStatOption(MobStats.Poison) != 0)
            updatePoison(time);
        int reset = stat.resetTemporary(time);
        if (hp == 1) {
            if (stat.getStatOption(MobStats.Poison) != 0)
                reset |= stat.reset(MobStats.Poison);
        }
        if (reset != 0) {
            sendMobTemporaryStatReset(reset);
        }
        if (time - lastRecovery > 8000) {
            setMobHP(Math.min(hp + template.getHPRecovery(), getMaxHP()));
            mp = Math.min(getMP() + template.getMPRecovery(), getMaxMP());
            lastRecovery = time;
        }
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
    
    public void updatePoison(long time) {
        if (stat.getStatOption(MobStats.Poison) > 0) {
            long lastPoison = Math.min(time, stat.getStatDuration(MobStats.Poison));
            long times = (lastPoison - lastUpdatePoison) / 1000;
            int oldHP = hp;
            int poison = stat.getStatOption(MobStats.Poison);
            
            setMobHP(Math.max(1, (int) (hp - times * poison)));
            
            if (hp != oldHP)
                damageLog.addLog(stat.getStat(MobStats.Poison).getModOption(), oldHP - hp, time);
            
            lastUpdatePoison += 1000 * times;
        }
    }
}
