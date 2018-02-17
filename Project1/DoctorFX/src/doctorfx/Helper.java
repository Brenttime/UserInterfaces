/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doctorfx;

import java.util.logging.Level;
import java.util.logging.Logger;
import models.Doctor;
import models.ORM;
import models.Patient;
import models.Specialty;
import models.Treatment;

/**
 *
 * @author brent
 */
public class Helper {
 
    
    public static String info(Doctor doctor) {

        return String.format(
            "id: %s\n"
            + "name: %s\n"
            + "Specialty: %s\n",
            doctor.getId(),
            doctor.getName(),
            doctor.getSpecialty()
        );
     }
 
    public static String info(Patient patient) {
      return String.format(
          "id: %s\n"
          + "name: %s\n"
          + "Admitted: %s\n",
          patient.getId(),
          patient.getName(),
          patient.getAdmitted()
      );
    }

    
    public static String info(Treatment treatment) {
      return String.format(
          treatment.getReport()
      );
    }
    
   
    public static java.sql.Date currentDate() {
      long now = new java.util.Date().getTime();
      java.sql.Date date = new java.sql.Date(now);
      return date;
    }

}
