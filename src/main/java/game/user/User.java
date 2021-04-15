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

import common.*;
import common.WhisperFlags.LocationResult;
import common.item.*;
import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
import common.user.UserEffect;
import game.Channel;
import game.GameApp;
import game.field.*;
import game.field.drop.Drop;
import game.field.drop.DropPickup;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.field.life.AttackIndex;
import game.field.life.AttackInfo;
import game.field.life.mob.Mob;
import game.field.life.mob.MobAttackInfo;
import game.field.life.mob.MobTemplate;
import game.field.life.npc.*;
import game.field.portal.Portal;
import game.field.portal.PortalMap;
import game.messenger.Messenger;
import game.miniroom.MiniRoom;
import game.miniroom.MiniRoomBase;
import game.script.ScriptVM;
import game.user.command.CommandHandler;
import game.user.command.UserGradeCode;
import game.user.item.*;
import game.user.skill.*;
import game.user.skill.Skills.*;
import game.user.stat.BasicStat;
import game.user.stat.CalcDamage;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.SecondaryStat;
import network.database.CommonDB;
import network.database.GameDB;
import network.packet.CenterPacket;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;
import util.Rect;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Eric
 */
public class User extends Creature {
    private int accountID;
    private int characterID;
    private int gradeCode;
    private int localSocketSN;
    // Account/Character Names
    private String nexonClubID;
    private String characterName;
    // Misc. Variables
    private final Lock lock;
    private final Lock lockSocket;
    private Npc tradingNpc;
    private long lastSelectNPCTime;
    private byte curFieldKey;
    private boolean onTransferField;
    private int incorrectFieldPositionCount;
    private long lastCharacterDataFlush;
    private long nextGeneralItemCheck;
    private long nextCheckCashItemExpire;
    private long lastCharacterHPInc;
    private long lastCharacterMPInc;
    private int illegalHPIncTime;
    private int illegalHPIncSize;
    private int illegalMPIncTime;
    private int illegalMPIncSize;
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
    private final CalcDamage calcDamage;
    private int invalidTryRepeatCount;
    private int invalidUserActionCount;
    private int invalidMobMoveCount;
    private int invalidHitPointCount;
    private int skipWarpCount;
    private int warpCheckedCount;
    private int invalidDamageCount;
    private int invalidDamageMissCount;
    // Character Data
    private final CharacterData character;
    private int characterDataModFlag;
    private final List<ItemSlotBase> realEquip;
    // Avatar Look
    private final AvatarLook avatarLook;
    private int avatarModFlag;
    // Basic Stat
    private final BasicStat basicStat;
    // Secondary Stat
    private final SecondaryStat secondaryStat;
    // User RNG's
    private final Rand32 rndActionMan;
    // Mini Rooms
    // private UserMiniRoom userMR;
    private MiniRoomBase miniRoom;
    private boolean miniRoomBalloon;
    // MSMessenger
    private Messenger userMSM;
    private boolean msMessenger;
    // ScriptVM
    private ScriptVM runningVM;
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

        this.hide = false;
        this.onTransferField = false;
        this.closeSocketNextTime = false;
        
        this.emotion = 0;
        this.curFieldKey = 0;
        this.invalidHitPointCount = 0;
        this.invalidMobMoveCount = 0;
        this.skipWarpCount = 0;
        this.warpCheckedCount = 0;
        this.invalidDamageCount = 0;
        this.invalidDamageMissCount = 0;
        this.tradeMoneyLimit = 0;
        this.tempTradeMoney = 0;
        this.accountID = -1;
        this.incorrectFieldPositionCount = 0;
        this.avatarModFlag = 0;
        this.characterDataModFlag = 0;
        this.lastSelectNPCTime = 0;

        long time = System.currentTimeMillis();
        this.lastCharacterHPInc = time;
        this.lastCharacterMPInc = time;
        this.lastCharacterDataFlush = time;
        this.nextCheckCashItemExpire = time;
        this.lastAttack = time;
        this.nexonClubID = "";
        this.characterName = "";
        this.community = "#TeamEric";

        this.curPos = new Point(0, 0);
        this.lock = new ReentrantLock(true);
        this.lockSocket = new ReentrantLock(true);
        this.basicStat = new BasicStat();
        this.secondaryStat = new SecondaryStat();
        this.avatarLook = new AvatarLook();
        this.rndActionMan = new Rand32();
        this.calcDamage = new CalcDamage();
        this.userSkill = new UserSkill(this);
        this.userMSM = new Messenger(this);
        // TODO: Nexon-like user caching to avoid DB load upon each login/migrate.
        this.character = GameDB.rawLoadCharacter(characterID);
        this.realEquip = new ArrayList<>(BodyPart.BP_Count + 1);

        for (int i = 0; i <= BodyPart.BP_Count; i++) {
            this.realEquip.add(i, null);
        }

