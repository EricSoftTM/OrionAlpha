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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import login.avatar.Avatar;
import login.user.client.ClientSocket;
import network.security.BCrypt;

/**
 * Login DB Processing
 * 
 * @author Eric
 */
public class LoginDB {
    
    public static int rawCheckPassword(String id, String passwd, ClientSocket socket) {
        int retCode = 1; //Success
        
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE LoginID = ?")) {
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
            try (PreparedStatement ps = con.prepareStatement("SELECT ConnectIP FROM userconnection WHERE AccountID = ?")) {
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
    
    public static void rawLoadAvatar(int accountID, int worldID, List<Avatar> avatars) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `character` WHERE `AccountID` = ? AND `WorldID` = ?")) {
                ps.setInt(1, accountID);
                ps.setInt(2, worldID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Avatar avatar = new Avatar();
                        avatar.getCharacterStat().setCharacterID(rs.getInt("CharacterID"));
                        avatar.getCharacterStat().setName(rs.getString("CharacterName"));
                        avatar.getCharacterStat().setGender(rs.getByte("Gender"));
                        avatar.getCharacterStat().setSkin(rs.getByte("Skin"));
                        avatar.getCharacterStat().setFace(rs.getInt("Face"));
                        avatar.getCharacterStat().setHair(rs.getInt("Hair"));
                        avatar.getCharacterStat().setLevel(rs.getByte("Level"));
                        avatar.getCharacterStat().setJob(rs.getShort("Job"));
                        avatar.getCharacterStat().setSTR(rs.getShort("STR"));
                        avatar.getCharacterStat().setDEX(rs.getShort("DEX"));
                        avatar.getCharacterStat().setINT(rs.getShort("INT"));
                        avatar.getCharacterStat().setLUK(rs.getShort("LUK"));
                        avatar.getCharacterStat().setHP(rs.getShort("HP"));
                        avatar.getCharacterStat().setMP(rs.getShort("MP"));
                        avatar.getCharacterStat().setMHP(rs.getShort("MaxHP"));
                        avatar.getCharacterStat().setMMP(rs.getShort("MaxMP"));
                        avatar.getCharacterStat().setAP(rs.getShort("AP"));
                        avatar.getCharacterStat().setSP(rs.getShort("SP"));
                        avatar.getCharacterStat().setEXP(rs.getInt("EXP"));
                        avatar.getCharacterStat().setPOP(rs.getShort("POP"));
                        avatar.getCharacterStat().setMoney(rs.getInt("Money"));
                        avatar.getCharacterStat().setPosMap(rs.getInt("Map"));
                        avatar.getCharacterStat().setPortal(rs.getByte("Portal"));
                        
                        avatar.load(accountID, avatar.getCharacterStat().getCharacterID());
                        
                        avatars.add(avatar);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
