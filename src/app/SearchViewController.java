package app;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import helperClass.HostServicesProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import helperClass.SceneState;
import javafx.scene.layout.VBox;
import main.Main;

public class SearchViewController implements Initializable {
	@FXML
	private Button _returnToMainButton;

	/**
	 * Field used for user to type the term they want to search on Wikipedia
	 */
	@FXML
	private TextField _searchInput;

	/**
	 * Button used when user want to start searching
	 */
	@FXML
	private Button _searchButton;

	/**
	 * Label used to display warning message when user types invalid terms to search
	 */
	@FXML
	private Label _failureLabel;

	/**
	 * Group that contains what is shown when the application is loading
	 */
	@FXML
	private Group _searchingGIF;

    /**
     * the Vbox that holds the error message after searching for bad word
     */
	@FXML
    private VBox _badWordError;

	/**
	 * The term that user searches
	 */
	private String _termSeached;

	/**
	 * String that contains the search result from wikipedia
	 */
	private String _searchResult;
	
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
	 * Functionality for _searchButton When clicked, start searching on wikipedia
	 */
	@FXML
	private void startSearchWhenClicked() throws IOException {
		_termSeached = _searchInput.getText();
		if (isBadWord()) {
			_failureLabel.setVisible(false);
			_badWordError.setVisible(true);
			_searchInput.clear();
			return;
		} else {
            _badWordError.setVisible(false);
        }
		searchScene(SceneState.IN_PROGRESS);
		WikitWorker worker = new WikitWorker();
		worker.start();
	}

	/**
	 * This method is attributed to https://stackoverflow.com/a/5868528
	 * @return true if the search term is a word in the bad-words list
	 */
	private boolean isBadWord() throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader("./.resources/bad-words.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equalsIgnoreCase(_termSeached)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Functionality for _searchInput When detected that enter key is pressed, fire
	 * the _searchButton
	 * @Override
	 * @param ke
	 */
	@FXML
	private void startSearchWhenKeyPressed(KeyEvent ke) {
		if (ke.getCode().equals(KeyCode.ENTER)) {
			_searchButton.fire();
		}
	}

	/**
	 * when first called, the state passed in is NORMAL Make then display the scene
	 * for asking what to search for.
	 * 
	 * @param state determines what version of this scene is made and displayed.
	 */
	public void searchScene(SceneState state) {

		switch (state) {

		case NORMAL:
			// normal state, clear the word inside _searchInput, wait for user to search
			_searchInput.clear();
			_searchInput.requestFocus();

			// hide the failure label and searching label
			_failureLabel.setVisible(false);
			_searchingGIF.setVisible(false);

			// show and enable the search button
			_searchButton.setDisable(false);
			_searchButton.setVisible(true);
			_returnToMainButton.setDisable(false);
			break;

		case INVALID:
			// show and enable the searchbutton and textfield to allow user search for other
			// things
			_searchButton.setVisible(true);
			_searchButton.setDisable(false);

			// enable the textfield
			_searchInput.setDisable(false);
			_searchInput.clear();
			_searchInput.requestFocus();

			// hide the searching label
			_searchingGIF.setVisible(false);

			// show the failure message
			_failureLabel.setVisible(true);
			_failureLabel.setText("OOPS! \"" + _termSeached + "\" was something we could not find :(");
			_returnToMainButton.setDisable(false);
			break;

		case IN_PROGRESS:
			// disable the searchInput textfield to stop user search for other terms
			_searchInput.setDisable(true);
			_searchInput.setText(_termSeached);
			_returnToMainButton.setDisable(true);

			// hide the search button, display the searching message, hide failureLabel
			_failureLabel.setVisible(false);
			_searchButton.setVisible(false);
			_searchingGIF.setVisible(true);

			break;
		}
		
	}

	/**
	 * called first when switch from main scene to searchView scene Used to create a
	 * temporary directory to hold all the temporary files
	 */
	public void createTemporaryFile() {
		try {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c",
					"mkdir -p ." + File.separator + ".temp" + File.separator);
			pb.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inner class for the worker thread that performs the wikit bash command.
	 */
	private class WikitWorker extends Thread {

		@Override
		public void run() {
			try {
				ProcessBuilder pb = new ProcessBuilder("wikit", _termSeached);
				Process process = pb.start();

				BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
				_searchResult = stdout.readLine();

				FinishedWikitJob finished = new FinishedWikitJob();
				Platform.runLater(finished);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	   /**
	    * Inner class for the job that is to be done once the wikit command is completed.
	    */
	   private class FinishedWikitJob implements Runnable {

		   @Override
	       public void run() {
			   if(_searchResult.equals(_termSeached + " not found :^(")) {
	               searchScene(SceneState.INVALID);
	           } else {
	               // Remove leading whitespace
	               _searchResult = _searchResult.trim();
	 
	               // show the result on the next scene 
	               String fxml = "/resources/fxmlFiles/ChooseAudioView.fxml";
	               FXMLLoader loader = Main.loadFXML(fxml);
	               ChooseAudioViewController audioView = loader.getController();
		           audioView.showSearchResult(_termSeached, _searchResult);
	           }
	       	    	
	        }
	    }
	   
	   @Override
	   public void initialize(URL url, ResourceBundle rb) {
	       Platform.runLater(new Runnable() {
	           @Override
	           public void run() {
	               _searchInput.requestFocus();
	           }
	       });
	   }

}
