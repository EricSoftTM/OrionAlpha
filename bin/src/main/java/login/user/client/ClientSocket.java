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
package login.user.client;

import common.OrionConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.LoginAcceptor;
import network.SocketDecoder;
import network.SocketEncoder;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import network.security.XORCipher;
import util.Logger;

/**
 *
 * @author Eric
 */
public class ClientSocket extends SimpleChannelInboundHandler {
    private static final int MaxCharacters = 15;
    private static final Map<Integer, ClientPacket> clientPacket = new HashMap<>();
    
    private final Channel channel;
    private SocketDecoder decoder;
    private SocketEncoder encoder;
    private final Lock lockSend;
    private XORCipher cipher;
    
    public int loginState;
    public int failCount;
    public int failCountbyPinCode;
    public boolean confirmEULA;
    public boolean isPinCodeChecked;
    public String id;
    public int accountID;
    public int worldID;
    public int channelID;
    public int characterID;
    public int gender;
    public int gradeCode;
    public int subGradeCode;
    public int privateStatusID;
    public int buyCharCount;
    public String nexonClubID;
    public boolean adminClient;
    public int countryID;
    public int birthDate;
    public int regStatID;
    public int useDay;
    
    public int localSocketSN;
    public int seqSnd;
    public int seqRcv;
    public int seqRcvLast;
    public int ipBlockType;
    public long aliveReqSent;
    public long lastAliveAck;
    public boolean firstAliveAck;
    public long ipCheckRequestSent;
    public boolean premium;
    public boolean tempBlockedIP;
    public boolean processed;
    public byte loginOpt;
    
    public ClientSocket(Channel socket) {
        this.channel = socket;
        this.lockSend = new ReentrantLock();
        this.loginState = 1;
        this.failCount = 0;
        this.failCountbyPinCode = 0;
        this.isPinCodeChecked = false;
        this.id = "";
        this.accountID = 0;
        this.worldID = -1;
        this.channelID = -1;
        this.gradeCode = 0;
        this.nexonClubID = "";
        this.adminClient = false;
        this.localSocketSN = 0;
        this.aliveReqSent = 0;
        //this.bIPBlockType = IPFilter.Permit;
        this.lastAliveAck = 0;
        this.firstAliveAck = true;
        this.ipCheckRequestSent = 0;
        this.premium = false;
        this.tempBlockedIP = false;
        this.processed = false;
        //this.bLoginOpt = CheckSPW.Disabled;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        encoder = new SocketEncoder(cipher);
        decoder = new SocketDecoder(cipher);
        channel.pipeline().addBefore("ClientSocket", "AliveAck", new IdleStateHandler(20, 20, 0));
        channel.pipeline().addBefore("ClientSocket", "SocketEncoder", encoder);
        channel.pipeline().addBefore("ClientSocket", "SocketDecoder", decoder);
        
        OutPacket packet = new OutPacket(Integer.MAX_VALUE);
        packet.encodeShort(14);
        packet.encodeShort(OrionConfig.CLIENT_VER);
        packet.encodeString(OrionConfig.CLIENT_PATCH);
        packet.encodeInt(seqRcv);
        packet.encodeInt(seqSnd);
        packet.encodeByte(OrionConfig.GAME_LOCALE);
        channel.writeAndFlush(Unpooled.wrappedBuffer(packet.toArray()));
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture writeFuture = ctx.channel().closeFuture();
        if (writeFuture != null) {
            writeFuture.awaitUninterruptibly();
        }
        closeSocket();
        ctx.channel().closeFuture().awaitUninterruptibly();
        ctx.channel().close().awaitUninterruptibly();
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null) {
            return;
        }
        InPacket packet = (InPacket) msg;
        try {
            if (packet.getDataLen() < 1) {
                return;
            }
            processPacket(packet);
        } finally {
            if (packet.getOffset() == 4)
                super.channelRead(ctx, msg);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx == null || (cause instanceof IOException || cause instanceof ClassCastException)) {
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //OnAliveAck();
        }
    }
    
    public void closeSocket() {
        onClose();
    }
    
    public final String getSocketRemoteIP() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().split(":")[0];
    }
    
    public int getLocalSocketSN() {
        return localSocketSN;
    }
    
    public void setLocalSocketSN(int sn) {
        this.localSocketSN = sn;
    }
    
    public void initSequence() {
        //this.uSeqRcv = Rand32.GetInstance().Random().intValue();
        //this.uSeqSnd = Rand32.GetInstance().Random().intValue();
        this.seqRcv = 42;
        this.seqSnd = 69;
        this.cipher = new XORCipher(seqSnd, seqRcv);
    }
    
    public boolean isAdmin() {
        return gradeCode >= 4;
        //return (nGradeCode & 0x1) > 0;
    }
    
    public boolean isLimitedIP() {
        return ((gradeCode >> 3) & 0x1) > 0;
    }
    
    private void processPacket(InPacket packet) {
        if (clientPacket.isEmpty()) {
            for (ClientPacket cp : ClientPacket.values()) {
                if (clientPacket.get(cp.get()) == null) {
                    clientPacket.put(cp.get(), cp);
                }
            }
        }
        if (packet.getDataLen() < 1) {
            return;
        }
        final int type = packet.decodeByte();
        ClientPacket msg = clientPacket.get(type);
        if (OrionConfig.LOG_PACKETS) {
            Logger.logReport("[Packet Logger] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
        }
        if (msg == null && !OrionConfig.LOG_PACKETS) {
            return;
        }
        
    }
    
    public void onAliveAck() {
        long tCur = System.currentTimeMillis();
        if (aliveReqSent > 0 && (tCur - aliveReqSent) <= 60000 * (firstAliveAck ? 8 : 3)) {
            aliveReqSent = 0;
            firstAliveAck = false;
            lastAliveAck = tCur;
        } else {
            Logger.logError("Alive check failed : Time-out");
            closeSocket();
        }
    }
    
    public void onClose() {
        if (channel == null) {
            return;
        }
        if (accountID != 0) {
            // Remove from AdminSocket memory for user banning. -> Ignore since we have no AdminSocket.
        }
        if (loginState < 7 || loginState >= 12)
            accountID = 0;
        loginState = 13;
        Logger.logReport("Client socket disconnected");
        LoginAcceptor.getInstance().removeSocket(this);
        
        channel.close();
    }
}
