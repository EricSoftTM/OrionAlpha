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
            GivePopularityResult = 19,
            BroadcastMsg = 20,
            CharacterInfo = 21,
            END_CHARACTERDATA = 22,
            BEGIN_STAGE = 23,
            SetField = 24,
            SetCashShop = 25,
            END_STAGE = 26,
            BEGIN_FIELD = 27,
            TransferFieldReqIgnored = 28,
            Whisper = 29,
            BlowWeather = 30,
            GroupMessage = 31,
            BEGIN_USERPOOL = 32,
            UserEnterField = 33,
            UserLeaveField = 34,
            BEGIN_USERCOMMON = 35,
            UserChat = 36,
            END_USERCOMMON = 37,
            BEGIN_USERREMOTE = 38,
            UserMove = 39,
            UserMeleeAttack = 40,
            UserShootAttack = 41,
            UserMagicAttack = 42,
            UserHit = 43,
            UserEmotion = 44,
            UserAvatarModified = 45,
            UserEffectRemote = 46,
            UserTemporaryStatSet = 47,
            UserTemporaryStatReset = 48,
            END_USERREMOTE = 49,
            BEGIN_USERLOCAL = 50,
            UserEffectLocal = 51,
            END_USERLOCAL = 52,
            END_USERPOOL = 53,
            BEGIN_MOBPOOL = 54,
            MobEnterField = 55,
            MobLeaveField = 56,
            MobChangeController = 57,
            BEGIN_MOB = 58,
            MobMove = 59,
            MobCtrlAck = 60,
            MobStatSet = 61,
            END_MOB = 62,
            END_MOBPOOL = 63,
            BEGIN_NPCPOOL = 64,
            NpcEnterField = 65,
            NpcLeaveField = 66,
            NpcChangeController = 67,
            BEGIN_NPC = 68,
            NpcMove = 69,
            BEGIN_NPCTEMPLATE = 70,
            END_NPCTEMPLATE = 71,
            END_NPC = 72,
            END_NPCPOOL = 73,
            BEGIN_DROPPOOL = 74,
            DropEnterField = 75,
            DropLeaveField = 76,
            END_DROPPOOL = 77,
            BEGIN_SCRIPT = 78,
            ScriptMessage = 79,
            END_SCRIPT = 80,
            BEGIN_SHOP = 81,
            OpenShopDlg = 82,
            ShopResult = 83,
            END_SHOP = 84,
            BEGIN_MESSENGER = 85,
            Messenger = 86,
            END_MESSENGER = 87,
            BEGIN_MINIROOM = 88,
            MiniRoom = 89,
            END_MINIROOM = 90,
            END_FIELD = 91,
            BEGIN_CASHSHOP = 92,
            CashShopChargeParamResult = 93,
            CashShopQueryCashResult = 94,
            CashShopCashItemResult = 95,
            END_CASHSHOP = 96,
            NO = 97
    ;
}
