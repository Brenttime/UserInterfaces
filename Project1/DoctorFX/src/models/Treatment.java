/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author brent
 */
public class Treatment extends Model{
  
  public static final String TABLE = "treatment";

  private int id = 0;
  private int patient_id;
  private int doctor_id;
  private String report;
  
  // must have default constructor accessible to the package
  Treatment() {}
 
  public Treatment(Patient patient, Doctor doctor) {
    this.patient_id = patient.getId();
    this.doctor_id = doctor.getId();
  }
  
  @Override
  public int getId() {
    return this.id;
  }
  
  public int getDoctorId() {
    return doctor_id;
  }
 
  public int getPatientId() {
    return patient_id;
  }
 
  public String getReport()
  {
    return report;
  }
  
  public void setReport(String report)
  {
    this.report = report;
  }
  
  public Doctor getDoctor() {
    try {
      return ORM.load(Doctor.class, doctor_id);
    }
    catch(Exception ex) {
      System.err.println(ex.getMessage());
      return null;
    }
  }
 
  public Patient getPatient() {
    try {
      return ORM.load(Patient.class, patient_id);
    }
    catch(Exception ex) {
      System.err.println(ex.getMessage());
      return null;
    }
  }

  @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    doctor_id = rs.getInt("doctor_id");
    patient_id = rs.getInt("patient_id");
  }
 
  @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "insert into %s (doctor_id,patient_id,report) values (?,?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setInt(++i, doctor_id);
    st.setInt(++i, patient_id);
    st.setString(++i, report);
    st.executeUpdate();
    id = ORM.getMaxId(TABLE);
  }
 
  @Override
  void update() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "update %s set report=? where id=?", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, report);
    st.setInt(++i, id);
    st.executeUpdate();
  }
 
  @Override
  public String toString() {
    return String.format("(%s,%s,%s,%s)", id, doctor_id, patient_id, report);
  }
}
