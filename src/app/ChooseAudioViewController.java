package app;

import java.io.IOException;


import helperClass.AudioChunk;
import helperClass.CreateAudio;
import helperClass.HostServicesProvider;
import helperClass.LoadAlertBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import main.Main;


public class ChooseAudioViewController {
	
	@FXML
	private Label _termLabel;
	
	/**
	 * show all the result retrived from wikipedia 
	 */
	@FXML
	private TextArea _resultArea;
	
	/**
	 * display the chucks of text that user selected 
	 */
	@FXML
	private TableView<AudioChunk> _audioChunkTableView;
	
	@FXML
	private TableColumn<AudioChunk, String> _textColumn; 
	
	@FXML
	private TableColumn<AudioChunk, String> _accentColumn;
	
	@FXML
	private TableColumn<AudioChunk, Double> _speedColumn;
	
	@FXML
	private ToggleGroup _accentsGroup;
	
	@FXML
	private Slider _speedSlider;
	
	@FXML
	private Button _nextButton;

	@FXML
    private ToggleGroup _musicGroup;
	
	private String _termSearched;

    private ObservableList<AudioChunk> _audioChunksCollection = FXCollections.observableArrayList();
	
	/**
	 * Label that used as a place holder for the table when no audio chunk is selected 
	 */
	private Label _noAudioLabel;
	
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
     * functionality of _upButton;
     * When clicked, move the row selected in the table up
     */
    @FXML
    private void moveRowUp() {
    	int index = _audioChunkTableView.getSelectionModel().getSelectedIndex();
        if (index > 0 && _audioChunksCollection.size() > 0) {
        	AudioChunk bufferAudioChunk = _audioChunksCollection.get(index);
            _audioChunksCollection.set(index, _audioChunksCollection.get(index-1));
            _audioChunksCollection.set(index-1, bufferAudioChunk);
            _audioChunkTableView.getSelectionModel().select(index-1);
        }
        _audioChunkTableView.requestFocus();
        
    }
    
    /**
     * functionality of _downButton;
     * When clicked, move the row selected in the table down
     */
    @FXML
    private void moveRowDown() {
    	int index = _audioChunkTableView.getSelectionModel().getSelectedIndex();
        if (index < _audioChunksCollection.size() - 1 && index >= 0) {
            AudioChunk bufferAudioChunk = _audioChunksCollection.get(index);
            _audioChunksCollection.set(index, _audioChunksCollection.get(index+1));
            _audioChunksCollection.set(index+1, bufferAudioChunk);
            _audioChunkTableView.getSelectionModel().select(index+1);
        }
        _audioChunkTableView.requestFocus();
    }
    
    /**
     * functionality of _deleteButton;
     * When clicked, delete the selected row in the table 
     */
    @FXML
    private void deleteRow() {
    	int index = _audioChunkTableView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < _audioChunksCollection.size()) {
            CreateAudio.delete(_audioChunksCollection.get(index).get_fileNumber());
            _audioChunksCollection.remove(index);
            _noAudioLabel.setText("No audio created");
            _noAudioLabel.setTextFill(Color.BLACK);
        }
        _audioChunkTableView.requestFocus();
        
