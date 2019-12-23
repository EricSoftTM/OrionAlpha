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
NPC: Arwen the Fairy
Quest: Arwen and Ellinia
'''

# TODO: Implement Arwen and Ellinia quest handling.
prompt = "I lost a very important item ..."
self.say(prompt)

'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Arwen and Ellinia#l#k")
if sel == 0:
  ret = self.askYesNo("A few days ago, on my way back to Ellinia, I got ambushed by a monster, which made me run for my life. Ever since then, even if I wanted to venture outside Ellinia, just the thought of that monster waiting for me scares me to death. Can you please take care of those monsters?")
  if ret == False:
    self.say("I see... the thought of not being able to go outside for a while is just terrible... If you ever change your mind, please come talk to me.")
  else:
    # TODO: Implement quest control on backend. => Set quest progress to started.
    self.sayNext("Thank you so much. Once you take care of those monsters, then and only then will I feel safe to walk out of Ellinia.")
    self.say("Now, on your way back to Ellinia, please eliminate #b100 Green Mushrooms#k. Then myself or others like me won't feel threatened to go out of Ellinia for a little while.")
'''

# Moon and Star Rocks don't exist in this version yet. Whoops..
'''
menu = ""
newItemID = ""

if self.userGetJob() != 0 and self.userGetLevel() > 39:
  self.sayNext("Yes... I am the chief alchemist of the fairies. It's true that fairies do not contact a human being for possibly a long period of time ... but a strong person like you will be fine. If you get the materials, I'll make you a special item.")
  sel = self.askMenu("What do you want to do? \r\n#b#L0##t4011007##l\r\n#L1##t4021009##l\r\n#L2##t4031042##l")
  if sel == 0:
    menu = "#v4011000# #t4011000# \r\n#v4011001# #t4011001# \r\n#v4011002# #t4011002# \r\n#v4011003# #t4011003# \r\n#v4011004# #t4011004# \r\n#v4011005# #t4011005# \r\n#v4011006# #t4011006# \r\n#v4031138# 10,000 mesos"
    ret = self.askYesNo("So you want to make #b#t4011007##k? For this, you need #rone#k of each of these refined items. Do you want to do it?\r\n#b" + menu)
    if ret == 0:
      self.say("It's not easy to make #t4011007#. Please get the materials soon.")
    else:
      ret = self.inventoryExchange(-10000, [4011000, -1, 4011001, -1, 4011002, -1, 4011003, -1, 4011004, -1, 4011005, -1, 4011006, -1, 4011007, 1])
      if ret == 0:
        self.say("Are you sure you have enough money or materials? Please make sure you have materials required.")
      else:
        newItemID = "#t4021009#"
  elif sel == 1:
    menu = "#v4021000# #t4021000# \r\n#v4021001# #t4021001# \r\n#v4021002# #t4021002# \r\n#v4021003# #t4021003# \r\n#v4021004# #t4021004# \r\n#v4021005# #t4021005# \r\n#v4021006# #t4021006# \r\n#v4021007# #t4021007# \r\n#v4021008# #t4021008# \r\n#v4031138# 15,000 mesos"
    ret = self.askYesNo("So you want to make #b#t4021009##k? For this, you need #rone#k of each of these refined items. Do you want to do it?\r\n#b" + menu)
    if ret == 0:
      self.say("It's not easy to make #t4021009#. Please get the materials soon.")
    else:
      ret = self.inventoryExchange(-15000, [4021000, -1, 4021001, -1, 4021002, -1, 4021003, -1, 4021004, -1, 4021005, -1, 4021006, -1, 4021007, -1, 4021008, -1, 4021009, 1])
      if ret == 0:
        self.say("Are you sure you have enough money or materials? Please make sure you have materials required.")
      else:
        newItemID = "#t4021009#"
  elif sel == 2:
    menu = "#v4001006# #t4001006# \r\n#v4011007# #t4011007# \r\n#v4021008# #t4021008# \r\n #v4031138# 30,000 mesos"
    ret = self.askYesNo("So you want to make #b#t4031042##k? For this, you need #rone#k of each of these refined items. Ah yes, this #t4031042# is a very special item. If you happen to knock it down, it will disappear, and you can not pass it on to anyone. Do you want to do it?\r\n#b" + menu)
    if ret == 0:
      self.say("It's not easy to make #t4031042#. Please get the materials soon.")
    else:
      ret = self.inventoryExchange(-30000, [4001006, -1, 4021008, -1, 4011007, -1, 4031042, 1])
      if ret == 0:
        self.say("Are you sure you have enough money or materials? Please make sure you have materials required.")
      else:
        newItemID1 = "#t4031042#"
  if newItemID != "":
    self.say("Cool, get it here. " + newItemID1 + ". It's very well done, it's important to use good materials. If you ever need my help, count on me, okay?")
else:
  self.say("I make valuable and rare items, but unfortunately, I can not do anything for a stranger like you.")
'''