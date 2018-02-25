/*
 * Brent Turner
 * 
 * Userinterface
 * DoctorCellCallBack
 */
package doctorfx;

import java.util.Collection;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Doctor;

/**
 * Provides a more dynamic way of getting the objects text, as opposed to the "toString" method
 * @author brent turner
 */
public class DoctorCellCallBack implements Callback<ListView<Doctor>, ListCell<Doctor>> {
  
  private Collection<Integer> highlightedIds = null;
 
  void setHightlightedIds(Collection<Integer> highlightedIds) {
    this.highlightedIds = highlightedIds;
  }  
    
  @Override
  public ListCell<Doctor> call(ListView<Doctor> p) {
    ListCell<Doctor> cell = new ListCell<Doctor>() {
      @Override
      protected void updateItem(Doctor doctor, boolean empty) {
        super.updateItem(doctor, empty);
        if (empty) {
          this.setText(null);
          return;
        }
        this.setText( doctor.getName() );
 
            if (highlightedIds == null) {
                return;
            }
 
            String css = ""
                + "-fx-text-fill: #c00;"
                + "-fx-font-weight: bold;"
                + "-fx-font-style: italic;"
                ;

            if (highlightedIds.contains(doctor.getId())) {
              this.setStyle(css);
            }
            else {
              this.setStyle(null);
            }
        }
    };  
    return cell;
  }
}
