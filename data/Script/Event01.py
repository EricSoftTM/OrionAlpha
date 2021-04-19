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
NPC: Jean
Script: MapleStory Event Assistant
'''

cmap = self.userGetFieldID();

# TODO: Implement all necessary options for the various event systems.
# Don't have the time nor motivation to handle all of this :|
if self.userIsSuperGM() == False:
	sel = self.askMenu("Please select an option.\r\n#b#L0# Choose Event Map#l\r\n#L1# Check the number of users in the Event Map#l#k")
	if sel == 0:
		sel = self.askMenu("Select an event. \r\n#b#L0# Ola Ola 1 ( 109030001 )#l\r\n#L1# Ola Ola 2 ( 109030101 )#l\r\n#L2# Ola Ola 3 ( 109030201 )#l\r\n#L3# Ola Ola 4 ( 109030301 )#l\r\n#L4# Ola Ola 5 ( 109030401 )#l\r\n#L5# MapleStory Physical Fitness Test ( 109040000 )#l\r\n#L6# OX Quiz ( 109020001 )#l\r\n#L11# Treasure Hunt ( 109010000 )#l\r\n#L12# Close Event Map Selection#l\r\n#k")
		if sel == 0:
			self.setIntReg("map", 109030001)
			self.setIntReg("count", 80)
			# field.notice(0, "The event is open. Please click on the Event NPC to enter the Event Map.")
else:
	self.sayNext("Hey, I'm #bJean#k. I am waiting for my brother #bPaul#k. He is supposed to be here by now...")
	self.sayNext("Hmm... what should I do? The event will start, soon... many people went to participate in the event, so we better be hurry...")
	sel = self.askMenu("Hey... why don't you go with me? I think my brother will come with other people.\r\n#L0##e1. #n#bWhat kind of an event is it?#k#l\r\n#L1##e2. #n#bExplain the event game to me.#k#l\r\n#L2##e3. #n#bAlright, let's go!#k#l")
	if sel == 0:
		self.sayNext("All this month OrionAlpha is celebrating its 1st anniversary! The GM's will be holding surprise GM Events throughout the event, so stay on your toes and make sure to participate in at least one of the events for great prizes!")
	elif sel == 1:
		sel = self.askMenu("There are many games for this event. It will help you a lot to know how to play the game before you play it. Choose the one you want to know more of!\r\n#b#L0# Ola Ola#l\r\n#L1# MapleStory Maple Physical Fitness Test#l\r\n#L2# OX Quiz#l")
		if sel == 0:
			self.sayNext("#b[Ola Ola]#k is a game where participants climb ladders to reach the top. Climb your way up and move to the next level by choosing the correct portal out of the numerous portals available.\r\nThe game consists of three levels, and the time limit is #b6 MINUTES#k. During [Ola Ola], you #bwon't be able to jump, teleport, haste, or boost your speed using potions or items#k. There are also trick portals that'll lead you to a strange place, so please be aware of those.");
		elif sel == 1:
			self.sayNext("#b[MapleStory Physical Fitness Test] is a race through an obstacle course#k much like the Forest of Patience. You can win it by overcoming various obstacles and reach the final destination within the time limit.\r\nThe game consists of four levels, and the time limit is #b15 MINUTES#k. During [MapleStory Physical Fitness Test], you won't be able to use teleport or haste.");
		elif sel == 2:
			self.sayNext("#b[OX Quiz]#k is a game of MapleStory smarts through X's and O's. Once you join the game, turn on the minimap by pressing M to see where the X and O are. A total of #r10 questions#k will be given, and the character that answers them all correctly wins the game.\r\nOnce the question is given, use the ladder to enter the area where the correct answer may be, be it X or O. If the character does not choose an answer or is hanging on the ladder past the time limit, the character will be eliminated. Please hold your position until [CORRECT] is off the screen before moving on. To prevent cheating of any kind, all types of chatting will be turned off during the OX quiz.");
	elif sel == 2:
		field = self.getIntReg("map")
		count = self.incIntReg("count", -1)

		strMap = ""
		preMapNum = ""
		if field >= 0:
			strMap = str(field)
			preMapNum = strMap[0:3]

		goEvent = 1
		'''
		cTime = currentTime; 
		if ( val2 == "" ) goEvent = 1; 
		else { 
			aTime = compareTime( cTime, val2 ); 
			if ( aTime >= 1440 ) goEvent = 1; 
			else goEvent = 0; 
		} 
		'''

		if goEvent == 1 and self.inventoryGetItemCount(4031019) < 1 and count >= 0 and preMapNum == "109":
			ret = self.inventoryExchange(0, 4000038, 1)
			if ret != 0:
				if cmap == 60000:
					self.questRecordSet(9000, "maple")
		else:
			self.incIntReg("count", 1)
			self.say("Either the event has not been started, you already have #t4031019#, or have already participated in this event within the last 24 hours. Please try again later!")
