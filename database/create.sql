create table users (
    id text primary key,
    name text not null,
    email text not null,
    password text not null,
    date timestamp default now()
);