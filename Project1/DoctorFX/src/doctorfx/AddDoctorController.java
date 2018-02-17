/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doctorfx;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class AddDoctorController implements Initializable {

    private HospitalController mainController;
 
    void setMainController(HospitalController mainController) {
      this.mainController = mainController;
    }
    
    @FXML
    private TextField lastNameField;
        
    @FXML
    private TextField firstNameField;

    @FXML
    private ListView<String> specialtySelection;

    @FXML
    private void add(Event event) {
        try {
          String specialty = specialtySelection.getSelectionModel().getSelectedItem();
          String firstName = firstNameField.getText().trim();
          String lastName = lastNameField.getText().trim();
          String name = lastName + "," + firstName;
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
          
          if(specialty == null)
          {
               throw new ExpectedException("Please select a specialty");
          }
          
          Specialty specialties = ORM.findOne(Specialty.class, 
                  "where name=?", new Object[]{specialty});
          
            // put it into the database
            Doctor newDoctor = new Doctor(name, specialties.getId(), specialty);
            ORM.store(newDoctor);

            // access the features of LibraryController
            ListView<Doctor> doctorList = mainController.getDoctorList();
            TextArea display = mainController.getDisplay();

            // reload booklist from database
            doctorList.getItems().clear();
            Collection<Doctor> doctors = ORM.findAll(Doctor.class,
                    "order by name");
            for (Doctor doctor : doctors) {
                doctorList.getItems().add(doctor);
                
                Specialty speciality = ORM.findOne(Specialty.class,
                    "where id=?", new Object[]{doctor.getSpecialty_Id()});

                doctor.setSpecialty(speciality.getName());
            }

            // select in list and scroll to added book
            doctorList.getSelectionModel().select(newDoctor);
            doctorList.scrollTo(newDoctor);

            doctorList.requestFocus();
            mainController.setLastFocused(doctorList);

            // set text display to added book
            display.setText(Helper.info(newDoctor));

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
        try{
            Collection<Specialty> specialties = ORM.findAll(Specialty.class);
            for (Specialty specialty : specialties) {
                specialtySelection.getItems().add(specialty.getName());
            }
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }    
    
}
