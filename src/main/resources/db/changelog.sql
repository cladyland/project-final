--liquibase formatted sql

--changeset kmpk:init_schema
DROP TABLE IF EXISTS USER_ROLE;
DROP TABLE IF EXISTS CONTACT;
DROP TABLE IF EXISTS MAIL_CASE;
DROP TABLE IF EXISTS PROFILE;
DROP TABLE IF EXISTS TASK_TAG;
DROP TABLE IF EXISTS TASK_TAGS;
DROP TABLE IF EXISTS USER_BELONG;
DROP TABLE IF EXISTS ACTIVITY;
DROP TABLE IF EXISTS TASK;
DROP TABLE IF EXISTS SPRINT;
DROP TABLE IF EXISTS PROJECT;
DROP TABLE IF EXISTS REFERENCE;
DROP TABLE IF EXISTS ATTACHMENT;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS TASK_TIME;

create table PROJECT
(
    ID          bigserial primary key,
    CODE        varchar(32)   not null
        constraint UK_PROJECT_CODE unique,
    TITLE       varchar(1024) not null,
    DESCRIPTION varchar(4096) not null,
    TYPE_CODE   varchar(32)   not null,
    STARTPOINT  timestamp,
    ENDPOINT    timestamp,
    PARENT_ID   bigint,
    constraint FK_PROJECT_PARENT foreign key (PARENT_ID) references PROJECT (ID) on delete cascade
);

create table MAIL_CASE
(
    ID        bigserial primary key,
    EMAIL     varchar(255) not null,
    NAME      varchar(255) not null,
    DATE_TIME timestamp    not null,
    RESULT    varchar(255) not null,
    TEMPLATE  varchar(255) not null
);

create table SPRINT
(
    ID          bigserial primary key,
    STATUS_CODE varchar(32)   not null,
    STARTPOINT  timestamp,
    ENDPOINT    timestamp,
    TITLE       varchar(1024) not null,
    PROJECT_ID  bigint        not null,
    constraint FK_SPRINT_PROJECT foreign key (PROJECT_ID) references PROJECT (ID) on delete cascade
);

create table REFERENCE
(
    ID         bigserial primary key,
    CODE       varchar(32)   not null,
    REF_TYPE   smallint      not null,
    ENDPOINT   timestamp,
    STARTPOINT timestamp,
    TITLE      varchar(1024) not null,
    AUX        varchar,
    constraint UK_REFERENCE_REF_TYPE_CODE unique (REF_TYPE, CODE)
);

create table USERS
(
    ID           bigserial primary key,
    DISPLAY_NAME varchar(32)  not null
        constraint UK_USERS_DISPLAY_NAME unique,
    EMAIL        varchar(128) not null
        constraint UK_USERS_EMAIL unique,
    FIRST_NAME   varchar(32)  not null,
    LAST_NAME    varchar(32),
    PASSWORD     varchar(128) not null,
    ENDPOINT     timestamp,
    STARTPOINT   timestamp
);

create table PROFILE
(
    ID                 bigint primary key,
    LAST_LOGIN         timestamp,
    LAST_FAILED_LOGIN  timestamp,
    MAIL_NOTIFICATIONS bigint,
    constraint FK_PROFILE_USERS foreign key (ID) references USERS (ID) on delete cascade
);

create table CONTACT
(
    ID    bigint       not null,
    CODE  varchar(32)  not null,
    VALUE varchar(256) not null,
    primary key (ID, CODE),
    constraint FK_CONTACT_PROFILE foreign key (ID) references PROFILE (ID) on delete cascade
);

create table TASK
(
    ID            bigserial primary key,
    TITLE         varchar(1024) not null,
    DESCRIPTION   varchar(4096) not null,
    TYPE_CODE     varchar(32)   not null,
    STATUS_CODE   varchar(32)   not null,
    PRIORITY_CODE varchar(32)   not null,
    ESTIMATE      integer,
    UPDATED       timestamp,
    PROJECT_ID    bigint        not null,
    SPRINT_ID     bigint,
    PARENT_ID     bigint,
    STARTPOINT    timestamp,
    ENDPOINT      timestamp,
    constraint FK_TASK_SPRINT foreign key (SPRINT_ID) references SPRINT (ID) on delete set null,
    constraint FK_TASK_PROJECT foreign key (PROJECT_ID) references PROJECT (ID) on delete cascade,
    constraint FK_TASK_PARENT_TASK foreign key (PARENT_ID) references TASK (ID) on delete cascade
);

