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
NPC: Nina
Quest: Nina's Brother Sen
'''

# TODO: Implement Nina's Brother Sen quest handling.
prompt = "I wonder what #bSen#k is up to..."
self.say(prompt)

'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Nina's Brother Sen#l#k")
if sel == 0:
	self.sayNext("Oh, hello traveler! Good thing you're here, I need your help! Head to the right a bit, and you'll see a #bhouse with a yellow roof#k. That's my house, and my baby brother #bSen#k is the only one inside right now.")
	ret = self.askYesNo("Sen did a great job taking care of the house by himself. He deserves a reward, but I don't know what to give him. Maybe something delicious... Can you ask him what he'd like to eat?")
	if ret == False:
		self.say("Oh, you must be busy. Wouldn't it be fun to get to know some others, though?")
	else:
		self.say("Thanks. To enter the house, press the #bUp arrow#k on your keyboard in front of the house, just like when you use a portal.")
		# TODO: Implement quest control on backend. => Set quest progress to started.
'''