create table `CARRIER`(
	ID bigint primary key AUTO_INCREMENT,
	TITLE varchar(100),
	NAME  varchar(100),
	PHONE varchar(100),
	EMAIL varchar(500),
	NOCEMAIL varchar(500),
	BTSID int,
	LOCATION varchar(100), 
	NOTES varchar(500),
	DAS_ID bigint, 
	
	constraint `DAS_CARRIER_FK` foreign key(`DAS_ID`) references `das`(`ID`) on delete cascade
);

-- role == group
create table `ROLE`(
	ID bigint primary key AUTO_INCREMENT,
	NAME varchar(100)
);

create table SUBJECT_ROLE(
	USER_ID bigint,
	ROLE_ID bigint,
	PRIMARY KEY (`USER_ID`,`ROLE_ID`),
	CONSTRAINT `FK_subject_role_s_id` FOREIGN KEY (`USER_ID`) REFERENCES `SUBJECT` (`ID`),
	CONSTRAINT `FK_subject_role_r_id` FOREIGN KEY (`ROLE_ID`) REFERENCES `ROLE` (`ID`)
);

create table PERMISISON_TEMPLATE(
	ID bigint primary key AUTO_INCREMENT,
	EXPRESSION varchar(255) DEFAULT NULL
);

create table ROLE_PERMISSION_TEMPLATE(
	ROLE_ID bigint,
	PERMISSION_TEMPLATE_ID bigint,
	PRIMARY KEY (`ROLE_ID`,`PERMISSION_TEMPLATE_ID`),
	CONSTRAINT `FK_role_permission_templater_id` FOREIGN KEY (`ROLE_ID`) REFERENCES `ROLE` (`ID`),
	CONSTRAINT `FK_role_permission_templatep_id` FOREIGN KEY (`PERMISSION_TEMPLATE_ID`) REFERENCES `PERMISISON_TEMPLATE` (`ID`)
);

INSERT INTO `ROLE` VALUES (1,'admin'),(2,'employee'),(3,'guest');
INSERT INTO `PERMISISON_TEMPLATE` values(1, '*:*'), (2, 'accounts:access,create,edit'), (3, 'ticket:*'), (4, 'company:*,das:*'), (5, 'rma:*'), (6, 'ticket:access,accounts:access');
INSERT INTO `SUBJECT_ROLE` VALUES (1,1),(152,2),(153,3);
INSERT INTO `ROLE_PERMISSION_TEMPLATE` VALUES (1,1),(2,2),(2,3),(2,4),(2,5),(3,6);


