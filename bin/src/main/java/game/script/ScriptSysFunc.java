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
package game.script;

import common.JobAccessor;
import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
import game.field.Field;
import game.field.FieldMan;
import game.field.GameObject;
import game.field.life.mob.Mob;
import game.field.portal.Portal;
import game.user.User;
import game.user.WvsContext;
import game.user.WvsContext.BroadcastMsg;
import game.user.WvsContext.Request;
import game.user.item.ChangeLog;
import game.user.item.ExchangeElem;
import game.user.item.Inventory;
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;
import game.user.item.StateChangeItem;
import game.user.skill.SkillInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.packet.InPacket;
import util.Logger;

/**
 *
 * @author Eric
 */
public class ScriptSysFunc {
    private final ScriptVM runningVM;
    private final Object continuation;
    private Object value;
    private int inputNo;
    private String inputStr;
    
    public ScriptSysFunc(ScriptVM vm) {
        this.runningVM = vm;
        this.continuation = new Object();
    }
    
    public void onScriptMessageAnswer(User target, InPacket packet) {
        if (runningVM.getTarget() != target) {
            return;
        }
        if (runningVM.getHistoryPos() < 0 || runningVM.getMsgHistory().isEmpty()) {
            Logger.logError("Invalid quest msg history position");
            return;
        }
        byte msgType = packet.decodeByte();
        byte ret = packet.decodeByte();
        switch (msgType) {
            case ScriptMessage.Say:
                int posMsgHistory;
                if (ret > 0) {
                    if (ret != 1) {
                        tryFinish();
                        return;
                    }
                    if (runningVM.getHistoryPos() == runningVM.getMsgHistory().size() - 1) {
                        tryResume();
                        return;
                    }
                    posMsgHistory = runningVM.getHistoryPos() + 1;
                } else {
                    if (ret < 0 || runningVM.getHistoryPos() < 0) {
                        tryFinish();
                        return;
                    }
                    posMsgHistory = runningVM.getHistoryPos() - 1;
                }
                runningVM.setHistoryPos(posMsgHistory);
                MsgHistory msg = runningVM.getMsgHistory().get(posMsgHistory);
                boolean next = (Boolean) msg.getMemory().get(1);
                target.sendPacket(ScriptMan.onSay((byte) msg.getSpeakerTypeID(), msg.getSpeakerTemplateID(), (String) msg.getMemory().get(0), posMsgHistory != 1, next));
                break;
            case ScriptMessage.AskYesNo:
                if (ret == -1) {
                    tryFinish();
                    return;
                }
                this.value = ret;
                tryResume();
                break;
            case ScriptMessage.AskAvatar:
                if (ret == 0) {
                    tryFinish();
                    return;
                }
                int sel = packet.decodeByte() & 0xFF;
                if ((sel & 0x80) == 0) {
                    //CUser::TryChangeHairOrFace(pTarget, *(_DWORD *)&v7[20].dummy[0], *(_DWORD *)(v18 + 4 * v17));
                }
                tryResume();
                break;
            case ScriptMessage.AskMenu:
                if (ret == 0) {
                    tryFinish();
                    return;
                }
                this.value = packet.decodeInt();
                tryResume();
                break;
            case ScriptMessage.AskText:
                if (ret == 0) {
                    tryFinish();
                    return;
                }
                this.inputStr = packet.decodeString();
                this.value = this.inputStr;
                tryResume();
                break;
            case ScriptMessage.AskNumber:
                if (ret == 0) {
                    tryFinish();
                    return;
                }
                this.inputNo = packet.decodeInt();
                this.value = this.inputNo;
                tryResume();
                break;
            default: {
                Logger.logError("Unable to process npc quest action");
                tryFinish();
            }
        }
    }
    
    public String getInputStrResult() {
        return inputStr;
    }
    
    public int getInputNoResult() {
        return inputNo;
    }
    
    public Point getCurrentPos() {
        return runningVM.getCurrentPos();
    }
    
    public User getUser() {
        return runningVM.getTarget();
    }
    
    public void clear() {
        if (runningVM.getStatus().get() != ScriptVM.Finishing) {
            runningVM.getStatus().set(ScriptVM.Finishing);
            runningVM.destroy(runningVM.getTarget());
        }
    }
    
    public void clearMsgHistory() {
        runningVM.getMsgHistory().clear();
        runningVM.setHistoryPos(0);
    }
    
