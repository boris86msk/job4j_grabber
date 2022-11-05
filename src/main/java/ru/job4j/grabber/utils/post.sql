create table post (
  id serial primary key,
  name varchar(150),
  text text UNIQUE,
  link varchar(150),
  created timestamp
);