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

'NPC: Arwen the Fairy'
'Script: Arwen Crafter'

self.say("#bScript: owen#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python (cleanup too)

let list = "";

if ( self.UserGetJob() != 0 && self.UserGetLevel() > 39 ) {
  self.sayNext( "Yes... I am the chief alchemist of the fairies. It's true that fairies do not contact a human being for possibly a long period of time ... but a strong person like you will be fine. If you get the materials, I'll make you a special item." );
  v2 = self.askMenu( "What do you want to do? \r\n#b#L0##t4011007##l\r\n#L1##t4021009##l\r\n#L2##t4031042##l" );
  nNewItemID1 = "";
  if ( v2 == 0 ) {
    list = "#v4011000# #t4011000# \r\n#v4011001# #t4011001# \r\n#v4011002# #t4011002# \r\n#v4011003# #t4011003# \r\n#v4011004# #t4011004# \r\n#v4011005# #t4011005# \r\n#v4011006# #t4011006# \r\n#v4031138# 10,000 mesos";
    nRet = self.askYesNo( "So you want to make #b#t4011007##k? For this, you need #rone#k of each of these refined items. Do you want to do it?\r\n#b" + list );
    if ( nRet == 0 ) self.say( "It's not easy to make #t4011007#. Please get the materials soon." );
    else {
      ret = self.InventoryExchange( -10000, 4011000, -1, 4011001, -1, 4011002, -1, 4011003, -1, 4011004, -1, 4011005, -1, 4011006, -1, 4011007, 1 );
      if ( ret == 0 ) self.say( "Are you sure you have enough money or materials? Please make sure you have #b#t4011000#, #t4011001#, #t4011002#, #t4011003#, #t4011004#, #t4011005# e #t4011006##k Refined, one of each." );
      else nNewItemID1 = "#t4011007#";
    }
  }
  else if ( v2 == 1 ) {
    list = "#v4021000# #t4021000# \r\n#v4021001# #t4021001# \r\n#v4021002# #t4021002# \r\n#v4021003# #t4021003# \r\n#v4021004# #t4021004# \r\n#v4021005# #t4021005# \r\n#v4021006# #t4021006# \r\n#v4021007# #t4021007# \r\n#v4021008# #t4021008# \r\n#v4031138# 15,000 mesos";
    nRet = self.askYesNo( "So you want to make #b#t4021009##k? For this, you need #rone#k of each of these refined items. Do you want to do it?\r\n#b" + list );
    if ( nRet == 0 ) self.say( "It's not easy to make #t4021009#. Please get the materials soon." );
    else {
      ret = self.InventoryExchange( -15000, 4021000, -1, 4021001, -1, 4021002, -1, 4021003, -1, 4021004, -1, 4021005, -1, 4021006, -1, 4021007, -1, 4021008, -1, 4021009, 1 );
      if ( ret == 0 ) self.say( "Are you sure you have enough money or materials? Please make sure you have materials required." );
      else nNewItemID1 = "#t4021009#";
    }
  }
  else if ( v2 == 2 ) {
    list = "#v4001006# #t4001006# \r\n#v4011007# #t4011007# \r\n#v4021008# #t4021008# \r\n #v4031138# 30,000 mesos";
    nRet = self.askYesNo( "So you want to make #b#t4031042##k? For this, you need #rone#k of each of these refined items. Ah yes, this #t4031042# is a very special item. If you happen to knock it down, it will disappear, and you can not pass it on to anyone. Do you want to do it?\r\n#b" + list );
    if ( nRet == 0 ) self.say( "It's not easy to make #t4031042#. Please get the materials soon." );
    else {
      ret = self.InventoryExchange( -30000, 4001006, -1, 4021008, -1, 4011007, -1, 4031042, 1 );
      if ( ret == 0 ) self.say( "Are you sure you have enough money or materials? Please make sure you have materials required." );
      else nNewItemID1 = "#t4031042#";
    }
  }
  if ( nNewItemID1 != "" ) self.say( "Cool, get it here. " + nNewItemID1 + ". It's very well done, it's important to use good materials. If you ever need my help, count on me, okay?" );
}
else self.say( "I make valuable and rare items, but unfortunately, I can not do anything for a stranger like you." );
'''
