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
NPC: Don Giovanni (Hair Salon Owner)
Script: Kerning City VIP Hair Stylist
'''

# Post-Beta: 5150003 => Beta: 4050003
self.say("#bScript: hair_kerning1#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

selectHair = self.askMenu( "Oi! Sou Don Giovanni, o chefe do salão de beleza! Se você tiver #b#t5150003##k, #b#t5151003##k, por que não me deixa cuidar do resto? Decida o que quer fazer com seu cableo...\r\n#b#L0# Mudar o estilo (cupom VIP)#l\r\n#L1# Tingir seu cabelo (cupom VIP)#l" ); 
if ( selectHair == 0 ) { 
	tHair = target.nHair % 10; 
	if ( target.nGender == 0 ) { 
		changeHair1 = 30030 + tHair; 
		changeHair2 = 30020 + tHair; 
		changeHair3 = 30000 + tHair; 
		changeHair4 = 30780 + tHair; 
	
		changeHair5 = 30130 + tHair; 
		changeHair6 = 30350 + tHair; 
		changeHair7 = 30190 + tHair; 
		changeHair8 = 30110 + tHair; 
		changeHair9 = 30180 + tHair; 
		changeHair10 = 30050 + tHair; 
		changeHair11 = 30040 + tHair; 
		changeHair12 = 30160 + tHair; 

		mHair = self.askAvatar( "Posso mudar o estilo do seu cabelo para alguma coisa totalmente nova. Já não está enjoado do seu cabelo? Posso fazer um novo corte com #b#t5150003##k. Escolha o estilo de acordo com seu gosto.", 5150003, changeHair1, changeHair2, changeHair3, changeHair4, changeHair5, changeHair6, changeHair7, changeHair8, changeHair9, changeHair10, changeHair11, changeHair12 ); 
	} 
	else if ( target.nGender == 1 ) { 
		changeHair1 = 31050 + tHair; 
		changeHair2 = 31040 + tHair; 
		changeHair3 = 31000 + tHair; 
		changeHair4 = 31760 + tHair; 
	
		changeHair5 = 31060 + tHair; 
		changeHair6 = 31090 + tHair; 
		changeHair7 = 31330 + tHair; 
		changeHair8 = 31020 + tHair; 
		changeHair9 = 31130 + tHair; 
		changeHair10 = 31120 + tHair; 
		changeHair11 = 31140 + tHair; 
		changeHair12 = 31010 + tHair; 

		mHair = self.askAvatar( "Posso mudar o estilo do seu cabelo para alguma coisa totalmente nova. Já não está enjoado do seu cabelo? Posso fazer um novo corte com #b#t5150003##k. Escolha o estilo de acordo com seu gosto.", 5150003, changeHair1, changeHair2, changeHair3, changeHair4, changeHair5, changeHair6, changeHair7, changeHair8, changeHair9, changeHair10, changeHair11, changeHair12 ); 
	} 
	if ( mHair == 1 ) self.say( "Certo, olhe seu novo corte de cabelo. O que você acha? Mesmo eu admito que isso é uma obra-de-arte! HAHAHA. Me procure quando quiser um novo corte de cabelo. Eu cuido do resto!" ); 
	else if ( mHair == -1 ) self.say( "Hum... Parece que você não tem o cupom certo... Pena, não posso cortar seu cabelo sem ele. Desculpe, colega." ); 
	else if ( mHair == -3 ) self.say( "Me desculpe. Parece que temos um problema aqui no salão. Não acho que possa cortar seu cabelo neste momento. Volte mais tarde." ); 
	else if ( mHair == 0 or mHair == -2 ) self.say( "Me desculpe. Parece que temos um pequeno problema em mudar seu penteado. Por favor, volte daqui a pouco." ); 
} 
else if ( selectHair == 1 ) { 
	cHair = target.nHair; 
	eHair = cHair - ( cHair % 10 ); 

	changeHair1 = eHair; 
	changeHair2 = eHair + 2; 
	changeHair4 = eHair + 3; 
	changeHair3 = eHair + 7; 
	changeHair5 = eHair + 5; 

	mHair = self.askAvatar( "Posso mudar a cor do seu cabelo para alguma coisa totalmente nova. Já não está enjoado do seu cabelo? Posso tingir seu cabelo se tiver #b#t5151003##k. Escolha a cor de seu gosto!", 5151003, changeHair1, changeHair2, changeHair3, changeHair4, changeHair5 ); 

	if ( mHair == 1 ) self.say( "Certo, olhe sua nova cor de cabelo. O que você acha? Mesmo eu admito que isso é uma obra-de-arte! HAHAHA. Me procure quando quiser um novo corte de cabelo. Eu cuido do resto!" ); 
	else if ( mHair == -1 ) self.say( "Hum... Parece que você não tem o cupom certo... Pena, não posso tingir seu cabelo sem ele. Desculpe, colega." ); 
	else if ( mHair == -3 ) self.say( "Me desculpe. Parece que temos um problema aqui no salão. Não acho que possa tingir seu cabelo neste momento. Volte mais tarde." ); 
	else if ( mHair == 0 or mHair == -2 ) self.say( "Me desculpe. Parece que temos um pequeno problema em mudar seu penteado. Por favor, volte daqui a pouco." ); 
}
'''
