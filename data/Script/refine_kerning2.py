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

'NPC: Chris'
'Script: Kerning\'s Ore Refiner'

self.say("#bScript: refine_kerning2#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

nRet1 = self.askYesNo( "What's up? All right? You want a rare mineral or a jewelry? With a small service charge, I can turn them into a handy material for a weapon or armor. I learned how to refine while working on repairing items... what do you think? You want to let me do it?" );
	if ( nRet1 == 0 ) self.say( "I understand ... but I'm sure someday you'll need my help ... and when that day comes you can come back and talk to me, right?" );
	else {
		v1 = self.askMenu( "OK! With the minirous and a small service fee, I can refine it so that you can use it. Before that, Make sure you have a slot available in your inventory... now... what do you want me to do?\r\n#b#L0#Refine a Mineral#l\r\n#L1#Refine a Jewel\r\n#L2#I have #t4000039#...#l\r\n#L3#Upgrade a Claw#l" );
		if ( v1 == 0 ) {
			v2 = self.askMenu( "What kind of mineral do you want to refine?\r\n#b#L0##t4011000##l\r\n#L1##t4011001##l\r\n#L2##t4011002##l\r\n#L3##t4011003##l\r\n#L4##t4011004##l\r\n#L5##t4011005##l\r\n#L6##t4011006##l" );
			if ( v2 == 0 ) chat_message5( 1, "#t4011000#", "#v4010000#", "#t4010000#s", 250 );
			else if ( v2 == 1 ) chat_message5( 2, "#t4011001#", "#v4010001#", "#t4010001#s", 250 );
			else if ( v2 == 2 ) chat_message5( 3, "#t4011002#", "#v4010002#", "#t4010002#s", 250 );
			else if ( v2 == 3 ) chat_message5( 4, "#t4011003#", "#v4010003#", "#t4010003#s", 450 );
			else if ( v2 == 4 ) chat_message5( 5, "#t4011004#", "#v4010004#", "#t4010004#s", 450 );
			else if ( v2 == 5 ) chat_message5( 6, "#t4011005#", "#v4010005#", "#t4010005#s", 450 );
			else if ( v2 == 6 ) chat_message5( 7, "#t4011006#", "#v4010006#", "#t4010006#s", 750 );
		}
		else if ( v1 == 1 ) {
			v2 = self.askMenu( "What kind of jewelry do you want to refine?\r\n#b#L0##t4021000##l\r\n#L1##t4021001##l\r\n#L2##t4021002##l\r\n#L3##t4021003##l\r\n#L4##t4021004##l\r\n#L5##t4021005##l\r\n#L6##t4021006##l\r\n#L7##t4021007##l\r\n#L8##t4021008##l" );
			if ( v2 == 0 ) chat_message5( 100, "#t4021000#", "#v4020000#", "#t4020000#s", 450 );
			else if ( v2 == 1 ) chat_message5( 101, "#t4021001#", "#v4020001#", "#t4020001#s", 450 );
			else if ( v2 == 2 ) chat_message5( 102, "#t4021002#", "#v4020002#", "#t4020002#s", 450 );
			else if ( v2 == 3 ) chat_message5( 103, "#t4021003#", "#v4020003#", "#t4020003#s", 450 );
			else if ( v2 == 4 ) chat_message5( 104, "#t4021004#", "#v4020004#", "#t4020004#s", 450 );
			else if ( v2 == 5 ) chat_message5( 105, "#t4021005#", "#v4020005#", "#t4020005#s", 450 );
			else if ( v2 == 6 ) chat_message5( 106, "#t4021006#", "#v4020006#", "#t4020006#s", 450 );
			else if ( v2 == 7 ) chat_message5( 107, "#t4021007#", "#v4020007#", "#t4020007#s", 950 );
			else if ( v2 == 8 ) chat_message5( 108, "#t4021008#", "#v4020008#", "#t4020008#s", 2900 );
		}
		else if ( v1 == 2 ) {
			nRet2 = self.askYesNo( "Do you have #b#t4000039##k? Hmm... with this I can try to do #b#t4011001##k. If you give me #b100 #b#t4000039#s#k and #b1.000 mesos#k, I can make #r#t4011001##k. What do you think? Do you want to try?" );
			if ( nRet2 == 0 ) self.say( "I can refine other minerals and jewels beyond these, so you can think about it..." );
			else {
				ret = self.InventoryExchange( -1000, 4000039, -100, 4011001, 1 );
				if ( ret == 0 ) self.say( "Maybe you're running short of money... make sure you have 100 #b#t4000039#s#k and #b1,000 mesos#k, and a free slot in your inventory..." );
				else self.say( "Well... here's #b#t4011001##k. What do you think? Very well done, eh? Hahaha... I'm glad I studied my refining skills a lot... come back whenever you want!" );
			}
		}
		else if ( v1 == 3 ) {
			self.sayNext( "So, you want to perfect the glove? Be careful! All items used for upgrading will #bdisappear#k, so you better think well before you make your decision..." );
			v2 = self.askMenu( "Now... what kind of claw do you want to upgrade?\r\n#L0##b#t1472023##k(Thief Lv. 60)#l\r\n#L1##b#t1472024##k(Thief Lv. 60)#l\r\n#L2##b#t1472025##k(Thief Lv. 60)#k#l" );
			if ( v2 == 0 ) chat_message4( 1, "#t1472023#", "#v1472022# #t1472022#\r\n#v4011007# #t4011007#\r\n#v4021000# 8 #t4021000#s\r\n#v2012000# 10 #t2012000#s\r\n#v4031138# 80.000 mesos", 60, "DEX + 4, Avoidability + 3", 30 );
			else if ( v2 == 1 ) chat_message4( 2, "#t1472024#", "#v1472022# #t1472022#\r\n#v4011007# #t4011007#\r\n#v4021005# 8 #t4021005#s\r\n#v2012002# 10 #t2012002#s\r\n#v4031138# 80.000 mesos", 60, "STR + 4, Avoidability + 3", 30 );
			else if ( v2 == 2 ) chat_message4( 3, "#t1472025#", "#v1472022# #t1472022#\r\n#v4011007# #t4011007#\r\n#v4021008# 3 #t4021008#s\r\n#v4000046# 5 #t4000046#s\r\n#v4031138# 100.000 mesos", 60, "STR + 5, Avoidability + 4", 30 );
		}
	}

  function chat_message3(index, makeItem, needItem, unitPrice ) {
  	inventory = target.inventory;

  	nRetNum = self.askNumber( "Do you want to make a #b" + makeItem + "#k? To do so, you will need the materials listed below. How many would you like to do?\r\n\r\n#b" + needItem + "\r\n#v4031138#" + unitPrice + " mesos#k", 0, 0, 100 );
  		nPrice = unitPrice * nRetNum;
  		nAllNum = nRetNum * 10;
  		nRetBuy = self.askYesNo( "To do #b" + nRetNum + " " + makeItem + "#k, You will need the items listen below. Are you sure you want to do it?\r\n\r\n#b" + nAllNum + " " + needItem + "\r\n#v4031138#" + nPrice + " mesos#k" );
  		if ( nRetBuy == 0 ) self.say( "I can refine other minerals and jewelry, so you can think about it, okay?" );
  		else {
  			if ( index == 1 ) ret = self.InventoryExchange( -nPrice, 4010000, -nAllNum, 4011000, nRetNum );
  			else if (	 index == 2 ) ret = self.InventoryExchange( -nPrice, 4010001, -nAllNum, 4011001, nRetNum );
  			else if (	 index == 3 ) ret = self.InventoryExchange( -nPrice, 4010002, -nAllNum, 4011002, nRetNum );
  			else if (	 index == 4 ) ret = self.InventoryExchange( -nPrice, 4010003, -nAllNum, 4011003, nRetNum );
  			else if (	 index == 5 ) ret = self.InventoryExchange( -nPrice, 4010004, -nAllNum, 4011004, nRetNum );
  			else if (	 index == 6 ) ret = self.InventoryExchange( -nPrice, 4010005, -nAllNum, 4011005, nRetNum );
  			else if (	 index == 7 ) ret = self.InventoryExchange( -nPrice, 4010006, -nAllNum, 4011006, nRetNum );
  			// jewel
  			else if (	 index == 100 ) ret = self.InventoryExchange( -nPrice, 4020000, -nAllNum, 4021000, nRetNum );
  			else if (	 index == 101 ) ret = self.InventoryExchange( -nPrice, 4020001, -nAllNum, 4021001, nRetNum );
  			else if (	 index == 102 ) ret = self.InventoryExchange( -nPrice, 4020002, -nAllNum, 4021002, nRetNum );
  			else if (	 index == 103 ) ret = self.InventoryExchange( -nPrice, 4020003, -nAllNum, 4021003, nRetNum );
  			else if (	 index == 104 ) ret = self.InventoryExchange( -nPrice, 4020004, -nAllNum, 4021004, nRetNum );
  			else if (	 index == 105 ) ret = self.InventoryExchange( -nPrice, 4020005, -nAllNum, 4021005, nRetNum );
  			else if (	 index == 106 ) ret = self.InventoryExchange( -nPrice, 4020006, -nAllNum, 4021006, nRetNum );
  			else if (	 index == 107 ) ret = self.InventoryExchange( -nPrice, 4020007, -nAllNum, 4021007, nRetNum );
  			else if (	 index == 108 ) ret = self.InventoryExchange( -nPrice, 4020008, -nAllNum, 4021008, nRetNum );

  			if ( ret == 0 ) self.say( "Please see if you have #b" + needItem + "#k, or if your inventory is full or not." );
  			else self.say( "Here, take the #r"  + nRetNum + " " + makeItem + "(s)#k you asked for. What do you think? Pretty refined, huh? Hahaha... finally, all those days spent studying refining days were worth it. Please come back another time!" );
  		}
  	}

  function chat_message5(index, makeItem, needItemIcon, needItemString, unitPrice ) {
  	inventory = target.inventory;

  	nRetNum = self.askNumber( "Do you want to make a #b" + makeItem + "#k? To do so, you will need the materials listed below. How many would you like to make?\r\n\r\n#b" + needItemIcon + " 10 " + needItemString + "\r\n#v4031138#" + unitPrice + " mesos#k", 0, 0, 100 );
  		nPrice = unitPrice * nRetNum;
  		nAllNum = nRetNum * 10;
  		nRetBuy = self.askYesNo( "To make #b" + nRetNum + " " + makeItem + "#k, You will need the items listed below. Do you really want to do it?\r\n\r\n#b" + needItemIcon + " " + nAllNum + " " + needItemString + "\r\n#v4031138#" + nPrice + " mesos#k" );
  		if ( nRetBuy == 0 ) self.say( "I can refine other minerals and jewelry, so you can think about it, okay?" );
  		else {
  			if ( index == 1 ) ret = self.InventoryExchange( -nPrice, 4010000, -nAllNum, 4011000, nRetNum );
  			else if (	 index == 2 ) ret = self.InventoryExchange( -nPrice, 4010001, -nAllNum, 4011001, nRetNum );
  			else if (	 index == 3 ) ret = self.InventoryExchange( -nPrice, 4010002, -nAllNum, 4011002, nRetNum );
  			else if (	 index == 4 ) ret = self.InventoryExchange( -nPrice, 4010003, -nAllNum, 4011003, nRetNum );
  			else if (	 index == 5 ) ret = self.InventoryExchange( -nPrice, 4010004, -nAllNum, 4011004, nRetNum );
  			else if (	 index == 6 ) ret = self.InventoryExchange( -nPrice, 4010005, -nAllNum, 4011005, nRetNum );
  			else if (	 index == 7 ) ret = self.InventoryExchange( -nPrice, 4010006, -nAllNum, 4011006, nRetNum );
  			// jewel
  			else if (	 index == 100 ) ret = self.InventoryExchange( -nPrice, 4020000, -nAllNum, 4021000, nRetNum );
  			else if (	 index == 101 ) ret = self.InventoryExchange( -nPrice, 4020001, -nAllNum, 4021001, nRetNum );
  			else if (	 index == 102 ) ret = self.InventoryExchange( -nPrice, 4020002, -nAllNum, 4021002, nRetNum );
  			else if (	 index == 103 ) ret = self.InventoryExchange( -nPrice, 4020003, -nAllNum, 4021003, nRetNum );
  			else if (	 index == 104 ) ret = self.InventoryExchange( -nPrice, 4020004, -nAllNum, 4021004, nRetNum );
  			else if (	 index == 105 ) ret = self.InventoryExchange( -nPrice, 4020005, -nAllNum, 4021005, nRetNum );
  			else if (	 index == 106 ) ret = self.InventoryExchange( -nPrice, 4020006, -nAllNum, 4021006, nRetNum );
  			else if (	 index == 107 ) ret = self.InventoryExchange( -nPrice, 4020007, -nAllNum, 4021007, nRetNum );
  			else if (	 index == 108 ) ret = self.InventoryExchange( -nPrice, 4020008, -nAllNum, 4021008, nRetNum );

  			if ( ret == 0 ) self.say( "Please see if you have #b" + needItemIcon + needItemString + "#k, or if your inventory is full or not." );
  			else self.say( "Here, take the #r"  + nRetNum + " " + makeItem + "(s)#k. What do you think? Pretty refined, huh? Hahaha ... Finally, all those days spent studying refining days were worth it. Please come back another time!" );
  		}
  	}

  function chat_message4(index, makeItem, needItem, reqLevel, itemOption, pad ) {
  	inventory = target.inventory;

  	nRet = self.askYesNo( "You want to upgrade " + makeItem + "? To do so, you will need the materials listed below. The item will have #r" + itemOption + "#k Embedded in it, with its Equipment Level Requirement being #d" + reqLevel + "#k And the attack power being #r" + pad + "#k. Be sure not to use an item that has been upgraded as a material for refinement. Do you want to do it?\r\n\r\n#b" + needItem );
  	if ( nRet == 0 ) self.say( "I can refine other minerals and jewelry, so you can think about it, okay?" );
  	else {
  		// Claws
  		if ( index == 1 ) ret = self.InventoryExchange( -80000, 1472022, -1, 4011007, -1, 4021000, -8, 2012000, -10, 1472023, 1 );
  		else if ( index == 2 ) ret = self.InventoryExchange( -80000, 1472022, -1, 4011007, -1, 4021005, -8, 2012002, -10, 1472024, 1 );
  		else if ( index == 3 ) ret = self.InventoryExchange( -100000, 1472022, -1, 4011007, -1, 4021008, -3, 4000046, -5, 1472025, 1 );

  		if ( ret == 0 ) self.say( "Please make sure you have all the items you need or if your inventory of etc is full or not." );
  		else self.say( "Here, take the " + makeItem + ". What do you think? Well done, huh? Hahaha ... Finally, all those days spent studying the details of the art of perfection were worth it. Please come back another time!" );
  	}
  }
'''
