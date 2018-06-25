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

'NPC: Chrishrama'
'Script: Sleepywood\'s Item Creator'

self.say("#bScript: refine_sleepy#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

let v1 = self.askMenu( "Hello, I live here, but don't underestimate me. How about I help you by making you a new pair of shoes?\r\n#L0##bRefine a mineral#k\r\n#L1##bRefine a jewel#k\r\n#L2##bCreate warrior shoes#k\r\n#L3##bCreate magician shoes#k\r\n#L4##bCreate archer shoes#k\r\n#L5##bCreate thief shoes#k");
if ( v1 == 0 ) {
	v2 = self.askMenu( "Which of these minerals would you like to create?\r\n#b#L0# #t4011000##l\r\n#L1# #t4011001##l\r\n#L2# #t4011002##l\r\n#L3# #t4011003##l\r\n#L4# #t4011004##l\r\n#L5# #t4011005##l\r\n#L6# #t4011006##l" );
	if ( v2 == 0 ) chat_message6( 1, "#t4011000#", "#v4010000#", "#t4010000#s", 300 );
	else if ( v2 == 1 ) chat_message6( 2, "#t4011001#", "#v4010001#", "#t4010001#s", 300 );
	else if ( v2 == 2 ) chat_message6( 3, "#t4011002#", "#v4010002#", "#t4010002#s", 300 );
	else if ( v2 == 3 ) chat_message6( 4, "#t4011003#", "#v4010003#", "#t4010003#s", 500 );
	else if ( v2 == 4 ) chat_message6( 5, "#t4011004#", "#v4010004#", "#t4010004#s", 500 );
	else if ( v2 == 5 ) chat_message6( 6, "#t4011005#", "#v4010005#", "#t4010005#s", 500 );
	else if ( v2 == 6 ) chat_message6( 7, "#t4011006#", "#v4010006#", "#t4010006#s", 800 );
}
else if ( v1 == 1 ) {
	v2 = self.askMenu( "What jewel would you like to refine?\r\n#b#L0# #t4021000##l\r\n#L1# #t4021001##l\r\n#L2# #t4021002##l\r\n#L3# #t4021003##l\r\n#L4# #t4021004##l\r\n#L5# #t4021005##l\r\n#L6# #t4021006##l\r\n#L7# #t4021007##l\r\n#L8# #t4021008##l" );
	if ( v2 == 0 ) chat_message6( 100, "#t4021000#", "#v4020000#", "#t4020000#s", 500 );
	else if ( v2 == 1 ) chat_message6( 101, "#t4021001#", "#v4020001#", "#t4020001#s", 500 );
	else if ( v2 == 2 ) chat_message6( 102, "#t4021002#", "#v4020002#", "#t4020002#s", 500 );
	else if ( v2 == 3 ) chat_message6( 103, "#t4021003#", "#v4020003#", "#t4020003#s", 500 );
	else if ( v2 == 4 ) chat_message6( 104, "#t4021004#", "#v4020004#", "#t4020004#s", 500 );
	else if ( v2 == 5 ) chat_message6( 105, "#t4021005#", "#v4020005#", "#t4020005#s", 500 );
	else if ( v2 == 6 ) chat_message6( 106, "#t4021006#", "#v4020006#", "#t4020006#s", 500 );
	else if ( v2 == 7 ) chat_message6( 107, "#t4021007#", "#v4020007#", "#t4020007#s", 1000 );
	else if ( v2 == 8 ) chat_message6( 108, "#t4021008#", "#v4020008#", "#t4020008#s", 3000 );
}
else if ( v1 == 2 ) {
	v2 = self.askMenu( "So you want to make warrior shoes. What kind of shoes do you want to create?\r\n#L0##b #t1072051##k(Warrior Lv. 25)#l\r\n#L1##b #t1072053##k(Warrior Lv. 25)#l\r\n#L2##b #t1072052##k(Warrior Lv. 25)#l\r\n#L3##b #t1072003##k(Warrior Lv. 30)#l\r\n#L4##b #t1072039##k(Warrior Lv. 30)#l\r\n#L5##b #t1072040##k(Warrior Lv. 30)#l\r\n#L6##b #t1072041##k(Warrior Lv. 30)#l\r\n#L7##b #t1072002##k(Warrior Lv. 35)#l\r\n#L8##b #t1072112##k(Warrior Lv. 35)#l\r\n#L9##b #t1072113##k(Warrior Lv. 35)#l\r\n#L10##b #t1072000##k(Warrior Lv. 40)#l\r\n#L11##b #t1072126##k(Warrior Lv. 40)#l\r\n#L12##b #t1072127##k(Warrior Lv. 40)#l\r\n#L13##b #t1072132##k(Warrior Lv. 50)#l\r\n#L14##b #t1072133##k(Warrior Lv. 50)#l\r\n#L15##b #t1072134##k(Warrior Lv. 50)#l\r\n#L16##b #t1072135##k(Warrior Lv. 50)#l\r\n#L17##b #t1072147##k(Warrior Lv. 60)#l\r\n#L18##b #t1072148##k(Warrior Lv. 60, warrior)#l\r\n#L19##b #t1072149##k(Warrior Lv. 60, warrior)#l");
	if ( v2 == 0 ) chat_message5( 1, "#t1072051#", "#v4011004# 2 #t4011004#s \r\n#v4011001# #t4011001# \r\n#v4000021# 15 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 10.000 mesos", 25, "DEX +1" );
	else if ( v2 == 1 ) chat_message5( 2, "#t1072053#", "#v4011006# 2 #t4011006#s \r\n#v4011001# #t4011001# \r\n#v4000021# 15 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 10.000 mesos", 25, "DEX +1" );
	else if ( v2 == 2 ) chat_message5( 3, "#t1072052#", "#v4021008# #t4021008# \r\n#v4011001# 2 #t4011001#s \r\n#v4000021# 20 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 12.000 mesos", 25, "STR +2" );
	else if ( v2 == 3 ) chat_message5( 4, "#t1072003#", "#v4021003# 4 #t4021003#s \r\n#v4011001# 2 #t4011001#s \r\n#v4000021# 45 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 20.000 mesos", 30, "STR +1" );
	else if ( v2 == 4 ) chat_message5( 5, "#t1072039#", "#v4011002# 4 #t4011002#s \r\n#v4011001# 2 #t4011001#s \r\n#v4000021# 45 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 20.000 mesos", 30, "DEX +1" );
	else if ( v2 == 5 ) chat_message5( 6, "#t1072040#", "#v4011004# 4 #t4011004#s \r\n#v4011001# 2 #t4011001#s \r\n#v4000021# 45 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 20.000 mesos", 30, "STR +1" );
	else if ( v2 == 6 ) chat_message5( 7, "#t1072041#", "#v4021000# 4 #t4021000#s \r\n#v4011001# 2 #t4011001#s \r\n#v4000021# 45 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 20.000 mesos", 30, "DEX +1" );
	else if ( v2 == 7 ) chat_message5( 8, "#t1072002#", "#v4011001# 3 #t4011001#s \r\n#v4021004# #t4021004# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 20 #t4000030#s \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 22.000 mesos", 35, "STR +1, MP +10" );
	else if ( v2 == 8 ) chat_message5( 9, "#t1072112#", "#v4011002# 3 #t4011002#s \r\n#v4021004# #t4021004# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 20 #t4000030#s \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 22.000 mesos", 35, "DEX +1, MP +10" );
	else if ( v2 == 9 ) chat_message5( 10, "#t1072113#", "#v4021008# 2 #t4021008#s \r\n#v4021004# #t4021004# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 20 #t4000030#s \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 25.000 mesos", 35, "STR +1, DEX +1, MP +10" );
	else if ( v2 == 10 ) chat_message5( 11, "#t1072000#", "#v4011003# 4 #t4011003#s \r\n#v4000021# 100 #t4000021#s \r\n#v4000030# 40 #t4000030#s \r\n#v4003000# 30 #t4003000#s \r\n#v4000033# 100 #t4000033#s \r\n#v4031138# 38.000 mesos", 40, "DEX +2, MP +20" );
	else if ( v2 == 11 ) chat_message5( 12, "#t1072126#", "#v4011005# 4 #t4011005#s \r\n#v4021007# #t4021007# \r\n#v4000030# 40 #t4000030#s \r\n#v4003000# 30 #t4003000#s \r\n#v4000042# 250 #t4000042#s \r\n#v4031138# 38.000 mesos", 40, "STR +1, DEX +1, MP +20" );
	else if ( v2 == 12 ) chat_message5( 13, "#t1072127#", "#v4011002# 4 #t4011002#s \r\n#v4021007# #t4021007# \r\n#v4000030# 40 #t4000030#s \r\n#v4003000# 30 #t4003000#s \r\n#v4000041# 120 #t4000041#s \r\n#v4031138# 38.000 mesos", 40, "STR +2, MP +20" );
	else if ( v2 == 13 ) chat_message5( 14, "#t1072132#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4021003# 6 #t4021003#s \r\n#v4000030# 65 #t4000030#s \r\n#v4003000# 45 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "DEX +3" );
	else if ( v2 == 14 ) chat_message5( 15, "#t1072133#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4011002# 6 #t4011002#s \r\n#v4000030# 65 #t4000030#s \r\n#v4003000# 45 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "DEX + 2, STR + 1" );
	else if ( v2 == 15 ) chat_message5( 16, "#t1072134#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4011005# 6 #t4011005#s \r\n#v4000030# 65 #t4000030#s \r\n#v4003000# 45 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "DEX + 1, STR + 2" );
	else if ( v2 == 16 ) chat_message5( 17, "#t1072135#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4011006# 6 #t4011006#s \r\n#v4000030# 65 #t4000030#s \r\n#v4003000# 45 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "STR +3" );
	else if ( v2 == 17 ) chat_message5( 18, "#t1072147#", "#v4021008# #t4021008# \r\n#v4011007# #t4011007# \r\n#v4021005# 8 #t4021005#s \r\n#v4000030# 80 #t4000030#s \r\n#v4003000# 55 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "STR +1, DEX +3" );
	else if ( v2 == 18 ) chat_message5( 19, "#t1072148#", "#v4021008# #t4021008# \r\n#v4011007# #t4011007# \r\n#v4011005# 8 #t4011005#s \r\n#v4000030# 80 #t4000030#s \r\n#v4003000# 55 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "STR + 2, DEX + 2" );
	else if ( v2 == 19 ) chat_message5( 20, "#t1072149#", "#v4021008# #t4021008# \r\n#v4011007# #t4011007# \r\n#v4021000# 8 #t4021000#s \r\n#v4000030# 80 #t4000030#s \r\n#v4003000# 55 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "STR + 3, DEX + 1" );
}
else if ( v1 == 3 ) {
	v2 = self.askMenu( "So you want to make magician shoes. What kind of shoes do you want to create?\r\n#L0##b #t1072019##k(Magician Lv. 20)#l\r\n#L1##b #t1072020##k(Magician Lv. 20)#l\r\n#L2##b #t1072021##k(Magician Lv. 20)#l\r\n#L3##b #t1072072##k(Magician Lv. 25)#l\r\n#L4##b #t1072073##k(Magician Lv. 25)#l\r\n#L5##b #t1072074##k(Magician Lv. 25)#l\r\n#L6##b #t1072075##k(Magician Lv. 30)#l\r\n#L7##b #t1072076##k(Magician Lv. 30)#l\r\n#L8##b #t1072077##k(Magician Lv. 30)#l\r\n#L9##b #t1072078##k(Magician Lv. 30)#l\r\n#L10##b #t1072089##k(Magician Lv. 35)#l\r\n#L11##b #t1072090##k(Magician Lv. 35)#l\r\n#L12##b #t1072091##k(Magician Lv. 35)#l\r\n#L13##b #t1072114##k(Magician Lv. 40)#l\r\n#L14##b #t1072115##k(Magician Lv. 40)#l\r\n#L15##b #t1072116##k(Magician Lv. 40)#l\r\n#L16##b #t1072117##k(Magician Lv. 40)#l\r\n#L17##b #t1072140##k(Magician Lv. 50)#l\r\n#L18##b #t1072141##k(Magician Lv. 50)#l\r\n#L19##b #t1072142##k(Magician Lv. 50)#l\r\n#L20##b #t1072143##k(Magician Lv. 50)#l\r\n#L21##b #t1072136##k(Magician Lv. 60)#l\r\n#L22##b #t1072137##k(Magician Lv. 60)#l\r\n#L23##b #t1072138##k(Magician Lv. 60)#l\r\n#L24##b #t1072139##k(Magician Lv. 60)#l" );
	if ( v2 == 0 ) chat_message5( 100, "#t1072019#", "#v4021005# #t4021005# \r\n#v4000021# 30 #t4000021#s \r\n#v4003000# 5 #t4003000#s \r\n#v4031138# 3.000 mesos", 20, "INT +1" );
	else if ( v2 == 1 ) chat_message5( 101, "#t1072020#", "#v4021001# #t4021001# \r\n#v4000021# 30 #t4000021#s \r\n#v4003000# 5 #t4003000#s \r\n#v4031138# 3.000 mesos", 20, "INT +1" );
	else if ( v2 == 2 ) chat_message5( 102, "#t1072021#", "#v4021000# #t4021000# \r\n#v4000021# 30 #t4000021#s \r\n#v4003000# 5 #t4003000#s \r\n#v4031138# 3.000 mesos", 20, "INT +1" );
	else if ( v2 == 3 ) chat_message5( 103, "#t1072072#", "#v4011004# #t4011004# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 8.000 mesos", 25, "INT +1" );
	else if ( v2 == 4 ) chat_message5( 104, "#t1072073#", "#v4021006# #t4021006# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 8.000 mesos", 25, "INT +1" );
	else if ( v2 == 5 ) chat_message5( 105, "#t1072074#", "#v4021004# #t4021004# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 8.000 mesos", 25, "INT +1" );
	else if ( v2 == 6 ) chat_message5( 106, "#t1072075#", "#v4021000# 2 #t4021000#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 18.000 mesos", 30, "INT +1" );
	else if ( v2 == 7 ) chat_message5( 107, "#t1072076#", "#v4021002# 2 #t4021002#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 18.000 mesos", 30, "INT +1" );
	else if ( v2 == 8 ) chat_message5( 108, "#t1072077#", "#v4011004# 2 #t4011004#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 18.000 mesos", 30, "INT +1" );
	else if ( v2 == 9 ) chat_message5( 109, "#t1072078#", "#v4021008# #t4021008# \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 18.000 mesos", 30, "INT +2" );
	else if ( v2 == 10 ) chat_message5( 110, "#t1072089#", "#v4021001# 3 #t4021001#s \r\n#v4021006# #t4021006# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "LUK +1, MP +10" );
	else if ( v2 == 11 ) chat_message5( 111, "#t1072090#", "#v4021000# 3 #t4021000#s \r\n#v4021006# #t4021006# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "INT +1, MP +10" );
	else if ( v2 == 12 ) chat_message5( 112, "#t1072091#", "#v4021008# 2 #t4021008#s \r\n#v4021006# #t4021006# \r\n#v4000021# 40 #t4000021#s \r\n#v4000030# 25 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 22.000 mesos", 35, "INT +1, LUK +1" );
	else if ( v2 == 13 ) chat_message5( 113, "#t1072114#", "#v4021000# 4 #t4021000#s \r\n#v4000030# 40 #t4000030#s \r\n#v4000043# 35 #t4000043#s \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 30.000 mesos", 40, "SOR +2, MP +5" );
	else if ( v2 == 14 ) chat_message5( 114, "#t1072115#", "#v4021005# 4 #t4021005#s \r\n#v4000030# 40 #t4000030#s \r\n#v4000037# 70 #t4000037#s \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 30.000 mesos", 40, "SOR +2, MP +5" );
	else if ( v2 == 15 ) chat_message5( 115, "#t1072116#", "#v4011006# 2 #t4011006#s \r\n#v4021007# #t4021007# \r\n#v4000030# 40 #t4000030#s \r\n#v4000027# 20 #t4000027#s \r\n#v4003000# 25 #t4003000#s \r\n#v4031138# 35.000 mesos", 40, "INT +2, MP +5" );
	else if ( v2 == 16 ) chat_message5( 116, "#t1072117#", "#v4021008# 2 #t4021008#s \r\n#v4021007# #t4021007# \r\n#v4000030# 40 #t4000030#s \r\n#v4000014# 30 #t4000014#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 40.000 mesos", 40, "INT +2, LUK +1, MP +10" );
	else if ( v2 == 17 ) chat_message5( 117, "#t1072140#", "#v4021009# #t4021009# \r\n#v4011006# 3 #t4011006#s \r\n#v4021000# 3 #t4021000#s \r\n#v4000030# 60 #t4000030#s \r\n#v4003000# 40 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "LUK +3" );
	else if ( v2 == 18 ) chat_message5( 118, "#t1072141#", "#v4021009# #t4021009# \r\n#v4011006# 3 #t4011006#s \r\n#v4021005# 3 #t4021005#s \r\n#v4000030# 60 #t4000030#s \r\n#v4003000# 40 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "INT +1, LUK +2" );
	else if ( v2 == 19 ) chat_message5( 119, "#t1072142#", "#v4021009# #t4021009# \r\n#v4011006# 3 #t4011006#s \r\n#v4021001# 3 #t4021001#s \r\n#v4000030# 60 #t4000030#s \r\n#v4003000# 40 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "INT +2, LUK +1" );
	else if ( v2 == 20 ) chat_message5( 120, "#t1072143#", "#v4021009# #t4021009# \r\n#v4011006# 3 #t4011006#s \r\n#v4021003# 3 #t4021003#s \r\n#v4000030# 60 #t4000030#s \r\n#v4003000# 40 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "INT +3" );
	else if ( v2 == 21 ) chat_message5( 121, "#t1072136#", "#v4021009# #t4021009# \r\n#v4011006# 4 #t4011006#s \r\n#v4011005# 5 #t4011005#s \r\n#v4000030# 70 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "INT +1, LUK +3" );
	else if ( v2 == 22 ) chat_message5( 122, "#t1072137#", "#v4021009# #t4021009# \r\n#v4011006# 4 #t4011006#s \r\n#v4021003# 5 #t4021003#s \r\n#v4000030# 70 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "INT +2, LUK +2" );
	else if ( v2 == 23 ) chat_message5( 123, "#t1072138#", "#v4021009# #t4021009# \r\n#v4011006# 4 #t4011006#s \r\n#v4011003# 5 #t4011003#s \r\n#v4000030# 70 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "INT +3, LUK +1" );
	else if ( v2 == 24 ) chat_message5( 124, "#t1072139#", "#v4021009# #t4021009# \r\n#v4011006# 4 #t4011006#s \r\n#v4021002# 5 #t4021002#s \r\n#v4000030# 70 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "INT +3, LUK +1" );
}
else if ( v1 == 4 ) {
	v2 = self.askMenu("So, you want to make archer shoes. What kind of shoes do you want to create?\r\n#L0##b #t1072027##k(Archer Lv. 25)#l\r\n#L1##b #t1072034##k(Archer Lv. 25)#l\r\n#L2##b #t1072069##k(Archer Lv. 25)#l\r\n#L3##b #t1072079##k(Archer Lv. 30)#l\r\n#L4##b #t1072080##k(Archer Lv. 30)#l\r\n#L5##b #t1072081##k(Archer Lv. 30)#l\r\n#L6##b #t1072082##k(Archer Lv. 30)#l\r\n#L7##b #t1072083##k(Archer Lv. 30)#l\r\n#L8##b #t1072101##k(Archer Lv. 35)#l\r\n#L9##b #t1072102##k(Archer Lv. 35)#l\r\n#L10##b #t1072103##k(Archer Lv. 35)#l\r\n#L11##b #t1072118##k(Archer Lv. 40)#l\r\n#L12##b #t1072119##k(Archer Lv. 40)#l\r\n#L13##b #t1072120##k(Archer Lv. 40)#l\r\n#L14##b #t1072121##k(Archer Lv. 40)#l\r\n#L15##b #t1072122##k(Archer Lv. 50)#l\r\n#L16##b #t1072123##k(Archer Lv. 50)#l\r\n#L17##b #t1072124##k(Archer Lv. 50)#l\r\n#L18##b #t1072125##k(Archer Lv. 50)#l\r\n#L19##b #t1072144##k(Archer Lv. 60)#l\r\n#L20##b #t1072145##k(Archer Lv. 60)#l\r\n#L21##b #t1072146##k(Archer Lv. 60)#l");
	if ( v2 == 0 ) chat_message5( 200, "#t1072027#", "#v4011000# 3 #t4011000#s \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 9.000 mesos", 25, "DEX +1" );
	else if ( v2 == 1 ) chat_message5( 201, "#t1072034#", "#v4021003# #t4021003# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 9.000 mesos", 25, "DEX +1" );
	else if ( v2 == 2 ) chat_message5( 202, "#t1072069#", "#v4021000# #t4021000# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 9.000 mesos", 25, "DEX +1" );
	else if ( v2 == 3 ) chat_message5( 203, "#t1072079#", "#v4021000# 2 #t4021000#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "DEX +1" );
	else if ( v2 == 4 ) chat_message5( 204, "#t1072080#", "#v4021005# 2 #t4021005#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "STR +1" );
	else if ( v2 == 5 ) chat_message5( 205, "#t1072081#", "#v4021003# 2 #t4021003#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "DEX +1" );
	else if ( v2 == 6 ) chat_message5( 206, "#t1072082#", "#v4021004# 2 #t4021004#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "DEX +1" );
	else if ( v2 == 7 ) chat_message5( 207, "#t1072083#", "#v4021006# 2 #t4021006#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "STR +1" );
	else if ( v2 == 8 ) chat_message5( 208, "#t1072101#", "#v4021002# 3 #t4021002#s \r\n#v4021006# #t4021006# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "STR +2" );
	else if ( v2 == 9 ) chat_message5( 209, "#t1072102#", "#v4021003# 3 #t4021003#s \r\n#v4021006# #t4021006# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "DEX +2" );
	else if ( v2 == 10 ) chat_message5( 210, "#t1072103#", "#v4021000# 3 #t4021000#s \r\n#v4021006# #t4021006# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "STR + 1, DEX + 1" );
	else if ( v2 == 11 ) chat_message5( 211, "#t1072118#", "#v4021000# 4 #t4021000#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000024# 40 #t4000024#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 32.000 mesos", 40, "STR +2, MP +5" );
	else if ( v2 == 12 ) chat_message5( 212, "#t1072119#", "#v4021006# 4 #t4021006#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000027# 20 #t4000027#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 32.000 mesos", 40, "DEX +1, STR +1, MP +5" );
	else if ( v2 == 13 ) chat_message5( 213, "#t1072120#", "#v4011003# 5 #t4011003#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000044# 40 #t4000044#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 40.000 mesos", 40, "DEX +2, MP +5" );
	else if ( v2 == 14 ) chat_message5( 214, "#t1072121#", "#v4021002# 5 #t4021002#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000009# 120 #t4000009#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 40.000 mesos", 40, "DEX +2, MP +5" );
	else if ( v2 == 15 ) chat_message5( 215, "#t1072122#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4021006# 3 #t4021006#s \r\n#v4000030# 60 #t4000030#s \r\n#v4000033# 80 #t4000033#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "STR +3" );
	else if ( v2 == 16 ) chat_message5( 216, "#t1072123#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4021006# 3 #t4021006#s \r\n#v4000030# 60 #t4000030#s \r\n#v4000032# 150 #t4000032#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "DEX + 1, STR + 2" );
	else if ( v2 == 17 ) chat_message5( 217, "#t1072124#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4021006# 3 #t4021006#s \r\n#v4000030# 60 #t4000030#s \r\n#v4000041# 100 #t4000041#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "DEX + 2, STR + 1" );
	else if ( v2 == 18 ) chat_message5( 218, "#t1072125#", "#v4021008# #t4021008# \r\n#v4011001# 3 #t4011001#s \r\n#v4021006# 3 #t4021006#s \r\n#v4000030# 60 #t4000030#s \r\n#v4000042# 250 #t4000042#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "DEX +3" );
	else if ( v2 == 19 ) chat_message5( 219, "#t1072144#", "#v4021007# #t4021007# \r\n#v4011006# 5 #t4011006#s \r\n#v4021000# 8 #t4021000#s \r\n#v4000030# 75 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "DEX +1, STR +3" );
	else if ( v2 == 20 ) chat_message5( 220, "#t1072145#", "#v4021007# #t4021007# \r\n#v4011006# 5 #t4011006#s \r\n#v4021005# 8 #t4021005#s \r\n#v4000030# 75 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "DEX + 2, STR + 2" );
	else if ( v2 == 21 ) chat_message5( 221, "#t1072146#", "#v4021007# #t4021007# \r\n#v4011006# 5 #t4011006#s \r\n#v4021003# 8 #t4021003#s \r\n#v4000030# 75 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "DEX + 3, STR + 1" );
}
else if ( v1 == 5 ) {
	v2 = self.askMenu("So, you want to make thief shoes. What kind of shoes do you want to create?\r\n#L0##b #t1072084##k(Thief Lv. 25)#l\r\n#L1##b #t1072085##k(Thief Lv. 25)#l\r\n#L2##b #t1072086##k(Thief Lv. 25)#l\r\n#L3##b #t1072087##k(Thief Lv. 25)#l\r\n#L4##b #t1072032##k(Thief Lv. 30)#l\r\n#L5##b #t1072033##k(Thief Lv. 30)#l\r\n#L6##b #t1072035##k(Thief Lv. 30)#l\r\n#L7##b #t1072036##k(Thief Lv. 30)#l\r\n#L8##b #t1072104##k(Thief Lv. 35)#l\r\n#L9##b #t1072105##k(Thief Lv. 35)#l\r\n#L10##b #t1072106##k(Thief Lv. 35)#l\r\n#L11##b #t1072108##k(Thief Lv. 40)#l\r\n#L12##b #t1072109##k(Thief Lv. 40)#l\r\n#L13##b #t1072110##k(Thief Lv. 40)#l\r\n#L14##b #t1072107##k(Thief Lv. 40)#l\r\n#L15##b #t1072128##k(Thief Lv. 50)#l\r\n#L16##b #t1072129##k(Thief Lv. 50)#l\r\n#L17##b #t1072130##k(Thief Lv. 50)#l\r\n#L18##b #t1072131##k(Thief Lv. 50)#l\r\n#L19##b #t1072150##k(Thief Lv. 60)#l\r\n#L20##b #t1072151##k(Thief Lv. 60)#l\r\n#L21##b #t1072152##k(Thief Lv. 60)#l");
	if ( v2 == 0 ) chat_message5( 300, "#t1072084#", "#v4021005# #t4021005# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 9.000 mesos", 25, "LUK +1" );
	else if ( v2 == 1 ) chat_message5( 301, "#t1072085#", "#v4021000# #t4021000# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 9.000 mesos", 25, "LUK +1" );
	else if ( v2 == 2 ) chat_message5( 302, "#t1072086#", "#v4021003# #t4021003# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 9.000 mesos", 25, "LUK +1" );
	else if ( v2 == 3 ) chat_message5( 303, "#t1072087#", "#v4021004# #t4021004# \r\n#v4000021# 35 #t4000021#s \r\n#v4003000# 10 #t4003000#s \r\n#v4031138# 9.000 mesos", 25, "LUK +1" );
	else if ( v2 == 4 ) chat_message5( 304, "#t1072032#", "#v4011000# 3 #t4011000#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "DEX +1" );
	else if ( v2 == 5 ) chat_message5( 305, "#t1072033#", "#v4011001# 3 #t4011001#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "LUK +1" );
	else if ( v2 == 6 ) chat_message5( 306, "#t1072035#", "#v4011004# 2 #t4011004#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 19.000 mesos", 30, "LUK +1" );
	else if ( v2 == 7 ) chat_message5( 307, "#t1072036#", "#v4011006# 2 #t4011006#s \r\n#v4000021# 50 #t4000021#s \r\n#v4003000# 15 #t4003000#s \r\n#v4031138# 21.000 mesos", 30, "DEX +2" );
	else if ( v2 == 8 ) chat_message5( 308, "#t1072104#", "#v4021000# 3 #t4021000#s \r\n#v4021004# #t4021004# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "LUK +2" );
	else if ( v2 == 9 ) chat_message5( 309, "#t1072105#", "#v4021003# 3 #t4021003#s \r\n#v4021004# #t4021004# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "DEX +2" );
	else if ( v2 == 10) chat_message5( 310, "#t1072106#", "#v4021002# 3 #t4021002#s \r\n#v4021004# #t4021004# \r\n#v4000021# 30 #t4000021#s \r\n#v4000030# 15 #t4000030#s \r\n#v4003000# 20 #t4003000#s \r\n#v4031138# 20.000 mesos", 35, "LUK +1, DEX +1" );
	else if ( v2 == 11) chat_message5( 311, "#t1072108#", "#v4021003# 4 #t4021003#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000032# 30 #t4000032#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 32.000 mesos", 40, "DEX +2, MP +5" );
	else if ( v2 == 12) chat_message5( 312, "#t1072109#", "#v4021006# 4 #t4021006#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000040# 3 #t4000040#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 35.000 mesos", 40, "LUK +1, STR +1, MP +5" );
	else if ( v2 == 13) chat_message5( 313, "#t1072110#", "#v4021005# 4 #t4021005#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000037# 70 #t4000037#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 35.000 mesos", 40, "LUK +1, DEX +1, MP +5" );
	else if ( v2 == 14) chat_message5( 314, "#t1072107#", "#v4021000# 5 #t4021000#s \r\n#v4000030# 45 #t4000030#s \r\n#v4000033# 50 #t4000033#s \r\n#v4003000# 30 #t4003000#s \r\n#v4031138# 40.000 mesos", 40, "LUK +2, MP +5" );
	else if ( v2 == 15) chat_message5( 315, "#t1072128#", "#v4011007# 2 #t4011007#s \r\n#v4021005# 3 #t4021005#s \r\n#v4000030# 50 #t4000030#s \r\n#v4000037# 200 #t4000037#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "LUK +3" );
	else if ( v2 == 16) chat_message5( 316, "#t1072129#", "#v4011007# 2 #t4011007#s \r\n#v4021003# 3 #t4021003#s \r\n#v4000030# 50 #t4000030#s \r\n#v4000045# 80 #t4000045#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "DEX +3" );
	else if ( v2 == 17) chat_message5( 317, "#t1072130#", "#v4011007# 2 #t4011007#s \r\n#v4021000# 3 #t4021000#s \r\n#v4000030# 50 #t4000030#s \r\n#v4000043# 150 #t4000043#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "LUK +3" );
	else if ( v2 == 18) chat_message5( 318, "#t1072131#", "#v4011007# 2 #t4011007#s \r\n#v4021001# 3 #t4021001#s \r\n#v4000030# 50 #t4000030#s \r\n#v4000036# 80 #t4000036#s \r\n#v4003000# 35 #t4003000#s \r\n#v4031138# 50.000 mesos", 50, "LUK +2, DEX +1" );
	else if ( v2 == 19) chat_message5( 319, "#t1072150#", "#v4021007# #t4021007# \r\n#v4011007# #t4011007# \r\n#v4021000# 8 #t4021000#s \r\n#v4000030# 75 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "LUK + 1, STR + 3" );
	else if ( v2 == 20) chat_message5( 320, "#t1072151#", "#v4021007# #t4021007# \r\n#v4011007# #t4011007# \r\n#v4011006# 5 #t4011006#s \r\n#v4000030# 75 #t4000030#s \r\n#v4003000# 50 #t4003000#s \r\n#v4031138# 60.000 mesos", 60, "STR +1, DEX +3" );
	else if ( v2 == 21) chat_message5( 321, "#t1072152#", "#v4021007# #t4021007# \r\n#v4011007# #t4011007# \r\n#v4021008# #t4021008# \r\n#v4000030# 75 #t4000030# \r\n#v4003000# 50 #t4003000# \r\n#v4031138# 60.000 mesos", 60, "DEX + 1, LUK + 3" );
}

function chat_message5(index, makeItem, needItem, reqLevel, itemOption ) {
	inventory = target.inventory;

	let nRet = self.askYesNo( "To make " + makeItem + ", I'll need the following materials. What do you think? Do you want to make one? \r\n\r\n#b" + needItem );
	if ( nRet == 0 ) self.say( "It's not just shoes and jewelry that I can refine, but also rare and valuable shoes... think about it and take a look around." );
	else {
		// Warrior Shoes
		if ( index == 1 ) ret = self.InventoryExchange( -10000, 4011004, -2, 4011001, -1, 4000021, -15, 4003000, -10, 1072051, 1 );
		else if ( index == 2 ) ret = self.InventoryExchange( -10000, 4011006, -2, 4011001, -1, 4000021, -15, 4003000, -10, 1072053, 1 );
		else if ( index == 3 ) ret = self.InventoryExchange( -12000, 4021008, -1, 4011001, -2, 4000021, -20, 4003000, -10, 1072052, 1 );
		else if ( index == 4 ) ret = self.InventoryExchange( -20000, 4021003, -4, 4011001, -2, 4000021, -45, 4003000, -15, 1072003, 1 );
		else if ( index == 5 ) ret = self.InventoryExchange( -20000, 4011002, -4, 4011001, -2, 4000021, -45, 4003000, -15, 1072039, 1 );
		else if ( index == 6 ) ret = self.InventoryExchange( -20000, 4011004, -4, 4011001, -2, 4000021, -45, 4003000, -15, 1072040, 1 );
		else if ( index == 7 ) ret = self.InventoryExchange( -20000, 4021000, -4, 4011001, -2, 4000021, -45, 4003000, -15, 1072041, 1 );
		else if ( index == 8 ) ret = self.InventoryExchange( -22000, 4011001, -3, 4021004, -1, 4000021, -30, 4000030, -20, 4003000, -25, 1072002, 1 );
		else if ( index == 9 ) ret = self.InventoryExchange( -22000, 4011002, -3, 4021004, -1, 4000021, -30, 4000030, -20, 4003000, -25, 1072112, 1 );
		else if ( index == 10 ) ret = self.InventoryExchange( -25000, 4021008, -2, 4021004, -1, 4000021, -30, 4000030, -20, 4003000, -25, 1072113, 1 );
		else if ( index == 11 ) ret = self.InventoryExchange( -38000, 4011003, -4, 4000021, -100, 4000030, -40, 4003000, -30, 4000033, -100, 1072000, 1 );
		else if ( index == 12 ) ret = self.InventoryExchange( -38000, 4011005, -4, 4021007, -1, 4000030, -40, 4003000, -30, 4000042, -250, 1072126, 1 );
		else if ( index == 13 ) ret = self.InventoryExchange( -38000, 4011002, -4, 4021007, -1, 4000030, -40, 4003000, -30, 4000041, -120, 1072127, 1 );
		else if ( index == 14 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4021003, -6, 4000030, -65, 4003000, -45, 1072132, 1 );
		else if ( index == 15 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4011002, -6, 4000030, -65, 4003000, -45, 1072133, 1 );
		else if ( index == 16 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4011005, -6, 4000030, -65, 4003000, -45, 1072134, 1 );
		else if ( index == 17 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4011006, -6, 4000030, -65, 4003000, -45, 1072135, 1 );
		else if ( index == 18 ) ret = self.InventoryExchange( -60000, 4021008, -1, 4011007, -1, 4021005, -8, 4000030, -80, 4003000, -55, 1072147, 1 );
		else if ( index == 19 ) ret = self.InventoryExchange( -60000, 4021008, -1, 4011007, -1, 4011005, -8, 4000030, -80, 4003000, -55, 1072148, 1 );
		else if ( index == 20 ) ret = self.InventoryExchange( -60000, 4021008, -1, 4011007, -1, 4021000, -8, 4000030, -80, 4003000, -55, 1072149, 1 );
		// Magician Shoes
		else if ( index == 100 ) ret = self.InventoryExchange( -3000, 4021005, -1, 4000021, -30, 4003000, -5, 1072019, 1 );
		else if ( index == 101) ret = self.InventoryExchange( -3000, 4021001, -1, 4000021, -30, 4003000, -5, 1072020, 1 );
		else if ( index == 102 ) ret = self.InventoryExchange( -3000, 4021000, -1, 4000021, -30, 4003000, -5, 1072021, 1 );
		else if ( index == 103 ) ret = self.InventoryExchange( -8000, 4011004, -1, 4000021, -35, 4003000, -10, 1072072, 1 );
		else if ( index == 104 ) ret = self.InventoryExchange( -8000, 4021006, -1, 4000021, -35, 4003000, -10, 1072073, 1 );
		else if ( index == 105 ) ret = self.InventoryExchange( -8000, 4021004, -1, 4000021, -35, 4003000, -10, 1072074, 1 );
		else if ( index == 106 ) ret = self.InventoryExchange( -18000, 4021000, -2, 4000021, -50, 4003000, -15, 1072075, 1 );
		else if ( index == 107 ) ret = self.InventoryExchange( -18000, 4021002, -2, 4000021, -50, 4003000, -15, 1072076, 1 );
		else if ( index == 108 ) ret = self.InventoryExchange( -18000, 4011004, -2, 4000021, -50, 4003000, -15, 1072077, 1 );
		else if ( index == 109 ) ret = self.InventoryExchange( -18000, 4021008, -1, 4000021, -50, 4003000, -15, 1072078, 1 );
		else if ( index == 110 ) ret = self.InventoryExchange( -20000, 4021001, -3, 4021006, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072089, 1 );
		else if ( index == 111 ) ret = self.InventoryExchange( -20000, 4021000, -3, 4021006, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072090, 1 );
		else if ( index == 112 ) ret = self.InventoryExchange( -22000, 4021008, -2, 4021006, -1, 4000021, -40, 4000030, -25, 4003000, -20, 1072091, 1 );
		else if ( index == 113 ) ret = self.InventoryExchange( -30000, 4021000, -4, 4000030, -40, 4000043, -35, 4003000, -25, 1072114, 1 );
		else if ( index == 114 ) ret = self.InventoryExchange( -30000, 4021005, -4, 4000030, -40, 4000037, -70, 4003000, -25, 1072115, 1 );
		else if ( index == 115 ) ret = self.InventoryExchange( -35000, 4011006, -2, 4021007, -1, 4000030, -40, 4000027, -20, 4003000, -25, 1072116, 1 );
		else if ( index == 116 ) ret = self.InventoryExchange( -40000, 4021008, -2, 4021007, -1, 4000030, -40, 4000014, -30, 4003000, -30, 1072117, 1 );
		else if ( index == 117 ) ret = self.InventoryExchange( -50000, 4021009, -1, 4011006, -3, 4021000, -3, 4000030, -60, 4003000, -40, 1072140, 1 );
		else if ( index == 118 ) ret = self.InventoryExchange( -50000, 4021009, -1, 4011006, -3, 4021005, -3, 4000030, -60, 4003000, -40, 1072141, 1 );
		else if ( index == 119 ) ret = self.InventoryExchange( -50000, 4021009, -1, 4011006, -3, 4021001, -3, 4000030, -60, 4003000, -40, 1072142, 1 );
		else if ( index == 120 ) ret = self.InventoryExchange( -50000, 4021009, -1, 4011006, -3, 4021003, -3, 4000030, -60, 4003000, -40, 1072143, 1 );
		else if ( index == 121 ) ret = self.InventoryExchange( -60000, 4021009, -1, 4011006, -4, 4011005, -5, 4000030, -70, 4003000, -50, 1072136, 1 );
		else if ( index == 122 ) ret = self.InventoryExchange( -60000, 4021009, -1, 4011006, -4, 4021003, -5, 4000030, -70, 4003000, -50, 1072137, 1 );
		else if ( index == 123 ) ret = self.InventoryExchange( -60000, 4021009, -1, 4011006, -4, 4011003, -5, 4000030, -70, 4003000, -50, 1072138, 1 );
		else if ( index == 124 ) ret = self.InventoryExchange( -60000, 4021009, -1, 4011006, -4, 4021002, -5, 4000030, -70, 4003000, -50, 1072139, 1 );
		// Bowman Shoes
		else if ( index == 200 ) ret = self.InventoryExchange( -9000, 4011000, -3, 4000021, -35, 4003000, -10, 1072027, 1 );
		else if ( index == 201 ) ret = self.InventoryExchange( -9000, 4021003, -1, 4000021, -35, 4003000, -10, 1072034, 1 );
		else if ( index == 202 ) ret = self.InventoryExchange( -9000, 4021000, -1, 4000021, -35, 4003000, -10, 1072069, 1 );
		else if ( index == 203 ) ret = self.InventoryExchange( -19000, 4021000, -2, 4000021, -50, 4003000, -15, 1072079, 1 );
		else if ( index == 204 ) ret = self.InventoryExchange( -19000, 4021005, -2, 4000021, -50, 4003000, -15, 1072080, 1 );
		else if ( index == 205 ) ret = self.InventoryExchange( -19000, 4021003, -2, 4000021, -50, 4003000, -15, 1072081, 1 );
		else if ( index == 206 ) ret = self.InventoryExchange( -19000, 4021004, -2, 4000021, -50, 4003000, -15, 1072082, 1 );
		else if ( index == 207 ) ret = self.InventoryExchange( -19000, 4021006, -2, 4000021, -50, 4003000, -15, 1072083, 1 );
		else if ( index == 208 ) ret = self.InventoryExchange( -20000, 4021002, -3, 4021006, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072101, 1 );
		else if ( index == 209 ) ret = self.InventoryExchange( -20000, 4021003, -3, 4021006, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072102, 1 );
		else if ( index == 210 ) ret = self.InventoryExchange( -20000, 4021000, -3, 4021006, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072103, 1 );
		else if ( index == 211 ) ret = self.InventoryExchange( -32000, 4021000, -4, 4000030, -45, 4000024, -40, 4003000, -30, 1072118, 1 );
		else if ( index == 212 ) ret = self.InventoryExchange( -32000, 4021006, -4, 4000030, -45, 4000027, -20, 4003000, -30, 1072119, 1 );
		else if ( index == 213 ) ret = self.InventoryExchange( -40000, 4011003, -5, 4000030, -45, 4000044, -40, 4003000, -30, 1072120, 1 );
		else if ( index == 214 ) ret = self.InventoryExchange( -40000, 4021002, -5, 4000030, -45, 4000009, -120, 4003000, -30, 1072121, 1 );
		else if ( index == 215 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4021006, -3, 4000030, -60, 4000033, -80, 4003000, -35, 1072122, 1 );
		else if ( index == 216 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4021006, -3, 4000030, -60, 4000032, -150, 4003000, -35, 1072123, 1 );
		else if ( index == 217 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4021006, -3, 4000030, -60, 4000041, -100, 4003000, -35, 1072124, 1 );
		else if ( index == 218 ) ret = self.InventoryExchange( -50000, 4021008, -1, 4011001, -3, 4021006, -3, 4000030, -60, 4000042, -250, 4003000, -35, 1072125, 1 );
		else if ( index == 219 ) ret = self.InventoryExchange( -60000, 4021007, -1, 4011006, -5, 4021000, -8, 4000030, -75, 4003000, -50, 1072144, 1 );
		else if ( index == 220 ) ret = self.InventoryExchange( -60000, 4021007, -1, 4011006, -5, 4021005, -8, 4000030, -75, 4003000, -50, 1072145, 1 );
		else if ( index == 221 ) ret = self.InventoryExchange( -60000, 4021007, -1, 4011006, -5, 4021003, -8, 4000030, -75, 4003000, -50, 1072146, 1 );
		// Thief Shoes
		else if ( index == 300 ) ret = self.InventoryExchange( -9000, 4021005, -1, 4000021, -35, 4003000, -10, 1072084, 1 );
		else if ( index == 301 ) ret = self.InventoryExchange( -9000, 4021000, -1, 4000021, -35, 4003000, -10, 1072085, 1 );
		else if ( index == 302 ) ret = self.InventoryExchange( -9000, 4021003, -1, 4000021, -35, 4003000, -10, 1072086, 1 );
		else if ( index == 303 ) ret = self.InventoryExchange( -9000, 4021004, -1, 4000021, -35, 4003000, -10, 1072087, 1 );
		else if ( index == 304 ) ret = self.InventoryExchange( -19000, 4011000, -3, 4000021, -50, 4003000, -15, 1072032, 1 );
		else if ( index == 305 ) ret = self.InventoryExchange( -19000, 4011001, -3, 4000021, -50, 4003000, -15, 1072033, 1 );
		else if ( index == 306 ) ret = self.InventoryExchange( -19000, 4011004, -2, 4000021, -50, 4003000, -15, 1072035, 1 );
		else if ( index == 307 ) ret = self.InventoryExchange( -21000, 4011006, -2, 4000021, -50, 4003000, -15, 1072036, 1 );
		else if ( index == 308 ) ret = self.InventoryExchange( -20000, 4021000, -3, 4021004, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072104, 1 );
		else if ( index == 309 ) ret = self.InventoryExchange( -20000, 4021003, -3, 4021004, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072105, 1 );
		else if ( index == 310 ) ret = self.InventoryExchange( -20000, 4021002, -3, 4021004, -1, 4000021, -30, 4000030, -15, 4003000, -20, 1072106, 1 );
		else if ( index == 311 ) ret = self.InventoryExchange( -32000, 4021003, -4, 4000030, -45, 4000032, -30, 4003000, -30, 1072108, 1 );
		else if ( index == 312 ) ret = self.InventoryExchange( -35000, 4021006, -4, 4000030, -45, 4000040, -3, 4003000, -30, 1072109, 1 );
		else if ( index == 313 ) ret = self.InventoryExchange( -35000, 4021005, -4, 4000030, -45, 4000037, -70, 4003000, -30, 1072110, 1 );
		else if ( index == 314 ) ret = self.InventoryExchange( -40000, 4021000, -5, 4000030, -45, 4000033, -50, 4003000, -30, 1072107, 1 );
		else if ( index == 315 ) ret = self.InventoryExchange( -50000, 4011007, -2, 4021005, -3, 4000030, -50, 4000037, -200, 4003000, -35, 1072128, 1 );
		else if ( index == 316 ) ret = self.InventoryExchange( -50000, 4011007, -2, 4021003, -3, 4000030, -50, 4000045, -80, 4003000, -35, 1072129, 1 );
		else if ( index == 317 ) ret = self.InventoryExchange( -50000, 4011007, -2, 4021000, -3, 4000030, -50, 4000043, -150, 4003000, -35, 1072130, 1 );
		else if ( index == 318 ) ret = self.InventoryExchange( -50000, 4011007, -2, 4021001, -3, 4000030, -50, 4000036, -80, 4003000, -35, 1072131, 1 );
		else if ( index == 319 ) ret = self.InventoryExchange( -60000, 4021007, -1, 4011007, -1, 4021000, -8, 4000030, -75, 4003000, -50, 1072150, 1 );
		else if ( index == 320 ) ret = self.InventoryExchange( -60000, 4021007, -1, 4011007, -1, 4011006, -5, 4000030, -75, 4003000, -50, 1072151, 1 );
		else if ( index == 321 ) ret = self.InventoryExchange( -60000, 4021007, -1, 4011007, -1, 4021008, -1, 4000030, -75, 4003000, -50, 1072152, 1 );

		if ( ret == 0 ) self.say( "Please check carefully if you have all the items you need and if your equip inventory is full or not." );
		else self.say( "There, the shoes are ready. Be careful not to trip!" );
	}
}

function chat_message6(index, makeItem, needItemIcon, needItemString, unitPrice ) {
	inventory = target.inventory;

	nRetNum = self.askNumber("To make " + makeItem + ", I need the following items... how many would you like to do?\r\n\r\n#b" + needItemIcon + " 10 " + needItemString + "\r\n" + unitPrice + " mesos#k", 0, 0, 100 );
	nPrice = unitPrice * nRetNum;
	nAllNum = nRetNum * 10;
	nRetBuy = self.askYesNo("To make #b" + nRetNum + " " + makeItem + "#k, I need the following materials. What do you think? Would you like to create #b" + nRetNum + " " + makeItem + "s#k?\r\n\r\n#b" + needItemIcon + " " + nAllNum + " " + needItemString + "\r\n" + nPrice + " mesos#k");
	if ( nRetBuy == 0 ) self.say("It's not just shoes and jewelry that I can refine, but also rare and valuable shoes... think about it and take a look around.");
	else {
		// Ores
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

		if ( ret == 0 ) self.say( "Please see me if you have " + needItemIcon + needItemString + ", or if your inventory is full or not." );
		else self.say( "Here, take the "  + nRetNum + " " + makeItem + "(s). What do you think? Pretty refined, huh? Hahaha... Finally, all those days spent studying refining days were worth it. Please come back another time!" );
	}
}
'''
