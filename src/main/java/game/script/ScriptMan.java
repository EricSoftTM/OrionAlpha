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

import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class ScriptMan {
    
    public static OutPacket onSay(byte speakerTypeID, int speakerTemplateID, String text, boolean prev, boolean next) {
        OutPacket packet = new OutPacket(LoopbackPacket.ScriptMessage);
        packet.encodeByte(speakerTypeID);
        packet.encodeInt(speakerTemplateID);
        packet.encodeByte(ScriptMessage.Say);
        packet.encodeString(text);
        packet.encodeBool(prev);
        packet.encodeBool(next);
        return packet;
    }
    
    public static OutPacket onAskYesNo(byte speakerTypeID, int speakerTemplateID, String text) {
        OutPacket packet = new OutPacket(LoopbackPacket.ScriptMessage);
        packet.encodeByte(speakerTypeID);
        packet.encodeInt(speakerTemplateID);
        packet.encodeByte(ScriptMessage.AskYesNo);
        packet.encodeString(text);
        return packet;
    }
    
    public static OutPacket onAskText(byte speakerTypeID, int speakerTemplateID, String msg, String msgDefault, short lenMin, short lenMax) {
        OutPacket packet = new OutPacket(LoopbackPacket.ScriptMessage);
        packet.encodeByte(speakerTypeID);
        packet.encodeInt(speakerTemplateID);
        packet.encodeByte(ScriptMessage.AskText);
        packet.encodeString(msg);
        packet.encodeString(msgDefault);
        packet.encodeShort(lenMin);
        packet.encodeShort(lenMax);
        return packet;
    }
    
    public static OutPacket onAskNumber(byte speakerTypeID, int speakerTemplateID, String msg, int def, int min, int max) {
        OutPacket packet = new OutPacket(LoopbackPacket.ScriptMessage);
        packet.encodeByte(speakerTypeID);
        packet.encodeInt(speakerTemplateID);
        packet.encodeByte(ScriptMessage.AskNumber);
        packet.encodeString(msg);
        packet.encodeInt(def);
        packet.encodeInt(min);
        packet.encodeInt(max);
        return packet;
    }
    
    public static OutPacket onAskMenu(byte speakerTypeID, int speakerTemplateID, String msg) {
        OutPacket packet = new OutPacket(LoopbackPacket.ScriptMessage);
        packet.encodeByte(speakerTypeID);
        packet.encodeInt(speakerTemplateID);
        packet.encodeByte(ScriptMessage.AskMenu);
        packet.encodeString(msg);
        return packet;
    }
    
    public static OutPacket onAskAvatar(byte speakerTypeID, int speakerTemplateID, String msg, int[] canadite) {
        OutPacket packet = new OutPacket(LoopbackPacket.ScriptMessage);
        packet.encodeByte(speakerTypeID);
        packet.encodeInt(speakerTemplateID);
        packet.encodeByte(ScriptMessage.AskAvatar);
        packet.encodeString(msg);
        packet.encodeByte(canadite.length);
        for (int val : canadite) {
            packet.encodeInt(val);
        }
        return packet;
    }
}
