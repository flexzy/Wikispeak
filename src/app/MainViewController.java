package app;

import java.io.IOException;

import helperClass.HostServicesProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import main.Main;

public class MainViewController {

    /**
     * Functionality for _viewCreationbutton 
     * When button clicked, change scene to view all existing creations scene 
     * @throws IOException
     */
    @FXML
    private void goToListCreationView() throws IOException {
    	String fxml = "/resources/fxmlFiles/ListOfCreationView.fxml";
    	FXMLLoader loader = Main.loadFXML(fxml);
    	ListOfCreationViewController listView = loader.getController();
    	listView.viewCreations();
    }
    
    /**
     * Functionality for _quizButton 
     * When button clicked, change scene to start the quiz  
     * @throws IOException
     */
    @FXML
    private void goToStartQuizView() throws IOException {
    	String fxml = "/resources/fxmlFiles/StartQuizView.fxml";
    	Main.loadFXML(fxml);
    }
    
    /**
     * Functionality for _createCreationbutton 
     * When button clicked, change scene to ask user to search for a word 
     * @throws IOException
     */
    @FXML
    private void goToSearchCreationView() throws IOException {
    	String fxml = "/resources/fxmlFiles/SearchView.fxml";
    	FXMLLoader loader = Main.loadFXML(fxml);
	    SearchViewController searchView = loader.getController();
	    searchView.createTemporaryFile();
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
