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
package game.party;

import java.util.ArrayList;
import java.util.List;

import game.field.Field;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class PartyMember {
    private final List<Integer> characterID, fieldID;
    private final List<String> characterName;
    private int partyBossCharacterID;
    
    public PartyMember() {
        this.characterID = new ArrayList<>(6);
        this.characterName = new ArrayList<>(6);
        this.fieldID = new ArrayList<>(6);
        
        for (int i = 0; i < 6; i++) {
            characterID.add(i, 0);
            characterName.add(i, "");
            fieldID.add(i, Field.Invalid);
        }
    }
    
    public void encode(OutPacket packet) {
        for (int id : characterID) {
            packet.encodeInt(id);
        }
        for (String name : characterName) {
            packet.encodeString(name, 13);
        }
        for (int field : fieldID) {
            packet.encodeInt(field);
        }
    }
    
    public int getCharacterID(String name) {
        int idx = findIndex(name);
        if (idx == -1) {
            return 0;
        } else {
            return characterID.get(idx);
        }
    }
    
    public List<Integer> getFieldID() {
        return fieldID;
    }
    
    public List<Integer> getCharacterID() {
        return characterID;
    }
    
    public List<String> getCharacterName() {
        return characterName;
    }
    
    public int getMemberCount() {
        int count = 0;
        for (int id : characterID) {
            if (id != 0) {
                count++;
            }
        }
        return count;
    }
    
    public int getPartyBoss() {
        return partyBossCharacterID;
    }
    
    public int findIndex(String name) {
        for (int i = 0; i < characterName.size(); i++) {
            if (characterName.get(i).toLowerCase().equals(name.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }
    
    public void setPartyBoss(int characterID) {
        this.partyBossCharacterID = characterID;
    }
}
