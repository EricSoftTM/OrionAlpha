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
 (1,1,0,'Eric',0,0,20001,30000,99,310,50,50,50,50,999,889,999,999,50,50,0,1337,9986396,100000000,8),
 (2,1,0,'Erica',1,0,21000,31000,42,230,999,999,999,999,30000,30000,30000,30000,5,10,1337,1337,9999990,104040000,2),
 (3,2,0,'Justin',0,0,20000,30030,10,0,5,6,6,8,50,5,50,5,0,0,0,0,0,0,0),
 (4,4,0,'Brookie',1,0,21001,31057,10,0,8,6,5,6,17,5,50,5,0,0,0,0,1337,104040000,0);
/*!40000 ALTER TABLE `character` ENABLE KEYS */;


--
-- Definition of table `givepopularity`
--

DROP TABLE IF EXISTS `givepopularity`;
CREATE TABLE `givepopularity` (
  `SN` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `CharacterID` int(11) NOT NULL DEFAULT 0,
  `TargetID` int(11) NOT NULL DEFAULT 0,
  `LastGivePopularity` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`SN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `givepopularity`
--

/*!40000 ALTER TABLE `givepopularity` DISABLE KEYS */;
/*!40000 ALTER TABLE `givepopularity` ENABLE KEYS */;


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
 (-2,0,-34),
 (-1,-9,0),
 (0,60,33),
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
  `BuyCharacterName` varchar(13) NOT NULL,
  `ExpiredDate` bigint(20) NOT NULL DEFAULT -1,
  PRIMARY KEY (`SN`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `itemlocker`
--

/*!40000 ALTER TABLE `itemlocker` DISABLE KEYS */;
INSERT INTO `itemlocker` (`SN`,`CashItemSN`,`AccountID`,`CharacterID`,`ItemID`,`CommodityID`,`Number`,`BuyCharacterName`,`ExpiredDate`) VALUES 
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
  PRIMARY KEY (`SN`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `itemslotbundle`
--

/*!40000 ALTER TABLE `itemslotbundle` DISABLE KEYS */;
INSERT INTO `itemslotbundle` (`SN`,`ItemSN`,`CashItemSN`,`CharacterID`,`POS`,`ItemID`,`Number`,`TI`,`ExpireDate`) VALUES 
 (3,0,29,4,1,2090000,1,2,1526521585788),
 (5,0,30,4,1,4040000,1,4,1534302467877),
 (6,30,0,1,12,2000000,199,2,3439756800000),
 (9,33,0,1,10,2040000,200,2,3439756800000),
 (10,34,0,1,3,2040001,200,2,3439756800000),
 (11,35,0,1,4,2040002,200,2,3439756800000),
 (12,36,0,1,5,2040003,200,2,3439756800000),
 (13,37,0,1,6,2040004,200,2,3439756800000),
 (14,38,0,1,7,2041001,200,2,3439756800000),
 (15,40,0,1,8,2041000,200,2,3439756800000),
 (16,41,0,1,9,2043001,193,2,3439756800000),
 (18,44,0,1,11,2000001,200,2,3439756800000),
 (19,45,0,4,2,2000002,100,2,3439756800000),
 (20,46,0,1,13,2000003,190,2,3439756800000),
 (21,48,0,1,2,2070000,600,2,3439756800000),
 (22,50,0,1,1,2070005,893,2,3439756800000),
 (23,55,0,1,14,2060000,928,2,3439756800000),
 (24,57,0,1,15,2061000,999,2,3439756800000),
 (25,58,0,1,16,2090000,200,2,3439756800000),
 (26,59,0,1,17,2080000,200,2,3439756800000),
 (27,60,0,1,18,2081000,200,2,3439756800000);
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
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `itemslotequip`
--

/*!40000 ALTER TABLE `itemslotequip` DISABLE KEYS */;
INSERT INTO `itemslotequip` (`SN`,`ItemSN`,`CashItemSN`,`CharacterID`,`POS`,`ItemID`,`RUC`,`CUC`,`I_STR`,`I_DEX`,`I_INT`,`I_LUK`,`I_MaxHP`,`I_MaxMP`,`I_PAD`,`I_MAD`,`I_PDD`,`I_MDD`,`I_ACC`,`I_EVA`,`I_Craft`,`I_Speed`,`I_Jump`,`ExpireDate`) VALUES 
 (1,1,0,1,-5,1042003,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (2,2,0,1,2,1060002,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (14,14,0,3,-5,1040002,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (15,15,0,3,-6,1060002,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (16,16,0,3,-7,1072001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (17,17,0,3,-11,1302000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3439756800000),
 (25,0,17,3,-15,1000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526366968780),
 (26,0,18,3,-16,1012000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526366974560),
 (29,20,0,1,-1,1002140,8,0,997,997,1002,997,0,0,0,0,199,199,201,200,0,31,49,3439756800000),
 (30,21,0,1,5,1322000,8,0,0,0,0,0,0,0,35,0,0,0,0,0,0,0,0,3439756800000),
 (31,22,0,1,1,1332000,8,0,0,0,0,0,0,0,29,0,0,0,0,0,0,0,0,3439756800000),
 (69,0,-29,4,-19,1041001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526521158928),
 (70,0,-30,4,-20,1061001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526521163467),
 (71,0,-31,4,-21,1071000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526521168347),
 (72,0,-22,4,-15,1001000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526511358228),
 (73,29,0,4,-11,1302000,7,0,0,0,0,0,0,0,17,0,0,0,0,0,0,0,0,3439756800000),
 (74,0,-34,4,1,1041005,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1526526773463),
 (76,42,0,1,6,1302000,4,2,2,0,0,0,0,0,21,0,0,0,0,0,0,0,0,3439756800000),
 (77,47,0,1,4,1322008,7,0,0,0,0,0,0,0,39,0,0,0,0,0,0,0,0,3439756800000),
 (78,49,0,1,3,1472000,5,0,0,0,0,0,0,0,10,0,0,0,0,0,0,0,0,3439756800000),
 (79,52,0,1,8,1462000,6,0,0,0,0,0,0,0,41,0,0,0,0,0,0,0,0,3439756800000),
 (80,53,0,1,-11,1452000,6,0,0,0,0,0,0,0,41,0,0,0,0,0,0,0,0,3439756800000),
 (81,56,0,1,7,1372005,6,0,0,0,0,0,0,0,15,22,0,0,0,0,0,0,0,3439756800000);
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
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=latin1;

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
 (14,1,1101007,25),
 (15,1,2301001,20),
 (16,1,1101005,1),
 (17,1,2001002,15),
 (18,1,2001003,2),
 (19,1,2001004,1),
 (20,1,2300000,10),
 (21,1,1301007,5),
 (22,1,1301006,5),
 (23,1,2301003,10),
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
 (34,1,2101003,5),
 (35,1,2001005,1),
 (36,1,4100002,10),
 (37,1,4100000,10),
 (38,1,4100001,10),
 (39,1,2301005,10),
 (40,1,4101003,10),
 (41,1,4001344,10),
 (42,1,3100000,10),
 (43,1,3001004,10),
 (44,1,3101004,10),
 (45,1,3101002,10),
 (46,1,3101005,3),
 (47,1,3101003,3),
 (48,1,3001005,10),
 (49,1,3001003,5),
 (50,1,3000001,20),
 (51,1,3000000,5),
 (52,1,3000002,5),
 (53,1,1201006,3),
 (54,1,2201001,1),
 (55,1,2201005,2),
 (56,1,2201004,2),
 (57,1,2101005,1),
 (58,1,2200000,3),
 (59,1,1201007,1),
 (60,1,3200000,5),
 (61,1,3201002,5),
 (62,1,3201004,3),
 (63,1,3201005,1),
 (64,1,4200000,5),
 (65,1,4101005,1),
 (66,1,4201002,5),
 (67,1,4201003,5),
 (68,1,4201004,5);
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
  `SSN1` int(8) NOT NULL DEFAULT 0,
  `BirthDate` int(8) NOT NULL DEFAULT 19700101 COMMENT 'YYYYMMDD',
  PRIMARY KEY (`AccountID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`AccountID`,`LoginID`,`Password`,`Gender`,`GradeCode`,`BlockReason`,`NexonCash`,`SSN1`,`BirthDate`) VALUES 
 (1,'eric@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',0,0,NULL,100000,0,19700101),
 (2,'justin@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',0,0,NULL,79100,0,19700101),
 (3,'arnah@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',0,0,NULL,100000,0,19700101),
 (4,'brookie@wizet.com','$2a$10$mWwD9CGdJ9JQb3CzsnmhDOvIqvSMHMIBKABWyn1.41JJYCcVFSsE.',1,0,NULL,1283037,0,19700101);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
