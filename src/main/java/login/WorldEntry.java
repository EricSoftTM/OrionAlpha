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

import java.util.ArrayList;
import java.util.List;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class WorldEntry {
    private final byte worldID;
    private final String name;
    private final List<ChannelEntry> channels;
    private final GameSocket socket;
    
    public WorldEntry(GameSocket socket, byte worldID, String name) {
        this.socket = socket;
        this.worldID = worldID;
        this.name = name;
        this.channels = new ArrayList<>();
    }
    
    public void addChannel(ChannelEntry ch) {
        channels.add(ch);
    }
    
    public ChannelEntry getChannel(int channel) {
        return channels.get(channel);
    }
    
    public final List<ChannelEntry> getChannels() {
        return channels;
    }
    
    public final GameSocket getSocket() {
        return socket;
    }
    
    public String getName() {
        return name;
    }
    
    public byte getWorldID() {
        return worldID;
    }
    
    public void removeChannel(ChannelEntry ch) {
        channels.remove(ch);
    }
    
    public void sendPacket(OutPacket packet) {
        if (socket != null) {
            socket.sendPacket(packet, false);
        }
    }
}
