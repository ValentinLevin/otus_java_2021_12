create table client (
    id bigserial primary key,
    name varchar not null
);

create table address (
    id bigserial primary key,
    street varchar not null,
    client_id bigint not null unique
);

create table phone (
    id bigserial primary key,
    client_id bigint,
    number varchar not null
);