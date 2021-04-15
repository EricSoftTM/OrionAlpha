-- Add the Cash inventory slots
ALTER TABLE `orionalpha`.`inventorysize` ADD COLUMN `CashCount` INT(11) NOT NULL DEFAULT 96 AFTER `EtcCount`;

-- Create a new inventory table for Pets
-- TODO