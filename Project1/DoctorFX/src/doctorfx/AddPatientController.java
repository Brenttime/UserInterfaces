/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * FXML Controller class
 *
 * @author Brenttime
 */
public class AddPatientController implements Initializable {

    
    private HospitalController mainController;
 
    void setMainController(HospitalController mainController) {
      this.mainController = mainController;
    }
    
    public static java.sql.Date currentDate() {
      long now = new java.util.Date().getTime();
      Date date = new Date(now);
      return date;
    }
    
    @FXML
    private TextField lastNameField;
        
    @FXML
    private TextField firstNameField;

    
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

            // access the features of LibraryController
            ListView<Patient> patientList = mainController.getPatientList();
            TextArea display = mainController.getDisplay();

            // reload booklist from database
            patientList.getItems().clear();
            Collection<Patient> patients = ORM.findAll(Patient.class, 
                    "order by name");
            for (Patient patient : patients) {
                patientList.getItems().add(patient);
            }

            // select in list and scroll to added book
            patientList.getSelectionModel().select(newPatient);
            patientList.scrollTo(newPatient);

            patientList.requestFocus();
            mainController.setLastFocused(patientList);

            // set text display to added book
            display.setText(Helper.info(newPatient));

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

    @FXML
    private void cancel(Event event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
  
  
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    
    
    
}
