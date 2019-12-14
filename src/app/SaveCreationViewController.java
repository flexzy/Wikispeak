package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import helperClass.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import main.Main;

public class SaveCreationViewController implements Initializable {
	@FXML
	private Button _returnToMainButton;
	
	@FXML
	private Button _createButton;
	
	@FXML
	private TextField _inputField;
	
	/**
	 * Vbox that contains the warning labels that are displayed 
	 * when name of the creation entered is invalid 
	 */
	@FXML
	private VBox _warningBox;

	@FXML
	private Group _creatingGIF;
	
	private String _creationName;
	
	private String _creationFile;
	
    private ObservableList<AudioChunk> _audioChunksCollection;
	
    private String _termSearched;
    
    private int _numPictures;

    private String _music;
    
    private final String QUIZ_INFOR_PATH = ".quiz/";
    
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
	 * functionality for _createButton
	 * when clicked, check if the input name is valid
	 */
	@FXML
	private void createCreation() {
		 _creationName = _inputField.getText();
         if (_creationName.matches("[a-zA-Z0-9_ -]+")) {
             checkNameIsFree();
         } else {
             nameCreation(SceneState.INVALID);
         }
	}
	
	/**
	 * Functionality for _inputField When detected that enter key is pressed, fire
	 * the _createButton
	 * 
	 * @param ke
	 */
	@FXML
	private void startCreateWhenKeyPressed(KeyEvent ke) {
		if (ke.getCode().equals(KeyCode.ENTER)) {
			_createButton.fire();
		}
	}
	
	/**
	 * pass all the user information into this class 
	 * @param audioChunksCollection all texts that user selected 
	 * @param picture number of pictures included in the creation 
	 * @param termSearched term searched in wikipedia 
	 */
	public void setUp(ObservableList<AudioChunk> audioChunksCollection, int picture, String termSearched, String music) {
		_audioChunksCollection = audioChunksCollection;
		_numPictures = picture;
		_termSearched = termSearched;
		_music = music;
		
	}
	
	/**
     * Make and display the scene asking for the name of the new creation.
     * @param state determines what version of this scene is made and displayed.
     */
    private void nameCreation(SceneState state) {

        switch (state) {

            case NORMAL:
                // hide the Vbox that displays the warning message 
            	_warningBox.setVisible(false);
            	
            	// hide the creating label
            	_creatingGIF.setVisible(false);
            	_inputField.clear();
            	_inputField.requestFocus();
                break;
                
            case INVALID:
            	// display the warning message
            	_warningBox.setVisible(true);
            	_creatingGIF.setVisible(false);
            	
            	// clear user input in text field
            	_inputField.clear();
            	_inputField.requestFocus();
                break;
                
            case IN_PROGRESS:     
            	
            	// start saving the creation, disable all the buttons
                _inputField.setDisable(true);
                _inputField.setText(_creationName);
                _creatingGIF.setVisible(true);
                _warningBox.setVisible(false);
                _createButton.setVisible(false);
                _inputField.requestFocus();
                _returnToMainButton.setDisable(true);
                break;
        }
        
    }

