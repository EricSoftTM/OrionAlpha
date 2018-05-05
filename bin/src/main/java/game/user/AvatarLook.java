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
package game.user;

import common.item.BodyPart;
import java.util.ArrayList;
import java.util.List;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class AvatarLook {
    public static final int
            Unknown1    = 0x1,
            Look        = 0x2,//Hack mask until I figure this shit out
            Unknown2    = 0x4,
            Unknown3    = 0x8
    ;
    
    private final List<Integer> equipped;
    private final List<Integer> equipped2;
    
    public AvatarLook() {
        this.equipped = new ArrayList<>();
        this.equipped2 = new ArrayList<>();
        for (int i = 0; i < BodyPart.BP_Count + 1; i++) {
            this.equipped.add(i, 0);
            //this.equipped2.add(i, 0);
        }
    }
    
    public void encode(OutPacket packet) {
        for (int pos = 1; pos <= equipped.size(); pos++) {
            if (equipped.get(pos) > 0) {
                packet.encodeByte(pos);
                packet.encodeInt(equipped.get(pos));
            }
        }
        packet.encodeByte(-1);
    }
    
    public List<Integer> getEquipped() {
        return equipped;
    }
    
    public List<Integer> getEquipped2() {
        return equipped2;
    }
}
