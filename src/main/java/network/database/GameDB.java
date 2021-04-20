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

import common.GivePopularityRes;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterData;
import common.user.CharacterStat;
import common.user.DBChar;
import game.field.drop.RewardInfo;
import game.friend.Friend;
import game.friend.FriendInfo;
import game.friend.FriendStatus;
import game.user.item.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import util.Logger;
import util.Pointer;

/**
 * For DB queries only used in the Game JVM.
 *
 * @author Eric
 */
public class GameDB {

    public static byte rawCheckGivePopularity(int characterID, int targetID) {
        byte retCode = GivePopularityRes.Success;
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `TargetID`, `LastGivePopularity` FROM `givepopularity` WHERE `CharacterID` = ? AND DATEDIFF(NOW(), `LastGivePopularity`) < 30")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // If their last fame was today
                        long time = rs.getTimestamp("LastGivePopularity").getTime() / 1000;
                        long cur = System.currentTimeMillis() / 1000;
                        if ((cur - time) < 86400) {
                            retCode = GivePopularityRes.AlreadyDoneToday;
                            break;
                        }
                        // If they famed the target within the past month
                        if (rs.getInt("TargetID") == targetID) {
                            retCode = GivePopularityRes.AlreadyDoneTarget;
                            break;
                        }
                    }
                }
            }
            if (retCode == GivePopularityRes.Success) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO `givepopularity` (`CharacterID`, `TargetID`) VALUES (?, ?)")) {
                    Database.execute(con, ps, characterID, targetID);
                }
            }
        } catch (SQLException ex) {
            retCode = GivePopularityRes.UnknownError;
            ex.printStackTrace(System.err);
        }
        return retCode;
    }

    public static void rawLoadAccount(int characterID, Pointer<Integer> accountID, Pointer<String> nexonClubID, Pointer<Integer> gradeCode) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `users` u JOIN `character` c ON u.AccountID = c.AccountID WHERE c.CharacterID = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        accountID.set(rs.getInt("AccountID"));
                        nexonClubID.set(rs.getString("LoginID"));
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
            CommonDB.rawGetItemPet(characterID, cd);
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
            for (int ti = ItemType.Equip; ti <= ItemType.Cash; ti++) {
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

    public static int rawLoadReward(int templateID, List<RewardInfo> rewardInfo) {
        int count = 0;

        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `reward` WHERE `TemplateID` = ?")) {
                ps.setInt(1, templateID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        double probRate = Math.min(1.0d, Math.max(0.0d, rs.getDouble("Prob")));
                        int prob = Math.min(1000000000, Math.max(0, (int) (probRate * 1000000000.0d)));
                        int money = rs.getInt("Money");
                        int itemId = rs.getInt("Item");
                        int min = rs.getInt("Min");
                        int max = rs.getInt("Max");
                        boolean premium = rs.getBoolean("premium");

                        rewardInfo.add(count++, new RewardInfo(money, itemId, prob, min, max, premium));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }

        return count;
    }

    public static void rawSaveCharacter(CharacterStat cs) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `character` SET `CharacterName` = ?, `Gender` = ?, `Skin` = ?, `Face` = ?, `Hair` = ?, `Level` = ?, `Job` = ?, `STR` = ?, `DEX` = ?, `INT` = ?, `LUK` = ?, `HP` = ?, `MP` = ?, `MaxHP` = ?, `MaxMP` = ?, `AP` = ?, `SP` = ?, `EXP` = ?, `POP` = ?, `Money` = ?, `Map` = ?, `Portal` = ?, `PetLockerSN` = ? WHERE `CharacterID` = ?")) {
                Database.execute(con, ps, cs.getName(), cs.getGender(), cs.getSkin(), cs.getFace(), cs.getHair(), cs.getLevel(), cs.getJob(), cs.getSTR(), cs.getDEX(), cs.getINT(), cs.getLUK(), cs.getHP(), cs.getMP(), cs.getMHP(), cs.getMMP(), cs.getAP(), cs.getSP(), cs.getEXP(), cs.getPOP(), cs.getMoney(), cs.getPosMap(), cs.getPortal(), cs.getPetLockerSN(), cs.getCharacterID());
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

    public static boolean getCharacterInfoByName(String characterName, Pointer<Integer> characterID) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `CharacterID` FROM `character` WHERE `CharacterName` = ?")) {
                ps.setString(1, characterName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        characterID.set(rs.getInt("CharacterID"));
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }

    public static int getFriendCount(int characterID) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `FriendCount` FROM `character` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("FriendCount");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return 0;
    }

    public static boolean loadFriend(int characterID, List<FriendInfo> friends) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT `FriendID`, `FriendName`, `Flag` FROM `friend` WHERE `CharacterID` = ?")) {
                ps.setInt(1, characterID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int flag = rs.getInt("Flag");
                        //if (flag == FriendStatus.Refused) {
                        //    continue;
                        //}
                        FriendInfo info = new FriendInfo();
                        info.setFriendID(rs.getInt("FriendID"));
                        info.setFriendName(rs.getString("FriendName"));
                        info.setFlag(flag);
                        friends.add(info);
                    }
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }

    public static boolean setFriend(int characterID, int friendID, String characterName, String friendName) {
        try (Connection con = Database.getDB().poolConnection()) {
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO `friend` (CharacterID, FriendID, FriendName, Flag) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE Flag = VALUES(Flag)")) {
                ps.setInt(1, characterID);
                ps.setInt(2, friendID);
                ps.setString(3, friendName);
                ps.setInt(4, FriendStatus.Normal);
                ps.executeUpdate();
                ps.setInt(1, friendID);
                ps.setInt(2, characterID);
                ps.setString(3, characterName);
                ps.setInt(4, FriendStatus.Request);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }

    public static int setAcceptFriend(int characterID, int friendID, String characterName) {
        try (Connection con = Database.getDB().poolConnection()) {
            // Doesn't properly handle insta accepting a friend you previously deleted. Read notes in onSetFriend
            try (PreparedStatement ps = con.prepareStatement("UPDATE `friend` SET Flag = ? WHERE CharacterID = ? AND FriendID = ?")) {
                ps.setInt(1, FriendStatus.Normal);
                ps.setInt(2, characterID);
                ps.setInt(3, friendID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("UPDATE `friend` SET Flag = ?, FriendName = ? WHERE CharacterID = ? AND FriendID = ?")) {
                ps.setInt(1, FriendStatus.Normal);
                ps.setString(2, characterName);
                ps.setInt(3, friendID);
                ps.setInt(4, characterID);
                ps.executeUpdate();
            }
            return FriendStatus.Normal;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return -1;
    }

    public static boolean deleteFriend(int characterID, int friendID) {
        try (Connection con = Database.getDB().poolConnection()) {
            /*try (PreparedStatement ps = con.prepareStatement("UPDATE `friend` SET Flag = ? WHERE CharacterID = ? AND FriendID = ?")) {
                ps.setInt(1, FriendStatus.Refused);
                ps.setInt(2, friendID);
                ps.setInt(3, characterID);
                ps.executeUpdate();
            }*/
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `friend` WHERE CharacterID = ? AND FriendID = ?")) {
                ps.setInt(1, characterID);
                ps.setInt(2, friendID);
                ps.executeUpdate();
                ps.setInt(1, friendID);
                ps.setInt(2, characterID);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
}
