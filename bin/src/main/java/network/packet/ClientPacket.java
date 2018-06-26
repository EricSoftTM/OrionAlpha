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
public class ClientPacket {
    public static final byte
            BEGIN_SOCKET = 0,
            CheckPassword = 1,
            SelectWorld = 2,
            SelectCharacter = 3,
            MigrateIn = 4,
            CheckDuplicatedID = 5,
            CreateNewCharacter = 6,
            DeleteCharacter = 7,
            // Both 0x8 and 0x9 are their own "AliveAck".. 
            // Maybe one is an acknowledge and the other is a request? idk
            AliveAck = 8,
            AliveAck2 = 9, // 0058B285
            ExceptionLog = 10,
            END_SOCKET = 11,
            BEGIN_USER = 12,
            UserTransferFieldRequest = 13,
            UserMigrateToCashShopRequest = 14,
            UserMove = 15, 
            UserMeleeAttack = 16,
            UserShootAttack = 17,
            UserMagicAttack = 18,
            Unknown = 19, // Not sure if this even exists in the client?
            UserHit = 20, 
            UserChat = 21,
            UserEmotion = 22,
            UserSelectNpc = 23,
            UserScriptMessageAnswer = 24,
            UserShopRequest = 25, 
            UserChangeSlotPositionRequest = 26,
            UserStatChangeItemUseRequest = 27,
            UserConsumeCashItemUseRequest = 28,
            UserPortalScrollUseRequest = 29,
            UserUpgradeItemUseRequest = 30, 
            UserAbilityUpRequest = 31,
            UserChangeStatRequest = 32,
            UserSkillUpRequest = 33,
            UserSkillUseRequest = 34,
            UserDropMoneyRequest = 35,
            UserGivePopularityRequest = 36,
            UserPartyRequest = 37, 
            UserCharacterInfoRequest = 38,
            BroadcastMsg = 39,
            Whisper = 40,
            Messenger = 41,
            MiniRoom = 42,
            PartyRequest = 43, // 00487F0F, 00595858
            PartyResult = 44, // 00595755, 005958CA
            Admin = 45,
            END_USER = 46,
            BEGIN_FIELD = 47,
            BEGIN_LIFEPOOL = 48,
            BEGIN_MOB = 49,
            MobMove = 50,
            END_MOB = 51,
            BEGIN_NPC = 52,
            NpcMove = 53,
            END_NPC = 54,
            END_LIFEPOOL = 55,
            BEGIN_DROPPOOL = 56,
            DropPickUpRequest = 57,
            END_DROPPOOL = 58,
            END_FIELD = 59,
            BEGIN_CASHSHOP = 60,
            CashShopChargeParamRequest = 61,
            CashShopQueryCashRequest = 62,
            CashShopCashItemRequest = 63,
            END_CASHSHOP = 64,
            NO = 65
    ;
}
