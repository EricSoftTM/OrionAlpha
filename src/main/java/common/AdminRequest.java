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
package common;

/**
 *
 * @author Eric
 */
public class AdminRequest {
	public static final int
			Create = 0,
			Remove = 1,
			IncEXP = 2,
			Ban = 3,
			Block = 4,
			Portal = 5,
			NPCVar = 6,
			NPCVar_Set = 7,
			NPCVar_Get = 8,
			BanishAll = 9,
			AdminEvent = 10,
			Quiz = 11,
			Answer = 12,
			Check = 13,
			Timer = 14,
			Desc = 15;
}
