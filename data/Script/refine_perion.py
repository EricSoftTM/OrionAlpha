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

'NPC: Mr. Thunder'
'Script: Perion\'s Item Creator'

self.say("#bScript: refine_perion#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

  v1 = self.askMenu("Hm? Who might you be? Oh, you've heard about my forging skills? In that case, I'd be glad to process some of your ores... for a fee.\r\n#b#L0#Refine a Mineral#l\r\n#L1#Refine a Jewel#l\r\n#L2#Upgrade a Helmet#l\r\n#L3#Upgrade a Shield#l");
  if ( v1 == 0 ) {
    v2 = self.askMenu( "What kind of mineral do you want to create?\r\n#b#L0# #t4011000##l\r\n#L1# #t4011001##l\r\n#L2# #t4011002##l\r\n#L3# #t4011003##l\r\n#L4# #t4011004##l\r\n#L5# #t4011005##l\r\n#L6# #t4011006##l" );
    if ( v2 == 0 ) chat_message11( 1, "#t4011000#", "#v4010000#", "#t4010000#", 300 );
    else if ( v2 == 1 ) chat_message11( 2, "#t4011001#", "#v4010001#", "#t4010001#", 300 );
    else if ( v2 == 2 ) chat_message11( 3, "#t4011002#", "#v4010002#", "#t4010002#", 300 );
    else if ( v2 == 3 ) chat_message11( 4, "#t4011003#", "#v4010003#", "#t4010003#", 500 );
    else if ( v2 == 4 ) chat_message11( 5, "#t4011004#", "#v4010004#", "#t4010004#", 500 );
    else if ( v2 == 5 ) chat_message11( 6, "#t4011005#", "#v4010005#", "#t4010005#", 500 );
    else if ( v2 == 6 ) chat_message11( 7, "#t4011006#", "#v4010006#", "#t4010006#", 800 );
  }
  else if ( v1 == 1 ) {
    v2 = self.askMenu( "What kind of jewel do you want to create?\r\n#b#L0# #t4021000##l\r\n#L1# #t4021001##l\r\n#L2# #t4021002##l\r\n#L3# #t4021003##l\r\n#L4# #t4021004##l\r\n#L5# #t4021005##l\r\n#L6# #t4021006##l\r\n#L7# #t4021007##l\r\n#L8# #t4021008##l" );
    if ( v2 == 0 ) chat_message11( 100, "#t4021000#", "#v4020000#", "#t4020000#", 500 );
    else if ( v2 == 1 ) chat_message11( 101, "#t4021001#", "#v4020001#", "#t4020001#", 500 );
    else if ( v2 == 2 ) chat_message11( 102, "#t4021002#", "#v4020002#", "#t4020002#", 500 );
    else if ( v2 == 3 ) chat_message11( 103, "#t4021003#", "#v4020003#", "#t4020003#", 500 );
    else if ( v2 == 4 ) chat_message11( 104, "#t4021004#", "#v4020004#", "#t4020004#", 500 );
    else if ( v2 == 5 ) chat_message11( 105, "#t4021005#", "#v4020005#", "#t4020005#", 500 );
    else if ( v2 == 6 ) chat_message11( 106, "#t4021006#", "#v4020006#", "#t4020006#", 500 );
    else if ( v2 == 7 ) chat_message11( 107, "#t4021007#", "#v4020007#", "#t4020007#", 1000 );
    else if ( v2 == 8 ) chat_message11( 108, "#t4021008#", "#v4020008#", "#t4020008#", 3000 );
  }
  else if ( v1 == 2 ) {
    self.sayNext( "So, you want to upgrade your helmet? Okay then, but I will give you some advice. All of the items that will be used for the refinement will #bdisappear#k. Take this into consideration when making the decision, of course." );
    v2 = self.askMenu( "Now... what kind of helmet do you want to perfect or create?\r\n#L0##b #t1002041##k(Warrior Lv. 15)#l\r\n#L1##b #t1002042##k(Warrior Lv. 15)#l\r\n#L2##b #t1002002##k(Warrior Lv. 10)#l\r\n#L3##b #t1002044##k(Warrior Lv. 10)#l\r\n#L4##b #t1002003##k(Warrior Lv. 12)#l\r\n#L5##b #t1002040##k(Warrior Lv. 12)#l\r\n#L6##b #t1002007##k(Warrior Lv. 15)#l\r\n#L7##b #t1002052##k(Warrior Lv. 15)#l\r\n#L8##b #t1002011##k(Warrior Lv. 20)#l\r\n#L9##b #t1002058##k(Warrior Lv. 20 )#l\r\n#L10##b #t1002009##k(Warrior Lv. 20)#l\r\n#L11##b #t1002056##k(Warrior Lv. 20)#l\r\n#L12##b #t1002087##k(Warrior Lv. 22)#l\r\n#L13##b #t1002088##k(Warrior Lv. 22)#l\r\n#L14##b #t1002049##k(Warrior Lv. 25)#l\r\n#L15##b #t1002050##k(Warrior Lv. 25)#l\r\n#L16##b #t1002047##k(Warrior Lv. 35)#l\r\n#L17##b #t1002048##k(Warrior Lv. 35)#l\r\n#L18##b #t1002099##k(Warrior Lv. 40)#l\r\n#L19##b #t1002098##k(Warrior Lv. 40)#l\r\n#L20##b #t1002085##k(Warrior Lv. 50)#l\r\n#L21##b #t1002028##k(Warrior Lv. 50)#l\r\n#L22##b #t1002022##k(Warrior Lv. 55)#l\r\n#L23##b #t1002101##k(Warrior Lv. 55)#l" );
    if ( v2 == 0 ) chat_message2( 1, "#t1002041#", "#v1002001# #t1002001# \r\n#v4021006# #t4021006# \r\n#v4031138# 300 mesos" );
    else if ( v2 == 1 ) chat_message2( 2, "#t1002042#", "#v1002001# #t1002001# \r\n#v4011002# #t4011002# \r\n#v4031138# 500 mesos" );
    else if ( v2 == 2 ) chat_message2( 3, "#t1002002#", "#v1002043# #t1002043# \r\n#v4011001# #t4011001# \r\n#v4031138# 500 mesos" );
    else if ( v2 == 3 ) chat_message2( 4, "#t1002044#", "#v1002043# #t1002043# \r\n#v4011002# #t4011002# \r\n#v4031138# 800 mesos" );
    else if ( v2 == 4 ) chat_message2( 5, "#t1002003#", "#v1002039# #t1002039# \r\n#v4011001# #t4011001# \r\n#v4031138# 500 mesos" );
    else if ( v2 == 5 ) chat_message2( 6, "#t1002040#", "#v1002039# #t1002039# \r\n#v4011002# #t4011002# \r\n#v4031138# 800 mesos" );
    else if ( v2 == 6 ) chat_message2( 7, "#t1002007#", "#v1002051# #t1002051# \r\n#v4011001# 2 #t4011001#s \r\n#v4031138# 1.000 mesos" );
    else if ( v2 == 7 ) chat_message2( 8, "#t1002052#", "#v1002051# #t1002051# \r\n#v4011002# 2 #t4011002#s \r\n#v4031138# 1.500 mesos" );
    else if ( v2 == 8 ) chat_message2( 9, "#t1002011#", "#v1002059# #t1002059# \r\n#v4011001# 3 #t4011001#s \r\n#v4031138# 1.500 mesos" );
    else if ( v2 == 9 ) chat_message2( 10, "#t1002058#", "#v1002059# #t1002059# \r\n#v4011002# 3 #t4011002#s \r\n#v4031138# 2.000 mesos" );
    else if ( v2 == 10 ) chat_message2( 11, "#t1002009#", "#v1002055# #t1002055# \r\n#v4011001# 3 #t4011001#s \r\n#v4031138# 1.500 mesos" );
    else if ( v2 == 11 ) chat_message2( 12, "#t1002056#", "#v1002055# #t1002055# \r\n#v4011002# 3 #t4011002#s \r\n#v4031138# 2.000 mesos" );
    else if ( v2 == 12 ) chat_message2( 13, "#t1002087#", "#v1002027# #t1002027# \r\n#v4011002# 4 #t4011002#s \r\n#v4031138# 2.000 mesos" );
    else if ( v2 == 13 ) chat_message2( 14, "#t1002088#", "#v1002027# #t1002027# \r\n#v4011006# 4 #t4011006#s \r\n#v4031138# 4.000 mesos" );
    else if ( v2 == 14 ) chat_message2( 15, "#t1002049#", "#v1002005# #t1002005# \r\n#v4011006# 5 #t4011006#s \r\n#v4031138# 4.000 mesos" );
    else if ( v2 == 15 ) chat_message2( 16, "#t1002050#", "#v1002005# #t1002005# \r\n#v4011005# 5 #t4011005#s \r\n#v4031138# 5.000 mesos" );
    else if ( v2 == 16 ) chat_message2( 17, "#t1002047#", "#v1002004# #t1002004# \r\n#v4021000# 3 #t4021000#s \r\n#v4031138# 8.000 mesos" );
    else if ( v2 == 17 ) chat_message2( 18, "#t1002048#", "#v1002004# #t1002004# \r\n#v4021005# 3 #t4021005#s \r\n#v4031138# 10.000 mesos" );
    else if ( v2 == 18 ) chat_message2( 19, "#t1002099#", "#v1002021# #t1002021# \r\n#v4011002# 5 #t4011002#s \r\n#v4031138# 12.000 mesos" );
    else if ( v2 == 19 ) chat_message2( 20, "#t1002098#", "#v1002021# #t1002021# \r\n#v4011006# 6 #t4011006#s \r\n#v4031138# 15.000 mesos" );
    else if ( v2 == 20 ) chat_message2( 21, "#t1002085#", "#v1002086# #t1002086# \r\n#v4011002# 5 #t4011002#s \r\n#v4031138# 20.000 mesos" );
    else if ( v2 == 21 ) chat_message2( 22, "#t1002028#", "#v1002086# #t1002086# \r\n#v4011004# 4 #t4011004#s \r\n#v4031138# 25.000 mesos" );
    else if ( v2 == 22 ) chat_message10( 100, "#t1002022#", "#v1002100# #t1002100# \r\n#v4011007# #t4011007# \r\n#v4011001# 7 #t4011001#s \r\n#v4031138# 30.000 mesos", "DEX +1, MP +30" );
    else if ( v2 == 23 ) chat_message10( 101, "#t1002101#", "#v1002100# #t1002100# \r\n#v4011007# #t4011007# \r\n#v4011002# 7 #t4011002#s \r\n#v4031138# 30000 mesos", "STR +1, MP +30" );
  }
  else if ( v1 == 3 ) {
    self.sayNext( "So you want to upgrade your shield? Okay then, but I will give you some advice. All item that will be used for perfection will #bdisappear#k. Take this into consideration when you make the decision, okay?" );
    v2 = self.askMenu( "So what kind of shield do you want to perfect and create?\r\n#L0##b #t1092013##k(Warrior Lv. 40)#l\r\n#L1##b #t1092014##k(Warrior Lv. 40)#l\r\n#L2##b #t1092010##k(Warrior Lv. 60)#l\r\n#L3##b #t1092011##k(Warrior Lv. 60)#l" );
    if ( v2 == 0 ) chat_message10( 1, "#t1092013#", "#v1092012# #t1092012# \r\n#v4011002# 10 #t4011002#s \r\n#v4031138# 100.000 mesos", "STR +2" );
    else if ( v2 == 1 ) chat_message10( 2, "#t1092014#", "#v1092012# #t1092012# \r\n#v4011003# #t4011003# \r\n#v4031138# 100.000 mesos", "DEX +2" );
    else if ( v2 == 2 ) chat_message10( 3, "#t1092010#", "#v1092009# #t1092009# \r\n#v4011007# #t4011007# \r\n#v4011004# 15 #t4011004#s \r\n#v4031138# 120.000 mesos", "DEX +2" );
    else if ( v2 == 3 ) chat_message10( 4, "#t1092011#", "#v1092009# #t1092009# \r\n#v4011007# #t4011007# \r\n#v4011003# 15 #t4011003#s \r\n#v4031138# 120.000 mesos", "STR +2" );
  }

function chat_message11(index, makeItem, needItemIcon, needItemString, unitPrice ) {
	inventory = target.inventory;

	nRetNum = self.askNumber( "To make a #b" + makeItem + "#k, I'm going to need the following materials. How many would you like to create?\r\n\r\n#b" + needItemIcon + " 10 " + needItemString + "\r\n#v4031138# " + unitPrice + " mesos#k", 0, 0, 100 );
	nPrice = unitPrice * nRetNum;
	nAllNum = nRetNum * 10;
	nRetBuy = self.askYesNo( "To make #b" + nRetNum + " " + makeItem + "(s)#k, I'm going to need the following materials. Are you sure you want to create them?\r\n\r\n#b" + needItemIcon + " " + nAllNum + " " + needItemString + "\r\n#v4031138# " + nPrice + " mesos#k" );
	if ( nRetBuy == 0 ) self.say( "We have all kinds of items, so do not panic and choose the one you want to buy..." );
	else {
		// To refine Minerals
		if ( index == 1 ) ret = self.InventoryExchange( -nPrice, 4010000, -nAllNum, 4011000, nRetNum );
		else if ( index == 2 ) ret = self.InventoryExchange( -nPrice, 4010001, -nAllNum, 4011001, nRetNum );
		else if (	 index == 3 ) ret = self.InventoryExchange( -nPrice, 4010002, -nAllNum, 4011002, nRetNum );
		else if (	 index == 4 ) ret = self.InventoryExchange( -nPrice, 4010003, -nAllNum, 4011003, nRetNum );
		else if (	 index == 5 ) ret = self.InventoryExchange( -nPrice, 4010004, -nAllNum, 4011004, nRetNum );
		else if (	 index == 6 ) ret = self.InventoryExchange( -nPrice, 4010005, -nAllNum, 4011005, nRetNum );
		else if (	 index == 7 ) ret = self.InventoryExchange( -nPrice, 4010006, -nAllNum, 4011006, nRetNum );
		// To refine Minerals
		else if (	 index == 100 ) ret = self.InventoryExchange( -nPrice, 4020000, -nAllNum, 4021000, nRetNum );
		else if (	 index == 101 ) ret = self.InventoryExchange( -nPrice, 4020001, -nAllNum, 4021001, nRetNum );
		else if (	 index == 102 ) ret = self.InventoryExchange( -nPrice, 4020002, -nAllNum, 4021002, nRetNum );
		else if (	 index == 103 ) ret = self.InventoryExchange( -nPrice, 4020003, -nAllNum, 4021003, nRetNum );
		else if (	 index == 104 ) ret = self.InventoryExchange( -nPrice, 4020004, -nAllNum, 4021004, nRetNum );
		else if (	 index == 105 ) ret = self.InventoryExchange( -nPrice, 4020005, -nAllNum, 4021005, nRetNum );
		else if (	 index == 106 ) ret = self.InventoryExchange( -nPrice, 4020006, -nAllNum, 4021006, nRetNum );
		else if (	 index == 107 ) ret = self.InventoryExchange( -nPrice, 4020007, -nAllNum, 4021007, nRetNum );
		else if (	 index == 108 ) ret = self.InventoryExchange( -nPrice, 4020008, -nAllNum, 4021008, nRetNum );

		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need, and if your inventory is full or not." );
		else self.say("Hey! Here, take the #r" + nRetNum + " " + makeItem + "(s)#k you asked for. This came out better than the order... a very refined item I don't think you'll find anywhere! Please come back again!");
	}
}
function chat_message2(index, makeItem, needItem ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To make a #b" + makeItem + "#k, I'm going to need the following materials. Make sure that you are not going to use an item that is being upgraded as material. What do you think? Do you want a #r" + makeItem + "#k? \r\n\r\n#b" + needItem + "#k?" );
	if ( nRet == 0 ) self.say( "No? Such a shame to hear that. Come back when you need me." );
	else {
		// Making Helmet
		if ( index == 1 ) ret = self.InventoryExchange( -300, 1002001, -1, 4021006, -1, 1002041, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -500, 1002001, -1, 4011002, -1, 1002042, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -500, 1002043, -1, 4011001, -1, 1002002, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -800, 1002043, -1, 4011002, -1, 1002044, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -500, 1002039, -1, 4011001, -1, 1002003, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -800, 1002039, -1, 4011002, -1, 1002040, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -1000, 1002051, -1, 4011001, -2, 1002007, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -1500, 1002051, -1, 4011002, -2, 1002052, 1 );
		else if ( index == 9 ) ret = self.InventoryExchange( -1500, 1002059, -1, 4011001, -3, 1002011, 1 );
		else if ( index == 10 ) ret = self.InventoryExchange( -2000, 1002059, -1, 4011002, -3, 1002058, 1 );
		else if ( index == 11 ) ret = self.InventoryExchange( -1500, 1002055, -1, 4011001, -3, 1002009, 1 );
		else if ( index == 12 ) ret = self.InventoryExchange( -2000, 1002055, -1, 4011002, -3, 1002056, 1 );
		else if ( index == 13 ) ret = self.InventoryExchange( -2000, 1002027, -1, 4011002, -4, 1002087, 1 );
		else if ( index == 14 ) ret = self.InventoryExchange( -4000, 1002027, -1, 4011006, -4, 1002088, 1 );
		else if ( index == 15 ) ret = self.InventoryExchange( -4000, 1002005, -1, 4011006, -5, 1002049, 1 );
		else if ( index == 16 ) ret = self.InventoryExchange( -5000, 1002005, -1, 4011005, -5, 1002050, 1 );
		else if ( index == 17 ) ret = self.InventoryExchange( -8000, 1002004, -1, 4021000, -3, 1002047, 1 );
		else if ( index == 18 ) ret = self.InventoryExchange( -10000, 1002004, -1, 4021005, -3, 1002048, 1 );
		else if ( index == 19 ) ret = self.InventoryExchange( -12000, 1002021, -1, 4011002, -5, 1002099, 1 );
		else if ( index == 20 ) ret = self.InventoryExchange( -15000, 1002021, -1, 4011006, -6, 1002098, 1 );
		else if ( index == 21 ) ret = self.InventoryExchange( -20000, 1002086, -1, 4011002, -5, 1002085, 1 );
		else if ( index == 22 ) ret = self.InventoryExchange( -25000, 1002086, -1, 4011004, -4, 1002028, 1 );

		if ( ret == 0 ) self.say("Please check carefully if you have all the items you need, and if your inventory is full or not.");
		else self.say("There, finished. What do you think, a piece of art, isn't it? Well, if you need anything else, you nkow where to find me.");
	}
}

function chat_message10(index, makeItem, needItem, itemOption ) {
	inventory = target.inventory;

	nRet = self.askYesNo("To Upgrade #b" + makeItem + "#k, I'm going to need the following materials. This item has an option #b" + itemOption + "#k. Make sure that you are not going to use an item that is being upgraded as material. What do you think? Do you want a #r" + makeItem + "#k? \r\n\r\n#b" + needItem);
	if ( nRet == 0 ) self.say("No? Such a shame to hear that. Come back when you need me.");
	else {
		// Making Shield or Helmet
		if ( index == 1 ) ret = self.InventoryExchange( -100000 , 1092012, -1, 4011002, -10, 1092013, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -100000 , 1092012, -1, 4011003, -10, 1092014, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -120000 , 1092009, -1, 4011007, -1, 4011004, -15, 1092010, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -120000 , 1092009, -1, 4011007, -1, 4011003, -15, 1092011, 1 );
		else if ( index == 100 ) ret = self.InventoryExchange( -30000 , 1002100, -1, 4011007, -1, 4011001, -7, 1002022, 1 );
		else if ( index == 101 ) ret = self.InventoryExchange( -30000 , 1002100, -1, 4011007, -1, 4011002, -7, 1002101, 1 );

		if ( ret == 0 ) self.say("Please check carefully if you have all the items you need, and if your inventory is full or not.");
		else self.say("There, finished. What do you think, a piece of art, isn't it? Well, if you need anything else, you nkow where to find me.");
	}
}
'''
