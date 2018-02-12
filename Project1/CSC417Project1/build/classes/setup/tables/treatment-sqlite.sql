create table treatment (
  id integer primary key not null,
  doctor_id integer not null,
  patient_id integer not null,
  report text not null,
  unique(doctor_id, patient_id),
  foreign key(doctor_id) references doctor(id),
  foreign key(patient_id) references patient(id)
)
