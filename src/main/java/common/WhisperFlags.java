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
package common;

/**
 *
 * @author Eric
 */
public class WhisperFlags {
    public class WhisperFlag {
        public static final byte
                Location    = 0x1,
                Whisper     = 0x2,
                Request     = 0x4,
                Result      = 0x8,
                Receive     = 0x10,
                Blocked     = 0x20
        ;
    }
    public class LocationResult {
        public static final byte
                None            = 0,
                GameSvr         = 1,
                ShopSvr         = 2,
                OtherChannel    = 3,
                Admin           = 4
        ;
    }
    
    public static final byte
            FindRequest       = WhisperFlag.Request | WhisperFlag.Location,
            ReplyRequest      = WhisperFlag.Request | WhisperFlag.Whisper,
            
            FindResult        = WhisperFlag.Result  | WhisperFlag.Location,
            ReplyResult       = WhisperFlag.Result  | WhisperFlag.Whisper,
            ReplyReceive      = WhisperFlag.Receive | WhisperFlag.Whisper,
            BlockedResult     = WhisperFlag.Blocked | WhisperFlag.Whisper,
            
            None = -1
    ;
}
