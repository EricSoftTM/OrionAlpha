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
            InspectFileCrc = 9, // 00554661
            ExceptionLog = 10,
            END_SOCKET = 11,
            BEGIN_USER = 12,
            UserTransferFieldRequest = 13,
            UserMigrateToCashShopRequest = -14, // TODO: Remove
            UserMove = 14,
            UserMeleeAttack = 15,
            UserShootAttack = 16,
            UserMagicAttack = 17,
            UserChat = 18,
            UserEmotion = 19,
            UserSelectNpc = 20,
            UserScriptMessageAnswer = 21,
            UserShopRequest = 22,
            UserChangeSlotPositionRequest = 23,
            UserStatChangeItemUseRequest = 24,
            UserPortalScrollUseRequest = 25,
            UserUpgradeItemUseRequest = 26,
            UserAbilityUpRequest = 27,
            UserChangeStatRequest = 28,
            UserSkillUpRequest = 29,
            UserSkillUseRequest = 30,
            UserDropMoneyRequest = 31,
            UserGivePopularityRequest = 32,
            UserPartyRequest = 33,
            BroadcastMsg = 34,
            Whisper = 35,
            Messenger = 36,
            MiniRoom = 37,
            PartyRequest = 38,
            Admin = 39,
            END_USER = 40,
            BEGIN_FIELD = 41,
            BEGIN_LIFEPOOL = 42,
            BEGIN_MOB = 43,
            MobMove = 44,
            END_MOB = 45,
            BEGIN_NPC = 46,
            NpcMove = 47,
            END_NPC = 48,
            END_LIFEPOOL = 49,
            BEGIN_DROPPOOL = 50,
            DropPickUpRequest = 51,
            END_DROPPOOL = 52,
            END_FIELD = 53,
            BEGIN_CASHSHOP = -60, // TODO: Remove
            CashShopChargeParamRequest = -61, // TODO: Remove
            CashShopQueryCashRequest = -62, // TODO: Remove
            CashShopCashItemRequest = -63, // TODO: Remove
            END_CASHSHOP = -64, // TODO: Remove
            NO = 54
    ;
    
    public static boolean filterForLog(byte type) {
        switch (type) {
            case AliveAck:
            case UserMove:
            case NpcMove:
            case MobMove:
                return false;
        }
        return true;
    }
}