        this.basicStat.clear();
        this.secondaryStat.clear();
    }

    public User(ClientSocket socket) {
        this(socket.getCharacterID());

        this.socket = socket;
        this.localSocketSN = socket.getLocalSocketSN();

        Pointer<Integer> grade = new Pointer<>(0);
        Pointer<Integer> id = new Pointer<>(0);
        Pointer<String> nexonID = new Pointer<>("");
        GameDB.rawLoadAccount(socket.getCharacterID(), id, nexonID, grade);

        this.accountID = id.get();
        this.nexonClubID = nexonID.get();
        this.gradeCode = grade.get();

        this.characterID = character.getCharacterStat().getCharacterID();
        this.characterName = character.getCharacterStat().getName();

        this.validateStat(true);

        // Apply default configured rates
        this.incExpRate *= GameApp.getInstance().getExpRate();
        this.incMesoRate = GameApp.getInstance().getMesoRate();
        this.incDropRate *= GameApp.getInstance().getDropRate();
        this.incDropRate_Ticket *= GameApp.getInstance().getDropRate();
    }

    /**
     * This is a User::~User deleting destructor.
     *
     * WARNING: This method should ONLY be used when you NULL the User object.
     */
    public final void destructUser() {
        /* Begin CUser::~CUser destructor */
        flushCharacterData(0, true);
        Logger.logReport("User logout");
        realEquip.clear();
        /* Begin CharacterData::~CharacterData destructor */
        for (List<Integer> itemTrading : character.getItemTrading())
            itemTrading.clear();
        character.getItemTrading().clear();
        //character.getQuestRecord().clear();
        character.getSkillRecord().clear();
        for (List<ItemSlotBase> itemSlot : character.getItemSlot())
            itemSlot.clear();
        character.getItemSlot().clear();
        character.getEquipped2().clear();
        character.getEquipped().clear();
        /* End CharacterData::~CharacterData destructor */
        if (miniRoom != null) {
            miniRoom = null;
        }
        if (runningVM != null) {
            runningVM = null;
        }
        /* End CUser::~CUser destructor */
    }

    ///////////////////////////// CQWUser START /////////////////////////////
    public boolean canStatChange(int inc, int dec) {
        // TODO

        return false;
    }

    public byte getLevel() {
        return character.getCharacterStat().getLevel();
    }

    public MiniRoomBase getMiniRoom() {
        return miniRoom;
    }

    public boolean incAP(int inc, boolean onlyFull) {
        lock.lock();
        try {
            int ap = character.getCharacterStat().getAP();
            if (onlyFull && (ap + inc < 0 || ap + inc > SkillAccessor.AP_MAX)) {
                return false;
            }
            int newSP = Math.max(Math.min(ap + inc, SkillAccessor.AP_MAX), 0);
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
            if (onlyFull && (DEX + inc < 0 || DEX + inc > SkillAccessor.DEX_MAX)) {
                return false;
            }
            int newDEX = Math.max(Math.min(DEX + inc, SkillAccessor.DEX_MAX), 0);
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
        if (lock(1500)) {
            try {
                int flag = CharacterStatType.EXP;
                if (getHP() > 0 && inc > 0 && (!onlyFull || inc + character.getCharacterStat().getEXP() >= 0)) {
                    if (inc < 0) {
                        if (character.getCharacterStat().getEXP() + inc < 0) {
                            character.getCharacterStat().setEXP(0);
                            addCharacterDataMod(DBChar.Character);
                            return flag;
                        }
                    }
                    if (inc <= 0) {
                        addCharacterDataMod(DBChar.Character);
                        return flag;
                    }
                    Pointer<Boolean> reachMaxLev = new Pointer<>(false);
                    if (ExpAccessor.tryProcessLevelUp(character, basicStat, inc, reachMaxLev)) {
                        incAP(5, false);
                        if (character.getCharacterStat().getJob() != 0) {
                            incSP(3, false);
                        }
                        flag |= CharacterStatType.LEV | CharacterStatType.AP | CharacterStatType.SP;
                        flag |= CharacterStatType.HP | CharacterStatType.MP | CharacterStatType.MHP | CharacterStatType.MMP;
                        validateStat(false);
                        onLevelUp();
                        if (reachMaxLev.get()) {
                            setMaxLevelReach();
                        }
                    }
                    addCharacterDataMod(DBChar.Character);
                    return flag;
                }
            } finally {
                unlock();
            }
        }
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
            if (onlyFull && (INT + inc < 0 || INT + inc > SkillAccessor.INT_MAX)) {
                return false;
            }
            int newINT = Math.max(Math.min(INT + inc, SkillAccessor.INT_MAX), 0);
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
            if (onlyFull && (LUK + inc < 0 || LUK + inc > SkillAccessor.LUK_MAX)) {
                return false;
            }
            int newLUK = Math.max(Math.min(LUK + inc, SkillAccessor.LUK_MAX), 0);
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
            if (onlyFull && (mhp + inc < 50 || mhp + inc > SkillAccessor.HP_MAX)) {
                return false;
            }
            int newMHP = Math.max(Math.min(mhp + inc, SkillAccessor.HP_MAX), 50);
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
            if (onlyFull && (mmp + inc < 5 || mmp + inc > SkillAccessor.MP_MAX)) {
                return false;
            }
            int newMMP = Math.max(Math.min(mmp + inc, SkillAccessor.MP_MAX), 5);
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
            if (onlyFull && (pop + inc < -SkillAccessor.POP_MAX || pop + inc > SkillAccessor.POP_MAX)) {
                return false;
            }
            int newPOP = Math.min(Math.max(pop + inc, -SkillAccessor.POP_MAX), SkillAccessor.POP_MAX);
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
            if (onlyFull && (sp + inc < 0 || sp + inc > SkillAccessor.SP_MAX)) {
                return false;
            }
            int newSP = Math.max(Math.min(sp + inc, SkillAccessor.SP_MAX), 0);
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
            if (onlyFull && (STR + inc < 0 || STR + inc > SkillAccessor.STR_MAX)) {
                return false;
            }
            int newSTR = Math.max(Math.min(STR + inc, SkillAccessor.STR_MAX), 0);
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
                if (character.getCharacterStat().getLevel() >= ExpAccessor.MAX_LEVEL && character.getCharacterStat().getEXP() > 0) {
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
        if (JobAccessor.findJob(val) != null) {
            lock.lock();
            try {
                List<Integer> curSkillRoot = new ArrayList<>();
                List<Integer> newSkillRoot = new ArrayList<>();
                SkillAccessor.getSkillRootFromJob(character.getCharacterStat().getJob(), curSkillRoot);
                SkillAccessor.getSkillRootFromJob(val, newSkillRoot);
                for (Iterator<Integer> it = curSkillRoot.iterator(); it.hasNext();) {
                    int skillRoot = it.next();
                    if (newSkillRoot.contains(skillRoot)) {
                        it.remove();
                    }
                }
                character.getCharacterStat().setJob((short) val);
                if (val != 0 && character.getCharacterStat().getLevel() < 11) {
                    // Do we still apply STR/DEX default stats in this ver?
                }
                if (!curSkillRoot.isEmpty()) {
                    List<SkillRecord> changes = new ArrayList<>();
                    for (int skillRoot : curSkillRoot) {
                        SkillRoot root = SkillInfo.getInstance().getSkillRoot(skillRoot);
                        if (root != null) {
                            for (SkillEntry skill : root.getSkills()) {
                                int skillID = skill.getSkillID();
                                character.getSkillRecord().remove(skillID);
                                SkillRecord change = new SkillRecord();
                                change.setInfo(-1);
                                change.setSkillID(skillID);
                                changes.add(change);
                            }
                        }
                    }
                    //UserSkillRecord.sendCharacterSkillRecord(this, false, changes);
                    addCharacterDataMod(DBChar.SkillRecord);
                    changes.clear();
                }
                /*if (!newSkillRoot.isEmpty()) {
                    List<SkillRecord> changes = new ArrayList<>();
                    for (int skillRoot : newSkillRoot) {
                        SkillRoot root = SkillInfo.getInstance().getSkillRoot(skillRoot);
                        if (root != null) {
                            for (SkillEntry skill : root.getSkills()) {
                                int skillID = skill.getSkillID();

                                // Ignore - we don't have masteries yet.
                            }
                        }
                    }
                    //UserSkillRecord.sendCharacterSkillRecord(this, false, changes);
                    addCharacterDataMod(DBChar.SkillRecord);
                    changes.clear();
                }*/
                validateStat(true);
                addCharacterDataMod(DBChar.Character);
                sendCharacterStat(Request.None, CharacterStatType.Job);
                //onUserEffect(false, true, UserEffect.JobChanged);
            } finally {
                unlock();
            }
        }
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
        if (socket != null && !onTransferField && getHP() > 0 && miniRoom == null && tradingNpc == null) {
            if (runningVM == null)
                return true;
        }
        return false;
    }

    public void destroyAdditionalProcess() {
        lock.lock();
        try {
            this.tradingNpc = null;
            if (miniRoom != null) {
                //miniRoom.onUserLeave(this);
	            miniRoom = null;
            }
            if (runningVM != null) {
            	runningVM.destruct();
                runningVM.destroy(this);
            }
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

    public BasicStat getBasicStat() {
        return basicStat;
    }

    public CalcDamage getCalcDamage() {
        return calcDamage;
    }

    public Channel getChannel() {
        if (socket != null) {
            return socket.getChannel();
        }
        return null;
    }

    public byte getChannelID() {
        if (socket != null) {
            return socket.getChannelID();
        }
        return 0;
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
    
    public byte getFieldKey() {
        return curFieldKey;
    }

    public int getGradeCode() {
        return gradeCode;
    }

    public short getHP() {
        return character.getCharacterStat().getHP();
    }

    public int getLocalSocketSN() {
        return localSocketSN;
    }

    public Messenger getMessenger() {
        return userMSM;
    }

    public int getPosMap() {
        return character.getCharacterStat().getPosMap();
    }

    public byte getPortal() {
        return character.getCharacterStat().getPortal();
    }

    public ScriptVM getScriptVM() {
        return runningVM;
    }

    public SecondaryStat getSecondaryStat() {
        return secondaryStat;
    }

    public ClientSocket getSocket() {
        return socket;
    }

    public byte getWorldID() {
        return GameApp.getInstance().getWorldID();
    }

    public double getExpRate() {
        return incExpRate;
    }

    public double getMesoRate() {
        return incMesoRate;
    }

    public double getDropRate() {
        return incDropRate;
    }

    public double getTicketDropRate() {
        return incDropRate_Ticket;
    }

    public boolean isGM() {
        return gradeCode >= UserGradeCode.GM.getGrade();
    }

    public boolean isHide() {
        return hide;
    }

    public void leaveField() {
        if (getField() != null) {
            getField().onLeave(this);
            if (getField().getForcedReturnFieldID() == Field.Invalid) {
                if (getHP() == 0) {
                    if (getField().getReturnFieldID() == Field.Invalid) {
                        if (lock()) {
                            try {
                                setPosMap(getField().getReturnFieldID());
                                if (OrionConfig.LOG_PACKETS) {
                                    Logger.logReport("To find crash : just before GetRandStartPoint2");
                                }
                                setPortal(getField().getPortal().getRandStartPoint().getPortalIdx());
                                if (getHP() == 0) {
                                    character.getCharacterStat().setHP(50);
                                }
                                addCharacterDataMod(DBChar.Character);
                            } finally {
                                unlock();
                            }
                        }
                    }
                    //setField(null);
                    return;
                }
                if (lock()) {
                    try {
                        Portal portal = getField().getPortal().findCloseStartPoint(curPos.x, curPos.y);
                        if (getPosMap() != getField().getFieldID() || getPortal() != portal.getPortalIdx()) {
                            setPosMap(getField().getFieldID());
                            setPortal(portal.getPortalIdx());
                            addCharacterDataMod(DBChar.Character);
                        }
                    } finally {
                        unlock();
                    }
                }
            } else {
                if (lock()) {
                    try {
                        setPosMap(getField().getForcedReturnFieldID());
                        Field field = FieldMan.getInstance(getChannelID()).getField(getField().getForcedReturnFieldID(), false);
                        byte portal = 0;
                        if (field.getPortal() != null) {
                            portal = field.getPortal().getRandStartPoint().getPortalIdx();
                        }
                        setPortal(portal);
                        addCharacterDataMod(DBChar.Character);
                    } finally {
                        unlock();
                    }
                }
            }
            //setField(null);
        }
    }

    @Override
    public boolean isShowTo(User user) {
        if (hide && user.gradeCode >= gradeCode) {
            return true;
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

    public void setScriptVM(ScriptVM vm) {
        this.runningVM = vm;
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
        Field field = FieldMan.getInstance(getChannelID()).getField(getPosMap(), false);
        setField(field);
        if (getHP() == 0) {
            character.getCharacterStat().setHP(50);
        }
        if (getField() == null) {
            field = FieldMan.getInstance(getChannelID()).getField(Field.Basic, false);
            setField(field);
            setPosMap(field.getFieldID());
            setPortal(field.getPortal().getRandStartPoint().getPortalIdx());
        }
        characterDataModFlag |= DBChar.Character;
        if (character.getCharacterStat().getLevel() >= ExpAccessor.MAX_LEVEL && character.getCharacterStat().getEXP() > 0) {
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
        if (getField().onEnter(this)) {
            // I'm genuinely curious why Nexon has migration packets for messenger,
            // when you can't "migrate" channels without logging out first?
            // The only "migration" done is to the Shop, which can't access messenger.
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
                getChannel().unregisterUser(this);
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
            case ClientPacket.UserMigrateToCashShopRequest:
                onMigrateToCashShopRequest();
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
            case ClientPacket.UserChangeSlotPositionRequest:
                onChangeSlotPositionRequest(packet);
                break;
            case ClientPacket.UserUpgradeItemUseRequest:
                onUpgradeItemRequest(packet);
                break;
            case ClientPacket.MiniRoom:
                MiniRoom.onMiniRoom(this, packet);
                break;
            case ClientPacket.UserAbilityUpRequest:
                onAbilityUpRequest(packet);
                break;
            case ClientPacket.UserStatChangeItemUseRequest:
                onStatChangeItemUseRequest(packet);
                break;
            case ClientPacket.UserShopRequest:
                onShopRequest(packet);
                break;
            case ClientPacket.UserSelectNpc:
                onSelectNpc(packet);
                break;
            case ClientPacket.Messenger:
                getMessenger().onMessenger(packet);
                break;
            case ClientPacket.UserConsumeCashItemUseRequest:
                onConsumeCashItemUseRequest(packet);
                break;
            case ClientPacket.UserScriptMessageAnswer:
                onScriptMessageAnswer(packet);
                break;
            case ClientPacket.UserChangeStatRequest:
                onChangeStatRequest(packet);
                break;
            case ClientPacket.Whisper:
                onWhisper(packet);
                break;
            case ClientPacket.UserPortalScrollUseRequest:
                onPortalScrollUseRequest(packet);
                break;
            case ClientPacket.BroadcastMsg:
                onBroadcastMsg(packet);
                break;
            case ClientPacket.Admin:
                onAdmin(packet);
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

    public void onAbilityUpRequest(InPacket packet) {
        if (lock()) {
            try {
                if (character.getCharacterStat().getAP() <= 0) {
                    Logger.logError("No ability point left");
                    closeSocket();
                    return;
                }
                int flag = packet.decodeInt();
                boolean up;
                switch (flag) {
                    case CharacterStatType.STR:
                        up = incSTR(1, true);
                        break;
                    case CharacterStatType.DEX:
                        up = incDEX(1, true);
                        break;
                    case CharacterStatType.INT:
                        up = incINT(1, true);
                        break;
                    case CharacterStatType.LUK:
                        up = incLUK(1, true);
                        break;
                    case CharacterStatType.MHP:
                    case CharacterStatType.MMP:
                        up = SkillAccessor.incMaxHPMP(character, basicStat, flag, false);
                        break;
                    default: {
                        Logger.logError("Incorrect AP-Up stat");
                        return;
                    }
                }
                if (up) {
                    flag |= CharacterStatType.AP;
                    character.getCharacterStat().setAP(character.getCharacterStat().getAP() - 1);
                    validateStat(false);
                }
                addCharacterDataMod(DBChar.Character);
                sendCharacterStat(Request.Excl, flag);
            } finally {
                unlock();
            }
        }
    }

    public void onAdmin(InPacket packet) {
        if (!isGM()) {
            return;
        }
        byte type = packet.decodeByte();

        if (lock()) {
            try {
                switch (type) {
                    case AdminRequest.Create: {// Create Item | /create
                        int itemID = packet.decodeInt();
                        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
                        if (ti < ItemType.Equip || ti > ItemType.Cash) {
                            return;
                        }

                        ItemSlotBase item = ItemInfo.getItemSlot(itemID, ItemVariationOption.Normal);
                        if (item != null) {
                            if (item.getType() == ItemSlotType.Bundle) {
                                item.setItemNumber(SkillInfo.getInstance().getBundleItemMaxPerSlot(itemID, character));
                            }
                            List<ChangeLog> changeLog = new ArrayList<>();
                            if (Inventory.rawAddItem(this, ti, item, changeLog, null)) {
                                Inventory.sendInventoryOperation(this, Request.None, changeLog);
                                addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                            }
                            changeLog.clear();
                        }
                        break;
                    }
                    case AdminRequest.Remove: {// Delete Inventory | /destroy
                        byte ti = packet.decodeByte();
                        if (ti < ItemType.Equip || ti > ItemType.Cash) {
                            return;
                        }
                        for (int pos = 1; pos <= character.getItemSlotCount(ti); pos++) {
                            ItemSlotBase item = character.getItem(ti, pos);
                            if (item != null) {
                                List<ChangeLog> changeLog = new ArrayList<>();
                                if (Inventory.rawRemoveItem(this, ti, (short) pos, item.getItemNumber(), changeLog, new Pointer<>(0), null)) {
                                    Inventory.sendInventoryOperation(this, Request.None, changeLog);
                                    addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                                }
                                changeLog.clear();
                            }
                        }
                        break;
                    }
                    case AdminRequest.IncEXP: {// /exp
                        int flag = incEXP(packet.decodeInt(), false);
                        if (flag != 0) {
                            sendCharacterStat(Request.None, flag);
                        }
                        break;
                    }
                    case AdminRequest.Ban: {// /ban
                        String characterName = packet.decodeString();
                        break;
                    }
                    case AdminRequest.Block: {// /block
                        String characterName = packet.decodeString();
                        int duration = packet.decodeInt();
                        break;
                    }
                    case AdminRequest.Portal: {// /pton | /ptoff
                        boolean enable = packet.decodeBool();
                        String portal = packet.decodeString();
                        getField().getPortal().enablePortal(portal, enable);
                        break;
                    }
                    case AdminRequest.NPCVar: {
                        byte action = packet.decodeByte();
                        String npc = packet.decodeString();
                        String var = packet.decodeString();
                        String val = "";
                        if (action == AdminRequest.NPCVar_Set) {// /varset
                            val = packet.decodeString();
                            getField().setNPCVariable(npc, var, val);
                        } else if (action == AdminRequest.NPCVar_Get) {// /varget
                            val = getField().getNPCVariable(npc, var);
                            if (val.isEmpty()) {
                                npc = "";
                                var = "";
                            }
                        }
                        sendPacket(FieldPacket.onAdminResult(AdminRequest.NPCVar, npc, var, val));
                        break;
                    }
                    case AdminRequest.BanishAll: {// /追放
                        packet.decodeInt();//*(this + 172)
                        break;
                    }
                    case AdminRequest.AdminEvent: {
                        // TODO: Implement OXQuiz
                        byte action = packet.decodeByte();
                        if (action == AdminRequest.Quiz) {// /問題
                            int problem = packet.decodeInt();
                            getField().broadcastPacket(FieldPacket.onQuiz(true, problem), false);
                        } else if (action == AdminRequest.Answer) {// /正解
                            //getField().broadcastPacket(FieldPacket.onQuiz(false, problem), false);
                        } else if (action == AdminRequest.Check) {// /採点
                        
                        }
                        break;
                    }
                    case AdminRequest.Timer: {
                        int duration = packet.decodeInt();
                        if (duration != 0) {// /tmset
                            getField().setTimerDuration(duration);
                        } else {// /tmon
                            getField().setTimerBegin();
                            getField().broadcastPacket(FieldPacket.onClock(getField().getTimerDuration()), false);
                        }
                        break;
                    }
                    case AdminRequest.Desc: {// /説明
                        getField().broadcastPacket(FieldPacket.onDesc(), true);
                        break;
                    }
                    default: {
                        Logger.logReport("New admin command found (%d)", type);
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void onAttack(byte type, InPacket packet) {
        if (secondaryStat.getStatOption(CharacterTemporaryStat.DarkSight) != 0) {
            int reset = secondaryStat.resetByCTS(CharacterTemporaryStat.DarkSight);
            sendTemporaryStatReset(reset);
            return;
        }

        //[01] 00 78 00 00 00 07 D1 02 CB FD 4C 02 D3 06 65 03
        byte attackInfo = packet.decodeByte();//nDamagePerMob | 16 * nMobCount
        byte damagePerMob = (byte) (attackInfo & 0xF);
        byte mobCount = (byte) ((attackInfo >>> 4) & 0xF);
        int skillID = packet.decodeInt();

        this.lastAttack = System.currentTimeMillis();
        int bulletItemID = 0;
        int mobTemplateID = 0;

        byte action = packet.decodeByte();//((_BYTE)bLeft << 7) | nAction & 0x7F
        byte left = (byte) ((action >> 7) & 1);
        byte speedDegree = packet.decodeByte();

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
            short bulletItemPos = 0;
            byte slv = 0;

            if (type == ClientPacket.UserShootAttack) {
                bulletItemPos = packet.decodeShort();
            }

            List<AttackInfo> attack = new ArrayList<>(mobCount);
            for (int i = 0; i < mobCount; i++) {
                AttackInfo info = new AttackInfo();
                info.mobID = packet.decodeInt();
                Mob mob = getField().getLifePool().getMob(info.mobID);
                if (mob != null) {
                    mobTemplateID = mob.getTemplateID();
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

            byte attackType;
            if (type == ClientPacket.UserMagicAttack) {
                attackType = 3;
            } else if (type == ClientPacket.UserShootAttack) {
                attackType = 2;
            } else if (type == ClientPacket.UserMeleeAttack) {
                attackType = 1;
            } else {
                Logger.logError("Attack Type Error %d", type);
                return;
            }

            SkillEntry skill = null;
            int maxCount = 1;
            int bulletCount = 1;
            if (skillID > 0) {
                if (lock()) {
                    try {
                        slv = (byte) SkillInfo.getInstance().getSkillLevel(character, skillID, null);
                        if (slv > 0) {
                            skill = SkillInfo.getInstance().getSkill(skillID);
                            if (skillID != Cleric.Heal && !SkillInfo.getInstance().adjustConsumeForActiveSkill(this, skillID, slv)) {
                                Logger.logReport("Failed to adjust consume for active skill!!! (SkillID:%d,SLV:%d)", skillID, slv);
                                getCalcDamage().skip();
                                return;
                            }
                            if (skill != null) {
                                SkillLevelData levelData = skill.getLevelData(slv);
                                if (levelData != null) {
                                    bulletCount = Math.max(1, levelData.getBulletCount());
                                    if (skillID == Hunter.ArrowBomb) {
                                        maxCount = 16;
                                    } else {
                                        // TODO: Check for all skills that can hit multiple mobs.
                                        maxCount = 16;
                                    }
                                }
                            }
                        }
                    } finally {
                        unlock();
                    }
                }
            }
            if (maxCount <= 1)
                maxCount = 1;
            if (maxCount < mobCount) {
                Logger.logError("Invalid mob count (Skill:%d,Lv:%d,Mob:%d)", skillID, slv, mobTemplateID);
            } else {
                if (bulletItemPos > 0) {
                    List<ChangeLog> changeLog = new ArrayList<>();
                    ItemSlotBase item = character.getItem(ItemType.Consume, bulletItemPos);
                    if (item != null) {
                        if (character.getItemTrading().get(ItemType.Consume).get(bulletItemPos) != 0 || item.getItemNumber() < bulletCount) {
                            getCalcDamage().skip();
                            return;
                        }
                        if (ItemAccessor.isJavelinItem(item.getItemID())) {
                            if (Inventory.rawWasteItem(this, bulletItemPos, (short) bulletCount, changeLog)) {
                                addCharacterDataMod(DBChar.ItemSlotConsume);
                                bulletItemID = item.getItemID();
                                Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            }
                        } else if (secondaryStat.getStatOption(CharacterTemporaryStat.SoulArrow) == 0) {
                            Pointer<Integer> decRet = new Pointer<>(0);
                            if (Inventory.rawRemoveItem(this, ItemType.Consume, bulletItemPos, (short) bulletCount, changeLog, decRet, null) && decRet.get() == bulletCount) {
                                addCharacterDataMod(DBChar.ItemSlotConsume);
                                bulletItemID = item.getItemID();
                                Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            } else {
                                Logger.logError("Invalid skill info in attack packet (nItemPos: %d, nCount: %d, nDecRet: %d)", bulletItemPos, bulletCount, decRet.get());
                            }
                        }
                        changeLog.clear();
                    }
                }
                if (getField().getLifePool().onUserAttack(this, type, attackType, mobCount, damagePerMob, skill, slv, action, left, speedDegree, bulletItemID, attack, ballStart)) {

                }
            }
        }
    }

    public void onBroadcastMsg(InPacket packet) {
        if (isGM() && getField() != null) {
            byte bmType = packet.decodeByte();
            String msg = packet.decodeString();

            getChannel().broadcast(WvsContext.onBroadcastMsg(bmType, msg));
        }
    }

    public void onChat(InPacket packet) {
        if (getField() == null) {
            return;
        }
        String text = packet.decodeString();

        if (text.charAt(0) == '!' || text.charAt(0) == '@') {
            CommandHandler.handle(this, text);
            return;
        }

        getField().splitSendPacket(getSplit(), UserCommon.onChat(characterID, text), null);
    }

    public void onCharacterInfoRequest(InPacket packet) {
        User target = getField().findUser(packet.decodeInt());
        if (target == null || target.isGM()) {
            sendCharacterStat(Request.Excl, 0);
        } else {
            if (target.lock()) {
                try {
                    sendPacket(WvsContext.onCharacterInfo(target));
                } finally {
                    target.unlock();
                }
            }
        }
    }

    public void onChangeSlotPositionRequest(InPacket packet) {
        byte type = packet.decodeByte();
        short oldPos = packet.decodeShort();
        short newPos = packet.decodeShort();
        short count = packet.decodeShort();
        Inventory.changeSlotPosition(this, Request.Excl, type, oldPos, newPos, count);
    }

    public void onChangeStatRequest(InPacket packet) {
        if (getField() == null) {
            return;
        }
        short hp = 0;
        short mp = 0;
        int flag = packet.decodeInt();
        if ((flag & CharacterStatType.HP) != 0) {
            hp = packet.decodeShort();
        }
        if ((flag & CharacterStatType.MP) != 0) {
            mp = packet.decodeShort();
        }
        byte option = packet.decodeByte();
        double recoveryRate = getField().getRecoveryRate();
        if (lock()) {
            try {
                if (getHP() == 0) {
                    return;
                }
                long time = System.currentTimeMillis();
                if (hp > 0) {
                    int restForHPDuration = 10000;
                    if (JobAccessor.getJobCategory(character.getCharacterStat().getJob()) == JobCategory.Fighter
                            && SkillInfo.getInstance().getSkillLevel(character, Warrior.ImproveBasic) > 0) {
                        restForHPDuration = 5000;
                    }
                    if ((option & 1) != 0) {
                        restForHPDuration = SkillAccessor.getEndureDuration(character);
                    }
                    if ((time - this.lastCharacterHPInc) < restForHPDuration - 2000) {
                        ++illegalHPIncTime;
                    }
                    if (illegalHPIncTime > 9) {
                        Logger.logError("Illegal HP recovery time : %d", characterID);
                        return;
                    }
                    int recoveryHP = (int) ((double) (SkillAccessor.getHPRecoveryUpgrade(character) + 10.0d) * recoveryRate);
                    if (recoveryHP < hp) {
                        ++illegalHPIncSize;
                    }
                    if (illegalHPIncSize > 9) {
                        Logger.logError("Illegal HP recovery size : %d", characterID);
                        return;
                    }
                    incHP(recoveryHP, false);
                    lastCharacterHPInc = time;
                }
                if (mp > 0) {
                    if ((time - lastCharacterMPInc) < 8000) {
                        ++illegalMPIncTime;
                    }
                    if (illegalMPIncTime > 7) {
                        Logger.logError("Illegal MP recovery time : %d", characterID);
                        return;
                    }
                    int recoveryMP = (int) ((double) (SkillAccessor.getMPRecoveryUpgrade(character) + 3.0d) * recoveryRate);
                    if (recoveryMP < mp) {
                        ++illegalMPIncSize;
                    }
                    if (illegalMPIncSize > 7) {
                        Logger.logError("Illegal MP recovery size : %d (nMP : %d)", recoveryMP, mp);
                        return;
                    }
                    incMP(recoveryMP, false);
                    lastCharacterMPInc = time;
                }
                sendCharacterStat(Request.None, flag);
            } finally {
                unlock();
            }
        }
    }

    public void onConsumeCashItemUseRequest(InPacket packet) {
        short pos = packet.decodeShort();
        int itemID = packet.decodeInt();
        String message = packet.decodeString();

        List<ChangeLog> changeLog = new ArrayList<>();
        Pointer<Integer> decRet = new Pointer<>(0);
        if (Inventory.rawRemoveItem(this, ItemType.Consume, pos, (short) 1, changeLog, decRet, null) && decRet.get() == 1) {
            if (ItemAccessor.isWeatherItem(itemID)) {
                getField().onWeather(itemID, message, 8000);
            }
            Inventory.sendInventoryOperation(this, Request.Excl, changeLog);
        } else {
            sendCharacterStat(Request.Excl, 0);
        }
        changeLog.clear();
    }

    public void onUpgradeItemRequest(InPacket packet) {
        Inventory.upgradeEquip(this, packet.decodeShort(), packet.decodeShort());
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
                    reward.setType(RewardType.MONEY);
                    reward.setPeriod(0);
                    int x = getCurrentPosition().x;
                    int y1 = getCurrentPosition().y;
                    getField().getDropPool().create(reward, this.characterID, 0, x, y1, x, y2.get(), 0, false, 0);
                }
            }
        }
    }

    public void onGivePopularityRequest(InPacket packet) {
        int targetID = packet.decodeInt();
        User target = getField().findUser(targetID);
        if (target == null || target == this) {
            sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.InvalidCharacterID, null, false));
        } else {
            if (character.getCharacterStat().getLevel() < 15) {
                sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.LevelLow, null, false));
            } else {
                boolean incFame = packet.decodeBool();

                byte ret = GameDB.rawCheckGivePopularity(characterID, targetID);
                if (ret == GivePopularityRes.Success) {
                    target.incPOP(incFame ? 1 : -1, true);
                    target.sendCharacterStat(Request.None, CharacterStatType.POP);
                    target.sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.Notify, getCharacterName(), incFame));

                    sendPacket(WvsContext.onGivePopularityResult(GivePopularityRes.Success, target.getCharacterName(), incFame));
                } else {
                    sendPacket(WvsContext.onGivePopularityResult(ret, null, false));
                }
            }
        }
    }

    public void onHit(InPacket packet) {
        // nexon japan debug code be like
        int hitStart = packet.decodeInt();
        if (hitStart != 0x123400) {
            return;
        }
        byte mobAttackIdx = packet.decodeByte();
        int obstacleData = 0;
        int clientDamage = 0;
        int mobTemplateID = 0;
        byte left = 0;
        byte reflect = 0;
        int mobID = 0;
        byte hitAction = 0;
        int damage = 0;
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
        // nexon japan debug code be like
        int hitEnd = packet.decodeInt();
        if (hitEnd != 0xABCD00) {
            return;
        }
        if (getField() == null) {
            return;
        }
        if (lock()) {
            try {
                if (getHP() > 0) {
                    if (clientDamage > 0) {
                        if (mobAttackIdx > AttackIndex.Counter) {
                            this.invalidDamageMissCount = 0;
                        }
                        int mp = 0;
                        int magicGuard = secondaryStat.getStatOption(CharacterTemporaryStat.MagicGuard);
                        if (magicGuard > 0) {
                            int inc = (int) (clientDamage * (double) magicGuard / 100.0d);
                            if (character.getCharacterStat().getMP() < inc) {
                                inc = character.getCharacterStat().getMP();
                            }
                            clientDamage -= inc;
                            mp = inc;
                        }
                        MobTemplate template = MobTemplate.getMobTemplate(mobTemplateID);
                        if (template != null) {
                            MobAttackInfo info = null;
                            if (mobAttackIdx >= AttackIndex.Mob_Physical && !template.getAttackInfo().isEmpty() && mobAttackIdx < template.getAttackInfo().size()) {
                                info = template.getAttackInfo().get(mobAttackIdx);
                            }
                            if (info != null) {
                                // deadlyAttack
                                // mpBurn
                            }
                            int powerGuard = secondaryStat.getStatOption(CharacterTemporaryStat.PowerGuard);
                            if (reflect != 0 && powerGuard != 0) {
                                if (powerGuard < reflect) {
                                    reflect = (byte) powerGuard;
                                }
                                int hpDealt = clientDamage;
                                if (hpDealt >= getHP()) {
                                    hpDealt = getHP();
                                }
                                damage = Math.min(reflect * hpDealt / 100, (int) (template.getMaxHP() / 10.0d));
                                if (template.isBoss()) {
                                    damage /= 2;
                                }
                                if (damage > 0) {
                                    // if fixedDamage > 0
                                    //  damage = fixedDamage
                                    // if invincible
                                    //  damage = 0
                                }
                                clientDamage -= damage;
                            }
                        }
                        incHP(-clientDamage, false);
                        int flag = CharacterStatType.HP;
                        if (mp != 0) {
                            incMP(-mp, false);
                            flag |= CharacterStatType.MP;
                        }
                        if (flag != 0) {
                            sendCharacterStat(Request.None, flag);
                        }
                    }
                    if (getHP() == 0) {
                        // This is where Nexon handles onUserDead, we call it in incHP.
                    } else {
                        // TODO: Look into this. ObstacleData might be ObstacleDamage.
                        if (clientDamage > 0) {
                            if (obstacleData > 0) {
                                int slv = obstacleData & 0xFF;
                                int skillID = (obstacleData >> 8) & 0xFF;

                                // Can't continue because MobSkills don't exist yet?
                                // Unless they do, but aren't in Skill.wz? o.O
                            } else if (mobTemplateID != 0) {
                                //onStatChangedByMobAttack(mobTemplateID, mobAttackIdx);
                            }
                        }
                    }
                }
            } finally {
                unlock();
            }
        }
        getField().splitSendPacket(getSplit(), UserRemote.onHit(this.characterID, mobAttackIdx, clientDamage, mobTemplateID, left, reflect, mobID, hitAction, hit), this);
        if (damage != 0) {
            getField().getLifePool().onUserAttack(this, mobID, damage, hit, (short) 100);
        }
    }

    public void onMigrateToCashShopRequest() {
        if (getField() == null || getSocket() == null || this.nexonClubID == null || this.nexonClubID.isEmpty()) {
            return;
        }

        if (lock()) {
            try {
                if (canAttachAdditionalProcess()) {
                    this.onTransferField = true;

                    OutPacket packet = new OutPacket(CenterPacket.ShopMigrateReq);
                    packet.encodeInt(getCharacterID());
                    packet.encodeByte(getWorldID());
                    packet.encodeByte(getChannelID());

                    getChannel().getCenter().sendPacket(packet);
                }
            } finally {
                unlock();
            }
        }
    }

    public void onPortalScrollUseRequest(InPacket packet) {
        if (getField() == null) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        if (lock()) {
            try {
                short pos = packet.decodeShort();
                int itemID = packet.decodeInt();
                ItemSlotBase item = character.getItem(ItemType.Consume, pos);
                PortalScrollItem info = ItemInfo.getPortalScrollItem(itemID);
                if (item == null || item.getItemID() != itemID || info == null || info.getItemID() != itemID) {
                    Logger.logError("Incorrect portal-scroll-item use request nPOS(%d), nItemID(%d), pInfo(%p)", pos, itemID);
                    //Logger.logError("Packet Dump: %s", packet.dumpString());
                    closeSocket();
                    return;
                }
                int fieldID = info.getMoveTo();
                if (fieldID == -1) {
                    fieldID = getField().getReturnFieldID();
                } else {
                    if ((getField().getOption() & FieldOpt.PortalScrollLimit) != 0) {
                        sendCharacterStat(Request.Excl, 0);
                        return;
                    }
                }
                if (!canAttachAdditionalProcess() || fieldID == Field.Invalid || fieldID == getField().getFieldID()) {
                    sendCharacterStat(Request.Excl, 0);
                } else {
                    if (FieldMan.getInstance(getChannelID()).isConnected(getField().getFieldID(), fieldID)) {
                        List<ChangeLog> changeLog = new ArrayList<>();
                        Pointer<Integer> decRet = new Pointer<>(0);
                        if (Inventory.rawRemoveItem(this, ItemType.Consume, pos, (short) 1, changeLog, decRet, null) && decRet.get() == 1) {
                            Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            addCharacterDataMod(DBChar.ItemSlotConsume);
                            postTransferField(fieldID, "", false);
                            sendCharacterStat(Request.Excl, 0);
                            changeLog.clear();
                            return;
                        }
                        Logger.logError("Incorrect portal-scroll-item use request nPOS(%d), nItemID(%d), pInfo(%p)", pos, itemID);
                        //Logger.logError("Packet Dump: %s", packet.dumpString());
                        closeSocket();
                        changeLog.clear();
                    } else {
                        sendPacket(FieldPacket.onTransferFieldReqIgnored(TransferField.NotConnectedArea));
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void onStatChangeItemUseRequest(InPacket packet) {
        if (getHP() == 0 || getField() == null || secondaryStat.getStatOption(CharacterTemporaryStat.DarkSight) != 0) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        short pos = packet.decodeShort();
        int itemID = packet.decodeInt();
        ItemSlotBase item = character.getItem(ItemType.Consume, pos);
        StateChangeItem sci = ItemInfo.getStateChangeItem(itemID);
        if (item == null || item.getItemID() != itemID || sci == null) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        List<ChangeLog> changeLog = new ArrayList<>();
        Pointer<Integer> decRet = new Pointer<>(0);
        if (!InventoryManipulator.rawRemoveItem(character, ItemType.Consume, pos, 1, changeLog, decRet, new Pointer<>()) || decRet.get() != 1) {
            changeLog.clear();
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        sendPacket(InventoryManipulator.makeInventoryOperation(Request.None, changeLog));
    
        int flag = sci.getInfo().apply(this, sci.getItemID(), character, basicStat, secondaryStat, System.currentTimeMillis(), false);
        addCharacterDataMod(DBChar.ItemSlotConsume | DBChar.Character);
        sendCharacterStat(Request.Excl, sci.getInfo().getFlag());
        sendTemporaryStatSet(flag);
    }

    public void onScriptMessageAnswer(InPacket packet) {
        if (lock()) {
            try {
                if (runningVM != null) {
                    runningVM.getScriptSys().onScriptMessageAnswer(this, packet);
                }
            } finally {
                unlock();
            }
        }
    }

    public void onSelectNpc(InPacket packet) {
        if (getField() != null) {
            long time = System.currentTimeMillis();
            if (lastSelectNPCTime == 0 || time <= lastSelectNPCTime || lastSelectNPCTime <= time - 500) {
                this.lastSelectNPCTime = time;

                Npc npc = getField().getLifePool().getNpc(packet.decodeInt());
                if (npc != null) {
                    if (npc.getNpcTemplate().getQuest() != null && !npc.getNpcTemplate().getQuest().isEmpty()) {
                        ScriptVM script = new ScriptVM();
                        if (script.setScript(this, npc.getNpcTemplate().getQuest(), npc)) {
                            script.run(this);
                        }
                    } else {
                        if (lock()) {
                            try {
                                if (!canAttachAdditionalProcess()) {
                                    return;
                                }

                                if (!npc.getNpcTemplate().getShopItem().isEmpty()) {
                                    this.tradingNpc = npc;
                                    sendPacket(ShopDlg.onOpenShopDlg(this, npc.getNpcTemplate()));
                                }
                            } finally {
                                unlock();
                            }
                        }
                    }
                }
            }
        }
    }

    public void onShopRequest(InPacket packet) {
        if (lock(1000)) {
            try {
                if (tradingNpc == null) {
                    return;
                }
                Npc npc = tradingNpc;
                NpcTemplate npcTemplate = npc.getNpcTemplate();
                byte mode = packet.decodeByte();
                switch (mode) {
                    case ShopResCode.Buy: {
                        short pos = packet.decodeShort();
                        int itemID = packet.decodeInt();
                        short count = packet.decodeShort();
                        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
                        ShopItem shopItem = null;
                        if (pos < 0 || npcTemplate.getShopItem().isEmpty() || pos >= npcTemplate.getShopItem().size()
                                || (shopItem = npcTemplate.getShopItem().get(pos)).itemID != itemID || count <= 0
                                || (!ItemAccessor.isBundleTypeIndex(ti)
                                || ItemAccessor.isRechargeableItem(itemID)) && count != 1) {
                            Logger.logError("Incorrect shop request");
                            //Logger.logError("Packet Dump: %s", packet.dumpString());
                            closeSocket();
                            return;
                        }
                        int price = shopItem.price;
                        int stockPrice = count * price;
                        if (character.getCharacterStat().getLevel() <= 15) {
                            //checkTradeLimitTime();
                            if (stockPrice + getTradeMoneyLimit() > 1000000) {
                                return;
                            }
                            setTempTradeMoney(stockPrice);
                        }
                        if (stockPrice <= 0 || character.getCharacterStat().getMoney() - character.getMoneyTrading() < stockPrice) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown2));
                        } else {
                            if (npc.decShopItemCount(shopItem.itemID, count)) {
                                List<ExchangeElem> exchange = new ArrayList<>();
                                ExchangeElem exchangeElem = new ExchangeElem();
                                ItemSlotBase item = ItemInfo.getItemSlot(shopItem.itemID, ItemVariationOption.None);
                                if (item == null) {
                                    npc.incShopItemCount(shopItem.itemID, count);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                    return;
                                }
                                if (ItemAccessor.isRechargeableItem(shopItem.itemID)) {
                                    int number = SkillInfo.getInstance().getBundleItemMaxPerSlot(shopItem.itemID, character);
                                    item.setItemNumber(number);
                                } else {
                                    //I believe this is actually pShopItem.nQuantity
                                    int number = item.getItemNumber();// + pShopItem.
                                    if (number <= 1)
                                        number = count;
                                    item.setItemNumber(number);
                                }
                                exchangeElem.initAdd((short) 0, (short) 0, item);
                                exchange.add(exchangeElem);
                                if (!Inventory.exchange(this, -stockPrice, exchange, null, null)) {
                                    npc.incShopItemCount(shopItem.itemID, count);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                    return;
                                }
                                if (character.getCharacterStat().getLevel() <= 15) {
                                    setTradeMoneyLimit(getTradeMoneyLimit() + getTempTradeMoney());
                                }
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Success));//BuySuccess
                            } else {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.NoStock));//BuyNoStock
                            }
                        }
                        break;
                    }
                    case ShopResCode.Sell: {
                        short pos = packet.decodeShort();
                        int itemID = packet.decodeInt();
                        short count = packet.decodeShort();
                        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
                        if (pos <= 0 || count <= 0 || ti <= ItemType.NotDefine || ti >= ItemType.NO
                                || (!ItemAccessor.isBundleTypeIndex(ti) || ItemAccessor.isRechargeableItem(itemID)) && count != 1) {
                            Logger.logError("Incorrect shop request");
                            //Logger.logError("Packet Dump: %s", packet.dumpString());
                            closeSocket();
                            return;
                        }
                        ItemSlotBase item = character.getItem(ti, pos);
                        if (item == null || item.getItemID() != itemID) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown3));
                            return;
                        } else if (ItemInfo.isCashItem(item.getItemID()) || item.isCashItem()) {
                            Logger.logError("Selling Invalid Item in Shop [%s] (%d,%d:%d)", characterName, pos, itemID, count);
                            closeSocket();
                            return;
                        }
                        int inc;
                        long uInc = 0;
                        if (ti == ItemType.Equip) {
                            EquipItem pInfo = ItemInfo.getEquipItem(itemID);
                            if (pInfo == null) {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                return;
                            }
                            inc = pInfo.getSellPrice();
                        } else {
                            BundleItem info = ItemInfo.getBundleItem(item.getItemID());
                            if (info == null) {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                return;
                            }
                            if (ItemAccessor.isRechargeableItem(itemID)) {
                                int number = item.getItemNumber();
                                double unitPrice = Math.ceil((double) number * info.getUnitPrice());
                                uInc = (long) (info.getSellPrice() + (long) unitPrice) >> 32;
                                inc = (int) (info.getSellPrice() + (long) unitPrice);
                            } else {
                                inc = count * info.getSellPrice();
                                uInc = (info.getSellPrice() + (long) count) >> 32;
                            }
                        }
                        int money = inc + character.getCharacterStat().getMoney();
                        if (money > 0 && uInc == 0 && inc >= 0) {
                            List<ChangeLog> changeLog = new ArrayList<>();
                            Pointer<Integer> decRet = new Pointer<>(0);
                            ItemSlotBase itemRemoved = item.makeClone();
                            if (!InventoryManipulator.rawRemoveItem(character, ti, pos, count, changeLog, decRet, null) || decRet.get() < count) {
                                character.setItem(ti, pos, itemRemoved);
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown2));
                                return;
                            }
                            addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                            Inventory.sendInventoryOperation(this, Request.None, changeLog);
                            incMoney(inc, false, true);
                            sendCharacterStat(Request.None, CharacterStatType.Money);
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Success));//BuySuccess
                            changeLog.clear();
                        }
                        break;
                    }
                    case ShopResCode.Recharge: {
                        short pos = packet.decodeShort();
                        if (pos <= 0) {
                            Logger.logError("Incorrect shop request nPOS(%d)", pos);
                            //Logger.LogError("Packet Dump: %s", packet.DumpString());
                            closeSocket();
                            return;
                        }
                        ItemSlotBase item = character.getItem(ItemType.Consume, pos);
                        if (item == null || !ItemAccessor.isRechargeableItem(item.getItemID())) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown3));
                            return;
                        }
                        int maxPerSlot = SkillInfo.getInstance().getBundleItemMaxPerSlot(item.getItemID(), character);
                        int count = maxPerSlot - item.getItemNumber();
                        double price = npc.getShopRechargePrice(item.getItemID());
                        if (count <= 0 || price <= 0.0) {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown3));
                            return;
                        }
                        if (npc.decShopItemCount(item.getItemID(), count)) {
                            double inc = Math.ceil((double) count * price);
                            if (((long) inc >> 32) != 0 || inc <= 0 || character.getCharacterStat().getMoney() - character.getMoneyTrading() < inc) {
                                sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                            } else {
                                List<ChangeLog> changeLog = new ArrayList<>();
                                if (Inventory.rawRechargeItem(this, pos, changeLog)) {
                                    addCharacterDataMod(DBChar.ItemSlotConsume);
                                    Inventory.sendInventoryOperation(this, Request.None, changeLog);
                                    incMoney(-(int)inc, false, true);
                                    sendCharacterStat(Request.None, CharacterStatType.Money);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Success));//RechargeSuccess
                                } else {
                                    npc.incShopItemCount(item.getItemID(), count);
                                    sendPacket(ShopDlg.onShopResult(ShopResCode.Unknown4));
                                }
                                changeLog.clear();
                            }
                        } else {
                            sendPacket(ShopDlg.onShopResult(ShopResCode.NoStock));//RechargeNoStock
                        }
                        break;
                    }
                    case ShopResCode.Close: {
                        this.tradingNpc = null;
                        break;
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void onTransferFieldRequest(InPacket packet) {
        if (getField() == null || socket == null) {
            closeSocket();
            return;
        }
        postTransferField(null, null, false, null, false, packet);
    }

    public void onMove(InPacket packet) {
        if (getField() != null) {
            Rect move = new Rect();
            getField().onUserMove(this, packet, move);
        }
    }

    public void onWhisper(InPacket packet) {
        byte flag = packet.decodeByte();
        String target = packet.decodeString();
        if (flag == WhisperFlags.ReplyRequest) {
            String text = packet.decodeString();

            User user = getChannel().findUserByName(target, true);
            boolean success = false;
            if (user != null && user.getField() != null && user.getField().getFieldID() != 0) {
                user.sendPacket(FieldPacket.onWhisper(WhisperFlags.ReplyReceive, null, getCharacterName(), text, LocationResult.None, -1, false));

                target = user.getCharacterName();
                success = true;
            }
            sendPacket(FieldPacket.onWhisper(WhisperFlags.ReplyResult, target, null, null, LocationResult.None, -1, success));
        } else if (flag == WhisperFlags.FindRequest) {
            User user = getChannel().findUserByName(target, true);
            byte location = LocationResult.None;
            int fieldID = Field.Invalid;
            if (user != null && user.getField() != null) {
                if (user.isGM()) {
                    location = LocationResult.Admin;
                } else {
                    location = LocationResult.GameSvr;
                    fieldID = user.getField().getFieldID();
                }
                target = user.getCharacterName();
            } else {
                // TODO: Check Shop server and other channels.
            }
            sendPacket(FieldPacket.onWhisper(WhisperFlags.FindResult, target, null, null, location, fieldID, false));
        } else if (flag == WhisperFlags.BlockedResult) {
            sendPacket(FieldPacket.onWhisper(WhisperFlags.BlockedResult, target, null, null, LocationResult.None, -1, false));
        }
    }

    public void onTransferField(Field field, int x, int y, byte portal) {
        getField().onLeave(this);
        if (lock()) {
            try {
                setField(field);
                setPosMap(field.getFieldID());
                setPortal((byte) (portal & 0x7F));
                setMovePosition(x, y, (byte) 0, (short) 0);
                addCharacterDataMod(DBChar.Character);
                avatarModFlag = 0;
            } finally {
                unlock();
            }
        }
        sendSetFieldPacket(false);
        if (getField().onEnter(this)) {
            this.attackCheckIgnoreCnt = 3;
            this.onTransferField = false;
            //this.attackTimeCheckAlert = 0;
        } else {
            Logger.logError("Failed in entering field");
            closeSocket();
        }
    }

    public void onTransferField(Field field, Portal portal) {
        if (lock()) {
            try {
                if (field != null) {
                    if (!field.getPortal().getPortal().contains(portal)) {
                        portal = field.getPortal().getPortal(0);
                    }
                    onTransferField(field, portal.getPortalPos().x, portal.getPortalPos().y, portal.getPortalIdx());
                }
            } finally {
                unlock();
            }
        }
    }

    public void postTransferField(int fieldID, String portal, boolean force) {
        postTransferField(fieldID, portal, force, new Point(0, 0), true, null);
    }

    public void postTransferField(Integer fieldID, String portal, boolean force, Point pos, boolean loopback, InPacket packet) {
        if (lock()) {
            try {
                if (!onTransferField || force) {
                    if (fieldID != null && FieldMan.getInstance(getChannelID()).isBlockedMap(fieldID)) {
                        Logger.logError("User tried to enter the Blocked Map #3 (From:%d,To:%d)", getField().getFieldID(), fieldID);
                        sendPacket(FieldPacket.onTransferFieldReqIgnored(TransferField.DisabledPortal));
                    } else {
                        if (getField() == null || socket == null) {
                            closeSocket();
                            return;
                        }
                        if (pos == null) {
                            pos = new Point(0, 0);
                        }
                        if (!loopback) {
                            fieldID = packet.decodeInt();
                            portal = packet.decodeString();
                        }
                        boolean isDead = false;
                        Portal pt = getField().getPortal().findPortal(portal);
                        if (getHP() == 0) {
                            if (getField().getForcedReturnFieldID() != Field.Invalid) {
                                fieldID = getField().getReturnFieldID();
                            } else {
                                fieldID = getField().getFieldID();
                            }
                            portal = "";
                            character.getCharacterStat().setHP(50);
                            basicStat.setFrom(character, character.getEquipped(), character.getEquipped2(), 0, 0);
                            secondaryStat.clear();
                            secondaryStat.setFrom(basicStat, character.getEquipped(), character);
                            validateStat(false);
                            addCharacterDataMod(DBChar.Character);
                            isDead = true;
                        }
                        if (fieldID != -1 && !loopback && !isDead && !isGM()) {
                            sendPacket(FieldPacket.onTransferFieldReqIgnored(TransferField.Done));
                            return;
                        }
                        if (!isDead && !loopback && !canAttachAdditionalProcess()) {
                            sendPacket(FieldPacket.onTransferFieldReqIgnored(TransferField.Done));
                            return;
                        }
                        if (fieldID == -1 && (portal == null || portal.isEmpty())) {
                            if (!loopback) {
                                sendPacket(FieldPacket.onTransferFieldReqIgnored(TransferField.Done));
                            }
                            return;
                        }
                        this.onTransferField = true;
                        if (fieldID == -1) {
                            pt = getField().getPortal().findPortal(portal);
                            if (pt == null || pt.tmap == Field.Invalid) {
                                Logger.logError("Incorrect portal");
                                closeSocket();
                                return;
                            }
                            if (!pt.enable) {
                                sendPacket(FieldPacket.onTransferFieldReqIgnored(TransferField.DisabledPortal));
                                this.onTransferField = false;
                                return;
                            }
                            fieldID = pt.tmap;
                            portal = pt.tname;
                            if (fieldID == getField().getFieldID()) {
                                this.onTransferField = false;
                                return;
                            }
                        }
                        Field field = FieldMan.getInstance(getChannelID()).getField(fieldID, false);
                        if (field == null) {
                            Logger.logError("Invalid Field ID *1* : %d (Old: %d, IsDead: %b)", fieldID, getField().getFieldID(), isDead);
                            closeSocket();
                            return;
                        }
                        if (FieldMan.getInstance(getChannelID()).isBlockedMap(fieldID)) {
                            Logger.logError("User tried to enter the Blocked Map #1 (From:%d,To:%d)", getField().getFieldID(), fieldID);
                            sendPacket(FieldPacket.onTransferFieldReqIgnored(TransferField.DisabledPortal));
                            this.onTransferField = false;
                            return;
                        }
                        if (portal == null || portal.isEmpty() || (pt = field.getPortal().findPortal(portal)) == null) {
                            if (pos.x == 0 && pos.y == 0) {
                                pt = field.getPortal().getRandStartPoint();
                            } else {
                                pt = field.getPortal().findCloseStartPoint(pos.x, pos.y);
                            }
                        }
                        pos.x = pt.getPortalPos().x;
                        pos.y = pt.getPortalPos().y;
                        onTransferField(field, pos.x, pos.y, pt.getPortalIdx());
                    }
                }
            } finally {
                unlock();
            }
        }
    }

    public void sendCharacterStat(byte request, int flag) {
        lock.lock();
        try {
            character.getCharacterStat().setMoney(character.getCharacterStat().getMoney() - character.getMoneyTrading());
            sendPacket(WvsContext.onStatChanged(request, character.getCharacterStat(), flag));
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
                Inventory.sendInventoryOperation(this, onExclRequest, null);
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
                    byte ti = ItemAccessor.getItemTypeIndexFromID(item.getItemID());
                    if (InventoryManipulator.rawAddItem(character, ti, item, changeLog, incRet)) {
                        pr.getItem().setItemNumber(pr.getItem().getItemNumber() - incRet.get());//-= nIncRet

                        if (ItemAccessor.isTreatSingly(pr.getItem()) || pr.getItem().getItemNumber() <= 0) {
                            pickUp = true;
                        }
                        characterDataModFlag |= ItemAccessor.getItemTypeFromTypeIndex(ti);
                        //if (ItemAccessor.isJavelinItem(pr.getItem().getItemID()) && incRet.Get() == 0)
                        //    consumeOnPickup = true;
                        if (pr.getSourceID() == 0 /*&& pr.getOwnType() == Drop.UserOwn*/) {
                            User user = getChannel().findUser(pr.getOwnerID());
                            if (user != null) {
                                user.flushCharacterData(0, true);
                            }
                        }
                    }
                }
                Inventory.sendInventoryOperation(this, onExclRequest, changeLog);
                changeLog.clear();
            }
            if (pr.isMoney() || incRet.get() > 0) {
                sendPacket(WvsContext.onDropPickUpMessage(pr.isMoney() ? DropPickup.Messo : DropPickup.AddInventoryItem, pr.getDropInfo(), pr.getItem() != null ? pr.getItem().getItemID() : 0, incRet.get()));
            } else {
                sendPacket(WvsContext.onDropPickUpMessage(DropPickup.Done, 0, 0, 0));
            }
            return pickUp;
        } finally {
            lock.unlock();
        }
    }

    public void sendDropPickUpFailPacket(byte onExclRequest) {
        sendCharacterStat(onExclRequest, 0);
        sendPacket(WvsContext.onDropPickUpMessage(DropPickup.Done, 0, 0, 0));
    }

    public void sendSetFieldPacket(boolean characterData) {
        curFieldKey++;
        if (characterData) {
            int s1 = Rand32.getInstance().random().intValue();
            int s2 = Rand32.getInstance().random().intValue();
            int s3 = Rand32.getInstance().random().intValue();
            calcDamage.setSeed(s1, s2, s3);
            sendPacket(Stage.onSetField(this, true, s1, s2, s3));
        } else {
            sendPacket(Stage.onSetField(this, false, -1, -1, -1));
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

    public void sendSystemMessage(String msg) {
        sendPacket(WvsContext.onBroadcastMsg(BroadcastMsg.Notice, msg));
    }

    public void setCommunity(String name) {
        this.community = name;
    }

    public void setGradeCode(int grade) {
        this.gradeCode = grade;
    }

    public void setMiniRoom(MiniRoomBase miniRoom) {
        this.miniRoom = miniRoom;
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

    public boolean isItemExist(byte ti, int itemID) {
        boolean exist = InventoryManipulator.isItemExist(character, ti, itemID);
        if (!exist) {
            if (ti == ItemType.Equip) {
                Pointer<Integer> bodyPart = new Pointer<>(0);
                ItemAccessor.getBodyPartFromItem(itemID, character.getCharacterStat().getGender(), bodyPart, false);
                ItemSlotBase item = character.getItem(ti, -bodyPart.get());
                if (item != null) {
                    if (item.getItemID() == itemID)
                        exist = true;
                }
            }
        }
        return exist;
    }

    private void onUserDead() {
        sendTemporaryStatReset(secondaryStat.reset());

        ExpAccessor.decreaseExp(character, basicStat, getField().getLifePool().getMobGenCount() <= 0 || getField().isTown());
        sendCharacterStat(Request.None, CharacterStatType.EXP);
    }

    public void onLevelUp() {
        onUserEffect(true, true, UserEffect.LevelUp);
    }

    public void setMaxLevelReach() {
        if (!isGM()) {
            String notice = String.format("[Congrats] %s has reached Level 200! Congratulate %s on such an amazing achievement!", characterName, characterName);
            getChannel().broadcast(WvsContext.onBroadcastMsg(BroadcastMsg.Notice, notice));
        }
    }
    
    public byte tryChangeHairOrFace(int couponItemID, int param) {
        if (ItemAccessor.getItemTypeIndexFromID(couponItemID) != ItemType.Etc) {
            return -1;
        }
        boolean valid = false;
        byte type = (byte) (couponItemID / 1000 % 10);
        switch (type) {
            case 0://Hair
                valid = (param != character.getCharacterStat().getHair());
                //valid &= ItemInfo.isValidHairID(param);
                break;
            case 1://Hair Color(?)
                valid = (param != character.getCharacterStat().getHair());
                //valid &= ItemInfo.isValidHairID(param);
                break;
            case 2://Face
                valid = (param != character.getCharacterStat().getFace());
                //valid &= ItemInfo.isValidFaceID(param);
                break;
        }
        if (!valid) {
            return -3;
        }
        ItemSlotBase item;
        int slotCount = character.getItemSlotCount(ItemType.Etc);
        for (int pos = 1; pos <= slotCount; pos++) {
            item = character.getItem(ItemType.Etc, pos);
            if (item != null && item.getItemID() == couponItemID && item.isCashItem() && character.getItemTrading().get(ItemType.Etc).get(pos) == 0) {
                Pointer<Integer> decRet = new Pointer<>(0);
                List<ChangeLog> changeLog = new ArrayList<>();
                if (Inventory.rawRemoveItem(this, ItemType.Etc, (short) pos, (short) 1, changeLog, decRet, null) && decRet.get() == 1) {
                    addCharacterDataMod(DBChar.ItemSlotEtc);
                    Inventory.sendInventoryOperation(this, Request.None, changeLog);
                    if (type == 0 || type == 1) {
                        setHair(param);
                        sendCharacterStat(Request.None, CharacterStatType.Hair);
	                    postAvatarModified(AvatarLook.Look);
                    } else if (type == 2) {
                        setFace(param);
	                    sendCharacterStat(Request.None, CharacterStatType.Face);
	                    postAvatarModified(AvatarLook.Face);
                    }
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        return -1;
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
                            CommonDB.rawUpdateItemEquip(characterID, character.getEquipped(), character.getEquipped2(), character.getItemSlot().get(ItemType.Equip));
                        }
                        if ((characterDataModFlag & DBChar.ItemSlotConsume) != 0 || (characterDataModFlag & DBChar.ItemSlotInstall) != 0
                                || (characterDataModFlag & DBChar.ItemSlotEtc) != 0) {
                            CommonDB.rawUpdateItemBundle(characterID, character.getItemSlot());
                        }
                        characterDataModFlag = 0;
                    }
                    if (miniRoom != null) {
                        //miniRoom.save();
                    }
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
                reset = secondaryStat.resetByReasonID(reasonID);
            else
                reset = secondaryStat.resetByTime(time);
            if (reset != 0) {
                validateStat(false);
                sendTemporaryStatReset(reset);
            }
        } finally {
            unlock();
        }
    }

    public void sendTemporaryStatSet(int set) {
        if (set != 0) {
            lock.lock();
            try {
                sendPacket(WvsContext.onTemporaryStatSet(Request.Excl, secondaryStat, set));
                if (getField() != null) {
                    getField().splitSendPacket(getSplit(), UserRemote.onTemporaryStatSet(characterID, secondaryStat, set), this);
                }
            } finally {
                unlock();
            }
        }
    }

    public void sendTemporaryStatReset(int reset) {
        if (reset != 0) {
            lock.lock();
            try {
                sendPacket(WvsContext.onTemporaryStatReset(reset));
                if (getField() != null) {
                    getField().splitSendPacket(getSplit(), UserRemote.onResetTemporaryStat(characterID, reset), this);
                }
            } finally {
                unlock();
            }
        }
    }

    public void onUserEffect(boolean local, boolean remote, byte effect, int... args) {
        int skillID = 0;
        int slv = 0;
        if (args.length > 0) {
            skillID = args[0];
            if (args.length > 1) {
                slv = args[1];
            }
        }
        if (remote) {
            getField().splitSendPacket(getSplit(), UserRemote.onEffect(getCharacterID(), effect, skillID, slv), this);
        }
        if (local) {
            sendPacket(UserLocal.onEffect(effect, skillID, slv));
        }
    }

    public void postAvatarModified(int flag) {
        if (((flag | avatarModFlag) != avatarModFlag) || flag == AvatarLook.Look) {
            avatarModFlag |= flag;
            avatarLook.load(character.getCharacterStat(), character.getEquipped(), character.getEquipped2());
            if (msMessenger)
                userMSM.notifyAvatarChanged();
            getField().splitSendPacket(getSplit(), UserRemote.onAvatarModified(this, flag), this);
            if (miniRoom != null) {
                //miniRoom.onAvatarChanged(this);
            }
            avatarModFlag = 0;
        }
    }

    public final void validateStat(boolean calledByConstructor) {
        lock.lock();
        try {
            AvatarLook avatarOld = avatarLook.makeClone();
            ItemAccessor.getRealEquip(character, realEquip, 0, 0);
            avatarLook.load(character.getCharacterStat(), character.getEquipped(), character.getEquipped2());

            int maxHPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.MaxHP);
            int maxMPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.MaxMP);
            int speed = secondaryStat.speed;
            int weaponID = 0;
            if (character.getItem(ItemType.Equip, -BodyPart.Weapon) != null) {
                weaponID = character.getItem(ItemType.Equip, -BodyPart.Weapon).getItemID();
            }

            basicStat.setFrom(character, realEquip, character.getEquipped2(), maxHPIncRate, maxMPIncRate);
            secondaryStat.setFrom(basicStat, realEquip, character);

            int flag = 0;
            if (character.getCharacterStat().getHP() > basicStat.getMHP()) {
                incHP(0, false);
                flag |= CharacterStatType.HP;
            }
            if (character.getCharacterStat().getMP() > basicStat.getMMP()) {
                incMP(0, false);
                flag |= CharacterStatType.MP;
            }

            if (!calledByConstructor) {
                if (flag != 0)
                    sendCharacterStat(Request.None, flag);
                if (!avatarLook.getEquipped().get(BodyPart.Weapon).equals(weaponID)) {
                    flag = 0;
                    if (secondaryStat.getStatOption(CharacterTemporaryStat.Booster) != 0) {
                        int reasonID = secondaryStat.getStatReason(CharacterTemporaryStat.Booster);
                        flag |= secondaryStat.resetByReasonID(reasonID);
                    }
                    if (secondaryStat.getStatOption(CharacterTemporaryStat.SoulArrow) != 0) {
                        int reasonID = secondaryStat.getStatReason(CharacterTemporaryStat.SoulArrow);
                        flag |= secondaryStat.resetByReasonID(reasonID);
                    }
                    sendTemporaryStatReset(flag);
                }
                flag = 0;
                if (!avatarOld.equals(avatarLook)) {
                    //addCharacterDataMod(DBChar.Avatar);
                    flag = AvatarLook.Look;
                }
                if (speed != secondaryStat.speed) {
                    flag |= AvatarLook.Unknown2;//idk, just a guess
                }
                postAvatarModified(flag);
            }
            // Max all skills on a user based on their current Job Race
            if (isGM() && character.getSkillRecord().isEmpty()) {
                for (SkillEntry skill : SkillInfo.getInstance().getAllSkills()) {
                    int skillID = skill.getSkillID();
                    int skillRoot = skillID / 10000;

                    if (JobAccessor.isCorrectJobForSkillRoot(character.getCharacterStat().getJob(), skillRoot)) {
                        character.getSkillRecord().put(skillID, skill.getLevelData().length);
                    }
                }
            }
            avatarOld.getEquipped().clear();
        } finally {
            unlock();
        }
    }

    public int getTradeMoneyLimit() {
        return tradeMoneyLimit;
    }

    public void setTradeMoneyLimit(int tradeMoneyLimit) {
        this.tradeMoneyLimit += tradeMoneyLimit;
    }

    public int getTempTradeMoney() {
        return tempTradeMoney;
    }

    public void setTempTradeMoney(int tempTradeMoney) {
        this.tempTradeMoney = tempTradeMoney;
    }

    public boolean isMSMessenger() {
        return msMessenger;
    }

    public void setMSMessenger(boolean msMessenger) {
        this.msMessenger = msMessenger;
    }

}
