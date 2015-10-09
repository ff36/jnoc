create table `carrier`(
	id bigint primary key AUTO_INCREMENT,
	title varchar(100),
	name  varchar(100),
	phone varchar(100),
	email varchar(500),
	nocEmail varchar(500),
	btsId int,
	location varchar(100), 
	notes varchar(500),
	dasId bigint, 
	
	constraint `das_carrier_fk` foreign key(`dasId`) references `das`(`id`) on delete cascade
);

--role == group
create table `role`(
	id bigint primary key AUTO_INCREMENT,
	name varchar(100)
);

create table subject_role(
	User_ID bigint,
	Role_ID bigint,
	PRIMARY KEY (`User_ID`,`Role_ID`),
	CONSTRAINT `FK_subject_role_s_id` FOREIGN KEY (`User_ID`) REFERENCES `subject` (`ID`),
	CONSTRAINT `FK_subject_role_r_id` FOREIGN KEY (`Role_ID`) REFERENCES `role` (`ID`)
);

create table permisison_template(
	ID bigint primary key AUTO_INCREMENT,
	EXPRESSION varchar(255) DEFAULT NULL
);

create table role_permission_template(
	Role_ID bigint,
	Permission_template_ID bigint,
	PRIMARY KEY (`Role_ID`,`Permission_template_ID`),
	CONSTRAINT `FK_role_permission_templater_id` FOREIGN KEY (`Role_ID`) REFERENCES `role` (`ID`),
	CONSTRAINT `FK_role_permission_templatep_id` FOREIGN KEY (`Permission_template_ID`) REFERENCES `permisison_template` (`ID`)
);


INSERT INTO `role` VALUES (1,'admin'),(2,'employee'),(3,'guest');
INSERT INTO `permisison_template` values(1, '*:*'), (2, 'accounts:access,create,edit'), (3, 'ticket:*'), (4, 'company:*,das:*'), (5, 'rma:*'), (6, 'ticket:access,accounts:access');
INSERT INTO `subject_role` VALUES (1,1),(152,2),(153,3);
INSERT INTO `role_permission_template` VALUES (1,1),(2,2),(2,3),(2,4),(2,5),(3,6);


