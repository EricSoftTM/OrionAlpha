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

'NPC: JM From the Streetz'
'Script: Kerning\'s Item Creator'

self.say("#bScript: refine_kerning#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

nRet1 = self.askYesNo( "Do you have to have a Mineral or a #t4000021#? With a fee, I can make a perfect thief equipment for you. Ah yes! Do not tell anyone about my business here in this town. So, do you really want to?" );
if ( nRet1 == 0 ) self.say( "No? You will not regret it a bit if you let me do this... if you change your mind, you just have to look for me and we'll do business..." );
else {
  v1 = self.askMenu( "Well, the rate will be reasonable, no need to worry. What do you want to do?\r\n#b#L0#Create a Claw#l\r\n#L1#Create a Glove#l\r\n#L2#Upgrade a Claw#l\r\n#L3#Upgrade a Glove#l\r\n#L4#Craft Materials#l" );;
  if ( v1 == 0 ) {
    v2 = self.askMenu( "The claw is the glove you put on to use shurikens. But it is useless to the thieves that use daggers. Now, what kind of claw do you want me to create?\r\n#L0##b #t1472001##k(Thief Lv. 15)#l\r\n#L1##b #t1472004##k(Thief Lv. 20)#l\r\n#L2##b #t1472008##k(Thief Lv. 30)#l\r\n#L3##b #t1472011##k(Thief Lv. 35)#l\r\n#L4##b #t1472014##k(Thief Lv. 40)#l\r\n#L5##b #t1472018##k(Thief Lv. 50)#l" );
    if ( v2 == 0 ) chat_message6( 1, "#t1472001#", "#v4011001# #t4011001# \r\n#v4000021# 20 #t4000021#s \r\n#v4003000# 5 #t4003000#s \r\n#v4031138# 2000 mesos", 15 );
    else if ( v2 == 1 ) chat_message6( 2, "#t1472004#", "#v4011000# 2 #t4011000#s \r\n#v4011001# #t4011001# \r\n#v4000021# 30 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 3000 mesos", 20 );
    else if ( v2 == 2 ) chat_message6( 3, "#t1472008#", "#v4011000# 3 #t4011000#s \r\n#v4011001# 2 #t4011001#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 15000 mesos", 30 );
    else if ( v2 == 3 ) chat_message6( 4, "#t1472011#", "#v4011000# 4 #t4011000#s \r\n#v4011001# 3 #t4011001#s \r\n#v4000021# 80#t4000021#s \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 30000 mesos", 35 );
    else if ( v2 == 4 ) chat_message6( 5, "#t1472014#", "#v4011000# 3 #t4011000#s \r\n#v4011001# 4 #t4011001#s \r\n#v4000021# 100 #t4000021#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 40000 mesos", 40 );
    else if ( v2 == 5 ) chat_message6( 6, "#t1472018#", "#v4011000# 4 #t4011000#s \r\n#v4011001# 5 #t4011001#s \r\n#v4000030# 40 #t4000030#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50000 mesos", 50 );
  }
  else if ( v1 == 1 ) {
    v2 = self.askMenu( "Now... what kind of glove do you want me to create?\r\n#L0##b#t1082002##k(Thief Lv. 10)#l\r\n#L1##b#t1082029##k(Thief Lv. 15)#l\r\n#L2##b#t1082030##k(Thief Lv. 15)#l\r\n#L3##b#t1082031##k(Thief Lv. 15)#l\r\n#L4##b#t1082032##k(Thief Lv. 20)#l\r\n#L5##b#t1082037##k(Thief Lv. 25)#l\r\n#L6##b#t1082042##k(Thief Lv. 30)#l\r\n#L7##b#t1082046##k(Thief Lv. 35)#l\r\n#L8##b#t1082075##k(Thief Lv. 40)#l\r\n#L9##b#t1082065##k(Thief Lv. 50)#l\r\n#L10##b#t1082092##k(Thief Lv. 60)#l" );
    if ( v2 == 0 ) chat_message6( 100, "#t1082002#", "#v4000021# 15 #t4000021#s \r\n#v4031138# 1.000 mesos", 10 );
    else if ( v2 == 1 ) chat_message6( 101, "#t1082029#", "#v4000021# 30 #t4000021#s \r\n#v4000018# 20 #t4000018#s \r\n#v4031138# 7.000 mesos", 15 );
    else if ( v2 == 2 ) chat_message6( 102, "#t1082030#", "#v4000021# 30 #t4000021#s \r\n#v4000015# 20 #t4000015#s \r\n#v4031138# 7.000 mesos", 15 );
    else if ( v2 == 3 ) chat_message6( 103, "#t1082031#", "#v4000021# 30 #t4000021#s \r\n#v4000020# 20 #t4000020#s \r\n#v4031138# 7.000 mesos", 15 );
    else if ( v2 == 4 ) chat_message6( 104, "#t1082032#", "#v4011000# 2 #t4011000#s \r\n#v4000021# 40 #t4000021#s \r\n#v4031138# 10.000 mesos", 20 );
    else if ( v2 == 5 ) chat_message6( 105, "#t1082037#", "#v4011000# 2 #t4011000#s \r\n#v4011001# #t4011001# \r\n#v4000021# 10 #t4000021#s \r\n#v4031138# 15.000 mesos", 25 );
    else if ( v2 == 6 ) chat_message6( 106, "#t1082042#", "#v4011001# 2 #t4011001#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 25.000 mesos", 30 );
    else if ( v2 == 7 ) chat_message6( 107, "#t1082046#", "#v4011001# 3 #t4011001#s \r\n#v4011000# #t4011000# \r\n#v4000021# 60 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 30.000 mesos", 35 );
    else if ( v2 == 8 ) chat_message6( 108, "#t1082075#", "#v4021000# 3 #t4021000#s \r\n#v4000014# 200 #t4000014#s \r\n#v4000021# 80 #t4000021#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 40.000 mesos", 40 );
    else if ( v2 == 9 ) chat_message6( 109, "#t1082065#", "#v4021005# 3 #t4021005#s \r\n#v4021008# #t4021008# \r\n#v4000030# 40 #t4000030#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 50.000 mesos", 50 );
    else if ( v2 == 10 ) chat_message6( 110, "#t1082092#", "#v4011007# #t4011007# \r\n#v4011000# 8 #t4011000#s \r\n#v4021007# #t4021007# \r\n#v4000030# 50 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 70.000 mesos", 60 );
  }
  else if ( v1 == 2 ) {
    self.sayNext( "Do you want to improve a claw? Okay then, but I'll give you some advice.: All items that will be used for perfection will #bdisappear#k. Take this into consideration when you make the decision, okay?" );
    v2 = self.askMenu( "The claw is the equipment that thieves uses to use shurikens. But it is useless to the thieves that use daggers. Well, what kind of Claw do you want to create? \r\n#L0##b#t1472002##k(Thief Lv. 15)#l\r\n#L1##b #t1472003##k(Thief Lv. 15)#l\r\n#L2##b #t1472005##k(Thief Lv. 20)#k#l\r\n#L3##b #t1472006##k(Thief Lv. 20)#l\r\n#L4##b #t1472007##k(Thief Lv. 25)#l\r\n#L5##b #t1472009##k(Thief Lv. 30)#l\r\n#L6##b #t1472010##k(Thief Lv. 30)#l\r\n#L7##b #t1472012##k(Thief Lv. 35)#l\r\n#L8##b #t1472013##k(Thief Lv. 35)#l\r\n#L9##b #t1472015##k(Thief Lv. 40)#l\r\n#L10##b #t1472016##k(Thief Lv. 40)#l\r\n#L11##b #t1472017##k(Thief Lv. 40)#l\r\n#L12##b #t1472019##k(Thief Lv. 50)#l\r\n#L13##b #t4021005# Remove#k(Thief Lv. 50)#l" );
    if ( v2 == 0 ) chat_message7( 1, "#t1472002#", "#v1472001# #t1472001# \r\n#v4011002# #t4011002# \r\n#v4031138# 1.000 mesos" );
    else if ( v2 == 1 ) chat_message7( 2, "#t1472003#", "#v1472001# #t1472001# \r\n#v4011006# #t4011006# \r\n#v4031138# 2.000 mesos" );
    else if ( v2 == 2 ) chat_message7( 3, "#t1472005#", "#v1472004# #t1472004# \r\n#v4011001# 2 #t4011001#s \r\n#v4031138# 3.000 mesos" );
    else if ( v2 == 3 ) chat_message7( 4, "#t1472006#", "#v1472004# #t1472004# \r\n#v4011003# 2 #t4011003#s \r\n#v4031138# 5.000 mesos" );
    else if ( v2 == 4 ) chat_message7( 5, "#t1472007#", "#v1472000# #t1472000# \r\n#v4011001# 3 #t4011001#s \r\n#v4000021# 20 #t4000021#s \r\n#v4003001# 30 #t4003001#s \r\n#v4031138# 5.000 mesos" );
    else if ( v2 == 5 ) chat_message7( 6, "#t1472009#", "#v1472008# #t1472008# \r\n#v4011002# 3 #t4011002#s \r\n#v4031138# 10.000 mesos" );
    else if ( v2 == 6 ) chat_message7( 7, "#t1472010#", "#v1472008# #t1472008# \r\n#v4011003# 3 #t4011003#s \r\n#v4031138# 15.000 mesos" );
    else if ( v2 == 7 ) chat_message7( 8, "#t1472012#", "#v1472011# #t1472011# \r\n#v4011004# 4 #t4011004#s \r\n#v4031138# 20.000 mesos" );
    else if ( v2 == 8 ) chat_message7( 9, "#t1472013#", "#v1472011# #t1472011# \r\n#v4021008# #t4021008# \r\n#v4031138# 25.000 mesos" );
    else if ( v2 == 9 ) chat_message8( 1, "#t1472015#", "#v1472014# #t1472014# \r\n#v4021000# 5 #t4021000#s \r\n#v4031138# 30.000 mesos", "DEX +2" );
    else if ( v2 == 10 ) chat_message8( 2, "#t1472016#", "#v1472014# #t1472014# \r\n#v4011003# 5 #t4011003#s \r\n#v4031138# 30.000 mesos", "LUK +2" );
    else if ( v2 == 11 ) chat_message8( 3, "#t1472017#", "#v1472014# #t1472014# \r\n#v4021008# 2 #t4021008#s \r\n#v4031138# 35.000 mesos", "LUK +3" );
    else if ( v2 == 12 ) chat_message8( 4, "#t1472019#", "#v1472018# #t1472018# \r\n#v4021000# 6 #t4021000#s \r\n#v4031138# 40.000 mesos", "DEX +3" );
    else if ( v2 == 13 ) chat_message8( 5, "#t1472020#", "#v1472018# #t1472018# \r\n#v4021005# 6 #t4021005#s \r\n#v4031138# 40.000 mesos", "LUK +3" );
  }
  else if ( v1 == 3 ) {
    self.sayNext( "Do you want to perfect a glove? Okay then, but I'll give you some advice.: All items that will be used for perfection will #bdisappear#k. Take this into consideration when you make the decision, okay?" );
    v2 = self.askMenu( "So... what kind of glove do you want to perfect? \r\n#L0##b #t1082033##k(Thief Lv. 20)#l\r\n#L1##b #t1082034##k(Thief Lv. 20)#l\r\n#L2##b #t1082038##k(Thief Lv. 25)#l\r\n#L3##b #t1082039##k(Thief Lv. 25)#l\r\n#L4##b #t1082043##k(Thief Lv. 30)#l\r\n#L5##b #t1082044##k(Thief Lv. 30)#l\r\n#L6##b #t1082047##k(Thief Lv. 35)#l\r\n#L7##b #t1082045##k(Thief Lv. 35)#l\r\n#L8##b #t1082076##k(Thief Lv. 40)#l\r\n#L9##b #t1082074##k(Thief Lv. 40)#l\r\n#L10##b #t1082067##k(Thief Lv. 50)#l\r\n#L11##b #t1082066##k(Thief Lv. 50)#l\r\n#L12##b #t1082093##k(Thief Lv. 60)#l\r\n#L13##b #t1082094##k(Thief Lv. 60)#l" );
    if ( v2 == 0 ) chat_message7( 100, "#t1082033#", "#v1082032# #t1082032# \r\n#v4011002# #t4011002# \r\n#v4031138# 5.000 mesos" );
    else if ( v2 == 1 ) chat_message7( 101, "#t1082034#", "#v1082032# #t1082032# \r\n#v4021004# #t4021004# \r\n#v4031138# 7.000 mesos" );
    else if ( v2 == 2 ) chat_message7( 102, "#t1082038#", "#v1082037# #t1082037# \r\n#v4011002# 2 #t4011002#s \r\n#v4031138# 10.000 mesos" );
    else if ( v2 == 3 ) chat_message7( 103, "#t1082039#", "#v1082037# #t1082037# \r\n#v4021004# 2 #t4021004#s \r\n#v4031138# 12.000 mesos" );
    else if ( v2 == 4 ) chat_message7( 104, "#t1082043#", "#v1082042# #t1082042# \r\n#v4011004# 2 #t4011004#s \r\n#v4031138# 15.000 mesos" );
    else if ( v2 == 5 ) chat_message7( 105, "#t1082044#", "#v1082042# #t1082042# \r\n#v4011006# #t4011006# \r\n#v4031138# 20.000 mesos" );
    else if ( v2 == 6 ) chat_message7( 106, "#t1082047#", "#v1082046# #t1082046# \r\n#v4011005# 3 #t4011005#s \r\n#v4031138# 22.000 mesos" );
    else if ( v2 == 7 ) chat_message7( 107, "#t1082045#", "#v1082046# #t1082046# \r\n#v4011006# 2 #t4011006#s \r\n#v4031138# 25.000 mesos" );
    else if ( v2 == 8 ) chat_message7( 108, "#t1082076#", "#v1082075# #t1082075# \r\n#v4011006# 4 #t4011006#s \r\n#v4031138# 45.000 mesos" );
    else if ( v2 == 9 ) chat_message7( 109, "#t1082074#", "#v1082075# #t1082075# \r\n#v4021008# 2 #t4021008#s \r\n#v4031138# 50.000 mesos" );
    else if ( v2 == 10 ) chat_message7( 110, "#t1082067#", "#v1082065# #t1082065# \r\n#v4021000# 5 #t4021000#s \r\n#v4031138# 55.000 mesos" );
    else if ( v2 == 11 ) chat_message7( 111, "#t1082066#", "#v1082065# #t1082065# \r\n#v4011006# 2 #t4011006#s \r\n#v4021008# #t4021008# \r\n#v4031138# 60.000 mesos" );
    else if ( v2 == 12 ) chat_message7( 112, "#t1082093#", "#v1082092# #t1082092# \r\n#v4011001# 7 #t4011001#s \r\n#v4000014# 200 #t4000014#s \r\n#v4031138# 70.000 mesos" );
    else if ( v2 == 13 ) chat_message7( 113, "#t1082094#", "#v1082092# #t1082092# \r\n#v4011006# 7 #t4011006#s \r\n#v4000027# 150 #t4000027#s \r\n#v4031138# 80.000 mesos" );
  }
  else if ( v1 == 4 ) {
    v2 = self.askMenu( "Did you say you want to create a material? Very well... of what kind?\r\n#L0##bCreate #t4003001# with #t4000003##k#l\r\n#L1##bCreate #t4003001# with #t4000018##k#l\r\n#L2##bCreate #t4003000# #k#l" );
    if ( v2 == 0 ) chat_message9( 1, "#t4003001#(s)", "#t4000003#es", 10, 1 );
    else if ( v2 == 1 ) chat_message9( 2, "#t4003001#(s)", "#t4000018#s", 5, 1 );
    else if ( v2 == 2 ) chat_message9( 3, "#t4003000#s", "#t4011001# and #t4011000#", 1, 15 );
  }
}

function chat_message6(index, makeItem, needItem, reqLevel ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To make a #b" + makeItem + "#k, I'm going to need the following materials. The Equipment Level Requirement: #d" + reqLevel + "#k. What do you think? Do you want me to do it?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "Oh, right? You are lacking in some materials, Since you're going to spin the city yourself, go look for some of the materials and bring them to me... I'll make a wonderful item for you." );
	else {
		// Making a claw
		if ( index == 1 ) ret = self.InventoryExchange( -2000, 4011001, -1, 4000021, -20, 4003000, -5, 1472001, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -3000, 4011000, -2, 4011001, -1, 4000021, -30, 4003000, -10, 1472004, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -15000, 4011000, -3, 4011001, -2, 4000021, -50, 4003000, -20, 1472008, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -30000, 4011000, -4, 4011001, -3, 4000021, -80, 4003000, -25, 1472011, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -40000, 4011000, -3, 4011001, -4, 4000021, -100, 4003000, -30, 1472014, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -50000, 4011000, -4, 4011001, -5, 4000030, -40, 4003000, -35, 1472018, 1 );
		// Making a glove
		else if ( index == 100 ) ret = self.InventoryExchange( -1000, 4000021, -15, 1082002, 1 );
		else if ( index == 101 ) ret = self.InventoryExchange( -7000, 4000021, -30, 4000018, -20, 1082029, 1 );
		else if ( index == 102 ) ret = self.InventoryExchange( -7000, 4000021, -30, 4000015, -20, 1082030, 1 );
		else if ( index == 103 ) ret = self.InventoryExchange( -7000, 4000021, -30, 4000020, -20, 1082031, 1 );
		else if ( index == 104 ) ret = self.InventoryExchange( -10000, 4011000, -2, 4000021, -40, 1082032, 1 );
		else if ( index == 105 ) ret = self.InventoryExchange( -15000, 4011000, -2, 4011001, -1, 4000021, -10, 1082037, 1 );
		else if ( index == 106 ) ret = self.InventoryExchange( -25000, 4011001, -2, 4000021, -50, 4003000, -10, 1082042, 1 );
		else if ( index == 107 ) ret = self.InventoryExchange( -30000, 4011001, -3, 4011000, -1, 4000021, -60, 4003000, -15, 1082046, 1 );
		else if ( index == 108 ) ret = self.InventoryExchange( -40000, 4000021, -80, 4021000, -3, 4000014, -200, 4003000, -30, 1082075, 1 );
		else if ( index == 109 ) ret = self.InventoryExchange( -50000, 4021005, -3, 4021008, -1, 4000030, -40, 4003000, -30, 1082065, 1 );
		else if ( index == 110 ) ret = self.InventoryExchange( -70000, 4011007, -1, 4011000, -8, 4021007, -1, 4000030, -50, 4003000, -50, 1082092, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have everything you need and if there is a free space in your inventory." );
		else self.say( "Here! Take the #r" + makeItem + "#k. What do you think? Is not it very cool? No, every time you see such a beauty!" );
	}
}

function chat_message7( index, makeItem, needItem ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To upgrade #b" + makeItem + "#k, I'm going to need the following materials. Oh, and please make sure not to use an already upgraded item as material. What do you think? Do you want me to do it?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "Oh, right? You are lacking in some materials, Since you're going to spin the city yourself, go look for some of the materials and bring them to me... I'll make a wonderful item for you." );
	else {
		// Upgrading a claw
		if ( index == 1 ) ret = self.InventoryExchange( -1000, 1472001, -1, 4011002, -1, 1472002, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -2000 , 1472001, -1, 4011006, -1, 1472003, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -3000 , 1472004, -1, 4011001, -2, 1472005, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -5000 , 1472004, -1, 4011003, -2, 1472006, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -5000 , 1472000, -1, 4011001, -3, 4000021, -20, 4003001, -30, 1472007, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -10000 , 1472008, -1, 4011002, -3, 1472009, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -15000 , 1472008, -1, 4011003, -3, 1472010, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -20000 , 1472011, -1, 4011004, -4, 1472012, 1 );
		else if ( index == 9 ) ret = self.InventoryExchange( -25000 , 1472011, -1, 4021008, -1, 1472013, 1 );
		// Upgrading a glove
		else if ( index == 100 ) ret = self.InventoryExchange( -5000, 1082032, -1, 4011002, -1, 1082033, 1 );
		else if ( index == 101 ) ret = self.InventoryExchange( -7000, 1082032, -1, 4021004, -1, 1082034, 1 );
		else if ( index == 102 ) ret = self.InventoryExchange( -10000, 1082037, -1, 4011002, -2, 1082038, 1 );
		else if ( index == 103 ) ret = self.InventoryExchange( -12000, 1082037, -1, 4021004, -2, 1082039, 1 );
		else if ( index == 104 ) ret = self.InventoryExchange( -15000, 1082042, -1, 4011004, -2, 1082043, 1 );
		else if ( index == 105 ) ret = self.InventoryExchange( -20000, 1082042, -1, 4011006, -1, 1082044, 1 );
		else if ( index == 106 ) ret = self.InventoryExchange( -22000, 1082046, -1, 4011005, -3, 1082047, 1 );
		else if ( index == 107 ) ret = self.InventoryExchange( -25000, 1082046, -1, 4011006, -2, 1082045, 1 );
		else if ( index == 108 ) ret = self.InventoryExchange( -45000, 1082075, -1, 4011006, -4, 1082076, 1 );
		else if ( index == 109 ) ret = self.InventoryExchange( -50000, 1082075, -1, 4021008, -2, 1082074, 1 );
		else if ( index == 110 ) ret = self.InventoryExchange( -55000, 1082065, -1, 4021000, -5, 1082067, 1 );
		else if ( index == 111 ) ret = self.InventoryExchange( -60000, 1082065, -1, 4011006, -2, 4021008, -1, 1082066, 1 );
		else if ( index == 112 ) ret = self.InventoryExchange( -70000, 1082092, -1, 4011001, -7, 4000014, -200, 1082093, 1 );
		else if ( index == 113 ) ret = self.InventoryExchange( -80000, 1082092, -1, 4011006, -7, 4000027, -150, 1082094, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have everything you need and if there is a free space in your inventory." );
		else self.say( "On here! Take the #r" + makeItem + "#k. What do you think? Is not it very cool? No, every time you see such a beauty!" );
	}
}

function chat_message8(index, makeItem, needItem, itemOption ) {
	inventory = target.inventory;

	nRet = self.askYesNo( "To upgrade #b" + makeItem + "#k, I'm going to need the following materials. There is also an enhancement of #r" + itemOption + "#k with the item. Oh, and please make sure not to use an already upgraded item as material. What do you think? Do you want me to do it?\r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "Oh, right? You're short of some materials, right? Since you will stay in the same city, go look for some of the materials and bring them to me... I'll make a wonderful item for you..." );
	else {
		if ( index == 1 ) ret = self.InventoryExchange( -30000 , 1472014, -1, 4021000, -5, 1472015, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -30000 , 1472014, -1, 4011003, -5, 1472016, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -35000 , 1472014, -1, 4021008, -2, 1472017, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -40000 , 1472018, -1, 4021000, -6, 1472019, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -40000 , 1472018, -1, 4021005, -6, 1472020, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have everything you need and if there is a free space in your inventory." );
		else self.say( "On here! Take the #r" + makeItem + "#k. What do you think? Is not it very cool? No, every time you see such a beauty!" );
	}
}

function chat_message9(index, makeItem, needItem, needNumber, itemNumber ) {
	inventory = target.inventory;

	nRetNum = self.askNumber( "With #b" + needNumber + " " + needItem + "#k, I can create " + itemNumber + " " + makeItem + ". Be happy, because this one is going to leave of grace. What do you think? How many would you like to create?", 0, 0, 100 );
	nNeedNum = nRetNum * needNumber;
	nAllNum = nRetNum * itemNumber;
	nRetBuy = self.askYesNo( "You want to do #b" + makeItem + " " + nRetNum + " times#k? I'll need #r" + nNeedNum + " " + needItem + "#k to do it.");
	if ( nRetBuy == 0 ) self.say( "It looks like you do not have all the items you need for this. Since you're going to stay in the city yourself, go find some of the materials and bring them to me... I'll make a wonderful item for you..." );
	else {
		if ( index == 1 ) ret = self.InventoryExchange( 0, 4000003, -nNeedNum, 4003001, nAllNum );
		else if ( index == 2 ) ret = self.InventoryExchange( 0, 4000018, -nNeedNum, 4003001, nAllNum );
		else if ( index == 3 ) ret = self.InventoryExchange( 0, 4011001, -nNeedNum, 4011000, -nNeedNum, 4003000, nAllNum );
		if ( ret == 0 ) self.say(  "Please check carefully if you have everything you need and if there is a free space in your inventory." );
		else self.say( "Here! Take it #r" + nAllNum + " " + makeItem + "#k. What do you think? Is not it very cool? No, every time you see such a beauty!" );
	}
}
'''
