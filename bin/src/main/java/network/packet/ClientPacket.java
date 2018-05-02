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
            Unknown10 = 10, // 0058B3B0
            END_SOCKET = 11,
            BEGIN_USER = 12,
            UserTransferFieldRequest = 13,
            UserMigrateToCashShopRequest = 14,
            UserMove = 15, // 00574639
            UserMeleeAttack = 16,
            UserShootAttack = 17,
            UserMagicAttack = 18,
            // missing 19
            UserHit = 20, // 005436CB
            UserChat = 21,
            UserEmotion = 22,
            UserSelectNpc = 23,
            UserScriptMessageAnswer = 24,
            Unknown25 = 25, // 004D4C34, 004D7CC5
            Unknown26 = 26, // 00593477
            Unknown27 = 27, // 00593A97
            UserShopRequest = 28, // 00594043
            Unknown29 = 29, // 00594187
            Unknown30 = 30, // 005938B6
            Unknown31 = 31, // 00596679
            UserChangeStatRequest = 32, // 00594530
            UserSkillUpRequest = 33, // 00596859
            UserSkillUseRequest = 34, // 00548191
            Unknown35 = 35, // 005969E7
            Unknown36 = 36, // 00596C14
            // missing 37
            Unknown38 = 38, // 00596DDA
            Unknown39 = 39, // 00486F0C, command-related?
            Whisper = 40, // 004879AA
            Messenger = 41, // 00503603
            MiniRoom = 42, // 00487C97, 0049CBFD, 0049CD63, 0049D4E2, 004F8005, 004FACDA, 004FB011, 004FB1A8, 
            Unknown43 = 43, // 00487F0F, 00595858
            Unknown44 = 44, // 00595755, 005958CA
            Unknown45 = 45, // 00486B38, command-related?
            // missing 46, 47, 48, 49
            Unknown50 = 50, // 004A4DA5, might be mob movement
            // missing 51, 52
            Unknown53 = 53, // 004B9617
            // missing 54, 55, 56
            Unknown57 = 57, // 005936EB
            // missing 58, 59, 60
            Unknown61 = 61, // 0042E454
            Unknown62 = 62, // 0042F4ED
            Unknown63 = 63 // multiple structures based on initial byte (0042F160, 0042F38C, 0042DEBF)
    ;
}
