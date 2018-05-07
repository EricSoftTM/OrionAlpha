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
package common.user;

import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class CharacterData {
    static final int 
            BodyPartCount = BodyPart.BP_Count,
            ItemTypeCount = ItemType.NO,
            Sticker       = BodyPart.Sticker
    ;
    
    private final CharacterStat characterStat;
    private final List<ItemSlotBase> equipped;
    private final List<ItemSlotBase> equipped2;
    private final List<List<ItemSlotBase>> itemSlot;
    private final List<List<Integer>> itemTrading;
    private final Map<Integer, Integer> skillRecord;
    private final Map<String, String> questRecord;
    private int moneyTrading;
    
    public CharacterData() {
        this.characterStat = new CharacterStat();
        this.equipped = new ArrayList<>(BodyPartCount + 1);
        this.equipped2 = new ArrayList<>(BodyPartCount + 1);
        this.itemSlot = new ArrayList<>(ItemType.NO);
        this.itemTrading = new ArrayList<>(ItemType.NO);
        this.skillRecord = new HashMap<>();
        this.questRecord = new HashMap<>();
        
        for (int i = 0; i <= BodyPartCount; i++) {
            equipped.add(i, null);
            equipped2.add(i, null);
        }
        for (int i = 0; i < ItemTypeCount; i++) {
            itemSlot.add(i, new ArrayList<>());
            itemTrading.add(i, new ArrayList<>());
        }
    }
    
    public void backupItemSlot(List<List<ItemSlotBase>> backupItem, List<List<Integer>> backupItemTrading) {
        
    }
    
    public void clearTradingInfo() {
        this.moneyTrading = 0;
        for (int i = 0; i < ItemType.NO; i++) {
            for (int j = 0; j < itemTrading.get(i).size(); j++) {
                itemTrading.get(i).set(j, 0);
            }
        }
    }
    
    public void encode(OutPacket packet, byte flag) {
        packet.encodeByte(flag);
        if ((flag & DBChar.Character) != 0) {
            characterStat.encode(packet);
        }
        if ((flag & DBChar.ItemSlotEquip) != 0) {
            for (int i = 1; i <= BodyPartCount; i++) {
                ItemSlotBase item = equipped.get(i);
                if (item != null) {
                    packet.encodeByte(i);
                    item.encode(packet);
                }
            }
            packet.encodeByte(0);
            for (int i = 1; i <= BodyPartCount; i++) {
                ItemSlotBase item = equipped2.get(i);
                if (item != null) {
                    packet.encodeByte(i);
                    item.encode(packet);
                }
            }
            packet.encodeByte(0);
            int slotCount = getItemSlotCount(ItemType.Equip);
            packet.encodeByte(slotCount);
            for (int i = 1; i <= slotCount; i++) {
                ItemSlotBase item = itemSlot.get(ItemType.Equip).get(i);
                if (item != null) {
                    packet.encodeByte(i);
                    item.encode(packet);
                }
            }
            packet.encodeByte(0);
        }
        for (int ti = ItemType.Consume; ti <= ItemType.Etc; ti++) {
            if ((flag & ItemAccessor.getItemTypeFromTypeIndex(ti)) != 0) {
                int slotCount = getItemSlotCount(ti);
                packet.encodeByte(slotCount);
                for (int i = 1; i <= slotCount; i++) {
                    ItemSlotBase item = itemSlot.get(ti).get(i);
                    if (item != null) {
                        packet.encodeByte(i);
                        item.encode(packet);
                    }
                }
                packet.encodeByte(0);
            }
        }
        if ((flag & DBChar.SkillRecord) != 0) {
            packet.encodeShort(skillRecord.size());
            for (Map.Entry<Integer, Integer> skillEntry : skillRecord.entrySet()) {
                packet.encodeInt(skillEntry.getKey());//nSkillID
                packet.encodeInt(skillEntry.getValue());//nSLV
            }
        }
        if ((flag & DBChar.QuestRecord) != 0) {
            packet.encodeShort(questRecord.size());
            for (Map.Entry<String, String> questEntry : questRecord.entrySet()) {
                packet.encodeString(questEntry.getKey());//Unknown
                packet.encodeString(questEntry.getValue());//Unknown
            }
        }
    }
    
    public List<ItemSlotBase> getEquipped() {
        return equipped;
    }
    
    public List<ItemSlotBase> getEquipped2() {
        return equipped2;
    }
    
    public final CharacterStat getCharacterStat() {
        return characterStat;
    }
    
    public List<List<ItemSlotBase>> getItemSlot() {
        return itemSlot;
    }
    
    public List<ItemSlotBase> getItemSlot(int ti) {
        return itemSlot.get(ti);
    }
    
    public int getItemSlotCount(int ti) {
        return itemSlot.get(ti).size() - 1;
    }
    
    public int getMoneyTrading() {
        return moneyTrading;
    }
    
    public Map<Integer, Integer> getSkillRecord() {
        return skillRecord;
    }
    
    public void load(ResultSet rs, byte flag) throws SQLException {
        if ((flag & DBChar.Character) != 0) {
            characterStat.load(rs);
        }
        
        if ((flag & DBChar.SkillRecord) != 0) {
            while (rs.next()) {
                skillRecord.put(rs.getInt("SkillID"), rs.getInt("Info"));
            }
        }
        if ((flag & DBChar.QuestRecord) != 0) {
            while (rs.next()) {
                questRecord.put(rs.getString("QRKey"), rs.getString("QRValue"));
            }
        }
    }
    
    public boolean setItem(int ti, int pos, ItemSlotBase item) {
        if (ti < ItemType.Equip || ti > ItemType.Etc) {
            return false;
        }
        if (ti == ItemType.Equip) {
            if (pos == 0 || pos < -BodyPartCount && pos >= -Sticker || pos > getItemSlotCount(ItemType.Equip) || pos < -Sticker - BodyPartCount) {
                return false;
            }
            if (pos >= -Sticker) {
                if (pos >= 0) {
                    itemSlot.get(ti).set(pos, item);
                } else {
                    equipped.set(-pos, item);
                }
            } else {
                equipped2.set(-Sticker - pos, item);
            }
        } else {
            if (pos <= 0 || pos > getItemSlotCount(ti)) {
                return false;
            }
            itemSlot.get(ti).set(pos, item);
        }
        return true;
    }
}
