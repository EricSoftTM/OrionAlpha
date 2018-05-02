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
package game.field;

import common.user.DBChar;
import game.user.User;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class Stage {
    
    public static OutPacket onSetField(User user, boolean characterData, int s1, int s2, int s3) {
        OutPacket packet = new OutPacket(LoopbackPacket.SetField);
        packet.encodeByte(0);//TODO: Local Channel
        packet.encodeBool(characterData);
        if (!characterData) {
            packet.encodeInt(user.getCharacter().getCharacterStat().getPosMap());
            packet.encodeByte(user.getCharacter().getCharacterStat().getPortal());
            packet.encodeShort(user.getCharacter().getCharacterStat().getHP());
        } else {
            packet.encodeInt(s1);
            packet.encodeInt(s2);
            packet.encodeInt(s3);
            user.getCharacter().encode(packet, DBChar.All);
        }
        return packet;
    }
}
