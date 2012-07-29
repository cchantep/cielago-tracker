-- attachment_tbl
ALTER TABLE attachment_tbl 
ADD CONSTRAINT attachment_pk PRIMARY KEY (uuid);


-- list_tbl
ALTER TABLE list_tbl 
ADD CONSTRAINT list_pk PRIMARY KEY (uuid);


-- message_tbl
ALTER TABLE message_tbl 
ADD CONSTRAINT message_pk PRIMARY KEY (uuid);

ALTER TABLE message_tbl 
ADD CONSTRAINT message_list_fk 
FOREIGN KEY (list_uuid) 
REFERENCES list_tbl(uuid) 
ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE INDEX message_list_idx ON message_tbl(list_uuid);

CREATE INDEX message_sendtime_idx ON message_tbl(send_time);

CREATE INDEX message_type_idx ON message_tbl(type);


-- attachment_tbl
ALTER TABLE attachment_tbl 
ADD CONSTRAINT attachment_message_fk 
FOREIGN KEY (message_uuid) 
REFERENCES message_tbl(uuid) 
ON UPDATE NO ACTION ON DELETE NO ACTION;


-- log_tbl
ALTER TABLE log_tbl 
ADD CONSTRAINT log_pk PRIMARY KEY (uuid);

ALTER TABLE log_tbl 
ADD CONSTRAINT log_message_fk 
FOREIGN KEY (message_uuid) 
REFERENCES message_tbl(uuid) 
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE log_tbl 
ADD CONSTRAINT log_list_fk 
FOREIGN KEY (list_uuid) 
REFERENCES list_tbl(uuid) 
ON UPDATE NO ACTION ON DELETE NO ACTION;


-- member_tbl
ALTER TABLE member_tbl 
ADD CONSTRAINT member_pk PRIMARY KEY (uuid);

ALTER TABLE member_tbl 
ADD CONSTRAINT member_list_fk 
FOREIGN KEY (list_uuid) 
REFERENCES list_tbl(uuid) 
ON UPDATE NO ACTION ON DELETE NO ACTION;


-- recipient_tbl
ALTER TABLE recipient_tbl 
ADD CONSTRAINT recipient_pk PRIMARY KEY (uuid);

CREATE INDEX recipient_message_idx ON recipient_tbl(message_uuid);

ALTER TABLE recipient_tbl 
ADD CONSTRAINT recipient_message_fk 
FOREIGN KEY (message_uuid) 
REFERENCES message_tbl(uuid) 
ON UPDATE NO ACTION ON DELETE NO ACTION;


-- receipt_tbl
ALTER TABLE receipt_tbl ADD CONSTRAINT receipt_pk 
PRIMARY KEY(message_id, recipient_id);

CREATE TRIGGER recipient_has_received_tg 
AFTER INSERT ON receipt_tbl 
REFERENCING NEW AS n 
FOR EACH ROW 
  UPDATE recipient_tbl SET send_state=2
  WHERE uuid = n.recipient_id
    AND message_uuid = n.message_id;
