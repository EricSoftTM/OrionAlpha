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
package game;

import game.user.ClientSocket;
import game.user.User;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.json.JsonObject;
import network.GameAcceptor;
import network.packet.CenterPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Utilities;

/**
 *
 * @author Eric
 */
public class CenterSocket extends SimpleChannelInboundHandler {
    private Channel channel;
    private Bootstrap bootstrap;
    private final EventLoopGroup workerGroup;
    private final Lock lock;
    private final Lock lockSend;
    private final byte channelID;
    private boolean closePosted;
    private String worldName;
    private String addr;
    private int port;
    
    public CenterSocket(int channel) {
        this.channelID = (byte) channel;
        this.worldName = "";
        this.addr = "";
        this.closePosted = false;
        this.lock = new ReentrantLock();
        this.lockSend = new ReentrantLock();
        this.workerGroup = new NioEventLoopGroup();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.logReport("Center socket closed");
        try {
            postCloseMessage();
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
    
    public void connect() {
        try {
            bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new CenterDecoder(), 
                                    CenterSocket.this,
                                    new CenterEncoder()
                            );
                        }
                    });
            
            // Connect to the Center socket
            channel = bootstrap.connect(getAddr(), getPort())
                    .syncUninterruptibly()
                    .channel()
                    .closeFuture()
                    .channel();
            
            // Send the Center Server the Login server information request
            OutPacket packet = new OutPacket(CenterPacket.InitGameSvr);
            packet.encodeByte(GameApp.getInstance().getWorldID());
            packet.encodeString(getWorldName());
            encodeChannel(packet);
            sendPacket(packet);
            
            Logger.logReport("Center socket connected successfully");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public void encodeChannel(OutPacket packet) {
        game.Channel ch = GameApp.getInstance().getChannel(getChannelID());
        
        packet.encodeByte(ch.getChannelID());
        packet.encodeString(ch.getAddr());
        packet.encodeShort(ch.getPort());
    }
    
    public String getAddr() {
        return addr;
    }
    
    public byte getChannelID() {
        return channelID;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public void init(JsonObject data) {
        this.addr = data.getString("ip", "127.0.0.1");
        this.port = data.getInt("port", 8383);
        this.worldName = data.getString("worldName", "OrionAlpha");
    }
    
    public void onShopMigrateResult(InPacket packet) {
        int characterID = packet.decodeInt();
        
        User user = GameApp.getInstance().getChannel(getChannelID()).findUser(characterID);
        if (user != null) {
            if (packet.decodeBool()) {
                user.sendPacket(ClientSocket.onMigrateCommand(false, Utilities.netIPToInt32(packet.decodeString()), packet.decodeShort()));
            } else {
                user.sendSystemMessage("The Cash Shop is unavailable.");
            }
        }
    }
    
    public void postCloseMessage() {
        lock.lock();
        try {
            if (!closePosted) {
                closePosted = true;
                channel.close();
                workerGroup.shutdownGracefully();
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void processPacket(InPacket packet) {
        if (GameAcceptor.getInstance(channelID) != null) {
            final byte type = packet.decodeByte();
            
            switch (type) {
                case CenterPacket.ShopMigrateRes:
                    onShopMigrateResult(packet);
                    break;
                default: {
                    Logger.logReport("Packet received: %s", packet.dumpString());
                }
            }
        }
    }
    
    public void sendPacket(OutPacket packet) {
        if (channel != null && channel.isActive()) {
            lockSend.lock();
            try {
                channel.writeAndFlush(packet.toArray());
            } finally {
                lockSend.unlock();
            }
        }
    }
    
    private static class CenterDecoder extends ReplayingDecoder<Void> {
        
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            int length = in.readInt();
            
            byte[] src = new byte[length];
            in.readBytes(src);
            
            InPacket packet = new InPacket();
            packet.setDataLen(length);
            packet.rawAppendBuffer(Unpooled.wrappedBuffer(src), length);
            out.add(packet);
        }
    }
    
    private static class CenterEncoder extends MessageToByteEncoder<byte[]> {
        
        @Override
        protected void encode(ChannelHandlerContext ctx, byte[] message, ByteBuf out) throws Exception {
            byte[] packet = (byte[]) message;
        
            out.writeInt(packet.length);
            out.writeBytes(Arrays.copyOf(packet, packet.length));
        }
    }
}
