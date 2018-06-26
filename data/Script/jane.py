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
NPC: Jane
Script: Potion NPC after completing her quests
'''

'10. Jane and the wild boar'
val = "" 'self.questRecordGetState(2013)'
isBeginner = self.userGetJob() == 0
potionInfo = {}

potionInfo[0] = {
	"Item": 2000002,
	"Price": 310,
	"Recovery": "300 HP"
}
potionInfo[1] = {
	"Item": 2022003,
	"Price": 1060,
	"Recovery": "1000 HP"
}
potionInfo[2] = {
	"Item": 2022000,
	"Price": 1600,
	"Recovery": "800 MP"
}

if val == "2":
	self.sayNext("It's you... thanks to you I was able to get a lot done. Nowadays I've been making a bunch of items. If you need anything let me know.")

	text = "Which item would you like to buy?#b"
	for i in range(0, len(potionInfo)):
		text = text + "\r\n#L" + str(i) + "##i" + str(potionInfo[i]["Item"]) + "# (price: " + str(potionInfo[i]["Price"]) + " mesos) #l"

	sel = self.askMenu(text)
	if sel in range(0, len(potionInfo)):
		retNum = self.askNumber("You want #b#i" + str(potionInfo[sel]["Item"]) + "##k? #i" + str(potionInfo[sel]["Item"]) + "# allows you to recover " + str(potionInfo[sel]["Recovery"]) + ". How many would you like to buy?", 0, 0, 100)
		price = potionInfo[sel]["Price"] * retNum
		retBuy = self.askYesNo("Will you purchase #r" + str(retNum) + "#k #b#i" + str(potionInfo[sel]["Item"]) + "#(s)#k? #i" + str(potionInfo[sel]["Item"]) + "# costs " + str(potionInfo[sel]["Price"]) + " mesos for one, so the total comes out to be #r" + str(price) + "#k mesos.")
		if retBuy == 0:
			self.sayNext("I still have quite a few of the materials you got me before. The items are all there so take your time choosing.")
		else:
			ret = self.inventoryExchange(-price, [potionInfo[sel]["Item"], retNum])
			if (ret == False): 
				self.say("Are you lacking mesos by any chance? Please check and see if you have an empty slot available at your etc. inventory, and if you have at least #r" + str(price) + "#k mesos with you.")
			else:
				self.sayNext("Thank you for coming. Stuff here can always be made so if you need something, please come again.")
elif self.userGetLevel() >= 40 and isBeginner == False:
	'Voc?s?poder?comprar a po豫o depois de tomar conta dos meus pedidos.'
	self.sayNext("You may purchase my potions once you have taken care of my orders.")
elif self.userGetLevel() >= 25 and isBeginner == False:
	self.sayNext("You don't seem strong enough to be able to purchase my potion...")
else:
	self.say("My dream is to travel everywhere, much like you. My father, however, does not allow me to do it, because he thinks it's very dangerous. He may say yes, though, if I show him some sort of a proof that I'm not the weak girl that he thinks I am...")

