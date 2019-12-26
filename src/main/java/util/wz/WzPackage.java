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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Eric
 */
public class WzPackage {
    private final Map<String, WzPackage> children;
    private final Map<String, WzProperty> entries;
    private final Map<String, WzSAXProperty> saxEntries;
    
    public WzPackage() {
        this.children = new ConcurrentHashMap<>();
        this.entries = new ConcurrentHashMap<>();
        this.saxEntries = new ConcurrentHashMap<>();
    }
    
    public WzPackage addPackage(File file) {
        WzPackage pkg = new WzPackage();
        children.put(file.getName(), pkg);
        return pkg;
    }
    
    public void addEntry(File file) {
        entries.put(file.getName().replaceAll(".xml", ""), new WzProperty(file));
        saxEntries.put(file.getName().replaceAll(".xml", ""), new WzSAXProperty(file));
    }
    
    public Map<String, WzPackage> getChildren() {
        return children;
    }
    
    public Map<String, WzProperty> getEntries() {
        return entries;
    }
    
    public Map<String, WzSAXProperty> getSAXEntries() {
        return saxEntries;
    }
    
    public WzProperty getItem(String path) {
        WzPackage pkg = this;
        while (path.contains("/")) {
            String dir = path.substring(0, path.indexOf("/"));
            if (pkg.children.containsKey(dir)) {
                pkg = pkg.children.get(dir);
                
                path = path.substring(path.indexOf("/") + 1);
            } else {
                return null;
            }
        }
        return pkg.entries.get(path);
    }
    
    public WzSAXProperty getSAXItem(String path) {
        WzPackage pkg = this;
        while (path.contains("/")) {
            String dir = path.substring(0, path.indexOf("/"));
            if (pkg.children.containsKey(dir)) {
                pkg = pkg.children.get(dir);
        
                path = path.substring(path.indexOf("/") + 1);
            } else {
                return null;
            }
        }
        return pkg.saxEntries.get(path);
    }
    
    public final void release() {
        for (Iterator<Map.Entry<String, WzPackage>> it = children.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, WzPackage> p = it.next();
            if (p != null && p.getValue() != null) {
                p.getValue().release();
            }
            it.remove();
        }
        for (Iterator<Map.Entry<String, WzProperty>> it = entries.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, WzProperty> p = it.next();
            if (p != null && p.getValue() != null) {
                p.getValue().release();
            }
            it.remove();
        }
        for (Iterator<Map.Entry<String, WzSAXProperty>> it = saxEntries.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, WzSAXProperty> p = it.next();
            if (p != null && p.getValue() != null) {
                p.getValue().release();
            }
            it.remove();
        }
        
        this.children.clear();
        this.entries.clear();
        this.saxEntries.clear();
    }
}
