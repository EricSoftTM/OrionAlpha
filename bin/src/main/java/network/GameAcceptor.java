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
import game.GameApp;
import game.user.ClientSocket;
import game.user.ClientSocket.MigrateState;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Eric
 */
/**
 * Our server acceptor.
 * Initializes all incoming connections.
 * 
 * @author Eric
 */
public class GameAcceptor extends ChannelInitializer<SocketChannel> implements Runnable {
    private final AtomicInteger serialNoCounter;
    private final Map<Integer, ClientSocket> sn2pSocket;
    private final Lock lock;
    private final InetSocketAddress addr;
    private EventLoopGroup workerGroup, childGroup;
    private Channel channel;
    private boolean acceptorClosed;
    private final AtomicInteger remainedSocket;
    
    /**
     * Constructs Game Server-specific acceptors for each World and Channel.
     * 
     * @param pAddr Our IP Socket Address that contains the IP and Port to bind to
     */
    public GameAcceptor(InetSocketAddress pAddr) {
        this.addr = pAddr;
        this.serialNoCounter = new AtomicInteger(0);
        this.remainedSocket = new AtomicInteger(0);
        this.sn2pSocket = new HashMap<>();
        this.lock = new ReentrantLock();
        this.acceptorClosed = true;
    }
    
    public static GameAcceptor getInstance() {
        return GameApp.getInstance().getAcceptor();
    }
    
    public int decRemainedSocket() {
        return this.remainedSocket.decrementAndGet();
    }
    
    public int incRemainedSocket() {
        return this.remainedSocket.incrementAndGet();
    }
    
    public ClientSocket getSocket(int localSocketSN) {
        lock.lock();
        try {
            return sn2pSocket.get(localSocketSN);
        } finally {
            lock.unlock();
        }
    }
    
    public void removeSocket(ClientSocket socket) {
        lock.lock();
        try {
            sn2pSocket.remove(socket.getLocalSocketSN());
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
            workerGroup = new NioEventLoopGroup(4);
            childGroup = new NioEventLoopGroup(10);
            channel =  new ServerBootstrap().group(workerGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this)
                    .option(ChannelOption.SO_BACKLOG, OrionConfig.MAX_CONNECTIONS)
                    .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(addr).syncUninterruptibly().channel().closeFuture().channel();
            
            acceptorClosed = false;
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
        workerGroup.shutdownGracefully();
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
        lock.lock();
        try {
            if (acceptorClosed) {
                return;
            }
            ClientSocket socket = new ClientSocket(ch);
            socket.setAddr(String.format("%s:%d", socket.getSocketRemoteIP(), addr.getPort()));
            socket.initSequence();
            int serialNo = serialNoCounter.incrementAndGet();
            socket.setLocalSocketSN(serialNo);
            sn2pSocket.put(serialNo, socket);
            ch.pipeline().addLast("ClientSocket", socket);
            if (sn2pSocket.size() > GameApp.getInstance().getConnectionLimit()) {
                for (ClientSocket client : sn2pSocket.values()) {
                    if (client != null && client.getMigrateState() == MigrateState.WaitMigrateIn) {
                        client.postClose();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
