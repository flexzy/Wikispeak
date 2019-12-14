package app;

import helperClass.HostServicesProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import helperClass.LoadAlertBox;
import main.Main;

public class ListOfCreationViewController {

    private ObservableList<String> _creationsList = FXCollections.observableArrayList();
    
    /**
     * Listview that displays all the creations 
     */
    @FXML
    private ListView<String> _creationsView = new ListView<>();
    
    /**
     * Tell the user how many creations are found 
     */
    @FXML
    private Label _numCreationsLabel; 

    @FXML
    private Button _playButton;
    
    @FXML
    private Button _deleteButton;

    @FXML
    private VBox _emptyVbox;

    /**
     * When clicked, goes back to the main menu 
     * @throws IOException 
     */
    @FXML
    private void goBackToMain() throws IOException {
    	Main.loadMainMenu();
    }
    
    /**
     * Play the creation selected from the listview
     */
    @FXML
    private void playCreation() {
    	String fxml = "/resources/fxmlFiles/PlayCreationView.fxml";
    	FXMLLoader loader = Main.loadFXML(fxml);
    	PlayCreationViewController play = loader.getController();
 	    play.play(_creationsView.getSelectionModel().getSelectedItem());
 	    play.setUpPreviewView("*list*");
    }
    
    /**
     * Delete the selected creation
     */
    @FXML
    private void deleteCreation() {
    	delete(_creationsView.getSelectionModel().getSelectedItem());
        _creationsView.requestFocus();
    }
    
    /**
     * Make and display the scene that lists the existing creations.
     */
    public void viewCreations() {

        getCreations();

        _creationsView.setItems(_creationsList);
        if (_creationsList.size() == 0) {
        	// when there is no creations, show a message indivates that 
            _creationsView.setVisible(false);
        	_numCreationsLabel.setVisible(false);
        	_deleteButton.setVisible(false);
        	_playButton.setVisible(false);
        	_emptyVbox.setVisible(true);
        } else if (_creationsList.size() == 1) {
            _creationsView.setVisible(true);
            _emptyVbox.setVisible(false);
        	_deleteButton.setDisable(false);
          	_playButton.setDisable(false);
            _numCreationsLabel.setText("You have 1 creation right now:");
            _creationsView.getSelectionModel().select(0);
        } else {
            _creationsView.setVisible(true);
            _emptyVbox.setVisible(false);
        	_deleteButton.setDisable(false);
          	_playButton.setDisable(false);
            _numCreationsLabel.setText("You have " + _creationsList.size() + " creations right now:");
            _creationsView.getSelectionModel().select(0);
        }
        _creationsView.requestFocus();
    }

    /**
     * Retrieve information on what creations currently exist.
     */
    private void getCreations() {
        try {
            _creationsList.clear();
            String command = "ls -1 ." + File.separator + "creations" + File.separator + " | sed -e 's/\\.mp4$//'";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            Process process = pb.start();
            
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = stdout.readLine()) != null) {
                _creationsList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the specified creation.
     * @param creationName the name of the creation to delete.
     */
    private void delete(String creationName) {
    	// set up a new stage to show the alert box 
    	LoadAlertBox alertLoad = new LoadAlertBox();
    	String fxml = "/resources/fxmlFiles/ConfirmDeleteAlertView.fxml";
    	FXMLLoader loader = alertLoad.createNewStage(fxml);

      
        ConfirmDeleteAlertViewController deleteView = loader.getController();
        	
        // pass the name of the creation chosen by the user to display the creation name in the alert box 
        deleteView.confirmDelete(creationName);

        alertLoad.loadAlertBox();
            
        // update the list of creations 
        viewCreations();
            
    }

    /**
     * Functionality for when the question mark button is pressed.
     * The user manual is opened up in external pdf viewer.
     */
    @FXML
    private void viewManual() {
        HostServicesProvider.INSTANCE.openUserManual();
    }
}

