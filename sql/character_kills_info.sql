CREATE TABLE IF NOT EXISTS `character_kills_info` (
  `cycle` INT NOT NULL AUTO_INCREMENT,
  `cycle_start` BIGINT NOT NULL DEFAULT 0,
  `winner_pvpkills` INT NOT NULL DEFAULT 0,
  `winner_pvpkills_count` INT NOT NULL DEFAULT 0,
  `winner_pkkills` INT NOT NULL DEFAULT 0,
  `winner_pkkills_count` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`cycle`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `character_kills_snapshot` (
  `charId` INT NOT NULL,
  `pvpkills` INT NOT NULL DEFAULT 0,
  `pkkills` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
