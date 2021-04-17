-- Add an email column as JMS doesn't use KSSN or birthdates
ALTER TABLE `orionalpha`.`users` ADD COLUMN `Email` varchar(45) NOT NULL DEFAULT 'eric@wizet.com' AFTER `LoginID`;

-- Create a new table to store the user's wishlists
DROP TABLE IF EXISTS `orionalpha`.`wishlist`;
CREATE TABLE  `orionalpha`.`wishlist` (
  `SN` int(11) NOT NULL AUTO_INCREMENT,
  `CharacterID` int(11) NOT NULL,
  `CommoditySN` varchar(128) NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0',
  PRIMARY KEY (`SN`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;