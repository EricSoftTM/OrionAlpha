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
NPC: Francois
Script: Ellinia Item Refiner
'''

self.say("#bScript: refine_ellinia#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

  v1 = self.askMenu( "Welcome to my eco-safe refining operation! What would you like today?\r\n#b#L0#Create a Wand#l\r\n#L1#Create a Staff#l\r\n#L2#Create a Glove#l\r\n#L3#Upgrade a Glove#l\r\n#L4#Upgrade a Hat#l" );
  if ( v1 == 0 ) {
    v2 = self.askMenu( "If you bring me lots of materials, I'll make you a wand with my magic powers. Now... what kind of wand do you want me to do?\r\n#L0##b #t1372005##k(Magician Lv. 8)#l\r\n#L1##b #t1372006##k(Magician Lv. 13)#l\r\n#L2##b #t1372002##k(Magician Lv. 18)#l\r\n#L3##b #t1372004##k(Magician Lv. 23)#l\r\n#L4##b #t1372003##k(Magician Lv. 28)#l\r\n#L5##b #t1372001##k(Magician Lv. 33)#l\r\n#L6##b #t1372000##k(Magician Lv. 38)#l\r\n#L7##b #t1372007##k(Magician Lv. 48)#l" );
    if ( v2 == 0 ) chat_message1( 1, "#t1372005#", "#v4003001# 5 #t4003001#s\r\n#v4031138# 1.000 mesos", 8 );
    else if ( v2 == 1 ) chat_message1( 2, "#t1372006#", "#v4003001# 10 #t4003001#s\r\n#v4000001# 50 #t4000001#s\r\n#v4031138# 3.000 mesos", 13 );
    else if ( v2 == 2 ) chat_message1( 3, "#t1372002#", "#v4011001# #t4011001#\r\n#v4000009# 30 #t4000009#s\r\n#v4003000# 5 #t4003000#s\r\n#v4031138# 5.000 mesos", 18 );
    else if ( v2 == 3 ) chat_message1( 4, "#t1372004#", "#v4011002# 2 #t4011002#s\r\n#v4003002# #t4003002#\r\n#v4003000# 10 #t4003000#s\r\n#v4031138# 12.000 mesos", 23 );
    else if ( v2 == 4 ) chat_message1( 5, "#t1372003#", "#v4011002# 3 #t4011002#s\r\n#v4021002# #t4021002#\r\n#v4003000# 10 #t4003000#s\r\n#v4031138# 30.000 mesos", 28 );
    else if ( v2 == 5 ) chat_message1( 6, "#t1372001#", "#v4021006# 5 #t4021006#s\r\n#v4011002# 3 #t4011002#s\r\n#v4011001# #t4011001#\r\n#v4003000# 15 #t4003000#s\r\n#v4031138# 60.000 mesos", 33 );
    else if ( v2 == 6 ) chat_message1( 7, "#t1372000#", "#v4021006# 5 #t4021006#s\r\n#v4021005# 5 #t4021005#s\r\n#v4021007# #t4021007#\r\n#v4003003# #t4003003#\r\n#v4003000# 20 #t4003000#s\r\n#v4031138# 120.000 mesos", 38 );
    else if ( v2 == 7 ) chat_message1( 8, "#t1372007#", "#v4011006# 4 #t4011006#s\r\n#v4021003# 3 #t4021003#s\r\n#v4021007# 2 #t4021007#s\r\n#v4021002# #t4021002#\r\n#v4003002# #t4003002#\r\n#v4003000# 30 #t4003000#s\r\n#v4031138# 200.000 mesos", 48 );
  }
  else if ( v1 == 1 ) {
    v2 = self.askMenu( "If you bring me many materials, I will make you a staff with my magical powers. Now... what kind of staff do you want me to do?\r\n#L0##b#t1382000##k (Magician Lv. 10)#l\r\n#L1##b#t1382003##k(Magician Lv. 15)#l\r\n#L2##b#t1382005##k(Magician Lv. 15)#l\r\n#L3##b#t1382004##k(Magician Lv. 20)#l\r\n#L4##b#t1382002##k(Magician Lv. 25)#l\r\n#L5##b#t1382001##k(Magician Lv. 45)#l" );
    if ( v2 == 0 ) chat_message1( 100, "#t1382000#", "#v4003001# 5 #t4003001#s\r\n2.000 mesos", 10 );
    else if ( v2 == 1 ) chat_message1( 101, "#t1382003#", "#v4021005# #t4021005#\r\n#v4011001# #t4011001#\r\n#v4003000# 5 #t4003000#s\r\n#v4031138# 2.000 mesos", 15 );
    else if ( v2 == 2 ) chat_message1( 102, "#t1382005#", "#v4021003# #t4021003#\r\n#v4011001# #t4011001#\r\n#v4003000# 5 #t4003000#s\r\n#v4031138# 2.000 mesos", 15 );
    else if ( v2 == 3 ) chat_message1( 103, "#t1382004#", "#v4003001# 50 #t4003001#s\r\n#v4011001# #t4011001#\r\n#v4003000# 10 #t4003000#s\r\n#v4031138# 5.000 mesos", 20 );
    else if ( v2 == 4 ) chat_message1( 104, "#t1382002#", "#v4021006# 2 #t4021006#s\r\n#v4021001# #t4021001#\r\n#v4011001# #t4011001#\r\n#v4003000# 15 #t4003000#s\r\n#v4031138# 12.000 mesos", 25 );
    else if ( v2 == 5 ) chat_message1( 105, "#t1382001#", "#v4011001# 3 #t4011001#s\r\n#v4021001# 5 #t4021001#s\r\n#v4021006# 5 #t4021006#s\r\n#v4021005# 5 #t4021005#s\r\n#v4003003# #t4003003#\r\n#v4000010# 50 #t4000010#s\r\n#v4003000# 30 #t4003000#s\r\n#v4031138# 180.000 mesos", 45 );
  }
  else if ( v1 == 2 ) {
    v2 = self.askMenu( "If you bring me many materials, I'll make you a glove with my magic powers. Now ... what kind of glove do you want me to do?\r\n#L0##b #t1082019##k (Magician Lv. 15)#l\r\n#L1##b #t1082020##k(Magician Lv. 20)#l\r\n#L2##b #t1082026##k(Magician Lv. 25)#l\r\n#L3##b #t1082051##k(Magician Lv. 30)#l\r\n#L4##b #t1082054##k(Magician Lv. 35)#l\r\n#L5##b #t1082062##k(Magician Lv. 40)#l\r\n#L6##b #t1082081##k(Magician Lv. 50)#l\r\n#L7##b #t1082086##k(Magician Lv. 60)#l" );
    if ( v2 == 0 ) chat_message1( 200, "#t1082019#", "#v4000021# 15 #t4000021#s\r\n#v4031138# 7.000 mesos", 15 );
    else if ( v2 == 1 ) chat_message1( 201, "#t1082020#", "#v4000021# 30 #t4000021#s\r\n#v4011001# #t4011001#\r\n#v4031138# 15.000 mesos", 20 );
    else if ( v2 == 2 ) chat_message1( 202, "#t1082026#", "#v4000021# 50 #t4000021#s\r\n#v4011006# 2 #t4011006#s\r\n#v4031138# 20.000 mesos", 25 );
    else if ( v2 == 3 ) chat_message1( 203, "#t1082051#", "#v4000021# 60 #t4000021#s\r\n#v4021006# #t4021006#\r\n#v4021000# 2 #t4021000#s\r\n#v4031138# 25.000 mesos", 30 );
    else if ( v2 == 4 ) chat_message1( 204, "#t1082054#", "#v4000021# 70 #t4000021#s\r\n#v4011006# #t4011006#\r\n#v4011001# 3 #t4011001#s\r\n#v4021000# 2 #t4021000#s\r\n#v4031138# 30.000 mesos", 35 );
    else if ( v2 == 5 ) chat_message1( 205, "#t1082062#", "#v4000021# 80 #t4000021#s\r\n#v4021000# 3 #t4021000#s\r\n#v4021006# 3 #t4021006#s\r\n#v4003000# 30 #t4003000#s\r\n#v4031138# 40.000 mesos", 40 );
    else if ( v2 == 6 ) chat_message1( 206, "#t1082081#", "#v4021000# 3 #t4021000#s\r\n#v4011006# 2 #t4011006#s\r\n#v4000030# 35 #t4000030#s\r\n#v4003000# 40 #t4003000#s\r\n#v4031138# 50.000 mesos", 50 );
    else if ( v2 == 7 ) chat_message1( 207, "#t1082086#", "#v4011007# #t4011007#\r\n#v4011001# 8 #t4011001#s\r\n#v4021007# #t4021007#\r\n#v4000030# 50 #t4000030#s\r\n#v4003000# 50 #t4003000#s\r\n#v4031138# 70.000 mesos", 60 );
  }
  else if ( v1 == 3 ) {
    self.sayNext( "Do you want to perfect a glove? Be careful! All items used for upgrading will disappear, so you better think well before you make your decision..." );
    v2 = self.askMenu( "Now... which glove do you want to upgrade?\r\n#L0##b #t1082021##k(Magician Lv. 20)#l\r\n#L1##b #t1082022##k(Magician Lv. 20)#l\r\n#L2##b #t1082027##k(Magician Lv. 25)#l\r\n#L3##b #t1082028##k(Magician Lv. 25)#l\r\n#L4##b #t1082052##k(Magician Lv. 30)#l\r\n#L5##b #t1082053##k(Magician Lv. 30)#l\r\n#L6##b #t1082055##k(Magician Lv. 35)#l\r\n#L7##b #t1082056##k(Magician Lv. 35)#l\r\n#L8##b #t1082063##k(Magician Lv. 40)#l\r\n#L9##b #t1082064##k(Magician Lv. 40)#l\r\n#L10##b #t1082082##k(Magician Lv. 50)#l\r\n#L11##b #t1082080##k(Magician Lv. 50)#l\r\n#L12##b #t1082087##k(Magician Lv. 60)#l\r\n#L13##b #t1082088##k(Magician Lv. 60)#l" );
    if ( v2 == 0 ) chat_message2( 1, "#t1082021#", "#v1082020# #t1082020#\r\n#t4021003#\r\n#v4031138# 20.000 mesos", 20, "INT +1" );
    else if ( v2 == 1 ) chat_message2( 2, "#t1082022#", "#v1082020# #t1082020#\r\n#v4021001# 2 #t4021001#s\r\n#v4031138# 25.000 mesos", 20, "INT +2" );
    else if ( v2 == 2 ) chat_message2( 3, "#t1082027#", "#v1082026# #t1082026#\r\n#v4021000# 3 #t4021000#s\r\n#v4031138# 30.000 mesos", 25, "INT +1" );
    else if ( v2 == 3 ) chat_message2( 4, "#t1082028#", "#v1082026# #t1082026#\r\n#v4021008# #t4021008#\r\n#v4031138# 40.000 mesos", 25, "INT +2" );
    else if ( v2 == 4 ) chat_message2( 5, "#t1082052#", "#v1082051# #t1082051#\r\n#v4021005# 3 #t4021005#s\r\n#v4031138# 35.000 mesos", 30, "INT +1" );
    else if ( v2 == 5 ) chat_message2( 6, "#t1082053#", "#v1082051# #t1082051#\r\n#v4021008# #t4021008#s\r\n#v4031138# 40.000 mesos", 30, "INT +2" );
    else if ( v2 == 6 ) chat_message2( 7, "#t1082055#", "#v1082054# #t1082054#\r\n#v4021005# 3 #t4021005#s\r\n#v4031138# 40.000 mesos", 35, "INT +1" );
    else if ( v2 == 7 ) chat_message2( 8, "#t1082056#", "#v1082054# #t1082054#\r\n#v4021008# #t4021008#s\r\n#v4031138# 45.000 mesos", 35, "INT +2" );
    else if ( v2 == 8 ) chat_message2( 9, "#t1082063#", "#v1082062# #t1082062#\r\n#v4021002# 4 #t4021002#s\r\n#v4031138# 45.000 mesos", 40, "INT +2" );
    else if ( v2 == 9 ) chat_message2( 10, "#t1082064#", "#v1082062# #t1082062#\r\n#v4021008# 2 #t4021008#s\r\n#v4031138# 50.000 mesos", 40, "INT +3" );
    else if ( v2 == 10 ) chat_message2( 11, "#t1082082#", "#v1082081# #t1082081#\r\n#v4021002# 5 #t4021002#s\r\n#v4031138# 55.000 mesos", 50, "INT +2, MP +15" );
    else if ( v2 == 11 ) chat_message2( 12, "#t1082080#", "#v1082081# #t1082081#\r\n#v4021008# 3 #t4021008#s\r\n#v4031138# 60.000 mesos", 50, "INT +3, MP +30" );
    else if ( v2 == 12 ) chat_message2( 13, "#t1082087#", "#v1082086# #t1082086#\r\n#v4011004# 3 #t4011004#s\r\n#v4011006# 5 #t4011006#s\r\n#v4031138# 70.000 mesos", 60, "INT +2, SOR +1, MP +15" );
    else if ( v2 == 13 ) chat_message2( 14, "#t1082088#", "#v1082086# #t1082086#\r\n#v4021008# 2 #t4021008#s\r\n#v4011006# 3 #t4011006#s\r\n#v4031138# 80.000 mesos", 60, "INT + 3, SOR + 1, MP + 30" );
  }
  else if ( v1 == 4 ) {
    self.sayNext( "So you want to improve your hat ... Take care! All items used for upgrading will disappear, so you better think well before you make your decision..." );
    v2 = self.askMenu( "What's up? What hat? You want to improve?\r\n#L0##b#t1002065##k(Magician Lv. 30)#l\r\n#L1##b#t1002013##k(Magician Lv. 30)#l" );
    if ( v2 == 0 ) chat_message2( 100, "#t1002065#", "#v1002064# #t1002064#\r\n#v4011001# 3 #t4011001#s\r\n#v4031138# 40.000 mesos", 30, "INT +1" );
    else if ( v2 == 1 ) chat_message2( 101, "#t1002013#", "#v1002064# #t1002064#\r\n#v4011006# 3 #t4011006#s\r\n#v4031138# 50.000 mesos", 30, "INT +2" );
  }

function chat_message1(index, makeItem, needItem, reqLevel ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To make a #b" + makeItem + "#k, You will need the items below. The level cap for the item will be #d" + reqLevel + "#k So please, first of all, make sure you really need the item. Are you sure you want to create one?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "No? You do not have all the materials. Try to get them all in town. Fortunately, it seems that the monsters around the forest have the most diverse types of materials with them." );
	else {
		// magic wand
		if ( index == 1 ) ret = self.InventoryExchange( -1000, 4003001, -5, 1372005, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -3000, 4003001, -10, 4000001, -50, 1372006, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -5000, 4011001, -1, 4000009, -30, 4003000, -5, 1372002, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -12000, 4011002, -2, 4003002, -1, 4003000, -10, 1372004, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -30000, 4011002, -3, 4021002, -1, 4003000, -10, 1372003, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -60000, 4021006, -5, 4011002, -3, 4011001, -1, 4003000, -15, 1372001, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -120000, 4021006, -5, 4021005, -5, 4021007, -1, 4003003, -1, 4003000, -20, 1372000, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -200000, 4011006, -4, 4021003, -3, 4021007, -2, 4021002, -1, 4003002, -1, 4003000, -30, 1372007, 1 );
		// staff
		else if ( index == 100 ) ret = self.InventoryExchange( -2000, 4003001, -5, 1382000, 1 );
		else if ( index == 101 ) ret = self.InventoryExchange( -2000, 4021005, -1, 4011001, -1, 4003000, -5, 1382003, 1 );
		else if ( index == 102 ) ret = self.InventoryExchange( -2000, 4021003, -1, 4011001, -1, 4003000, -5, 1382005, 1 );
		else if ( index == 103 ) ret = self.InventoryExchange( -5000, 4003001, -50, 4011001, -1, 4003000, -10, 1382004, 1 );
		else if ( index == 104 ) ret = self.InventoryExchange( -12000, 4021006, -2, 4021001, -1, 4011001, -1, 4003000, -15, 1382002, 1 );
		else if ( index == 105 ) ret = self.InventoryExchange( -180000, 4011001, -3, 4021001, -5, 4021006, -5, 4021005, -5, 4003003, -1, 4000010, -50, 4003000, -30, 1382001, 1 );
		// glove
		else if ( index == 200 ) ret = self.InventoryExchange( -7000, 4000021, -15, 1082019, 1 );
		else if ( index == 201 ) ret = self.InventoryExchange( -15000, 4000021, -30, 4011001, -1, 1082020, 1 );
		else if ( index == 202 ) ret = self.InventoryExchange( -20000, 4000021, -50, 4011006, -2, 1082026, 1 );
		else if ( index == 203 ) ret = self.InventoryExchange( -25000, 4000021, -60, 4021006, -1, 4021000, -2, 1082051, 1 );
		else if ( index == 204 ) ret = self.InventoryExchange( -30000, 4000021, -70, 4011006, -1, 4011001, -3, 4021000, -2, 1082054, 1 );
		else if ( index == 205 ) ret = self.InventoryExchange( -40000, 4000021, -80, 4021000, -3, 4021006, -3, 4003000, -30, 1082062, 1 );
		else if ( index == 206 ) ret = self.InventoryExchange( -50000, 4021000, -3, 4011006, -2, 4000030, -35, 4003000, -40, 1082081, 1 );
		else if ( index == 207 ) ret = self.InventoryExchange( -70000, 4011007, -1, 4011001, -8, 4021007, -1, 4000030, -50, 4003000, -50, 1082086, 1 );

		if ( ret == 0 ) self.say( "Please make sure you have all the items you need or if your inventory is full or not." );
		else self.say( "Here, take the #r" + makeItem + "#k. The more I see, the more it seems perfect to me. Haha, it does not hurt to think that the other magicians are afraid of my abilities..." );
	}
}

function chat_message2(index, makeItem, needItem, reqLevel, itemOption ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To upgrade #b" + makeItem + "#k, You will need the items below. The level cap for the item is #d" + reqLevel + "#k, with the option of #r" + itemOption + "#k embedded in it, so please see if you really need it. Ah! And there's one more thing. Please make sure NOT to use an item that has been upgraded as an enhancement material. Are you sure you want to create this item?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "No? You do not have all the materials. Try to get them all in town. Fortunately, it seems that the monsters around the forest have the most diverse types of materials with them." );
	else {
		// Glove
		if ( index == 1 ) ret = self.InventoryExchange( -20000, 1082020, -1, 4021003, -1, 1082021, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -25000, 1082020, -1, 4021001, -2, 1082022, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -30000, 1082026, -1, 4021000, -3, 1082027, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -40000, 1082026, -1, 4021008, -1, 1082028, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -35000, 1082051, -1, 4021005, -3, 1082052, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -40000, 1082051, -1, 4021008, -1, 1082053, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -40000, 1082054, -1, 4021005, -3, 1082055, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -45000, 1082054, -1, 4021008, -1, 1082056, 1 );
		else if ( index == 9 ) ret = self.InventoryExchange( -45000, 1082062, -1, 4021002, -4, 1082063, 1 );
		else if ( index == 10 ) ret = self.InventoryExchange( -50000, 1082062, -1, 4021008, -2, 1082064, 1 );
		else if ( index == 11 ) ret = self.InventoryExchange( -55000, 1082081, -1, 4021002, -5, 1082082, 1 );
		else if ( index == 12 ) ret = self.InventoryExchange( -60000, 1082081, -1, 4021008, -3, 1082080, 1 );
		else if ( index == 13 ) ret = self.InventoryExchange( -70000, 1082086, -1, 4011004, -3, 4011006, -5, 1082087, 1 );
		else if ( index == 14 ) ret = self.InventoryExchange( -80000, 1082086, -1, 4021008, -2, 4011006, -3, 1082088, 1 );
		// Hat
		else if ( index == 100 ) ret = self.InventoryExchange( -40000, 1002064, -1, 4011001, -3, 1002065, 1 );
		else if ( index == 101 ) ret = self.InventoryExchange( -50000, 1002064, -1, 4011006, -3, 1002013, 1 );

		if ( ret == 0 ) self.say( "Please make sure you have all the items you need or if your inventory is full or not." );
		else self.say( "It's a success! Oh, I've never felt so alive! Please come back again!" );
	}
}
'''
