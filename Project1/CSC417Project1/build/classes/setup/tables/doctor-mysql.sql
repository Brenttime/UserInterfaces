create table doctor (
  id integer auto_increment primary key not null,
  name varchar(255) unique not null,
  specialty_id integer not null,
  foreign key(specialty_id) references specialty(id)
)
