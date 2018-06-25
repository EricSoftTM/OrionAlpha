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

'NPC: Vicious'
'Script: Henesys Item Maker'

self.say("#bScript: refine_henesys#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

  v1 = self.askMenu( "Hello. I am Vicious, retired Sniper. However, I used to be the top student of Athena Pierce. Though I no longer hunt, I can make some archer items that will be useful for you...\r\n#b#L0#Create a Bow#l\r\n#L1#Create a Crossbow#l\r\n#L2#Create a Glove#l\r\n#L3#Upgrade a Glove#l\r\n#L4#Create Materials#l\r\n#L5#Create Arrows#l");
  if ( v1 == 0 ) {
    v2 = self.askMenu( "I can make a bow if you can get me some materials. What kind of bow do you want?\r\n#L0##b #t1452002##k(Archer Lv. 10)#l\r\n#L1##b #t1452003##k(Archer Lv. 15)#l\r\n#L2##b #t1452001##k(Archer Lv. 20)#l\r\n#L3##b #t1452000##k(Archer Lv. 25)#l\r\n#L4##b #t1452005##k(Archer Lv. 30)#l\r\n#L5##b #t1452006##k(Archer Lv. 35)#l\r\n#L6##b #t1452007##k(Archer Lv. 40)#l" );
    if ( v2 == 0 ) chat_message1( 1, "#t1452002#", "#v4003001# 5 #t4003001#s \r\n#v4000000# 30 #t4000000#s \r\n#v4031138# 800 mesos", 10 );
    else if ( v2 == 1 ) chat_message1( 2, "#t1452003#", "#v4011001# #t4011001# \r\n#v4003000# 3 #t4003000#s \r\n#v4031138# 2.000 mesos", 15 );
    else if ( v2 == 2 ) chat_message1( 3, "#t1452001#", "#v4003001# 30 #t4003001#s \r\n#v4000016# 50 #t4000016#s \r\n#v4031138# 3.000 mesos", 20 );
    else if ( v2 == 3 ) chat_message1( 4, "#t1452000#", "#v4011001# 2 #t4011001#s \r\n#v4021006# 2 #t4021006#s \r\n#v4003000# 8 #t4003000#s \r\n#v4031138# 5.000 mesos", 25 );
    else if ( v2 == 4 ) chat_message1( 5, "#t1452005#", "#v4011001# 5 #t4011001#s \r\n#v4011006# 5 #t4011006#s \r\n#v4021003# 3 #t4021003#s \r\n#v4021006# 2 #t4021006#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 30.000 mesos", 30 );
    else if ( v2 == 5 ) chat_message1( 6, "#t1452006#", "#v4011004# 7 #t4011004#s \r\n#v4021000# 6 #t4021000#s \r\n#v4021004# 3 #t4021004#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 40.000 mesos", 35 );
    else if ( v2 == 6 ) chat_message1( 7, "#t1452007#", "#v4021008# #t4021008# \r\n#v4011001# 10 #t4011001#s \r\n#v4011006# 3 #t4011006#s \r\n#v4000014# 50 #t4000014#s \r\n#v4003000# 40 #t4003000#s \r\n#v4031138# 80.000 mesos", 40 );
  }
  else if ( v1 == 1 ) {
    v2 = self.askMenu( "I can make a crossbow if you can get me some materials. What kind of crossbow do you want?\r\n#L0##b #t1462001##k(Archer Lv. 12)#l\r\n#L1##b #t1462002##k(Archer Lv. 18)#l\r\n#L2##b #t1462003##k(Archer Lv. 22)#l\r\n#L3##b #t1462000##k(Archer Lv. 28)#l\r\n#L4##b #t1462004##k(Archer Lv. 32)#l\r\n#L5##b #t1462005##k(Archer Lv. 38)#l\r\n#L6##b #t1462006##k(Archer Lv. 42)#l\r\n#L7##b #t1462007##k(Archer Lv. 50)#l" );
    if ( v2 == 0 ) chat_message1( 100, "#t1462001#", "#v4003001# 7 #t4003001#s \r\n#v4003000# 2 #t4003000#s \r\n#v4031138# 1.000 mesos", 12 );
    else if ( v2 == 1 ) chat_message1( 101, "#t1462002#", "#v4003001# 20 #t4003001#s \r\n#v4011001# #t4011001# \r\n#v4003000# 5 #t4003000#s \r\n#v4031138# 2.000 mesos", 18 );
    else if ( v2 == 2 ) chat_message1( 102, "#t1462003#", "#v4003001# 50 #t4003001#s \r\n#v4011001# #t4011001# \r\n#v4003000# 8 #t4003000#s \r\n#v4031138# 3.000 mesos", 22 );
    else if ( v2 == 3 ) chat_message1( 103, "#t1462000#", "#v4011001# 2 #t4011001#s \r\n#v4021006# #t4021006# \r\n#v4021002# #t4021002# \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 10.000 mesos", 28 );
    else if ( v2 == 4 ) chat_message1( 104, "#t1462004#", "#v4011001# 5 #t4011001#s \r\n#v4011005# 5 #t4011005#s \r\n#v4021006# 3 #t4021006#s \r\n#v4003001# 50 #t4003001#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 30.000 mesos", 32 );
    else if ( v2 == 5 ) chat_message1( 105, "#t1462005#", "#v4021008# #t4021008# \r\n#v4011001# 8 #t4011001#s \r\n#v4011006# 4 #t4011006#s \r\n#v4021006# 2 #t4021006#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 50.000 mesos", 38 );
    else if ( v2 == 6 ) chat_message1( 106, "#t1462006#", "#v4021008# 2 #t4021008#s \r\n#v4011004# 6 #t4011004#s \r\n#v4003001# 30 #t4003001#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 80.000 mesos", 42 );
    else if ( v2 == 7 ) chat_message1( 107, "#t1462007#", "#v4021008# 2 #t4021008#s \r\n#v4011006# 5 #t4011006#s \r\n#v4021006# 3 #t4021006#s \r\n#v4003001# 40 #t4003001#s \r\n#v4003000# 40 #t4003000#s \r\n#v4031138# 100.000 mesos", 50 );
  }
  else if ( v1 == 2 ) {
    v2 = self.askMenu( "I'll make a glove if you can get me some materials. What kind of glove do you want?\r\n#L0##b #t1082012##k(Archer Lv. 15)#l\r\n#L1##b #t1082013##k(Archer Lv. 20)#l\r\n#L2##b #t1082016##k(Archer Lv. 25)#l\r\n#L3##b #t1082048##k(Archer Lv. 30)#l\r\n#L4##b #t1082068##k(Archer Lv. 35)#l\r\n#L5##b #t1082071##k(Archer Lv. 40)#l\r\n#L6##b #t1082084##k(Archer Lv. 50)#l\r\n#L7##b #t1082089##k(Archer Lv. 60)#l" );
    if ( v2 == 0 ) chat_message1( 200, "#t1082012#", "#v4000021# 15 #t4000021#s \r\n#v4000009# 20 #t4000009#s \r\n#v4031138# 5.000 mesos", 15 );
    else if ( v2 == 1 ) chat_message1( 201, "#t1082013#", "#v4000021# 20 #t4000021#s \r\n#v4000009# 20 #t4000009#s \r\n#v4011001# 2 #t4011001#s \r\n#v4031138# 10.000 mesos", 20 );
    else if ( v2 == 2 ) chat_message1( 202, "#t1082016#", "#v4000021# 40 #t4000021#s \r\n#v4000009# 50 #t4000009#s \r\n#v4011006# 2 #t4011006#s \r\n#v4031138# 15.000 mesos", 25 );
    else if ( v2 == 3 ) chat_message1( 203, "#t1082048#", "#v4000021# 50 #t4000021#s \r\n#v4021001# #t4021001# \r\n#v4011006# 2 #t4011006#s \r\n#v4031138# 20.000 mesos", 30 );
    else if ( v2 == 4 ) chat_message1( 204, "#t1082068#", "#v4000021# 60 #t4000021#s \r\n#v4011001# 3 #t4011001#s \r\n#v4011000# #t4011000# \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 30.000 mesos", 35 );
    else if ( v2 == 5 ) chat_message1( 205, "#t1082071#", "#v4000021# 80 #t4000021#s \r\n#v4021002# 3 #t4021002#s \r\n#v4011001# 3 #t4011001#s \r\n#v4021000# #t4021000# \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 40.000 mesos", 40 );
    else if ( v2 == 6 ) chat_message1( 206, "#t1082084#", "#v4011004# 3 #t4011004#s \r\n#v4021002# 2 #t4021002#s \r\n#v4011006# #t4011006# \r\n#v4000030# 40 #t4000030#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50 );
    else if ( v2 == 7 ) chat_message1( 207, "#t1082089#", "#v4011007# #t4011007# \r\n#v4021006# 8 #t4021006#s \r\n#v4011006# 2 #t4011006#s \r\n#v4000030# 50 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 70.000 mesos", 60 );
  }
  else if ( v1 == 3 ) {
    self.sayNext( "Do you want to perfect a glove? It's best to be careful about that. All items that will be used for perfection will #bdisappear#k. Think carefully before you make your decision." );
    v2 = self.askMenu( "Let's see, what kind of glove do you want to perfect?\r\n#L0##b#t1082014##k(Archer Lv. 20)#l\r\n#L1##b#t1082015##k(Archer Lv. 20)#l\r\n#L2##b#t1082017##k(Archer Lv. 25)#l\r\n#L3##b#t1082018##k(Archer Lv. 25)#l\r\n#L4##b#t1082049##k(Archer Lv. 30)#l\r\n#L5##b#t1082050##k(Archer Lv. 30)#l\r\n#L6##b#t1082069##k(Archer Lv. 35)#l\r\n#L7##b#t1082070##k(Archer Lv. 35)#l\r\n#L8##b#t1082072##k(Archer Lv. 40)#l\r\n#L9##b#t1082073##k(Archer Lv. 40)#l\r\n#L10##b#t1082085##k(Archer Lv. 50)#l\r\n#L11##b#t1082083##k(Archer Lv. 50)#l\r\n#L12##b#t1082090##k(Archer Lv. 60)#l\r\n#L13##b#t1082091##k(Archer Lv. 60)#l" );
    if ( v2 == 0 ) chat_message2( 1, "#t1082014#", "#v1082013# #t1082013# \r\n#v4021000# #t4021000# \r\n#v4031138# 5.000 mesos", 20 );
    else if ( v2 == 1 ) chat_message2( 2, "#t1082015#", "#v1082013# #t1082013# \r\n#v4021003# 2 #t4021003#s \r\n#v4031138# 7.000 mesos", 20 );
    else if ( v2 == 2 ) chat_message2( 3, "#t1082017#", "#v1082016# #t1082016# \r\n#v4021000# 3 #t4021000#s \r\n#v4031138# 10.000 mesos", 25 );
    else if ( v2 == 3 ) chat_message2( 4, "#t1082018#", "#v1082016# #t1082016# \r\n#v4021008# #t4021008# \r\n#v4031138# 12.000 mesos", 25 );
    else if ( v2 == 4 ) chat_message2( 5, "#t1082049#", "#v1082048# #t1082048# \r\n#v4021003# 3 #t4021003#s \r\n#v4031138# 15.000 mesos", 30 );
    else if ( v2 == 5 ) chat_message2( 6, "#t1082050#", "#v1082048# #t1082048# \r\n#v4021008# #t4021008# \r\n#v4031138# 20.000 mesos", 30 );
    else if ( v2 == 6 ) chat_message2( 7, "#t1082069#", "#v1082068# #t1082068# \r\n#v4011002# 4 #t4011002#s \r\n#v4031138# 22.000 mesos", 35 );
    else if ( v2 == 7 ) chat_message2( 8, "#t1082070#", "#v1082068# #t1082068# \r\n#v4011006# 2 #t4011006#s \r\n#v4031138# 25.000 mesos", 35 );
    else if ( v2 == 8 ) chat_message2( 9, "#t1082072#", "#v1082071# #t1082071# \r\n#v4011006# 4 #t4011006#s \r\n#v4031138# 30.000 mesos", 40 );
    else if ( v2 == 9 ) chat_message2( 10, "#t1082073#", "#v1082071# #t1082071# \r\n#v4021008# 2 #t4021008#s \r\n#v4031138# 40.000 mesos", 40 );
    else if ( v2 == 10 ) chat_message2( 11, "#t1082085#", "#v1082084# #t1082084# \r\n#v4021000# 5 #t4021000#s \r\n#v4011000# #t4011000# \r\n#v4031138# 55.000 mesos", 50 );
    else if ( v2 == 11 ) chat_message2( 12, "#t1082083#", "#v1082084# #t1082084# \r\n#v4021008# 2 #t4021008#s \r\n#v4011006# 2 #t4011006#s \r\n#v4031138# 60.000 mesos", 50 );
    else if ( v2 == 12 ) chat_message2( 13, "#t1082090#", "#v1082089# #t1082089# \r\n#v4021007# #t4021007# \r\n#v4021000# 5 #t4021000#s \r\n#v4031138# 70.000 mesos", 60 );
    else if ( v2 == 13 ) chat_message2( 14, "#t1082091#", "#v1082089# #t1082089# \r\n#v4021007# 2 #t4021007#s \r\n#v4021008# 2 #t4021008#s \r\n#v4031138# 80.000 mesos", 60 );
  }
  else if ( v1 == 4 ) {
    v2 = self.askMenu( "So, you want to create some materials, right? Very good... what kind?\r\n#L0##b Create #t4003001# with #t4000003##k#l\r\n#L1##b Create #t4003001# with #t4000018##k#l\r\n#L2##b Create #t4003000##k#l" );
    if ( v2 == 0 ) chat_message3( 1, "#t4003001#", "#t4000003#", 10, 1 );
    else if ( v2 == 1 ) chat_message3( 2, "#t4003001#", "#t4000018#(s)", 5, 1 );
    else if ( v2 == 2 ) chat_message3( 3, "#t4003000#", "#t4011001#(s) and #t4011000#(s)", 1, 15 );
  }
  else if ( v1 == 5 ) {
    v2 = self.askMenu( "So, you want to create arrows? The better the arrow, the greater the advantage you will have in battle. All right, what kind?\r\n#L0##b #t2060000##k#l\r\n#L1##b #t2061000##k#l\r\n#L2##b #t2060001##k#l\r\n#L3##b #t2061001##k#l" );
    if ( v2 == 0 ) chat_message6( 1, "#t2060000#", "#v4003001# #t4003001# \r\n#v4003004# #t4003004# ", 1000, "" );
    else if ( v2 == 1 ) chat_message6( 2, "#t2061000#", "#v4003001# #t4003001# \r\n#v4003004# #t4003004# ", 1000, "" );
    else if ( v2 == 2 ) chat_message6( 3, "#t2060001#", "#v4011000# #t4011000# \r\n#v4003001# 3 #t4003001#s \r\n#v4003004# 10 #t4003004#s ", 900, "Atk. +1" );
    else if ( v2 == 3 ) chat_message6( 4, "#t2061001#", "#v4011000# #t4011000# \r\n#v4003001# 3 #t4003001#s \r\n#v4003004# 10 #t4003004#s ", 900, "Atk. +1" );
  }

function chat_message1(index, makeItem, needItem, reqLevel ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To make a #b" + makeItem + "#k, I'll need the following materials. The Equipment Level Requirement: #d" + reqLevel + "#k and please make sure that you will not use an item that is being upgraded as material. What do you think? Do you want a #r" + makeItem + "#k?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "Oh, right? Do not have the materials? Please come back later. Looks like I'm staying here for a while." );
	else {
		// Making a bow
		if ( index == 1 ) ret = self.InventoryExchange( -800, 4003001, -5, 4000000, -30, 1452002, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -2000, 4011001, -1, 4003000, -3, 1452003, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -3000, 4003001, -30, 4000016, -50, 1452001, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -5000, 4011001, -2, 4021006, -2, 4003000, -8, 1452000, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -30000, 4011001, -5, 4011006, -5, 4021003, -3, 4021006, -2, 4003000, -30, 1452005, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -40000, 4011004, -7, 4021000, -6, 4021004, -3, 4003000, -35, 1452006, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -80000, 4021008, -1, 4011001, -10, 4011006, -3, 4000014, -50, 4003000, -40, 1452007, 1 );
		// Making a crossbow
		else if ( index == 100 ) ret = self.InventoryExchange( -1000, 4003001, -7, 4003000, -2, 1462001, 1 );
		else if ( index == 101 ) ret = self.InventoryExchange( -2000, 4003001, -20, 4011001, -1, 4003000, -5, 1462002, 1 );
		else if ( index == 102 ) ret = self.InventoryExchange( -3000, 4003001, -50, 4011001, -1, 4003000, -8, 1462003, 1 );
		else if ( index == 103 ) ret = self.InventoryExchange( -10000, 4011001, -2, 4021006, -1, 4021002, -1, 4003000, -10, 1462000, 1 );
		else if ( index == 104 ) ret = self.InventoryExchange( -30000, 4011001, -5, 4011005, -5, 4021006, -3, 4003001, -50, 4003000, -15, 1462004, 1 );
		else if ( index == 105 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -8, 4011006, -4, 4021006, -2, 4003000, -20, 1462005, 1 );
		else if ( index == 106 ) ret = self.InventoryExchange( -80000, 4021008, -2, 4011004, -6, 4003001, -30, 4003000, -30, 1462006, 1 );
		else if ( index == 107 ) ret = self.InventoryExchange( -100000, 4021008, -2, 4011006, -5, 4021006, -3, 4003001, -40, 4003000, -40, 1462007, 1 );
		// Making a glove
		else if ( index == 200 ) ret = self.InventoryExchange( -5000, 4000021, -15, 4000009, -20, 1082012, 1 );
		else if ( index == 201 ) ret = self.InventoryExchange( -10000, 4000021, -20, 4000009, -20, 4011001, -2, 1082013, 1 );
		else if ( index == 202 ) ret = self.InventoryExchange( -15000, 4000021, -40, 4000009, -50, 4011006, -2, 1082016, 1 );
		else if ( index == 203 ) ret = self.InventoryExchange( -20000, 4000021, -50, 4021001, -1, 4011006, -2, 1082048, 1 );
		else if ( index == 204 ) ret = self.InventoryExchange( -30000, 4000021, -60, 4011001, -3, 4011000, -1, 4003000, -15, 1082068, 1 );
		else if ( index == 205 ) ret = self.InventoryExchange( -40000, 4000021, -80, 4021002, -3, 4011001, -3, 4021000, -1, 4003000, -25, 1082071, 1 );
		else if ( index == 206 ) ret = self.InventoryExchange( -50000, 4011004, -3, 4021002, -2, 4011006, -1, 4000030, -40, 4003000, -35, 1082084, 1 );
		else if ( index == 207 ) ret = self.InventoryExchange( -70000, 4011007, -1, 4021006, -8, 4011006, -2, 4000030, -50, 4003000, -50, 1082089, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your inventory is full or not." );
		else self.say( "A perfect item, as usual. Come and see me if you need anything else." );
	}
}

function chat_message2(index, makeItem, needItem, reqLevel ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To improve #b" + makeItem + "#k, I'm going to need the following materials. The Equipment Level Requirement: #d" + reqLevel + "#k and please make sure that you will not use an item that is being upgrade as material. What do you think? Do you want a #r" + makeItem + "#k?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "Oh, right? Do not have the materials? Please come back later. Looks like I'm staying here for a while." );
	else {
		// Upgrading a glove
		if ( index == 1 ) ret = self.InventoryExchange( -5000, 1082013, -1, 4021000, -1, 1082014, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -7000, 1082013, -1, 4021003, -2, 1082015, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -10000, 1082016, -1, 4021000, -3, 1082017, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -12000, 1082016, -1, 4021008, -1, 1082018, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -15000, 1082048, -1, 4021003, -3, 1082049, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -20000, 1082048, -1, 4021008, -1, 1082050, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -22000, 1082068, -1, 4011002, -4, 1082069, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -25000, 1082068, -1, 4011006, -2, 1082070, 1 );
		else if ( index == 9 ) ret = self.InventoryExchange( -30000, 1082071, -1, 4011006, -4, 1082072, 1 );
		else if ( index == 10 ) ret = self.InventoryExchange( -40000, 1082071, -1, 4021008, -2, 1082073, 1 );
		else if ( index == 11 ) ret = self.InventoryExchange( -55000, 1082084, -1, 4021000, -5, 4011000, -1, 1082085, 1 );
		else if ( index == 12 ) ret = self.InventoryExchange( -60000, 1082084, -1, 4021008, -2, 4011006, -2, 1082083, 1 );
		else if ( index == 13 ) ret = self.InventoryExchange( -70000, 1082089, -1, 4021007, -1, 4021000, -5, 1082090, 1 );
		else if ( index == 14 ) ret = self.InventoryExchange( -80000, 1082089, -1, 4021007, -2, 4021008, -2, 1082091, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your inventory is full or not." );
		else self.say( "A perfect item, as usual. Come and see me if you need anything else." );
	}
}

function chat_message3(index, makeItem, needItem, needNumber, itemNumber ) {
	inventory = target.inventory;

	nRetNum = self.askNumber( "With #b" + needNumber + " " + needItem + "#k, I can create" + itemNumber + " " + makeItem + "(s). Bring me the supplies and I can do it for you. Now... how many would you like me to do?", 0, 0, 100 );
	nNeedNum = nRetNum * needNumber;
	nAllNum = nRetNum * itemNumber;
	nRetBuy = self.askYesNo( "Do you want make #b" + makeItem + "#k " + nRetNum + " times? I need #r" + nNeedNum + " " + needItem + "#k." );
	if ( nRetBuy == 0 ) self.say( "Oh, right? Do not have the materials? Please come back later. It seems like I'm going to stay here for a while" );
	else {
		if ( index == 1 ) ret = self.InventoryExchange( 0, 4000003, -nNeedNum, 4003001, nAllNum );
		else if ( index == 2 ) ret = self.InventoryExchange( 0, 4000018, -nNeedNum, 4003001, nAllNum );
		else if ( index == 3 ) ret = self.InventoryExchange( 0, 4011001, -nNeedNum, 4011000, -nNeedNum, 4003000, nAllNum );
		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your inventory is full or not." );
		else self.say( "Hey! Here, take the #r" + nAllNum + " " + makeItem + "(s)#k. It gave me a bit of extra work, but... it turned out to be a wonderful thing. Please come and talk to me if you need anything later. Until this day comes and see you soon..." );
	}
}

function chat_message6(index, makeItem, needItem, unitNum, itemOption ) {
	inventory = target.inventory;

	if ( itemOption == "" ) nRet = self.askYesNo( "To make #b" + unitNum + " "+ makeItem + "#k(s), I need the following items. If you have the materials, it will be much better for you to create the item here than to buy it from the store. Do you really want it?\r\n\r\n#b" + needItem );
	else nRet = self.askYesNo( "To make #b" + unitNum + " "+ makeItem + "#k(s), I need the following items. As a matter of course, this arrow also has an enhancement of #b" + itemOption + "#k in her. This is a special type of arrow, so if you have the necessary materials, why not create some? Do you really want me to?\r\n\r\n#b" + needItem );

	if ( nRet == 0 ) self.say( "I make important items for the archers and see them for a reasonable price, so feel free to see them all. You know nothing of it, right?" );
	else {
		// Making an arrow
		if ( index == 1 ) ret = self.InventoryExchange( 0, 4003001, -1, 4003004, -1, 2060000, 1000 );
		else if ( index == 2 ) ret = self.InventoryExchange( 0, 4003001, -1, 4003004, -1, 2061000, 1000 );
		else if ( index == 3 ) ret = self.InventoryExchange( 0, 4011000, -1, 4003001, -3, 4003004, -10, 2060001, 900 );
		else if ( index == 4 ) ret = self.InventoryExchange( 0, 4011000, -1, 4003001, -3, 4003004, -10, 2061001, 900 );
		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your inventory is full or not." );
		else self.say( "Hey! Here, take the #r" + unitNum + " " + makeItem + "#k. It gave me a bit of extra work, but... it turned out to be a wonderful thing. Please come and talk to me if you need anything later. Until this day comes and see you soon..." );
	}
}
'''
