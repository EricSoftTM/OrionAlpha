# OrionAlpha
OrionAlpha - A Nexon Replica Emulator Project

----------------------------------------------------------------------
## Resources
 * You can download the game and client [here](https://mega.nz/#!yoVU2CpZ!J0U2UL_8-kV-x996Ui0qt4FgWdPgErMUALrlb59e41Y)
 * To emulate a korean locale in the client, you can download LocaleEmulator [here](https://mega.nz/#!T5t00IwA!YByix3DVt-_Pi0IpU-OwUnvhCDyZEPz4JQ6S-kbYHks)
 * You can download the named client IDB [here](https://mega.nz/#!C8FVnZwY!cnU-PYQadLzmtb9eiUHdr4heWfYb1uS7vbXo3Te4M34)
## Requirements/Dependencies
 * Java JDK (8 or higher)
 * javax.json (or Java EE)
 * Netty (4.1.15 or higher)
 * HikariCP (2.7 or higher)
 * MariaDB Connector/J (2.1 or higher)
 * slf4j (1.7 or higher)
 ----------------------------------------------------------------------
 ## Architecture
 The OrionAlpha Emulator is split up into two parts: Login, and Game, and each execute on their own thread. 
 Login is the sole Login server which will have connectivity to each world and can migrate you back and forth. 
 Game is designed to be each world, and takes the JVM argument -DworldID=X to define which world it is. Each Game JVM that controls the world will also control all of its channels (thus, no multi-jvm here).
 
 ----------------------------------------------------------------------
 ## Server Configuration
 Located within the root of the emulator are a few configuration files:
  * Game0, Game1, etc is used to configure each World.
  * Login is used to configure the Login.
  
  The configuration is done within JSON; each property is defined as a key (string), and a value (int, boolean, etc).
  Below will further explain the defintions of most of the properties used.
  
  * `port` -> The port to be binded for the server's connection acceptor. 
  * `PublicIP` -> The public IP address users will connect to.
  * `PrivateIP` -> The private IP address that connects the JVMs together.
  * `dbGameWorld` -> The Schema name of the active database that the emulator connects to.
  * `dbGameWorldSource` -> The IP and Port for your database.
  * `incEXPRate` -> The server's Experience Rate modifier.
  * `incMoneyRate` -> The server's Meso Rate modifier.
  * `incDropRate` -> The server's Drop Rate modifier.
  ----------------------------------------------------------------------
  ## Client Modifications
  Even knowing there's barely anything to edit in such an early version, below are helpful client edits.
  
  ### Modifying IP
  If you intend to use OrionAlpha, you can change the IP in either our client or the clean client.
   * Our client's default IP is `127.0.0.1`
   * Nexon's default IP is `218.153.9.173`
  
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
  
