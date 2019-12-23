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
NPC: Denma the Owner
Script: Henesys VIP Plastic Surgery
'''

teye = (self.userGetFace() / 100) % 10 * 100
changeFace1 = 20000 + teye
changeFace2 = 20001 + teye
changeFace3 = 20002 + teye
changeFace4 = 20003 + teye
changeFace5 = 20004 + teye
changeFace6 = 20005 + teye
changeFace7 = 20006 + teye
changeFace8 = 20007 + teye
changeFace9 = 20008 + teye
changeFace10 = 20012 + teye
changeFace11 = 20014 + teye

if self.userGetGender() == 1:
    changeFace1 += 1000
    changeFace2 += 1000
    changeFace3 += 1000
    changeFace4 += 1000
    changeFace5 += 1000
    changeFace6 += 1000
    changeFace7 += 1000
    changeFace8 += 1000
    changeFace0 += 1000
    changeFace10 += 1000
    changeFace11 += 1000

# Post-Beta: 5152001 => Beta: 4052001
face = self.askAvatar("Let's see... I can turn your face into something totally new. You want to try? By #bHenesys Cosmetic Surgery Coupon (Premium)#k, you can get a look of your own. Calmly choose a face of your choice ...", 4052001, [changeFace1, changeFace2, changeFace3, changeFace4, changeFace5, changeFace6, changeFace7, changeFace8, changeFace9, changeFace10, changeFace11])
if face == 1:
	self.say("Okay, the procedure is over. Look, here is a mirror. What do you think? Even I admit this is a work of art... haha, well, call me when you get sick of this new look, okay?")
elif face == -1:
	self.say("Hmm... Looks like you don't have the specific coupon for this place... Sorry to say, but without the coupon, no plastic surgery for you.")
elif face == -3:
	self.say("Hmm... Looks like we have a problem here at the hospital, and I feel I can't continue the procedure right away. Please come back later.")
elif face == 0 or face == -2:
	self.say("Hmm... There seems to be a problem with the procedure here. Please come back later.")