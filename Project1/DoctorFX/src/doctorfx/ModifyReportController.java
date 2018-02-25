/*
 * Brent Turner
 * 
 * Userinterface
 * ModifyTreatmentController
 */
package doctorfx;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Doctor;
import models.ORM;
import models.Patient;
import models.Treatment;

/**
 * ModifyTreatmentController is a controller class for the modify treatment 
 * Panel.
 *
 * @author Brent Turner
 */
public class ModifyReportController implements Initializable {

    
    private HospitalController mainController;
 
    void setMainController(HospitalController mainController) {
      this.mainController = mainController;
    }
    
    private Treatment treatmentToModify;
    
    void setTreatmentToModify(Treatment treatment) {
        this.treatmentToModify = treatment;

        // initialize fields from doctor & patient to modify
        try{
            Doctor doctor = ORM.findOne(Doctor.class, "where id=?",
                    new Object[]{treatment.getDoctorId()});
            lblDoctor.setText(doctor.getName());

            Patient patient = ORM.findOne(Patient.class, "where id=?",
              new Object[]{treatment.getPatientId()});
            lblPatient.setText(patient.getName());
        }    
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
        
        //Fill the area with the current report
        if(treatment.getReport() != null) {
           reportArea.setText(treatment.getReport());
        }
    }
    
    //see reportChanged()
    private Boolean reportModified = false;
    
    @FXML
    private Label lblDoctor;
        
    @FXML
    private Label lblPatient;
    
    @FXML
    private TextArea reportArea;
    
    //Modify button handler
    @FXML
    private void modify(Event event)
    {
        try{
            String report = reportArea.getText();
            String doctorName = lblDoctor.getText();
            String patientName = lblPatient.getText();
            TextArea display = mainController.getDisplay();

            treatmentToModify.setReport(report);
            ORM.store(treatmentToModify);
            display.setText(Helper.info(treatmentToModify));   
            
            ((Button) event.getSource()).getScene().getWindow().hide();
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }
    
    /*
     * If the user attempts to modify the report. we will later handle if 
     * they attempt to cancel after modification
     */
    @FXML
    private void reportChanged(Event event)
    {
        this.reportModified = true;
    }
    
    //Cancel button handler
    @FXML
    private void cancel(Event event) {
        
        //Did the user attempt to modify the report at all?
        if(reportModified == true) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to exit this dialog?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
              event.consume();
            }
            else
            {
                ((Button)event.getSource()).getScene().getWindow().hide();
            }
        }
        else
        {
            ((Button)event.getSource()).getScene().getWindow().hide();
        }

    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //no initialize needed

    }
}
