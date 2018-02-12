create table patient (
  id integer auto_increment primary key not null,
  name varchar(255) unique not null,
  admitted date not null
)
