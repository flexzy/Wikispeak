package main;


import java.io.File;
import java.io.IOException;

import helperClass.BashCommandProcess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	private static Stage _primaryStage;
	
    @Override
    public void start(Stage primaryStage) {
       _primaryStage = primaryStage;
       _primaryStage.setTitle("Varpedia");

       // initialise the singleton instance of HostServices for the application stage
		helperClass.HostServicesProvider.INSTANCE.init(getHostServices());

        // Load the fonts for the application
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Whale I Tried.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Comic Sans MS.ttf"), 14);
       
       // load the main.fxml as a layout 
   	   try {
   		   Main.loadMainMenu();
   	   } catch (IOException e1) {
   		   e1.printStackTrace();
   	   }
   	   
   	   // when stage closed, remove all the temporary files 
       primaryStage.setOnCloseRequest(e -> this.removeTempFiles());
      
    }
    
	public static void main(String[] args) {
        // Make directory to hold the creations
		String command = "mkdir ." + File.separator + "creations" + File.separator;
		BashCommandProcess.runBashCommand(command);

        // make directory to hold all creation needed for quiz 
		command = "mkdir ." + File.separator + ".quiz" + File.separator;
		BashCommandProcess.runBashCommand(command);

        launch(args);
    }
	
	 /**
     * called by all the return back to menu button in order to load the main view
     * @throws IOException 
     */
    public static void loadMainMenu() throws IOException {
    	Main main = new Main();
        main.removeTempFiles();
    	String fxml = "/resources/fxmlFiles/MainView.fxml";
    	Main.loadFXML(fxml);
    }
    
    /**
     * called by all the other classes when needs to switch scene 
     * @param fxml
     * @return
     */
    public static FXMLLoader loadFXML(String fxml) {
    	FXMLLoader loader = new FXMLLoader();
    	Main main = new Main();
    	loader.setLocation(main.getClass().getResource(fxml));
    	try {
    		Parent layout = loader.load();
    		Scene scene = new Scene(layout);
    	    _primaryStage.setScene(scene);
    	    _primaryStage.show();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
		return loader;
    }
    
    private void removeTempFiles() {
	    // remove all the temporary files 
	    try {
	        ProcessBuilder pb = new ProcessBuilder("bash", "-c", "rm -rf ." + File.separator + ".temp" + File.separator);
	        pb.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }

}
