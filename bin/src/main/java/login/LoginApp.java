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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import network.LoginAcceptor;
import util.Logger;

/**
 *
 * @author Eric
 */
public class LoginApp implements Runnable {
    private static final LoginApp instance = new LoginApp();
    
    private LoginAcceptor acceptor;
    private final List<WorldEntry> worlds;
    public final long serverStartTime;
    public String addr;
    public int port = 8484;
    
    public LoginApp() {
        this.worlds = new ArrayList<>();
        this.serverStartTime = System.currentTimeMillis();
    }
    
    public final LoginAcceptor getAcceptor() {
        return acceptor;
    }
    
    public static LoginApp getInstance() {
        return instance;
    }
    
    public void addWorld(WorldEntry world) {
        this.worlds.add(world);
    }
    
    private void createAcceptor() {
        acceptor = new LoginAcceptor(new InetSocketAddress(port));
        acceptor.run();
    }
    
    public final List<WorldEntry> getWorlds() {
        return this.worlds;
    }
    
    private void initializeCenter() {
        
    }
    
    private void initializeDB() {
        
    }
    
    public static void main(String[] args) {
        LoginApp.getInstance().run();
    }
    
    @Override
    public void run() {
        try {
            Logger.logReport("MapleStory Global Service WvsLogin.exe.");
            setUp();
            //TimerThread.World.Register(new RankingWorker(), RankingWorker.RANKING_INTERVAL);
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
        // CreateTimerThread
    }
}
