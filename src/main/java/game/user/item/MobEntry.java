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
package game.user.item;

/**
 *
 * @author Eric
 */
public class MobEntry {
	private int mobTemplateID;
	private int prob;
	
	public MobEntry(int templateID, int prob) {
		this.mobTemplateID = templateID;
		this.prob = prob;
	}
	
	public int getMobTemplateID() {
		return mobTemplateID;
	}
	
	public int getProb() {
		return prob;
	}
}
