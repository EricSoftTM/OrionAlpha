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

import game.field.GameObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class ScriptSysFunc {
    private final ScriptVM runningVM;
    
    public ScriptSysFunc(ScriptVM vm) {
        this.runningVM = vm;
    }
    
    public void clear() {
        runningVM.destroy(runningVM.getTarget());
    }
    
    public void clearMsgHistory() {
        runningVM.getMsgHistory().clear();
        runningVM.setHistoryPos(0);
    }
    
    private void makeMessagePacket(int type, List<Object> memory, GameObject speaker) {
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
    }
    
    public void sayNext(String text) {
        say(text, true);
    }
    
    private void sendMessageAnswer() {
        runningVM.getTarget().sendPacket(runningVM.getMsgHistory().get(runningVM.getHistoryPos()).getPacket());
        //runningVM.getStatus().set(ScriptVM.Message);
    }
}
