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
import static models.Doctor.TABLE;

/**
 *
 * @author Brenttime
 */
public class Specialty extends Model{
    public static final String TABLE = "specialty";
    
    private int id = 0;
    private String name;

    Specialty() {}

    public Specialty(String name) {
      this.name = name;
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

   @Override
    void load(ResultSet rs) throws SQLException {
      id = rs.getInt("id");
      name = rs.getString("name");
    }

   @Override
    void insert() throws SQLException {
      Connection cx = ORM.connection();
      String sql = String.format(
          "insert into %s (name) values (?)", TABLE);
      PreparedStatement st = cx.prepareStatement(sql);
      int i = 0;
      st.setString(++i, name);
      st.executeUpdate();
      id = ORM.getMaxId(TABLE);
    }

   @Override
    void update() throws SQLException {
      Connection cx = ORM.connection();
      String sql = String.format(
          "update %s set name=? where id=?", TABLE);
      PreparedStatement st = cx.prepareStatement(sql);
      int i = 0;
      st.setString(++i, name);
      st.setInt(++i, id);
      st.executeUpdate();
    }

    @Override
    public String toString() {
      return String.format("(%s,%s)", id, name);
    }

}
