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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Eric
 */
public class WzSAXProperty {
    private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();
    
    private File file;
    private List<WzXML> entities;
    
    public WzSAXProperty(File file) {
        this.file = file;
    }
    
    public void addEntity(WzXML entity) {
        if (this.entities == null) {
            this.entities = new ArrayList<>();
        }
        this.entities.add(entity);
    }
    
    public String getFileName() {
        if (file != null) {
            return file.getName();
        }
        return "";
    }
    
    public final void parse() {
        try {
            SAXParser parser = getFactory().newSAXParser();
            
            DefaultHandler handler = new DefaultHandler() {
                String rootElement = null;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attr) {
                    String key = attr.getValue("", "name");
                    String value = attr.getValue("", "value");
                    WzNodeType type = WzNodeType.valueOf(qName.toUpperCase());
                    for (WzXML entity : entities) {
                        if (rootElement == null) {
                            if (key != null && key.contains(".img")) {
                                rootElement = key;
                            }
                        }
                        entity.parse(rootElement, key, value, type);
                    }
                }
            };
            
            parser.parse(file, handler);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public void release() {
        if (this.entities != null) {
            this.entities.clear();
        }
        
        this.file = null;
    }
    
    static SAXParserFactory getFactory() {
        return FACTORY;
    }
}
