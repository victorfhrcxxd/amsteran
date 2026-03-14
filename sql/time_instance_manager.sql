CREATE TABLE IF NOT EXISTS `time_instance_manager` (
  `objectId` INT NOT NULL,
  `expire_time` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`objectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
