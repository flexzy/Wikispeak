package app;

import java.io.IOException;

import helperClass.HostServicesProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import main.Main;

public class CreationFinishViewController {
	
	private String _creationName;
	
	/**
	 * Functionality for _returnToMainButton When clicked, goes back to the main
	 * menu
	 * @throws IOException
	 */
	@FXML
	private void goBackToMain() throws IOException {
		Main.loadMainMenu();
	}
	
	public void setCreationName(String creationName) {
		_creationName = creationName;
	}
	
	/**
	 * Functionality for _playButton 
	 * When clicked, go to the playCreationView
	 */
	@FXML
	private void playCreation() {
		String fxml = "/resources/fxmlFiles/PlayCreationView.fxml";
		FXMLLoader loader = Main.loadFXML(fxml);
		PlayCreationViewController play = loader.getController();
  	    play.play(_creationName); 	
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
