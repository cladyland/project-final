--init_schema
drop table if exists user_role;
drop table if exists contact;
drop table if exists mail_case;
drop table if exists profile;
drop table if exists task_tag;
drop table if exists task_tags;
drop table if exists user_belong;
drop table if exists activity;
drop table if exists reference;
drop table if exists attachment;
drop table if exists users;
drop table if exists task_time;
drop table if exists task;
drop table if exists sprint;
drop table if exists project;

create table project
(
       id          bigint auto_increment primary key,
       code        varchar(32)   not null constraint uk_project_code unique,
       title       varchar(1024) not null,
       description varchar(4096) not null,
       type_code   varchar(32)   not null,
       startpoint  timestamp,
       endpoint    timestamp,
       parent_id   bigint,
       constraint fk_project_parent foreign key (parent_id) references project (id) on delete cascade
);

create table mail_case
(
       id        bigint auto_increment primary key,
       email     varchar(255) not null,
       name      varchar(255) not null,
       date_time timestamp    not null,
       result    varchar(255) not null,
       template  varchar(255) not null
);

create table sprint
(
       id          bigint auto_increment primary key,
       status_code varchar(32)   not null,
       startpoint  timestamp,
       endpoint    timestamp,
       title       varchar(1024) not null,
       project_id  bigint        not null,
       constraint fk_sprint_project foreign key (project_id) references project (id) on delete cascade
);

create table reference
(
       id         bigint auto_increment primary key,
       code       varchar(32)   not null,
       ref_type   smallint      not null,
       endpoint   timestamp,
       startpoint timestamp,
       title      varchar(1024) not null,
       aux        varchar,
       constraint uk_reference_ref_type_code unique (ref_type, code)
);

create table users
(
       id           bigint auto_increment primary key,
       display_name varchar(32)  not null constraint uk_users_display_name unique,
       email        varchar(128) not null constraint uk_users_email unique,
       first_name   varchar(32)  not null,
       last_name    varchar(32),
       password     varchar(128) not null,
       endpoint     timestamp,
       startpoint   timestamp
);

create table profile
(
       id                 bigint primary key,
       last_login         timestamp,
       last_failed_login  timestamp,
       mail_notifications bigint,
       constraint fk_profile_users foreign key (id) references users (id) on delete cascade
);

create table contact
(
       id    bigint       not null,
       code  varchar(32)  not null,
       value1 varchar(256) not null,
       primary key (id, code),
       constraint fk_contact_profile foreign key (id) references profile (id) on delete cascade
);

create table task
(
       id            bigint auto_increment primary key,
       title         varchar(1024) not null,
       description   varchar(4096) not null,
       type_code     varchar(32)   not null,
       status_code   varchar(32)   not null,
       priority_code varchar(32)   not null,
       estimate      integer,
       updated       timestamp,
       project_id    bigint        not null,
       sprint_id     bigint,
       parent_id     bigint,
       startpoint    timestamp,
       endpoint      timestamp,
       constraint fk_task_sprint foreign key (sprint_id) references sprint (id) on delete set null,
       constraint fk_task_project foreign key (project_id) references project (id) on delete cascade,
       constraint fk_task_parent_task foreign key (parent_id) references task (id) on delete cascade
);

create table activity
(
       id            bigint auto_increment primary key,
       author_id     bigint not null,
       task_id       bigint not null,
       updated       timestamp,
       comment       varchar(4096),
       title         varchar(1024),
       description   varchar(4096),
       estimate      integer,
       type_code     varchar(32),
       status_code   varchar(32),
       priority_code varchar(32),
       constraint fk_activity_users foreign key (author_id) references users (id),
       constraint fk_activity_task foreign key (task_id) references task (id) on delete cascade
);

create table task_tag
(
       task_id bigint      not null,
       tag     varchar(32) not null,
       constraint uk_task_tag unique (task_id, tag),
       constraint fk_task_tag foreign key (task_id) references task (id) on delete cascade
);

