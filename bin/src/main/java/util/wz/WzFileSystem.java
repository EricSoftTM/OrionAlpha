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
package util.wz;

import java.io.File;
import util.Logger;

/**
 *
 * @author Eric
 */
public class WzFileSystem {
    private WzPackage root;
    
    public WzFileSystem() {
        
    }
    
    public WzPackage getPackage() {
        return root;
    }
    
    public WzFileSystem init(String name) {
        File file = new File(System.getProperty("wzpath") + name);
        if (!file.isDirectory() || file.getName().endsWith(".xml")) {
            Logger.logError("Invalid filesystem initialized.");
            return null;
        }
        root = new WzPackage();
        
        createChildPackage(root, file);
        return this;
    }
    
    private void createChildPackage(WzPackage p, File f) {
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                createChildPackage(p.addPackage(file), file);
            } else {
                if (!file.getName().endsWith(".xml")) {
                    Logger.logError("Unidentified file: %s", file.getAbsolutePath());
                    continue;
                }
                p.addEntry(file);
            }
        }
    }
}
