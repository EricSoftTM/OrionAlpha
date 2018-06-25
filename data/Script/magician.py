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

'NPC: Grendel the Really Old'
'Script: Magician Job Instructor'

self.say("#bScript: magician#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

let val = self.QuestRecordGet(7500);
let cJob = self.UserGetJob();

if (self.UserGetLevel() >= 200) {
	if (self.UserGetJob() >= 200 && self.UserGetJob() < 300) {
		let info = self.QuestRecordGet(7530);
		if (info == "") {
			let rank = self.UserGetRanking();
			if (rank > 0 && rank <= 20) {
				magician_lv200(rank);
			}
		}
	}
} else if (val == "s" && (cJob == 210 || cJob == 220 || cJob == 230)) {
	self.QuestRecordSet(7500, "p1");
	self.sayNext("I've been waiting for you. Few days ago, I heard about you from #bRobeira#k of Ossyria. Well... I'd like to test your strength. You will find a Door of Dimension deep inside the Cursed Temple in the heart of Victoria Island. Nobody but you can go into that passage. If you go into the passage, you will meet my other self. Beat him and bring #b#t4031059##k to me.");
	self.sayNext("My other self is quite strong. He uses many special skills and you should fight with him 1 on 1. However, people cannot stay long in the secret passage, so it is important to beat him ASAP. Well... Good luck! I will look forward to you bringing #b#t4031059##k to me.");
} else if (val == "p1") {
	if (self.InventoryGetItemCount(4031059) >= 1) {
		self.sayNext("Wow... You beat my other self and brought #b#t4031059##k to me. Good! This surely proves your strength. In terms of strength, you are ready to advance to 3rd job. As I promised, I will give #b#t4031057##k to you. Give this necklace to #bRobeira#k in Ossyria and you will be able to take second the test of 3rd job advancement. Good luck~.");
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
	if (cJob == 200) {
		if (self.UserGetLevel() >= 30) {
			if (self.InventoryGetItemCount(4031009) >= 1) self.say("Still haven't met the person yet? Find #b#p1072001##k who's around #b#m101020000#k near Ellinia. Give the letter to him and he may let you know what to do.");
			else if (self.InventoryGetItemCount(4031012) >= 1) {
				self.sayNext("You got back here safely. Well done. I knew you'd pass the tests very easily...alright, I'll make you much stronger now. Before that, though...you need to choose one of the three paths that will be given to you. It will be a tough decision for you to make, but...if you have any questions about it, feel free to ask.");
				let nSel = self.askMenu("Alright, when you have made your decision, click on [I'll choose my occupation!] at the very bottom...\r\n#L0##bPlease explain the characteristics of the Wizard of Fire and Poison.#l#k\r\n#L1##bPlease explain the characteristics of the Wizard of ICe and Lightning.#l#k\r\n#L2##bPlease explain the characteristics of the Cleric.#l#k\r\n#L3##bI'll choose my occupation!#l#k\r\n");
				if (nSel == 0) {
					self.sayNext("Allow me to explain the Wizard of Fire and Poison. They specialize in fire and poision magic. Skills like #bMeditation#k, that allows you and your whole party's magic ability to increase for a time being, and #bMP Eater#k, which allows you a certain probability of absorbing some of your enemy's MP, are essential to all the attacking Magicians.");
					self.sayNext("I'll explain to you a magic attack called #bFire Arrow#k. It fires away flamearrows to the enemies, making it the most powerful skill available for the skills in the 2nd level. It'll work best on enemies that are weak against fire in general, for the damage will be much bigger. On the other hand, if you use them on enemies that are strong against fire, the damage will only be half of what it usually is, so keep that in mind.");
					self.sayNext("I'll explain to you a magic attack called #bPoison Breath#k. It fires away venomous bubbles on the enemies, poisoning them in the process. Once poisoned, the enemy's HP will decrease little by little over time. If the magic doesn't work too well or the monster has high HP, it may be a good idea to fire enough to kill them with the overdose of poison.");
				} else if (nSel == 1) {
					self.sayNext("Allow me to explain the Wizard of Ice and Lightning. They specialize in ice and lightning magic. Skills like #bMeditation#k, that allows you and your whole party's magic ability to increase for a time being, and #bMP Eater#k, which allows you a certain probability of absorbing some of your enemy's MP, are essential to all the attacking Magicians.");
					self.sayNext("I'll explain to you a magic attack called #bCold Beam#k. It fires away pieces of ice at the enemies, and although not quite as powerful as Fire Arrow, whoever's struck by it will be frozen for a short period of time. The damage increases much more if the enemy happens to be weak against ice. The opposite holds true, too, in that if the enemy is used to ice, the damage won't quite be as much, so keep that in mind.");
					self.sayNext("I'll explain to you a magic attack called #bThunder Bolt#k. It's the only 2nd-level skill for Magicians that can be considered the Total Spell, affecting a lot of monsters at once. It may not dish out a lot of damage, but the advantage is that it damages all the monsters around you. You can only attack upto six monsters at once, though. Still, it's a pretty incredible attack.");
				} else if (nSel == 2) {
					self.sayNext("Allow me to explain the Cleric. Clerics use religious magic on monsters through prayers and incantation. Skills like #bBless#k, which temporarily improves the weapon def., magic def., accuracy, avoidability, and #bInvincible#k, which decreases the weapon damage for a certain amount, help magicians overcome their shortcomings ...");
					self.sayNext("Cleric is the only Wizard that can perform recovering magic. Clerics are the only one that can do recovery magic. It's called #bHeal#k, and the more MP, INT's, and the skill level for this skill you have, the more HP you may recover. It also affects your party close by so it's a very useful skill, enabling you to continue to hunt without the help of the potion.");
					self.sayNext("Clerics also have a magic attack called #bHoly Arrow#k. It's a spell that allows the Cleric to fire away phantom arrows at the monsters. The damage isn't too great, but it can apply tremendous damage to the undead's and other evil-based monsters. Those monsters are very weak against holy attack. What do you think, isn't it interesting, right?");
				} else if (nSel == 3) {
					let nSel2 = self.askMenu("Now, have you made up your mind? Please select your occupation for the 2nd job advancement.\r\n#L0##bThe Wizard of Fire and Poison#l#k\r\n#L1##bThe Wizard of Ice and Lightning#l#k\r\n#L2##bCleric#l#k\r\n");
					if (nSel2 == 0) {
						let nRet = self.askYesNo("So you want to make the 2nd job advancement as the #bWizard of Fire and Poison#k? Once you have made your decision, you can't go back and change your job anymore. Are you sure about the decision?");
						if (nRet == 0) self.sayNext("Really? Have to give more thought to it, huh? Take your time, take your time. This is not something you should take lightly ... come talk to me once you have made your decision.");
						else if (nRet == 1) {
							let nPSP = (self.UserGetLevel() - 30) * 3;
							if (self.UserGetSP() > nPSP) self.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
							else if (!self.InventoryExchange(0, 4031012, -1)) self.sayNext("Hmmmm... are you sure you have #b#t4031012##k from #p1072001#? It's better to be sure... because you can't job advance without it....");
							else {
								self.UserJob(210);
								self.UserIncSP(1, false);
								let incval = Math.floor((Math.random() * (450 - 500 + 1) + 500));
								self.UserIncMMP(incval, false);
								self.InventoryIncSlotCount(4, 4);
								self.sayNext("From here on out, you have become the #bWizard of Fire and Poison#k... Wizards use high intelligence and the power of nature all around us to take down the enemies...please continue your studies, for one day I may make you much more powerful with my own power...");
								self.sayNext("I have just given you a book that gives you the list of skills you can acquire as the Wizard of Fire and Poison...I've also extended your etc. inventory by added a whole row to it, along with your maximum MP...go see it for yourself.");
								self.sayNext("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottomleft corner. You'll be able to boost up the newly-acquired 2nd level skills. A word of warning though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
								self.sayNext("The Wizards have to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger. Find me after you have advanced much further ...");
							}
						}
					} else if (nSel2 == 1) {
						let nRet = self.askYesNo("So you want to make the 2nd job advancement as the #bWizard of Ice and Lightning#k? Once you have made your decision, you can't go back and change your job anymore. Are you sure about the decision?");
						if (nRet == 0) self.sayNext("Really? Have to give more thought to it, huh? Take your time, take your time. This is not something you should take lightly ... come talk to me once you have made your decision.");
						else if (nRet == 1) {
							let nPSP = (self.UserGetLevel() - 30) * 3;
							if (self.UserGetSP() > nPSP) self.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
							else if (!self.InventoryExchange(0, 4031012, -1)) self.sayNext("Hmmmm... are you sure you have #b#t4031012##k from #p1072001#? It's better to be sure... because you can't job advance without it....");
							else {
								self.UserJob(220);
								self.UserIncSP(1, false);
								let incval = Math.floor((Math.random() * (450 - 500 + 1) + 500));
								self.UserIncMMP(incval, false);
								self.InventoryIncSlotCount(4, 4);
								self.sayNext("From here on out, you have become the #bWizard of Ice and Lightning#k... Wizards use high intelligence and the power of nature all around us to take down the enemies...please continue your studies, for one day I may make you much more powerful with my own power...");
								self.sayNext("I have just given you a book that gives you the list of skills you can acquire as the Wizard of Ice and Lightning...I've also extended your etc. inventory by added a whole row to it. Your maximum MP has gone up, too. Go see for it yourself.");
								self.sayNext("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottomleft corner. You'll be able to boost up the newly-acquired 2nd level skills. A word of warning though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
								self.sayNext("The Wizards have to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger. Find me after you have advanced much further. I'll be waiting ...");
							}
						}
					} else if (nSel2 == 2) {
						let nRet = self.askYesNo("So you want to make the 2nd job advancement as the #bCleric#k? Once you have made your decision, you can't go back and change your job anymore. Are you sure about the decision?");
						if (nRet == 0) self.sayNext("Really? Have to give more thought to it, huh? Take your time, take your time. This is not something you should take lightly ... come talk to me once you have made your decision.");
						else if (nRet == 1) {
							let nPSP = (self.UserGetLevel() - 30) * 3;
							if (self.UserGetSP() > nPSP) self.sayNext("Hmmm...you have too much SP...you can't make the 2nd job advancement with that many SP in store. Use more SP on the skills on the 1st level and then come back.");
							else if (!self.InventoryExchange(0, 4031012, -1)) self.sayNext("Hmmmm... are you sure you have #b#t4031012##k from #p1072001#? It's better to be sure... because you can't job advance without it....");
							else {
								self.UserJob(230);
								self.UserIncSP(1, false);
								let incval = Math.floor((Math.random() * (450 - 500 + 1) + 500));
								self.UserIncMMP(incval, false);
								self.InventoryIncSlotCount(4, 4);
								self.sayNext("Alright, you're a #bCleric#k from here on out. Clerics blow life into every living organism here with their undying faith in God. Never stop working on your faith...then one day, I'll help you become much more powerful...");
								self.sayNext("I have just given you a book that gives you the list of skills you can acquire as the Cleric...I've also extended your etc. inventory by added a whole row to it, along with your maximum MP...go see it for yourself.");
								self.sayNext("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottomleft corner. You'll be able to boost up the newly-acquired 2nd level skills. A word of warning though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
								self.sayNext("The Cleric needs more faith than anything else. Keep your strong faith in God and treat everyone with respect and dignity they deserve. Keep working hard and you may one day earn more religious magic power...alright...please find me after you have made more strides. I'll be waiting for you...");
							}
						}
					}
				}
			} else {
				let nRet = self.askYesNo("Hmmm...you have grown quite a bit since last time. You look much different from before, where you looked weak and small...instead now I can definitely feel you presence as the Magician...so...what do you think? Do you want to get even stronger than you are right now? Pass a simple test and I can do that for you...do you want to do it?");
				if (nRet == 0) self.say("Really? It will help you out a great deal on your journey if you get stronger fast...if you choose to change your mind in the future, please feel free to come back. Know that I'll make you much more powerful than you are right now.");
				else if (nRet == 1) {
					self.sayNext("Good...you look strong, alright, but I need to see if it is for real. The test isn't terribly difficult and you should be able to pass it. Here, take my letter first. Make sure you don't lose it.");
					let ret = self.InventoryExchange(0, 4031009, 1);
					if (!ret) self.say("Hmmmm... I can't give you the letter because you don't have a free slot in your Etc. inventory slot. Come back after freeing one slot or two of your inventory, because the letter is your only way to take the test.");
					else self.say("Please get this letter to #b#p1072001##k around #b#m101020000##k near Ellinia. He's doing the role of an instructor in place of me. He'll give you all the details about it. Best of luck to you...");
				}
			}
		} else {
			let v = self.askMenu("Any questions about being a Magician?\r\n#L0##bWhat are the basic characteristics of being a Magician?#k#l\r\n#L1##bWhat are the weapons that the Magicians use?#k#l\r\n#L2##bWhat are the armors the Magicians can wear?#k#l\r\n#L3##bWhat are the skills available for Magicians?#k#l");
			if (v == 0) {
				self.sayNext("I'll tell you more about being a Magician. Magicians put high levels of magic and intelligence to good use. They can use the power of nature all around us to kill the enemies, but they are very weak in close combats. The stamina isn't high, either, so be careful and avoid death at all cost.");
				self.sayNext("Since you can attack the monsters from afar, that'll help you quite a bit. Try boosting up the level of INT if you want to attack the enemies accurately with your magic. The higher your intelligence, the better you'll be able to handle your magic...");
			} else if (v == 1) {
				self.sayNext("I'll tell you more about the weapons that Magicians use. Actually, it doesn't mean much for Magicians to attack the opponents with weapons. Magicians lack power and dexterity, so you will have a hard time even defeating a snail.");
				self.sayNext("If we're talking about the magicial powers, then THAT's a whole different story. The weapons that Magicians use are blunt weapons, staff, and wands. Blunt weapons are good for, well, blunt attacks, but...I would not recommend that on Magicians, period...");
				self.sayNext("Rather, staffs and wands are the main weaponry of choice. These weapons have special magicial powers in them, so it enhances the Magicians' effectiveness. It'll be wise for you to carry a weapon with a lot of magicial powers in it...");
			} else if (v == 2) {
				self.sayNext("I'll tell you more about the armors that Magicians can wear. Honestly, the Magicians don't have much armor to wear since they are weak in physiical strength and low in stamina. Its defensive abilities isn't great either, so I don't know if it helps a lot or not...");
				self.sayNext("Some armors, however, have the ability to eliminate the magicial power, so it can guard you from magic attacks. It won't help much, but still better than not warning them at all...so buy them if you have time...");
			} else if (v == 3) {
				self.sayNext("The skills available for Magicians use the high levels of intelligence and magic that Magicians have. Also available are Magic Guard and Magic Armor, which help Magicians with weak stamina prevent from dying.");
				self.sayNext("The offensive skills are #bEnergy Bolt#k and #bMagic Claw#k. First, Energy Bolt is a skill that applies a lot of damage to the opponent with minimal use of MP.");
				self.sayNext("Magic Claw, on the other hand, uses up a lot of MP to attack one opponent TWICE. But, you can only use Energy Bolt once it's more than 1, so keep that in mind. Whatever you choose to do, it's all upto you...");
			}
		}
	} else if (cJob == 0) {
		self.sayNext("Do you want to be a Magician? You need to meet some requirements in order to do so. You need to be at least at #bLevel 8#k. Let's see if you have what it takes to become a Magician...");
		if (self.UserGetLevel() > 7) {//&& self.UserGetINT() > 19
			let nRet = self.askYesNo("You definitely have the look of a Magician. You may not be there yet, but I can see the Magician in you...what do you think? Do you want to become the Magician?");
			if (nRet == 0) self.sayNext("Really? Have to give more thought to it, huh? Take your time, take your time. This is not something you should take lightly...come talk to me once your have made your decision...");
			else if (nRet == 1) {
				if (self.InventoryGetSlotCount(1) > self.InventoryGetHoldCount(1)) {
					self.sayNext("Alright, you're a Magician from here on out, since I, Grendel the Really old, the head Magician, allow you so. It isn't much, but I'll give you a little bit of what I have...");
					let ret = self.InventoryExchange(0, 1372043, 1);
					if (!ret) self.sayNext("Hmmm. Make sure you have a free slot in your inventory tab Equip. I would like to give you a weapon since you job advanced.");
					else {
						self.UserJob(200);
						self.UserIncSP(1, false);
						if (self.UserGetLevel() >= 30) {
							self.sayNext("I think you've made the job advancement way too late. Usually, for beginners under Level 29 that were late in making job advancements, we compensate them with lost Skill Points, that weren't rewarded, but...I think you're a little too late for that. I am so sorry, but there's nothing I can do.");
						} else {
							let nPSP = (self.UserGetLevel() - 8) * 3;
							self.UserIncSP(nPSP, false);
						}
						let incval = Math.floor((Math.random() * (150 - 100 + 1) + 100));
						self.UserIncMMP(incval, false);
						self.sayNext("You have just equipped yourself with much more magicial power. Please keep training and make yourself much better...I'll be watching you from here and there...");
						self.sayNext("I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having ");
						self.sayNext("One more warning. Once you have chosen your job, try to stay alive as much as you can. Once you reach that level, when you die, you will lose your experience level. You wouldn't want to lose your hard-earned experience points, do you?");
						self.sayNext("OK! This is all I can teach you. Go to places, train and better yourself. Find me when you feel like you've done all you can, and need something interesting. I'll be waiting for you here...");
						self.sayNext("Oh, and... if you have any questions about being the Magician, feel free to ask. I don't know EVERYTHING, per se, but I'll help you out with all that I know of. Til then...");
					}
				} else
					self.sayNext("Hmmm... Make sure you have a free space in your inventory tab for Equip. I'm trying to give you a weapon for your work.");
			}
		} else
			self.sayNext("You need more training to be a Magician. In order to be one, you need to train yourself to be more powerful than you are right now. Please come back much stronger.");
	}
	else if ( self.UserGetJob() == 210 ) self.say( "Ahhh... you are... how do you like being a Wizard? You... you seem used to these fire arrows now... please, work hard and train more." ); 
	else if ( self.UserGetJob() == 220 ) self.say( "Ahhh... you are... how do you like being a Wizard? You... you seem used to ice and light now... please, work hard and train more." ); 
	else if ( self.UserGetJob() == 230 ) self.say( "Ahhh... you are... how do you like being a Cleric? You... you seem used to holy magic now... please, work hard and train more." ); 
	else if ( self.UserGetJob() == 211 || self.UserGetJob() == 221 ) self.say( "Ahhh... You finally became a #bMage#k... I knew you wouldn't let me down. How does it feel to be a Mage? Please, work hard and train more." ); 
	else if ( self.UserGetJob() == 231 ) self.say( "Ahhh... You finally became a #bPriest#k... I knew you wouldn't let me down. How does it feel to be a Priest? Please, work hard and train more." ); 
	else self.say( "Would you like to have the power of nature in itself in your hands? It may be a long, hard road to be on, but you'll surely be rewarded in the end, reaching the very top of wizardry..." );
}

function magician_lv200(rank) {
	let info = self.QuestRecordGet(7530);
	if (info == "") {
		self.sayNext("You… you… is it really you? Wow… I remember when you were level 1, it seems like yesterday… and now you're the grand wizard of all magicians, a bona-fide hero of the Maple World.");
		let nRet = self.askYesNo("You are more than good enough to become the face of all magicians here. What do you think? Are you interested in leaving your other self here to show the other magicians what greatness is all about?");
		if (nRet == 0) self.sayNext("I can't believe you said no to that. Let me know if you ever change your mind.");
		else {
			let code = 9901100 + rank - 1;
			let ret = self.RegisterImitatedNPC(code);
			if (!ret) self.sayNext("I don't think you are worthy of leaving your other self here.");
			else {
				self.QuestRecordSet(7530, "1");
				self.sayNext("What do you think? Your other self is now established at the temple of all magicians here! Here's hoping other warriors will be inspired for greatness whenever they look at this..");
			}
		}
	} else if (info == "1") {
		self.sayNext("Numerous adventurers stop by and get inspired by your likeness. Your every move is being watched by others, so set an example of strength, honor, and dedication to everyone that crosses your path. May the wisdom of the elders grant you insight!");
	}
}
'''
