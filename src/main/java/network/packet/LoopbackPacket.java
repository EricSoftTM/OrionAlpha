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
package network.packet;

/**
 *
 * @author Eric
 */
public class LoopbackPacket {
    public static final byte
            BEGIN_SOCKET = 0,
            CheckPasswordResult = 1,
            SelectWorldResult = 2,
            SelectCharacterResult = 3,
            CheckDuplicatedIDResult = 4,
            CreateNewCharacterResult = 5,
            DeleteCharacterResult = 6,
            MigrateCommand = 7,
            AliveReq = 8,
            END_SOCKET = 9,
            BEGIN_CHARACTERDATA = 10,
            InventoryOperation = 11,
            InventoryGrow = 12,
            // Both StatChanged and TemporaryStatSet are in one function in 1.23
            StatChanged = 13,
            TemporaryStatSet = 13,
            ChangeSkillRecordResult = 14,
            SkillUseResult = 15,
            PartyResult = 16,
            DropPickUpMessage = 17,
            GivePopularityResult = 18,
            BroadcastMsg = 19,
            END_CHARACTERDATA = 20,
            BEGIN_STAGE = 21,
            SetField = 22,
            SetCashShop = -1, // TODO: Remove
            END_STAGE = 23,
            BEGIN_FIELD = 24,
            TransferFieldReqIgnored = 25,
            Whisper = 26,
            BEGIN_USERPOOL = 27,
            UserEnterField = 28,
            UserLeaveField = 29,
            BEGIN_USERCOMMON = 30,
            UserChat = 31,
            END_USERCOMMON = 32,
            BEGIN_USERREMOTE = 33,
            UserMove = 34,
            UserMeleeAttack = 35,
            UserShootAttack = 36,
            UserMagicAttack = 37,
            UserHit = 38,
            UserEmotion = 39,
            UserAvatarModified = 40,
            UserEffectRemote = 41,
            END_USERREMOTE = 42,
            BEGIN_USERLOCAL = 43,
            END_USERLOCAL = 44,
            END_USERPOOL = 45,
            BEGIN_MOBPOOL = 46,
            MobEnterField = 47,
            MobLeaveField = 48,
            MobChangeController = 49,
            BEGIN_MOB = 50,
            MobMove = 51,
            MobCtrlAck = 52,
            MobStatSet = 53,
            END_MOB = 54,
            END_MOBPOOL = 55,
            BEGIN_NPCPOOL = 56,
            NpcEnterField = 57,
            NpcLeaveField = 58,
            NpcChangeController = 59,
            BEGIN_NPC = 60,
            NpcMove = 61,
            BEGIN_NPCTEMPLATE = 62,
            END_NPCTEMPLATE = 63,
            END_NPC = 64,
            END_NPCPOOL = 65,
            BEGIN_DROPPOOL = 66,
            DropEnterField = 67,
            DropLeaveField = 68,
            END_DROPPOOL = 69,
            BEGIN_SCRIPT = 70,
            ScriptMessage = 71,
            END_SCRIPT = 72,
            BEGIN_SHOP = 73,
            OpenShopDlg = 74,
            ShopResult = 75,
            END_SHOP = 76,
            BEGIN_MESSENGER = 77,
            Messenger = 78,
            END_MESSENGER = 79,
            BEGIN_MINIROOM = 80,
            MiniRoom = 81,
            END_MINIROOM = 82,
            END_FIELD = 83,
            BEGIN_CASHSHOP = -1, // TODO: Remove
            CashShopChargeParamResult = -1, // TODO: Remove
            CashShopQueryCashResult = -1, // TODO: Remove
            CashShopCashItemResult = -1, // TODO: Remove
            END_CASHSHOP = -1, // TODO: Remove
            NO = 84
    ;
    
    public static boolean filterForLog(byte type) {
        switch (type) {
            case AliveReq:
            case UserMove:
            case NpcMove:
            case MobMove:
                return false;
        }
        return true;
    }
}
