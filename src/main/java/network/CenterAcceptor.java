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

import login.GameSocket;
import common.OrionConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.InPacket;

/**
 * Our server acceptor.
 * Initializes all incoming connections.
 * 
 * @author Eric
 */
public class CenterAcceptor extends ChannelInitializer<SocketChannel> implements Runnable {
    private final SocketAddress addr;
    private EventLoopGroup acceptor, childGroup;
    private Channel channel;
    private final List<GameSocket> sockets;
    private final Lock lock;
    
    /**
     * Constructs Game Server-specific acceptors for each World and Channel.
     * 
     * @param pAddr Our IP Socket Address that contains the IP and Port to bind to
     */
    public CenterAcceptor(SocketAddress pAddr) {
        this.addr = pAddr;
        this.lock = new ReentrantLock();
        this.sockets = new ArrayList<>();
    }
    
    public void removeSocket(GameSocket socket) {
        lock.lock();
        try {
            sockets.remove(socket);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Initializes the GameAcceptor and binds to our SocketAddress.
     */
    @Override
    public void run() {
        try {
            acceptor = new NioEventLoopGroup(4);
            childGroup = new NioEventLoopGroup(10);
            channel =  new ServerBootstrap().group(acceptor, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this)
                    .option(ChannelOption.SO_BACKLOG, OrionConfig.MAX_CONNECTIONS)
                    .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(addr).syncUninterruptibly().channel().closeFuture().channel();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    /**
     * Once the server socket has been closed,
     * we gracefully shutdown (or unbind) the server.
     */
    public void unbind() {
        channel.close();
        acceptor.shutdownGracefully();
        childGroup.shutdownGracefully();
    }

    /**
     * Initializes an incoming SocketChannel,
     * constructs their ClientSocket handler,
     * and inserts the channel into the
     * pipeline. 
     * 
     * This method will also update the
     * current active connections count 
     * on our GUI.
     * 
     * @param ch Incoming socket channel
     * @throws Exception 
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        GameSocket socket = new GameSocket(ch);
        
        ch.pipeline().addLast(
                new CenterDecoder(), 
                socket, 
                new CenterEncoder() 
        );
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