        // if there is no audio selected, disable the next button 
        if (_audioChunksCollection.isEmpty()) {
        	_nextButton.setDisable(true);
        }
    }
    
    
    /**
     * functionality of _previewButton;
     * When clicked, preview the selected text 
     */
    @FXML
    private void previewAudio() {
    	if (!_resultArea.getSelectedText().isEmpty() && !isTooManyWords(_resultArea.getSelectedText())) {
            RadioButton accentSelected = (RadioButton) _accentsGroup.getSelectedToggle();
            CreateAudio.preview(_resultArea.getSelectedText(), accentSelected.getText(), convertSpeedInput(_speedSlider.getValue()));
        } else if (isTooManyWords(_resultArea.getSelectedText())) {
            this.popUpTooManyWordAlert();
        }
    }
    
    
    /**
     * functionality of _addButton;
     * When clicked, add the selected text as a audio file 
     */
    @FXML
    private void addAudio() {
    	 String text = _resultArea.getSelectedText();
         if (!text.isEmpty() && !isTooManyWords(text)) {
             // calculate the largest number an audio file is currently named
             int max = 0;
             for (AudioChunk audioChunk : _audioChunksCollection) {
                 if (audioChunk.get_fileNumber() > max) {
                     max = audioChunk.get_fileNumber();
                 }
             }

             RadioButton accentSelected = (RadioButton) _accentsGroup.getSelectedToggle();
             AudioChunk audioChunk = new AudioChunk(text, accentSelected.getText(), convertSpeedInput(_speedSlider.getValue()), max+1);
             _audioChunksCollection.add(audioChunk);

             CreateAudio.textToAudioFile(audioChunk);
             
             // enable the next button
             _nextButton.setDisable(false);
         } else if (isTooManyWords(text)) {
        	 
        	this.popUpTooManyWordAlert();
         }
    }
    
    
    private void popUpTooManyWordAlert() {
    	LoadAlertBox alertLoad = new LoadAlertBox();
    	String fxml = "/resources/fxmlFiles/TooManyWordsAlertView.fxml";
    	alertLoad.createNewStage(fxml);
    	alertLoad.loadAlertBox();
    }
    
    /**
     * functionality of _nextButton;
     * When clicked, add the selected text as a audio file 
     */
    @FXML
    private void goToChooseImageScene() {
        // get the selected music to add
        RadioButton musicSelected = (RadioButton) _musicGroup.getSelectedToggle();
    	
    	/**
    	 * download 12 images and go to the chooseImageScene 
    	 */
        String fxml = "/resources/fxmlFiles/ChooseImageView.fxml";
        FXMLLoader loader = Main.loadFXML(fxml);
        ChooseImageViewController chooseImageView = loader.getController();
	    chooseImageView.setUp(_termSearched, _audioChunksCollection, musicSelected.getText());
       
    }
    
    /**
     * display the scene showing the sentences retrieved from Wikit.
     */
    public void showSearchResult(String subject, String searchResult) {
    	
    	_termSearched = subject;

        _termLabel.setText("Here is what we found on \"" + _termSearched + "\":");
        
        _resultArea.setWrapText(true);
        _resultArea.setText(searchResult);

        // set up the columns for the tableview
        _textColumn.setCellValueFactory(new PropertyValueFactory<>("_text"));
        _accentColumn.setCellValueFactory(new PropertyValueFactory<>("_accent"));
        _speedColumn.setCellValueFactory(new PropertyValueFactory<>("_speed"));

        // set up tableview for the audio chunks added
        _audioChunkTableView.setItems(_audioChunksCollection);
        _noAudioLabel = new Label();
        _noAudioLabel.setText("No audio created");
        _audioChunkTableView.setPlaceholder(_noAudioLabel);
        _textColumn.setCellFactory(tc -> {
            TableCell<AudioChunk, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.setWrappingWidth(_textColumn.getPrefWidth()-10);
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        
        
        // set up the slider for change the speed of audio 
        _speedSlider.setBlockIncrement(1);
        _speedSlider.setMajorTickUnit(1);
        _speedSlider.setMinorTickCount(0);
        _speedSlider.setShowTickLabels(true);
        _speedSlider.setSnapToTicks(true);
        _speedSlider.setShowTickMarks(true);
        _speedSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 0.5) return "0.5";
                if (n < 1.5) return "0.75";
                if (n < 2.5) return "1.0";
                if (n < 3.5) return "1.5";

                return "2.0";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "0.5":
                        return 0.5d;
                    case "0.75":
                        return 0.75d;
                    case "1.0":
                        return 1.0d;
                    case "1.5":
                        return 1.5d;

                    default:
                        return 2.0d;
                }
            }
        });
        
        _resultArea.requestFocus();
    }

    private static double convertSpeedInput(double speed) {
        if (speed == 0.0d) {
            return 0.5d;
        } else if (speed == 1.0d) {
            return 0.75d;
        } else if (speed == 2.0d) {
            return 1.0d;
        } else if (speed == 3.0d) {
            return 1.5d;
        } else {
            return 2.0d;
        }
    }
    
    /**
     * method to check whether the user has selected too many words
     * @param text the selected chunk of text
     * @return true if they have selected more than 40 words, else false
     */
    private static boolean isTooManyWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        } else {
            String[] words = text.split("\\s+");
            return words.length > 40;
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
