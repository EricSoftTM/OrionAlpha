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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Our global Database operation handling. 
 * Pools connections and accesses raw database operations.
 * 
 * @author Eric
 */
public class Database extends UnifiedDB {
    private static Database instance;
    
    /**
     * Constructs a database provided given credentials within the local JVM.
     *
     */
    private Database(String dbName, String serverName, String user, String passwd, int port) {
        super(dbName, serverName, user, passwd, port);
    }
    
    /**
     * Constructs a default database within the local JVM.
     * 
     * This is the only real database that should be ever
     * constructed within this JVM, using the above info.
     * 
     * @param dbName The database schema name
     * @param serverName The database's server host to connect to
     * @param user The username credential to login to
     * @param passwd The password credential
     * @param port The database's server port to connect to
     * 
     * @return The active Database instance for this JVM.
     */
    public static Database createInstance(String dbName, String serverName, String user, String passwd, int port) {
        if (instance == null) {
            instance = new Database(dbName, serverName, user, passwd, port);
        }
        return instance;
    }
    
    /**
     * Initializes and/or retrieves the official instance 
     * of this JVM's Database. Used for all UnifiedDB
     * parameters throughout the source when accessing
     * the Database or a DB Accessor.
     * 
     * @return The current database
     */
    public static Database getDB() {
        if (instance == null) {
            throw new RuntimeException("Database instance was not yet created.");
        }
        return instance;
    }
    
    private static int bind(PreparedStatement propSet, Object... commands) {
        for (int i = 1; i <= commands.length; i++) {
            Object command = commands[i - 1];
            if (command != null) {
                try {
                    if (command instanceof Number) {
                        // Specific to only setByte calls, default Integer
                        if (command instanceof Byte) {
                            propSet.setByte(i, (Byte) command);
                        } else if (command instanceof Short) {
                            propSet.setShort(i, (Short) command);
                        // Specific to only setLong calls, default Integer
                        } else if (command instanceof Long) {
                            propSet.setLong(i, (Long) command);
                        } else if (command instanceof Double) {
                            propSet.setDouble(i, (Double) command);
                        // Almost all types are INT(11), so default to this
                        } else {
                            propSet.setInt(i, (Integer) command);
                        }
                    // If it is otherwise a String, we only require setString
                    } else if (command instanceof String) {
                        propSet.setString(i, (String) command);
                    } else if (command instanceof Boolean) {
                        propSet.setBoolean(i, (Boolean) command);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace(System.err);
                }
            } else {
                return -4;
            }
        }
        return 1;
    }
    
    public static int execute(Connection con, PreparedStatement propSet, Object... commands) throws SQLException {
        if (propSet != null) {
            int result = bind(propSet, commands);
            
            if (result > 0) {
                int rowsAffected = propSet.executeUpdate();
                if (rowsAffected == 0) {
                    String query = propSet.toString();
                    // The only valid DML statement for re-insertion is UPDATE.
                    if (!query.contains("DELETE FROM") && !query.contains("INSERT INTO")) {
                        // Substring based on if the query contains '?' IN params or not
                        if (query.contains("', parameters"))
                            query = query.substring(query.indexOf("UPDATE"), query.indexOf("', parameters"));
                        else
                            query = query.substring(query.indexOf("UPDATE"));

                        // Begin the new query, starting by converting an update to an insert
                        String newQuery = query.replaceAll("UPDATE", "INSERT INTO");

                        // Substring the FRONT rows (prior to WHERE condition)
                        String rows;
                        if (newQuery.contains("WHERE"))
                            rows = newQuery.substring(newQuery.indexOf("SET ") + "SET ".length(), newQuery.indexOf("WHERE "));
                        else
                            rows = newQuery.substring(newQuery.indexOf("SET ") + "SET ".length());
                        // Construct an array of every front row
                        String[] frontRows = rows.replaceAll(" = \\?, ", ", ").replaceAll(" = \\? ", ", ").split(", ");
                        // Not all queries perform an UPDATE with a WHERE condition, allocate empty back rows
                        String[] backRows = { };
                        // If the query does contain a WHERE condition, parse the back rows (everything after WHERE)
                        if (newQuery.contains("WHERE")) {
                            rows = newQuery.substring(newQuery.indexOf("WHERE ") + "WHERE ".length());
                            backRows = rows.replaceAll(" = \\? AND ", ", ").replaceAll(" = \\?", ", ").split(", ");
                        }
                        // Merge the front and back rows into one table, these are all columns being inserted
                        String[] rowData = new String[frontRows.length + backRows.length];
                        System.arraycopy(frontRows, 0, rowData, 0, frontRows.length);
                        System.arraycopy(backRows, 0, rowData, frontRows.length, backRows.length);

                        // Begin transforming the query - clear the rest of the string, transform to (Col1, Col2, Col3)
                        newQuery = newQuery.substring(0, newQuery.indexOf("SET "));
                        newQuery += "(";
                        for (String row : rowData) {
                            newQuery += row + ", ";
                        }
                        // Trim the remaining , added at the end of the last column
                        newQuery = newQuery.substring(0, newQuery.length() - ", ".length());

                        // Begin appending the VALUES(?, ?) for the total size there is rows
                        newQuery += ") VALUES(";
                        for (String row : rowData) {
                            newQuery += "?, ";
                        }
                        // Trim the remaining , added at the end of the last column
                        newQuery = newQuery.substring(0, newQuery.length() - ", ".length());
                        newQuery += ")";
                        
                        return execute(con, con.prepareStatement(newQuery), commands);
                    }
                }
                return rowsAffected;
            }
            return result;
        }
        return -1;
    }
}
