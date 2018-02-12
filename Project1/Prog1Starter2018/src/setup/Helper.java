package setup;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import models.DBProps;

class Helper {

  static String getResourceContent(String filename) throws IOException {
    InputStream istr = Helper.class.getResourceAsStream(filename);
    if (istr == null) {
      throw new IOException("Missing file: " + filename);
    }
    Scanner s = new Scanner(istr).useDelimiter("\\A");
    return s.next();
  }

  public static void createTables(Properties props) throws
      IOException, SQLException, ClassNotFoundException {

    String url = props.getProperty("url");
    String username = props.getProperty("username");
    String password = props.getProperty("password");
    String driver = props.getProperty("driver");
    if (driver != null) {
      Class.forName(driver); // load driver if necessary
    }
    Connection cx = DriverManager.getConnection(url, username, password);

    @SuppressWarnings("unchecked")
    ArrayList<String> create_order
        = new ArrayList(Arrays.asList("specialty", "patient", "doctor", "treatment"));

    @SuppressWarnings("unchecked")
    ArrayList<String> drop_order = (ArrayList<String>) create_order.clone();
    Collections.reverse(drop_order);

    Statement stmt = cx.createStatement();

    System.out.format("\n---- drop tables\n");
    for (String table : drop_order) {
      String sql = String.format("drop table if exists %s", table);
      System.out.println(sql);
      stmt.execute(sql);
    }

    System.out.format("\n---- create tables\n");
    for (String table : create_order) {
      String filename = String.format("tables/%s-%s.sql", table, DBProps.which);
      String sql = getResourceContent(filename).trim();
      System.out.println(sql);
      stmt.execute(sql);
    }
  }

  static void populateTables(Properties props)
      throws SQLException, ClassNotFoundException, IOException {
    String url = props.getProperty("url");
    String username = props.getProperty("username");
    String password = props.getProperty("password");
    String driver = props.getProperty("driver");
    if (driver != null) {
      Class.forName(driver); // load driver if necessary
    }
    Connection cx = DriverManager.getConnection(url, username, password);
    PreparedStatement stmt;

    // these are for internal use to correspond names to table id values
    Map<String, Integer> patientId = new HashMap<>();
    Map<String, Integer> specialtyId = new HashMap<>();
    Map<String, Integer> doctorId = new HashMap<>();
    int id;

    //========================================================
    System.out.println("\n--- add patients");

    stmt = cx.prepareStatement("insert into patient (name,admitted) values(?,?)");
    String[] patients = {
      "Carson,Susan",
      "Bard,Thomas",
      "Mcgillis,Alysa",
      "Carson,James",
      "Liggett,James",
      "Collier,Rosanne", 
      "Farney,Tommy", 
      "Maddock,Amal",
    };
    
    long millisPerDay = 24 * 60 * 60 * 1000;
    int days_ago = 3;
    id = 0;
    for (String name : patients) {
      long admitted = System.currentTimeMillis() - (days_ago++) * millisPerDay;
            
      stmt.setString(1, name);
      stmt.setDate(2, new java.sql.Date(admitted));
      stmt.execute();
      patientId.put(name, ++id);
    }

    //========================================================
    System.out.println("\n--- add specialties");

    stmt = cx.prepareStatement("insert into specialty (name) values(?)");

    String[] specialties = {
      "oncology",
      "cardiology",
      "dermatology",
      "anesthesiology",
      "child neurology",
      "family medicine",
      "pediatrics",};

    id = 0;
    for (String name : specialties) {
      stmt.setString(1, name);
      stmt.execute();
      specialtyId.put(name, ++id);
    }

    //========================================================
    System.out.println("\n--- add doctors");

    stmt = cx.prepareStatement(
        "insert into doctor (name, specialty_id) values(?,?)"
    );

    String[][] doctors = new String[][]{
      {"Guo,Cheryl", "oncology"},
      {"Howse,Manuel", "cardiology"},
      {"Lanier,Jennifer", "oncology"},
      {"Sippel,John", "cardiology"},
      {"Brady,Chad", "child neurology"},
      {"Mazer,Dominique", "pediatrics"},
      {"Wooding,Bernard", "family medicine"},};

    id = 0;
    for (String[] doctor : doctors) {
      String name = doctor[0];
      String specialty = doctor[1];

      stmt.setString(1, name);
      stmt.setInt(2, specialtyId.get(specialty));
      stmt.execute();
      doctorId.put(name, ++id);
    }

    //========================================================
    System.out.println("\n--- add treatments");

    stmt = cx.prepareStatement(
        "insert into treatment (doctor_id,patient_id,report) values(?,?,?)");

    String treatments[][] = new String[][]{
      {"Sippel,John", "Bard,Thomas", "Patient given basic cardiology tests."},
      {"Lanier,Jennifer", "Bard,Thomas", 
        "Patient presented abdominal pains.\n"
        + "Did initial oncology tests."
      },
      {"Lanier,Jennifer", "Mcgillis,Alysa", "Performed a CT scan."},
      {"Lanier,Jennifer", "Liggett,James", ""},
    };

    for (String[] treatment : treatments) {
      String doctor = treatment[0];
      String patient = treatment[1];
      String report = treatment[2];

      stmt.setInt(1, doctorId.get(doctor));
      stmt.setInt(2, patientId.get(patient));
      stmt.setString(3, report);
      stmt.execute();
    }
  }

  static void dumpTables(Properties props)
      throws SQLException, ClassNotFoundException, IOException {
    String url = props.getProperty("url");
    String username = props.getProperty("username");
    String password = props.getProperty("password");
    String driver = props.getProperty("driver");
    if (driver != null) {
      Class.forName(driver); // load driver if necessary
    }
    Connection cx = DriverManager.getConnection(url, username, password);
    Statement stmt = cx.createStatement();

    ResultSet rs;

    System.out.println("\n==> specialties");
    rs = stmt.executeQuery("select * from specialty");
    while (rs.next()) {
      int id = rs.getInt("id");
      String name = rs.getString("name");
      System.out.format("%s:%s\n", id, name);
    }

    System.out.println("\n==> patients");
    rs = stmt.executeQuery("select * from patient");
    while (rs.next()) {
      int id = rs.getInt("id");
      String name = rs.getString("name");
      Date admitted = rs.getDate("admitted");
      System.out.format("%s:%s:%s\n", id, name, admitted);
    }

    System.out.println("\n==> doctors");
    rs = stmt.executeQuery("select * from doctor");
    while (rs.next()) {
      int id = rs.getInt("id");
      String name = rs.getString("name");
      int specialty_id = rs.getInt("specialty_id");
      System.out.format("%s:%s:specialty=%s\n", id, name, specialty_id);
    }

    System.out.println("\n==> treatments");
    rs = stmt.executeQuery("select * from treatment");
    while (rs.next()) {
      int id = rs.getInt("id");
      int patient_id = rs.getInt("patient_id");
      int doctor_id = rs.getInt("doctor_id");
      String report = rs.getString("report");
      System.out.format(
          "%s:patient=%s:doctor=%s\n--- report ---\n%s\n--------------\n\n", 
          id, patient_id, doctor_id, report);
    }

  }
}
