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
package network;

import common.OrionConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.nio.ByteOrder;
import java.util.List;
import network.packet.InPacket;
import network.security.XORCrypter;
import util.Logger;
import util.Pointer;
import util.Utilities;

/**
 * The server-end networking decoder. 
 * Receives incoming socket buffer lists from the remote socket.
 * 
 * @author Eric
 */
public class SocketDecoder extends ReplayingDecoder<Void> {
    private final XORCrypter cipher;
    
    public SocketDecoder(XORCrypter cipher) {
        this.cipher = cipher;
    }
    
    class RecvData {
        /* The maximum incoming data buffer sizes */
        class MaxSize {
            static final int
                    Client = 0x1000,
                    Center = 0x8000
            ;
        }
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Pointer<Integer> lastState = new Pointer<>(0);
        InPacket packet = new InPacket();
        ByteBuf buff;
        int state;
        
        buff = in.readBytes(4);
        try {
            state = packet.appendBuffer(buff, lastState);
            if (state > 0 && lastState.get() <= 0) {
                if (packet.decodeSeqBase(cipher.getSeqRcv()) != OrionConfig.CLIENT_VER) {
                    Logger.logError("Incorrect packet header sequencing");
                    ctx.disconnect();
                    return;
                }
                if (packet.getDataLen() > RecvData.MaxSize.Client) {
                    Logger.logError("Received packet length overflow");
                    ctx.disconnect();
                    return;
                }
            }
        } finally {
            buff.release();
        }
        buff = in.readBytes(packet.getDataLen());
        try {
            state = packet.appendBuffer(buff, lastState);
            if (state == 2) {
                if (!packet.decryptData(cipher)) {
                    Logger.logError("DecryptData Failed");
                    cipher.updateSeqRcv();
                    return;
                }
                cipher.updateSeqRcv();
                out.add(packet);
            }
        } finally {
            buff.release();
        }
    }
}