create table user_belong
(
       id             bigint auto_increment primary key,
       object_id      bigint      not null,
       object_type    smallint    not null,
       user_id        bigint      not null,
       user_type_code varchar(32) not null,
       startpoint     timestamp,
       endpoint       timestamp,
       constraint fk_user_belong foreign key (user_id) references users (id)
);
create unique index if not exists uk_user_belong on user_belong (object_id, object_type, user_id, user_type_code);
create index if not exists ix_user_belong_user_id on user_belong (user_id);

create table attachment
(
       id          bigint auto_increment primary key,
       name        varchar(128) not null,
       file_link   varchar(2048) not null,
       object_id   bigint        not null,
       object_type smallint      not null,
       user_id     bigint        not null,
       date_time   timestamp,
       constraint fk_attachment foreign key (user_id) references users (id)
);

create table user_role
(
       user_id bigint   not null,
       role    smallint not null,
       constraint uk_user_role unique (user_id, role),
       constraint fk_user_role foreign key (user_id) references users (id) on delete cascade
);

create table task_time
(
       id bigint auto_increment primary key,
       task_id bigint not null,
       work_time varchar not null,
       test_time varchar,
       constraint fk_task_time foreign key (task_id) references task (id) on delete cascade
);

delete from profile;
delete from user_role;
delete from user_belong;
delete from users;
delete from task;
delete from sprint;
delete from project;

insert into users (email, password, first_name, last_name, display_name, startpoint)
values ('user@gmail.com', '{noop}password', 'userFirstName', 'userLastName', 'userDisplayName', '2023-04-09 23:05:05.000000'),
       ('admin@gmail.com', '{noop}admin', 'adminFirstName', 'adminLastName', 'adminDisplayName', '2023-04-09 23:05:05.000000'),
       ('guest@gmail.com', '{noop}guest', 'guestFirstName', 'guestLastName', 'guestDisplayName', '2023-04-09 23:05:05.000000');

-- 0 DEV
-- 1 ADMIN
insert into user_role (role, user_id)
values (0, 1),
       (1, 2),
       (0, 2);

delete from reference;
insert into reference (code, title, ref_type, startpoint)
-- TASK
values ('task', 'Task', 2, '2023-04-09 23:05:05.000000'),
       ('story', 'Story', 2, '2023-04-09 23:05:05.000000'),
       ('bug', 'Bug', 2, '2023-04-09 23:05:05.000000'),
       ('epic', 'Epic', 2, '2023-04-09 23:05:05.000000'),
-- TASK_STATUS
       ('icebox', 'Icebox', 3, '2023-04-09 23:05:05.000000'),
       ('backlog', 'Backlog', 3, '2023-04-09 23:05:05.000000'),
       ('ready', 'Ready', 3, '2023-04-09 23:05:05.000000'),
       ('in progress', 'In progress', 3, '2023-04-09 23:05:05.000000'),
       ('done', 'Done', 3, '2023-04-09 23:05:05.000000'),
-- SPRINT_STATUS
       ('planning', 'Planning', 4, '2023-04-09 23:05:05.000000'),
       ('implementation', 'Implementation', 4, '2023-04-09 23:05:05.000000'),
       ('review', 'Review', 4, '2023-04-09 23:05:05.000000'),
       ('retrospective', 'Retrospective', 4, '2023-04-09 23:05:05.000000'),
-- USER_TYPE
       ('admin', 'Admin', 5, '2023-04-09 23:05:05.000000'),
       ('user', 'User', 5, '2023-04-09 23:05:05.000000'),
-- PROJECT
       ('scrum', 'Scrum', 1, '2023-04-09 23:05:05.000000'),
       ('task tracker', 'Task tracker', 1, '2023-04-09 23:05:05.000000'),
