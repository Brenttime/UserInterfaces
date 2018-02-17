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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Doctor;
import models.ORM;
import models.Specialty;

/**
 * FXML Controller class
 *
 * @author Brenttime
 */
public class AddSpecialtyController implements Initializable {

    private HospitalController mainController;
 
    void setMainController(HospitalController mainController) {
      this.mainController = mainController;
    }
    
    @FXML
    private TextArea existingSpecialtiesArea;
        
    @FXML
    private TextField newSpecialtyField;
    
    @FXML
    private void add(Event event) {
        try {
          
            String newSpecialtyItem = newSpecialtyField.getText().trim();


            if(newSpecialtyItem.length() == 0)
            {
                throw new ExpectedException("A specialty must be given");
            }
          
            Specialty specialtyWithName = ORM.findOne(Specialty.class,
                "where name=?", new Object[]{newSpecialtyItem});

            if(specialtyWithName != null)
            {
                throw new ExpectedException("A unique specialty must be given");
            }
          
            // put it into the database
            Specialty newSpecialty = new Specialty(newSpecialtyItem);
            ORM.store(newSpecialty);

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
                existingSpecialtiesArea.appendText(specialty.getName() + "\n");
            }
        }
        catch (Exception ex) {
          ex.printStackTrace(System.err);
          System.exit(1);
        }
    }    
    
}
