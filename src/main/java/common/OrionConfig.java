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
 * Various Configuration/Information
 *
 * @author Eric
 */
public class OrionConfig {
    // Version Info
    public static final int CLIENT_VER = 20;
    public static final String CLIENT_PATCH = "\0";
    public static final int GAME_LOCALE = 3; //JP

    // Maximum Active Network Sessions
    public static final int MAX_CONNECTIONS = 1000;

    // Master Password
    public static final char[] MASTER_PASSWORD = "$2a$10$SdXGImegkpvdeHohjas29.ZBR7LwmvpXxNBy..chrmXiMFqyntJZm".toCharArray();

    // Log Packets
    public static final boolean LOG_PACKETS = false;
}
