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
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.util.concurrent.ScheduledFuture;
import login.LoginApp;
import login.user.ClientSocket;
import util.Logger;

/**
 * Our server acceptor.
 * Initializes all incoming connections.
 * 
 * @author Eric
 */
public class LoginAcceptor {
    private final AtomicInteger serialNoCounter;
    private final Map<Integer, ClientSocket> sn2pSocket;
    private final Map<String, Integer> ipCount;//Nexon _actually_ stores this as a UINT dwIP key.
    private final SocketAddress addr;
    private EventLoopGroup bossGroup, workerGroup;
    private Channel channel;
    private ScheduledFuture<?> update;
    public boolean acceptorClosed;
    public boolean isWebDead;
    public boolean isLaunchingModeBeingChanged;
    public boolean ignoreSPW;
    public boolean querySSNOnCreateNewCharacter;
    
    /**
     * Constructs Login Server-specific acceptors.
     * 
     * @param addr Our IP Socket Address that contains the IP and Port to bind to
     */
    public LoginAcceptor(SocketAddress addr) {
        this.addr = addr;
        this.serialNoCounter = new AtomicInteger(0);
        this.sn2pSocket = new ConcurrentHashMap<>();
        this.ipCount = new ConcurrentHashMap<>();
        this.acceptorClosed = true;
    }
    
    public static LoginAcceptor getInstance() {
        return LoginApp.getInstance().getAcceptor();
    }
    
    public ClientSocket getSocket(int localSocketSN) {
        return sn2pSocket.get(localSocketSN);
    }
    
    public int getSocketCount() {
        return sn2pSocket.size();
    }
    
    public void removeSocket(ClientSocket socket) {
        sn2pSocket.remove(socket.getLocalSocketSN());
    }
    
    /**
     * Initializes the LoginAcceptor and binds to our SocketAddress.
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
				            LoginAcceptor.this.initChannel(ch);
			            }
		            })
                    .option(ChannelOption.SO_BACKLOG, OrionConfig.MAX_CONNECTIONS)
                    .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(addr).syncUninterruptibly().channel().closeFuture().channel();
	
	        update = bossGroup.scheduleAtFixedRate(() -> update(System.currentTimeMillis()), 1000, 100, TimeUnit.MILLISECONDS);
            
            acceptorClosed = false;
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
			if (update != null) {
				update.cancel(true);
			}
			channel.close();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private void update(long time) {
		for (ClientSocket socket : sn2pSocket.values()) {
			if (socket != null) {
				socket.onUpdate(time);
			}
		}
	}

    private void initChannel(SocketChannel ch) throws Exception {
        if (this.isLaunchingModeBeingChanged || this.acceptorClosed) {
            return;
        }
        ClientSocket socket = new ClientSocket(ch);
        socket.initSequence();//rand() | (rand() << 16)
	    int serialNo = this.serialNoCounter.incrementAndGet();
	    socket.setLocalSocketSN(serialNo);
	    this.sn2pSocket.put(serialNo, socket);
        ch.pipeline().addLast("ClientSocket", socket);
        if (!this.ipCount.containsKey(socket.getSocketRemoteIP())) {
            this.ipCount.put(socket.getSocketRemoteIP(), 0);
        }
        int nIPCount = this.ipCount.get(socket.getSocketRemoteIP());
        this.ipCount.put(socket.getSocketRemoteIP(), ++nIPCount);
        if (nIPCount <= 30) {
            if (this.sn2pSocket.size() > 5000) {
                Logger.logError("IP Connection Count > 5000");
                List<String> socketIP = new ArrayList<>();
                for (Iterator<Map.Entry<String, Integer>> it = this.ipCount.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, Integer> entry = it.next();
                    if (entry.getValue() > 10) {
                        it.remove();
                        socketIP.add(entry.getKey());
                    }
                }
                for (Iterator<ClientSocket> it = this.sn2pSocket.values().iterator(); it.hasNext();) {
                    ClientSocket entry = it.next();
                    if (socketIP.contains(entry.getSocketRemoteIP())) {
                        Logger.logError("IP Connection Counting > 10 [%s]", entry.getSocketRemoteIP());
                        it.remove();
                        entry.closeSocket();
                    }
                }
                socketIP.clear();
            }
            Logger.logReport("Connection accepted [%s]", socket.getSocketRemoteIP());
        }
    }
}
