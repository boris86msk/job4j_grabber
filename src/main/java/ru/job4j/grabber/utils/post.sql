create table post (
  id serial primary key,
  name varchar(150),
  text text(2000),
  link varchar(150) UNIQUE,
  created timestamp
);