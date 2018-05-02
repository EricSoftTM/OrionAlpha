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
package login.avatar;

import common.item.BodyPart;
import common.item.ItemSlotBase;
import common.user.CharacterStat;
import common.user.DBChar;
import java.util.ArrayList;
import java.util.List;
import network.database.LoginDB;
import network.packet.OutPacket;

/**
 * AvatarData
 * 
 * @author Eric
 */
public class AvatarData {
    static final int BODY_PART_COUNT = BodyPart.BP_Count;
    
    private final CharacterStat characterStat;
    private final List<ItemSlotBase> equipped;
    private final List<ItemSlotBase> equipped2;
    
    public AvatarData() {
        this.characterStat = new CharacterStat();
        this.equipped = new ArrayList<>();
        this.equipped2 = new ArrayList<>();
        
        for (int i = 0; i < BODY_PART_COUNT + 1; i++) {
            this.equipped.add(i, null);
            this.equipped2.add(i, null);
        }
    }
    
    public void encode(OutPacket packet) {
        packet.encodeByte(DBChar.Character);// | DBChar.ItemSlotEquip
        // Encode Character
        characterStat.encode(packet);
        // Encode Equipment
        for (int nPOS = 1; nPOS <= equipped.size(); nPOS++) {
            ItemSlotBase item = equipped.get(nPOS - 1);
            if (item != null) {
                packet.encodeByte(nPOS);
                item.encode(packet);
            }
        }
        packet.encodeByte(0);
        // Encode Cash Equipment
        for (int nPOS = 1; nPOS <= equipped2.size(); nPOS++) {
            ItemSlotBase item = equipped2.get(nPOS - 1);
            if (item != null) {
                packet.encodeByte(nPOS);
                item.encode(packet);
            }
        }
        packet.encodeByte(0);
        // Encode Equip Inventory
        packet.encodeByte(0);
    }
    
    public CharacterStat getCharacterStat() {
        return characterStat;
    }
    
    public List<ItemSlotBase> getEquipped() {
        return equipped;
    }
    
    public List<ItemSlotBase> getEquipped2() {
        return equipped2;
    }
    
    public boolean load(int accountID, int characterID) {
        //TODO: Load items..
        return true;
    }
}
