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
            try (PreparedStatement ps = con.prepareStatement("")) {
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
