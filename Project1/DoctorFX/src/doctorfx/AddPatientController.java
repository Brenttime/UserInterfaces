/*
 * Brent Turner
 * 
 * Userinterface
 * AddPatientController
 */
package doctorfx;

import java.net.URL;
import java.sql.Date;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Doctor;
import models.ORM;
import models.Patient;
import models.Specialty;

/**
 * AddPatientController is a controller class for the Add Patient Panel
 *
 * @author Brent Turner
 */
public class AddPatientController implements Initializable {

    
    private HospitalController mainController;
 
    void setMainController(HospitalController mainController) {
      this.mainController = mainController;
    }
    
    //Used for admitted date
    public static java.sql.Date currentDate() {
      long now = new java.util.Date().getTime();
      Date date = new Date(now);
      return date;
    }
    
    @FXML
    private TextField lastNameField;
        
    @FXML
    private TextField firstNameField;

    //Add button handler
    @FXML
    private void add(Event event) {
        try {
          
          String firstName = firstNameField.getText().trim();
          String lastName = lastNameField.getText().trim();
          String name = lastName + "," + firstName;
          Date admitted = currentDate();
          
          int specialty_id;
          
          if(lastName.length() == 0)
          {
              throw new ExpectedException("A last name must be given");
          }
          if(firstName.length() == 0)
          {
              throw new ExpectedException("A first name must be given");
          }
          
          Doctor doctorWithName =
            ORM.findOne(Doctor.class, "where name=?", new Object[]{name});
          if(doctorWithName != null)
          {
              throw new ExpectedException("existing doctor with same name");
          }
          
          
          
            // put it into the database
            Patient newPatient = new Patient(name, admitted);
            ORM.store(newPatient);

            // access the features of hosptialController
            ListView<Patient> patientList = mainController.getPatientList();
            TextArea display = mainController.getDisplay();

            // reload patientlist from database
            patientList.getItems().clear();
            Collection<Patient> patients = ORM.findAll(Patient.class, 
                    "order by name");
            for (Patient patient : patients) {
                patientList.getItems().add(patient);
            }

            // select in list and scroll to added patient
            patientList.getSelectionModel().select(newPatient);
            patientList.scrollTo(newPatient);

            patientList.requestFocus();
            mainController.setLastFocused(patientList);

            // set text display to added patient
            display.setText(Helper.info(newPatient));

            //all done so hide the panel
            ((Button) event.getSource()).getScene().getWindow().hide();
        }
        catch (ExpectedException ex) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setContentText(ex.getMessage());
          alert.show();
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }

    //if user cancels the add
    @FXML
    private void cancel(Event event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
  
  
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //no initialize code needed
    }    
    
    
}
