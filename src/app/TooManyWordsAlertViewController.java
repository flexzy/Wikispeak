package app;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class TooManyWordsAlertViewController {

    /**
     * close the stage when this button is pressed
     * @param e the event of the okay button being pressed
     */
    public void OkayButtonPressed(ActionEvent e) {
    	 Node source = (Node) e.getSource();
    	 Stage window = (Stage) source.getScene().getWindow();
    	 window.close();
    }
}
