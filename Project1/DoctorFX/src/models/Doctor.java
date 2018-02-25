/*
 * Brent Turner
 * 
 * User Interfaces
 * Doctor class
 */
package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import static models.ORM.connection;

/**
 * Doctor object template
 * @author brent turner
 */
public final class Doctor extends Model {
    
 public static final String TABLE = "doctor";
 
  private int id = 0;
  private String name;
  
  //The specialty id that is found in the sql table
  private int specialty_Id;
  
  //The associated specialty with the specialty_id assocaited with this doctor
  private String specialty;
 
  //Empty Contructor
  Doctor() {}
 
  //Constructor
  public Doctor(String name, int specialty_Id, String specialty) {
    this.name = name;
    this.specialty_Id = specialty_Id;
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
  
  public int getSpecialty_Id()
  {
    return specialty_Id;
  }
  
  public void setSpecialty_Id(int specialty_Id)
  {
      this.specialty_Id = specialty_Id;
  }
    
  public String getSpecialty()
  {
    return specialty;
  }
  
  public void setSpecialty(String specialty)
  {
      this.specialty = specialty;
  }
  

 //ORM Functions below
  
 @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    name = rs.getString("name");
    specialty_Id = rs.getInt("specialty_id");
  }
 
 @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "insert into %s (name,specialty_id) values (?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setInt(++i, specialty_Id);
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
    st.setInt(++i, specialty_Id);
    st.setInt(++i, id);
    st.executeUpdate();
  }
 
  @Override
  public String toString() {
    return String.format("(%s,%s,%s)", id, name, specialty_Id);
  }

}
