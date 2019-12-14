package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import helperClass.HostServicesProvider;
import helperClass.MakeNodeDragable;
import helperClass.MakeNodeDragTarget;

import java.lang.Math;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import main.Main;

public class QuizViewController {
	@FXML
	private HBox _labelHBox;
	
    @FXML
    private GridPane _quizGridPane;
 
    /**
     * position of mediaView in _quizGridPane
     */
    private int[] _dropHBoxPosInPane = new int[] {4,5,6,7};

    @FXML
    private Label _choiceLabel0;

    @FXML
    private Label _choiceLabel1;

    @FXML
    private Label _choiceLabel2;

    @FXML
    private Label _choiceLabel3;

    private List<Label> _labelList = new ArrayList<Label>();

    @FXML
    private Button _checkButton;

    @FXML
	private Group _instructionsGroup;

    @FXML
	private Pane _congratulationsPane;
    
    private List<Integer> _creationNumberList; 
    
    private List<Integer> _labelPositionList;
    
    private List<String> _answerList = new ArrayList<String>();
    
    private String _mode = "hardMode";
    
    /**
     * position of different imageView in the _quizPane children list 
     */
    private final int posOfImageViewInPane = 0;
    private final int posOfSmileFaceInPane = 1;
    private final int posOfSadFaceInPane = 2;
    private final int posOfPlayImageInPane = 3;
    private final int posOfPauseImageInPane = 4;
    private final int posOfReplayImageInPane = 5;

    /**
     * set up the quiz view
     * @param mode hard level of the quiz, either harMode or easyMode;
     */
    public void setUp(String mode) {
    	this.chooseMode(mode);
    	this.setUpFunctionality();
    }
    
    private void chooseMode(String mode) {
    	_mode = mode; 
    }
	
	/**
	 * This is a template method that is called when quizView first loaded 
	 */
	private void setUpFunctionality() {
		// put all the labels and mediaView into a list
		this.initialiseList();
		
		// randomly choose the creations, displayed videos and labels in the view 
		this.prepareCreations();
		
		// make all the labels draggable 
		this.makeLabeldragable();
		
		// make all the HBox as the target for drop
		this.makeHBoxTheTarget();
	}
	
	private void prepareCreations() {
		// get a list of folders that contains creations for the quiz
		File file = new File(".quiz/");
		File[] fileList = file.listFiles();
				
		int total = fileList.length;
		
		// get four random numbers 
    	_creationNumberList = new ArrayList<Integer>();
    	_labelPositionList = new ArrayList<Integer>();
    	
    	// this four number decide which creation will be used in the quiz 
    	while(_creationNumberList.size() < 4){
    		int num=(int)(Math.random() * total);
    		if (!_creationNumberList.contains(num)) {
    			_creationNumberList.add(num);
    		}

    	}
    	
    	// this four number decide which label will contain the termSearched 
    	while(_labelPositionList.size() < 4){
    		int num=(int)(Math.random() * 4);
    		if (!_labelPositionList.contains(num)) {
    			_labelPositionList.add(num);
    		}
    	}
    	
    	this.displayVideoAndLabel(fileList);
		
	}
	
	private void initialiseList() {
		
		// add all choiselabel into a list to enable future easy use 
		_labelList.add(_choiceLabel0);
		_labelList.add(_choiceLabel1);
		_labelList.add(_choiceLabel2);
		_labelList.add(_choiceLabel3);
			  
	}
	
	private void makeLabeldragable() {
		
		MakeNodeDragable dragableFactory = new MakeNodeDragable();
		
		for (int i = 0; i < _labelList.size(); i++) {
			// add all the required listener to each _choiseLabel 
			dragableFactory.makeDraggable(_labelList.get(i));
		}
	     
	}
	
