
CREATE TABLE IF NOT EXISTS `channels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `linkHash` char(40) NOT NULL,
  `link` varchar(511) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `channels_linkHash_uindex` (`linkHash`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;




CREATE TABLE IF NOT EXISTS `items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `link` varchar(511) NOT NULL,
  `desc` text,
  `text` text,
  `date` datetime DEFAULT NULL,
  `channelId` int(11) NOT NULL,
  `linkHash` char(40) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `items_linkHash_uindex` (`linkHash`),
  KEY `items_channels_id_fk` (`channelId`),
  CONSTRAINT `items_channels_id_fk` FOREIGN KEY (`channelId`) REFERENCES `channels` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
