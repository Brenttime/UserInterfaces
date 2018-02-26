/*
 * Brent Turner
 * 
 * User Interfaces
 * Doctor class
 */
package models;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static models.ORM.connection;

/**
 * Doctor object template
 * @author brent turner
 */
public final class Doctor extends Model {
    
 public static final String TABLE = "doctor";
 
  private int id = 0;
  private String name;
  
  //The associated specialty with the specialty_id assocaited with this doctor
  private Specialty specialty;
 
  //Empty Contructor
  Doctor() {}
 
  //Constructor
  public Doctor(String name, Specialty specialty) {
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
    
  public Specialty getSpecialty()
  {
    return specialty;
  }
  
  public void setSpecialty(Specialty specialty)
  {
      this.specialty = specialty;
  }
  

 //ORM Functions below
 @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    name = rs.getString("name");
     try {
         Specialty doctorSpecialty = ORM.findOne(Specialty.class,
                 "where id=?", new Object[]{rs.getInt("specialty_id")});
         specialty = doctorSpecialty;
     } catch (Exception ex) {
        ex.printStackTrace(System.err);
        System.exit(1);
     }
  }
 
 @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "insert into %s (name,specialty_id) values (?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setInt(++i, specialty.getId());
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
    st.setInt(++i, specialty.getId());
    st.setInt(++i, id);
    st.executeUpdate();
  }
 
  @Override
  public String toString() {
    return String.format("(%s,%s,%s)", id, name, specialty.getName());
  }

}
