-- Metiers
INSERT INTO dastrax_dev.METIER (ID, `NAME`) 
VALUES ('1', 'ADMIN');

INSERT INTO dastrax_dev.METIER (ID, `NAME`) 
VALUES ('2', 'VAR');

INSERT INTO dastrax_dev.METIER (ID, `NAME`) 
VALUES ('3', 'CLIENT');

INSERT INTO dastrax_dev.METIER (ID, `NAME`) 
VALUES ('4', 'SOLO');

INSERT INTO dastrax_dev.METIER (ID, `NAME`) 
VALUES ('5', 'ADHOC');

-- Root Nexus
INSERT INTO dastrax_dev.NEXUS (ID, EXCLUDESUBJECTS, `NAME`, CREATOR_UID, ROOTACL_ID, NEXUS_ID) 
VALUES ('1', NULL, 'ALL ADMIN', NULL, '1', NULL);

INSERT INTO dastrax_dev.NEXUS (ID, EXCLUDESUBJECTS, `NAME`, CREATOR_UID, ROOTACL_ID, NEXUS_ID) 
VALUES ('2', NULL, 'ALL VAR', NULL, '2', NULL);

INSERT INTO dastrax_dev.NEXUS (ID, EXCLUDESUBJECTS, `NAME`, CREATOR_UID, ROOTACL_ID, NEXUS_ID) 
VALUES ('3', NULL, 'ALL CLIENT', NULL, '3', NULL);

-- Email Templates
INSERT INTO dastrax_dev.EMAILTEMPLATE (ID, DESCRIPTION, HTML, LINKPATH, PLAINTEXT, SUBJECT, TITLE) 
VALUES ('NEW_ACCOUNT', 'Template for a new account', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> LINK: $variable.link <br /> PIN: $variable.pin <br /> #end </body> </html>', '/confirm/account.jsf', '#foreach($variable in $variablesList) DATE: $variable.date TIME: $variable.time LINK: $variable.link PIN: $variable.pin #end', 'New Account', 'New Account');

INSERT INTO dastrax_dev.EMAILTEMPLATE (ID, DESCRIPTION, HTML, LINKPATH, PLAINTEXT, SUBJECT, TITLE) 
VALUES ('CHANGE_EMAIL', 'Template for a new email', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> LINK: $variable.link <br /> PIN: $variable.pin <br /> #end </body> </html>', '/confirm/email.jsf', '#foreach($variable in $variablesList) DATE: $variable.date TIME: $variable.time LINK: $variable.link PIN: $variable.pin #end', 'Change Email', 'Change Email');

INSERT INTO dastrax_dev.EMAILTEMPLATE (ID, DESCRIPTION, HTML, LINKPATH, PLAINTEXT, SUBJECT, TITLE) 
VALUES ('CHANGE_PASSWORD', 'Template for a new password', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> LINK: $variable.link <br /> PIN: $variable.pin <br /> #end </body> </html>', '/confirm/password.jsf', '#foreach($variable in $variablesList) DATE: $variable.date TIME: $variable.time LINK: $variable.link PIN: $variable.pin #end', 'Change Password', 'Change Password');

INSERT INTO dastrax_dev.EMAILTEMPLATE (ID, DESCRIPTION, HTML, LINKPATH, PLAINTEXT, SUBJECT, TITLE) 
VALUES ('NEW_TICKET', 'Template for a new ticket', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> LINK: $variable.link <br /> PIN: $variable.pin <br /> #end </body> </html>', null, '#foreach($variable in $variablesList) DATE: $variable.date TIME: $variable.time LINK: $variable.link PIN: $variable.pin #end', 'New Ticket', 'New Ticket');

INSERT INTO dastrax_dev.EMAILTEMPLATE (ID, DESCRIPTION, HTML, LINKPATH, PLAINTEXT, SUBJECT, TITLE) 
VALUES ('TICKET_COMMENT', 'Template for a new ticket comment', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> LINK: $variable.link <br /> PIN: $variable.pin <br /> #end </body> </html>', null, '#foreach($variable in $variablesList) DATE: $variable.date TIME: $variable.time LINK: $variable.link PIN: $variable.pin #end', 'New Ticket Comment', 'New Ticket Comment');

INSERT INTO dastrax_dev.EMAILTEMPLATE (ID, DESCRIPTION, HTML, LINKPATH, PLAINTEXT, SUBJECT, TITLE) 
VALUES ('TICKET_CLOSED', 'Template for a closed ticket', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> LINK: $variable.link <br /> PIN: $variable.pin <br /> #end </body> </html>', null, '#foreach($variable in $variablesList) DATE: $variable.date TIME: $variable.time LINK: $variable.link PIN: $variable.pin #end', 'Ticket Closed', 'Ticket Closed');

INSERT INTO dastrax_dev.EMAILTEMPLATE (ID, DESCRIPTION, HTML, LINKPATH, PLAINTEXT, SUBJECT, TITLE) 
VALUES ('EXCEPTION', 'Template for application exception', '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> <html xmlns="http://www.w3.org/1999/xhtml"> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head> <body> #foreach($variable in $variablesList) <br /> DATE: $variable.date <br /> TIME: $variable.time <br /> LINK: $variable.link <br /> PIN: $variable.pin <br /> EXCEPTION: $variable.exception <br /> #end </body> </html>', null, '#foreach($variable in $variablesList) DATE: $variable.date TIME: $variable.time LINK: $variable.link PIN: $variable.pin #end', 'Dastrax Exception', 'Dastrax Exception');
