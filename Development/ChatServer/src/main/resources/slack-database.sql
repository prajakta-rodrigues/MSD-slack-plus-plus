DROP schema if exists slack;
create schema slack;
USE slack;

create table slack.user(id int(15) primary key, handle varchar(30), first_name varchar(30), last_name varchar(30),
password varchar(20), account_created_date timestamp, type varchar(20)) ENGINE=INNODB;

create table slack.channel(id int(15) AUTO_INCREMENT primary key) ENGINE=INNODB;

create table slack.message(id int(15) AUTO_INCREMENT primary key, sender_id int(15) , type varchar(20),
sent_date timestamp, channel_id int(15), 
constraint fk_message_channel_id foreign key(channel_id)  references slack.channel(id), 
constraint fk_message_user_id foreign key(sender_id) references slack.user(id)) ENGINE=INNODB;

create table slack.group(id int(15) primary key, name varchar(30) UNIQUE NOT NULL, created_date timestamp, 
private boolean , parent_id int(15), channel_id int(15) UNIQUE NOT NULL, 
constraint fk_group_parent_id foreign key(parent_id) references slack.group(id), 
constraint fk_group_channel_id foreign key(channel_id) references slack.channel(id))ENGINE=InnoDB;

create table slack.user_group(user_id int(15), group_id int(15), isModerator boolean, created_date timestamp, 
primary key(user_id, group_id), constraint fk_user_id foreign key(user_id) references slack.user(id),
constraint fk_group_id foreign key(group_id) references slack.group(id)) ENGINE=InnoDB;

create table slack.direct_message(user1_id int(15), user2_id int(15) references slack.user(id), 
channel_id int(15) UNIQUE NOT NULL references slack.channel(id), constraint fk_user1_id foreign key(user1_id) references slack.user(id),
constraint fk_user2_id foreign key(user2_id) references slack.user(id),
primary key(user1_id,user2_id), unique(user2_id,user1_id)) ENGINE=InnoDB;

CREATE TABLE friend_request(
    sender_id int(15) NOT NULL,
    receiver_id int(15) NOT NULL,
    PRIMARY KEY (sender_id, receiver_id));
CREATE TABLE friend(
    user1_id int(15) NOT NULL,
    user2_id int(15) NOT NULL,
    PRIMARY KEY (user1_id, user2_id),
    FOREIGN KEY (user1_id) REFERENCES slack.user(id),
    FOREIGN KEY (user2_id) REFERENCES slack.user(id)) ENGINE=INNODB;
 
delimiter //
CREATE TRIGGER slack.insert_direct_messaging BEFORE INSERT ON slack.direct_message
FOR EACH ROW
BEGIN
	declare n int(15);
    declare new_user1_id int;
    declare new_user2_id int;
    set new_user1_id = new.user1_id;
	set new_user2_id = new.user2_id;
	set n := (Select count(*) from slack.direct_message d where user1_id = new_user2_id and user2_id = new_user1_id);
    if n >= 1 then
		SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Direct messaging between two users already exists';
	end if;
END//

delimiter ;

ALTER TABLE slack.user MODIFY COLUMN password VARCHAR(100);

ALTER TABLE slack.user
 ADD CONSTRAINT unique_handle UNIQUE (handle);
 
ALTER TABLE slack.user
	ADD COLUMN is_active TINYINT DEFAULT 0,
    ADD COLUMN active_channel int(15) DEFAULT 1,
    ADD CONSTRAINT FOREIGN KEY(active_channel) REFERENCES slack.channel(id);
 
ALTER TABLE slack.group
	ADD COLUMN creator_id int(15) NOT NULL,
    ADD CONSTRAINT FOREIGN KEY (creator_id)
		REFERENCES user(id),
	ADD CONSTRAINT UNIQUE (channel_id);
    
ALTER TABLE slack.message
	ADD COLUMN text varchar(500) NOT NULL;
    
INSERT INTO slack.channel VALUES();
INSERT INTO slack.user VALUES(-1, 'Slackbot', null, null, null, null, null, null, null);
INSERT INTO slack.group VALUES (1, 'general', CURDATE(), 0, NULL, 1, -1);
        
delimiter //
CREATE PROCEDURE slack.make_group (
	creatorId int(15),
    groupId int(15),
    groupName varchar(30)
)
BEGIN
	INSERT INTO slack.channel VALUES();
    INSERT INTO slack.group(creator_id, id, name, channel_id, created_date) VALUES 
		(creatorId, groupId, groupName, LAST_INSERT_ID(), CURTIME());
END //

CREATE PROCEDURE slack.make_dm (
	senderId int(15),
    receiverId int(15)
)
BEGIN
	DECLARE channelId int(15);
	INSERT INTO slack.channel VALUES();
    SET channelId = LAST_INSERT_ID();
    INSERT INTO slack.direct_message VALUES (senderId, receiverId, channelId);
	SELECT channelId as 'channel_id';
