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
import common.item.BodyPart;
import common.item.ItemSlotBase;
import common.user.CharacterData;
import common.user.DBChar;
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import login.user.ClientSocket;
import login.user.item.Inventory;
import network.security.BCrypt;
import util.Pointer;

/**
 * Login DB Processing
 * 
 * @author Eric
 */
public class LoginDB {
    static final String[] DELETE_CHARACTER = {
        "character",
        "givepopularity",
        "inventorysize",
        "itemlocker",
        "itemslotbundle",
        "itemslotequip",
        "questperform",
        "skillrecord"
    };
    
    public static boolean rawCheckDuplicateID(String id, int accountID) {
        boolean nameUsed = true;
        
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(`CharacterName`) FROM `character` WHERE `CharacterName` = ?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nameUsed = rs.getInt(1) != 0;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return nameUsed;
    }
    
    public static int rawCheckPassword(String id, String passwd, ClientSocket socket) {
        int retCode = 2; //DBFail
        
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
                                socket.setSSN(rs.getInt("SSN1"));
                                
                                retCode = 1; //Success
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
            ex.printStackTrace(System.err);
        }
        
        return retCode;
    }
    
    public static int rawCheckUserConnected(int accountID) {
        int retCode = 2; //DBFail
        
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `ConnectIP` FROM `userconnection` WHERE `AccountID` = ?")) {
                ps.setInt(1, accountID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        retCode = 6; //AlreadyConnected
                    } else {
                        retCode = 1; //Success
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return retCode;
    }
    
    public static int rawCreateNewCharacter(int accountID, int worldIdx, String characterName, int gender, int face, int skin, int hair, int level, int job, int clothes, int pants, int shoes, int weapon, List<Integer> stats, int map, Pointer<Integer> characterID) {
        int retCode = 2; //DBFail
        int result;
        
        try (Connection con = Database.getDB().poolConnection()) {
            // Construct the new character
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO `character` (`AccountID`, `WorldID`, `CharacterName`, `Gender`, `Skin`, `Face`, `Hair`, `Level`, `Job`, `STR`, `DEX`, `INT`, `LUK`, `Map`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                result = Database.execute(con, ps, accountID, worldIdx, characterName, gender, skin, face, hair, level, job, stats.get(0), stats.get(1), stats.get(2), stats.get(3), map);
                
                if (result >= 0) {
                    retCode = 1; //Success
                }
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        characterID.set(rs.getInt(1));
                    } else {
                        retCode = 2; //DBFail
                    }
                }
            }
            if (retCode == 1) {
                // Initialize their inventory size
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO `inventorysize` (`CharacterID`) VALUES (?)")) {
                    result = Database.execute(con, ps, characterID.get());
                    if (result <= 0) {
                        retCode = 2; //DBFail
                    }
                }
                // Construct their Avatar, make equipment
                if (retCode == 1) {
                    List<ItemSlotBase> equipped = new ArrayList<>();
                    for (int i = 0; i <= BodyPart.BP_Count; i++) {
                        if (i == BodyPart.Clothes && clothes != 0) {
                            equipped.add(i, ItemInfo.getItemSlot(clothes, ItemVariationOption.None));
                            Inventory.getNextSN(equipped.get(i), false);
                        } else if (i == BodyPart.Pants && pants != 0) {
                            equipped.add(i, ItemInfo.getItemSlot(pants, ItemVariationOption.None));
                            Inventory.getNextSN(equipped.get(i), false);
                        } else if (i == BodyPart.Shoes && shoes != 0) {
                            equipped.add(i, ItemInfo.getItemSlot(shoes, ItemVariationOption.None));
                            Inventory.getNextSN(equipped.get(i), false);
                        } else if (i == BodyPart.Weapon && weapon != 0) {
                            equipped.add(i, ItemInfo.getItemSlot(weapon, ItemVariationOption.None));
                            Inventory.getNextSN(equipped.get(i), false);
                        } else {
                            equipped.add(i, null);
                        }
                    }
                    CommonDB.rawUpdateItemEquip(characterID.get(), equipped, null, null);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return retCode;
    }
    
    public static int rawDeleteCharacter(int characterID) {
        int retCode = 0; //Success
        
        try (Connection con = Database.getDB().poolConnection()) {
            for (String deleteCharacter : DELETE_CHARACTER) {
                String query = String.format("DELETE FROM `%s` WHERE `CharacterID` = ?", deleteCharacter);
                if (query.contains("givepopularity")) {
                    query += " OR `TargetID` = ?";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    Database.execute(con, ps, characterID, characterID);
                }
            }
        } catch (SQLException ex) {
            retCode = 2; //DBFail?
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
            return cd;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return cd;
    }
}
