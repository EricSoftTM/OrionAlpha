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
NPC: Sera
Quest: Heena and Sera
'''

# TODO: Implement Heena and Sera quest handling.
prompt = "It is a fine day for laundry. Wouldn't you agree?"
self.say(prompt)

'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Heena and Sera#l#k") 
if sel == 0:
	self.say("Oh, hello there! You must be a new traveler! Did you just talk to Heena?")
	# TODO: Implement quest control on backend. => Set quest progress to completed.
	self.userIncEXP(5, False)
	self.sayNext("Maple Island may be small, but it's also the perfect place for me to meet new travelers. There's nothing quite like making new friends. " + self.getUser().getCharacterName() + ", hopefully you will make lots of new friends here too.")
	self.say("To continue, use the #bportal#k located to the right. See that spot on the ground that's glowing? That's the portal. Step in it and press the #bUp#k arrow key. I'll now bid you adieu. May your journey be blessed!")
'''