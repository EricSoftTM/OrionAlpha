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
package login;

import game.user.item.ItemInfo;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import login.user.item.NewEquip;
import network.CenterAcceptor;
import network.LoginAcceptor;
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
public class LoginApp implements Runnable {
    private static final LoginApp instance = new LoginApp();
    
    private LoginAcceptor acceptor;
    private CenterAcceptor centerAcceptor;
    private ShopEntry shop;
    private final List<WorldEntry> worlds;
    private final List<NewEquip> newEquip;
    private final List<String> forbiddenNames;
    private final long serverStartTime;
    private final AtomicLong cashItemInitSN;
    private final AtomicLong itemInitSN;
    private final Lock lockCashItemSN;
    private final Lock lockItemSN;
    private String addr;
    private int port;
    private int centerPort;
    
    public LoginApp() {
        this.worlds = new ArrayList<>();
        this.newEquip = new ArrayList<>();
        this.forbiddenNames = new ArrayList<>();
        this.serverStartTime = System.currentTimeMillis();
        this.itemInitSN = new AtomicLong(0);
        this.cashItemInitSN = new AtomicLong(0);
        this.lockItemSN = new ReentrantLock();
        this.lockCashItemSN = new ReentrantLock();
    }
    
    public final LoginAcceptor getAcceptor() {
        return acceptor;
    }
    
    public final CenterAcceptor getCenterAcceptor() {
        return centerAcceptor;
    }
    
    public static LoginApp getInstance() {
        return instance;
    }
    
    public void addWorld(WorldEntry world) {
        this.worlds.add(world);
    }
    
    public boolean checkCharEquip(int gender, int type, int itemID) {
        for (NewEquip equip : newEquip) {
            if (equip.getGender() == gender && equip.getType() == type && equip.getItemID() == itemID) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkCharName(String charName, boolean wordCheck) {
        // Check if the name is null, empty, or has spaces
        if (charName == null || charName.isEmpty() || charName.contains(" ")) {
            return false;
        }
        // Check if the name contains invalid symbols (excluding korean)
        for (char c : charName.toCharArray()) {
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9')) {
                if ((c & 0x80) == 0 || c < 176 || c > 200)
                    return false;
            }
        }
        // Check if the name is forbidden
        if (wordCheck) {
            for (String forbiddenName : forbiddenNames) {
                if (charName.toLowerCase().contains(forbiddenName)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void createAcceptor() {
        this.acceptor = new LoginAcceptor(new InetSocketAddress(addr, port));
        this.acceptor.run();
    }
    
    private void createCenterAcceptor() {
        this.centerAcceptor = new CenterAcceptor(new InetSocketAddress(addr, centerPort));
        this.centerAcceptor.run();
    }
    
    public WorldEntry getWorld(int worldID) {
        for (WorldEntry world : getWorlds()) {
            if (world.getWorldID() == worldID) {
                return world;
            }
        }
        return null;
    }
    
    public final List<WorldEntry> getWorlds() {
        return this.worlds;
    }
    
    public final long getNextCashSN() {
        lockCashItemSN.lock();
        try {
            return cashItemInitSN.get();
        } finally {
            lockCashItemSN.unlock();
        }
    }

    public final long getNextSN() {
        lockItemSN.lock();
        try {
            final long itemSN = itemInitSN.decrementAndGet();

            return itemSN;
        } finally {
            lockItemSN.unlock();
        }
    }
    
    public long getServerStartTime() {
        return serverStartTime;
    }
    
    public ShopEntry getShop() {
        return shop;
    }
    
    private void initializeCenter() {
        try (JsonReader reader = Json.createReader(new FileReader("Login.img"))) {
            JsonObject loginData = reader.readObject();
            
            this.port = loginData.getInt("port", 8484);
            this.centerPort = loginData.getInt("centerPort", 8383);
            this.addr = loginData.getString("PublicIP", "127.0.0.1");
            
            Logger.logReport("Login configuration parsed successfully");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
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
        CommonDB.rawLoadItemInitSN(-1, this.itemInitSN, this.cashItemInitSN);
    }
    
    private void loadNewCharInfo() {
        WzPackage etcDir = new WzFileSystem().init("Etc").getPackage();
        if (etcDir != null) {
            WzProperty forbidden = etcDir.getItem("ForbiddenName.img");
            if (forbidden != null) {
                for (WzProperty name : forbidden.getChildNodes()) {
                    this.forbiddenNames.add(WzUtil.getString(name, "-"));
                }
            }
            
            WzProperty makeChar = etcDir.getItem("MakeCharInfo.img");
            if (makeChar != null) {
                for (WzProperty makeGender : makeChar.getChildNodes()) {
                    int gender = (makeGender.getNodeName().equals("CharMale") ? 0 : 1);
                    for (WzProperty makeType : makeGender.getChildNodes()) {
                        for (WzProperty makeInfo : makeType.getChildNodes()) {
                            this.newEquip.add(new NewEquip(gender, Byte.parseByte(makeType.getNodeName()), WzUtil.getInt32(makeInfo, 0)));
                        }
                    }
                }
            }
            
            etcDir.release();
        }
        etcDir = null;
    }
    
    public static void main(String[] args) {
        LoginApp.getInstance().run();
    }
    
    @Override
    public void run() {
        try {
            Logger.logReport("MapleStory Korea Service WvsLogin.exe.");
            setUp();
            Logger.logReport("WvsLogin has been initialized in " + ((System.currentTimeMillis() - serverStartTime) / 1000.0) + " seconds.");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(0);
        }
    }
    
    public void setShop(ShopEntry shop) {
        this.shop = shop;
    }
    
    public void setUp() {
        loadNewCharInfo();
        initializeDB();
        initializeItemSN();
        initializeCenter();
        createAcceptor();
        createCenterAcceptor();
        
        ItemInfo.load();
        // CreateTimerThread
    }
    
    public void updateItemInitSN() {
        CommonDB.rawUpdateItemInitSN(-1, this.itemInitSN, this.cashItemInitSN);
    }
}
