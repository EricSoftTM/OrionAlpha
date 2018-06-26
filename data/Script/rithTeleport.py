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
NPC: Phil (Lith Harbor)
Script: Town Warper
'''

def goTown(mapNum, fee):
	ret = self.askYesNo("You don't have anything else to do here, right? Would you like to go to #b#m" + str(mapNum) + "##k? It will cost #b" + str(fee) + " mesos#k.")
	if ret == 1:
		if self.userIncMoney(-fee, True) == False:
			self.say("You don't have enough money. Without enough money, you won't be able to ride the taxi.")
		else:
			self.registerTransferField(mapNum, "")
	else:
		self.say("There's a lot to see in this city. Come back and look for us when you need to go somewhere else.")


'1. The NPC at Lith harbor that warps the characters to other towns with a service fee.'
towns = [[102000000, 120], [101000000, 120], [100000000, 80], [103000000, 100], [120000000, 100]]
val = 1
menu = ""

self.sayNext("Do you wanna head over to some other town? With a little money involved, I can make it happen. It's a tad expensive, but I run a special 90% discount for beginners.")
sel = self.askMenu("It's understable that you may be confused about this place if this is your first time around. If you got any questions about this place, fire away. #b\r\n#L0#What kind of towns are here in Victoria Island?#l\r\n#L1#Please take me somewhere else.#l#k")
if sel == 0:
	sel = self.askMenu("There are 6 big towns here in Victoria Island. Which of those do you want to know more of?\r\n#b#L0##m104000000##l\r\n#L1##m102000000##l\r\n#L2##m101000000##l\r\n#L3##m100000000##l\r\n#L4##m103000000##l\r\n#L5##m105040300##l")
	if sel == 0:
		self.sayNext("The town you are at is Lith Harbor! Alright I'll explain to you more about #b#m104000000##k. It's the place you landed on Victoria Island by riding The Victoria. That's #m104000000#. A lot of beginners who just got here from Maple Island start their journey here.")
		self.sayNext("It's a quiet town with the wide body of water on the back of it, thanks to the fact that the harbor is located at the west end of the island. Most of the people here are, or used to be fishermen, so they may look intimidating, but if you strike up a conversation with them, they'll be friendly to you.")
		self.sayNext("Around town lies a beautiful prairie. Most of the monsters there are small and gentle, perfect for beginners. If you haven't chosen your job yet, this is a good place to boost up your level.")
	elif sel == 1:
		self.sayNext("Alright I'll explain to you more about #b#m102000000##k. It's a warrior-town located at the northern-most part of Victoria Island, surrounded by rocky mountains. With an unfriendly atmosphere, only the strong survives there.")
		self.sayNext("Around the highland you'll find a really skinny tree, a wild hog running around the place, and monkeys that live all over the island. There's also a deep valley, and when you go deep into it, you'll find a humongous dragon with the power to match his size. Better go in there very carefully, or don't go at all.")
		self.sayNext("If you want to be a the #bWarrior#k then find #r#p1022000##k, the chief of #m102000000#. If you're level 10 or higher, along with a good STR level, he may make you a warrior afterall. If not, better keep training yourself until you reach that level.")
	elif sel == 2:
		self.sayNext("Alright I'll explain to you more about #b#m101000000##k. It's a magician-town located at the fart east of Victoria Island, and covered in tall, mystic trees. You'll find some fairies there, too; They don't like humans in general so it'll be best for you to be on their good side and stay quiet.")
		self.sayNext("Near the forest you'll find green slimes, walking mushrooms, monkeys and zombie monkeys all residing there. Walk deeper into the forest and you'll find witches with the flying broomstick navigating the skies. A word of warning: unless you are really strong, I recommend you don't go near them.")
		self.sayNext("If you want to be the #bMagician#k, search for #r#p1032001##k, the head wizard of #m101000000#. He may make you a wizard if you're at or above level 8 with a decent amount of INT. If that's not the case, you may have to hunt more and train yourself to get there.")
	elif sel == 3:
		self.sayNext("Alright I'll explain to you more about #b#m100000000##k. It's a bowman-town located at the southernmost part of the island, made on a flatland in the midst of a deep forest and prairies. The weather's just right, and everything is plentiful around that town, perfect for living. Go check it out.")
		self.sayNext("Around the prairie you'll find weak monsters such as snails, mushrooms, and pigs. According to what I hear, though, in the deepest part of the Pig Park, which is connected to the town somewhere, you'll find a humongous, powerful mushroom called Mushmom every now and then.")
		self.sayNext("If you want to be the #bBowman#k, you need to go see #r#p1012100##k at #m100000000#. With a level at or above 10 and a decent amount of DEX, she may make you be one afterall. If not, go train yourself, make yourself stronger, then try again.")
	elif sel == 4:
		self.sayNext("Alright I'll explain to you more about #b#m103000000##k. It's a thief-town located at the northwest part of Victoria Island, and there are buildings up there that have just this strange feeling around them. It's mostly covered in black clouds, but if you can go up to a really high place, you'll be able to see a very beautiful sunset there.")
		self.sayNext("From #m103000000#, you can go into several dungeons. You can go to a swamp where alligators and snakes are abound, or hit the subway full of ghosts and bats. At the deepest part of the underground, you'll find Lace, who is just as big and dangerous as a dragon.")
		self.sayNext("If you want to be the #bThief#k, seek #r#p1052001##k, the heart of darkness of #m103000000#. He may well make you the thief if you're at or above level 10 with a good amount of DEX. If not, go hunt and train yourself to reach there.")
	elif sel == 5:
		self.sayNext("Alright I'll explain to you more about #b#m105040300##k. It's a forest town located at the southeast side of Victoria Island. It's pretty much in between #m100000000# and the ant-tunnel dungeon. There's a hotel there, so you can rest up after a long day at the dungeon ... it's a quiet town in general.")
		self.sayNext("In front of the hotel there's an old buddhist monk by the name of #r#p1061000##k. Nobody knows a thing about that monk. Apparently he collects materials from the travellers and create something, but I am not too sure about the details. If you have any business going around that area, please check that out for me.")
		self.sayNext("From #m105040300#, head east and you'll find the ant tunnel connected to the deepest part of the Victoria Island. Lots of nasty, powerful monsters abound so if you walk in thinking it's a walk in the park, you'll be coming out as a corpse. You need to fully prepare yourself for a rough ride before going in.")
		self.sayNext("And this is what I hear ... apparently, at #m105040300# there's a secret entrance leading you to an unknown place. Apparently, once you move in deep, you'll find a stack of black rocks that actually move around. I want to see that for myself in the near future ...")
elif sel == 1:
	menu = "There's a special 90% discount for all beginners. Alright, where would you want to go?#b"
	if self.userIsBeginner() == False:
		val = 10
		menu = "Oh you aren't a beginner, huh? Then I'm afraid I may have to charge you full price. Where would you like to go?#b"
	for i in range(0, len(towns)):
		menu = menu + "\r\n#L" + str(i) + "##m" + str(towns[i][0]) + "# (" + str(towns[i][1] * val) + " mesos)#l"
	sel = self.askMenu(menu)
	if sel in range(0, len(towns)):
		goTown(towns[sel][0], towns[sel][1] * val)