	private void makeHBoxTheTarget() {
		
		MakeNodeDragTarget targetFactory = new MakeNodeDragTarget();
		
		for (int i = 0; i < _dropHBoxPosInPane.length; i++) {
			// add all the required listener to each _dropHBox
			targetFactory.makeDragTarget(_quizGridPane.getChildren().get(_dropHBoxPosInPane[i]));
		}
		
		targetFactory.makeDragTarget(_labelHBox);
	}

	
	private void displayVideoAndLabel(File[] fileList) {
		
		// for each creation chosen by _creationNumberSet, find the slideShow and put it in the mediaPlayer
		for (int i = 0; i < _creationNumberList.size(); i++) {
			int posOfFile = _creationNumberList.get(i);
			String path = fileList[posOfFile].toURI().toString() + "/" + _mode + "/" + fileList[posOfFile].getName().replace(" ", "%20") + ".mp4";
			
			Media media = new Media(path);
			MediaPlayer player = new MediaPlayer(media);
			
			Pane pane = (Pane) _quizGridPane.getChildren().get(i);
			MediaView mediaView = (MediaView) pane.getChildren().get(0);
			mediaView.setMediaPlayer(player);
			
			
			// get the term searched for the selected creation 
			String termPath = ".quiz/" + fileList[posOfFile].getName() + "/term.txt";
			File file = new File(termPath); 
			
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				
				// read the txt file that contains the term searched for that creation  
				String term; 
				while ((term = br.readLine()) != null) {
					// get the position of the label that should contain this term searched 
					int labelPosition = _labelPositionList.get(i);
					Label label = _labelList.get(labelPosition);
					label.setText(term);
					
					// add this term into the answer list
					_answerList.add(term);
				} 
			} catch (IOException e) {
				e.printStackTrace();
			} 	
		}
	}
	
	/**
	 * Functionality for _checkButton, when clicked, check if the answer user gives is correct
	 */
	@FXML
	private void checkOrRetry() {
	
		for (int i = 0; i < 4; i++) {
			// stop all the videos 
			Pane pane = (Pane)_quizGridPane.getChildren().get(i);
			MediaView mediaView = (MediaView) pane.getChildren().get(0);
			MediaPlayer player = mediaView.getMediaPlayer();
			player.pause();
			
			// set all the images to be inviable 
			pane.getChildren().get(posOfPlayImageInPane).setVisible(false);		
			pane.getChildren().get(posOfPauseImageInPane).setVisible(false);		
			pane.getChildren().get(posOfReplayImageInPane).setVisible(false);	
		}
		
		if (_checkButton.getText().equals("Check")) {
	
			this.checkAnswer();
			
		} else {
			
			for (int i = 0; i < 4; i++) {
				Pane pane = (Pane) _quizGridPane.getChildren().get(i);
				pane.getChildren().get(posOfSadFaceInPane).setVisible(false);
				pane.getChildren().get(posOfSmileFaceInPane).setVisible(false);
				pane.getChildren().get(posOfReplayImageInPane).setVisible(false);
				
				// show the play image 
				pane.getChildren().get(posOfPlayImageInPane).setVisible(true);
			}
			
			// endable the _labelHBox and _quizGridPane
			_labelHBox.setDisable(false);
			_quizGridPane.setDisable(false);
			
			// change the text on button to be "Check"
			_checkButton.setText("Check");
		}
	}
	
	private void checkAnswer() {
		boolean allCorrect = true;
		for (int i = 0; i < _dropHBoxPosInPane.length; i++) {
			boolean correct = false;
			
			// check if there is a label in the dropBox 
			int pos = _dropHBoxPosInPane[i];
			HBox box = (HBox) _quizGridPane.getChildren().get(pos);
			Pane pane = (Pane) _quizGridPane.getChildren().get(i);
			
			if (box.getChildren().size() == 1) {
				Label label = (Label) box.getChildren().get(0);
				
				// check if the text on the label is correct
				if (_answerList.get(i).equalsIgnoreCase(label.getText())) {
					// show the happy face 
					pane.getChildren().get(posOfSmileFaceInPane).setVisible(true);
					pane.getChildren().get(posOfSadFaceInPane).setVisible(false);
					
					correct = true;
				} 
			} 
			if (!correct) {
				// show the sad face 
				pane.getChildren().get(posOfSmileFaceInPane).setVisible(false);
				pane.getChildren().get(posOfSadFaceInPane).setVisible(true);
				
				allCorrect = false;
			}
		}
		
		if (allCorrect) {
			// do something here 
			_checkButton.setDisable(true);
			_instructionsGroup.setVisible(false);
			_labelHBox.setVisible(false);
			_congratulationsPane.setVisible(true);
		
			
		} else {
		
			// change the text on button to "retry"
			_checkButton.setText("Try Again!");
			
			// disable the _quizGridPane and _labelHBox
			_quizGridPane.setDisable(true);
			_labelHBox.setDisable(true);
		}
	}
	
	/**
	 * functionality for all the media view
	 * @param e mouse clicked event 
	 */
	@FXML
	private void playOrPause(MouseEvent e) {
		// get the pane that is being clicked
		Pane pane = (Pane) e.getSource();
		this.addHoverProperty(pane);
		MediaView mediaView = (MediaView) pane.getChildren().get(posOfImageViewInPane);
		MediaPlayer player = mediaView.getMediaPlayer();
		
		if (_mode.equals("easyMode")) {
			// creation stops when finish
			player.setOnEndOfMedia(new Runnable() {
				public void run() {
					pane.getChildren().get(posOfReplayImageInPane).setVisible(true);
	            }
	        });
		} else {
			// automatically replay the video when reach the end 
			player.setOnEndOfMedia(new Runnable() {
				public void run() {
					player.seek(player.getStartTime()); // Restart the video 
					player.play(); 
            	}
			});
		}
		
		Status status = player.getStatus(); // To get the status of Player 
	
		// check the current state of mediaPlayer
		if (status == Status.PLAYING) { 
		
            // If the status is Video playing 
            if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) { 
            	
                // If the player is at the end of video 
                player.seek(player.getStartTime()); // Restart the video 
                player.play(); 
                
                // hide the replay image 
                pane.getChildren().get(posOfReplayImageInPane).setVisible(false);
                
            } 
            else { 
            	
                // Pausing the player 
                player.pause(); 
                
                // display the "play" image
                pane.getChildren().get(posOfPlayImageInPane).setVisible(true);
                pane.getChildren().get(posOfReplayImageInPane).setVisible(false);
               
            } 
            pane.getChildren().get(posOfPauseImageInPane).setVisible(false);
        } 
		
		// If the video is stopped, halted or paused 

		if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED || ( status == Status.READY)) { 
            player.play(); // Start the video 

            // hide the play and pause images 
            pane.getChildren().get(posOfPlayImageInPane).setVisible(false);
            pane.getChildren().get(posOfPauseImageInPane).setVisible(false);
            pane.getChildren().get(posOfReplayImageInPane).setVisible(false);
        } 
	}
	
	/**
	 * Add a listener to all the pane. 
	 * When mouse hover detected, if the video is playing, show a pause image 
	 * @param pane
	 */
	private void addHoverProperty(Pane pane) {
		pane.hoverProperty().addListener((obs, oldVal, newValue) -> {
            // get the pauseImage
			ImageView pauseImageView = (ImageView) pane.getChildren().get(posOfPauseImageInPane);
			MediaView mediaView = (MediaView) pane.getChildren().get(posOfImageViewInPane);
			MediaPlayer player = mediaView.getMediaPlayer();
			
			Status status = player.getStatus(); // To get the status of Player 
			
			// get the imageView that contains the replay image 
			ImageView replayImageView = (ImageView) pane.getChildren().get(posOfReplayImageInPane);
			
			if ((newValue) && (status == Status.PLAYING) && (!replayImageView.isVisible())) {
				// only shows the pause image if the video is playing and it is not in the replay stage
				pauseImageView.setVisible(true);
            } else {
                pauseImageView.setVisible(false);
            }
        });
	}
	
	/**
	 * functionality for _backButton
	 */
	@FXML
	private void goBackToStartQuizView() {
		String fxml = "/resources/fxmlFiles/StartQuizView.fxml";
		Main.loadFXML(fxml);
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