-- CONTACT
       ('skype', 'Skype', 0, '2023-04-09 23:05:05.000000'),
       ('tg', 'Telegram', 0, '2023-04-09 23:05:05.000000'),
       ('mobile', 'Mobile', 0, '2023-04-09 23:05:05.000000'),
       ('phone', 'Phone', 0, '2023-04-09 23:05:05.000000'),
       ('website', 'Website', 0, '2023-04-09 23:05:05.000000'),
       ('vk', 'VK', 0, '2023-04-09 23:05:05.000000'),
       ('linkedin', 'LinkedIn', 0, '2023-04-09 23:05:05.000000'),
       ('github', 'GitHub', 0, '2023-04-09 23:05:05.000000'),
-- PRIORITY
       ('critical', 'Critical', 7, '2023-04-09 23:05:05.000000'),
       ('high', 'High', 7, '2023-04-09 23:05:05.000000'),
       ('normal', 'Normal', 7, '2023-04-09 23:05:05.000000'),
       ('low', 'Low', 7, '2023-04-09 23:05:05.000000'),
       ('neutral', 'Neutral', 7, '2023-04-09 23:05:05.000000');

insert into reference (code, title, ref_type, aux, startpoint)
-- MAIL_NOTIFICATION
values ('assigned', 'Assigned', 6, '1', '2023-04-09 23:05:05.000000'),
       ('three_days_before_deadline', 'Three days before deadline', 6, '2', '2023-04-09 23:05:05.000000'),
       ('two_days_before_deadline', 'Two days before deadline', 6, '4', '2023-04-09 23:05:05.000000'),
       ('one_day_before_deadline', 'One day before deadline', 6, '8', '2023-04-09 23:05:05.000000'),
       ('deadline', 'Deadline', 6, '16', '2023-04-09 23:05:05.000000'),
       ('overdue', 'Overdue', 6, '32', '2023-04-09 23:05:05.000000');

insert into profile (id, last_failed_login, last_login, mail_notifications)
values (1, null, null, 49),
       (2, null, null, 14);

delete from contact;
insert into contact (id, code, value1)
values (1, 'skype', 'userSkype'),
       (1, 'mobile', '+01234567890'),
       (1, 'website', 'user.com'),
       (2, 'github', 'adminGitHub'),
       (2, 'tg', 'adminTg'),
       (2, 'vk', 'adminVk');

-- bugtracking
insert into project (id, code, title, description, type_code, startpoint, endpoint, parent_id)
values (2, 'task tracker', 'PROJECT-1', 'test project', 'task tracker', '2023-04-09 23:05:05.000000', null, null);

insert into sprint (id, status_code, startpoint, endpoint, title, project_id)
values (1, 'planning', '2023-04-09 23:05:05.000000', '2023-04-12 23:05:12.000000', 'Sprint-1', 2);

insert into task (id, title, description, type_code, status_code, priority_code, estimate, updated, project_id, sprint_id, parent_id, startpoint, endpoint)
values (2, 'Task-1', 'short test task', 'task', 'in progress', 'high', null, null, 2, 1, null, '2023-04-09 23:05:05.000000', null),
       (3, 'Task-2', 'test 2 task', 'bug', 'ready', 'normal', null, null, 2, 1, null, '2023-04-09 23:05:05.000000', null),
       (5, 'Task-4', 'test 4', 'bug', 'in progress', 'normal', null, null, 2, 1, null, '2023-04-09 23:05:05.000000', null),
       (4, 'Task-3', 'test 3 descr', 'task', 'done', 'low', null, null, 2, 1, null, '2023-04-09 23:05:05.000000', null);

insert into user_belong (id, object_id, object_type, user_id, user_type_code, startpoint, endpoint)
values (3, 2, 2, 2, 'admin', '2023-04-09 23:05:05.000000', null),
       (4, 3, 2, 2, 'admin', '2023-04-09 23:05:05.000000', null),
       (5, 4, 2, 2, 'admin', '2023-04-09 23:05:05.000000', null),
       (6, 5, 2, 2, 'admin', '2023-04-09 23:05:05.000000', null);