create table ACTIVITY
(
    ID            bigserial primary key,
    AUTHOR_ID     bigint not null,
    TASK_ID       bigint not null,
    UPDATED       timestamp,
    COMMENT       varchar(4096),
--     history of task field change
    TITLE         varchar(1024),
    DESCRIPTION   varchar(4096),
    ESTIMATE      integer,
    TYPE_CODE     varchar(32),
    STATUS_CODE   varchar(32),
    PRIORITY_CODE varchar(32),
    constraint FK_ACTIVITY_USERS foreign key (AUTHOR_ID) references USERS (ID),
    constraint FK_ACTIVITY_TASK foreign key (TASK_ID) references TASK (ID) on delete cascade
);

create table TASK_TAG
(
    TASK_ID bigint      not null,
    TAG     varchar(32) not null,
    constraint UK_TASK_TAG unique (TASK_ID, TAG),
    constraint FK_TASK_TAG foreign key (TASK_ID) references TASK (ID) on delete cascade
);

create table USER_BELONG
(
    ID             bigserial primary key,
    OBJECT_ID      bigint      not null,
    OBJECT_TYPE    smallint    not null,
    USER_ID        bigint      not null,
    USER_TYPE_CODE varchar(32) not null,
    STARTPOINT     timestamp,
    ENDPOINT       timestamp,
    constraint FK_USER_BELONG foreign key (USER_ID) references USERS (ID)
);
create unique index UK_USER_BELONG on USER_BELONG (OBJECT_ID, OBJECT_TYPE, USER_ID, USER_TYPE_CODE);
create index IX_USER_BELONG_USER_ID on USER_BELONG (USER_ID);

create table ATTACHMENT
(
    ID          bigserial primary key,
    NAME        varchar(128) not null,
    FILE_LINK   varchar(2048) not null,
    OBJECT_ID   bigint        not null,
    OBJECT_TYPE smallint      not null,
    USER_ID     bigint        not null,
    DATE_TIME   timestamp,
    constraint FK_ATTACHMENT foreign key (USER_ID) references USERS (ID)
);

create table USER_ROLE
(
    USER_ID bigint   not null,
    ROLE    smallint not null,
    constraint UK_USER_ROLE unique (USER_ID, ROLE),
    constraint FK_USER_ROLE foreign key (USER_ID) references USERS (ID) on delete cascade
);

create table TASK_TIME
    (
        ID bigserial primary key,
        TASK_ID bigint not null,
        WORK_TIME varchar not null,
        TEST_TIME varchar,
        constraint FK_TASK_TIME foreign key (TASK_ID) references TASK (ID) on delete cascade
);

--changeset kmpk:populate_data

insert into USERS (EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, DISPLAY_NAME)
values ('user@gmail.com', '{noop}password', 'userFirstName', 'userLastName', 'userDisplayName'),
       ('admin@gmail.com', '{noop}admin', 'adminFirstName', 'adminLastName', 'adminDisplayName'),
       ('guest@gmail.com', '{noop}guest', 'guestFirstName', 'guestLastName', 'guestDisplayName');

-- 0 DEV
-- 1 ADMIN
insert into USER_ROLE (ROLE, USER_ID)
values (0, 1),
       (1, 2),
       (0, 2);


--============ References =================
insert into REFERENCE (CODE, TITLE, REF_TYPE)
-- TASK
values ('task', 'Task', 2),
       ('story', 'Story', 2),
       ('bug', 'Bug', 2),
       ('epic', 'Epic', 2),
-- TASK_STATUS
       ('icebox', 'Icebox', 3),
       ('backlog', 'Backlog', 3),
       ('ready', 'Ready', 3),
       ('in progress', 'In progress', 3),
       ('done', 'Done', 3),
-- SPRINT_STATUS
       ('planning', 'Planning', 4),
       ('implementation', 'Implementation', 4),
       ('review', 'Review', 4),
       ('retrospective', 'Retrospective', 4),
-- USER_TYPE
       ('admin', 'Admin', 5),
       ('user', 'User', 5),
-- PROJECT
       ('scrum', 'Scrum', 1),
       ('task tracker', 'Task tracker', 1),
-- CONTACT
       ('skype', 'Skype', 0),
       ('tg', 'Telegram', 0),
       ('mobile', 'Mobile', 0),
       ('phone', 'Phone', 0),
       ('website', 'Website', 0),
       ('linkedin', 'LinkedIn', 0),
       ('github', 'GitHub', 0),
