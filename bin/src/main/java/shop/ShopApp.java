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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import network.ShopAcceptor;
import network.database.Database;
import util.Logger;

/**
 *
 * @author Eric
 */
public class ShopApp implements Runnable {

    private static final ShopApp instance = new ShopApp();

    private ShopAcceptor acceptor;
    private byte worldID;
    private int waitingFirstPacket;
    private int connectionLimit;
    private final long serverStartTime;
    private final AtomicLong itemInitSN;
    private final AtomicLong cashItemInitSN;
    private final Lock lockItemSN;
    private final Lock lockCashItemSN;
    private short port;
    private String ip;

    public ShopApp() {
        this.waitingFirstPacket = 1000 * 15;
        this.connectionLimit = 1000;
        this.serverStartTime = System.currentTimeMillis();
        this.itemInitSN = new AtomicLong(0);
        this.cashItemInitSN = new AtomicLong(0);
        this.lockItemSN = new ReentrantLock();
        this.lockCashItemSN = new ReentrantLock();
    }

    public static final ShopApp getInstance() {
        return instance;
    }

    private void createAcceptor() {
        try (JsonReader reader = Json.createReader(new FileReader("Shop.img"))) {
            JsonObject shopData = reader.readObject();

            ip = shopData.getString("PublicIP", "127.0.0.1");
            port = (short) shopData.getInt("port", 8787);
            acceptor = new ShopAcceptor(new InetSocketAddress(ip, port));
            acceptor.run();

            Logger.logReport("Socket acceptor started");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public final ShopAcceptor getAcceptor() {
        return acceptor;
    }

    public final long getNextCashSN() {
        lockCashItemSN.lock();
        try {
            final long cashItemSN = cashItemInitSN.incrementAndGet();

            return cashItemSN;
        } finally {
            lockCashItemSN.unlock();
        }
    }

    public final long getNextSN() {
        lockItemSN.lock();
        try {
            final long itemSN = itemInitSN.incrementAndGet();

            return itemSN;
        } finally {
            lockItemSN.unlock();
        }
    }

    public short getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public int getConnectionLimit() {
        return connectionLimit;
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
        //ShopDB.rawLoadItemInitSN(this.worldID, this.itemInitSN, this.cashItemInitSN);
    }

    public static void main(String[] args) {
        ShopApp.getInstance().run();
    }

    @Override
    public void run() {
        initializeDB();
        initializeItemSN();
        createAcceptor();
        Logger.logReport("WvsShop has been initialized in " + ((System.currentTimeMillis() - serverStartTime) / 1000.0) + " seconds.");
    }

    public void updateItemInitSN() {
        //ShopDB.rawUpdateItemInitSN(this.worldID, this.itemInitSN, this.cashItemInitSN);
    }
}
