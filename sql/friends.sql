ALTER TABLE `orionalpha`.`character` ADD COLUMN `FriendCount` TINYINT UNSIGNED NOT NULL DEFAULT 20 AFTER `PetLockerSN`;

DROP TABLE IF EXISTS `orionalpha`.`friend`;
CREATE TABLE `orionalpha`.`friend` (
  `SN` INT NOT NULL AUTO_INCREMENT,
  `CharacterID` INT UNSIGNED NOT NULL,
  `FriendID` INT UNSIGNED NOT NULL,
  `FriendName` VARCHAR(13) NOT NULL,
  `Flag` TINYINT NULL DEFAULT 0,
  PRIMARY KEY (`SN`),
  INDEX `FriendID` (`FriendID`),
  UNIQUE INDEX `CharacterID_FriendID` (`CharacterID`, `FriendID`),
  CONSTRAINT `FK__characterid` FOREIGN KEY (`CharacterID`) REFERENCES `character` (`CharacterID`) ON DELETE CASCADE,
  CONSTRAINT `FK__friendid` FOREIGN KEY (`FriendID`) REFERENCES `character` (`CharacterID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;