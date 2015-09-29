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