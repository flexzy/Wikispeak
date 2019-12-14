package app;

import java.io.File;
import helperClass.BashCommandProcess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmDeleteAlertViewController {
    
    @FXML
    private Label _creationLabel;
    
    private String _creationName;

    /**
     * Make and display the pop-up window that asks for confirmation to delete a specified creation.
     * @param creationName the name of the creation to delete.
     * @return true if the user confirms to delete the creation, otherwise false.
     */
    public void confirmDelete(String creationName) {
        _creationLabel.setText(creationName + ".mp4");
        _creationName = creationName;
    }
    
    /**
     * functionality for noButton, when pressed, close the window 
     */
    @FXML
    private void noButtonPressed(ActionEvent e) {
    	 Node node = (Node) e.getSource();
    	 Stage window = (Stage) node.getScene().getWindow();
         window.close();
    }
    
    /**
     * functionality for _yesButton, when pressed, delete the file and close the window 
     */
    @FXML
    private void yesButtonPressed(ActionEvent e) {
    	
    	String deleteCommand = "rm -f \"." + File.separator + "creations" + File.separator + _creationName + ".mp4\"";
    	BashCommandProcess.runBashCommand(deleteCommand);;

        
        // delete the corresponding files for quiz 
        String path = "\"./.quiz/" + _creationName + "\"";
        
        String command = "rm -r " + path;
        BashCommandProcess.runBashCommand(command);
       
        // get the stage and close it 
        Node node = (Node) e.getSource();
        Stage window = (Stage) node.getScene().getWindow();
        window.close();
    }
}
