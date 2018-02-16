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
public final class Patient extends Model{
 
  public static final String TABLE = "patient";
 
  private int id = 0;
  private String name;
  private String admitted;
 
  Patient() {}
 
  public Patient(String name, String admitted) {
    this.name = name;
    this.admitted = admitted;
  }
 
  @Override
  public int getId() {
    return id;
  }
 
  public String getName() {
    return name;
  }
 
  public String getAdmitted() {
    return admitted;
  }
 
  public void setName(String name) {
    this.name = name;
  }
 
  public void setAdmitted(String admitted) {
    this.admitted = admitted;
  }
 
  @Override
  void load(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    name = rs.getString("name");
    admitted = rs.getString("admitted");
  }
 
  @Override
  void insert() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "insert into %s (name,admitted) values (?,?)", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setString(++i, admitted);
    st.executeUpdate();
    id = ORM.getMaxId(TABLE);
  }
 
  
  @Override
  void update() throws SQLException {
    Connection cx = ORM.connection();
    String sql = String.format(
        "update %s set name=?,admitted=? where id=?", TABLE);
    PreparedStatement st = cx.prepareStatement(sql);
    int i = 0;
    st.setString(++i, name);
    st.setString(++i, admitted);
    st.setInt(++i, id);
    st.executeUpdate();
  }
 
  @Override
  public String toString() {
    return String.format("(%s,%s,%s)", id, name, admitted);
  }
}
