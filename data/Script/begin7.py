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
Author: Brookie
NPC: Shanks
Script: Sending the character to Victoria Island
'''

ret = self.askYesNo("Take this ship and you'll head off to a bigger continent.#e For 150 mesos#n, I'll take you to #bVictoria Island#k. The thing is, once you leave this place, you can't ever come back. What do you think? Do you want to go to Victoria Island?")
if ret == 0:
	self.say("Hmm... I guess you still have things to do here?")
else:
	if self.userGetLevel() < 7:
		self.say("Let's see... I don't think you are strong enough. You'll have to be at least #bLevel 7#k to go to Victoria Island.")
	else:
		self.sayNext("Bored of this place? Here... Give me 150 mesos first...")
		if self.userIncMoney(-150, True) == False:
			self.say("What? You're telling me you wanted to go without any money? You're one weirdo...")
		else:
			self.sayNext("Awesome! #e150 mesos#n accepted! Alright, off to Victoria Island!")
			self.registerTransferField(104000000, "maple00")

