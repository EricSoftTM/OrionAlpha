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
    public static final short
            BEGIN_SOCKET = 0,
            CheckPassword = 1,
            SelectWorld = 2,
            SelectCharacter = 3,
            MigrateIn = 4,
            CheckDuplicatedID = 5,
            CreateNewCharacter = 6,
            DeleteCharacter = 7,
            AliveAck = 8,
            Unknown9 = 9, // 0058B285
            ExceptionLog = 10, // 0058B3B0
            END_SOCKET = 11,
            BEGIN_USER = 12,
            UserTransferFieldRequest = 13,
            UserMigrateToCashShopRequest = 14,
            UserMove = 15, 
            UserMeleeAttack = 16,
            UserShootAttack = 17,
            UserMagicAttack = 18,
            Unknown19 = 19,
            UserHit = 20, 
            UserChat = 21,
            UserEmotion = 22,
            UserSelectNpc = 23,
            UserScriptMessageAnswer = 24,
            Unknown25 = 25, // 004D4C34, 004D7CC5
            UserChangeSlotPositionRequest = 26, // 00593477
            Unknown27 = 27, // 00593A97
            UserShopRequest = 28, // 00594043
            Unknown29 = 29, // 00594187
            Unknown30 = 30, // 005938B6
            UserAbilityUpRequest = 31, // 00596679
            UserChangeStatRequest = 32, // 00594530
            UserSkillUpRequest = 33, // 00596859
            UserSkillUseRequest = 34, // 00548191
            UserDropMoneyRequest = 35,
            UserGivePopularityRequest = 36, // 00596C14
            UserPartyRequest = 37, 
            UserCharacterInfoRequest = 38, // 00596DDA
            Unknown39 = 39, // 00486F0C, command-related?
            Whisper = 40, // 004879AA
            Messenger = 41, // 00503603
            MiniRoom = 42, // 00487C97, 0049CBFD, 0049CD63, 0049D4E2, 004F8005, 004FACDA, 004FB011, 004FB1A8, 
            Unknown43 = 43, // 00487F0F, 00595858
            Unknown44 = 44, // 00595755, 005958CA
            Unknown45 = 45, // 00486B38, command-related?
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
            CashShopChargeParamRequest = 61, // 0042E454
            CashShopQueryCashRequest = 62, // 0042F4ED
            CashShopCashItemRequest = 63, // 0042F160, 0042F38C, 0042DEBF
            END_CASHSHOP = 64,
            NO = 65
    ;
}
