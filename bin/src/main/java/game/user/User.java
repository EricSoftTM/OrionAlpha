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

import java.awt.Point;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import common.ExpAccessor;
import common.JobAccessor;
import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
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
import game.field.life.npc.Npc;
import game.field.life.npc.NpcTemplate;
import game.field.life.npc.ShopDlg;
import game.field.life.npc.ShopItem;
import game.field.life.npc.ShopResCode;
import game.field.portal.Portal;
import game.field.portal.PortalMap;
import game.messenger.Messenger;
import game.miniroom.MiniRoom;
import game.miniroom.MiniRoomBase;
import game.user.WvsContext.BroadcastMsg;
import game.user.WvsContext.Request;
import game.user.command.CommandHandler;
import game.user.command.UserGradeCode;
import game.user.item.BundleItem;
import game.user.item.ChangeLog;
import game.user.item.EquipItem;
import game.user.item.ExchangeElem;
import game.user.item.Inventory;
import game.user.item.InventoryManipulator;
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;
import game.user.item.StateChangeItem;
import game.user.skill.*;
import game.user.skill.Skills.*;
import game.user.stat.BasicStat;
import game.user.stat.CalcDamage;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.SecondaryStat;
import network.database.CommonDB;
import network.database.GameDB;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.*;

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
    private Npc tradingNpc;
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
    private Messenger msm;
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
        this.onTransferField = false;
        this.closeSocketNextTime = false;
        
        this.emotion = 0;
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
        this.lastCharacterDataFlush = time;
        this.nextCheckCashItemExpire = time;
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
        this.msm = new Messenger(this);
        // TODO: Nexon-like user caching to avoid DB load upon each login/migrate.
        this.character = GameDB.rawLoadCharacter(characterID);
        
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
        //if (runningVM != null) {
        //    runningVM = null;
        //}
        /* End CUser::~CUser destructor */
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
                        if (character.getCharacterStat().getJob() != 0) {
                            incSP(3, false);
                            incAP(5, false);
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
            //if (runningVM == null)
                return true;
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
    
    public BasicStat getBasicStat() {
        return basicStat;
    }
    
    public CalcDamage getCalcDamage() {
        return calcDamage;
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
        return msm;
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
        return gradeCode >= UserGradeCode.GM.getGrade();
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
            field = FieldMan.getInstance().getField(Field.Basic, false);
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
        // TODO: Load anything else i'm missing
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
            case ClientPacket.UserMigrateToCashShopRequest:
                onMigrateToCashShopRequest(packet);
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
    
    public void onChat(InPacket packet) {
        if (getField() == null) {
            return;
        }
        String text = packet.decodeString();
        
        if (text.startsWith("!")) {
            CommandHandler.handle(this, text);
            return;
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
    
    public void onChangeSlotPositionRequest(InPacket packet) {
        byte type = packet.decodeByte();
        short oldPos = packet.decodeShort();
        short newPos = packet.decodeShort();
        short count = packet.decodeShort();
        Inventory.changeSlotPosition(this, Request.Excl, type, oldPos, newPos, count);
    }
    
    public void onConsumeCashItemUseRequest(InPacket packet) {
        short pos = packet.decodeShort();
        int itemID = packet.decodeInt();
        String message = packet.decodeString();
        
        // Weather: 2090000
        // Megaphone: 2080000
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
    
    public void onMigrateToCashShopRequest(InPacket packet) {
        sendPacket(ClientSocket.onMigrateCommand(false, Utilities.netIPToInt32("127.0.0.1"), 8787));
    }
    
    public void onStatChangeItemUseRequest(InPacket packet) {
        if (getHP() == 0 || getField() == null || secondaryStat.getStatOption(CharacterTemporaryStat.DarkSight) != 0) {
            sendCharacterStat(Request.Excl, 0);
            return;
        }
        if (getField().lock(1000)) {
            try {
                if (lock()) {
                    try {
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
                    } finally {
                        unlock();
                    }
                }
            } finally {
                getField().unlock();
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
                        // TODO: User ScriptVM Handling
                        Logger.logReport("Executing npc script %s", npc.getNpcTemplate().getQuest());
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
                    if (fieldID != null && FieldMan.getInstance().isBlockedMap(fieldID)) {
                        Logger.logError("User tried to enter the Blocked Map #3 (From:%d,To:%d)", getField().getFieldID(), fieldID);
                        sendPacket(FieldPacket.onTransferFieldReqIgnored());
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
                            secondaryStat.setFrom(basicStat, character.getEquipped(), character.getEquipped2(), character);
                            validateStat(false);
                            addCharacterDataMod(DBChar.Character);
                            isDead = true;
                        }
                        if (fieldID != -1 && !loopback && !isDead && !isGM()) {
                            sendPacket(FieldPacket.onTransferFieldReqIgnored());
                            return;
                        }
                        if (!isDead && !loopback && !canAttachAdditionalProcess()) {
                            sendPacket(FieldPacket.onTransferFieldReqIgnored());
                            return;
                        }
                        if (fieldID == -1 && (portal == null || portal.isEmpty())) {
                            if (!loopback) {
                                sendPacket(FieldPacket.onTransferFieldReqIgnored());
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
                                sendPacket(FieldPacket.onTransferFieldReqIgnored());
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
                        Field field = FieldMan.getInstance().getField(fieldID, false);
                        if (field == null) {
                            Logger.logError("Invalid Field ID *1* : %d (Old: %d, IsDead: %b)", fieldID, getField().getFieldID(), isDead);
                            closeSocket();
                            return;
                        }
                        if (FieldMan.getInstance().isBlockedMap(fieldID)) {
                            Logger.logError("User tried to enter the Blocked Map #1 (From:%d,To:%d)", getField().getFieldID(), fieldID);
                            sendPacket(FieldPacket.onTransferFieldReqIgnored());
                            this.onTransferField = false;
                            return;
                        }
                        if (portal == null || portal.isEmpty() || (pt = field.getPortal().findPortal(portal)) == null) {
                            if (pos.x == 0 && pos.y == 0) {
                                pt = field.getPortal().getRandStartPoint();
                            } else {
                                pt = field.getPortal().findCloseStartPoint(pos.x, pos.y);
                            }
                            pos.x = pt.getPortalPos().x;
                            pos.y = pt.getPortalPos().y;
                        }
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
                            User user = User.findUser(pr.getOwnerID());
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
    }
    
    public void onLevelUp() {
        onUserEffect(true, true, UserEffect.LevelUp);
    }
    
    public void setMaxLevelReach() {
        if (!isGM()) {
            String notice = String.format("[Congrats] %s has reached Level 200! Congratulate %s on such an amazing achievement!", characterName, characterName);
            User.broadcast(WvsContext.onBroadcastMsg(BroadcastMsg.Notice, notice));
        }
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
            //AvatarLook avatarOld = avatarLook.makeClone();
            //ItemAccessor.getRealEquip
            avatarLook.load(character.getCharacterStat(), character.getEquipped(), character.getEquipped2());
            
            int maxHPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.MaxHP);
            int maxMPIncRate = secondaryStat.getStatOption(CharacterTemporaryStat.MaxMP);
            int speed = secondaryStat.speed;
            int weaponID = 0;
            if (character.getItem(ItemType.Equip, -BodyPart.Weapon) != null) {
                weaponID = character.getItem(ItemType.Equip, -BodyPart.Weapon).getItemID();
            }
            
            basicStat.setFrom(character, character.getEquipped(), character.getEquipped2(), maxHPIncRate, maxMPIncRate);
            secondaryStat.setFrom(basicStat, character.getEquipped(), character.getEquipped2(), character);
            
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
                // TODO: AvatarLook compare (current vs old)
                if (speed != secondaryStat.speed) {
                    flag |= AvatarLook.Unknown2;//idk, just a guess
                }
                // postAvatarModified(flag);
            }
            
            if (isGM() && character.getSkillRecord().isEmpty()) {
                // TODO: Max all skills
            }
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
    
    public class UserEffect {
        public static final byte
                LevelUp         = 0,
                SkillUse        = 1,
                SkillAffected   = 2
        ;
    }
}
