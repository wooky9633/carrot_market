create table member
(
    email varchar(255),
    password varchar(255),
    name varchar(255),
    nickname varchar(255),
    locationX Float,
    locationY Float,
    primary key(email)
);

create table board
(
id bigint generated by default as identity,
price bigint,
title varchar(255),
text varchar(255),
category_Id int,
nickname varchar(255),
locationX float,
locationY float,
register_Date varchar(255),
deadline_Date varchar(255),
dibs_Cnt int,
view_Cnt int,
chat_Cnt int,
picture varchar(255),
primary key(id)
);