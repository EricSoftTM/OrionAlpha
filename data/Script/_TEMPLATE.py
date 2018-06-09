'''
Using 3 single-quotes creates a multi-line comment, like this.
This is mainly used for our licensing at the beginning of all our scripts.
Below is what will always be put in a multi-line comment at the top of the script.
'''

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

'To create a single-line comment, use a single-quote.'
'This is the comment we typically use for writing the NPC and Script descriptions.'
'You can also use a single-line comment \
across multiple lines \
by using backslashes.'

'NPC: Regular Cab'
'Script: Transports users between the towns of Victoria Island.'

'''
Below are some important things to know coming from JS to Python.

1) Remember that Python will execute all of our scripts in a global scope.
This means we never invoke a function call like you would see in Odin's start(), action(), etc.
Instead, we simply execute all of our code line-by-line right here in the script.

2) Python does not declare variables the same way as you would in JS.
This means we never declare unitialized variables, use var/let/etc., or use type defitions.
In addition, you cannot modify a value from the global scope in Python like you can in JS.
If you wish to modify a variable declared outside of a function within the global scope,
you must use the global keyword:

val = "a"

def foo():
	global val
	val = "b"

print(val)

The above code will modify 'val' from 'a' to 'b'. If you didn't use global val, no changes to the
global scope would have been made.

3) Operators are quite different in Python in many ways:
- We never end our statements with a semi-colan (;) like we would in JS.
- Parentheses aren't actually necessary to use within conditions.
- To define a block of code we don't use curly brackets ({), we use colans (:).
- All code that is contained within the block is indented inside of it - if you indent back,
then that means that the block has ended. Formatting is very important in Python, you must
always indent properly (or use a Python editor to do this for you)!
- When working with conditionals, there is no 'else if', there is only 'if', 'elif', and 'else'.
Remember that whenever using a conditional, you must always define it as a block (e.g 'else:')
- Booleans are a bit different in Python. They are not 'true'/'false', they are 'True'/'False'.
Also, there is no ! operator like there is in JS, so you just do val == False.
- If your conditional is checking if a value is >= x && <= y, you can use the 'in' keyword. (e.g 'if val in (1, 5):')
- Forloops are not declared like in JS. If you're looping from i = 0, i < x, use 'for i in range(0, x):'
- To declare a function, we don't use the keyword 'function', instead we define it by 'def'. (e.g 'def foo():')

4) Adding objects within a string doesn't work like it would in JS. (e.g "you have " + mesos + " mesos").
In Python, you have to basically 'toString' the object by doing 'str(mesos)'. (e.g "you have " + str(mesos) + " mesos")
'''

'As you can see, we still declare and handle arrays the same as you would in JS.'
towns = [["Lith Harbor", 104000000, 120], ["Henesys", 100000000, 100], ["Ellinia", 101000000, 100], ["Kerning City", 103000000, 80]]

'This is an example of how to define and use a function with parameters.'
'NOTE: You must ALWAYS DECLARE THE FUNCTION FIRST. That is, define the function above, and call it from below.'
def goTown(mapName, mapNum, fee):
	'Our scripts are stateless - they wait for an async response back from the user before continuing execution.'
	'In this case, we ask the user to select yes or no, and assign that response to ret.'
	ret = self.askYesNo("You don't have anything else to do here, huh? Do you really want to go to #b" + mapName + "#k? It'll cost you #b" + str(fee) + " mesos#k.")
	'If ret is 1 (yes), then we proceed to transfer the user to the selected town.'
	if ret == 1:
		if (self.userIncMoney(-fee, True)):
			self.registerTransferField(mapNum, "")
		else:
			self.say("I'm afraid you don't have enough money, you'll have to walk.")
	else:
		self.say("There's a lot to see in this town. Come back back when you want to go elsewhere.")

'This continues the global scope and is where the NPC starts execution at.'
self.sayNext("Hi, i'm the Regular Cab. You came to the right place if you want to go to another town fast and secure. Your satisfaction is guaranteed.")
menu = "Choose your destination, the fare leties from place to place.\r\n#b"
'We forloop from i=0, to i=towns.length. In each loop, we append the town selections to menu.'
for i in range(0, len(towns)):
	menu += "#L" + str(i) + "#" + towns[i][0] + " (" + str(towns[i][2]) + " mesos)#l\r\n"
'Just like we do above in goTown, we assign the response of the menu selection to sel.'
sel = self.askMenu(menu)
'Now we make sure the selection response is valid, and proceed to call goTown to move the user.'
if sel in (0, len(towns)):
	'Again, accessing array arguments is the same as you would in JS.'
	goTown(towns[sel][0], towns[sel][1], towns[sel][2])

'''
Another example to show the differences that are commonly seen in Odin is that you can't
send multiple "sendOk" (or any sendXXX) at once. If you do, it sends them all at once and
will just crash your client.

Not in OrionAlpha! Because it is line-by-line async script execution, we can do the following:

self.sayNext("Hi!")
self.sayNext("Welcome to OrionAlpha!")
ret = self.askYesNo("Are you ready to start your journey?")
if ret == 1:
	self.registerTransferField(map, "")

Each call will wait for a response. When the user clicks 'Next', the npc will then execute the
second sayNext call. When they click next again, the npc will execute the askYesNo call.
Finally, if the response of the askYesNo call was 'yes', then it will warp the user. Ta-da!
'''

'Last, but not least, is that none of our scripts EVER require the need of a dispose() call. :)'