package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class ORM {
  
  private static final boolean DEBUG = false;

  private static java.sql.Connection cx; // one connection per class

  private static String url;
  private static String username;
  private static String password;

  public static void init(Properties props) throws ClassNotFoundException {
    url = props.getProperty("url");
    username = props.getProperty("username");
    password = props.getProperty("password");
    String driver = props.getProperty("driver");
    if (driver != null) {
      Class.forName(driver); // load driver if necessary
    }
  }

  static Connection connection() throws SQLException {
    if (url == null) {
      throw new SQLException("ORM not initialized");
    }
    if (cx == null || cx.isClosed()) {
      if (DEBUG) {
        System.out.println("**** new connection");
      }
      cx = DriverManager.getConnection(url, username, password);
    }
    return cx;
  }

  public static int store(Model m) throws SQLException {
    if (m.getId() == 0) {
      if (DEBUG) {
        System.out.println("**store => insert***");
      }
      m.insert();
    }
    else {
      if (DEBUG) {
        System.out.println("**store => update***");
      }
      m.update();
    }
    return m.getId();
  }

  public static <T extends Model> T load(Class C, int id) throws Exception {
    String table = (String) C.getField("TABLE").get(null);

    cx = connection();
    String sql = String.format("select * from %s where id=?", table);
    PreparedStatement st = cx.prepareStatement(sql);
    st.setInt(1, id);
    if (DEBUG) {
      System.out.println("**load**: " + st);
    }
    ResultSet rs = st.executeQuery();
    if (!rs.next()) {
      return null;
    }
    @SuppressWarnings("unchecked")
    T m = (T) C.newInstance();
    m.load(rs);
    return m;
  }
  
  public static <T extends Model> boolean remove(T obj) throws Exception {
    if (obj.getId() == 0) {
      return false;
    }
    String table = (String) obj.getClass().getField("TABLE").get(null);

    cx = connection();
    String sql = String.format("delete from %s where id=?", table);
    PreparedStatement st = cx.prepareStatement(sql);
    st.setInt(1, obj.getId());
    if (DEBUG) {
      System.out.println("**remove**: " + st);
    }
    int affected_rows = st.executeUpdate();
    return affected_rows > 0;
  }

  public static <T extends Model> T findOne(Class C, String extra, Object[] values)
    throws Exception {
    if (extra == null) {
      extra = "";
    }
    String table = (String) C.getField("TABLE").get(null);

    cx = connection();
    String sql = String.format("select * from %s %s", table, extra);
    PreparedStatement st = cx.prepareStatement(sql);
    if (values != null) {
      int pos = 1;
      for (Object value : values) {
        st.setObject(pos++, value);
      }
    }
    if (DEBUG) {
      System.out.println("**findOne**: " + st);
    }
    ResultSet rs = st.executeQuery();
    if (!rs.next()) {
      return null;
    }
    @SuppressWarnings("unchecked")
    T m = (T) C.newInstance();
    m.load(rs);
    return m;
  }
  
  public static <T extends Model> Collection<T> findAll(
    Class C, String extra, Object[] inserts) throws Exception 
  {
    String table = (String) C.getField("TABLE").get(null);

    if (extra == null) {
      extra = "";
    }
    cx = connection();
    String sql = String.format("select * from %s %s", table, extra);
    PreparedStatement st = cx.prepareStatement(sql);

    if (inserts != null) {
      int pos = 1;
      for (Object value : inserts) {
        st.setObject(pos++, value);
      }
    }
    if (DEBUG) {
      System.out.println("**findAll**: " + st);
    }
    ResultSet rs = st.executeQuery();
    Set<T> L = new LinkedHashSet<>();
    while (rs.next()) {
      @SuppressWarnings("unchecked")
      T m = (T) C.newInstance();
      m.load(rs);
      L.add(m);
    }
    return L;
  }

  public static <T extends Model> Collection<T> findAll(Class C, String extra) 
    throws Exception 
  {
    return findAll(C, extra, null);
  }

  public static <T extends Model> Collection<T> findAll(Class C) throws Exception {
    return findAll(C, null, null);
  }
  
  static int getMaxId(String table) throws SQLException {
    cx = connection();
    String sql = String.format("select max(id) from %s", table);
    Statement st = cx.createStatement();
    if (DEBUG) {
      System.out.println("**getMaxId**");
    }
    ResultSet rs = st.executeQuery(sql);
    rs.next();
    return rs.getInt(1);
  }
}