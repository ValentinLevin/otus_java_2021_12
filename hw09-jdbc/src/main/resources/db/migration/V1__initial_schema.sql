create table public.client
(
    id   bigserial not null primary key,
    name varchar(50)
);

create table public.manager
(
    no   bigserial not null primary key,
    label varchar,
    param1 varchar
);