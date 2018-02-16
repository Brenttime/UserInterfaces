/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doctorfx;

import java.util.Collection;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Doctor;
import models.Patient;

/**
 *
 * @author brent
 */
public class PatientCellCallBack implements Callback<ListView<Patient>, ListCell<Patient>> {
    private Collection<Integer> highlightedIds = null;

    void setHightlightedIds(Collection<Integer> highlightedIds) {
      this.highlightedIds = highlightedIds;
    }  

    @Override
    public ListCell<Patient> call(ListView<Patient> p) {
      ListCell<Patient> cell = new ListCell<Patient>() {

        @Override
        protected void updateItem(Patient patient, boolean empty) {
          super.updateItem(patient, empty);
          if (empty) {
            this.setText(null);
            return;
          }
          this.setText( patient.getName() );
          if (highlightedIds == null) {
            return;
          }

            String css = ""
                + "-fx-text-fill: #c00;"
                + "-fx-font-weight: bold;"
                + "-fx-font-style: italic;"
                ;

            if (highlightedIds.contains(patient.getId())) {
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
