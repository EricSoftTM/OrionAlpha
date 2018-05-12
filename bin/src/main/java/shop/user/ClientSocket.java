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
package shop.user;

import common.OrionConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ScheduledFuture;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.SocketDecoder;
import network.SocketEncoder;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import network.security.XORCrypter;
import shop.ShopApp;
import util.Logger;
import util.Rand32;
import util.Utilities;

/**
 *
 * @author sunnyboy
 */
public class ClientSocket extends SimpleChannelInboundHandler {

    public class MigrateState {

        public static final int Invalid = 0,
                WaitMigrateIn = 1,
                WaitCenterMigrateInResult = 2,
                Identified = 3,
                WaitCenterMigrateOutResult = 4,
                WaitMigrateOut = 5,
                Disconnected = 6;
    }

    private final Channel channel;
    private SocketDecoder decoder;
    private SocketEncoder encoder;
    private final Lock lockSend;
    private XORCrypter cipher;
    private ScheduledFuture update;
    private int migrateState;
    private int characterID;
    public boolean adminClient;
    private User user;
    private int localSocketSN;
    private int seqSnd;
    private int seqRcv;
    private AtomicBoolean closePosted;
    private AtomicBoolean updatePosted;
    private String addr;
    public long aliveReqSent;
    public long lastAliveAck;
    private long acceptTime;
    private long migrateOut;

