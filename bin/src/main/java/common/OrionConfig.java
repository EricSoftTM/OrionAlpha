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
    // Server Name
    public static final String SERVER_NAME = "OrionAlpha";
    
    // Game Server Connection Info
    public static final String GAME_ADDR = "127.0.0.1";
    public static final int GAME_PORT = 7575;
    
    // Login Server Connection Info
    public static final String LOGIN_ADDR = "127.0.0.1";
    public static final int LOGIN_PORT = 8484;
    
    // Version Info
    public static final int CLIENT_VER = 223;
    public static final String CLIENT_PATCH = "\0";
    public static final int GAME_LOCALE = 1; //KR
    
    // Maximum Active Network Sessions
    public static final int MAX_CONNECTIONS = 1000;
    
    // Master Password
    public static final String MASTER_PASSWORD = "$2a$10$SdXGImegkpvdeHohjas29.ZBR7LwmvpXxNBy..chrmXiMFqyntJZm";
    
    // Log Packets
    public static final boolean LOG_PACKETS = true;
}
