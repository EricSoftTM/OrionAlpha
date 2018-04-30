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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import login.ChannelEntry;
import login.LoginApp;
import login.LoginPacket;
import login.WorldEntry;
import network.LoginAcceptor;
import network.SocketDecoder;
import network.SocketEncoder;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import network.security.XORCipher;
import util.Logger;

/**
 *
 * @author Eric
 */
public class ClientSocket extends SimpleChannelInboundHandler {
    private static final int MAX_CHARACTERS = 15;
    
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
    private int accountID;
    public short worldID;
    private byte channelID;
    public int characterID;
    private byte gender;
    private byte gradeCode;
    private String nexonClubID;
    public boolean adminClient;
    public int birthDate;
    public boolean closePosted;
    
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
    
    public ClientSocket(Channel socket) {
        this.channel = socket;
        this.lockSend = new ReentrantLock();
        this.loginState = 1;
        this.failCount = 0;
        this.failCountbyPinCode = 0;
        this.isPinCodeChecked = false;
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
        packet.encodeInt(cipher.getSeqRcv());
        packet.encodeInt(cipher.getSeqSnd());
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
    
    public String getNexonClubID() {
        return nexonClubID;
    }
    
    public int getAccountID() {
        return accountID;
    }
    
    public byte getChannelID() {
        return channelID;
    }
    
    public byte getGender() {
        return gender;
    }
    
    public byte getGradeCode() {
        return gradeCode;
    }
    
    public int getLocalSocketSN() {
        return localSocketSN;
    }
    
    public final String getSocketRemoteIP() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().split(":")[0];
    }
    
    public short getWorldID() {
        return worldID;
    }
    
    public void initSequence() {
        //this.uSeqRcv = Rand32.GetInstance().Random().intValue();
        //this.uSeqSnd = Rand32.GetInstance().Random().intValue();
        this.seqRcv = 42;
        this.seqSnd = 69;
        this.cipher = new XORCipher(seqSnd, seqRcv);
    }
    
    public boolean isAdmin() {
        return this.gradeCode >= 4;
        //return (nGradeCode & 0x1) > 0;
    }
    
    public boolean isLimitedIP() {
        return ((this.gradeCode >> 3) & 0x1) > 0;
    }
    
    private void processPacket(InPacket packet) {
        if (packet.getDataLen() < 1) {
            return;
        }
        final int type = packet.decodeByte();
        if (OrionConfig.LOG_PACKETS) {
            Logger.logReport("[Packet Logger] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
        }
        if (type == ClientPacket.AliveAck) {
            
        } else if (type >= ClientPacket.BEGIN_SOCKET && type <= ClientPacket.END_SOCKET) {
            switch (type) {
                case ClientPacket.CheckPassword:
                    onCheckPassword(packet);
                    break;
            }
        }
    }
    
    public void onAliveAck() {
        long cur = System.currentTimeMillis();
        if (this.aliveReqSent > 0 && (cur - this.aliveReqSent) <= 60000 * (this.firstAliveAck ? 8 : 3)) {
            this.aliveReqSent = 0;
            this.firstAliveAck = false;
            this.lastAliveAck = cur;
        } else {
            Logger.logError("Alive check failed : Time-out");
            closeSocket();
        }
    }
    
    public void onClose() {
        if (this.channel == null) {
            return;
        }
        if (this.accountID != 0) {
            // Remove from AdminSocket memory for user banning. -> Ignore since we have no AdminSocket.
        }
        if (this.loginState < 7 || this.loginState >= 12)
            this.accountID = 0;
        this.loginState = 13;
        Logger.logReport("Client socket disconnected");
        LoginAcceptor.getInstance().removeSocket(this);
        
        this.channel.close();
    }
    
    public void onCheckPassword(InPacket packet) {
        if (this.loginState != 1) {
            onClose();
            return;
        }
        String id = packet.decodeString();
        String passwd = packet.decodeString();
        
        int result = 1;
        if (passwd.equals("ericftw"))
            result = 3;
        
        this.accountID = 30000;
        this.gender = 0;
        this.gradeCode = 1;
        this.nexonClubID = "test@test.com";
        LoginApp.getInstance().getWorlds().add(new WorldEntry((byte) 0, "Orion"));
        LoginApp.getInstance().getWorlds().get(0).addChannel(new ChannelEntry((byte) 0, (byte) 1));
        
        sendPacket(LoginPacket.onCheckPasswordResult(this, result), false);
    }
    
    public boolean postClose() {
        if (!this.closePosted) {
            this.closePosted = true;
            return false;
        } else {
            onClose();
            return true;
        }
    }
    
    public void ResetLoginState(int loginState) {
        if (this.accountID > 0) {
            //mClientSocket.remove(dwAccountID);
        }
        this.nexonClubID = "";
        this.worldID = -1;
        this.channelID = -1;
        this.characterID = 0;
        this.processed = false;
        this.ipCheckRequestSent = 0;
        this.tempBlockedIP = false;
        this.premium = false;
        if (this.loginState == loginState) {
            ++this.failCount;
            if (this.failCount > 5)
                onClose();
        } else {
            this.failCount = 0;
        }
        this.loginState = loginState;
        //COutPacket::Init(&v2->m_oPacket, 2, 1);
        //TODO: Handle OutPacket LoginStates. I'm assuming this just directly sends a CheckPassword with nLoginState?
    }
    
    public void sendPacket(OutPacket packet, boolean force) {
        if (packet.getOffset() >= 0x32000) {
            Logger.logError("SendPacket size is more than 200KB. Dump: %s IP: %s", packet.dumpString(), getSocketRemoteIP());
            return;
        }
        this.lockSend.lock();
        try {
            if (!this.closePosted || force) {
                List<byte[]> buff = new LinkedList<>();
                packet.makeBufferList(buff, OrionConfig.CLIENT_VER, cipher);
                cipher.updateSeqSend();
                sendPacket(buff);
            }
        } finally {
            this.lockSend.unlock();
        }
    }
    
    public void sendPacket(List<byte[]> buff) {
        this.lockSend.lock();
        try {
            if (this.channel == null || buff == null || buff.isEmpty()) {
                throw new RuntimeException("fuck everything");
            }
            Iterator<byte[]> t = buff.iterator();
            while (t.hasNext()) {
                this.channel.write(t.next());
            }
            this.channel.flush();
        } finally {
            this.lockSend.unlock();
        }
    }
    
    public void setLocalSocketSN(int sn) {
        this.localSocketSN = sn;
    }
    
    /**
     * Migrates a remote client to a different game server.
     *
     * @param isGuestAccount If the requested account is a guest, crash them for hacking. (guests can't cc!)
     * @param addr The InetAddress of the requested channel server.
     * @param port The port the game server is on.
     * @return The server migration packet.
    */
    public static OutPacket onMigrateCommand(boolean isGuestAccount, InetAddress addr, int port) {
        OutPacket packet = new OutPacket(LoopbackPacket.MigrateCommand);
        packet.encodeBool(isGuestAccount);
        packet.encodeBuffer(addr.getAddress());
        packet.encodeShort(port);
        return packet;
    }
    
    /**
     * Sends an Alive Req to the client and an Alive Ack back to the server.
     *
     * @param time
     * @return 
    */
    public static OutPacket onAliveReq(int time) {
        OutPacket packet = new OutPacket(LoopbackPacket.AliveReq);
        packet.encodeInt(time);
        return packet;
    }
}
