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

import game.user.User.UserEffect;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class UserLocal {
    
    public static OutPacket onEffect(int userEffect, int skillID, int slv) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEffectLocal);
        packet.encodeByte(userEffect);
        switch (userEffect) {
            case UserEffect.LevelUp:
                break;
            case UserEffect.SkillUse:
                packet.encodeInt(skillID);
                packet.encodeByte(slv);
                break;
            case UserEffect.SkillAffected:
                packet.encodeInt(skillID);
                packet.encodeByte(slv);
                break;
        }
        return packet;
    }
}
