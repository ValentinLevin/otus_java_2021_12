CREATE TABLE public.client (
   id BIGSERIAL,
   name VARCHAR(50),
   CONSTRAINT client_pkey PRIMARY KEY(id)
) ;

ALTER TABLE public.client
    OWNER TO usr;

CREATE TABLE public.manager (
    no BIGSERIAL,
    label VARCHAR,
    param1 VARCHAR,
    CONSTRAINT manager_pkey PRIMARY KEY(no)
) ;

ALTER TABLE public.manager
    OWNER TO usr;