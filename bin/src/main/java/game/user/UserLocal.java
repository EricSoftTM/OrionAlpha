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

import common.user.UserEffect;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class UserLocal {
    
    /**
     * The (local) user effect packet. 
     * This sends both skill and level-up effects to the user using them.
     * 
     * @param userEffect The type of user effect (@see game.user.User.UserEffect)
     * @param args The optional arguments (nSkillID and nSLV for skill effects)
     * 
     * @return The local user effect packet
     */
    public static OutPacket onEffect(byte userEffect, int... args) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEffectLocal);
        packet.encodeByte(userEffect);
        switch (userEffect) {
            case UserEffect.LevelUp:
                break;
            case UserEffect.SkillUse:
                packet.encodeInt(args[0]);
                packet.encodeByte(args[1]);
                break;
            case UserEffect.SkillAffected:
                packet.encodeInt(args[0]);
                packet.encodeByte(args[1]);
                break;
        }
        return packet;
    }
}
