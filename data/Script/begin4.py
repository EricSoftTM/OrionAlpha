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
Author: Brookie
NPC: Peter
Script: Character control
'''

if self.userGetJob() != 0:
    self.sayNext("Essa ?a ?ea de miss?s para aprendizes. Voc?n? ?um aprendiz, ?")
    self.registerTransferField(104000000, "")
else:
    self.sayNext("Chegou t? longe... incr?el! Voc?pode come?r a viajar por a?agora mesmo! Ok, eu te levo at?a pr?ima parada.")
    self.sayNext("Mas vou te dar um conselho: Depois que sair daqui, voc?estar?livre, em lugares com muitos monstros e sem meios para voltar. Bem, ent?, at?depois!")
    self.userIncEXP(3, False)
    self.registerTransferField(40000, "")