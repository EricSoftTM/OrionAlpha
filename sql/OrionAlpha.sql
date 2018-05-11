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
  `AccountID` int(11) DEFAULT NULL,
  `WorldID` tinyint(2) unsigned NOT NULL,
  `CharacterName` varchar(13) NOT NULL,
  `Gender` tinyint(1) unsigned NOT NULL,
  `Skin` tinyint(2) unsigned NOT NULL,
  `Face` int(11) unsigned NOT NULL,
  `Hair` int(11) unsigned NOT NULL,
  `Level` tinyint(4) unsigned NOT NULL,
  `Job` int(11) unsigned NOT NULL,
  `STR` int(11) unsigned NOT NULL,
  `DEX` int(11) unsigned NOT NULL,
  `INT` int(11) unsigned NOT NULL,
  `LUK` int(11) unsigned NOT NULL,
  `HP` int(11) unsigned NOT NULL,
  `MP` int(11) unsigned NOT NULL,
  `MaxHP` int(11) unsigned NOT NULL,
  `MaxMP` int(11) unsigned NOT NULL,
  `AP` int(11) unsigned NOT NULL,
  `SP` int(11) unsigned NOT NULL,
  `EXP` int(11) unsigned NOT NULL,
  `POP` int(11) unsigned NOT NULL,
  `Money` int(11) unsigned NOT NULL,
  `Map` int(11) unsigned NOT NULL,
  `Portal` tinyint(4) unsigned NOT NULL,
  PRIMARY KEY (`CharacterID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `character`
--

/*!40000 ALTER TABLE `character` DISABLE KEYS */;
INSERT INTO `character` (`CharacterID`,`AccountID`,`WorldID`,`CharacterName`,`Gender`,`Skin`,`Face`,`Hair`,`Level`,`Job`,`STR`,`DEX`,`INT`,`LUK`,`HP`,`MP`,`MaxHP`,`MaxMP`,`AP`,`SP`,`EXP`,`POP`,`Money`,`Map`,`Portal`) VALUES 
 (1,1,0,'Eric',0,0,20001,30000,30,110,100,200,300,400,999,999,999,999,5,10,1337,1337,9999990,104040000,2),
 (2,1,0,'Erica',1,0,21000,31000,42,230,999,999,999,999,30000,30000,30000,30000,5,10,1337,1337,9999990,104040000,2);
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
 (2,24,24,24,24);
/*!40000 ALTER TABLE `inventorysize` ENABLE KEYS */;


--
-- Definition of table `iteminitsn`
--

DROP TABLE IF EXISTS `iteminitsn`;
CREATE TABLE `iteminitsn` (
  `WorldID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ItemSN` bigint(20) unsigned NOT NULL DEFAULT 1,
  `CashItemSN` bigint(20) unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`WorldID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `iteminitsn`
--

/*!40000 ALTER TABLE `iteminitsn` DISABLE KEYS */;
INSERT INTO `iteminitsn` (`WorldID`,`ItemSN`,`CashItemSN`) VALUES 
 (0,4,4),
 (1,1,1);
/*!40000 ALTER TABLE `iteminitsn` ENABLE KEYS */;


--
-- Definition of table `itemslotbundle`
--

DROP TABLE IF EXISTS `itemslotbundle`;
CREATE TABLE `itemslotbundle` (
  `SN` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `ItemSN` bigint(10) unsigned NOT NULL DEFAULT 0,
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
  `ItemSN` bigint(10) unsigned NOT NULL DEFAULT 0,
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
  PRIMARY KEY (`SN`),
  UNIQUE KEY `ItemSN` (`ItemSN`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `itemslotequip`
--

/*!40000 ALTER TABLE `itemslotequip` DISABLE KEYS */;
INSERT INTO `itemslotequip` (`SN`,`ItemSN`,`CashItemSN`,`CharacterID`,`POS`,`ItemID`,`RUC`,`CUC`,`I_STR`,`I_DEX`,`I_INT`,`I_LUK`,`I_MaxHP`,`I_MaxMP`,`I_PAD`,`I_MAD`,`I_PDD`,`I_MDD`,`I_ACC`,`I_EVA`,`I_Craft`,`I_Speed`,`I_Jump`,`ExpireDate`) VALUES 
 (1,1,0,1,-1,1002140,7,0,999,999,999,999,999,999,999,999,999,999,999,999,999,999,999,-1),
 (2,2,0,1,-5,1042003,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1),
 (3,3,0,1,-11,1302000,7,0,0,0,0,0,0,0,0,0,1337,1337,0,0,0,0,0,-1),
 (4,0,0,1,-11,1302000,7,0,0,0,0,0,0,0,0,0,1337,1337,0,0,0,0,0,-1);
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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

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
 (15,1,2301001,20);
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
  PRIMARY KEY (`AccountID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`AccountID`,`LoginID`,`Password`,`Gender`,`GradeCode`,`BlockReason`) VALUES 
 (1,'admin@maplestory.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',0,0,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
