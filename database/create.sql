create table users (
    id text primary key,
    name text not null,
    date timestamp default now()
);