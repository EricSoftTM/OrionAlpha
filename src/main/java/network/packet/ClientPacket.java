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
            AliveAck = 8,
            CheckSecurityThreadUpdated = 9,
            END_SOCKET = 10,
            BEGIN_USER = 11,
            UserTransferFieldRequest = 12,
            UserTransferChannelRequest = 13,
            UserMigrateToCashShopRequest = 14,
            UserMove = 15, 
            UserMeleeAttack = 16,
            UserShootAttack = 17,
            UserMagicAttack = 18,
            UserHit = 19,
            UserChat = 20,
            UserEmotion = 21,
            UserSelectNpc = 22,
            UserScriptMessageAnswer = 23,
            UserShopRequest = 24,
            UserChangeSlotPositionRequest = 25,
            UserStatChangeItemUseRequest = 26,
            UserPetFoodItemUseRequest = 27,
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
            UserPetInfoRequest = 39,
            UserActivatePetRequest = 40,
            BroadcastMsg = 41,
            Whisper = 42,
            Messenger = 43,
            MiniRoom = 44,
            PartyRequest = 45,
            PartyResult = 46,
            Admin = 47,
            FriendRequest = 48,
            BEGIN_PET = 49,
            PetMove = 50,
            PetActionRequest = 51,
            PetInteractionRequest = 52,
            END_PET = 53,
            END_USER = 54,
            BEGIN_FIELD = 55,
            BEGIN_LIFEPOOL = 56,
            BEGIN_MOB = 57,
            MobMove = 58,
            END_MOB = 59,
            BEGIN_NPC = 60,
            NpcMove = 61,
            END_NPC = 62,
            END_LIFEPOOL = 63,
            BEGIN_DROPPOOL = 64,
            DropPickUpRequest = 65,
            END_DROPPOOL = 66,
            END_FIELD = 67,
            BEGIN_CASHSHOP = 68,
            CashShopChargeParamRequest = 69,
            CashShopQueryCashRequest = 70,
            CashShopCashItemRequest = 71,
            END_CASHSHOP = 72,
            NO = 73
    ;
}
