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

import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemType;
import common.user.CharacterData;
import common.user.CharacterStat;
import common.user.DBChar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.FileTime;

/**
 *
 * @author Eric
 */
public class GameDB {
    
    public static void rawGetInventorySize(int characterID, CharacterData cd) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `inventorysize` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String[] types = {"Equip", "Consume", "Install", "Etc" };
                        for (int i = 1; i <= types.length; i++) {
                            int count = rs.getInt(String.format("%sCount", types[i - 1]));
                            for (int j = 0; j <= count; j++) {
                                cd.getItemSlot(i).add(j, null);
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawGetItemEquip(int characterID, CharacterData cd) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `itemslotequip` WHERE `CharacterID` = ? ORDER BY `SN`")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int itemID = rs.getInt("ItemID");
                        int pos = rs.getInt("POS");
                        
                        ItemSlotEquip item = new ItemSlotEquip(itemID);
                        item.setDateExpire(FileTime.longToFileTime(rs.getLong("ExpireDate")));
                        item.ruc = rs.getByte("RUC");
                        item.cuc = rs.getByte("CUC");
                        item.iSTR = rs.getShort("I_STR");
                        item.iDEX = rs.getShort("I_DEX");
                        item.iINT = rs.getShort("I_INT");
                        item.iLUK = rs.getShort("I_LUK");
                        item.iMaxHP = rs.getShort("I_MaxHP");
                        item.iMaxMP = rs.getShort("I_MaxMP");
                        item.iPAD = rs.getShort("I_PAD");
                        item.iMAD = rs.getShort("I_MAD");
                        item.iPDD = rs.getShort("I_PDD");
                        item.iMDD = rs.getShort("I_MDD");
                        item.iACC = rs.getShort("I_ACC");
                        item.iEVA = rs.getShort("I_EVA");
                        item.iCraft = rs.getShort("I_Craft");
                        item.iSpeed = rs.getShort("I_Speed");
                        item.iJump = rs.getShort("I_Jump");
                        
                        cd.setItem(ItemType.Equip, pos, item);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static void rawGetItemBundle(int characterID, CharacterData cd) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `itemslotbundle` WHERE `CharacterID` = ? ORDER BY `SN`")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int itemID = rs.getInt("ItemID");
                        int pos = rs.getInt("POS");
                        int ti = rs.getInt("TI");
                        
                        ItemSlotBundle item = new ItemSlotBundle(itemID);
                        item.setDateExpire(FileTime.longToFileTime(rs.getLong("ExpireDate")));
                        item.setItemNumber(rs.getShort("Number"));
                        
                        cd.setItem(ti, pos, item);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public static CharacterData rawLoadCharacter(int characterID) {
        try (Connection con = Database.getDB().poolConnection()) {
            CharacterData cd = new CharacterData();
            // Load Stats
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `character` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cd.load(rs, DBChar.Character);
                    }
                }
            }
            // Load Items
            rawGetInventorySize(characterID, cd);
            rawGetItemEquip(characterID, cd);
            rawGetItemBundle(characterID, cd);
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
            return cd;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return null;
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
}
