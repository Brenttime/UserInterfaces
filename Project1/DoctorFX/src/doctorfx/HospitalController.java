/*
 * Brent Turner
 * 
 * Userinterface
 * HospitalController
 */
package doctorfx;

import java.io.IOException;
import models.Doctor;
import models.Patient;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.DBProps;
import models.Treatment;
import models.ORM;
import models.Specialty;

/**
 * HospitalController is a controller class for the Hospital Panel. This is
 * the general UI controller that navigates to other panels, and displays 
 * the database information.
 *
 * @author Brent Turner
 */
public class HospitalController implements Initializable {
    
    private Node lastFocused = null;
    
    private final Collection<Integer> patientTreatmentIds = new HashSet<>();
    private final Collection<Integer> doctorTreatmentIds = new HashSet<>();
 
    void setLastFocused(Node lastFocused) {
      this.lastFocused = lastFocused;
    }
  
    @FXML
    private ListView<Doctor> doctorList;
    
    @FXML
    private ListView<Patient> patientList;
    
    @FXML
    private TextArea display;
    
    ListView<Doctor> getDoctorList() {
      return doctorList;
    }

    ListView<Patient> getPatientList() {
      return patientList;
    }
 
    TextArea getDisplay() {
      return display;
    }
  
    //user clicks on a doctor handler
    @FXML
    private void doctorSelect(Event event) {
        Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
        if (doctor == null) {
          // you get here if selecting in the list with nothing selected
          // outside of any element
          if (lastFocused != null) {
            lastFocused.requestFocus();
          }
          return;
        }
        
        lastFocused = doctorList;

        try{
            // get the doctor of this user
            Collection<Treatment> treatments = ORM.findAll(Treatment.class,
              "where doctor_id=?", new Object[]{doctor.getId()});

            doctorTreatmentIds.clear();
            for (Treatment treatment : treatments) {
              doctorTreatmentIds.add(treatment.getPatientId());
            }
            
            // pick up style changes in doctorList
            patientList.refresh();
            
            //System.out.println("doctorList selection: " + doctor);
            display.setText(Helper.info(doctor));
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }
    
    //user clicks on a patient handler
    @FXML
    private void patientSelect(Event event) {
      try {
        Patient patient = patientList.getSelectionModel().getSelectedItem();
        
        if (patient == null) {
          if (lastFocused != null) {
            lastFocused.requestFocus();
          }
          return;
        }
        lastFocused = patientList;

        // get the doctor of this user
        Collection<Treatment> treatments = ORM.findAll(Treatment.class,
          "where patient_id=?", new Object[]{patient.getId()});

        patientTreatmentIds.clear();
        for (Treatment treatment : treatments) {
          patientTreatmentIds.add(treatment.getDoctorId());
        }

        // pick up style changes in doctorList
        doctorList.refresh();
        
        display.setText(Helper.info(patient));
      }
      catch (Exception ex) {
        ex.printStackTrace(System.err);
        System.exit(1);
      }
    }
    
    //focus reset method
    @FXML
    private void refocus(Event event) {
      if (lastFocused != null) {
        lastFocused.requestFocus();
      }
    }
    
    //used to display the report for a patient doctor combo
    @FXML
    private void treatmentReport(Event event)
    {
        try {
            
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            
            //Ensure user selects both a patient and a doctor
            if(patient == null || doctor == null) {
                throw new ExpectedException("must select a doctor and a patient");
            }
            
            Treatment treatment = ORM.findOne(Treatment.class, 
                "where patient_id=? and doctor_id=?", 
                new Object[]{patient.getId(), doctor.getId()});

            //Ensure the patient has the highlighted doctor
            if(treatment == null)
            {
                throw new ExpectedException("patient does not have doctor "+ doctor.getName());
            }
            
            //treatment displays to the textarea
            display.setText(Helper.info(treatment));

            
        }
        catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            if (lastFocused != null) {
              lastFocused.requestFocus();
            }
        }
        catch(Exception ex)
        {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }

    //clear button handler
    @FXML
    private void clear(Event event)
    {
        //clear focused
        patientList.getSelectionModel().clearSelection();
        doctorList.getSelectionModel().clearSelection();
        
        //clear highlights
        patientTreatmentIds.clear();
        doctorTreatmentIds.clear();
        display.clear();
        
        //get current focus
        lastFocused.requestFocus();
        
        //refresh the lists
        patientList.refresh();
        doctorList.refresh();
    }
    
    //Handle reorder patients by name selection
    @FXML
    private void patientsByName(Event event)
    {
        try{
            //clear list
            patientList.getItems().clear();
            
            Collection<Patient> patients = ORM.findAll(Patient.class,
                "order by name"); //where sql searches by name
            
            //reinitialize items on list
            for (Patient patient : patients) {
              patientList.getItems().add(patient);
            }
            
            patientList.refresh();
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }
    
    //Handle reorder patients by date of admittance
    @FXML
    private void patientsByAdmitted(Event event)
    {
        try{
            //Clear the list
            patientList.getItems().clear();
            
            Collection<Patient> patients = ORM.findAll(Patient.class,
                "order by admitted"); //set order via sql
            
            //Reinitialize the list
            for (Patient patient : patients) {
              patientList.getItems().add(patient);
            }
            
            patientList.refresh();
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }
    
    //Link button handler - links patient and doctor
    @FXML
    private void patientLink(Event event)
    {
        try {
            //What is current selected in both doctor and patient lists
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            
            //Ensure user selected both a doctor and patient
            if (patient == null || doctor == null) {
              throw new ExpectedException("must select doctor and patient");
            }
            
            //Get current status of the selections
            Treatment treatment = ORM.findOne(Treatment.class, 
              "where patient_id=? and doctor_id=?", 
              new Object[]{patient.getId(), doctor.getId()}
            );
            
            //Ensure the two are not already linked
            if (treatment != null) {
              throw new ExpectedException("user already has this doctor");
            }
            
            //Get all current speicalites being treated for this patient
            Collection<Treatment> currentTreatments = 
                    ORM.findAll(Treatment.class,
                "where patient_id=?", new Object[]{patient.getId()});
                        
            /*
             * Ensure this doctor does not have a speciality the patient 
             * is already being treated for
             */
            for (Treatment treatments : currentTreatments) {
                Doctor otherDoctors = ORM.findOne(Doctor.class, 
                        "where id=?", new
                        Object[]{treatments.getDoctorId()});
                if (doctor.getSpecialty() == otherDoctors.getSpecialty()) {
                  throw new ExpectedException(
                          "patient already has a doctor with specialty " + 
                                  doctor.getName());        
                }
            }
            
            //Start the new treatment with a blank report
            treatment = new Treatment(patient, doctor, "");
            
            //store it to the database
            ORM.store(treatment);
            ORM.store(doctor);
            
            //add the highlight to the ui
            patientTreatmentIds.add(doctor.getId());
            doctorTreatmentIds.add(patient.getId());
            
            //refresh the ui lists
            doctorList.refresh();
            patientList.refresh();
            
            //lastFocused.requestFocus();    // there must be a lastFocused
            if (lastFocused == doctorList) {
              display.setText(Helper.info(doctor));
            }
          }
          catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            if (lastFocused != null) {
              lastFocused.requestFocus();
            }
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
          }
    }
    
    //UnLink button handler - unlinks patient and doctor
    @FXML
    private void patientUnlink(Event event)
    {
        try {
            //What is current selected in both doctor and patient lists
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            
             //Ensure user selected both a doctor and patient
            if (patient == null || doctor == null) {
              throw new ExpectedException("must select doctor and patient");
            }

            // get the treatment from database
            Treatment treatment = ORM.findOne(Treatment.class, 
              "where patient_id=? and doctor_id=?", 
              new Object[]{patient.getId(), doctor.getId()}
            );
            
            //Ensure the two are not already unlinked
            if (treatment == null) {
              throw new ExpectedException("patient does not have this doctor");
            }
            
            // remove the treatment
            ORM.remove(treatment);
            ORM.store(doctor);

            // reset doctorlist & patientlist
            patientTreatmentIds.remove(doctor.getId());
            doctorTreatmentIds.remove(patient.getId());
            doctorList.refresh();
            patientList.refresh();
            
            lastFocused.requestFocus();    // there must be a lastFocused
            if (lastFocused == doctorList) {
              display.setText(Helper.info(doctor));
            }
          }
          catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            if (lastFocused != null) {
              lastFocused.requestFocus();
            }
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
          }
    }
    
    //Remove patient handler
    @FXML
    private void removePatient(Event event) {
        try {
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            
            //ensure a patient is selected
            if (patient == null) {
              throw new ExpectedException("must select patient");
            }
            
            //see if the user is sure they would like to delete a user
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
              return;
            }

            // Get treatments and remove them
            Collection<Treatment> treatments = ORM.findAll(Treatment.class,
                "where patient_id=?", new Object[]{patient.getId()});
            for(Treatment treatment : treatments)
            {
                ORM.remove(treatment);
            }
                        
            // remove from patient table
            ORM.remove(patient);

            // remove from list
            patientList.getItems().remove(patient);
            patientList.getSelectionModel().clearSelection();

            if (lastFocused == patientList) {
              display.setText("");
              lastFocused = null;
            }
        }
        catch (ExpectedException ex) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setContentText(ex.getMessage());
          alert.show();
          if (lastFocused != null) {
            lastFocused.requestFocus();
          }
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }

