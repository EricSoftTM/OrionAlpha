-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.5.5-10.2.14-MariaDB


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema orionalpha
--

CREATE DATABASE IF NOT EXISTS orionalpha;
USE orionalpha;

--
-- Definition of table `character`
--

DROP TABLE IF EXISTS `character`;
CREATE TABLE `character` (
  `CharacterID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `AccountID` int(11) NOT NULL,
  `WorldID` tinyint(2) unsigned NOT NULL DEFAULT 0,
  `CharacterName` varchar(13) NOT NULL,
  `Gender` tinyint(1) unsigned DEFAULT 0,
  `Skin` tinyint(2) unsigned DEFAULT 0,
  `Face` int(11) unsigned DEFAULT 0,
  `Hair` int(11) unsigned DEFAULT 0,
  `Level` tinyint(4) unsigned DEFAULT 1,
  `Job` int(11) unsigned DEFAULT 0,
  `STR` int(11) unsigned DEFAULT 4,
  `DEX` int(11) unsigned DEFAULT 4,
  `INT` int(11) unsigned DEFAULT 4,
  `LUK` int(11) unsigned DEFAULT 4,
  `HP` int(11) unsigned DEFAULT 50,
  `MP` int(11) unsigned DEFAULT 5,
  `MaxHP` int(11) unsigned DEFAULT 50,
  `MaxMP` int(11) unsigned DEFAULT 5,
  `AP` int(11) unsigned DEFAULT 0,
  `SP` int(11) unsigned DEFAULT 0,
  `EXP` int(11) unsigned DEFAULT 0,
  `POP` int(11) unsigned DEFAULT 0,
  `Money` int(11) unsigned DEFAULT 0,
  `Map` int(11) unsigned DEFAULT 0,
  `Portal` tinyint(4) unsigned DEFAULT 0,
  PRIMARY KEY (`CharacterID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `character`
--

/*!40000 ALTER TABLE `character` DISABLE KEYS */;
INSERT INTO `character` (`CharacterID`,`AccountID`,`WorldID`,`CharacterName`,`Gender`,`Skin`,`Face`,`Hair`,`Level`,`Job`,`STR`,`DEX`,`INT`,`LUK`,`HP`,`MP`,`MaxHP`,`MaxMP`,`AP`,`SP`,`EXP`,`POP`,`Money`,`Map`,`Portal`) VALUES 
 (1,1,0,'Eric',0,0,20001,30000,38,110,100,200,300,400,986,999,999,999,5,13,1337,1337,9999990,104040000,17),
 (2,1,0,'Erica',1,0,21000,31000,42,230,999,999,999,999,30000,30000,30000,30000,5,10,1337,1337,9999990,104040000,2),
 (3,2,0,'Justin',0,0,20000,30030,10,0,5,6,6,8,50,5,50,5,0,0,0,0,0,0,0),
 (4,4,0,'Brookie',1,0,21001,31057,10,0,8,6,5,6,50,5,50,5,0,0,0,0,0,0,0);
/*!40000 ALTER TABLE `character` ENABLE KEYS */;


--
-- Definition of table `inventorysize`
--

DROP TABLE IF EXISTS `inventorysize`;
CREATE TABLE `inventorysize` (
  `CharacterID` int(11) NOT NULL DEFAULT 0,
  `EquipCount` int(11) NOT NULL DEFAULT 24,
  `ConsumeCount` int(11) NOT NULL DEFAULT 24,
  `InstallCount` int(11) NOT NULL DEFAULT 24,
  `EtcCount` int(11) NOT NULL DEFAULT 24,
  PRIMARY KEY (`CharacterID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inventorysize`
--

/*!40000 ALTER TABLE `inventorysize` DISABLE KEYS */;
INSERT INTO `inventorysize` (`CharacterID`,`EquipCount`,`ConsumeCount`,`InstallCount`,`EtcCount`) VALUES 
 (1,24,24,24,24),
 (2,24,24,24,24),
 (3,24,24,24,24),
 (4,24,24,24,24);
/*!40000 ALTER TABLE `inventorysize` ENABLE KEYS */;


--
-- Definition of table `iteminitsn`
--

DROP TABLE IF EXISTS `iteminitsn`;
CREATE TABLE `iteminitsn` (
  `WorldID` int(11) NOT NULL AUTO_INCREMENT,
  `ItemSN` bigint(20) NOT NULL DEFAULT 1,
  `CashItemSN` bigint(20) NOT NULL DEFAULT 1,
  PRIMARY KEY (`WorldID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `iteminitsn`
--

/*!40000 ALTER TABLE `iteminitsn` DISABLE KEYS */;
INSERT INTO `iteminitsn` (`WorldID`,`ItemSN`,`CashItemSN`) VALUES 
 (-2,0,-14),
 (-1,-9,0),
 (0,26,22),
 (1,1,1);
/*!40000 ALTER TABLE `iteminitsn` ENABLE KEYS */;


--
-- Definition of table `itemlocker`
--

DROP TABLE IF EXISTS `itemlocker`;
CREATE TABLE `itemlocker` (
  `SN` bigint(10) NOT NULL AUTO_INCREMENT,
  `CashItemSN` bigint(20) NOT NULL,
  `AccountID` int(11) DEFAULT NULL,
  `CharacterID` int(11) DEFAULT NULL,
  `ItemID` int(11) NOT NULL DEFAULT 0,
  `CommodityID` int(11) NOT NULL DEFAULT 0,
  `Number` int(11) NOT NULL DEFAULT 0,
  `BuyCharacterID` varchar(13) NOT NULL,
  `ExpiredDate` bigint(20) NOT NULL DEFAULT -1,
  PRIMARY KEY (`SN`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `itemlocker`
--

/*!40000 ALTER TABLE `itemlocker` DISABLE KEYS */;
INSERT INTO `itemlocker` (`SN`,`CashItemSN`,`AccountID`,`CharacterID`,`ItemID`,`CommodityID`,`Number`,`BuyCharacterID`,`ExpiredDate`) VALUES 
 (1,-3,2,3,1000000,20000004,1,'Justin',1526358188165),
 (2,-5,2,3,1002186,20000000,1,'Justin',1526360158528);
/*!40000 ALTER TABLE `itemlocker` ENABLE KEYS */;


--
-- Definition of table `itemslotbundle`
--

DROP TABLE IF EXISTS `itemslotbundle`;
CREATE TABLE `itemslotbundle` (
  `SN` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `ItemSN` bigint(20) NOT NULL DEFAULT 0,
  `CashItemSN` bigint(20) DEFAULT 0,
  `CharacterID` int(11) NOT NULL,
  `POS` int(11) NOT NULL DEFAULT 0,
  `ItemID` int(11) NOT NULL DEFAULT 0,
  `Number` int(11) NOT NULL DEFAULT 0,
  `TI` int(11) NOT NULL DEFAULT 0,
  `ExpireDate` bigint(20) NOT NULL DEFAULT -1,
  PRIMARY KEY (`SN`) USING BTREE,
  UNIQUE KEY `ItemSN` (`ItemSN`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `itemslotbundle`
--

/*!40000 ALTER TABLE `itemslotbundle` DISABLE KEYS */;
/*!40000 ALTER TABLE `itemslotbundle` ENABLE KEYS */;


--
-- Definition of table `itemslotequip`
--

DROP TABLE IF EXISTS `itemslotequip`;
CREATE TABLE `itemslotequip` (
  `SN` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `ItemSN` bigint(20) NOT NULL DEFAULT 0,
  `CashItemSN` bigint(20) DEFAULT 0,
  `CharacterID` int(11) DEFAULT NULL,
  `POS` int(11) NOT NULL DEFAULT 0,
  `ItemID` int(11) NOT NULL DEFAULT 0,
  `RUC` int(11) NOT NULL DEFAULT 0,
  `CUC` int(11) NOT NULL DEFAULT 0,
  `I_STR` int(11) NOT NULL DEFAULT 0,
  `I_DEX` int(11) NOT NULL DEFAULT 0,
  `I_INT` int(11) NOT NULL DEFAULT 0,
  `I_LUK` int(11) NOT NULL DEFAULT 0,
  `I_MaxHP` int(11) NOT NULL DEFAULT 0,
  `I_MaxMP` int(11) NOT NULL DEFAULT 0,
  `I_PAD` int(11) NOT NULL DEFAULT 0,
  `I_MAD` int(11) NOT NULL DEFAULT 0,
  `I_PDD` int(11) NOT NULL DEFAULT 0,
  `I_MDD` int(11) NOT NULL DEFAULT 0,
  `I_ACC` int(11) NOT NULL DEFAULT 0,
  `I_EVA` int(11) NOT NULL DEFAULT 0,
  `I_Craft` int(11) NOT NULL DEFAULT 0,
  `I_Speed` int(11) NOT NULL DEFAULT 0,
  `I_Jump` int(11) NOT NULL DEFAULT 0,
  `ExpireDate` bigint(20) NOT NULL DEFAULT -1,
  PRIMARY KEY (`SN`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `itemslotequip`
--

/*!40000 ALTER TABLE `itemslotequip` DISABLE KEYS */;
INSERT INTO `itemslotequip` (`SN`,`ItemSN`,`CashItemSN`,`CharacterID`,`POS`,`ItemID`,`RUC`,`CUC`,`I_STR`,`I_DEX`,`I_INT`,`I_LUK`,`I_MaxHP`,`I_MaxMP`,`I_PAD`,`I_MAD`,`I_PDD`,`I_MDD`,`I_ACC`,`I_EVA`,`I_Craft`,`I_Speed`,`I_Jump`,`ExpireDate`) VALUES 
 (1,1,0,1,-5,1042003,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (2,2,0,1,2,1060002,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (7,3,0,1,1,1072001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (8,4,0,1,5,1302000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (14,14,0,3,-5,1040002,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (15,15,0,3,-6,1060002,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (16,16,0,3,-7,1072001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (17,17,0,3,-11,1302000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (25,0,17,3,-15,1000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526366968780),
 (26,0,18,3,-16,1012000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526366974560),
 (28,19,0,1,4,1372000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (29,20,0,1,-1,1002140,8,0,997,997,1002,997,0,0,0,0,199,199,201,200,0,31,49,3439756800000),
 (30,21,0,1,-11,1322000,8,0,0,0,0,0,0,0,35,0,0,0,0,0,0,0,0,3439756800000),
 (31,22,0,1,3,1332000,8,0,0,0,0,0,0,0,29,0,0,0,0,0,0,0,0,3439756800000),
 (36,23,0,4,-5,1041011,7,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,3439756800000),
 (37,24,0,4,-6,1061008,7,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,3439756800000),
 (38,25,0,4,-7,1072038,5,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,3439756800000),
 (39,26,0,4,1,1302000,7,0,0,0,0,0,0,0,17,0,0,0,0,0,0,0,0,3439756800000),
 (44,0,19,4,-15,1001000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526411485798),
 (45,0,20,4,-19,1041001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526411520850),
 (46,0,21,4,-20,1061001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526411527711),
 (47,0,22,4,-21,1071000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526411536850);
/*!40000 ALTER TABLE `itemslotequip` ENABLE KEYS */;


--
-- Definition of table `questperform`
--

DROP TABLE IF EXISTS `questperform`;
CREATE TABLE `questperform` (
  `SN` int(11) NOT NULL AUTO_INCREMENT,
  `CharacterID` int(11) NOT NULL DEFAULT 0,
  `QRKey` varchar(100) NOT NULL DEFAULT '0',
  `QRValue` varchar(100) NOT NULL,
  PRIMARY KEY (`SN`),
  UNIQUE KEY `QRKey` (`QRKey`,`CharacterID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `questperform`
--

/*!40000 ALTER TABLE `questperform` DISABLE KEYS */;
INSERT INTO `questperform` (`SN`,`CharacterID`,`QRKey`,`QRValue`) VALUES 
 (1,1,'hijustin','questsxd');
/*!40000 ALTER TABLE `questperform` ENABLE KEYS */;


--
-- Definition of table `skillrecord`
--

DROP TABLE IF EXISTS `skillrecord`;
CREATE TABLE `skillrecord` (
  `SN` int(11) NOT NULL AUTO_INCREMENT,
  `CharacterID` int(11) NOT NULL DEFAULT 0,
  `SkillID` int(11) NOT NULL DEFAULT 0,
  `Info` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`SN`),
  UNIQUE KEY `SkillID` (`SkillID`,`CharacterID`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `skillrecord`
--

/*!40000 ALTER TABLE `skillrecord` DISABLE KEYS */;
INSERT INTO `skillrecord` (`SN`,`CharacterID`,`SkillID`,`Info`) VALUES 
 (1,1,4101004,10),
 (2,1,1001005,15),
 (3,1,1001004,15),
 (4,1,1001003,10),
 (5,1,1000002,8),
 (6,1,1000001,10),
 (7,1,1000000,16),
 (8,1,1100000,10),
 (9,1,1100001,10),
 (10,1,1100002,10),
 (11,1,1100003,10),
 (12,1,1101004,10),
 (13,1,1101006,5),
 (14,1,1101007,5),
 (15,1,2301001,20),
 (16,1,1101005,1),
 (17,1,2001002,3),
 (18,1,2001003,1),
 (19,1,2001004,1),
 (20,1,2300000,10),
 (21,1,1301007,5),
 (22,1,1301006,5),
 (23,1,2301003,5),
 (24,1,2301002,10),
 (25,1,4201005,5),
 (26,1,2301004,5),
 (27,1,4000000,8),
 (28,1,4001003,5),
 (29,1,4001002,3),
 (30,1,4000001,6),
 (31,1,2100000,6),
 (32,1,2101001,4),
 (33,1,2101002,5),
 (34,1,2101003,5);
/*!40000 ALTER TABLE `skillrecord` ENABLE KEYS */;


--
-- Definition of table `userconnection`
--

DROP TABLE IF EXISTS `userconnection`;
CREATE TABLE `userconnection` (
  `AccountID` int(11) unsigned NOT NULL,
  `World` int(11) unsigned NOT NULL,
  `ConnectIP` varchar(45) NOT NULL,
  PRIMARY KEY (`AccountID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `userconnection`
--

/*!40000 ALTER TABLE `userconnection` DISABLE KEYS */;
/*!40000 ALTER TABLE `userconnection` ENABLE KEYS */;


--
-- Definition of table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `AccountID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `LoginID` varchar(45) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Gender` tinyint(2) unsigned NOT NULL DEFAULT 0,
  `GradeCode` tinyint(2) unsigned NOT NULL DEFAULT 0,
  `BlockReason` tinyint(4) unsigned DEFAULT NULL,
  `NexonCash` int(11) NOT NULL DEFAULT 0,
  `SSN1` varchar(7) NOT NULL DEFAULT '0',
  PRIMARY KEY (`AccountID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`AccountID`,`LoginID`,`Password`,`Gender`,`GradeCode`,`BlockReason`,`NexonCash`,`SSN1`) VALUES 
 (1,'eric@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',0,0,NULL,100000,''),
 (2,'justin@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',0,0,NULL,79100,''),
 (3,'arnah@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',0,0,NULL,100000,''),
 (4,'brookie@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',1,0,NULL,1325337,'0');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
