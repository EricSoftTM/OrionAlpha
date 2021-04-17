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
package game.friend;

/**
 *
 * @author Eric
 */
public class FriendResCode {
	public static final byte
			// FriendReq
			LoadFriend                  = 0,
			SetFriend                   = 1,
			AcceptFriend                = 2,
			DeleteFriend                = 3,
			NotifyLogin                 = 4,
			NotifyLogout                = 5,
			// FriendRes
			LoadFriend_Done             = 6,
			Invite                      = 7,
			SetFriend_Done              = 8,
			SetFriend_FullMe            = 9,
			SetFriend_FullOther         = 10,
			SetFriend_AlreadySet        = 11,
			SetFriend_UnknownUser       = 12,
			SetFriend_Unknown           = 13,
			AcceptFriend_Unknown        = 14,
			DeleteFriend_Done           = 15,
			DeleteFriend_Unknown        = 16,
			Notify                      = 17
	;
}