    private void makeMessagePacket(int type, List<Object> memory, GameObject speaker) {
        if (runningVM.getStatus().get() == ScriptVM.Finishing) {
            throw new RuntimeException("Attempting to execute script after finish status.");
        }
        if (!runningVM.getMsgHistory().isEmpty() && (type != ScriptMessage.Say || runningVM.getMsgHistory().getFirst().getType() != ScriptMessage.Say)) {
            clearMsgHistory();
        }
        MsgHistory msg = new MsgHistory(type);
        msg.getMemory().addAll(memory);
        byte speakerTypeID = 0;
        int speakerTemplateID = 0;
        if (speaker != null) {
            speakerTypeID = (byte) speaker.getGameObjectTypeID();
            speakerTemplateID = speaker.getTemplateID();
        }
        switch (type) {
            case ScriptMessage.Say:
                boolean next = (Boolean) memory.get(1);
                msg.setPacket(ScriptMan.onSay(speakerTypeID, speakerTemplateID, (String) memory.get(0), runningVM.getHistoryPos() != 0, next));
                break;
            case ScriptMessage.AskYesNo:
                msg.setPacket(ScriptMan.onAskYesNo(speakerTypeID, speakerTemplateID, (String) memory.get(0)));
                break;
            case ScriptMessage.AskAvatar:
                msg.setPacket(ScriptMan.onAskAvatar(speakerTypeID, speakerTemplateID, (String) memory.get(0), (int[]) memory.get(1)));
                break;
            case ScriptMessage.AskMenu:
                msg.setPacket(ScriptMan.onAskMenu(speakerTypeID, speakerTemplateID, (String) memory.get(0)));
                break;
            case ScriptMessage.AskText:
                msg.setPacket(ScriptMan.onAskText(speakerTypeID, speakerTemplateID, (String) memory.get(0), (String) memory.get(1), (Short) memory.get(2), (Short) memory.get(3)));
                break;
            case ScriptMessage.AskNumber:
                msg.setPacket(ScriptMan.onAskNumber(speakerTypeID, speakerTemplateID, (String) memory.get(0), (Integer) memory.get(1), (Integer) memory.get(2), (Integer) memory.get(3)));
                break;
            default: {
                return;
            }
        }
        msg.setSpeakerTypeID(speakerTypeID);
        msg.setSpeakerTemplateID(speakerTemplateID);
        runningVM.getMsgHistory().addLast(msg);
        runningVM.setHistoryPos(runningVM.getMsgHistory().size());
    }
    
    public void say(String text) {
        say(text, false);
    }
    
    public void say(String text, boolean next) {
        List<Object> memory = new ArrayList<>();
        memory.add(text);
        memory.add(next);
        makeMessagePacket(ScriptMessage.Say, memory, runningVM.getSelf());
        sendMessageAnswer();
        memory.clear();
        tryCapture();
    }
    
    public void sayNext(String text) {
        say(text, true);
    }
    
    public Object askYesNo(String text) {
        List<Object> memory = new ArrayList<>();
        memory.add(text);
        makeMessagePacket(ScriptMessage.AskYesNo, memory, runningVM.getSelf());
        sendMessageAnswer();
        memory.clear();
        tryCapture();
        return value;
    }
    
    public Object askMenu(String text) {
        List<Object> memory = new ArrayList<>();
        memory.add(text);
        makeMessagePacket(ScriptMessage.AskMenu, memory, runningVM.getSelf());
        sendMessageAnswer();
        memory.clear();
        tryCapture();
        return value;
    }
    
    public Object askAvatar(String text, int canadite[]) {
        List<Object> memory = new ArrayList<>();
        memory.add(text);
        memory.add(canadite);
        makeMessagePacket(ScriptMessage.AskAvatar, memory, runningVM.getSelf());
        sendMessageAnswer();
        memory.clear();
        tryCapture();
        return value;
    }
    
    public Object askNumber(String text, int def, int min, int max) {
        List<Object> memory = new ArrayList<>();
        memory.add(text);
        memory.add(def);
        memory.add(min);
        memory.add(max);
        makeMessagePacket(ScriptMessage.AskNumber, memory, runningVM.getSelf());
        sendMessageAnswer();
        memory.clear();
        tryCapture();
        return value;
    }
    
    public Object askText(String text) {
        return askText(text, "", 0, 0);
    }
    
