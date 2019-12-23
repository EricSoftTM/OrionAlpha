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
NPC: Mr. Smith
Script: Perion Ore Refiner
'''

self.say("#bScript: refine_perion2#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

  v1 = self.askMenu("Um... Hi, I'm Mr. Thunder's apprentice. He's getting up there in age, so he handles most of the heavy-duty work while I handle some of the lighter jobs. What can I do for you?\r\n#b#L0#Make a Glove#l\r\n#L1#Upgrade a Glove#l\r\n#L2#Create Materials#l");
  if ( v1 == 0 ) {
    v2 = self.askMenu("I'm the best glove creator in town! Now...what kind of glove do you want me to do?\r\n#L0##b #t1082003##k (Warrior Lv. 10)#l\r\n#L1##b #t1082000##k(Warrior Lv. 15)#l\r\n#L2##b #t1082004##k(Warrior Lv. 20)#l\r\n#L3##b #t1082001##k(Warrior Lv. 25)#l\r\n#L4##b #t1082007##k(Warrior Lv. 30)#l\r\n#L5##b #t1082008##k(Warrior Lv. 35)#l\r\n#L6##b #t1082023##k(Warrior Lv. 40)#l\r\n#L7##b #t1082009##k(Warrior Lv. 50)#l\r\n#L8##b #t1082059##k(Warrior Lv. 60)#l");
    if ( v2 == 0 ) chat_message3( 1, "#t1082003#", "#v4000021# 15 #t4000021#s\r\n#v4011001# #t4011001# \r\n#v4031138# 1.000 mesos", 10 );
    else if ( v2 == 1 ) chat_message3( 2, "#t1082000#", "#v4011001# 2 #t4011001#s \r\n#v4031138# 2.000 mesos", 15 );
    else if ( v2 == 2 ) chat_message3( 3, "#t1082004#", "#v4000021# 40 #t4000021#s \r\n#v4011000# 2 #t4011000#s \r\n#v4031138# 5.000 mesos", 20 );
    else if ( v2 == 3 ) chat_message3( 4, "#t1082001#", "#v4011001# 2 #t4011001#s \r\n#v4031138# 10.000 mesos", 25 );
    else if ( v2 == 4 ) chat_message3( 5, "#t1082007#", "#v4011000# 3 #t4011000#s \r\n#v4011001# 2 #t4011001#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 20.000 mesos", 30 );
    else if ( v2 == 5 ) chat_message3( 6, "#t1082008#", "#v4000021# 30 #t4000021#s \r\n#v4011001#  4 #t4011001#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 30.000 mesos", 35 );
    else if ( v2 == 6 ) chat_message3( 7, "#t1082023#", "#v4000021# 50 #t4000021#s \r\n#v4011001# 5 #t4011001#s \r\n#v4003000# 40 #t4003000#s \r\n#v4031138# 40.000 mesos", 40 );
    else if ( v2 == 7 ) chat_message3( 8, "#t1082009#", "#v4011001# 3 #t4011001#s \r\n#v4021007# 2 #t4021007#s \r\n#v4000030# 30 #t4000030#s \r\n#v4003000# 45 #t4003000#s \r\n#v4031138# 50.000 mesos", 50 );
    else if ( v2 == 8 ) chat_message3( 9, "#t1082059#", "#v4011007# #t4011007# \r\n#v4011000# 8 #t4011000#s \r\n#v4011006# 2 #t4011006#s \r\n#v4000030# 50 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 70.000 mesos", 60 );
  }
  else if ( v1 == 1 ) {
    self.sayNext("So you want to improve the glove? Okay then, but I'll give you some advice. All items that will be used for perfection will #bdisappear#k. Take this into consideration when you make the decision, okay?");
    v2 = self.askMenu("So what kind of glove do you want to perfect and create?\r\n#L0##b #t1082005##k(Warrior Lv. 30)#l\r\n#L1##b #t1082006##k(Warrior Lv. 30)#l\r\n#L2##b #t1082035##k(Warrior Lv. 35)#l\r\n#L3##b#t1082036##k(Warrior Lv. 35)#l\r\n#L4##b #t1082024##k(Warrior Lv. 40)#l\r\n#L5##b#t1082025##k(Warrior Lv. 40)#l\r\n#L6##b#t1082010##k(Warrior Lv. 50)#l\r\n#L7##b#t1082011##k(Warrior Lv. 50)#l\r\n#L8##b#t1082060##k(Warrior Lv. 60)#l\r\n#L9##b#t1082061##k(Warrior Lv. 60)#l");
    if ( v2 == 0 ) chat_message4( 1, "#t1082005#", "#v1082007# #t1082007# \r\n#v4011001# #t4011001# \r\n#v4031138# 20.000 mesos", 30 );
    else if ( v2 == 1 ) chat_message4( 2, "#t1082006#", "#v1082007# #t1082007# \r\n#v4011005# 2 #t4011005#s \r\n#v4031138# 25.000 mesos", 30 );
    else if ( v2 == 2 ) chat_message4( 3, "#t1082035#", "#v1082008# #t1082008# \r\n#v4021006# 3 #t4021006#s \r\n#v4031138# 30.000 mesos", 35 );
    else if ( v2 == 3 ) chat_message4( 4, "#t1082036#", "#v1082008# #t1082008# \r\n#v4021008# #t4021008# \r\n#v4031138# 40.000 mesos", 35 );
    else if ( v2 == 4 ) chat_message4( 5, "#t1082024#", "#v1082023# #t1082023# \r\n#v4011003# 4 #t4011003#s \r\n#v4031138# 45.000 mesos", 40 );
    else if ( v2 == 5 ) chat_message4( 6, "#t1082025#", "#v1082023# #t1082023# \r\n#v4021008# 2 #t4021008#s \r\n#v4031138# 50.000 mesos", 40 );
    else if ( v2 == 6 ) chat_message4( 7, "#t1082010#", "#v1082009# #t1082009# \r\n#v4011002# 5 #t4011002#s \r\n#v4031138# 55.000 mesos", 50 );
    else if ( v2 == 7 ) chat_message4( 8, "#t1082011#", "#v1082009# #t1082009# \r\n#v4011006# 4 #t4011006#s \r\n#v4031138# 60.000 mesos", 50 );
    else if ( v2 == 8 ) chat_message4( 9, "#t1082060#", "#v1082059# #t1082059# \r\n#v4011002# 3 #t4011002#s \r\n#v4021005# 5 #t4021005#s \r\n#v4031138# 70.000 mesos", 60 );
    else if ( v2 == 9 ) chat_message4( 10, "#t1082061#", "#v1082059# #t1082059# \r\n#v4021007# 2 #t4021007#s \r\n#v4021008# 2 #t4021008#s \r\n#v4031138# 80.000 mesos", 60 );
  }
  else if ( v1 == 2 ) {
    v2 = self.askMenu( "So, you want to create some materials, of course? Okay... what type of materials do you want to create?\r\n#L0##bCreate #t4003001# with #t4000003##k#l\r\n#L1##bCreate #t4003001# with #t4000018##k#l\r\n#L2##bCreate #t4003000#s#k#l" );
    if ( v2 == 0 ) chat_message5( 1, "#t4003001#(s)", "#t4000003#es", 10, 1 );
    else if ( v2 == 1 ) chat_message5( 2, "#t4003001#(s)", "#t4000018#s", 5, 1 );
    else if ( v2 == 2 ) chat_message5( 3, "#t4003000#s", "#t4011001#(s) and #t4011000#(s)", 1, 15 );
  }

function chat_message3(index, makeItem, needItem, reqLevel) {
	inventory = target.inventory;


	nRet = self.askYesNo( "To make a #b" + makeItem + "#k, I'm going to need the following materials. The Equipment Level Requirement: #d" + reqLevel + "#k and please make sure that you will not use an item that is being upgraded as material. What do you think? Do you want a #r" + makeItem + "#k?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say("You do no have the materials? All right... go look for all of them and then come and talk to me, okay?");
	else {
		if ( index == 1 ) ret = self.InventoryExchange( -1000, 4000021, -15, 4011001, -1, 1082003, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -2000, 4011001, -2, 1082000, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -5000, 4000021, -40, 4011000, -2, 1082004, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -10000, 4011001, -2, 1082001, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -20000, 4011000, -3, 4011001, -2, 4003000, -15, 1082007, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -30000, 4011001, -4, 4000021, -30, 4003000, -30, 1082008, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -40000, 4011001, -5, 4000021, -50, 4003000, -40, 1082023, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -50000, 4011001, -3, 4021007, -2, 4000030, -30, 4003000, -45, 1082009, 1 );
		else if ( index == 9 ) ret = self.InventoryExchange( -70000, 4011007, -1, 4011000, -8, 4011006, -2, 4000030, -50, 4003000, -50, 1082059, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your inventory is full or not." );
		else self.say( "Here! Take the #r" + makeItem + "#k you asked for. Do you not think I'm as good as Mr. Thunder? You'll be more than happy with what I did here." );
	}
}

function chat_message4(index, makeItem, needItem, reqLevel) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To make a #b" + makeItem + "#k, I'm going to need the following materials. The Equipment Level Requirement: #d" + reqLevel + "#k and please make sure that you will not use an item that is being upgraded as material. What do you think? Do you want a #r" + makeItem + "#k?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "Do you not have the materials? All right... go find all of them and then come and talk to me, okay? I'll wait..." );
	else {
		if ( index == 1 ) ret = self.InventoryExchange( -20000, 1082007, -1, 4011001, -1, 1082005, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -25000, 1082007, -1, 4011005, -2, 1082006, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -30000, 1082008, -1, 4021006, -3, 1082035, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -40000, 1082008, -1, 4021008, -1, 1082036, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -45000, 1082023, -1, 4011003, -4, 1082024, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -50000, 1082023, -1, 4021008, -2, 1082025, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -55000, 1082009, -1, 4011002, -5, 1082010, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -60000, 1082009, -1, 4011006, -4, 1082011, 1 );
		else if ( index == 9 ) ret = self.InventoryExchange( -70000, 1082059, -1, 4011002, -3, 4021005, -5, 1082060, 1 );
		else if ( index == 10 ) ret = self.InventoryExchange( -80000, 1082059, -1, 4021007, -2, 4021008, -2, 1082061, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your inventory is full or not." );
		else self.say( "Did that come out right? Come by me again if you have anything for me to practice on." );
	}
}

function chat_message5(index, makeItem, needItem, needNumber, itemNumber ) {
	inventory = target.inventory;

	nRetNum = self.askNumber( "With #b" + needNumber + " " + needItem + "#k, I can create #r" + itemNumber + " " + makeItem + "#k. Be happy because this is on my account. What do you think? How many do you want?", 0, 0, 100 );
	nNeedNum = nRetNum * needNumber;
	nAllNum = nRetNum * itemNumber;
	nRetBuy = self.askYesNo( "Do you want to make #b" + makeItem + "#k " + nRetNum + " times? I'll need  #r" + nNeedNum + " " + needItem + "#k." );
	if ( nRetBuy == 0 ) self.say( "You do not have the materials? No problem... go find them all and then come and talk to me, okay? I'll wait..." );
	else {
		if ( index == 1 ) ret = self.InventoryExchange( 0, 4000003, -nNeedNum, 4003001, nAllNum );
		else if ( index == 2 ) ret = self.InventoryExchange( 0, 4000018, -nNeedNum, 4003001, nAllNum );
		else if ( index == 3 ) ret = self.InventoryExchange( 0, 4011001, -nNeedNum, 4011000, -nNeedNum, 4003000, nAllNum );
		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your inventory is full or not." );
		else self.say( "Here! Take it! " + nAllNum + " " + makeItem + ". Do you not think I'm as good as Mr. Thunder? You'll be more than happy with what I did here." );
	}
}
'''
