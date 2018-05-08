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
import game.field.life.Controller;
import game.user.User;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.packet.OutPacket;
import util.Pointer;

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
    private final Map<Integer, Integer> attackers;
    
    public Mob(Field field, MobTemplate template, boolean noDropPriority) {
        super();
        setField(field);
        
        this.template = template;
        this.noDropPriority = noDropPriority;
        this.mobType = 0;//MobSpecies.Beast
        this.controller = null;
        this.nextAttackPossible = false;
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
    
    public int distributeExp(Pointer<Integer> ownType, Pointer<Integer> lastDamageCharacterID) {
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
    
    public int getEXP() {
        return template.getEXP();
    }
    
    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.Mob;
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
    
    public void getMobStat(MobStat mobStat) {
        
    }
    
    @Override
    public int getTemplateID() {
        return templateID;
    }
    
    public void giveMoney(User user) {//, AttackInfo ai, int attackCount
        
    }
    
    public void giveReward(int ownerID, Point hit, short delay, boolean steal) {
        
    }
    
    public void heal(int min, int max) {
        // can mobs even heal yet? guess so because HPRecovery/MPRecovery exists
    }
    
    public void init(MobGen mobGen, short fh) {
        
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
}
