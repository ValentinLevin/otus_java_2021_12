insert into client(id, name)
values (1, 'client 1 name'), (2, 'client 2 name');

ALTER SEQUENCE public.client_id_seq RESTART 3 CACHE 1;

insert into address(id, street, client_id)
values (1, 'client 1 street', 1), (2, 'client 2 street', 2);
ALTER SEQUENCE public.address_id_seq RESTART 3 CACHE 1;

insert into phone(id, client_id, number)
values
    (1, 1, 'client 1 phone 1'), (2, 1, 'client 1 phone 2'), (3, 1, 'client 1 phone 3'),
    (4, 2, 'client 2 phone 1');
ALTER SEQUENCE public.phone_id_seq RESTART 5 CACHE 1;
