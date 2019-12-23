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
NPC: Teo
Quest: Maya of Henesys (Maya and the Weird Medicine), Finding Sophia
'''

# TODO: Implement Maya of Henesys quest handling.
# TODO: Implement Finding Sophia quest handling.
prompt = "I heard that Maya is sick again. Tragic..."
self.say(prompt)

'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Maya of Henesys#l#k") # After completing the dialogue, even if declining, this changes to 'Finding Sophia'.
if sel == 0: # Initiate Finding Sophia Quest
	self.sayNext("")
	self.sayNext("")
	ret = self.askYesNo("")
	if ret == True:
		# TODO: Implement quest control on backend. => Set quest progress to started.
'''
