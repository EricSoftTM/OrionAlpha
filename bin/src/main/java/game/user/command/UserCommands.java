package game.user.command;

import java.util.ArrayList;
import java.util.List;

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
import game.user.WvsContext;
import game.user.WvsContext.Request;
import game.user.item.ChangeLog;
import game.user.item.InventoryManipulator;
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;

/**
 * @author Arnah
*/
public class UserCommands {

    public static String level(User user, CharacterStat stat, String[] args) {
        if (args.length > 0) {
            stat.setLevel(Byte.parseByte(args[0]));
        } else {
            stat.setLevel((byte) 99);
        }
        user.sendCharacterStat(Request.Excl, CharacterStatType.LEV);
        return "Level has been set to " + stat.getLevel() + ".";
    }

    public static String fixme(User user, String[] args) {
        user.sendCharacterStat(Request.Excl, 0);
        return null;
    }
    
    public static String say(User user, Field field, String[] args) {
        if (args.length > 0) {
            String text = args[0];
            
            // TODO: Multi-channel broadcasting support.
            field.splitSendPacket(user.getSplit(), FieldPacket.onGroupMessage(user.getCharacterName(), text), null);
        }
        return "!say <message>";
    }

    public static String job(User user, CharacterStat stat, String[] args) {
        if (args.length > 0) {
            stat.setJob(Short.parseShort(args[0]));
            user.sendCharacterStat(Request.Excl, CharacterStatType.Job);
            return null;
        }
        return "!job <jobid>";
    }

    public static String packet(User user, String[] args) {
        //user.sendPacket(WvsContext.onIncEXPMessage(1));
        user.sendPacket(FieldPacket.onTransferFieldReqIgnored());
        return null;
    }
    
    public static String weather(User user, Field field, String[] args) {
        if (args.length > 0) {
            String text = args[0];
            
            field.splitSendPacket(user.getSplit(), FieldPacket.onBlowWeather(2090000, text), null);
        }
        return "!weather <message>";
    }

    public static String sp(User user, CharacterStat stat, String[] args) {
        if (args.length > 0) {
            stat.setSP(Short.parseShort(args[0]));
            user.sendCharacterStat(Request.Excl, CharacterStatType.SP);
            return null;
        }
        return "!sp <amount>";
    }

    public static String item(User user, CharacterData cd, DropPool pool, String[] args) {
        return spawnItem("item", user, cd, pool, args);
    }

    public static String drop(User user, CharacterData cd, DropPool pool, String[] args) {
        return spawnItem("drop", user, cd, pool, args);
    }

    private static String spawnItem(String alias, User user, CharacterData cd, DropPool pool, String[] args) {
        if (alias.equals("drop") || alias.equals("item")) {
            if (args.length > 0) {
                int itemID = Integer.parseInt(args[0]);
                ItemSlotBase item = ItemInfo.getItemSlot(itemID, ItemVariationOption.Normal);
                String itemName = ItemInfo.getItemName(itemID);
                if (itemName.isEmpty())
                    itemName = String.valueOf(itemID);
                if (item != null) {
                    if (item.getType() == ItemSlotType.Bundle) {
                        item.setItemNumber(ItemInfo.getBundleItem(itemID).getSlotMax());
                    }
                    if (alias.equals("item")) {
                        List<ChangeLog> changeLog = new ArrayList<>();
                        InventoryManipulator.rawAddItem(cd, ItemAccessor.getItemTypeIndexFromID(itemID), item,
                                changeLog, null);
                        user.sendPacket(InventoryManipulator.makeInventoryOperation(Request.None, changeLog));
                        user.addCharacterDataMod(
                                ItemAccessor.getItemTypeFromTypeIndex(ItemAccessor.getItemTypeIndexFromID(itemID)));
                    } else {
                        Reward reward = new Reward(RewardType.Item, item, 0, 0);
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
