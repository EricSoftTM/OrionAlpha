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
package game.user.command;

import common.Request;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemSlotType;
import common.user.CharacterData;
import common.user.CharacterStat;
import common.user.CharacterStat.CharacterStatType;
import game.field.Field;
import game.field.FieldPacket;
import game.field.drop.DropPool;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.user.User;
import game.user.item.ChangeLog;
import game.user.item.InventoryManipulator;
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;
import util.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arnah
*/
public class GMCommands {

    @CommandDesc("Clear drops on the map.")
    public static String clear(User user, Field field) {
        field.expireDrops(user);
        return null;
    }

    public static String sp(User user, CharacterStat stat, String[] args) {
        if (args.length > 0) {
            stat.setSP(Short.parseShort(args[0]));
            user.sendCharacterStat(Request.Excl, CharacterStatType.SP);
            return null;
        }
        return "!sp <amount>";
    }

    public static String ap(User user, CharacterStat stat, String[] args) {
        if (args.length > 0) {
            stat.setAP(Short.parseShort(args[0]));
            user.sendCharacterStat(Request.Excl, CharacterStatType.AP);
            return null;
        }
        return "!ap <amount>";
    }

    public static String map(User user, String[] args) {
        if (args.length > 0) {
            user.postTransferField(Integer.parseInt(args[0]), "", false);
            return null;
        }
        return "!map <mapid>";
    }

    @CommandDesc("Updates the level of your character")
    public static String level(User user, CharacterStat stat, String[] args) {
        if (args.length > 0) {
            stat.setLevel(Byte.parseByte(args[0]));
            user.sendCharacterStat(Request.Excl, CharacterStatType.LEV);
            return null;
        }
        return "!level <level>";
    }

    @CommandDesc("Sets the amount of mesos in your inventory")
    public static String meso(User user, String[] args) {
        if (args.length > 0) {
            user.incMoney(Integer.parseInt(args[0]), true);
            user.sendCharacterStat(Request.Excl, CharacterStatType.Money);
            return null;
        }
        return "!meso <amount>";
    }

    @CommandDesc("Changes your job")
    public static String job(User user, CharacterStat stat, String[] args) {
        if (args.length > 0) {
            stat.setJob(Short.parseShort(args[0]));
            user.sendCharacterStat(Request.Excl, CharacterStatType.Job);
            return null;
        }
        return "!job <jobid>";
    }

    public static String heal(User user, CharacterStat stat) {
        stat.setHP(stat.getMHP());
        stat.setMP(stat.getMMP());
        user.sendCharacterStat(Request.Excl, CharacterStatType.HP | CharacterStatType.MP);
        return null;
    }

    @CommandDesc("Broadcasts snowing weather with a message to the map")
    public static String weather(User user, Field field, String[] args) {
        if (args.length > 0) {
            String text = Utilities.joinStringFrom(args, 0);
            field.onWeather(2090000, text, 60);
            return null;
        }
        return "!weather <message>";
    }

    @CommandDesc("Broadcasts your message")
    public static String say(User user, String[] args) {
        if (args.length > 0) {
            String text = Utilities.joinStringFrom(args, 0);

            user.getChannel().broadcast(FieldPacket.onGroupMessage(user.getCharacterName(), text));
            return null;
        }
        return "!say <message>";
    }

    @CommandAlias("drop")
    @CommandDesc("Spawns items either in your inventory or on the ground")
    public static String item(String alias, User user, CharacterData cd, DropPool pool, String[] args) {
        if (alias.equals("drop") || alias.equals("item")) {
            if (args.length > 0) {
                int itemID = Integer.parseInt(args[0]);
                ItemSlotBase item = ItemInfo.getItemSlot(itemID, ItemVariationOption.Normal);
                String itemName = ItemInfo.getItemName(itemID);
                if (itemName == null || itemName.isEmpty())
                    itemName = String.valueOf(itemID);
                if (item != null) {
                    if (item.getType() == ItemSlotType.Bundle) {
                        int quantity = 1;
                        if (args.length > 1) {
                            quantity = Math.max(1, Math.min(Integer.parseInt(args[1]), ItemInfo.getBundleItem(itemID).getSlotMax()));
                        }
                        item.setItemNumber(quantity);
                    }
                    if (alias.equals("item")) {
                        List<ChangeLog> changeLog = new ArrayList<>();
                        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
                        InventoryManipulator.rawAddItem(cd, ti, item, changeLog, null);
                        user.sendPacket(InventoryManipulator.makeInventoryOperation(Request.None, changeLog));
                        user.addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                    } else {
                        Reward reward = new Reward(RewardType.ITEM, item, 0, 0);
                        pool.create(reward, user.getCharacterID(), 0, user.getCurrentPosition().x,
                                user.getCurrentPosition().y, user.getCurrentPosition().x, 0, 0, user.isGM(), 0);
                    }
                    return "The item (" + itemName + ") has been successfully created.";
                } else {
                    return "The Item (" + itemName + ") does not exist.";
                }
            }
        }
        return "!item <itemid>";
    }
}
