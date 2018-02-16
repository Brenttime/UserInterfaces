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
public final class Doctor extends Model {
    
 public static final String TABLE = "doctor";
 
  private int id = 0;
  private String name;
  private String specialty;
 
  Doctor() {}
 
  public Doctor(String name, String specialty) {
    this.name = name;
    this.specialty = specialty;
  }
 
 @Override
  public int getId() {
    return id;
  }
 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getSpecialty()
  {
    return specialty;
  }
  
  public void setSpecialty(String specialty)
  {
      this.specialty = specialty;
  }

  
 @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    name = rs.getString("name");
    specialty = rs.getString("specialty_id");
  }
 
 @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "insert into %s (name,specialty_id) values (?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setString(++i, specialty);
    st.executeUpdate();
    id = ORM.getMaxId(TABLE);
  }
 
 @Override
  void update() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "update %s set name=?, specialty_id=? where id=?", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setString(++i, specialty);
    st.setInt(++i, id);
    st.executeUpdate();
  }
 
  @Override
  public String toString() {
    return String.format("(%s,%s,%s)", id, name, specialty);
  }

}
