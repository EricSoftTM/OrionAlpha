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
NPC: Andre (Hair Salon Assistant)
Script: Kerning City General Hair Stylist
'''

# Post-Beta: 5150002 => Beta: 4050002
self.say("#bScript: hair_kerning2#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
TODO: Port to Python

selectHair = self.askMenu( "Sou Andres, assistente do Don. Todos me chamam de André. Se você tiver um #b#t5150002##k ou #b#t5151002##k, deixe-me trocar seu penteado ...\r\n#b#L0# Corte de cabelo(cupom normal)#l\r\n#L2# Pintar seu cabelo(cupom normal)#l" ); 
if ( selectHair == 0 ) { 
	nRet1 = self.askYesNo( "Se usar o cupom EXP, seu cabelo vai mudar ALEATORIAMENTE com a chance de ganhar um novo estilo experimental criado por mim. Vai usar #b#t5150002##k e realmente mudar o seu estilo?" ); 
	nRet1 = self.askYesNo( "Se usar o cupom normal, seu cabelo vai mudar ALEATORIAMENTE com a chance de ganhar um novo estilo que você mesmo não achava que fosse possível. Vai usar #b#t5150002##k e realmente mudar o seu estilo?" );
	if ( nRet1 == 0 ) self.say( "Percebo... Pense um pouco mais e, se quiser, me procure."); 
	else if ( nRet1 == 1 ) { 
		tHair = target.nHair % 10; 
		if ( target.nGender == 0 ) {
		changeHair1 = 30000 + tHair;
		changeHair2 = 30020 + tHair;
		changeHair3 = 30030 + tHair;
		changeHair4 = 30040 + tHair;
		changeHair5 = 30050 + tHair;
		changeHair6 = 30110 + tHair;
		changeHair7 = 30130 + tHair;
		changeHair8 = 30160 + tHair;
		changeHair9 = 30180 + tHair;
		changeHair10 = 30190 + tHair;
		changeHair11 = 30350 + tHair;
		changeHair12 = 30610 + tHair;
		changeHair13 = 30440 + tHair;
		changeHair14 = 30400 + tHair;	
		
		mHair = self.makeRandAvatar( 5150002, changeHair1, changeHair2, changeHair3, changeHair4, changeHair5, changeHair6, changeHair7, changeHair8, changeHair9, changeHair10, changeHair11, changeHair12, changeHair13, changeHair14 );
		} 
		else if ( target.nGender == 1 ) { 
		changeHair1 = 31000 + tHair;
		changeHair2 = 31010 + tHair;
		changeHair3 = 31020 + tHair;
		changeHair4 = 31040 + tHair;
		changeHair5 = 31050 + tHair;
		changeHair6 = 31060 + tHair;
		changeHair7 = 31090 + tHair;
		changeHair8 = 31120 + tHair;
		changeHair9 = 31130 + tHair;
		changeHair10 = 31140 + tHair;
		changeHair11 = 31330 + tHair;
		changeHair12 = 31700 + tHair;
		changeHair13 = 31620 + tHair;
		changeHair14 = 31610 + tHair;
		
		mHair = self.makeRandAvatar( 5150002, changeHair1, changeHair2, changeHair3, changeHair4, changeHair5, changeHair6, changeHair7, changeHair8, changeHair9, changeHair10, changeHair11, changeHair12, changeHair13, changeHair14 );
		} 
		if ( mHair == 1 ) self.say( "Aqui está o espelho. Seu novo corte! O que você acha? Sei que não é o mais transado, mas me parece muito legal! Volte quando precisar de uma nova mudança!" ); 
		else if ( mHair == -1 ) self.say( "Hum... Tem certeza de que tem o cupom certo? Desculpe, mas nada de corte de cabelo sem ele." ); 
		else if ( mHair == -3 ) self.say( "Me desculpe. Parece que temos um problema aqui no salão. Não acho que possa cortar seu cabelo neste momento. Volte mais tarde." ); 
		else if ( mHair == 0 or mHair == -2 ) self.say( "Me desculpe. Parece que temos um pequeno problema em mudar seu penteado. Por favor, volte daqui a pouco." ); 
	} 
} 

else if ( selectHair == 2 ) { 
	nRet1 = self.askYesNo( "Se usar um cupom normal, seu cabelo irá mudar aleatoriamente. Ainda quer usar #b#t5151002##k e tingir seu cabelo?" ); 
	if ( nRet1 == 0 ) self.say( "Percebo... Pense um pouco mais e, se quiser, me procure."); 
	else if ( nRet1 == 1 ) { 
		cHair = target.nHair; 
		eHair = cHair - ( cHair % 10 ); 

		changeHair1 = eHair; 
		changeHair2 = eHair + 2; 
		changeHair4 = eHair + 3; 
		changeHair3 = eHair + 7; 
		changeHair5 = eHair + 5; 

		if ( target.nGender == 0 ) mHair = self.makeRandAvatar( 5151002, changeHair1, changeHair2, changeHair3, changeHair4, changeHair5 ); 
		else if ( target.nGender == 1 ) mHair = self.makeRandAvatar( 5151002, changeHair1, changeHair2, changeHair3, changeHair4, changeHair5 ); 

		if ( mHair == 1 ) self.say( "Aqui está o espelho. Seu novo corte! O que você acha? Sei que não é o mais transado, mas me parece muito legal! Volte quando precisar de uma nova mudança!" ); 
		else if ( mHair == -1 ) self.say( "Hum... Tem certeza de que tem o cupom certo? Desculpe, mas nada de corte de cabelo sem ele." ); 
		else if ( mHair == -3 ) self.say( "Me desculpe. Parece que temos um problema aqui no salão. Não acho que possa tingir seu cabelo neste momento. Volte mais tarde." ); 
		else if ( mHair == 0 or mHair == -2 ) self.say( "Me desculpe. Parece que temos um pequeno problema em mudar sua cor. Por favor, volte daqui a pouco." ); 
	} 
}
'''