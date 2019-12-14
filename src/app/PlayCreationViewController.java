package app;

import java.io.File;

import helperClass.HostServicesProvider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;
import main.Main;

/**
 * Code for functionality of _volumeSlider, _timeSlider, _playButton attributes to GreeksforGreeks 
 * https://www.geeksforgeeks.org/javafx-building-a-media-player/
 * @author zyan225
 *
 */
public class PlayCreationViewController {
	@FXML
	private BorderPane _borderPane;
	
	
	private MediaPlayer _player;
	
	@FXML
	private Slider _volumeSlider;
	
	@FXML
	private Slider _timeSlider;

	@FXML
	private Button _playButton;
	
	/**
	 * detect the previous scene before playCreationView 
	 * "finish" means it is previous scene is SaveCreationView 
	 */
	private String _previousView = "finish";
	
	private String _creationName;

	@FXML
	private Label _titleLabel;
	
	public void setUpPreviewView(String previousView) {
		_previousView = previousView;
	}
	
	/**
	 * functionality for _backButton
	 * When clicked, go back to the previous view 
	 */
	@FXML
	private void goBackToPreviousStage() {
		_player.stop();
		
		FXMLLoader loader = new FXMLLoader();
		
		String fxml = "";
		if (_previousView.equals("*list*")) {
			// go back to list of creation scene 
			fxml = "/resources/fxmlFiles/ListOfCreationView.fxml";

		} else {
			// go back to the play creation scene 
			fxml = "/resources/fxmlFiles/CreationFinishView.fxml";
		}
		
		loader = Main.loadFXML(fxml);

		
		if (_previousView.equals("*list*")) {
			// search all the creations and display them in the listview 
			ListOfCreationViewController creationView = loader.getController();
			creationView.viewCreations();
		} else {
			CreationFinishViewController creationFinishView = loader.getController();
			creationFinishView.setCreationName(_creationName);
		}
	    	
	}
	
	/**
	 * functionality for _playButton
	 * when clicked, if the video is playing, pause it
	 * if it is not playing, start playing
	 */
	@FXML
	private void playOrPause() {

		 // get the status of the player 
	     Status status = _player.getStatus(); 
         if (status == Status.PLAYING) { 
        	 
             // If the status is Video playing 
             if (_player.getCurrentTime().greaterThanOrEqualTo(_player.getTotalDuration())) { 

                 // If the player is at the end of video 
                 _player.seek(_player.getStartTime()); // Restart the video 
                 _player.play(); 
                 _playButton.setText("||");
             } 
             else { 
                 // Pausing the player 
                 _player.pause(); 

                 _playButton.setText("▶");
             }
         } // If the video is stopped, halted or paused 
         if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) { 
             _player.play(); // Start the video 
             _playButton.setText("||"); 
         } 
    } 
	
	
	@FXML
	private void JumpToCertainPartsOfVideo() {
		// It would set the time as specified by user by pressing 
		_player.seek(Duration.seconds(_timeSlider.getValue())); 
	}
	
	public void play(String creationName) {
		_creationName = creationName;
		
		File fileUrl = new File("./creations/"+_creationName+".mp4");

		_titleLabel.setText(_creationName+".mp4");
		
		// set the mediaview inside borderpane
		MediaView mediaView = new MediaView();
		_borderPane.setCenter(mediaView);
		mediaView.setFitHeight(650);
		
	 	// play the video 
		Media video = new Media(fileUrl.toURI().toString());
		
		_player = new MediaPlayer(video);
		
		_player.setOnReady(new Runnable() {

	        @Override
	        public void run() {
	    		_player.setAutoPlay(true);
	    		mediaView.setMediaPlayer(_player);
	
	    		// set the maximum value for _timeSlider 
	        	_timeSlider.setMax(Math.floor(_player.getTotalDuration().toSeconds()*10) / 10);
	      
	        }
	    });
		
		
		_volumeSlider.setValue(80);
		 _player.setVolume(_volumeSlider.getValue() / 100); 
		 
		// when volumeSlider changes value, the volume of the video changed 
		_volumeSlider.valueProperty().addListener(new InvalidationListener() { 
               public void invalidated(Observable ov) 
               { 
                   if (_volumeSlider.isPressed()) { 
                       _player.setVolume(_volumeSlider.getValue() / 100); 
                   } 
               } 
           }); 
		
		// allow user to jump between videos 
		_player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
	        if (!_timeSlider.isValueChanging()) {
	            _timeSlider.setValue(newTime.toSeconds());
	        }
	    });
		
		// when media finished, change the text displayed on _playButton to be a "replay" sign
		_player.setOnEndOfMedia(new Runnable() {
            public void run() {
            	_playButton.setText("↻");
            }
        });
            	
         
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
