/*
 * Brent Turner
 * 
 * UserInterfaces
 * Helper
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
 * This class is meant to display information in the text area of Hospital.fxml
 * 
 * @author brent turner
 */
public class Helper {
 
    //Doctor info
    public static String info(Doctor doctor) {

        return String.format(
            "id: %s\n"
            + "name: %s\n"
            + "Specialty: %s\n",
            doctor.getId(),
            doctor.getName(),
            doctor.getSpecialty().getName()  //access the object's name getter
        );
     }
    //Patient info
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

    //treatment report info
    public static String info(Treatment treatment) {
      return String.format(
          treatment.getReport()
      );
    }
    
   //date info
    public static java.sql.Date currentDate() {
      long now = new java.util.Date().getTime();
      java.sql.Date date = new java.sql.Date(now);
      return date;
    }

}
