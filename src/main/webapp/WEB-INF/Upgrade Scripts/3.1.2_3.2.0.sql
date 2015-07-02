update `template` set `linkpath`='/a/accounts/create.jsf' where `id`='8'; 

ALTER TABLE `ticket`
ADD COLUMN `MailTitle`  varchar(255) NULL COMMENT 'create by mail. will set this title' AFTER `TITLE`;

--ALTER TABLE `account`
--ADD COLUMN `CERTIFIED`  tinyint(1) NULL AFTER `CLOSEEPOCH`;

  