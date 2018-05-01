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

import common.OrionConfig;
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
import network.GameAcceptor;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;

/**
 *
 * @author Eric
 */
public class CenterSocket extends SimpleChannelInboundHandler {
    private Channel channel;
    private final EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private final Lock lock;
    private final Lock lockSend;
    public boolean bClosePosted;
    
    public CenterSocket() {
        this.bClosePosted = false;
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
            String sAddr = "127.0.0.1:8383";
            channel = bootstrap.connect(sAddr.split(":")[0], Integer.parseInt(sAddr.split(":")[1]))
                    .syncUninterruptibly()
                    .channel()
                    .closeFuture()
                    .channel();
            
            // Send the Center Server the Login server information request
            OutPacket packet = new OutPacket(Byte.MAX_VALUE);
            packet.encodeByte(Integer.valueOf(System.getProperty("gameID", "0")));
            packet.encodeString(OrionConfig.SERVER_NAME);//WorldName
            packet.encodeByte(1);//TODO: Channels
            sendPacket(packet);
            
            Logger.logReport("Center socket connected successfully");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public void postCloseMessage() {
        lock.lock();
        try {
            if (!bClosePosted) {
                bClosePosted = true;
                channel.close();
                workerGroup.shutdownGracefully();
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void processPacket(InPacket packet) {
        if (GameAcceptor.getInstance() != null) {
            Logger.logReport("Packet received: %s", packet.dumpString());
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
