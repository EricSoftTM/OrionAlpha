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
package login;

import java.util.HashMap;
import java.util.Map;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class ShopEntry {
    private final String addr;
    private final short port;
    private final GameSocket socket;
    private final Map<Integer, ChannelEntry> users;
    
    public ShopEntry(GameSocket socket, String addr, short port) {
        this.socket = socket;
        this.addr = addr;
        this.port = port;
        this.users = new HashMap<>();
    }
    
    public String getAddr() {
        return addr;
    }
    
    public short getPort() {
        return port;
    }
    
    public final GameSocket getSocket() {
        return socket;
    }
    
    public Map<Integer, ChannelEntry> getUsers() {
        return users;
    }
    
    public void sendPacket(OutPacket packet) {
        if (socket != null) {
            socket.sendPacket(packet, false);
        }
    }
}
