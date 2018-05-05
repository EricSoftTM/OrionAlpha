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
package game.user.stat;

import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class SecondaryStat {
    public int pad;
    public int pdd;
    public int mad;
    public int mdd;
    public int acc;
    public int eva;
    public int craft;
    public int speed;
    public int jump;
    
    public void encodeForLocal(OutPacket packet) {
        packet.encodeInt(0);
    }
    
    public void encodeForRemote(OutPacket packet, int flag) {
        packet.encodeInt(flag);
        // TODO: Stat values. Data (i think) is SkillID | (SLV << 16)
        if ((flag & CharacterTemporaryStat.PAD) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.PDD) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.MAD) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.MDD) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.ACC) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.EVA) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.Craft) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.Speed) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.Jump) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.MagicGuard) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.DarkSight) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.Booster) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.PowerGuard) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.MaxHP) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.MaxMP) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.Invincible) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.SoulArrow) != 0) {
            packet.encodeInt(0);
        }
        if ((flag & CharacterTemporaryStat.Stun) != 0) {
            packet.encodeInt(0);
        }
    }
}
