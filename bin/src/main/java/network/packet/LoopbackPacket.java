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
            // Unknown = 17
            DropPickUpMessage = 18,
            IncEXPMessage = 19,
            // Unknown = 20
            BroadcastMsg = 21,
            CharacterInfo = 22,
            END_CHARACTERDATA = 23,
            BEGIN_STAGE = 24,
            SetField = 25,
            SetCashShop = 26,
            END_STAGE = 27,
            BEGIN_FIELD = 28,
            TransferFieldReqIgnored = 29,
            Whisper = 30,
            BlowWeather = 31,
            GroupMessage = 32,
            // Unknown = 33
            BEGIN_USERPOOL = 34,
            UserEnterField = 35,
            UserLeaveField = 36,
            BEGIN_USERCOMMON = 37,
            UserChat = 38,
            END_USERCOMMON = 39,
            BEGIN_USERREMOTE = 40,
            UserMove = 41,
            UserMeleeAttack = 42,
            UserShootAttack = 43,
            UserMagicAttack = 44,
            UserHit = 45,
            UserEmotion = 46,
            UserAvatarModified = 47,
            UserEffectRemote = 48,
            UserTemporaryStatSet = 49,
            UserTemporaryStatReset = 50,
            END_USERREMOTE = 51,
            BEGIN_USERLOCAL = 52,
            UserEffectLocal = 53,
            END_USERLOCAL = 54,
            END_USERPOOL = 55,
            BEGIN_MOBPOOL = 56,
            MobEnterField = 57,
            MobLeaveField = 58,
            MobChangeController = 59,
            BEGIN_MOB = 60,
            MobMove = 61,
            MobCtrlAck = 62,
            MobAffected = 63,
            END_MOB = 64,
            END_MOBPOOL = 65,
            BEGIN_NPCPOOL = 66,
            NpcEnterField = 67,
            NpcLeaveField = 68,
            NpcChangeController = 69,
            BEGIN_NPC = 70,
            NpcMove = 71,
            BEGIN_NPCTEMPLATE = 72,
            END_NPCTEMPLATE = 73,
            END_NPC = 74,
            END_NPCPOOL = 75,
            BEGIN_DROPPOOL = 76,
            DropEnterField = 77,
            DropLeaveField = 78,
            END_DROPPOOL = 79,
            BEGIN_SCRIPT = 80,
            ScriptMessage = 81,
            END_SCRIPT = 82,
            BEGIN_SHOP = 83,
            OpenShopDlg = 84,
            ShopResult = 85,
            END_SHOP = 86,
            BEGIN_MESSENGER = 87,
            Messenger = 88,
            END_MESSENGER = 89,
            BEGIN_MINIROOM = 90,
            MiniRoom = 91,
            END_MINIROOM = 92,
            END_FIELD = 93,
            BEGIN_CASHSHOP = 94,
            CashShopChargeParamResult = 95,
            CashShopQueryCashResult = 96,
            CashShopCashItemResult = 97,
            END_CASHSHOP = 98,
            NO = 99
    ;
}
