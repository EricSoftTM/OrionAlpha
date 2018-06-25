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

'NPC: Jane'
'Script: Potion NPC after completing her quests'

self.say("#bScript: jane#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

// 10. Jane and the wild boar
let val = self.QuestRecordGetState(2013);
let bIsBeginner = (self.UserGetJob() % 1000) == 0;
let PotionInfo = [
	{
		"Item": 2000002,
		"Price": 310,
		"Recovery": "300 HP"
	},
	{
		"Item": 2022003,
		"Price": 1060,
		"Recovery": "1000 HP"
	},
	{
		"Item": 2022000,
		"Price": 1600,
		"Recovery": "800 MP"
	},
	{
		"Item": 2001000,
		"Price": 3120,
		"Recovery": "1000 HP and MP"
	}
];

if (val == 2) {
	self.sayNext("It's you... thanks to you I was able to get a lot done. Nowadays I've been making a bunch of items. If you need anything let me know.");
	
	let sText = "Which item would you like to buy?#b";
	for (let i = 0; i < PotionInfo.length; sText += "\r\n#L" + i + "##z" + PotionInfo[i]["Item"] + "# (price: " + PotionInfo[i]["Price"] + " mesos) #l", i++);
	
	let nSel = self.askMenu(sText);
	if (nSel >= 0 && nSel < PotionInfo.length) {
		let nRetNum = self.askNumber("You want #b#t" + PotionInfo[nSel]["Item"] + "##k? #t" + PotionInfo[nSel]["Item"] + "# allows you to recover " + PotionInfo[nSel]["Recovery"] + ". How many would you like to buy?", 0, 0, 100);
		let nPrice = PotionInfo[nSel]["Price"] * nRetNum;
		let nRetBuy = self.askYesNo("Will you purchase #r" + nRetNum + "#k #b#t" + PotionInfo[nSel]["Item"] + "#(s)#k? #t" + PotionInfo[nSel]["Item"] + "# costs " + PotionInfo[nSel]["Price"] + " mesos for one, so the total comes out to be #r" + nPrice + "#k mesos.");
		if (nRetBuy == 0)
			self.sayNext("I still have quite a few of the materials you got me before. The items are all there so take your time choosing.");
		else {
			let ret = self.InventoryExchange(-nPrice, PotionInfo[nSel]["Item"], nRetNum);
			if (!ret) self.say("Are you lacking mesos by any chance? Please check and see if you have an empty slot available at your etc. inventory, and if you have at least #r" + nPrice + "#k mesos with you.");
			else self.sayNext("hank you for coming. Stuff here can always be made so if you need something, please come again.");
		}
	}
} else if (self.UserGetLevel() >= 40 && !bIsBeginner) {
	//Voc?s?poder?comprar a po豫o depois de tomar conta dos meus pedidos.
	self.sayNext("You may purchase my potions once you have taken care of my orders.");
} else if (self.UserGetLevel() >= 25 && !bIsBeginner) {
	self.sayNext("You don't seem strong enough to be able to purchase my potion...");
} else {
	self.say("My dream is to travel everywhere, much like you. My father, however, does not allow me to do it, because he thinks it's very dangerous. He may say yes, though, if I show him some sort of a proof that I'm not the weak girl that he thinks I am...");
}
'''
