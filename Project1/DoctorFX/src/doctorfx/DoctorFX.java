/*
 * Brent Turner
 * 
 * Userinterface
 * DoctorFX
 */
package doctorfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.DBProps;

/**
 * This class just initializes the main GUI
 * 
 * @author brent turner
 */
public class DoctorFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        //the main gui window is loaded
        Parent root = FXMLLoader.load(getClass().getResource("Hospital.fxml"));
        
        Scene scene = new Scene(root);
        
        //defualt resolution of the application
        stage.setWidth(700);
        stage.setHeight(500);
        
        //set title
        stage.setTitle("Doctors & Patients - " + DBProps.which); 
    
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
