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
 *
 * @author brent
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
    
    @FXML
    private void refocus(Event event) {
      if (lastFocused != null) {
        lastFocused.requestFocus();
      }
    }
    
    @FXML
    private void treatmentReport(Event event)
    {
        try {
            
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            
            if(patient == null || doctor == null) {
                throw new ExpectedException("must select a doctor and a patient");
            }
            
            Treatment treatment = ORM.findOne(Treatment.class, 
                "where patient_id=? and doctor_id=?", 
                new Object[]{patient.getId(), doctor.getId()});

            if(treatment == null)
            {
                throw new ExpectedException("patient does not have doctor "+ doctor.getName());
            }

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

    @FXML
    private void clear(Event event)
    {
        patientList.getSelectionModel().clearSelection();
        doctorList.getSelectionModel().clearSelection();
        patientTreatmentIds.clear();
        doctorTreatmentIds.clear();
        display.clear();
        lastFocused.requestFocus();
        
        patientList.refresh();
        doctorList.refresh();
    }
    
    @FXML
    private void patientsByName(Event event)
    {
        try{
            
            patientList.getItems().clear();
            
            Collection<Patient> patients = ORM.findAll(Patient.class,
                "order by name");
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
    
    @FXML
    private void patientsByAdmitted(Event event)
    {
        try{
            patientList.getItems().clear();
            Collection<Patient> patients = ORM.findAll(Patient.class,
                "order by admitted");
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
    
    @FXML
    private void patientLink(Event event)
    {
        try {
            
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            
            if (patient == null || doctor == null) {
              throw new ExpectedException("must select doctor and patient");
            }
            
            Treatment treatment = ORM.findOne(Treatment.class, 
              "where patient_id=? and doctor_id=?", 
              new Object[]{patient.getId(), doctor.getId()}
            );
            
            if (treatment != null) {
              throw new ExpectedException("user already has this doctor");
            }
            
            Collection<Treatment> currentTreatments = ORM.findAll(Treatment.class,
                "where patient_id=?", new Object[]{patient.getId()});
                        
            for (Treatment treatments : currentTreatments) {
                Doctor otherDoctors = ORM.findOne(Doctor.class, 
                        "where id=?", new
                        Object[]{treatments.getDoctorId()});
                if (doctor.getSpecialty() == otherDoctors.getSpecialty()) {
                  throw new ExpectedException("patient already has a doctor with specialty " + doctor.getName());        
                }
            }
            
            treatment = new Treatment(patient, doctor, "");
            ORM.store(treatment);
            ORM.store(doctor);
            
            patientTreatmentIds.add(doctor.getId());
            doctorTreatmentIds.add(patient.getId());
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
    
    @FXML
    private void patientUnlink(Event event)
    {
        try {
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            if (patient == null || doctor == null) {
              throw new ExpectedException("must select doctor and patient");
            }

            // get the book from database
            Treatment treatment = ORM.findOne(Treatment.class, 
              "where patient_id=? and doctor_id=?", 
              new Object[]{patient.getId(), doctor.getId()}
            );
            
            if (treatment == null) {
              throw new ExpectedException("patient does not have this doctor");
            }
            
            // remove the borrow
            ORM.remove(treatment);
            ORM.store(doctor);

            // reset booklist
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
    
    @FXML
    private void removePatient(Event event) {
        try {
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            if (patient == null) {
              throw new ExpectedException("must select patient");
            }
            
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
                        
            // remove from user table
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

    @FXML
    private void addPatient(Event event) {
        try {
          //======================== The absolutely essential part
          // get fxmlLoader
          URL fxml = getClass().getResource("AddPatient.fxml");
          FXMLLoader fxmlLoader = new FXMLLoader(fxml);
          fxmlLoader.load();

          // get scene from loader
          Scene scene = new Scene(fxmlLoader.getRoot());

          // create a stage for the scene
          Stage dialogStage = new Stage();            
          dialogStage.setScene(scene);

          //dialogStage.setHeight(250);

          // specify dialog title
          dialogStage.setTitle("Add a Patient");

          // make it block the application
          dialogStage.initModality(Modality.APPLICATION_MODAL);

          // invoke the dialog
          dialogStage.show();
          //===============================================   

          // get controller from fxmlLoader
          AddPatientController dialogController = fxmlLoader.getController();

          // pass the LibraryController to the dialog controller
          dialogController.setMainController(this);
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }

    @FXML
    private void addDoctor(Event event) {
        try {
          //======================== The absolutely essential part
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

          // pass the LibraryController to the dialog controller
          dialogController.setMainController(this);
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }

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

          // pass the LibraryController to the dialog controller
          dialogController.setMainController(this);
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }
    
    @FXML
    private void modifyReport(Event event) {
        try {
            
            Patient patient = patientList.getSelectionModel().getSelectedItem();
            Doctor doctor = doctorList.getSelectionModel().getSelectedItem();
            
            if(patient == null || doctor == null) {
                throw new ExpectedException("must select a doctor and a patient");
            }
            
            Treatment treatment = ORM.findOne(Treatment.class, 
                "where patient_id=? and doctor_id=?", 
                new Object[]{patient.getId(), doctor.getId()});

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

            // pass the LibraryController to the dialog controller
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

            // set book to be modified in dialog
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
            for (Doctor doctor : doctors) {
                doctorList.getItems().add(doctor);
                
                Specialty speciality = ORM.findOne(Specialty.class,
                        "where id=?", new Object[]{doctor.getSpecialty_Id()});

                doctor.setSpecialty(speciality.getName());
            }
            
            Collection<Patient> patients = ORM.findAll(Patient.class,
                    "order by name");
            for (Patient patient : patients) {
              patientList.getItems().add(patient);
            }
                        
            DoctorCellCallBack doctorCellCallback = new DoctorCellCallBack();
            doctorList.setCellFactory(doctorCellCallback);

            PatientCellCallBack patientCellCallback = new PatientCellCallBack();
            patientList.setCellFactory(patientCellCallback);  
            
            doctorCellCallback.setHightlightedIds( patientTreatmentIds );
            patientCellCallback.setHightlightedIds( doctorTreatmentIds );
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }    
}
