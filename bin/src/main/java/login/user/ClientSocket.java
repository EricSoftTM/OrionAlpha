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
package login.user;

import common.OrionConfig;
import common.user.CharacterData;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import login.LoginApp;
import login.LoginPacket;
import login.WorldEntry;
import login.avatar.Avatar;
import network.LoginAcceptor;
import network.SocketDecoder;
import network.SocketEncoder;
import network.database.LoginDB;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import network.security.XORCrypter;
import util.Logger;
import util.Rand32;
import util.Utilities;

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
    private XORCrypter cipher;
    
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
    private final Map<Integer, String> characters;
    
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
        this.characters = new HashMap<>();
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
        this.seqRcv = Rand32.getInstance().random().intValue();
        this.seqSnd = Rand32.getInstance().random().intValue();
        this.cipher = new XORCrypter(seqSnd, seqRcv);
    }
    
    public boolean isAdmin() {
        return this.gradeCode >= 4;
        //return (nGradeCode & 0x1) > 0;
    }
    
    public boolean isLimitedIP() {
        return ((this.gradeCode >> 3) & 0x1) > 0;
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
    
    public void onCheckDuplicateID(InPacket packet) {
        if (loginState == 8) {
            String checkedName = packet.decodeString();
            
            Logger.logReport("Checking if name %s is valid..", checkedName);
            boolean nameUsed = false;//TODO Database
            
            sendPacket(LoginPacket.onCheckDuplicateIDResult(checkedName, nameUsed), false);
        } else {
            postClose();
        }
    }
    
    public void onCheckPassword(InPacket packet) {
        if (this.loginState != 1) {
            onClose();
            return;
        }
        String id = packet.decodeString();
        String passwd = packet.decodeString();
        
        if (id == null || id.isEmpty()) {
            return;
        }
        
        int retCode = LoginDB.rawCheckPassword(id, passwd, this);
        if (retCode == 1) {
            retCode = LoginDB.rawCheckUserConnected(this.accountID);
        }
        
        sendPacket(LoginPacket.onCheckPasswordResult(this, retCode), false);
    }
    
    public void onCreateNewCharacter(InPacket packet) {
        // [04 00 45 72 69 63] [21 4E 00 00] [4B 75 00 00] [8A DE 0F 00] [A2 2C 10 00] [85 5B 10 00] [F0 DD 13 00] [07 00 00 00] [06 00 00 00] [06 00 00 00] [06 00 00 00]
        if (loginState != 8) {
            postClose();
            return;
        }
        List<Integer> stat = new ArrayList<>(4);
        boolean ret = true;
        
        String charName = packet.decodeString();
        int face = packet.decodeInt();
        int hair = packet.decodeInt();
        int clothes = packet.decodeInt();
        int pants = packet.decodeInt();
        int shoes = packet.decodeInt();
        int weapon = packet.decodeInt();
        
        for (int i = 0; i < 4; i++) {
            stat.add(packet.decodeInt()); // STR, DEX, INT, LUK
        }
        
        // TODO: CheckCharName
        
        // TODO: Check Character Equips
        
        WorldEntry world = LoginApp.getInstance().getWorld(this.worldID);
        if (ret && world != null && world.getSocket() != null) {
            // TODO: Insert into DB
            
            Avatar avatar = new Avatar();
            sendPacket(LoginPacket.onCreateNewCharacterResult((byte) 0, avatar), false);
        }
    }
    
    public void onDeleteCharacter(InPacket packet) {
        if (loginState != 8) {
            postClose();
            return;
        }
        int characterId = packet.decodeInt();
        boolean ret = true;//does only the client validate this? o.O
        
        if (ret) {
            // TODO: Remove from DB
            
            sendPacket(LoginPacket.onDeleteCharacterResult(characterId, (byte) 0), false);
        }
    }
    
    public void onSelectCharacter(InPacket packet) {
        if (loginState == 8) {
            characterID = packet.decodeInt();
            
            if (!characters.containsKey(characterID)) {
                closeSocket();
                return;
            }
            
            WorldEntry pWorld = LoginApp.getInstance().getWorld(worldID);
            if (pWorld != null) {
                loginState = 9;
                
                // TODO: Handle addresses for each WorldEntry.
                sendPacket(LoginPacket.onSelectCharacterResult((byte) 1, Utilities.netIPToInt32("127.0.0.1"), (short) 8585, characterID), false);
            }
        } else {
            postClose();
        }
    }
    
    public void onSelectWorld(InPacket packet) {
        this.worldID = packet.decodeByte();
        this.channelID = packet.decodeByte();
        
        WorldEntry pWorld = LoginApp.getInstance().getWorld(worldID);
        if (pWorld != null) {
            List<Integer> characterId = new ArrayList<>();
            int count = LoginDB.rawGetWorldCharList(this.accountID, this.worldID, characterId);
            
            List<CharacterData> avatars = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                avatars.add(LoginDB.rawLoadCharacter(characterId.get(i)));
                characters.put(characterId.get(i), avatars.get(i).getCharacterStat().getName());
            }
            
            loginState = 8;
            sendPacket(LoginPacket.onSelectWorldResult(1, avatars), false);
        } else {
            Logger.logError("User %s attempting to connect to offline world %d", this.nexonClubID, this.worldID);
        }
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
    
    private void processPacket(InPacket packet) {
        if (packet.getDataLen() < 1) {
            return;
        }
        final byte type = packet.decodeByte();
        if (OrionConfig.LOG_PACKETS) {
            Logger.logReport("[Packet Logger] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
        }
        if (type == ClientPacket.AliveAck) {
            sendPacket(onAliveReq(packet.decodeInt()), false);
        } else if (type >= ClientPacket.BEGIN_SOCKET && type <= ClientPacket.END_SOCKET) {
            switch (type) {
                case ClientPacket.CheckPassword:
                    onCheckPassword(packet);
                    break;
                case ClientPacket.SelectWorld:
                    onSelectWorld(packet);
                    break;
                case ClientPacket.CheckDuplicatedID:
                    onCheckDuplicateID(packet);
                    break;
                case ClientPacket.CreateNewCharacter:
                    onCreateNewCharacter(packet);
                    break;
                case ClientPacket.DeleteCharacter:
                    onDeleteCharacter(packet);
                    break;
                case ClientPacket.SelectCharacter:
                    onSelectCharacter(packet);
                    break;
                default: {
                    Logger.logReport("[Unidentified Packet] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
                }
            }
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
                cipher.updateSeqSnd();
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
    
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
    
    public void setGender(byte gender) {
        this.gender = gender;
    }
    
    public void setGradeCode(byte grade) {
        this.gradeCode = grade;
    }
    
    public void setLocalSocketSN(int sn) {
        this.localSocketSN = sn;
    }
    
    public void setNexonClubID(String id) {
        this.nexonClubID = id;
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