END //

CREATE PROCEDURE slack.accept_friend_request
(
	senderId int(15),
  receiverId int(15)
)
BEGIN
	DECLARE error TINYINT DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET error = TRUE;
	START TRANSACTION;
		DELETE FROM friend_request WHERE sender_id = receiverId AND receiver_id = senderId;
        INSERT INTO friend VALUES (senderId, receiverId);
	IF error THEN
		ROLLBACK;
        SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Could not become friends';
	ELSE
		COMMIT;
        SELECT 'Users are now friends' as 'message';
    END IF;
END //

CREATE TRIGGER slack.insert_group AFTER INSERT ON slack.group
FOR EACH ROW
BEGIN
	INSERT INTO slack.user_group VALUES (NEW.creator_id, NEW.id, TRUE, CURTIME());
END //

CREATE TRIGGER slack.insert_user AFTER INSERT ON slack.user
FOR EACH ROW
BEGIN
	INSERT INTO slack.user_group VALUES (NEW.id, 1, FALSE, CURTIME());
END //

CREATE TRIGGER slack.notify_msg_receivers AFTER INSERT ON slack.message
FOR EACH ROW
BEGIN
	DECLARE isDM TINYINT;
    DECLARE receiver int(15);
    DECLARE user1 int(15);
    DECLARE user2 int(15);
    DECLARE notificationType varchar(20);
    
    SET notificationType = 'UNREAD_MESSAGES';
    
	IF NEW.type LIKE 'BCT' THEN
		SELECT (count(*) > 0), user1_id, user2_id 
        INTO isDM, user1, user2
        FROM direct_message 
        WHERE channel_id = NEW.channel_id;
        
        IF isDM THEN
			SET receiver = IF (NEW.sender_id = user1, user2, user1);
            INSERT INTO slack.notification (receiver_id, associated_user_id, type, created_date, new)
            SELECT id, NEW.sender_id, notificationType, CURDATE(), TRUE
            FROM slack.user
            WHERE id = receiver AND (!is_active OR active_channel <> NEW.channel_id);
		ELSE
			INSERT INTO slack.notification (receiver_id, associated_group_id, type, created_date, new)
			SELECT user_id, group_id, notificationType, CURDATE(), TRUE
            FROM slack.group g 
				JOIN slack.user_group ug ON (g.id = ug.group_id)
                JOIN slack.user u ON (ug.user_id = u.id)
            WHERE (!u.is_active OR u.active_channel <> NEW.channel_id) 
				AND g.channel_id = NEW.channel_id
                AND user_id <> NEW.sender_id;
        END IF;
	END IF;
END //

CREATE TRIGGER slack.insert_friend_request BEFORE INSERT ON slack.friend_request
FOR EACH ROW
BEGIN
    DECLARE n int(15);
    DECLARE senderId int(15);
    DECLARE receiverID int(15);
    SET senderId = NEW.sender_id;
    SET receiverId = NEW.receiver_id;
    if n >= 1 then
        SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Friend Request between users already exists';
    end if;
END //
CREATE TRIGGER slack.insert_friend BEFORE INSERT ON slack.friend
FOR EACH ROW
BEGIN
    declare n int(15);
    declare new_user1_id int;
    declare new_user2_id int;
    set new_user1_id = new.user1_id;
    set new_user2_id = new.user2_id;
    set n := (Select count(*) from slack.friend where user1_id = new_user2_id and user2_id = new_user1_id);
    if n >= 1 then
        SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'The two users are already friends!';
    end if;
END //

delimiter ;

		
create table slack.notification(id int(15) primary key auto_increment, receiver_id int(15) not null,associated_user_id int(15), associated_group_id int(15),
type varchar(30) not null, created_date timestamp, new boolean,
constraint fk_group_notification_id foreign key(associated_group_id) references slack.group(id),
constraint fk_receiver_notification_id foreign key(receiver_id) references slack.user(id),
constraint fk_user_notification_id foreign key(associated_user_id) references slack.user(id)) ENGINE=INNODB;

create table slack.group_invitation(invitee_id int(15)  NOT NULL,
invitor_id int(15)  NOT NULL, group_id int(15) NOT NULL,
created_date timestamp  NOT NULL, 
primary key(invitee_id, invitor_id , group_id),
constraint fk_invitee_id foreign key(invitee_id)  references slack.user(id), 
constraint fk_invitor_id foreign key(invitor_id) references slack.user(id),
constraint fk_invitation_group_id foreign key(group_id) references slack.group(id)) ENGINE=INNODB;

ALTER TABLE slack.user MODIFY COLUMN type VARCHAR(20) NOT NULL DEFAULT 'GENERAL';
update slack.user set type = 'SYSTEM' where id = -1; 
update slack.user set type = 'GENERAL' where id = null;

alter table slack.user add column dnd boolean default false;
