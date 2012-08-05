-----
-- Table: list_tbl
-----
INSERT INTO list_tbl(uuid, login, password, sender_email, sender_dn, owner_email) VALUES('list1', 'test1', 'pass1', 'test1@noreply.org', 'Test 1', 'test1@noreply.org');

INSERT INTO list_tbl(uuid, login, password, sender_email, sender_dn, owner_email) VALUES('list2', 'test2', 'pass2', 'test2@noreply.org', 'Test 2', 'test2@noreply.org');

-----
-- Table: manager_tbl
-----
INSERT INTO manager_tbl(username, md5_secret) 
VALUES('manager', MD5('pass_manager'));