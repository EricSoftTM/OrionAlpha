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
NPC: Luke (also used for Mike, who's NPC exists but isn't on any map yet)
Quest: Luke the Security Guy
'''

# TODO: Implement Luke the Security Guy quest handling.
prompt = "Okay, who just woke me up?? I hate anyone that wakes me up from my nap ... huh? What am I doing? What do you think I'm doing? Of course I'm guarding the entrance!! This is the entrance to the #bVictoria Island : Center Dungeon#k. You have to be careful in there; the monsters you've faced don't even compare to the ones you're about to face in here. I suggest you don't go in there unless you can protect yourself. Okay, nap time!"
self.say(prompt)

'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Luke the Security Guy#l#k")
if sel == 0:
	ret = self.askYesNo("Being a good son, I annually make my mom an extra special, healthy, and tasty dish, but I don't have as much time this year because of my job. Can you get the ingredients I need for my dish?")
	if ret == False:
		self.say("Must be busy, eh? Can't blame you. I am, too. Let me know if you get any free time.")
	else:
		# TODO: Implement quest control on backend. => Set quest progress to started.
		self.say("Alright! This year I'm going to make my mom a tasty Snake Drink! Can you get me #b100 Jr. Necki Skins#k, #b10 Stirge Wings#k, and for the final touch, #b1 Salad#k please?")
'''