    public ClientSocket(Channel socket) {
        this.channel = socket;
        this.lockSend = new ReentrantLock();
        this.migrateState = MigrateState.WaitMigrateIn;
        this.characterID = 0;
        this.adminClient = false;
        this.user = null;
        this.closePosted = new AtomicBoolean(false);
        this.updatePosted = new AtomicBoolean(false);
        this.addr = "";
        this.localSocketSN = 0;
        this.aliveReqSent = 0;
        this.lastAliveAck = 0;
        this.acceptTime = 0;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        encoder = new SocketEncoder(cipher);
        decoder = new SocketDecoder(cipher);
        channel.pipeline().addBefore("ClientSocket", "AliveAck", new IdleStateHandler(20, 15, 0));
        channel.pipeline().addBefore("ClientSocket", "SocketEncoder", encoder);
        channel.pipeline().addBefore("ClientSocket", "SocketDecoder", decoder);
        update = channel.eventLoop().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                onUpdate();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
        OutPacket packet = new OutPacket(Integer.MAX_VALUE);
        packet.encodeShort(14);
        packet.encodeShort(OrionConfig.CLIENT_VER);
        packet.encodeString(OrionConfig.CLIENT_PATCH);
        packet.encodeInt(cipher.getSeqRcv());
        packet.encodeInt(cipher.getSeqSnd());
        packet.encodeByte(OrionConfig.GAME_LOCALE);
        acceptTime = System.currentTimeMillis();
        channel.writeAndFlush(Unpooled.wrappedBuffer(packet.toArray()));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            onClose();
            if (update != null) {
                update.cancel(true);
            }
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
        cause.printStackTrace(System.err);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                if (user != null && migrateState == MigrateState.WaitCenterMigrateOutResult) {
                    // If a user has been migrating for over 30 seconds, dc and prepare a
                    // HandleUserMigrateTimeout on the center JVM.
                    if (System.currentTimeMillis() - migrateOut > 30000) {
                        channelInactive(ctx);
                    }
                    // If there's no active user and they aren't migrating, then they have no
                    // excuse as to why they have no read operations for 20 seconds straight.
                } else {
                    channelInactive(ctx);
                }
                // Handle the AliveAck on the client-end if this ever occurs.
                // If the user hits over a 15-second writer timeout -> disconnect.
            } else if (e.state() == IdleState.WRITER_IDLE) {
                channel.writeAndFlush(onAliveReq((int) (System.currentTimeMillis() / 1000)));
            }
        }
    }

    public String getAddr() {
        return addr;
    }

    public int getCharacterID() {
        return characterID;
    }

    public int getLocalSocketSN() {
        return localSocketSN;
    }

    public int getMigrateState() {
        return migrateState;
    }

    public final String getSocketRemoteIP() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().split(":")[0];
    }

    public User getUser() {
        return user;
    }

    public void initSequence() {
        this.seqRcv = Rand32.getInstance().random().intValue();
        this.seqSnd = Rand32.getInstance().random().intValue();
        this.cipher = new XORCrypter(this.seqSnd, this.seqRcv);
    }

    public void onClose() {
        if (this.migrateState == MigrateState.Invalid || this.migrateState == MigrateState.WaitMigrateIn) {
            this.characterID = 0;
        }
        ShopApp.getInstance().getAcceptor().incRemainedSocket();
        ShopApp.getInstance().getAcceptor().removeSocket(this);
        if (this.user != null) {
            this.user.onSocketDestroyed(false);
            this.user.destructUser();
            this.user = null;
        }
        this.channel.close();
        ShopApp.getInstance().getAcceptor().decRemainedSocket();
    }

    public void onMigrateIn(InPacket packet) {
        if (migrateState == MigrateState.WaitMigrateIn) {
            characterID = packet.decodeInt();

            user = new User(this);
            if (user.getCharacterID() == characterID) {
                migrateState = MigrateState.Identified;
                if (User.registerUser(user)) {
                    user.onMigrateInSuccess();
                    return;
                }
                Logger.logError("Duplicated login [%s]", addr);
            } else {
                Logger.logError("CharacterID does not match!");
            }
        }
        postClose();
    }

    public void onUpdate() {
        long cur = System.currentTimeMillis();

        if (isWaitMigrateInState() && ((cur - this.acceptTime >= ShopApp.getInstance().getWaitingFirstPacket()))) {
            Logger.logError("Disconnect dummy connection - %d(ms) : %s", (cur - acceptTime), addr);
            postClose();
        } else {
            if (this.aliveReqSent != 0) {
                //SecuritySession Update, unknown if it will be added
                if (cur - this.aliveReqSent > 180000) {
                    Logger.logError("Alive check failed : Ack lost");
                    postClose();
                }
            } else if (this.lastAliveAck == 0 || cur - this.lastAliveAck > 7000) {
                this.aliveReqSent = cur != 0 ? cur : 1;
            }
            cur = System.currentTimeMillis();
            if (user == null || user.update(cur)) {
                this.updatePosted.set(false);
            } else {
                onClose();
            }
        }
    }

    public boolean isWaitMigrateInState() {
        return this.migrateState == MigrateState.WaitMigrateIn;
    }

    public boolean postClose() {
        if (closePosted.compareAndSet(false, true)) {
            onClose();
            return true;
        }
        return false;
    }

    private void processPacket(InPacket packet) {
        final byte type = packet.decodeByte();
        if (OrionConfig.LOG_PACKETS) {
            Logger.logReport("[Packet Logger] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
        }
        switch (type) {
            case ClientPacket.AliveAck:
                sendPacket(onAliveReq(packet.decodeInt()), false);
                break;
            case ClientPacket.AliveAck2:
                Logger.logReport("Unknown AliveAck2 recieved.");
                break;
            case ClientPacket.MigrateIn:
                onMigrateIn(packet);
                break;
            default:
                /*if (type < ClientPacket.BEGIN_CASHSHOP) {
                 Logger.logReport("[Unidentified Packet] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
                 return;
                 }*/
                Logger.logReport("Recieved " + "[0x" + Integer.toHexString(type).toUpperCase() + "]");
                processUserPacket(type, packet);
                break;
        }
    }

    private void processUserPacket(byte type, InPacket packet) {
        try {
            if (this.migrateState >= MigrateState.Identified && this.migrateState <= MigrateState.WaitMigrateOut) {
                if (user != null) {
                    user.onPacket(type, packet);
                }
            } else if (migrateState != MigrateState.Disconnected) {
                postClose();
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public void setLocalSocketSN(int sn) {
        this.localSocketSN = sn;
    }

    public void setAcceptInfo(long cur) {
        this.acceptTime = cur;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void sendPacket(OutPacket packet, boolean force) {
        if (packet.getOffset() >= 0x32000) {
            Logger.logError("SendPacket size is more than 200KB. Dump: %s IP: %s", packet.dumpString(), getSocketRemoteIP());
            return;
        }
        lockSend.lock();
        try {
            if (!this.closePosted.get() || force) {
                List<byte[]> buff = new LinkedList<>();
                packet.makeBufferList(buff, OrionConfig.CLIENT_VER, cipher);
                encoder.getCipher().updateSeqSnd();
                sendPacket(buff);
                buff.clear();
            }
        } finally {
            lockSend.unlock();
        }
    }

    private void sendPacket(List<byte[]> buff) {
        lockSend.lock();
        try {
            if (channel == null || buff == null || buff.isEmpty()) {
                throw new RuntimeException("fuck everything");
            }
            Iterator<byte[]> t = buff.iterator();
            while (t.hasNext()) {
                channel.write(t.next());
            }
            channel.flush();
        } finally {
            lockSend.unlock();
        }
    }

    public void onFilterMigrateOut() {
        if (user == null) {
            onMigrateOut();
        }
    }

    private void onMigrateOut() {
        if (this.migrateState == MigrateState.Identified) {
            this.migrateState = MigrateState.WaitCenterMigrateOutResult;
            sendPacket(ClientSocket.onMigrateCommand(false, Utilities.netIPToInt32("127.0.0.1"), (short) 8585), false); // get ip/port from GameApp in future
        }
    }
    
    /**
     * Migrates a remote client to a different game server.
     *
     * @param isGuestAccount If the requested account is a guest, crash them for
     * hacking. (guests can't cc!)
     * @param ip The IP Address of the requested game server.
     * @param port The port the game server is on.
     * @return The server migration packet.
     */
    public static OutPacket onMigrateCommand(boolean isGuestAccount, int ip, int port) {
        OutPacket packet = new OutPacket(LoopbackPacket.MigrateCommand);
        packet.encodeBool(!isGuestAccount);
        if (!isGuestAccount) {
            packet.encodeInt(ip);
            packet.encodeShort(port);
        }
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