    public Object askText(String msg, String msgDefault, int lenMin, int lenMax) {
        List<Object> memory = new ArrayList<>();
        memory.add(msg);
        memory.add(msgDefault);
        memory.add(lenMin);
        memory.add(lenMax);
        makeMessagePacket(ScriptMessage.AskText, memory, runningVM.getSelf());
        sendMessageAnswer();
        memory.clear();
        tryCapture();
        return value;
    }
    
    public void fieldBroadcastMessage(byte bmType, String message) {
        fieldBroadcastMessage(userGetFieldID(), bmType, message);
    }
    
    public void fieldBroadcastMessage(int fieldID, byte bmType, String message) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return;
        }
        field.broadcastPacket(WvsContext.onBroadcastMsg(bmType, message), false);
    }
    
    public boolean fieldEnablePortal(int fieldID, String portal, boolean enable) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return false;
        }
        return field.getPortal().enablePortal(portal, enable);
    }
    
    public int fieldGetMobCount(int fieldID, int mobID) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return 0;
        }
        return field.getLifePool().getMobCount(mobID);
    }
    
    public int fieldGetUserCount() {
        return fieldGetUserCount(userGetFieldID());
    }
    
    public int fieldGetUserCount(int fieldID) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return 0;
        }
        return field.getUsers().size();
    }
    
    public boolean fieldIsUserExist(int fieldID, int characterID) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return false;
        }
        return field.isUserExist(characterID);
    }
    
    public void fieldRemoveAllMob(int fieldID) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return;
        }
        field.getLifePool().removeAllMob();
    }
    
    public void fieldRemoveMob(int fieldID, int mobTemplateID) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return;
        }
        field.getLifePool().removeMob(mobTemplateID);
    }
    
    public void fieldSetMobGen(int fieldID, boolean mobGen, int mobTemplateID) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return;
        }
        field.getLifePool().setMobGen(mobGen, mobTemplateID);
    }
    
    public void fieldSnowOn(int duration) {
        getUser().getField().onWeather(2090000, "", duration);
    }
    
    public void fieldSummonMob(int fieldID, int mobTemplateID, int x, int y, int count) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return;
        }
        Mob mob = field.getLifePool().createMob(mobTemplateID);
        if (mob != null) {
            for (int i = 0; i < Math.min(count, 100); i++) {
                field.getLifePool().createMob(mob, new Point(x, y));
            }
        }
    }
    
    public void fieldSummonNpc(int fieldID, int npcTemplateID, int x, int y) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return;
        }
        //field.summonNpc(npcTemplateID, x, y);
    }
    
    public void fieldTransferFieldAll(int fieldID, int portalIdx) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No field %d", fieldID);
            return;
        }
        Portal portal = field.getPortal().getPortal(portalIdx);
        if (portal == null) {
            portal = field.getPortal().getPortal(0);
        }
        fieldTransferFieldAll(fieldID, portal.getPortalName());
    }
    
    public void fieldTransferFieldAll(int fieldID, String portal) {
        //getUser().getField().postTransferFieldAll(fieldID, portal);
    }
    
    public void fieldWeatherMsg(int itemID, String msg, int duration) {
        getUser().getField().onWeather(itemID, msg, duration);
    }
    
    public void fieldVanishNpc(int templateID) {
        //getUser().getField().vanishNpc(templateID);
    }
    
    public boolean inventoryExchange(int incMoney, int... args) {
        if (getUser() == null || (args.length % 2) != 0) {
            return false;
        }
        List<ExchangeElem> exchange = new ArrayList<>();
        List<Integer> items = new ArrayList<>();
        List<ChangeLog> logAdd = new ArrayList<>();
        List<ChangeLog> logRemove = new ArrayList<>();
        int argCount = args.length / 2;
        ExchangeElem exchangeElem;
        for (int i = 0; i < argCount; i++) {
            int argIdx = 2 * i;
            int itemID = args[argIdx];
            int count = args[argIdx + 1];
            byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
            if (count <= 0) {
                if (ItemAccessor.isRechargeableItem(itemID)) {
                    int nSlotCount = getUser().getCharacter().getItemSlotCount(ti);
                    if (nSlotCount < 1) {
                        logRemove.clear();
                        logAdd.clear();
                        items.clear();
                        exchange.clear();
                        return false;
                    }
                    int j = 1;
                    while (true) {
                        ItemSlotBase pItem = getUser().getCharacter().getItem(ti, j);
                        if (pItem != null && pItem.getItemID() == itemID) {
                            break;
                        }
                        ++j;
                        if (j > nSlotCount) {
                            break;
                        }
                    }
                    if (j == 0 || j > nSlotCount || getUser().getCharacter().getItem(ti, j) == null) {
                        logRemove.clear();
                        logAdd.clear();
                        items.clear();
                        exchange.clear();
                        return false;
                    }
                    exchangeElem = new ExchangeElem();
                    exchangeElem.add = false;
                    exchangeElem.r.itemID = 0;
                    exchangeElem.r.count = 1;
                    exchangeElem.r.ti = ti;
                    exchangeElem.r.pos = (short) j;
                    exchange.add(exchangeElem);
                } else {
                    exchangeElem = new ExchangeElem();
                    exchangeElem.add = false;
                    exchangeElem.r.itemID = itemID;
                    exchangeElem.r.count = (short) -count;
                    exchangeElem.r.ti = 0;
                    exchangeElem.r.pos = 0;
                    exchange.add(exchangeElem);
                }
            } else {
                if (!ItemAccessor.isBundleTypeIndex(ti)) {
                    exchangeElem = new ExchangeElem();
                    exchangeElem.initAdd(itemID, (short) 1, null);
                    exchange.add(exchangeElem);
                } else {
                    int maxPerSlot = SkillInfo.getInstance().getBundleItemMaxPerSlot(itemID, getUser().getCharacter());
                    if (maxPerSlot <= 0) {
                        logRemove.clear();
                        logAdd.clear();
                        items.clear();
                        exchange.clear();
                        return false;
                    }
                    int roomCount = Inventory.getRoomCountInSlot(getUser(), ti);
                    if (ItemAccessor.isRechargeableItem(itemID)) {
                        ItemSlotBase item = ItemInfo.getItemSlot(itemID, ItemVariationOption.None);
                        if (item != null) {
                            item.setItemNumber(maxPerSlot);
                        }
                        exchangeElem = new ExchangeElem();
                        exchangeElem.initAdd(0, (short) 0, item);
                        exchange.add(exchangeElem);
                    } else if (count > 0) {
                        int j = 0;
                        while (true) {
                            int remain = Math.min(count, maxPerSlot);
                            exchangeElem = new ExchangeElem();
                            exchangeElem.initAdd(itemID, (short) remain, null);
                            exchange.add(exchangeElem);
                            j++;
                            if (j > roomCount) {
                                if (count > maxPerSlot) {
                                    Logger.logError("Invalid argument in sysUInventoryExchange ArgCount[%d], ArgIdx[%d], ItemID[%d], Count[%d]", argCount, argIdx, itemID, count);
                                }
                                logRemove.clear();
                                logAdd.clear();
                                items.clear();
                                exchange.clear();
                                return false;
                            }
                            remain -= count;
                            if (remain <= 0)
                                break;
                        }
                    }
                }
            }
            items.add(itemID);
            items.add(count);
        }
        if (incMoney != 0 || argCount != 0) {
            if (Inventory.exchange(getUser(), incMoney, exchange, logAdd, logRemove)) {
                Inventory.sendInventoryOperation(getUser(), Request.None, logRemove);
                Inventory.sendInventoryOperation(getUser(), Request.None, logAdd);
                if (!items.isEmpty() && items.size() >= 2) {
                    //getUser().postQuestEffect(true, items, null, 0);
                }
                if (incMoney != 0) {
                    //getUser().sendIncMoneyMessage(incMoney);
                }
                logRemove.clear();
                logAdd.clear();
                items.clear();
                exchange.clear();
                return true;
            }
        }
        return false;
    }
    
    public int inventoryGetHoldCount(byte ti) {
        int count = 0;
        if (ti >= ItemType.Equip && ti <= ItemType.Etc) {
            for (ItemSlotBase item : getUser().getCharacter().getItemSlot(ti)) {
                if (item != null) {
                    ++count;
                }
            }
        } else {
            count = -1;
        }
        return count;
    }
    
    public int inventoryGetItemCount(int itemID) {
        return Inventory.getItemCount(getUser(), itemID);
    }
    
    public int inventoryGetSlotCount(byte ti) {
        if (ti >= ItemType.Equip && ti <= ItemType.Etc) {
            return getUser().getCharacter().getItemSlotCount(ti);
        }
        return 0;
    }
    
    public boolean inventoryIncSlotCount(byte ti, int inc) {
        return Inventory.incItemSlotCount(getUser(), ti, inc);
    }
    
    public boolean inventoryRemoveEquippedItem(int itemID) {
        for (int pos = -1; pos > -BodyPart.BP_Count; pos--) {
            ItemSlotBase item = getUser().getCharacter().getItem(ItemType.Equip, pos);
            if (item != null && item.getItemID() == itemID) {
                List<ExchangeElem> exchange = new ArrayList<>();
                ExchangeElem exchangeElem = new ExchangeElem();
                exchangeElem.add = false;
                exchangeElem.r.itemID = 0;
                exchangeElem.r.count = 1;
                exchangeElem.r.ti = ItemType.Equip;
                exchangeElem.r.pos = (short) pos;
                exchange.add(exchangeElem);
                if (Inventory.exchange(getUser(), 0, exchange, null, null)) {
                    return true;
                }
                exchange.clear();
                break;
            }
        }
        return false;
    }
    
    public void userBroadcastMessage(int bmType, String message) {
        getUser().sendPacket(WvsContext.onBroadcastMsg(BroadcastMsg.Notice, message));
    }
    
    public void userEnforceNpcChat(int npcID) {
        if (getUser() != null && getUser().getField() != null && getUser().getHP() != 0) {
            //getUser().enforceNpcChat(npcID);
        }
    }
    
    public short userGetAP() {
        return getUser().getCharacter().getCharacterStat().getAP();
    }
    
    public int userGetCharacterID() {
        return getUser().getCharacter().getCharacterStat().getCharacterID();
    }
    
    public short userGetDEX() {
        return getUser().getCharacter().getCharacterStat().getDEX();
    }
    
    public int userGetFace() {
        return getUser().getCharacter().getCharacterStat().getFace();
    }
    
    public int userGetFieldID() {
        return getUser().getField().getFieldID();
    }
    
    public byte userGetGender() {
        return getUser().getCharacter().getCharacterStat().getGender();
    }
    
    public short userGetHP() {
        return getUser().getCharacter().getCharacterStat().getHP();
    }
    
    public int userGetHair() {
        return getUser().getCharacter().getCharacterStat().getHair();
    }
    
    public short userGetINT() {
        return getUser().getCharacter().getCharacterStat().getINT();
    }
    
    public short userGetJob() {
        return getUser().getCharacter().getCharacterStat().getJob();
    }
    
    public short userGetLUK() {
        return getUser().getCharacter().getCharacterStat().getLUK();
    }
    
    public byte userGetLevel() {
        return getUser().getCharacter().getCharacterStat().getLevel();
    }
    
    public short userGetMHP() {
        return getUser().getCharacter().getCharacterStat().getMHP();
    }
    
    public short userGetMMP() {
        return getUser().getCharacter().getCharacterStat().getMMP();
    }
    
    public short userGetMP() {
        return getUser().getCharacter().getCharacterStat().getMP();
    }
    
    public int userGetMoney() {
        return getUser().getCharacter().getCharacterStat().getMoney();
    }
    
    public short userGetPOP() {
        return getUser().getCharacter().getCharacterStat().getPOP();
    }
    
    public short userGetSP() {
        return getUser().getCharacter().getCharacterStat().getSP();
    }
    
    public short userGetSTR() {
        return getUser().getCharacter().getCharacterStat().getSTR();
    }
    
    public int userGetXPos() {
        return getUser().getCurrentPosition().x;
    }
    
    public int userGetYPos() {
        return getUser().getCurrentPosition().y;
    }
    
    public void userGiveBuff(int itemID) {
        StateChangeItem sci = ItemInfo.getStateChangeItem(itemID);
        if (sci != null) {
            if (getUser().lock()) {
                try {
                    long time = System.currentTimeMillis();
                    int flag = sci.getInfo().apply(getUser(), itemID, getUser().getCharacter(), getUser().getBasicStat(), getUser().getSecondaryStat(), time, false);
                    getUser().addCharacterDataMod(DBChar.Character);
                    getUser().sendCharacterStat(Request.None, sci.getInfo().getFlag());
                    getUser().sendTemporaryStatSet(flag);
                    //getUser().sendGiveBuffMessage(itemID);
                } finally {
                    getUser().unlock();
                }
            }
        }
    }
    
    public boolean userIncAP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incAP(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.AP);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncDEX(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incDEX(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.DEX);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncEXP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                int flag = getUser().incEXP(inc, onlyFull);
                if (flag != 0) {
                    getUser().sendCharacterStat(Request.None, flag);
                    //getUser().sendIncEXPMessage
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncHP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incHP(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.HP);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncINT(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incINT(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.INT);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncLUK(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incLUK(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.LUK);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncMHP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incMHP(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.MHP);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncMMP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incMMP(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.MMP);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncMP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incMP(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.MP);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncMoney(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incMoney(inc, onlyFull, true)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.Money);
                    //getUser().sendIncMoneyMessage(inc);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncPOP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incPOP(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.POP);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncSP(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incSP(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.SP);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIncSTR(int inc, boolean onlyFull) {
        if (getUser().lock()) {
            try {
                if (getUser().incSTR(inc, onlyFull)) {
                    getUser().sendCharacterStat(Request.None, CharacterStatType.STR);
                    return true;
                }
            } finally {
                getUser().unlock();
            }
        }
        return false;
    }
    
    public boolean userIsBeginner() {
        return getUser().getCharacter().getCharacterStat().getJob() == JobAccessor.Novice.getJob();
    }
    
    public boolean userIsSuperGM() {
        return getUser().isGM();
    }
    
    public boolean userIsWearItem(int necessaryItemID) {
        return getUser().isWearItemOnNeed(necessaryItemID);
    }
    
    public void userFace(int param) {
        getUser().getCharacter().getCharacterStat().setFace(param);
        getUser().sendCharacterStat(Request.None, CharacterStatType.Face);
        //getUser().postAvatarModified(AvatarLook.Look);
    }
    
    public void userHair(int param) {
        getUser().getCharacter().getCharacterStat().setHair(param);
        getUser().sendCharacterStat(Request.None, CharacterStatType.Hair);
        //getUser().postAvatarModified(AvatarLook.Look);
    }
    
    public void userJob(int val) {
        if (val > 0) {
            if (getUser().lock()) {
                try {
                    getUser().setJob(val);
                    getUser().sendCharacterStat(Request.None, CharacterStatType.Job);
                } finally {
                    getUser().unlock();
                }
            }
        }
    }
    
    public void userScriptMessage(String message) {
        getUser().sendSystemMessage(message);
    }
    
    public void userSkin(int param) {
        getUser().getCharacter().getCharacterStat().setSkin(param);
        getUser().sendCharacterStat(Request.None, CharacterStatType.Skin);
        //getUser().postAvatarModified(AvatarLook.Look);
    }
    
    public void registerTransferField(int field) {
        registerTransferField(field, 0);
    }
    
    public void registerTransferField(int fieldID, int portalIdx) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        Portal portal = field.getPortal().getPortal(portalIdx);
        if (portal == null) {
            portal = field.getPortal().getPortal(0);
        }
        getUser().postTransferField(fieldID, portal.getPortalName(), true);
    }
    
    public void registerTransferField(int fieldID, String portal) {
        Field field = FieldMan.getInstance().getField(fieldID, false);
        if (field == null) {
            Logger.logError("VM run time error - No Field %d", fieldID);
            return;
        }
        getUser().onTransferField(field, field.getPortal().findPortal(portal));
    }
    
    private void sendMessageAnswer() {
        runningVM.getTarget().sendPacket(runningVM.getMsgHistory().get(runningVM.getHistoryPos()).getPacket());
        runningVM.getStatus().set(ScriptVM.Message);
    }
    
    private void tryCapture() {
        this.value = null;
        synchronized (continuation) {
            try {
                continuation.wait();
            } catch (InterruptedException ex) {
                continuation.notifyAll();
            }
        }
    }
    
    private void tryFinish() {
        if (runningVM.getStatus().get() != ScriptVM.Finishing) {
            if (runningVM.getStatus().get() != ScriptVM.Pending) {
                runningVM.getStatus().set(ScriptVM.Pending);
                clearMsgHistory();
                clear();
                tryResume();
            } else {
                clear();
            }
        }
    }
    
    private void tryResume() {
        if (runningVM.getStatus().get() != ScriptVM.Finishing) {
            synchronized (continuation) {
                continuation.notifyAll();
                //clear();
            }
        }
    }
}
