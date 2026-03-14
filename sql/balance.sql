CREATE TABLE IF NOT EXISTS `balance` (
  `from_class` INT NOT NULL,
  `to_class` INT NOT NULL,
  `mod_val` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`from_class`, `to_class`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
