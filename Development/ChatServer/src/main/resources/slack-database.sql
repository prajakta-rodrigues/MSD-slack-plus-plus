DROP schema if exists slack;
create schema slack;

create table slack.user(id int(15) primary key, handle varchar(30), first_name varchar(30), last_name varchar(30),
password varchar(20), account_created_date timestamp, type varchar(20)) ENGINE=INNODB;

create table slack.channel(id int(15) primary key) ENGINE=INNODB;

create table slack.message(id int(15) primary key, sender_id int(15) , type varchar(20), 
sent_date timestamp, channel_id int(15), 
constraint fk_message_channel_id foreign key(channel_id)  references slack.channel(id), 
constraint fk_message_user_id foreign key(sender_id) references slack.user(id)) ENGINE=INNODB;

create table slack.group(id int(15) primary key, name varchar(30), created_date timestamp, 
private boolean , parent_id int(15), channel_id int(15), 
constraint fk_group_parent_id foreign key(parent_id) references slack.group(id), 
constraint fk_group_channel_id foreign key(channel_id) references slack.channel(id))ENGINE=InnoDB;

create table slack.user_group(user_id int(15), group_id int(15), isModerator boolean, created_date timestamp, 
primary key(user_id, group_id), constraint fk_user_id foreign key(user_id) references slack.user(id),
constraint fk_group_id foreign key(group_id) references slack.group(id)) ENGINE=InnoDB;

create table slack.direct_message(user1_id int(15), user2_id int(15) references slack.user(id), 
channel_id int(15) references slack.channel(id), constraint fk_user1_id foreign key(user1_id) references slack.user(id),
constraint fk_user2_id foreign key(user2_id) references slack.user(id),
primary key(user1_id,user2_id), unique(user2_id,user1_id)) ENGINE=InnoDB;
 
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
 
create table slack.notification(id int(15) primary key, receiver_id int(15) not null,associated_user_id int(15), associated_group_id int(15),
type varchar(30) not null, created_date timestamp, new boolean,
constraint fk_group_notification_id foreign key(associated_group_id) references slack.group(id),
constraint fk_receiver_notification_id foreign key(receiver_id) references slack.user(id),
constraint fk_user_notification_id foreign key(associated_user_id) references slack.user(id)) ENGINE=INNODB;