CREATE TABLE IF NOT EXISTS `characters_dressme_data` (
  `obj_Id` int(11) NOT NULL,
  `armor_skins` varchar(255) DEFAULT '',
  `armor_skin_option` int(11) DEFAULT 0,
  `weapon_skins` varchar(255) DEFAULT '',
  `weapon_skin_option` int(11) DEFAULT 0,
  `hair_skins` varchar(255) DEFAULT '',
  `hair_skin_option` int(11) DEFAULT 0,
  `face_skins` varchar(255) DEFAULT '',
  `face_skin_option` int(11) DEFAULT 0,
  `shield_skins` varchar(255) DEFAULT '',
  `shield_skin_option` int(11) DEFAULT 0,
  PRIMARY KEY (`obj_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
