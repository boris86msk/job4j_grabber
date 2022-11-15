create table post (
  id serial primary key,
  name varchar(150),
  text text,
  link varchar(150) UNIQUE,
  created timestamp
);