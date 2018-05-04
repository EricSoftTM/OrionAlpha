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

import common.OrionConfig;
import common.item.ItemSlotEquip;
import common.item.ItemType;
import common.user.CharacterData;
import common.user.DBChar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import login.avatar.Avatar;
import login.user.client.ClientSocket;
import network.security.BCrypt;
import util.FileTime;

/**
 * Login DB Processing
 * 
 * @author Eric
 */
public class LoginDB {
    
    public static int rawCheckPassword(String id, String passwd, ClientSocket socket) {
        int retCode = 1; //Success
        
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `users` WHERE `LoginID` = ?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String pass = rs.getString("Password");
                        if (BCrypt.checkPassword(passwd, pass) || BCrypt.checkPassword(passwd, OrionConfig.MASTER_PASSWORD)) {
                            int blockReason = rs.getByte("BlockReason");
                            if (blockReason > 0) {
                                retCode = 5; //Blocked
                            } else {
                                socket.setNexonClubID(id);
                                socket.setAccountID(rs.getInt("AccountID"));
                                socket.setGender(rs.getByte("Gender"));
                                socket.setGradeCode(rs.getByte("GradeCode"));
                            }
                        } else {
                            retCode = 4; //IncorrectPassword
                        }
                    } else {
                        retCode = 3; //NotRegistered
                    }
                }
            }
        } catch (SQLException ex) {
            retCode = 2; //DBFail
            ex.printStackTrace(System.err);
        }
        
        return retCode;
    }
    
    public static int rawCheckUserConnected(int accountID) {
        int retCode = 1; //Success
        
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `ConnectIP` FROM `userconnection` WHERE `AccountID` = ?")) {
                ps.setInt(1, accountID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        retCode = 6; //AlreadyConnected
                    }
                }
            }
        } catch (SQLException ex) {
            retCode = 2; //DBFail
            ex.printStackTrace(System.err);
        }
        
        return retCode;
    }
    
    public static int rawGetEveryWorldCharList(int accountID, List<Integer> worldID, List<Integer> characterID) {
        int count = 0;
        
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `WorldID`, `CharacterID` FROM `character` WHERE `AccountID` = ?")) {
                ps.setInt(1, accountID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        worldID.add(rs.getInt("WorldID"));
                        characterID.add(rs.getInt("CharacterID"));
                        count++;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return count;
    }
    
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
    
    public static int rawGetWorldCharList(int accountID, int worldID, List<Integer> characterID) {
        int count = 0;
        
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `CharacterID` FROM `character` WHERE `AccountID` = ? AND `WorldID` = ?")) {
                ps.setInt(1, accountID);
                ps.setInt(2, worldID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        characterID.add(rs.getInt("CharacterID"));
                        count++;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return count;
    }
    
    public static void rawLoadAvatar(int accountID, int worldID, List<Avatar> avatars) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `character` WHERE `AccountID` = ? AND `WorldID` = ?")) {
                ps.setInt(1, accountID);
                ps.setInt(2, worldID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Avatar avatar = new Avatar();
                        avatar.getCharacterStat().load(rs);
                        
                        avatar.load(accountID, avatar.getCharacterStat().getCharacterID());
                        
                        avatars.add(avatar);
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
            return cd;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return null;
    }
}
