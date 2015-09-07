-- Update exceptions template
UPDATE jnoc_uat.EMAILTEMPLATE SET `HTML` = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> EXCEPTION NAME: $variable.exception_name <br /> EXCEPTION MESSAGE: $variable.exception_message <br /> EXCEPTION STACK: $variable.exception_stack <br /> #end </body> </html>' WHERE ID = 'EXCEPTION';

-- Insert first permission
INSERT INTO jnoc_uat.`PERMISSION` (ID, `NAME`, VERSION) 
	VALUES ('1', '*:*', 1);
INSERT INTO jnoc_uat.SUBJECT_PERMISSION (Subject_UID, permissions_ID) 
	VALUES ('1', '1');