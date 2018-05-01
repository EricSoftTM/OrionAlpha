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
package game;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetSocketAddress;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import network.GameAcceptor;
import util.Logger;

/**
 *
 * @author Eric
 */
public class GameApp implements Runnable {
    private static final GameApp instance = new GameApp();
    
    private GameAcceptor acceptor;
    private CenterSocket socket;
    private int connectionLimit;
    public final long serverStartTime;
    
    public GameApp() {
        this.connectionLimit = 4000;
        this.serverStartTime = System.currentTimeMillis();
    }
    
    public static GameApp getInstance() {
        return instance;
    }
    
    private void connectCenter() {
        socket = new CenterSocket();
        socket.connect();
    }
    
    public void createAcceptor() {
        try (JsonReader reader = Json.createReader(new FileReader("Game" + System.getProperty("gameID", "0") + ".img"))) {
            JsonObject gameData = reader.readObject();
            
            String ip = gameData.getString("PublicIP", "127.0.0.1");
            int port = gameData.getInt("port", 8585);
            
            acceptor = new GameAcceptor(new InetSocketAddress(ip, port));
            acceptor.run();
            
            Logger.logReport("Socket acceptor started");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public final GameAcceptor getAcceptor() {
        return acceptor;
    }
    
    public int getConnectionLimit() {
        return connectionLimit;
    }
    
    public void initializeGameData() {
        long time;
        
        // Load Items and Equipment
        
        // Load Skills
        
        // Load Mobs
        time = System.currentTimeMillis();
        
        Logger.logReport("Loaded Mob Attributes in " + ((System.currentTimeMillis() - time) / 1000.0) + " seconds.");
        
        // Load Npcs
        time = System.currentTimeMillis();
        
        Logger.logReport("Loaded Npc Attributes in " + ((System.currentTimeMillis() - time) / 1000.0) + " seconds.");
        
        // Load Maps
        time = System.currentTimeMillis();
        
        Logger.logReport("Loaded map (field) data from map files in " + ((System.currentTimeMillis() - time) / 1000.0) + " seconds.");
    }
    
    public static void main(String[] args) {
        GameApp.getInstance().run();
    }
    
    @Override
    public void run() {
        // CreateTimerThread
        
        // Database.Load
        
        initializeGameData();
        connectCenter();
        createAcceptor();
        Logger.logReport("The Game Server has been initialized in " + ((System.currentTimeMillis() - serverStartTime) / 1000.0) + " seconds.");
    }
    
    public void setConnectionLimit(int limit) {
        this.connectionLimit = limit;
    }
}
