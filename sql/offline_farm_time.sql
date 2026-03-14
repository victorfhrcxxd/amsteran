ALTER TABLE `characters` ADD COLUMN `offline_farm_end_time` BIGINT NOT NULL DEFAULT 0;
ALTER TABLE `characters` ADD COLUMN `offline_farm_type` INT NOT NULL DEFAULT 0;
