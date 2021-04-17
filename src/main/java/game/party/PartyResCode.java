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
package game.party;

/**
 *
 * @author Eric
 */
public class PartyResCode {
    public static final byte
            // PartyReq
            LoadParty                       = 0,
            CreateNewParty                  = 1,
            WithdrawParty                   = 2,
            JoinParty                       = 3,
            InviteParty                     = 4,
            KickParty                       = 5,
            // PartyRes
            LoadParty_Done                  = 6,
            CreateNewParty_Done             = 7,
            CreateNewParty_AlreadyJoined    = 8,
            CreateNewParty_Beginner         = 9,
            CreateNewParty_Unknown          = 10,
            WithdrawParty_Done              = 11,
            WithdrawParty_NotJoined         = 12,
            WithdrawParty_Unknown           = 13,
            JoinParty_Done                  = 14,
            JoinParty_AlreadyJoined         = 15,
            JoinParty_OverDesiredSize       = 16,
            JoinParty_UnknownUser           = 17,
            JoinParty_Unknown               = 18,
            InviteParty_Rejected            = 19
    ;
}
