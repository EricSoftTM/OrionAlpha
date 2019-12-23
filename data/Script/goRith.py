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
NPC: Regular Cab
Script: Transports users between the towns of Victoria Island.
'''

towns = [["Lith Harbor", 104000000, 120], ["Henesys", 100000000, 100], ["Ellinia", 101000000, 100], ["Kerning City", 103000000, 80]]

def goTown(mapName, mapNum, fee):
	ret = self.askYesNo("You don't have anything else to do here, huh? Do you really want to go to #b" + mapName + "#k? It'll cost you #b" + str(fee) + " mesos#k.")
	if ret == 1:
		if (self.userIncMoney(-fee, True)):
			self.registerTransferField(mapNum, "")
		else:
			self.say("I'm afraid you don't have enough money, you'll have to walk.")
	else:
		self.say("There's a lot to see in this town. Come back back when you want to go elsewhere.")

self.sayNext("Hi, i'm the Regular Cab. You came to the right place if you want to go to another town fast and secure. Your satisfaction is guaranteed.")
menu = "Choose your destination, the fare leties from place to place.#b"
for i in range(0, len(towns)):
	menu += "\r\n#L" + str(i) + "#" + towns[i][0] + " (" + str(towns[i][2]) + " mesos)#l"
sel = self.askMenu(menu)
if sel in range(0, len(towns)):
	goTown(towns[sel][0], towns[sel][1], towns[sel][2])
