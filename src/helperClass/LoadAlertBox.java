package helperClass;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * this class is used when a warning box needs to pop up 
 * @author zyan225
 *
 */
public class LoadAlertBox {
	private Stage  _window;
	
	private Parent _layout;

	private FXMLLoader _loader = new FXMLLoader();
	
	/**
	 * create a new stage to create a loader with the required scene 
	 * @param fxml
	 * @return _loader 
	 */
	public FXMLLoader createNewStage (String fxml) {
		 _window = new Stage();
    	 _window.initStyle(StageStyle.UNDECORATED);
         _window.initModality(Modality.APPLICATION_MODAL);
    
    	_loader.setLocation(this.getClass().getResource(fxml));
    	try {
			_layout = _loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _loader;
	}
	
	/**
	 * Load the warning box in another new stage 
	 */
	public void loadAlertBox() {
		Scene scene = new Scene(_layout);
		_window.setScene(scene);
		_window.showAndWait();
	}

}
