package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws IOException {
		
		
       //This access the fxml file that we created from scene builder
		Parent root = FXMLLoader.load(getClass().getResource("Scener.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show(); //Shows the output
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				
				Platform.exit();
				System.exit(0);	
			}		
		});
	}	
	

	public static void main(String[] args) {
		launch(args);
	}
}