    /**
     * Check if the entered creation name is available.
     */
    private void checkNameIsFree() {
        try {
            _creationFile = "." + File.separator + "creations" + File.separator + _creationName + ".mp4";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "[ -e \"" + _creationFile + "\" ]");
            Process process = pb.start();
            if (process.waitFor() == 0) {
            	LoadAlertBox alertLoad = new LoadAlertBox();
            	String fxml = "/resources/fxmlFiles/NameExistsAlertView.fxml";
            	FXMLLoader loader = alertLoad.createNewStage(fxml);
            	NameExistsAlertViewController nameExist = loader.getController();
            	// add the creationName into the alertBox 
            	nameExist.setText(_creationName);
            	
            	// load the new stage 
            	alertLoad.loadAlertBox();
            	
            	// get which button user pressed 
            	boolean overwrite = nameExist.isOverwrite();
            
                if (!overwrite) {
                    nameCreation(SceneState.NORMAL);
                    return;
                }
            }
            createCreationFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create a creation by combining the audio and video files 
     */
    private void createCreationFiles() {
        nameCreation(SceneState.IN_PROGRESS);
        Thread worker = new Thread(() -> {
            try {
            	
                CreateAudio.mergeAudioFiles(_audioChunksCollection);

                CreateAudio.addMusic(_music);
                
                CreateVideo video = new CreateVideo(_numPictures, _termSearched);
                video.createVideo();
                
                
                // merge the flide show with the audio to create the final creation 
            	ProcessBuilder pb = new ProcessBuilder();
                pb.command("bash", "-c", "ffmpeg -y -i ./.temp/output2.mp4 -i ./.temp/audio.wav -c:v copy -c:a aac -strict experimental \"" + _creationFile + "\"");
                Process process = pb.start();
                process.waitFor();
                
                this.makeQuizResource();

                Runnable finished = this::success;
                Platform.runLater(finished);
            } catch (Exception e) {
                e.printStackTrace();
            }
      
            
        });
        worker.start();
    }
    
    /**
     * template method
     * 1. put the slide show into the correct folder
     * 2. merge the slide show without term with the audio, put the resulting video into correct folder
     * 3. write the term searched in the text file 
     */
    private void makeQuizResource() {
    	this.makeQuizResourceForHardMode();
    	this.makeQuizResourceForEasyMode();
    	this.writeTermInTextFile();
    }

    private void writeTermInTextFile() {
    	// create a text file in the .quiz/_creationName folder 
		PrintWriter writer;
		try {
			writer = new PrintWriter(QUIZ_INFOR_PATH + _creationName + "/term.txt", "UTF-8");
			writer.println(_termSearched);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	private void makeQuizResourceForEasyMode() {
	     
        // create a folder to hold the video without termsearched for this creation
		File dirHardQuiz = new File(QUIZ_INFOR_PATH + _creationName + "/easyMode");
		dirHardQuiz.mkdir();
		
		String path = QUIZ_INFOR_PATH + _creationName + "/easyMode/" + _creationName + ".mp4";
		// merge the slideshow without termsearched with audio 
		String command = "ffmpeg -y -i ./.temp/output.mp4 -i ./.temp/audio.wav -c:v copy -c:a aac -strict experimental \"" + path + "\"";
		BashCommandProcess.runBashCommand(command);
		
		
	}

	private void makeQuizResourceForHardMode() {
   	    //create a folder in .quiz to hold the quiz information for this creation
        File creationQuiz = new File(QUIZ_INFOR_PATH + _creationName);
        creationQuiz.mkdir();
        
        // create a folder to hold the slideShow for this creation
		File dirHardQuiz = new File(QUIZ_INFOR_PATH + _creationName + "/hardMode");
		dirHardQuiz.mkdir();
		 
        // copy the file created into .quiz/hardMode directory for hardmode quiz usage
		File source = new File("./.temp/output.mp4");
		String desPath = QUIZ_INFOR_PATH + _creationName + "/hardMode/" + _creationName + ".mp4";
		File des = new File(desPath);
		if (des.exists()) {
			// file exists, delete the old one first
			des.delete();
		}
		try {
			Files.copy(source.toPath(), des.toPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	/**
     * Make and display the scene informing the user that their creation has been successfully created.
     */
    private void success() {
         // change to the next scene
    	 String fxml = "/resources/fxmlFiles/CreationFinishView.fxml";
    	 FXMLLoader loader = Main.loadFXML(fxml);
    	 CreationFinishViewController finish = loader.getController();
	     finish.setCreationName(_creationName);	
    	
    }
   
    @Override
	public void initialize(URL url, ResourceBundle rb) {
    	Platform.runLater(new Runnable() {
    		@Override
	        public void run() {
    			// when the scene is first loaded, set the focus to be on input field
    			_inputField.requestFocus();
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
