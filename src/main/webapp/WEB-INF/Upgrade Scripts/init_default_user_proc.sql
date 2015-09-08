DELIMITER $$
DROP PROCEDURE IF EXISTS init_default_user$$ 
CREATE PROCEDURE init_default_user()
BEGIN
	DECLARE def_username VARCHAR(255) DEFAULT 'changeit@jnoc.com'; 
	DECLARE def_password VARCHAR(255) DEFAULT '$shiro1$SHA-256$500000$jbxaCnWz7dHrQa5vjsSHgQ==$/PVKH7PjUtZwRy6BtBDSbzEr5bmYsrfMbK8YuS4BH1c=';

	DECLARE account_id INT;
	DECLARE contact_id INT;
	DECLARE subject_id INT;
	
	INSERT INTO `ACCOUNT` (`CLOSEEPOCH`, `CONFIRMED`, `CREATIONEPOCH`, `CURRENTSESSIONEPOCH`, 
			`LASTSESSIONEPOCH`, `LOCKED`, `S3ID`, `VERSION`)
		VALUES (NULL,1,1377631184752,1416986454340,1410333039410,0,NULL,1);
	
	SELECT max(id) FROM `ACCOUNT` INTO account_id;	
	
	
	INSERT INTO `CONTACT` (`DOBEPOCH`, `EMAIL`, `FIRSTNAME`, `LASTNAME`, `TYPE`) VALUES (NULL,NULL,'Chan','geit',NULL);
	SELECT max(id) FROM `CONTACT` INTO contact_id;	
	
	
	INSERT INTO `SUBJECT` (`EMAIL`, `PASSWORD`, `VERSION`, `COMPANY_ID`, `METIER_ID`, `ACCOUNT_ID`, `CONTACT_ID`)
		VALUES (def_username, def_password, 12, NULL, 1, account_id, contact_id);
	SELECT max(id) FROM `SUBJECT` INTO subject_id;
	
	INSERT INTO `SUBJECT_PERMISSION` (`User_ID`, `permissions_ID`) VALUES (subject_id,1);
	
END $$
DELIMITER ;
CALL init_default_user();