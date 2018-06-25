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

'NPC: Thief Job Instructor'
'Script: Thief 2nd Job Advancement'

self.say("#bScript: change_rogue#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

if (self.UserGetJob() == 400 && self.UserGetLevel() >= 30) {
	let nBlack = self.InventoryGetItemCount(4031013);
	if (self.InventoryGetItemCount(4031011) >= 1) {
		if (nBlack == 0) {
			self.sayNext("Hmmm...it is definitely the letter from #b#p1052001##k...so you came all the way here to take the test and make the 2nd job advancement as the rogue. Alright, I'll explain the test to you. Don't sweat it much, though; it's not that complicated.");
			self.sayNext("I'll send you to a hidden map. You'll see monsters not normally seen in normal fields. They look the same like the regular ones, but with a totally different attitude. They neither boost your experience level nor provide you with item.");
			self.sayNext("You'll be able to acquire a marble called #b#t4031013##k while knocking down those monsters. It is a special marble made out of their sinister, evil minds. Collect 30 of those, then go talk to a colleague of mine in there. That's how you pass the test.");
			let nRet = self.askYesNo("Once you go inside, you can't leave until you take care of your mission. If you die, your experience level will decrease...so you better really buckle up and get ready...well, do you want to go for it now?");
			if (nRet == 0) self.sayNext("You don't seem too prepared for this. Find me when you ARE ready. There are neither portals or stores inside, so you better get 100% ready for it.");
			else if (nRet == 1) {
				self.sayNext("Alright! I'll let you in! Defeat the monster inside to earn 30 Dark Marble and then talk to my colleague inside; he'll give you #b#t4031012##k as a proof that you've passed the test. Best of luck to you.");
				let aMap = [108000400, 108000401, 108000402];
				self.RegisterTransferField(aMap[Math.floor(Math.random() * aMap.length)], "");
			}
		} else if (nBlack > 0) {
			let nRet = self.askYesNo("So you've given up in the middle of this before. Don't worry about it, because you can always retake the test. Now...do you want to go back in and try again?");
			if (nRet == 0) self.sayNext("You don't seem too prepared for this. Find me when you ARE ready. There are neither portals or stores inside, so you better get 100% ready for it.");
			else if (nRet == 1) {
				self.sayNext("Alright! I'll let you in! Sorry to say this, but I have to take away all your marbles beforehand. Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you the #b#t4031013##k, the proof that you've passed the test. Best of luck to you.");
				self.InventoryExchange(0, 4031013, -nBlack);
				let aMap = [108000400, 108000401, 108000402];
				self.RegisterTransferField(aMap[Math.floor(Math.random() * aMap.length)], "");
			}
		}
	} else
		self.sayNext("Do you want to be a stronger thief? Let me take care of that for you, then. You look definitely qualified for it. For now, go see #b#p1052001##k of Kerning City first.");
} else if (self.UserGetJob() == 400 && self.UserGetLevel() < 30) {
	//TODO: English Translation
	//Voc?realmente deseja se tornar um Gatuno melhor? Ent? deixe-me tomar conta disso. No entanto, voc?parece muito #Gfraco:fraca#. Treine at?ficar mais forte e depois volte aqui.
	self.sayNext("You really want to become a better thief? Let me take care of it. However, you seem very #gweak#k. Train and get stronger and then come back here.");
} else if (self.UserGetJob() == 410 || self.UserGetJob() == 420 || self.UserGetJob() == 430) {
	//TODO: English Translation
	//Hummm... foi voc?quem passou no meu teste!! O que voc?acha? Ficou mais forte desde ent?? Bom! Agora consigo definitivamente notar seu porte de Gatuno...
	self.sayNext("Hmmm... it was you who took on my test!! What do you think? Have you become stronger since then?? Good! Now I can definitely see your strength as a rogue.");//"size of robber" LUL
}
'''
