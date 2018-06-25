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

'NPC: ?? ??'
'Script: ?? ??'

self.say("#bScript: begin2#k\r\n\r\n#rSorry, I am not coded yet.#k")

'''
// 2. use the items  
  script "begin2" {  
  	qr = target.questRecord;  
  	val = qr.getState( 1002 );  

  		inventory = target.inventory;  
  			if ( val == 0 ) {  
  			if ( target.nGender == 0 ) {  
  				self.say( "Ei, você aí! Posso falar com você por um minuto? Hahah! Eu sou #p2000#, um instrutor que ajuda novos viajantes como você." );  
  				self.say( "Quem te falou para fazer ISSO? HAHAHAH! Você é um viajante MUITO curioso! Bom, bom, bom... Eu faço isso porque quero. É isso." );  
  			}  
  			else if ( target.nGender == 1 ) {  
  				self.say( "Ei! Você aí! Você está livre? Hehe... Eu sou #p2000#, o instrutor, e adoro bater papo com #Ggarotos:garotas# legais como você e, claro, ajudar durante o jogo.#I" );  
  				self.say( "Ei! Me dê um minuto do seu tempo. Eu vou lhe dar muitas informações valiosas. Qualquer coisa para uma belezinha como você. Hahaha!!!#I" );  
  			}  
  			self.say( "Certo! Vamos nos divertir! Yahh!" );  
  			val2 = target.nHP / 2;  
  			target.incHP( -val2, 0 );  
  			self.say( "#GSurpreso:Surpresa#? Você não pode ficar com o HP abaixo de 0, eu vou lhe dar uma #r#t2010007##k para comer. Você pode recuperar sua força assim. Abra seu inventário e clique duas vezes nele." );  
  			self.say( "Você terá que comer cada #t2010007# que eu te dei, mas pode recuperar HP ficando aí quieto, então, venha falar comigo quando tiver recuperado sua HP por completo." );  
  			ret = inventory.exchange( 0, 2010007, 1 );  
  			if ( ret == 0 ) self.say( "Você não comeu um pouquinho demais?" );  
  			else qr.set( 1002, "" );  
  		}  
  	else if ( val == 1 and inventory.itemCount( 2010007 ) == 0 and target.nHP == target.nMHP ) {  
  			self.say( "O que você faz se quiser pegar o item? É fácil, certo? Você pode designá-lo como uma #bTecla de Atalho#k no canto inferior direito da tela. Você não sabia disso, sabia? Hahaha!" );  
  			self.say( "Certo! Você aprendeu muito, então toma aqui um presentinho. Você não deveria me agradecer por aprender uma perícia. Use-a quando necessário." );  
  			self.say( "Isto é tudo o que posso te ensinar. É triste, mas tenho que dizer adeus. E tome cuidado por aí. Até mais..." );  
  			target.incEXP( 2, 0 );  
  			ret = inventory.exchange( 0, 2000000, 3, 2000003, 3 );  
  			if ( ret == 0 ) self.say( "Você não comeu um pouquinho demais?" );  
  			qr.setComplete( 1002 );  
  	}  
  	else if ( inventory.itemCount( 2010007 ) > 0 ) self.say( "Venha, coma a #r#t2010007##k que eu te dei~ Abra o inventário e clique na guia #b'Usar'#k, depois clique duas vezes no #t2010000# para pegá-la." );  
  	else if ( target.nHP != target.nMHP ) self.say( "Você não recuperou totalmente sua força. Você comeu mesmo a #t2010007# que eu te dei? Tem certeza?" );  
  	else self.say( "O tempo está ótimo hoje!" );  
  }  
'''