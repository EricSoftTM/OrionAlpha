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

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Eric
 */
public class WzProperty {
    private File file;
    private Node node;
    private List<WzProperty> props;
    
    public WzProperty(File file) {
        this.file = file;
        
        serialize();
    }
    
    private WzProperty(Node node) {
        this.node = node;
    }
    
    private void serialize() {
        try {
            this.node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getFirstChild();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public List<WzProperty> getChildNodes() {
        if (props == null) {
            props = new ArrayList<>();
        } else {
            props.clear();
        }
        
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
                props.add(new WzProperty(child));
            }
        }
        
        return props;
    }
    
    public WzProperty getNode(String path) {
        if (node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes()) {
            if (path.split("/")[0].equals("..")) {
                return getParentNode().getNode(path.substring(path.indexOf("/") + 1));
            }
            
            Node subNode = node;
            for (String curPath : path.split("/")) {
                boolean foundSubNode = false;
                
                NodeList list = subNode.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node subnode = list.item(i);
                    if (subnode.getNodeType() == Node.ELEMENT_NODE && subnode.getAttributes().getNamedItem("name").getNodeValue().equals(curPath)) {
                        foundSubNode = true;
                        subNode = subnode;
                    }
                }
                if (!foundSubNode) return null;
            }
            return new WzProperty(subNode);
        }
        return null;
    }
    
    public Object getNodeValue() {
        switch (getNodeType()) {
            case Short:
                return Short.parseShort(node.getAttributes().getNamedItem("value").getNodeValue());
            case Int:
                return Integer.parseInt(node.getAttributes().getNamedItem("value").getNodeValue());
            case Float:
                return Float.parseFloat(node.getAttributes().getNamedItem("value").getNodeValue());
            case Double:
                return Double.parseDouble(node.getAttributes().getNamedItem("value").getNodeValue());
            case String:
            case WzUOL:
                return node.getAttributes().getNamedItem("value").getNodeValue();
            case WzVector2D:
                return new Point(Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue()), Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue()));
            case WzCanvas:
            case WzConvex2D:
        }
        return null;
    }
    
    public String getNodeName() {
        return node.getAttributes().getNamedItem("name").getNodeValue();
    }
    
    public WzNodeType getNodeType() {
        switch (node.getNodeName()) {
            case "imgdir":
                return WzNodeType.WzProperty;
            case "canvas":
                return WzNodeType.WzCanvas;
            case "convex":
                return WzNodeType.WzConvex2D;
            case "vector":
                return WzNodeType.WzVector2D;
            case "sound":
                return WzNodeType.WzSound;
            case "uol":
                return WzNodeType.WzUOL;
            case "short":
                return WzNodeType.Short;
            case "int":
                return WzNodeType.Int;
            case "float":
                return WzNodeType.Float;
            case "double":
                return WzNodeType.Double;
            case "string":
                return WzNodeType.String;
        }
        return null;
    }
    
    public WzProperty getParentNode() {
        Node parent = node.getParentNode();
        if (parent.getNodeType() != Node.DOCUMENT_NODE) {
            return new WzProperty(parent);
        }
        return null;
    }
    
    public final void release() {
        int children = node.getChildNodes().getLength();
        if (children > 0) {
            for (int i = 0; i < children; i++) {
                Node child = node.getChildNodes().item(i);
                if (child != null) {
                    node.removeChild(node.getChildNodes().item(i));
                }
            }
        }
        
        if (props != null) {
            props.clear();
        }
        
        this.node = null;
        this.file = null;
        this.props = null;
    }
}
