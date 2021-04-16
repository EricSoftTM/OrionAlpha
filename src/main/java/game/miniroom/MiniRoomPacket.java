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
package game.miniroom;

/**
 *
 * @author sunnyboy
 * @author Eric
 */
public class MiniRoomPacket {
    public static final byte
            Create          = 0,
            CreateResult    = 1,
            Invite          = 2,//004AF450
            InviteResult    = 3,//004AF6E0
            Enter           = 4,//004AF9A0
            EnterResult     = 5,//004AF1F0
            Chat            = 6,//004AFC30
            Avatar          = 7,//004AFB50
            Leave           = 8,//004AFA60
            Balloon         = 9,//004DCB20
            // TradeRoomPacket
            PutItem_TR      = 10,//00522EF0
            PutMoney        = 11,//005230A0
            Trade           = 12,//00523120
            // PersonalShopPacket
            PutItem_PS      = 13,//004DD500
            BuyItem         = 14,//004DD0F0
            BuyResult       = 15,//004DCF00
            Refresh         = 16 //004DCCD0
    ;
}
