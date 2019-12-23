'''
This file is part of OrionAlpha, a MapleStory Emulator Project.
Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
'''

'''
Author: Eric
NPC: Blackbull
Quest: Fixing Blackbull's House
'''

# TODO: Implement Fixing Blackbull's House quest handling.
prompt = "Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so..."
self.say(prompt)

'''
	This doesn't exist in BMS nor Orion, so all we have are old references.

	=> Dialogue
		=> AskYesNo
			=> Start Quest

	Quest:
		- 30 Tree Branch from Stump
		- 50 Firewood from Axe Stump or Dark Axe Stump

	=> Complete Quest
		=> Rewards

	Rewards:
		- 50 EXP
		- 1 Steel Shield or 1 Red Triangular Shield (Random Reward)
'''