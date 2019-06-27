
INSERT INTO `test_binlog`.`test_table1` (`str_value`, `date_value`, `double_value`) VALUES ('aaa', '2018-05-06 11:22:33', '1.65');
UPDATE `test_binlog`.`test_table1` SET `str_value`='bbb', `date_value`='2018-05-09 11:22:33', `double_value`='2.4' WHERE `id`='1';
DELETE FROM `test_binlog`.`test_table1` WHERE `id`='1';
