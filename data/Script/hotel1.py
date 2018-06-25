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

'NPC: Hotel Receptionist'
'Script: Enters users into the Sleepywood sauna'

self.say("#bScript: hotel1#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

// A receptionist from sleepywood hotel
let aaHotel = [[105040401, 499], [105040402, 999]];

self.sayNext("Welcome. We're the Sleepywood Hotel. Our hotel works hard to serve you the best at all times. If you are tired and worn out from hunting, how about a relaxing stay at our hotel?");
let nSel = self.askMenu("We offer two kinds of rooms for service. Please choose the one of your liking.\r\n#b#L0#Regular sauna(" + aaHotel[0][1] + " mesos per use)#l\r\n#L1#VIP sauna(" + aaHotel[1][1] + " mesos per use)#l");
if (nSel == 0) {
	nRet = self.askYesNo("You've chosen the regular sauna. Your HP and MP will recover fast and you can even purchase some items there. Are you sure you want to go in?");
} else if (nSel == 1) {
	nRet = self.askYesNo("You've chosen the VIP sauna. Your HP and MP will recover even faster than that of the regular sauna and you can even find a special item in there. Are you sure you want to go in?");
}

if (nSel >= 0 && nSel < aaHotel.length) {
	if (nRet == 1) {
		let mon = self.UserIncMoney(-aaHotel[nSel][1], true);
		if (!mon) self.say("I'm sorry. It looks like you don't have enough mesos. It will cost you " + aaHotel[nSel][1] + " mesos to stay at our hotel.");
		else self.RegisterTransferField(aaHotel[nSel][0], "");
	} else {
		self.say("We offer other kinds of services, too, so please think carefully and then make your decision.");
	}
}
'''
