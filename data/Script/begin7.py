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

'NPC: Shanks'
'Script: Sending the character to Victoria Island'

self.say("#bScript: begin7#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

let nRet = self.askYesNo("Take this ship and you'll head off to a bigger continent.#e For 150 mesos#n, I'll take you to #bVictoria Island#k. The thing is, once you leave this place, you can't ever come back. What do you think? Do you want to go to Victoria Island?");
if (nRet == 0) {
	self.say("Hmm... I guess you still have things to do here?");
} else {
	if (self.UserGetLevel() < 7) {
		self.say("Let's see... I don't think you are strong enough. You'll have to be at least #bLevel 7#k to go to Victoria Island.");
	} else {
		if (self.InventoryGetItemCount(4031801) > 0) {
			self.sayNext("Okay, now give me 150 mesos... Hey, what's that? Is that the recommendation letter from Lucas, the chief of Amherst? Hey, you should have told me you had this. I, Shanks, recognize greatness when I see one, and since you have been recommended by Lucas, I see that you have a great, great potential as an adventurer. No way would I charge you for this trip!");
			self.sayNext("Since you have the recommendation letter, I won't charge you for this. Alright, buckle up, because we're going to head to Victoria Island right now, and it might get a bit turbulent!!");
			if (self.UserGetJob() == 1000) {
				self.InventoryExchange(0, 4031801, -1);
				self.QuestRecordSetState(20100, 1);
				self.RegisterTransferField(130000000);
			} else {
				self.InventoryExchange(0, 4031801, -1);
				self.RegisterTransferField(104000000, "maple00");
			}
		} else {
			self.say("Bored of this place? Here... Give me 150 mesos first...");
			if (!self.UserIncMoney(-150, true)) {
				self.say("What? You're telling me you wanted to go without any money? You're one weirdo...");
			} else {
				self.say("Awesome! #e150 mesos#n accepted! Alright, off to Victoria Island!");
				self.RegisterTransferField(104000000, "maple00");
			}
		}
	}
}
'''
