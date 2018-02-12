create table doctor (
  id integer primary key not null,
  name varchar(255) unique not null collate nocase,
  specialty_id integer not null,
  foreign key(specialty_id) references specialty(id)
)