-- PRIORITY
       ('critical', 'Critical', 7),
       ('high', 'High', 7),
       ('normal', 'Normal', 7),
       ('low', 'Low', 7),
       ('neutral', 'Neutral', 7),
-- TAG
       ('backend', 'Backend', 8),
       ('frontend', 'Frontend', 8),
       ('test', 'Test', 8),
       ('feature', 'Feature', 8),
       ('bug', 'Bug', 8),
       ('dev', 'Dev', 8),
       ('design', 'Design', 8);

insert into REFERENCE (CODE, TITLE, REF_TYPE, AUX)
-- MAIL_NOTIFICATION
values ('assigned', 'Assigned', 6, '1'),
       ('three_days_before_deadline', 'Three days before deadline', 6, '2'),
       ('two_days_before_deadline', 'Two days before deadline', 6, '4'),
       ('one_day_before_deadline', 'One day before deadline', 6, '8'),
       ('deadline', 'Deadline', 6, '16'),
       ('overdue', 'Overdue', 6, '32');

insert into PROFILE (ID, LAST_FAILED_LOGIN, LAST_LOGIN, MAIL_NOTIFICATIONS)
values (1, null, null, 49),
       (2, null, null, 14);

insert into CONTACT (ID, CODE, VALUE)
values (1, 'skype', 'userSkype'),
       (1, 'mobile', '+01234567890'),
       (1, 'website', 'user.com'),
       (2, 'github', 'adminGitHub'),
       (2, 'tg', 'adminTg');

--changeset kriffer:add_dashboard

insert into PROJECT (id, code, title, description, type_code, startpoint, endpoint, parent_id)
values (2, 'task tracker', 'PROJECT-1', 'test project', 'task tracker', null, null, null);

insert into SPRINT (id, status_code, startpoint, endpoint, title, project_id)
values (1, 'planning', '2023-04-09 23:05:05.000000', '2023-04-12 23:05:12.000000', 'Sprint-1', 2);

insert into TASK (id, title, description, type_code, status_code, priority_code, project_id, sprint_id)
values (2, 'Task-1', 'short test task', 'task', 'in progress', 'high', 2, 1),
       (3, 'Task-2', 'test 2 task', 'bug', 'ready', 'normal', 2, 1),
       (5, 'Task-4', 'test 4', 'bug', 'in progress', 'normal', 2, 1),
       (4, 'Task-3', 'test 3 descr', 'task', 'done', 'low', 2, 1);
--Backlog
insert into TASK (id, title, description, type_code, status_code, priority_code, project_id)
values (6, 'Task-5', 'test 5', 'story', 'backlog', 'low', 2),
       (7, 'Task-6', 'test 6', 'epic', 'backlog', 'high', 2),
       (8, 'Task-7', 'test 7', 'story', 'backlog', 'normal', 2),
       (9, 'Task-8', 'test 8', 'bug', 'backlog', 'normal', 2),
       (10, 'Task-9', 'test 9', 'task', 'backlog', 'low', 2),
       (11, 'Task-10', 'test 10', 'epic', 'backlog', 'normal', 2),
       (12, 'Task-11', 'test 11', 'task', 'backlog', 'high', 2),
       (13, 'Task-12', 'test 12', 'story', 'backlog', 'low', 2);

insert into USER_BELONG (id, object_id, object_type, user_id, user_type_code)
values (3, 2, 2, 2, 'admin'),
       (4, 3, 2, 2, 'admin'),
       (5, 4, 2, 2, 'admin'),
       (6, 5, 2, 2, 'admin');

insert into ACTIVITY (AUTHOR_ID, TASK_ID, UPDATED, STATUS_CODE)
values (2, 4, '2023-04-18 09:08:01.000000', 'in progress'),
       (2, 3, '2023-04-23 13:26:38.000000', 'in progress'),
       (2, 4, '2023-04-24 16:43:52.000000', 'ready'),
       (2, 3, '2023-04-26 18:04:41.000000', 'ready'),
       (2, 4, '2023-04-29 21:48:13.000000', 'done'),
       (2, 2, '2023-05-06 11:11:26.000000', 'in progress'),
       (2, 5, '2023-05-15 15:10:07.000000', 'in progress');

insert into TASK_TIME (TASK_ID, WORK_TIME, TEST_TIME)
values (3, '3 days, 4 hours, 38 minutes, 3 seconds', null),
       (4, '6 days, 7 hours, 35 minutes, 51 seconds', '5 days, 5 hours, 4 minutes, 21 seconds');
