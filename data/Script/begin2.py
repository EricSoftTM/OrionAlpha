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
NPC: Roger
Quest: Roger's Apple
'''

# TODO: Implement Roger's Apple quest handling.
prompt = "Hey! Nice weather today, huh?"
self.say(prompt)

# q1021s
'''
sel = self.askMenu(prompt + "\r\n\r\n#b#L0#Roger's Apple#l#k")
if sel == 0:
  if self.userGetGender() == 0:
    self.sayNext("Hey, Man~  What's up? Haha!  I am Roger who can teach you adorable new Maplers lots of information.")
    self.sayNext("You are asking who made me do this?  Ahahahaha!  Myself!  I wanted to do this and just be kind to you new travellers.")
  else:
    self.sayNext("Hey there, Pretty~ I am Roger who teaches you adorable new Maplers lots of information.")
    self.sayNext("I know you are busy! Please spare me some time~ I can teach you some useful information! Ahahaha!")

  ret = self.askYesNo("So..... Let me just do this for fun! Abaracadabra~!")
  if ret == 0:
    self.sayNext("I can't believe you have just turned down an attractive guy like me!")
  else:
    if self.inventoryGetItemCount(2010007) >= 1:
      val = self.userGetHP() / 2
      self.userIncHP(-val, False)
      self.sayNext("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you #r#t2010007##k. Please take it. You will feel stronger. Open the Item window and double click to consume. Hey, it's very simple to open the Item window. Just press #bI#k on your keyboard.")
      self.sayNext("Please take all #t2010007# that I gave you. You will be able to see the HP bar increasing. Please talk to me again when you recover your HP 100%.")
    else:
      if self.inventoryExchange(0, [2010007, 1]) == False:
        self.sayNext("Your use inventory must be full.")
      else:
        val = self.userGetHP() / 2
        self.userIncHP(-val, False)
        self.sayNext("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you #r#t2010007##k. Please take it. You will feel stronger. Open the Item window and double click to consume. Hey, it's very simple to open the Item window. Just press #bI#k on your keyboard.")
        self.sayNext("Please take all #t2010007# that I gave you. You will be able to see the HP bar increasing. Please talk to me again when you recover your HP 100%.")
'''

# q1021e
'''
if self.inventoryGetItemCount(2010007) == 0:
  if self.userGetHP() == self.userGetMHP():
    file = "#fUI/UIWindow.img/QuestIcon/"
    self.sayNext("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.")
    self.sayNext("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!")
    self.say("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care if yourself and Good luck my friend!\r\n\r\n" + file + "4/0#\r\n#v2010000# 3 #t2010000#\r\n#v2010009# 3 #t2010009#\r\n\r\n" + file + "8/0# 10 exp")
    
    if self.inventoryExchange(0, [2010000, 3, 2010009, 3]) == True:
      self.sayNext("Your inventory is full...")
    else:
      self.userIncEXP(10, False)
      # TODO: Implement quest control on backend. => Set quest progress to completed.
  else:
    self.sayNext("Hey, your HP is not fully recovered yet. Did you take all the #t2010007# that I gave you? Are you sure?")
else:
  # Veja... Eu disse para voc?pegar cada #r#t2010007##k que eu te dei. Abra a Janela de Itens e clique na #baba USO#b. L?voc?ver?a #t2010007#, d?dois cliques para usar.
  self.sayNext("filler text awaiting English translation, nice hacks m8")
'''