package org.projects.workoutsapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.projects.workoutsapp.controllers.MainController;
import org.projects.workoutsapp.entities.WorkoutRecord;
import org.projects.workoutsapp.persistence.DatabaseClient;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scenes/Main.fxml"));
			Parent root = loader.load();
			MainController controller = loader.getController();
			Scene scene = new Scene(root);

			controller.setup(stage);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
