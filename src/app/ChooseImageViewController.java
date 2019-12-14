package app;

import java.io.File;
import java.io.IOException;
import helperClass.AudioChunk;
import helperClass.CreateVideo;
import helperClass.HostServicesProvider;
import helperClass.SceneState;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import main.Main;

public class ChooseImageViewController {
	@FXML
	private Button _returnToMainButton;
	
	@FXML
	private GridPane _displayImagePane;

	@FXML
	private VBox _downloadingVBox;
	
	@FXML
	private Button _nextButton;
	
	@FXML
	private Label _messageLabel;
	
	private String _termSearched;
	
	private CreateVideo _createVideo;
	
	private ObservableList<AudioChunk> _audioChunksCollection;
	
	private int _numPicture = 0;

	private String _music;
	
	
	public void setUp(String termSearched, ObservableList<AudioChunk> audioChunksCollection, String music) {
		_termSearched = termSearched;
		_audioChunksCollection = audioChunksCollection;
		_music = music;
		this.downloadPicture();
	}
	
	/**
	 * functionality for all the checkbox 
	 * @param e
	 */
	@FXML
	private void checkBoxClicked(MouseEvent e) {
		CheckBox checkBox = (CheckBox)e.getSource();
		if (checkBox.isSelected()) {
			// checkBox is selected, store how many checkbox is being clicked
			_numPicture++;
			
			// enable the next button
			_nextButton.setDisable(false);
			
			if (_numPicture == 10) {
				// disable all the other checkbox as the maximum images avaliable is 10
				for (int i = 0; i < 12; i++) {
					
					CheckBox check = (CheckBox) _displayImagePane.getChildren().get(i);
					if (!check.isSelected()) {
						// it is not checked, disable it
						check.setDisable(true);
					}
				
				}
			}
			
		} else {
			// checkBox is not selected
			_numPicture--;
			
			// enable all the checkbox
			for (int i = 0; i < 12; i++) {
				
				CheckBox check = (CheckBox) _displayImagePane.getChildren().get(i);
				if (!check.isSelected()) {
					// it is not checked, enable it 
					check.setDisable(false);
				}
			
			}
			
			// if there is no picture selected, disable the next button
			if (_numPicture == 0) {
				_nextButton.setDisable(true);
			}
		}
	}
	
	/**
	 * When the scene is first load, call the download image to download 12 images from flickr
	 */
	private void downloadPicture() {
		_createVideo = new CreateVideo(12, _termSearched);
		this.displayDownloadImage(SceneState.IN_PROGRESS);
	}
	
	/**
	 * show all the images downloaded from fliker
	 * 
	 */
	private void displayDownloadImage(SceneState state) {

		switch(state) {
			
			case NORMAL:
			
				/**
				 * show the _displayImagePane to show all the images download
				 * hide the processing message 
				 */
				_downloadingVBox.setVisible(false);
				_displayImagePane.setVisible(true);
				_messageLabel.setVisible(true);
				_returnToMainButton.setDisable(false);
				_returnToMainButton.setVisible(true);
				_nextButton.setVisible(true);
				
				break;
				
			case IN_PROGRESS:
		
				
				_returnToMainButton.setDisable(true);
				
				/**
				 * show a screen where displaying a message saying it is downloading images 
				 */
				Thread worker = new Thread(() -> {
					_createVideo.downloadImage();
					Runnable finished = this::downloadFinished;
					Platform.runLater(finished);
				}); 
				worker.start();	
	            
	            break;
			
			case INVALID:
				break;
	            
		}
		
	}
	
	/**
	 * get all the images from ./temp/imageDownload file 
	 * display them in the each checkbox inside gride pane
	 * make downloadingVBox invisible and displayImagePane visible 
	 */
	private void downloadFinished() {
		
		for (int i = 0; i < 12; i++) {
			
			Image image = new Image("file:.temp/imageDownload/" + i + ".jpg");
			
			// get the checkbox inside _displayImagePane
			CheckBox checkBox = (CheckBox) _displayImagePane.getChildren().get(i);
			 
			// set the image into the imageView inside checkbox
			((ImageView)checkBox.getGraphic()).setImage(image);
		}
		
		// change the state of scene to NORMAL
		this.displayDownloadImage(SceneState.NORMAL);
	}
	
	/**
	 * Functionality for _returnToMainButton
	 * When clicked, goes back to the main menu 
	 * @throws IOException 
	 */
	 @FXML
	 private void goBackToMain() throws IOException {
		Main.loadMainMenu();
	 }
	 
	 /**
	  * functionality for _nextButton
	  * when clicked, delete the images that are not selected and go to the next scene 
	  */
	 @FXML
	 private void goToSaveCreationView() { 
		 
		 
		 for (int i = 0; i < 12; i++) {			
			 // get the checkbox inside _displayImagePane
			 CheckBox checkBox = (CheckBox) _displayImagePane.getChildren().get(i);
			 
			 // check the state of checkBox
			 if (!checkBox.isSelected()) {
				 // delete this picture in the folder 
				 File file = new File(".temp/imageDownload/" + i + ".jpg"); 
				 file.delete();
			 } 
		 }	
		 
		 /**
		   * Go to saveCreationView and start creating creations 
	       */
		 String fxml = "/resources/fxmlFiles/SaveCreationView.fxml";
		 FXMLLoader loader = Main.loadFXML(fxml);
		 SaveCreationViewController saveController = loader.getController();
		 saveController.setUp(_audioChunksCollection, _numPicture, _termSearched, _music);
	    
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
