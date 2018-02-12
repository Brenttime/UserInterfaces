create table specialty (
  id integer primary key not null,
  name varchar(255) unique not null collate nocase
)
