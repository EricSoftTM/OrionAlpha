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
package network.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import util.Logger;

/**
 * Our ConnectionPool and globally used Database Connection handler.
 * All Connections are to be used from PoolConnection() and closed after.
 * Upon shutdown, Close() is to be called to shutdown the DataSource.
 * 
 * @author Eric
 */
public class UnifiedDB {
    public static final int 
            // Maximum concurrent pools
            MAXIMUM_POOL_SIZE   = 20,
            // Default connection port
            DefaultPort         = 3306
    ;
    public static final String 
            // Supported drivers available
            Driver_MySQL        = "mysql",
            Driver_MariaDB      = "mariadb",
            // Default connection server host
            DefaultHost         = "localhost"
    ;
    
    private HikariDataSource dataSource;
    private final String user;
    private final String password;
    private final String dbName;
    private final String serverName;
    private final int port;
    
    public UnifiedDB(String dbName, String user, String passwd) {
        this.dbName = dbName;
        this.serverName = DefaultHost;
        this.user = user;
        this.password = passwd;
        this.port = DefaultPort;
    }
    
    public UnifiedDB(String dbName, String serverName, String user, String passwd, int port) {
        this.dbName = dbName;
        this.serverName = serverName;
        this.user = user;
        this.password = passwd;
        this.port = port;
    }
    
    /**
     * Load the DataSource upon startup for use to access the object.
    */
    public final void load() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            // DB Config
            // While it is preferred to use a DataSourceClassName, HikariCP optionally allows the JdbcURL option. :)
            config.setJdbcUrl(String.format("jdbc:%s://%s:%d/%s", Driver_MariaDB, serverName, port, dbName));
            
            config.setUsername(user);
            config.setPassword(password);
            
            // DB Options
            config.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
            config.setAutoCommit(true);
            //config.setIdleTimeout(10000);
            //config.setMaxLifetime(1800000);
            
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("cachePrepStmts", "true");//useServerPrepStmts
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("autoReconnect", "true");
            
            dataSource = new HikariDataSource(config);
        }
    }
    
    /**
     * Pools a Connection object from the Data Source.
     * To configure maximum pooled connections, change constant 'MAXIMUM_POOL_SIZE'.
     * 
     * Connection timeout properties are set to the HikariCP library defaults.
     * 
     * @return A new java.sql.Connection object. 
     */
    public Connection poolConnection() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            }
            Logger.logError("Attempting to make a connection before loading the database.");
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    /**
     * Properly close the DataSource.
     * This should ONLY be called when shutting down the center servers.
     * 
     * -> shutdown is now deprecated, resort to close.
     */
    public void close() {
        dataSource.close();
    }
}
