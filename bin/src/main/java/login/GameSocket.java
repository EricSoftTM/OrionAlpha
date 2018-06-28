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
    private final Channel channel;
    private final Lock lockSend;
    private boolean closePosted;
    private boolean aliveReqSended;
    private long lastAliveAckRcvTime;
    private long lastAliveReqSndTime;
    private String addr;
    
    public GameSocket(Channel channel) {
        this.channel = channel;
        this.lockSend = new ReentrantLock();
        this.closePosted = false;
        this.aliveReqSended = false;
        this.lastAliveAckRcvTime = 0;
        this.addr = "";
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
        if (addr.isEmpty() && channel != null) {
            return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().split(":")[0];
        }
        return addr;
    }
    
    public void onClose() {
        LoginApp.getInstance().getCenterAcceptor().removeSocket(this);
    }
    
    public boolean postClose() {
        if (!closePosted) {
            closePosted = true;
            return false;
        } else {
            onClose();
            return true;
        }
    }
    
    public void processPacket(InPacket packet) {
        final byte type = packet.decodeByte();
        
        if (type == Byte.MAX_VALUE) {
            byte worldID = packet.decodeByte();
            String worldName = packet.decodeString();
            
            WorldEntry world = LoginApp.getInstance().getWorld(worldID);
            if (world == null) {
                world = new WorldEntry(this, worldID, worldName);
                
                LoginApp.getInstance().addWorld(world);
            }
            
            world.addChannel(new ChannelEntry(world.getWorldID(), packet.decodeByte(), packet.decodeString(), packet.decodeShort()));
        }
    }
    
    public void sendPacket(OutPacket packet, boolean force) {
        lockSend.lock();
        try {
            if (!closePosted || force) {
                channel.writeAndFlush(packet.toArray());
            }
        } finally {
            lockSend.unlock();
        }
    }
}
