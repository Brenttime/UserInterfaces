/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * FXML Controller class
 *
 * @author Brenttime
 */
public class ModifyReportController implements Initializable {

    
    private HospitalController mainController;
 
    void setMainController(HospitalController mainController) {
      this.mainController = mainController;
    }
    
    private Treatment treatmentToModify;
    
    void setTreatmentToModify(Treatment treatment) {
        this.treatmentToModify = treatment;

        // initialize fields from book to modify
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
        
        if(treatment.getReport() != null) {
           reportArea.setText(treatment.getReport());
        }
    }
    
    private Boolean reportModified = false;
    
    @FXML
    private Label lblDoctor;
        
    @FXML
    private Label lblPatient;
    
    @FXML
    private TextArea reportArea;
    
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
    
    @FXML
    private void reportChanged(Event event)
    {
        this.reportModified = true;
    }
    
    @FXML
    private void cancel(Event event) {
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


    }
}
