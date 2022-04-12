-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

 */

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
