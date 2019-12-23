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
NPC: Heena
Script: New Mapler
Quest: Heena and Sera
'''

# TODO: Implement Heena and Sera quest handling.
prompt = "You must be new! Allow me to help. To move, use the #bleft and right arrows#k. To jump, press the #bAlt#k key.  To speak to NPCs, simply double-click on them. Easy, right? Oh, sometimes you'll have to climb ladders or ropes. To do so, use the #bup arrow#k next to the object you want to climb!"
self.say(prompt)

'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Heena and Sera#l#k") 
if sel == 0:
	self.sayNext("Hello! You must be new! Your name is " + self.getUser().getCharacterName() + "? You might feel a little weird stepping into Maple World, but don't worry about a thing! Maple World welcomes you with open arms.")
	self.sayNext("To start a conversation with people in this world, just #bdouble-click#k on them. You can talk to any person that displays a blinking green icon in your Mini Map.")
	self.sayNext("#bAnyone with a light bulb over their head has a quest for you.#k These people need your help, so don't hesitate to click on them!")
	ret = self.askYesNo("I just remembered that #bSera#k has been looking for a traveler to talk to. Do you want to say hello to Sera?")
	if ret == 0:
		self.say("Don't be shy! We're all eager to see you.")
	else:
		# TODO: Implement quest control on backend. => Set quest progress to started.
		self.sayNext("You've just accepted a quest! Now, check the #bMinimap#k on the left side of the screen. Press #bM#k to open/close it. It displays various colors to indiciate different people, and the blinking green figures signify residents of this world.")
		self.say("If you look at the Minimap right now, you'll see two green spots. The left one is me, Heena, and the right one is Sera. All you need to do is move to the #bright#k, and you'll see Sera hanging her laundry.")
'''
