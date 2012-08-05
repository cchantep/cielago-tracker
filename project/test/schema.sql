CREATE TABLE message_tbl (
    send_time BIGINT,
    list_uuid VARCHAR(36) NOT NULL,
    subject VARCHAR(125) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    content LONG VARCHAR NOT NULL,
    notification_id SMALLINT,
    format VARCHAR(125),
    content_type VARCHAR(125) NOT NULL,
    receipt BOOLEAN DEFAULT false,
    retry_counter SMALLINT DEFAULT 0,
    sender_dn LONG VARCHAR,-- NOT NULL,
    sender_email VARCHAR(125),-- NOT NULL,
    type INTEGER NOT NULL
);

CREATE TABLE receipt_tbl (
    message_id VARCHAR(36) NOT NULL,
    message_subject VARCHAR(125) NOT NULL,
    recipient_id VARCHAR(36) NOT NULL,
    recipient_email VARCHAR(125) NOT NULL,
    recipient_inet VARCHAR(15),
    time TIMESTAMP NOT NULL
);


CREATE TABLE list_tbl (
    login VARCHAR(125) NOT NULL,
    password VARCHAR(125) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    sender_email VARCHAR(125) NOT NULL,
    sender_dn VARCHAR(125) NOT NULL,
    owner_email VARCHAR(125) NOT NULL
);


CREATE TABLE attachment_tbl (
    message_uuid VARCHAR(36) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    inline BOOLEAN DEFAULT false,
    content_type VARCHAR(125) DEFAULT 'text/plain',
    file_name VARCHAR(125) NOT NULL
);


CREATE TABLE recipient_tbl (
    message_uuid VARCHAR(36),
    dn VARCHAR(125) NOT NULL,
    email VARCHAR(125) NOT NULL,
    uuid VARCHAR(36) NOT NULL,
    has_been_sent BOOLEAN DEFAULT false,
    send_state INTEGER DEFAULT 0
);


CREATE TABLE log_tbl (
    message_uuid VARCHAR(36),
    rcp_num INTEGER,
    list_uuid VARCHAR(36),
    msg_size INTEGER,
    uuid VARCHAR(36) NOT NULL,
    state SMALLINT,
    time BIGINT
);


CREATE TABLE member_tbl (
    dn VARCHAR(125) NOT NULL,
    list_uuid VARCHAR(36),
    email VARCHAR(125) NOT NULL,
    uuid VARCHAR(36) NOT NULL
);


-----
-- Table: manager_tbl
-----
CREATE TABLE manager_tbl (
    username VARCHAR(25) NOT NULL,
    md5_secret VARCHAR(125) NOT NULL
);


-----
-- View: trackers
-----
CREATE FUNCTION MD5(CLEARTEXT VARCHAR(125))
RETURNS VARCHAR(50) 
PARAMETER STYLE JAVA 
NO SQL LANGUAGE JAVA 
EXTERNAL NAME 'org.apache.commons.codec.digest.DigestUtils.md5Hex';

CREATE VIEW trackers (list_uuid, username, md5_secret) AS 
SELECT l.uuid, m.username, m.md5_secret FROM list_tbl l, manager_tbl m 
UNION SELECT uuid, login, MD5(password) FROM list_tbl;