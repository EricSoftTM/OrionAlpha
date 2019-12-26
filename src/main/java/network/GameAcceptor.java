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
    private final AtomicInteger remainedSocket;
    private EventLoopGroup workerGroup, childGroup;
    private Channel channel;
    private boolean acceptorClosed;
    
    /**
     * Constructs Game Server specific acceptors for each World and Channel.
     * 
     * @param addr Our IP Socket Address that contains the IP and Port to bind to
     */
    public GameAcceptor(InetSocketAddress addr) {
        this.addr = addr;
        this.serialNoCounter = new AtomicInteger(0);
        this.remainedSocket = new AtomicInteger(0);
        this.sn2pSocket = new HashMap<>();
        this.lock = new ReentrantLock();
        this.acceptorClosed = true;
    }
    
    public static GameAcceptor getInstance(int channel) {
        return GameApp.getInstance().getChannel(channel).getAcceptor();
    }
    
    /**
     * Decrement the remaining sockets waiting to close session.
     * 
     * @return The new decremented value of remaining sockets
     */
    public int decRemainedSocket() {
        return this.remainedSocket.decrementAndGet();
    }
    
    /**
     * Retrieves the IP address of the Game Server.
     * 
     * @return The IP of the server
     */
    public String getAddr() {
        return this.addr.getAddress().getHostAddress().split(":")[0];
    }
    
    /**
     * Retrieves the channel index by subtracting the world port from this port.
     * 
     * @return The ChannelID of the server
     */
    public short getChannelID() {
        return (short) (getPort() - GameApp.getInstance().getPort());
    }
    
    /**
     * Retrieves the Port of the Game Server.
     * 
     * @return The Port of the server
     */
    public short getPort() {
        return (short) this.addr.getPort();
    }
    
    /**
     * Finds a ClientSocket from the pool by their <code>localSocketSN</code>.
     * 
     * @param localSocketSN The SocketSN of the client to find
     * 
     * @return The reference (if it exists) of the client
     */
    public ClientSocket getSocket(int localSocketSN) {
        lock.lock();
        try {
            return sn2pSocket.get(localSocketSN);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Increment the remaining sockets waiting to close session.
     * 
     * @return The new incremented value of remaining sockets
     */
    public int incRemainedSocket() {
        return this.remainedSocket.incrementAndGet();
    }
    
    /**
     * Initializes an incoming SocketChannel, constructs their new ClientSocket,
     * and inserts the channel into the pipeline.
     * 
     * @param ch The incoming socket channel
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
            socket.setChannelID(getChannelID());
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
    
    /**
     * Removes the <code>socket</code> from the ClientSocket pool.
     * 
     * @param socket The ClientSocket to remove
     */
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
     * Once the server socket has been closed, we gracefully shutdown (or unbind) 
     * the game acceptor's channel and workers.
     * 
     */
    public void unbind() {
        channel.close();
        workerGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }
}
