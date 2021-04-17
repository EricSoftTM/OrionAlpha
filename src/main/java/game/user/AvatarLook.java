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
import common.item.ItemSlotBase;
import common.user.CharacterStat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class AvatarLook {
    public static final int
            Face        = 0x1,
            Look        = 0x2//Hack mask
    ;
    
    private final List<Integer> hairEquip;
    private int weaponStickerID;
    
    public AvatarLook() {
        this.hairEquip = new ArrayList<>();
        for (int i = 0; i <= BodyPart.BP_Count; i++) {
            this.hairEquip.add(i, 0);
        }
        this.weaponStickerID = 0;
    }
    
    public void load(CharacterStat cs, List<ItemSlotBase> equipped, List<ItemSlotBase> equipped2) {
        this.hairEquip.set(0, cs.getHair());
        for (int i = 1; i <= BodyPart.BP_Count; i++) {
            if (equipped2.get(i) != null && i != BodyPart.Weapon) {
                this.hairEquip.set(i, equipped2.get(i).getItemID());
            } else if (equipped.get(i) != null) {
                if (i == BodyPart.Weapon && equipped2.get(i) != null) {
                    this.weaponStickerID = equipped2.get(i).getItemID();
                }
                this.hairEquip.set(i, equipped.get(i).getItemID());
            } else {
                this.hairEquip.set(i, 0);
            }
        }
    }
    
    public void encode(OutPacket packet) {
        for (int pos = 0; pos < hairEquip.size(); pos++) {
            if (hairEquip.get(pos) > 0) {
                packet.encodeByte(pos);
                packet.encodeInt(hairEquip.get(pos));
            }
        }
        packet.encodeByte(-1);
    }
    
    public List<Integer> getEquipped() {
        return hairEquip;
    }
    
    public int getWeaponStickerID() {
        return weaponStickerID;
    }
    
    public final AvatarLook makeClone() {
        AvatarLook al = new AvatarLook();
        al.getEquipped().clear();
        al.getEquipped().addAll(getEquipped());
        return al;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof AvatarLook) {
            AvatarLook al = (AvatarLook) o;
            return this.hairEquip.equals(al.hairEquip);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.hairEquip);
        return hash;
    }
}
