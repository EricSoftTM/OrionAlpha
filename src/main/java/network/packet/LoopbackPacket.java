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
            TemporaryStatReset = 14,
            ChangeSkillRecordResult = 15,
            SkillUseResult = 16,
            PartyResult = 17,
            DropPickUpMessage = 18,
            CashItemExpireMessage = 19,
            GivePopularityResult = 21,
            BroadcastMsg = 22,
            CharacterInfo = 23,
            PetInfo = 24,
            FriendResult = 25,
            END_CHARACTERDATA = 26,
            BEGIN_STAGE = 27,
            SetField = 28,
            SetCashShop = 29,
            END_STAGE = 30,
            BEGIN_FIELD = 31,
            TransferFieldReqIgnored = 32,
            TransferChannelReqIgnored = 33,
            Whisper = 34,
            BlowWeather = 35,
            GroupMessage = 36,
            Quiz = 37,
            Clock = 38,
            Desc = 39,
            AdminResult = -1,
            BEGIN_USERPOOL = 40,
            UserEnterField = 41,
            UserLeaveField = 42,
            BEGIN_USERCOMMON = 43,
            UserChat = 44,
            UserMiniRoomBalloon = 45,
            BEGIN_PET = 46,
            PetActivated = 47,
            PetMove = 48,
            PetAction = 49,
            PetActionCommand = 50,
            PetNameChanged = 51,
            PetEvol = 52,
            END_PET = 53,
            END_USERCOMMON = 54,
            BEGIN_USERREMOTE = 55,
            UserMove = 56,
            UserMeleeAttack = 57,
            UserShootAttack = 58,
            UserMagicAttack = 59,
            UserHit = 60,
            UserEmotion = 61,
            UserAvatarModified = 62,
            UserEffectRemote = 63,
            UserTemporaryStatSet = 64,
            UserTemporaryStatReset = 65,
            END_USERREMOTE = 66,
            BEGIN_USERLOCAL = 67,
            UserEffectLocal = 68,
            UserTeleport = 69,
            END_USERLOCAL = 70,
            END_USERPOOL = 71,
            BEGIN_MOBPOOL = 72,
            MobEnterField = 73,
            MobLeaveField = 74,
            MobChangeController = 75,
            BEGIN_MOB = 76,
            MobMove = 77,
            MobCtrlAck = 78,
            MobStatSet = 79,
            END_MOB = 80,
            END_MOBPOOL = 81,
            BEGIN_NPCPOOL = 82,
            NpcEnterField = 83,
            NpcLeaveField = 84,
            NpcChangeController = 85,
            BEGIN_NPC = 86,
            NpcMove = 87,
            END_NPC = 88,
            END_NPCPOOL = 89,
            BEGIN_DROPPOOL = 90,
            DropEnterField = 91,
            DropLeaveField = 92,
            END_DROPPOOL = 93,
            BEGIN_SCRIPT = 94,
            ScriptMessage = 95,
            END_SCRIPT = 96,
            BEGIN_SHOP = 97,
            OpenShopDlg = 98,
            ShopResult = 99,
            END_SHOP = 100,
            BEGIN_MESSENGER = 101,
            Messenger = 102,
            END_MESSENGER = 103,
            BEGIN_MINIROOM = 104,
            MiniRoom = 105,
            END_MINIROOM = 106,
            END_FIELD = 107,
            BEGIN_CASHSHOP = 108,
            CashShopChargeParamResult = 109,
            CashShopQueryCashResult = 111,
            CashShopCashItemResult = 112,
            END_CASHSHOP = 113,
            NO = 114
    ;
}
