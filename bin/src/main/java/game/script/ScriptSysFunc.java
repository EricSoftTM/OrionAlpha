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
import common.user.CharacterStat.CharacterStatType;
import game.field.Field;
import game.field.FieldMan;
import game.field.GameObject;
import game.field.portal.Portal;
import game.user.User;
import game.user.WvsContext.Request;
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
    
    public boolean userIsBeginner() {
        return getUser().getCharacter().getCharacterStat().getJob() == JobAccessor.Novice.getJob();
    }
    
    public boolean userIsSuperGM() {
        return getUser().isGM();
    }
    
    public void userScriptMessage(String message) {
        getUser().sendSystemMessage(message);
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
