package app;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class NameExistsAlertViewController {
	
	  @FXML
	  private Label _statementLabel;
	  
	  private boolean _choseOverwrite;
	  
	/**
	 * functionality for overwrite button, when clicked, change value of _choseOverwirte to true
	 * close the stage 
	 * @param e the event of the overwrite button being pressed
	 */
	@FXML
	private void Overwrite(ActionEvent e) {
		_choseOverwrite = true;
		Node node = (Node) e.getSource();
		Stage window = (Stage) node.getScene().getWindow();
        window.close();
	}
	
	/**
	 * functionality for rename button, when clicked, change value of _choseOverwirte to false
	 * close the stage 
	 * @param e the event of the rename button being pressed
	 */
	@FXML
	private void Rename(ActionEvent e) {
		_choseOverwrite = false;
		Node node = (Node) e.getSource();
		Stage window = (Stage) node.getScene().getWindow();
        window.close();
	}
	
	/**
	 * display the existing creation name on the pop up box 
	 * @param existingName the name that already exists
	 */
	public void setText(String existingName) {
		_statementLabel.setText(existingName);
	}
	
	public boolean isOverwrite() {
		return _choseOverwrite;
	}

}