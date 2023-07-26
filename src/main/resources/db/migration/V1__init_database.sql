drop table if exists users_roles;
drop table if exists users;
drop table if exists roles;

create table users
(
    id         bigserial,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    username   varchar(255) not null,
    email      varchar(255) not null,
    password   varchar(255) not null,

    constraint PK_users primary key (id),
    constraint UQ_email_users unique (email)
);

create table roles
(
    id   bigserial,
    name varchar(255) not null,

    constraint PK_roles primary key (id)
);

create table users_roles
(
    user_id bigint not null,
    role_id bigint not null,
    constraint PK_users_roles primary key (user_id, role_id),
    constraint FK_users_roles_users foreign key (user_id) references users (id),
    constraint FK_users_roles_roles foreign key (role_id) references roles (id)
);

insert into roles (name)
values ('ROLE_USER'),
       ('ROLE_ADMIN');

insert into users(first_name, last_name, username, email, password)
values ('Alex', 'Bon', 'user', 'alex_bon@gmail.com',
        '$2a$10$W.gGXo2sMH/ZiM6Rf.s4te.DpLvnoGL0I5wILH/fmWFOAIo7xVWMC'),
       ('John', 'Smith', 'admin_user', 'john_smith@gmail.com',
        '$2a$10$W.gGXo2sMH/ZiM6Rf.s4te.DpLvnoGL0I5wILH/fmWFOAIo7xVWMC');

insert into users_roles(user_id, role_id)
values (1, 1),
       (2, 2);