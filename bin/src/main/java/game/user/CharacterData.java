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

import common.item.BodyPart;
import common.user.CharacterStat;
import common.user.DBChar;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class CharacterData {
    private final CharacterStat characterStat;
    
    public CharacterData() {
        this.characterStat = new CharacterStat();
    }
    
    public final CharacterStat getCharacterStat() {
        return characterStat;
    }
    
    public void Encode(int flag, OutPacket packet) {
        packet.encodeByte(flag);
        if ((flag & DBChar.Character) != 0) {
            characterStat.Encode(packet);
        }
        if ((flag & DBChar.ItemSlotEquip) != 0) {
            for (int nPOS = 1; nPOS <= BodyPart.BP_Count; nPOS++) {
                // encode if exist (aEquipped)
            }
            packet.encodeByte(0);
            for (int nPOS = 1; nPOS <= BodyPart.BP_Count; nPOS++) {
                // encode if exist (aEquipped2)
            }
            packet.encodeByte(0);
            packet.encodeByte(25);
            for (int nPOS = 1; nPOS <= 25; nPOS++) {
                // encode if exist
            }
            packet.encodeByte(0);
        }
        if ((flag & DBChar.ItemSlotConsume) != 0) {
            packet.encodeByte(25);
            for (int nPOS = 1; nPOS <= 25; nPOS++) {
                // encode if exist
            }
            packet.encodeByte(0);
        }
        if ((flag & DBChar.ItemSlotInstall) != 0) {
            packet.encodeByte(25);
            for (int nPOS = 1; nPOS <= 25; nPOS++) {
                // encode if exist
            }
            packet.encodeByte(0);
        }
        if ((flag & DBChar.ItemSlotEtc) != 0) {
            packet.encodeByte(25);
            for (int nPOS = 1; nPOS <= 25; nPOS++) {
                // encode if exist
            }
            packet.encodeByte(0);
        }
        if ((flag & DBChar.SkillRecord) != 0) {
            packet.encodeShort(0);
            for (int i = 0; i < 0; i++) {
                packet.encodeInt(0);//SkillID
                packet.encodeInt(0);//SLV
            }
        }
        if ((flag & DBChar.QuestRecord) != 0) {
            packet.encodeShort(0);
            for (int i = 0; i < 0; i++) {
                packet.encodeString("");//Unknown
                packet.encodeString("");//Unknown
            }
        }
    }
}