    //add patient handler
    @FXML
    private void addPatient(Event event) {
        try {
          // get fxmlLoader
          URL fxml = getClass().getResource("AddPatient.fxml");
          FXMLLoader fxmlLoader = new FXMLLoader(fxml);
          fxmlLoader.load();

          // get scene from loader
          Scene scene = new Scene(fxmlLoader.getRoot());

          // create a stage for the scene
          Stage dialogStage = new Stage();            
          dialogStage.setScene(scene);


          // specify dialog title
          dialogStage.setTitle("Add a Patient");

          // make it block the application
          dialogStage.initModality(Modality.APPLICATION_MODAL);

          // invoke the dialog
          dialogStage.show();
          //===============================================   

          // get controller from fxmlLoader
          AddPatientController dialogController = fxmlLoader.getController();

          // pass the hospitalController to the dialog controller
          dialogController.setMainController(this);
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }

    //add doctor handler
    @FXML
    private void addDoctor(Event event) {
        try {
          // get fxmlLoader
          URL fxml = getClass().getResource("AddDoctor.fxml");
          FXMLLoader fxmlLoader = new FXMLLoader(fxml);
          fxmlLoader.load();

          // get scene from loader
          Scene scene = new Scene(fxmlLoader.getRoot());

          // create a stage for the scene
          Stage dialogStage = new Stage();            
          dialogStage.setScene(scene);

          dialogStage.setHeight(250);

          // specify dialog title
          dialogStage.setTitle("Add a Doctor");

          // make it block the application
          dialogStage.initModality(Modality.APPLICATION_MODAL);

          // invoke the dialog
          dialogStage.show();
          //===============================================   

          // get controller from fxmlLoader
          AddDoctorController dialogController = fxmlLoader.getController();

          // pass the HospitalController to the dialog controller
          dialogController.setMainController(this);
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }

    //add a specialty handler
    @FXML
    private void addSpeciality(Event event) {
        try {
          // get fxmlLoader
          URL fxml = getClass().getResource("AddSpecialty.fxml");
          FXMLLoader fxmlLoader = new FXMLLoader(fxml);
          fxmlLoader.load();

          // get scene from loader
          Scene scene = new Scene(fxmlLoader.getRoot());

          // create a stage for the scene
          Stage dialogStage = new Stage();            
          dialogStage.setScene(scene);

          // specify dialog title
          dialogStage.setTitle("Add a Specialty");

          // make it block the application
          dialogStage.initModality(Modality.APPLICATION_MODAL);

          // invoke the dialog
          dialogStage.show();
          //===============================================   

          // get controller from fxmlLoader
          AddSpecialtyController dialogController = fxmlLoader.getController();

          // pass the HospitalController to the dialog controller
          dialogController.setMainController(this);
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }
    
    //modify report handler
    @FXML
    private void modifyReport(Event event) {
        try {
            
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            
            //Ensure both a patient and doctor are selected
            if(patient == null || doctor == null) {
                throw new ExpectedException("must select a doctor and a patient");
            }
            
            Treatment treatment = ORM.findOne(Treatment.class, 
                "where patient_id=? and doctor_id=?", 
                new Object[]{patient.getId(), doctor.getId()});
            
            //make sure their exists a treatment between the selections
            if(treatment == null)
            {
                throw new ExpectedException("patient does not have doctor "+ doctor.getName());
            }
            
            // get fxmlLoader
            URL fxml = getClass().getResource("ModifyReport.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            fxmlLoader.load();

            // get scene from loader
            Scene scene = new Scene(fxmlLoader.getRoot());

            // create a stage for the scene
            Stage dialogStage = new Stage();            
            dialogStage.setScene(scene);

            // specify dialog title
            dialogStage.setTitle("Add a Specialty");

            // make it block the application
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // invoke the dialog
            dialogStage.show();
            //===============================================   

            // get controller from fxmlLoader
            ModifyReportController dialogController = fxmlLoader.getController();

            // pass the HospitalController to the dialog controller
            dialogController.setMainController(this);
            
            
            // query window closing
            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
              @Override
              public void handle(WindowEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Are you sure you want to exit this dialog?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK) {
                  event.consume();
                }
              }
            });

            // set doctor to be modified in dialog
            dialogController.setTreatmentToModify(treatment);            
            
        }
        catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            if (lastFocused != null) {
              lastFocused.requestFocus();
            }
        }
        catch(Exception ex)
        {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            ORM.init(DBProps.getProps());
            
            Collection<Doctor> doctors = ORM.findAll(Doctor.class, 
                    "order by name");
            
            //add initial database status to the ui
            for (Doctor doctor : doctors) {
                doctorList.getItems().add(doctor);
                
                Specialty speciality = ORM.findOne(Specialty.class,
                        "where id=?", new Object[]{doctor.getSpecialty_Id()});

                doctor.setSpecialty(speciality.getName());
            }
            
            Collection<Patient> patients = ORM.findAll(Patient.class,
                    "order by name");
            //add initial database status to the ui
            for (Patient patient : patients) {
              patientList.getItems().add(patient);
            }
            
            //display the names of the objects on doctor list
            DoctorCellCallBack doctorCellCallback = new DoctorCellCallBack();
            doctorList.setCellFactory(doctorCellCallback);

            //display the names of the objects on patient list
            PatientCellCallBack patientCellCallback = new PatientCellCallBack();
            patientList.setCellFactory(patientCellCallback);  
            
            //set up the highlights for linked treatments
            doctorCellCallback.setHightlightedIds( patientTreatmentIds );
            patientCellCallback.setHightlightedIds( doctorTreatmentIds );
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }    
}
