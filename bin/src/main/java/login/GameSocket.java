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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.InPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class GameSocket extends SimpleChannelInboundHandler {
    public final Lock lockSend;
    //packetRecv
    //packetLoopback
    public WorldEntry pServer;
    public boolean bClosePosted;
    public boolean bAliveReqSended;
    public long tLastAliveAckRcvTime;
    public long tLastAliveReqSndTime;
    public String sAddr;
    private final Channel channel;
    
    public GameSocket(Channel channel) {
        this.channel = channel;
        this.lockSend = new ReentrantLock();
        this.bClosePosted = false;
        this.bAliveReqSended = false;
        this.tLastAliveAckRcvTime = 0;
        this.sAddr = "";
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            onClose();
        } finally {
            ctx.channel().close();
        }
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null) {
            return;
        }
        InPacket packet = (InPacket) msg;
        if (packet.getDataLen() < 1) {
            return;
        }
        processPacket(packet);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx == null || (cause instanceof IOException || cause instanceof ClassCastException)) {
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
    
    public final String getAddr() {
        if (sAddr.isEmpty() && channel != null) {
            return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().split(":")[0];
        }
        return sAddr;
    }
    
    public void onClose() {
        LoginApp.getInstance().getCenterAcceptor().removeSocket(this);
    }
    
    public boolean postClose() {
        if (!bClosePosted) {
            bClosePosted = true;
            return false;
        } else {
            onClose();
            return true;
        }
    }
    
    public void processPacket(InPacket packet) {
        final byte type = packet.decodeByte();
        
        if (type == Byte.MAX_VALUE) {
            WorldEntry world = new WorldEntry(this, packet.decodeByte(), packet.decodeString());
            
            byte channels = packet.decodeByte();
            for (byte i = 1; i <= channels; i++) {
                world.addChannel(new ChannelEntry(world.getWorldID(), i));
            }
            
            LoginApp.getInstance().addWorld(world);
        }
    }
    
    public void sendPacket(OutPacket packet, boolean bForce) {
        lockSend.lock();
        try {
            if (!bClosePosted || bForce) {
                channel.writeAndFlush(packet.toArray());
            }
        } finally {
            lockSend.unlock();
        }
    }
}
