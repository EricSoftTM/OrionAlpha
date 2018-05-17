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

import common.item.ItemType;
import common.user.CharacterData;
import common.user.DBChar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import shop.user.CashItemInfo;
import shop.user.User;
import util.FileTime;

/**
 *
 * @author Eric
 * @author sunnyboy
 */
public class ShopDB {

    private static void rawGetItemLocker(User user) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `itemlocker` WHERE `AccountID` = ?")) {
                ps.setInt(1, user.getAccountID());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CashItemInfo cashItemInfo = new CashItemInfo();
                        cashItemInfo.setCashItemSN(rs.getLong("CashItemSN"));
                        cashItemInfo.setAccountID(user.getAccountID());
                        cashItemInfo.setCharacterID(rs.getInt("CharacterID"));
                        cashItemInfo.setItemID(rs.getInt("ItemID"));
                        cashItemInfo.setCommodityID(rs.getInt("CommodityID"));
                        cashItemInfo.setNumber(rs.getShort("Number"));
                        cashItemInfo.setBuyCharacterName(rs.getString("BuyCharacterName"));
                        cashItemInfo.setDateExpire(FileTime.longToFileTime(rs.getLong("ExpiredDate")));
                        user.getCashItemInfo().add(cashItemInfo);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void rawIncreaseItemSlotCount(int characterID, byte typeIndex, int slotCount) {
        String[] types = { "Equip", "Consume", "Install", "Etc" };
        String inventory = String.format("%sCount", types[typeIndex]);
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `inventorysize` SET `" + inventory + "` = ? WHERE `CharacterID` = ?")) {
                Database.execute(con, ps, slotCount, characterID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void rawLoadAccount(int characterID, User user) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `users` u JOIN `character` c ON u.AccountID = c.AccountID WHERE c.CharacterID = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        user.setNexonCash(rs.getInt("NexonCash"));
                        user.setAccountID(rs.getInt("AccountID"));
                        user.setNexonClubID(rs.getString("LoginID"));
                        // more will be added when I get there
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static CharacterData rawLoadCharacter(int characterID, User user) {
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
            CommonDB.rawGetInventorySize(characterID, cd);
            CommonDB.rawGetItemEquip(characterID, cd);
            CommonDB.rawGetItemBundle(characterID, cd);

            rawGetItemLocker(user);
            return cd;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }

        return null;
    }

    public static void rawUpdateItemLocker(int characterID, List<CashItemInfo> cashItemInfo) {
        try (Connection con = Database.getDB().poolConnection()) {
            String removeCashSN = "";
            try (PreparedStatement ps = con.prepareStatement("UPDATE `itemlocker` SET `AccountID` = ?, `CharacterID` = ?, `ItemID` = ?, `CommodityID` = ?, `Number` = ?, `BuyCharacterName` = ?, `ExpiredDate` = ? WHERE `CashItemSN` = ?")) {
                for (CashItemInfo cashInfo : cashItemInfo) {
                    if (cashInfo != null) {
                        if (cashInfo.getCashItemSN() != 0) {
                            removeCashSN += cashInfo.getCashItemSN() + ", ";
                        }
                        Database.execute(con, ps, cashInfo.getAccountID(), cashInfo.getCharacterID(), cashInfo.getItemID(), cashInfo.getCommodityID(), cashInfo.getNumber(), cashInfo.getBuyCharacterName(), cashInfo.getDateExpire().fileTimeToLong(), cashInfo.getCashItemSN());
                    }
                }
            }
            if (removeCashSN.isEmpty()) {
                return;//wouldn't want to kill their inventory ;)
            }
            String query = "DELETE FROM `itemlocker` WHERE `CharacterID` = ?";
            if (!removeCashSN.isEmpty()) {
                query += String.format(" AND `CashItemSN` NOT IN (%s)", removeCashSN.substring(0, removeCashSN.length() - 2));
            }
            try (PreparedStatement ps = con.prepareStatement(query)) {
                Database.execute(con, ps, characterID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void rawUpdateNexonCash(int accountID, int nexonCash) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `users` SET `NexonCash` = ? WHERE `AccountID` = ?")) {
                Database.execute(con, ps, nexonCash, accountID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
