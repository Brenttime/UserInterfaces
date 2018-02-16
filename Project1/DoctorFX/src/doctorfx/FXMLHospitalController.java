package doctorfx;

import models.Doctor;
import models.Patient;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import models.DBProps;
import models.Treatment;
import models.ORM;

/**
 *
 * @author brent
 */
public class FXMLHospitalController implements Initializable {
    
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
            System.out.println("doctorList selection: " + doctor);
            display.setText(Helper.info(doctor));
        }
        catch(Exception e){
                
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ORM.init(DBProps.getProps());

            Collection<Doctor> doctors = ORM.findAll(Doctor.class);
            for (Doctor doctor : doctors) {
              doctorList.getItems().add(doctor);
            }

            Collection<Patient> patients = ORM.findAll(Patient.class);
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
