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
NPC: Chief Stan
Quest: Alex's Request, Talking to Stan
'''

# TODO: Implement Alex's Request quest handling.
prompt = "My Son...! That ungrateful kid... He just didn't listen to me and ran out. I can never ever fogive him...! He can never come to this town again...!"
self.say(prompt)

'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Alex's Request#l#k") # After completing dialogue, even if declining, this becomes 'Talking to Stan'.
if sel == 0:
	ret = self.askYesNo("")
	if ret == False:
		self.say("I guess you don't feel comfortable handling a strange letter like this. If you ever change your mind, however, please talk to me. This letter concerns me more than it should.")
	else:
		# TODO: Implement quest control on backend. => Set quest progress to started.
'''