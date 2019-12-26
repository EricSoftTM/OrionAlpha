# OrionAlpha
OrionAlpha - A Nexon Replica Emulator Project

----------------------------------------------------------------------
## Resources
 * You can download the game and client [here](https://mega.nz/#!O9Vy3C7Q!0FsLIilRwzImzjUY_9MxOqtvA4wuMn0SDWE65BkGHZk)
 * To emulate a korean locale in the client, you can download LocaleEmulator [here](https://mega.nz/#!T5t00IwA!YByix3DVt-_Pi0IpU-OwUnvhCDyZEPz4JQ6S-kbYHks)
 * You can download the named client IDB [here](https://mega.nz/#!KgdRna6Q!T5Op7_b_JF62QEvHqeYFp2NJcEYeoigqFdOHpREf5pI)
## Requirements/Dependencies
 * Java JDK (11 or higher)
 * javax.json (or Java EE)
 * Netty (4.1.31 or higher)
 * HikariCP (3.1.0 or higher)
 * MariaDB Connector/J (2.2.3 or higher)
 * slf4j (1.7.4 or higher)
 * Jython (2.7.1 or higher)
 * favr.lib.BCrypt (0.9.0 or higher)
 ----------------------------------------------------------------------
 ## Architecture
 The OrionAlpha Emulator is split up into two parts: *Login*, and *Game*, each executing on their own thread. 
 
 **Login** is the central server which will have connectivity to each world and can migrate you back and forth. 
 
 **Game** is designed to be each world, and takes the JVM argument `-DgameID=X` to define which world it is. Each Game JVM that controls the world will also control all of its channels (thus, no multi-jvm here).
 
 ----------------------------------------------------------------------
 ## Server Configuration
 Located within the root of the emulator are a few configuration files:
  * Game0, Game1, etc is used to configure each World.
  * Login is used to configure the Login.
  * Shop is used to configure the Cash Shop.
  * Database is used to configure the connection to the database.
  
  The configuration is done within JSON; each property is defined as a key (string), and a value (int or string).
  Below will further explain the defintions of most of the properties used.
  
  Login/Game/Shop:
  * `port` -> The port to be binded for the server's connection acceptor.
  * `centerPort` -> The private port of your login server that connects JVMs together.
  * `PublicIP` -> The public IP address users will connect to.
  * `PrivateIP` -> The private IP address that connects the JVMs together.
  
  Database:
  * `dbPort` -> The port to connect to your database.
  * `dbGameWorld` -> The Schema name of the active database that the emulator connects to.
  * `dbGameWorldSource` -> The IP/hostname to connect to your database.
  * `dbGameWorldInfo` -> The username/password of your database, separated by comma. (e.g "root,password")
  
  Game:
  * `gameWorldId` -> The ID of the world (0 = Scania, 1 = Bera, etc).
  * `channelNo` -> The number of channels for the world.
  * `incExpRate` -> The server's Experience Rate modifier. We use Nexon's standard. (e.g 100 = 1x, 250 = 2.5x, etc)
  * `incMesoRate` -> The server's Meso Rate modifier (Nexon standard).
  * `incDropRate` -> The server's Drop Rate modifier (Nexon standard).
  * `worldName` -> The name of the world. This is sent to and displayed in the login server.
  ----------------------------------------------------------------------
  ## Client Modifications
  Even knowing there's barely anything to edit in such an early version, below are helpful client edits.
  
  ### Modifying IP
  If you intend to use OrionAlpha, you can change the IP in either our client or the clean client.
   * Our client's default IP is `127.0.0.1`
   * Nexon's default IP's are `218.153.9.172` and `218.153.9.173`
  
  ### Enable Multi-Client
  Allow multiple clients to be open/executed simultaneously.
   * Change the instruction at address `005872D9` to `jmp 0058732D`
  
  ### Enable Window Mode
  Forces the client to execute only in Window Mode, and not Full Screen. 
  * Change the instruction at address `00589F92` to `mov dword ptr [esp+0x84], 0x0`
  
  ### Modifying Client Resolution
  Allows you to make the dimensions of the game bigger, for example 1024x768.
  * Change the instruction at address `0058A05E` to `push 0x320` where `0x320` is the Width
  * Change the instruction at address `0058A04F` to `push 0x258` where `0x258` is the Height
  
  ### Activate Chat Repeat Bypass
  Allows you to send the same message into the chat more than 3 times without any issue.
  * Change the instruction at address `00444261` to `jmp 004442A4`
  
  ### Activate Chat Spam Bypass
  Allows you to spam messages constantly without having to wait 2 seconds.
  * Change the instruction at address `004442C8` to `jmp 0044431B`
  
  ### Enable Infinite Text
  Allows you to type as many characters as you want into a single message, literally.
  * Change the instruction at address `0051E38D` to `mov dword ptr [esp+0xC4], 0xFF` where `0xFF` is the maximum
  
  ### Enable Swear Filter
  Allows you to enter curse words without getting a pop-up and restricting your message from sending.
  * Change the instruction at address `004441B8` to `jmp 004441ED`
  
  ### Remove Nexon ADs
  Allows you to disable the ad balloons after closing the client because they're annoying.
  * Change the instruction at address `005878A5` to `nop`
  
  ### Enable Droppable NX
  Allows you to drop cash NX items like any other item.
  * Change the instruction at address `0047965A` to `nop`
  * Change the instruction at address `00479666` to `nop`
  
  ### Re-Enable Admin Actions
  Restores the ability to allow GM/Admins to drop items, mesos, etc.
  * Change the instruction at address `004795D2` to `jmp 004795E9`
  * Change the instruction at address `005002E7` to `jmp 00500318`
  
  ### Modifying Damage Cap
  Allows you to extend the damage cap up to a maximum of `32,767`.
  * Change the instruction at address `005C3E98` to `13337.0` where `13337.0` is the new cap
  
  ### Modifying Meso Cap
  Allows you to drop meso bags exceeding 50,000 by setting a new cap.
  * Change the instruction at address `005003CE` to `cmp eax, 0xC350` where `0xC350` is the new max
  * Change the instruction at address `005003D5` to `cmp eax, 0xC350` where `0xC350` is the new max
  
