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

'NPC: Magician Job Instructor'
'Script: Magician 2nd Job Advancement'

self.say("#bScript: change_magician#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
if (self.UserGetJob() == 200 && self.UserGetLevel() >= 30) {
	let nBlack = self.InventoryGetItemCount(4031013);
	if (self.InventoryGetItemCount(4031009) >= 1) {
		if (nBlack == 0) {
			self.sayNext("Hmmm...it is definitely the letter from #b#p1032001##k...so you came all the way here to take the test and make the 2nd job advancement as the magician. Alright, I'll explain the test to you. Don't sweat it much, though; it's not that complicated.");
			self.sayNext("I'll send you to a hidden map. You'll see monsters not normally seen in normal fields. They look the same like the regular ones, but with a totally different attitude. They neither boost your experience level nor provide you with item.");
			self.sayNext("You'll be able to acquire a marble called #b#t4031013##k while knocking down those monsters. It is a special marble made out of their sinister, evil minds. Collect 30 of those, then go talk to a colleague of mine in there. That's how you pass the test.");
			let nRet = self.askYesNo("Once you go inside, you can't leave until you take care of your mission. If you die, your experience level will decrease...so you better really buckle up and get ready...well, do you want to go for it now?");
			if (nRet == 0) self.sayNext("I don't think you are prepared for this. Talk to me when you make yourself much more ready for this. There are neither portals nor stores inside, so you better get ready for it all the way.");
			else if (nRet == 1) {
				self.sayNext("Alright, I'll let you in! Once you go in, defeat the monsters and collect 30 Dark Marbles. After that go talk to my colleague inside to receive #b#t4031012##k as proof that you have passed the test. Best of luck to you.");
				let aMap = [108000200, 108000201, 108000202];
				self.RegisterTransferField(aMap[Math.floor(Math.random() * aMap.length)], "");
			}
		} else if (nBlack > 0) {
			let nRet = self.askYesNo("So you've given up in the middle of this before. Don't worry about it, because you can always retake the test. Now...do you want to go back in and try again?");
			if (nRet == 0) self.sayNext("You don't seem too prepared for this. Find me when you ARE ready. There are neither portals or stores inside, so you better get 100% ready for it.");
			else if (nRet == 1) {
				self.sayNext("Alright! I'll let you in! Sorry to say this, but I have to take away all your marbles beforehand. Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you the #b#t4031012##k, the proof that you've passed the test. Best of luck to you.");
				self.InventoryExchange(0, 4031013, -nBlack);
				let aMap = [108000200, 108000201, 108000202];
				self.RegisterTransferField(aMap[Math.floor(Math.random() * aMap.length)], "");
			}
		}
	} else
		self.sayNext("Do you want to be a stronger magician? Let me take care of that for you, then. You look definitely qualified for it. For now, go see #b#p1032001##k of Ellinia first.");
} else if (self.UserGetJob() == 200 && self.UserGetLevel() < 30) {
	//TODO: English Translation
	self.sayNext("Deseja se tornar um Bruxo mais poderoso do que j?? Permita-me tomar conta disso. No entanto, voc?parece um pouco #Gfraco:fraca#. Treine mais um pouco, fique mais forte e depois volte aqui.");
} else if (self.UserGetJob() == 210 || self.UserGetJob() == 220 || self.UserGetJob() == 230) {
	//TODO: English Translation
	self.sayNext("Ah, foi voc?quem passou no meu teste outro dia! O que voc?acha? Conseguiu se tornar mais forte? Bom! Agora consigo definitivamente notar seu porte de Bruxo...");
}
'''
