package app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import helperClass.HostServicesProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import main.Main;

public class StartQuizViewController implements Initializable {
	@FXML
	private AnchorPane _chooseModePane;
	
	@FXML
	private AnchorPane _needMoreCreationPane;
	
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
	 * Functionality for _returnToMainButton When clicked, goes back to the main
	 * menu
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goBackToMain() throws IOException {
		Main.loadMainMenu();
	}
	
	
	/**
	 * Functionality for _hardModeButton
	 * @throws IOException 
	 **/
	@FXML
	private void goToHardModeQuizView() throws IOException {
		String fxml = "/resources/fxmlFiles/QuizView.fxml";
		FXMLLoader loader = Main.loadFXML(fxml);
		QuizViewController quiz = loader.getController();
		quiz.setUp("hardMode");;
		
	}
	
	/**
	 * Functionality for _hardModeButton
	 * @throws IOException 
	 **/
	@FXML
	private void goToEasyModeQuizView() throws IOException {
		String fxml = "/resources/fxmlFiles/QuizView.fxml";
		FXMLLoader loader = Main.loadFXML(fxml);
		QuizViewController quiz = loader.getController();
		quiz.setUp("easyMode");
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// check if there are four creations already created 
		int numCreation = (new File("creations")).list().length;
		if (numCreation < 4) {
			// more creation is needed 
			_chooseModePane.setVisible(false);
			_needMoreCreationPane.setVisible(true);
		} else {
			// enough creation, choose the mode 
			_chooseModePane.setVisible(true);
			_needMoreCreationPane.setVisible(false);
		}
		
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
