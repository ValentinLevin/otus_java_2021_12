create table client (
    id bigserial primary key,
    address_id bigint,
    name varchar not null
);

create table address (
    id bigserial primary key,
    street varchar not null
);

create table phone (
    id bigserial primary key,
    client_id bigint,
    number varchar not null
);

create table "user" (
    id bigserial primary key ,
    username varchar not null unique,
    password varchar not null
);