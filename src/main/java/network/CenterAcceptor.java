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

import io.netty.channel.*;
import login.GameSocket;
import common.OrionConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import network.packet.InPacket;

/**
 * Our server acceptor.
 * Initializes all incoming connections.
 * 
 * @author Eric
 */
public class CenterAcceptor {
    private final SocketAddress addr;
    private EventLoopGroup bossGroup, workerGroup;
    private Channel channel;
    private final List<GameSocket> sockets;
    
    /**
     * Constructs Game Server-specific acceptors for each World and Channel.
     * 
     * @param pAddr Our IP Socket Address that contains the IP and Port to bind to
     */
    public CenterAcceptor(SocketAddress pAddr) {
        this.addr = pAddr;
        this.sockets = new ArrayList<>();
    }
    
    public void addSocket(GameSocket socket) {
        sockets.add(socket);
    }
    
    public void removeSocket(GameSocket socket) {
        sockets.remove(socket);
    }
    
    /**
     * Initializes the CenterAcceptor and binds to our SocketAddress.
     */
    public void start() {
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            
            channel =  new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            GameSocket socket = new GameSocket(ch);
                            
                            ch.pipeline().addLast("GameSocket", socket);
                        }
                    })
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
    public void terminate() {
	    try {
		    channel.close();
	    } finally {
		    bossGroup.shutdownGracefully();
		    workerGroup.shutdownGracefully();
	    }
    }
    
    public static class CenterDecoder extends ReplayingDecoder<Void> {
        
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
    
    public static class CenterEncoder extends MessageToByteEncoder<byte[]> {
        
        @Override
        protected void encode(ChannelHandlerContext ctx, byte[] message, ByteBuf out) throws Exception {
            byte[] packet = (byte[]) message;
        
            out.writeInt(packet.length);
            out.writeBytes(Arrays.copyOf(packet, packet.length));
        }
    }
}
