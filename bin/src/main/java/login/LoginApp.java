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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import network.CenterAcceptor;
import network.LoginAcceptor;
import network.database.Database;
import util.Logger;

/**
 *
 * @author Eric
 */
public class LoginApp implements Runnable {
    private static final LoginApp instance = new LoginApp();
    
    private LoginAcceptor acceptor;
    private CenterAcceptor centerAcceptor;
    private final List<WorldEntry> worlds;
    public final long serverStartTime;
    private String addr;
    private int port;
    private int centerPort;
    
    public LoginApp() {
        this.worlds = new ArrayList<>();
        this.serverStartTime = System.currentTimeMillis();
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
    
    private void createAcceptor() {
        acceptor = new LoginAcceptor(new InetSocketAddress(addr, port));
        acceptor.run();
    }
    
    private void createCenterAcceptor() {
        centerAcceptor = new CenterAcceptor(new InetSocketAddress(addr, centerPort));
        centerAcceptor.run();
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
    
    public void setUp() {
        initializeDB();
        initializeCenter();
        createAcceptor();
        createCenterAcceptor();
        // CreateTimerThread
    }
}
