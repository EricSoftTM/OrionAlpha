-- Add the Cash inventory slots
ALTER TABLE `orionalpha`.`inventorysize` ADD COLUMN `CashCount` INT(11) NOT NULL DEFAULT 96 AFTER `EtcCount`;

-- Save the currently active pet on the user
ALTER TABLE `orionalpha`.`character` ADD COLUMN `PetLockerSN` bigint(20) NOT NULL DEFAULT 0 AFTER `Portal`;

-- Create a new inventory table for Pets
DROP TABLE IF EXISTS `orionalpha`.`itemslotpet`;
CREATE TABLE  `orionalpha`.`itemslotpet` (
  `CashItemSN` bigint(10) unsigned NOT NULL,
  `POS` int(11) NOT NULL DEFAULT 0,
  `CharacterID` int(11) NOT NULL,
  `ItemID` int(11) NOT NULL,
  `PetName` tinytext NOT NULL,
  `PetLevel` int(11) NOT NULL DEFAULT 1,
  `Tameness` int(11) NOT NULL DEFAULT 0,
  `Repleteness` int(11) NOT NULL DEFAULT 100,
  `DeadDate` bigint(20) NOT NULL DEFAULT -1,
  PRIMARY KEY (`CashItemSN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;