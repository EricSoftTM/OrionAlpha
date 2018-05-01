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

import common.OrionConfig;
import java.util.List;
import login.avatar.Avatar;
import login.user.client.ClientSocket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class LoginPacket {
    
    public static OutPacket onCheckPasswordResult(ClientSocket socket, int result) {
        OutPacket packet = new OutPacket(LoopbackPacket.CheckPasswordResult);
        packet.encodeByte(result);
        if (result == 1) {
            packet.encodeInt(socket.getAccountID());
            packet.encodeByte(socket.getGender());
            packet.encodeByte(socket.getGradeCode());
            packet.encodeString(socket.getNexonClubID());
            packet.encodeByte(LoginApp.getInstance().getWorlds().size());
            for (WorldEntry world : LoginApp.getInstance().getWorlds()) {
                packet.encodeByte(world.getChannels().size());
                for (ChannelEntry channel : world.getChannels()) {
                    packet.encodeString(world.getName() + "-" + channel.getChannelID());
                    packet.encodeInt(channel.getUserNo());
                    packet.encodeByte(channel.getWorldID());
                    packet.encodeByte(channel.getChannelID());
                }
            }
        }
        return packet;
    }
    
    public static OutPacket onSelectWorldResult(int msg, List<Avatar> avatars) {
        OutPacket packet = new OutPacket(LoopbackPacket.SelectWorldResult);
        packet.encodeByte(msg);
        if (msg == 1) {
            packet.encodeInt(0); // Unknown
            
            packet.encodeByte(avatars.size());
            for (Avatar avatar : avatars) {
                avatar.encode(packet);
            }
        }
        return packet;
    }
}
