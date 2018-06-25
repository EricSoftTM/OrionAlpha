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

'NPC: Warrior Job Instructor'
'Script: Furthers a user\'s progress in Warrior 2nd Job Advancement'

self.say("#bScript: inside_swordman#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

// Warrior Job Instructor 
if (self.UserGetJob() == 100 && self.UserGetLevel() >= 30) {
	if (self.InventoryGetItemCount(4031013) >= 30) {
		self.sayNext("Ohhhhh...you collected all 30 Dark Marbles!! It should have been difficult...just incredible! You've passed the test and for that, I'll reward you #b#t4031012##k. Take that and go back to Perion.");
		let nBlack = self.InventoryGetItemCount(4031013);
		let ret = self.InventoryExchange(0, 4031013, -nBlack, 4031008, -1, 4031012, 1);
		//Algo est?errado... verifique se voc?tem 30 itens do tipo #t4031013#, a carta do #b#p1022000##k e um slot vazio no seu invent?io de Etc.
		if (!ret) self.say("Please, check whether or not you have room in your Etc inventory.");
		else self.RegisterTransferField(102020300, "");
	} else {
		let nRet = self.askYesNo("What's going on? Doesn't look like you have collected 30 #b#t4031013##k, yet...If you're having problems with it, then you can leave, come back and try it again. So...do you want to give up and get out of here?");
		if (nRet == 0)
			self.sayNext("That's right! Stop acting weak and start collecting the marbles. Talk to me when you have collected 30 #b#t4031013##k.");
		else if (nRet == 1) {
			self.sayNext("Really... alright, I'll let you out. Please don't give up, though. You can always try again, so do not give up. Until then, bye...");
			self.RegisterTransferField(102020300, "");
		}
	}
} else {
	//O qu? Como voc?chegou aqui?... que estranho... bom, vou deixar voc?sair. Este ?um lugar muito perigoso. V?embora ou correr?mais riscos.
	self.sayNext("What are you doing here? This is for warriors who are ready for advancement.");
	self.RegisterTransferField(102020300, "");
}
'''
