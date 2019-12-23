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
NPC: Peter
Script: Character control
'''

if self.userGetJob() != 0:
    self.say("This is the mission area for apprentices. You're not an apprentice, are you?")
    self.registerTransferField(104000000, "")
else:
    self.sayNext("You have come so far... incredible! You can start traveling around right now! Ok, I'll take you to the next stop.")
    self.sayNext("But I'll give you some advice: Once you get out of here, you'll be left in places with lots of monsters and no means to return. Well then, see you later!")
    self.userIncEXP(3, False)
    self.registerTransferField(40000, "")