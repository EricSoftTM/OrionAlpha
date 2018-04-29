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

import network.GameAcceptor;
import util.Logger;

/**
 *
 * @author Eric
 */
public class GameApp implements Runnable {
    private static final GameApp instance = new GameApp();
    
    private GameAcceptor acceptor;
    public final long serverStartTime;
    
    public GameApp() {
        serverStartTime = System.currentTimeMillis();
    }
    
    public static GameApp getInstance() {
        return instance;
    }
    
    public void createAcceptor() {
        
    }
    
    public final GameAcceptor getAcceptor() {
        return acceptor;
    }
    
    public void initializeGameData() {
        long tCur;
        
        // Load Items and Equipment
        
        // Load Skills
        
        // Load Mobs
        tCur = System.currentTimeMillis();
        
        Logger.logReport("Loaded Mob Attributes in " + ((System.currentTimeMillis() - tCur) / 1000.0) + " seconds.");
        
        // Load Npcs
        tCur = System.currentTimeMillis();
        
        Logger.logReport("Loaded Npc Attributes in " + ((System.currentTimeMillis() - tCur) / 1000.0) + " seconds.");
        
        // Load Maps
        tCur = System.currentTimeMillis();
        
        Logger.logReport("Loaded map (field) data from map files in " + ((System.currentTimeMillis() - tCur) / 1000.0) + " seconds.");
    }
    
    public static void main(String[] args) {
        GameApp.getInstance().run();
    }
    
    @Override
    public void run() {
        // CreateTimerThread
        
        // Database.Load
        
        initializeGameData();
        createAcceptor();
        Logger.logReport("The Game Server has been initialized in " + ((System.currentTimeMillis() - serverStartTime) / 1000.0) + " seconds.");
    }
}
