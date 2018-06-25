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

'NPC: Dances with Balrog'
'Script: Warrior Job Instructor'

self.say("#bScript: fighter#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python (needs a lot of cleanup)

let val = self.QuestRecordGet(7500);
let cJob = self.UserGetJob();

if (self.UserGetLevel() >= 200) {
	if (self.UserGetJob() >= 100 && self.UserGetJob() < 200) {
		let info = self.QuestRecordGet(7530);
		if (info == "") {
			let rank = self.UserGetRanking();
			if (rank > 0 && rank <= 20) {
				fighter_lv200(rank);
			}
		}
	}
} else if (val == "s" && (cJob == 110 || cJob == 120 || cJob == 130)) {
	self.QuestRecordSet(7500, "p1");
	self.sayNext("I've been waiting for you. A few days ago, I heard about you from #bTylus#k of Ossyria. Well... I'd like to test your strength. You will find a Door of Dimension deep inside the Cursed Temple in the heart of Victoria Island. Nobody but you can go into that passage. If you go into the passage, you will meet my other self. Beat him and bring #b#t4031059##k to me.");
	self.sayNext("My other self is quite strong. He uses many special skills and you should fight with him 1 on 1. However, people cannot stay long in the secret passage, so it is impotrant to beat him ASAP. Well... Good luck! I will look forward to you bringing #b#t4031059##k to me.");
} else if (val == "p1") {
	if (self.InventoryGetItemCount(4031059) >= 1) {
		self.sayNext("Wow... You beat my other self and brought #b#t4031059##k to me. Good! This surely proves your strength. In terms of strength, you are ready to advance to 3rd job. As I promised, I will give #b#t4031057##k to you. Give this necklace to #bTylus#k in Ossyria and you will be able to take second the test of 3rd job advancement. Good luck~.");
		let ret = self.InventoryExchange(0, 4031059, -1, 4031057, 1);
		if (!ret) self.say("Hmm... how strange. Are you sure that you have #b#t4031059##k? If yes, make sure you have a free slot in your inventory.");
		else self.QuestRecordSet(7500, "p2");
	} else
		self.say("You will find a Door of Dimension deep inside the Cursed Temple in the heart of Victoria Island. Nobody but you can go into that passage. If you go into the passage, you will meet my other self. Beat him and bring #b#t4031059##k to me.");
} else if (val == "p2") {
	if (self.InventoryGetItemCount(4031057) <= 0) {
		self.say("Ahh! You lost #b#t4031057##k, huh? I told you to be careful... For God's sake, I'll give you another one... AGAIN. Please, be careful this time. Without this, you won't be able to do the quest for 3rd job advancement.");
		let ret = self.InventoryExchange(0, 4031057, 1);
		if (!ret) self.say("Hmm... how strange. Make sure you have a free slot in your inventory.");
	} else
		self.say("Deliver this necklace to #bTylus#k of Ossyria and you'll be able to do the second quest for job advancement. Good luck~.");
} else {
	if (cJob == 100) {
		if (self.UserGetLevel() >= 30) {
			if (self.InventoryGetItemCount(4031008) >= 1) self.say("Still haven't found the person? Find #b#p1072000##k which is near #b#m102020300##k next to #m102000000#. Deliver the letter to him and maybe he'll tell you what to do.");
			else if (self.InventoryGetItemCount(4031012) >= 1) {
				self.sayNext("Ahh! You came back without a scratch! I knew this would be easy... I admit that you're a strong and formidable warrior... right, now I'll turn you into a stronger Warrior... BUT, before that, you'll have to choose one of the three ways that will be offered... it won't be easy, if you have any questions, bring it on!");
				let nSel = self.askMenu("Well, when you decide, click on [I want to choose my job!] in the bottom.\r\n#b#L0#Explain to me the job of a Fighter.#k#l\r\n#b#L1#Explain to me the job of a Page.#k#l\r\n#b#L2#Explain to me the job of a Spearman.#k#l\r\n#b#L3#I want to choose my job!#k#l");
				if (nSel == 0) {
					self.sayNext("Let me explain the job of a Fighter. It's the most common type of Warrior. The used weapons are #bsword#k and the #baxe#k, because there are advanced abilities that could be adquired later. Don't utilize both weapons at the same time. Use the one that suits you better...");
					self.sayNext("Besides that, there are abilities like #b#q1101006##k and #b#q1101007##k available to fighters. #b#q1101006##k is the type of ability that lets you and your party improve your weapons. With it, you'll be able to defeat enemies with a surprising amount of power, which makes the skill really useful. The disadvantage is that your defense will be lowered.");
					self.sayNext("#b#q1101007##k is an ability that allows you to return a portion of the damage dealt by an enemy. The stronger the attack, the more damage is returned. This will help those who prefer close combat. What do you think? Isn't it cool to be a Fighter?");
				} else if (nSel == 1) {
					self.sayNext("Let me explain the job of a Page. Page is a knight giving their first steps. He uses #bswords#k and/or #bmaces#k. It is not a good idea to use both weapons, so it's better to choose one and stick with it.");
					self.sayNext("Besides that, there are abilities #b#q1201006##k and #b#q1101007##k for you to learn. #b#q1201006##k makes any opponents around you love some offensive and defensive abilities. This skill is useful against powerful enemies with great offensive ablilities. Also works well in competitive games.");
					self.sayNext("#b#q1101007##k is an ability that allows you to return a portion of the damage dealt by an enemy. The stronger the attack, the more damage is returned. It's the perfect ability for warriors that specialize in really close combat. What do you think? Isn't it cool to be a Page?");
				} else if (nSel == 2) {
					self.sayNext("Let me explain the job of a Spearman. It's a class specialized in #bspears#k and #bpolearms#k. There are many usefull abilities to be adquried with both weapons, but i recommendthat you choose one and stick with it.");
					self.sayNext("Besides that, there are abilities like #b#q1301006##k and #b#q1301007##k for you to learn. #b#q1301006##k allows you and your party to reinforce your weapon and magic defense. It's an usefull ability for Spearman since they use both hands to attack and do not have a free hand for defense.");
					self.sayNext("#b#q1301007##k is an ability that lets you and your party increase your maximum HP and MP. You could make an increase of up to 160%, this ability will help you and your partie whenever you go take on really strong enemies. What do you think? Isn't it cool to be a Spearman?");
				} else if (nSel == 3) {
					let nSel2 = self.askMenu("Hmmmm, already made up your mind? Choose the 2nd job of advancement of your preference.\r\n#b#L0#Fighter#k#l\r\n#b#L1#Page#k#l\r\n#b#L2#Spearman#k#l");
					if (nSel2 == 0) {
						let nRet = self.askYesNo("Are you sure you want to be a #bFighter#k? After making your decision, you won't be able to go back and change it... still want it?");
						if (nRet == 0) self.sayNext("Really? Then you need to think a little more. No need to hurry... it's not something to be done half heartedly... let me know when you made your decision, okay?");
						else if (nRet == 1) {
							let nPSP = (self.UserGetLevel() - 30) * 3;
							if (self.UserGetSP() > nPSP) self.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
							else if (!self.InventoryExchange(0, 4031012, -1)) self.sayNext("Hmmmm... Are you sure you have #b#t4031012##k from #p1072000#? It's better to be sure, because I can't allow you to advance classes without it.");
							else {
								self.UserJob(110);
								self.UserIncSP(1, false);
								let incval = Math.floor((Math.random() * (350 - 300 + 1) + 300));
								self.UserIncMHP(incval, false);
								self.InventoryIncSlotCount(2, 4);
								self.InventoryIncSlotCount(4, 4);
								self.sayNext("Right! Now you are a #bFighter#k! A fighter fights to be the strongest among the strong and never stops fighting . Never lose your fighting spirit and always give your best. I'll help you become stronger than you already are.");
								self.sayNext("I gave you a book that contains a list of abilities you can obtain as a ighter. In the book, you'll find a lot of abilities Fighters can learn. Your inventory of Use and Etc. were also expanded. Your max MP also increased... check it.");
								self.sayNext("Also gave you a little bit of #bSP#k. Open the #bAbilities Menu#k localized in the lower left. You'll be able to improve your 2nd job skills even further. A warning: It's not possible to improve them all at once. Some will only be available after learning others. Don't forget this.");
								self.sayNext("Fighters need to be strong. But remember to not abuse your power and use it against someone weaker. Use your power rightfully, because... to use it the right way is more difficult than becoming stronger. Look for me after you have advanced more.");
							}
						}
					} else if (nSel2 == 1) {
						let nRet = self.askYesNo("So you really want to be a #bPage#k? Remember, when you make your decision, you won't be able to change classes. Are you sure that you want to do this?");
						if (nRet == 0) self.sayNext("Really? You need to think better, right? No need to hurry. It's not something to be done half heartedly... come talk to me when you have made your decision, okay?");
						else if (nRet == 1) {
							let nPSP = (self.UserGetLevel() - 30) * 3;
							if (self.UserGetSP() > nPSP) self.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
							else if (!self.InventoryExchange(0, 4031012, -1)) self.sayNext("Hmmmm... Are you sure you have #b#t4031012##k from #p1072000#? It's better to be sure, because I can't allow you to advance classes without it.");
							else {
								self.UserJob(120);
								self.UserIncSP(1, false);
								let incval = Math.floor((Math.random() * (150 - 100 + 1) + 100));
								self.UserIncMHP(incval, false);
								self.InventoryIncSlotCount(2, 4);
								self.InventoryIncSlotCount(4, 4);
								self.sayNext("Right! Now you are a #bPage#k! Pages have the intelligence and braveness of the Warriors... I hope you follow the right path with the right mentality... I'll help you become even stronger than you already are.");
								self.sayNext("I gave you a book that contains a list of abilities you can obtain as a Page. In the book, you'll find a lot of abilities Page can learn. Your inventory of Use and Etc. were also expanded. Your max MP also increased... check it.");
								self.sayNext("Also gave you a little bit of #bSP#k. Open the #bAbilities Menu#k localized in the lower left. You'll be able to improve your 2nd job skills even further. A warning: It's not possible to improve them all at once. Some will only be available after learning others. Don't forget this.");
								self.sayNext("Fighters need to be strong. But remember to not abuse your power and use it against someone weaker. Use your power rightfully, because... to use it the right way is more difficult than becoming stronger. Look for me after you have advanced more. I'll be waiting.");
							}
						}
					} else if (nSel2 == 2) {
						let nRet = self.askYesNo("So you really want to be a #bSpearman#k? When you make your decision, you won't be able to change it. Are you sure you want to do this?");
						if (nRet == 0) self.sayNext("Really? You need to think more about it? No need to hurry. It's not something to be done half heartedly... come talk to me when you have made your decision, okay?");
						else if (nRet == 1) {
							let nPSP = (self.UserGetLevel() - 30) * 3;
							if (self.UserGetSP() > nPSP) self.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
							else if (!self.InventoryExchange(0, 4031012, -1)) self.sayNext("Hmmmm... Are you sure you have #b#t4031012##k from #p1072000#? It's better to be sure, because I can't allow you to advance classes without it.");
							else {
								self.UserJob(130);
								self.UserIncSP(1, false);
								let incval = Math.floor((Math.random() * (150 - 100 + 1) + 100));
								self.UserIncMHP(incval, false);
								self.InventoryIncSlotCount(2, 4);
								self.InventoryIncSlotCount(4, 4);
								self.sayNext("Right! Now you are a  #bSpearman#k! The Spearman uses the power of darkness to defeat enemies, always in the darkness... believe in yourself and in your own power... I'll help you become even stronger than you already are.");
								self.sayNext("I gave you a book that contains a list of abilities you can obtain as a Spearman. In the book, you'll find a lot of abilities Page can learn. Your inventory of Use and Etc. were also expanded. Your max MP also increased... check it.");
								self.sayNext("Also gave you a little bit of #bSP#k. Open the #bAbilities Menu#k localized in the lower left. You'll be able to improve your 2nd job skills even further. A warning: It's not possible to improve them all at once. Some will only be available after learning others. Don't forget this.");
								self.sayNext("Spearmans need to be strong. But remember to not abuse your power and use it against someone weaker. Use your power rightfully, because... to use it the right way is more difficult than becoming stronger. Look for me after you have advanced more. I'll be waiting.");
							}
						}
					}
				}
			} else {
				let nRet = self.askYesNo("Wow, you really grew up! You aren't #gsmall#k and #gweak#k... now I can see you as a true Warrior! Impressive... so, what do you think? Want to be even stronger? You just need to take a test! Are you up to it?");
				if (nRet == 0) self.say("Really? Becoming stronger faster will help you in your journey... if you change your mind in the future, you can come back here. Remember that I will turn you more #gstronger#k than you already are.");
				else if (nRet == 1) {
					self.sayNext("Good choice. You're strong, don't get me wrong, but there's still the need to test your strength and to see if your power is real. The test isn't that hard, you'll do good... Here, take this letter. Don't lose it.");
					let ret = self.InventoryExchange(0, 4031008, 1);
					if (!ret) self.say("Hmmmm... I can't give you the letter because you don't have a free slot in your Etc. inventory slot. Come back after freeing one slot or two of your inventory, because the letter is your only way to take the test.");
					else self.say("Give this letter to #b#p1072000##k which can be near #b#m102020300##k next to #m102000000#. He's subbing me as an instrutor, because I'm busy here. Give him the letter and he'll give you the test in my place. Other information will be given by him. Good luck.");
				}
			}
		} else {
			let v = self.askMenu("Oh, you have a question?\r\n#L0##bWhat are the general characteristics of being a Warrior?#k#l\r\n#L1##bWhat are the weapons that the Warriors use?#k#l\r\n#L2##bWhat are the armors that the Warriors can wear?#k#l\r\n#L3##bWhat are the skills available for the Warriors?#k#l");
			if (v == 0) {
				self.sayNext("Let me explain the role of a Warrior. Warriors possess awesome physical strength and power. They can also defende monsters' attacks, so they are the best when fighting up close with the monsters. With a high level of stamina, you won't be dying easily either.");
				self.sayNext("To accurately attack the monster, however, you need a healthy dose of DEX, so don't just concentrate on boosting up the STR. If you want to improve rapidly, I recommend that you face stronger monsters.");
			} else if (v == 1) {
				self.sayNext("Let me explain the weapons Warriors use. They can use weapons that allow them to slash, stab or strike. You won't be able to use weapons like bows and projectile weapons. Same with the small canes. ");
				self.sayNext("The most common weapons are sword, blunt weapon, polearm, speak, axe, and etc...Every weapon has its advantages and disadvantages, so please take a close look at them before choosin gone. For now, try using the ones with high attack rating.");
			} else if (v == 2) {
				self.sayNext("Let me explain the armors Warriors wear. Warriors are strong with high stamine, so they are able to wear tough, strong armor. It's not the greatest looking ones...but it serves its purpose well, the best of the armors.");
				self.sayNext("Especially the shields, they are perfect for the Warriors. Remember, though, that you won't be able to use the shield if you are using the weapon that requires both hands. I know it's going to be a hard decision for you...");
			} else if (v == 3) {
				self.sayNext("For the Warriors, the skills available are geared towards their awesome physical strength and power. The skill that helps you in close combats will help you the most. There's also a skill that allows you to recover your HP. Make sure to master that.");
				self.sayNext("The two attacking skills available are #bPower Strike#k and #bSlash Blast#k. Power Strike is the one that applies a lot of damage to a single enemy. You can boost this skills up from the beginning.");
				self.sayNext("On the other hand, Slash Blast does not apply much damage, but instead attacks multiple enemies around the area at once. You can only use this once you have 1 Power Strike boosted up. Its up to you.");
			}
		}
	} else if (cJob == 0) {
		self.sayNext("Do you wish to be a Warrior? You need to meet some criteria in order to do so. #bYou need to be at least in Level 10#k. Let's see...");
		if (self.UserGetLevel() > 9) {//&& self.UserGetSTR() > 34
			let nRet = self.askYesNo("You definitely have the look of a Warrior. You may not be there just yet, but I can see the Warrior in you. What do you think? Do you want to become a Warrior?");
			if (nRet == 0) self.sayNext("Really? Do you need more time to give more thought to it? By all means... this is not something you should take lightly. Come talk to me once your have made your decision.");
			else if (nRet == 1) {
				if (self.InventoryGetSlotCount(1) > self.InventoryGetHoldCount(1)) {
					self.sayNext("From here on out, you are going to be the Warrior! Please continue working hard...I'll enhance your abilities a bit with the hope of you training yourself to be even stronger than you're now. Haaaaaap!!");
					let ret = self.InventoryExchange(0, 1302077, 1);
					if (!ret) self.sayNext("Hmmm. Make sure you have a free slot in your inventory tab Equip. I would like to give you a weapon since you job advanced.");
					else {
						self.UserJob(100);
						self.UserIncSP(1, false);
						if (self.UserGetLevel() >= 30) {
							self.sayNext("I think you've made the job advancement way too late. Usually, for beginners under Level 29 that were late in making job advancements, we compensate them with lost Skill Points, that weren't rewarded, but...I think you're a little too late for that. I am so sorry, but there's nothing I can do.");
						} else {
							let nPSP = (self.UserGetLevel() - 10) * 3;
							self.UserIncSP(nPSP, false);
						}
						let incval = Math.floor((Math.random() * (250 - 200 + 1) + 200));
						self.UserIncMHP(incval, false);
						self.InventoryIncSlotCount(1, 4);
						self.InventoryIncSlotCount(2, 4);
						self.InventoryIncSlotCount(3, 4);
						self.InventoryIncSlotCount(4, 4);
						self.sayNext("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can accquire only after having learned a couple of skills first.");
						self.sayNext("One more warning. Once you have chosen your job, try to stay alive as much as you can. Once you reach that level, when you die, you will lose your experience level. You wouldn't want to lose your hard-earned experience points, do you? This is all i can teach you...from here on out, it's all about pushing yourself harder and become better. See me after you feel that you have gotten much more powerful than you are right now.");
						self.sayNext("Oh, and... if you have questions about being a Warrior, feel free to ask. I don't know EVERYTHING, but I'll help you out with all that I know of. Til then...");
					}
				} else
					self.sayNext("Hmmm... Make sure you have a free space in your inventory tab for Equip. I'm trying to give you a weapon for your work.");
			}
		} else
			self.sayNext("You need more training to be a Warrior. In order to be one, you need to train yourself to be more powerful than you are right now. Please come back much stronger.");
	}
	else if ( cJob == 110 ) self.say( "Ahhh... It's you! What do you think? How's it to be a Fighter? You... look stronger! I hope you keep training." ); 
	else if ( cJob == 120 ) self.say( "Ahhh... It's you! What do you think? How's it to be a Page? You... look stronger! I hope you keep training!" ); 
	else if ( cJob == 130 ) self.say( "Ahhh... It's you! What do you think? How's it to be a Spearman? You... look stronger! I hope you keep training" ); 
	else if ( cJob == 111 ) self.say( "Ahhh... You finally became a #bCrusader#k... I knew I wouldn't be let down. So, how does it feel to be a Crusader? Please, keep up the work and keep training." ); 
	else if ( cJob == 121 ) self.say( "Ahhh... You finally became a #bWhite Knight#k... I knew I wouldn't be let down. So, how does it feel to be a White Knight? Please, keep up the work and keep training." ); 
	else if ( cJob == 131 ) self.say( "Ahhh... You finally became a #bDragon Knight#k... I knew I wouldn't be let down. So, how does it feel to be a Dragon Knight? Please, keep up the work and keep training." ); 
	else if ( cJob == 112 ) self.say( "Ahhh... You finally became a #bHero#k... I knew I wouldn't be let down. So, how does it feel to be a Hero? Please, keep up the work and keep training." ); 
	else if ( cJob == 122 ) self.say( "Ahhh... You finally became a #bPaladin#k... I knew I wouldn't be let down. So, how does it feel to be a Paladin? Please, keep up the work and keep training." ); 
	else if ( cJob == 132 ) self.say( "Ahhh... You finally became a #bDark Knight#k... I knew I wouldn't be let down. So, how does it feel to be a Dark Knight? Please, keep up the work and keep training." ); 
	else self.say( "Awesome body! Awesome power! Warriors are they way to go!!!! What do you think? Want to make the job advancement as a Warrior??" ); 
}

function fighter_lv200(rank) {
	let info = self.QuestRecordGet(7530);
	if (info == "") {
		self.sayNext("You… you… is it really you? Wow… I remember when you were level 1, it seems like yesterday… and now you're the warrior of all warriors, a bona-fide hero of the Maple World.");
		let nRet = self.askYesNo("You are more than good enough to become the face of all warriors here. What do you think? Are you interested in leaving your other self here to show the other warriors what greatness is all about?");
		if (nRet == 0) self.sayNext("I can't believe you said no to that. Let me know if you ever change your mind.");
		else {
			let code = 9901000 + rank - 1;
			let ret = self.RegisterImitatedNPC(code);
			if (!ret) self.sayNext("I don't think you are worthy of leaving your other self here.");
			else {
				self.QuestRecordSet(7530, "1");
				self.sayNext("What do you think? Your other self is now established at the temple of all warriors here! Here's hoping other warriors will be inspired for greatness whenever they look at this..");
			}
		}
	} else if (info == "1") {
		self.sayNext("Numerous adventurers stop by and get inspired by your likeness. Your every move is being watched by others, so set an example of strength, honor, and dedication to everyone that crosses your path. May the wisdom of the elders grant you insight!");
	}
}
'''
