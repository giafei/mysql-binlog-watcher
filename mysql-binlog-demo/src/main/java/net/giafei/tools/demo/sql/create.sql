CREATE SCHEMA `test_binlog` DEFAULT CHARACTER SET utf8mb4 ;

CREATE TABLE `test_binlog`.`test_table1` (
 `id` INT NOT NULL AUTO_INCREMENT,
 `str_value` VARCHAR(45) NULL,
 `date_value` DATETIME NULL,
 `double_value` DOUBLE NULL,
 PRIMARY KEY (`id`));
