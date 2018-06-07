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
package network.database;

import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemType;
import common.user.CharacterData;
import common.user.CharacterStat;
import common.user.DBChar;
import game.user.item.Inventory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import util.Pointer;

/**
 * For DB queries only used in the Game JVM.
 * 
 * @author Eric
 */
public class GameDB {
    
    public static void rawLoadAccount(int characterID, Pointer<Integer> accountID, Pointer<String> nexonClubID, Pointer<Integer> gradeCode) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `users` u JOIN `character` c ON u.AccountID = c.AccountID WHERE c.CharacterID = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        accountID.set(rs.getInt("AccountID"));
                        nexonClubID.set(rs.getString("NexonClubID"));
                        gradeCode.set(rs.getInt("GradeCode"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static CharacterData rawLoadCharacter(int characterID) {
        CharacterData cd = null;
        
        try (Connection con = Database.getDB().poolConnection()) {
            // Load Stats
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `character` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cd = new CharacterData();
                        cd.load(rs, DBChar.Character);
                    } else {
                        return cd;
                    }
                }
            }
            // Load Items
            CommonDB.rawGetInventorySize(characterID, cd);
            CommonDB.rawGetItemEquip(characterID, cd);
            CommonDB.rawGetItemBundle(characterID, cd);
            // Load Skills
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `skillrecord` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    cd.load(rs, DBChar.SkillRecord);
                }
            }
            // Load Quests
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `questperform` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    cd.load(rs, DBChar.QuestRecord);
                }
            }
            // Initialize New Equip ItemSN/CashItemSN
            boolean updateEquip = false;
            for (ItemSlotBase item : cd.getEquipped()) {
                if (item != null) {
                    boolean changeSN = (item.getSN() < -1 || item.getCashItemSN() < -1);
                    if (changeSN) {
                        Inventory.getNextSN(item, item.getCashItemSN() != 0);
                    }
                    updateEquip |= changeSN;
                }
            }
            if (updateEquip) {
                CommonDB.rawUpdateItemEquip(characterID, cd.getEquipped(), cd.getEquipped2(), cd.getItemSlot(ItemType.Equip));
            }
            // Initialize New Bundle ItemSN/CashItemSN
            boolean updateBundle = false;
            for (int ti = ItemType.Equip; ti <= ItemType.Etc; ti++) {
                for (ItemSlotBase item : cd.getItemSlot(ti)) {
                    if (item != null) {
                        boolean changeSN = (item.getSN() < -1 || item.getCashItemSN() < -1);
                        if (changeSN) {
                            Inventory.getNextSN(item, item.getCashItemSN() != 0);
                        }
                        updateBundle |= changeSN;
                    }
                }
            }
            if (updateBundle) {
                CommonDB.rawUpdateItemBundle(characterID, cd.getItemSlot());
            }
            return cd;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return cd;
    }
    
    public static void rawSaveCharacter(CharacterStat cs) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `character` SET `CharacterName` = ?, `Gender` = ?, `Skin` = ?, `Face` = ?, `Hair` = ?, `Level` = ?, `Job` = ?, `STR` = ?, `DEX` = ?, `INT` = ?, `LUK` = ?, `HP` = ?, `MP` = ?, `MaxHP` = ?, `MaxMP` = ?, `AP` = ?, `SP` = ?, `EXP` = ?, `POP` = ?, `Money` = ?, `Map` = ?, `Portal` = ? WHERE `CharacterID` = ?")) {
                Database.execute(con, ps, cs.getName(), cs.getGender(), cs.getSkin(), cs.getFace(), cs.getHair(), cs.getLevel(), cs.getJob(), cs.getSTR(), cs.getDEX(), cs.getINT(), cs.getLUK(), cs.getHP(), cs.getMP(), cs.getMHP(), cs.getMMP(), cs.getAP(), cs.getSP(), cs.getEXP(), cs.getPOP(), cs.getMoney(), cs.getPosMap(), cs.getPortal(), cs.getCharacterID());
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawSaveSkillRecord(int characterID, Map<Integer, Integer> skillRecord) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `skillrecord` SET `Info` = ? WHERE `CharacterID` = ? AND `SkillID` = ?")) {
                for (Map.Entry<Integer, Integer> skill : skillRecord.entrySet()) {
                    Database.execute(con, ps, skill.getValue(), characterID, skill.getKey());
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawSetInventorySize(int characterID, List<Integer> inventorySize) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `inventorysize` SET `EquipCount` = ?, `ConsumeCount` = ?, `InstallCount` = ?, `EtcCount` = ? WHERE `CharacterID` = ?")) {
                Database.execute(con, ps, inventorySize.get(0), inventorySize.get(1), inventorySize.get(2), inventorySize.get(3), characterID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
