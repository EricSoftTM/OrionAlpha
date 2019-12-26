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
package shop;

import game.user.item.ItemInfo;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import network.ShopAcceptor;
import network.database.CommonDB;
import network.database.Database;
import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class ShopApp implements Runnable {
    private static final ShopApp instance = new ShopApp();
    
    private String addr;
    private short port;
    private byte worldID;
    private int connectionLimit;
    private int waitingFirstPacket;
    private ShopAcceptor acceptor;
    private CenterSocket socket;
    private final long serverStartTime;
    private final AtomicLong itemInitSN;
    private final AtomicLong cashItemInitSN;
    private final Lock lockItemSN;
    private final Lock lockCashItemSN;
    private final Map<Long, Commodity> commodity;

    public ShopApp() {
        this.worldID = -2;//ShopSvr
        this.waitingFirstPacket = 1000 * 15;
        this.connectionLimit = 1000;
        this.serverStartTime = System.currentTimeMillis();
        this.itemInitSN = new AtomicLong(0);
        this.cashItemInitSN = new AtomicLong(0);
        this.lockItemSN = new ReentrantLock();
        this.lockCashItemSN = new ReentrantLock();
        this.commodity = new HashMap<>();
    }

    public static final ShopApp getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        ShopApp.getInstance().run();
    }
    
    private void connectCenter() {
        try (JsonReader reader = Json.createReader(new FileReader("Shop.img"))) {
            JsonObject shopData = reader.readObject();
            
            Integer world = shopData.getInt("gameWorldId", getWorldID());
            if (world != getWorldID()) {
                //this.worldID = world.byteValue();
            }

            this.addr = shopData.getString("PublicIP", "127.0.0.1");
            this.port = (short) shopData.getInt("port", 8787);
            
            JsonObject loginData = shopData.getJsonObject("login");
            if (loginData != null) {
                this.socket = new CenterSocket();
                this.socket.init(loginData);
                this.socket.connect();
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void createAcceptor() {
        this.acceptor = new ShopAcceptor(new InetSocketAddress(addr, port));
        this.acceptor.run();

        Logger.logReport("Socket acceptor started");
    }

    public final ShopAcceptor getAcceptor() {
        return acceptor;
    }
    
    public final CenterSocket getCenter() {
        return socket;
    }

    public Map<Long, Commodity> getCommodity() {
        return this.commodity;
    }

    public Commodity findCommodity(long commoditySN) {
        if (commodity.containsKey(commoditySN)) {
            return commodity.get(commoditySN);
        }
        return null;
    }

    public int getConnectionLimit() {
        return connectionLimit;
    }

    public String getAddr() {
        return addr;
    }

    public final long getNextCashSN() {
        lockCashItemSN.lock();
        try {
            final long cashItemSN = cashItemInitSN.decrementAndGet();

            return cashItemSN;
        } finally {
            lockCashItemSN.unlock();
        }
    }

    public final long getNextSN() {
        lockItemSN.lock();
        try {
            return itemInitSN.get();
        } finally {
            lockItemSN.unlock();
        }
    }

    public short getPort() {
        return port;
    }

    public long getServerStartTime() {
        return serverStartTime;
    }

    public int getWaitingFirstPacket() {
        return waitingFirstPacket;
    }

    public byte getWorldID() {
        return worldID;
    }

    private void initializeCommodity() {
        long time;

        time = System.currentTimeMillis();
        ItemInfo.load();
        Logger.logReport("Loaded Item Info in " + ((System.currentTimeMillis() - time) / 1000.0) + " seconds.");
    
        WzPackage etcDir = new WzFileSystem().init("Etc").getPackage();
        if (etcDir != null) {
            WzProperty img = etcDir.getItem("Commodity.img");
            time = System.currentTimeMillis();
            for (WzProperty imgDir : img.getChildNodes()) {
                Commodity comm = new Commodity();
                comm.setSN(WzUtil.getInt32(imgDir.getNode("SN"), 0));
                comm.setItemID(WzUtil.getInt32(imgDir.getNode("ItemId"), 0));
                comm.setCount(WzUtil.getShort(imgDir.getNode("Count"), 0));
                comm.setPrice(WzUtil.getInt32(imgDir.getNode("Price"), 0));
                comm.setPeriod(WzUtil.getByte(imgDir.getNode("Period"), 0));
                comm.setPriority(WzUtil.getByte(imgDir.getNode("Priority"), 0));
                commodity.put(comm.getSN(), comm);
            }
            etcDir.release();
            Logger.logReport("Loaded Commodity in " + ((System.currentTimeMillis() - time) / 1000.0) + " seconds.");
        }
        etcDir = null;
    }

    private void initializeDB() {
        try (JsonReader reader = Json.createReader(new FileReader("Database.img"))) {
            JsonObject dbData = reader.readObject();

            int dbPort = dbData.getInt("dbPort", 3306);
            String dbName = dbData.getString("dbGameWorld", "orionalpha");
            String dbSource = dbData.getString("dbGameWorldSource", "127.0.0.1");
            String[] dbInfo = dbData.getString("dbGameWorldInfo", "root,").split(",");

            // Construct the instance of the Database
            Database.createInstance(dbName, dbSource, dbInfo[0], dbInfo.length == 1 ? "" : dbInfo[1], dbPort);

            // Load the initial instance of the Database
            Database.getDB().load();

            Logger.logReport("DB configuration parsed successfully");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void initializeItemSN() {
        CommonDB.rawLoadItemInitSN(this.worldID, this.itemInitSN, this.cashItemInitSN);
    }

    @Override
    public void run() {
        connectCenter();
        initializeDB();
        initializeItemSN();
        initializeCommodity();
        createAcceptor();
        Logger.logReport("The Shop Server has been initialized in " + ((System.currentTimeMillis() - serverStartTime) / 1000.0) + " seconds.");
    }

    public void updateItemInitSN() {
        CommonDB.rawUpdateItemInitSN(this.worldID, this.itemInitSN, this.cashItemInitSN);
    }
}
