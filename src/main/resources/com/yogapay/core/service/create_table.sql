CREATE TABLE `common_cache` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(16) NOT NULL,
	`key` VARCHAR(32) NOT NULL,
	`value` BLOB NOT NULL,
	`time_to_live_seconds` INT(10) UNSIGNED NOT NULL,
	`time_to_idle_seconds` INT(10) UNSIGNED NOT NULL,
	`last_access_time` DATETIME NOT NULL,
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `name_key` (`name`, `key`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `common_task` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	`single` ENUM('1') NULL DEFAULT NULL,
	`locked` ENUM('1') NULL DEFAULT NULL,
	`alias` VARCHAR(50) NULL DEFAULT NULL,
	`runtime` VARCHAR(50) NULL DEFAULT NULL,
	`begin_time` DATETIME NULL DEFAULT NULL,
	`end_time` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `name_single` (`name`, `single`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM
;
