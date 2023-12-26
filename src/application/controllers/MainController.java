package application.controllers;

import java.io.IOException;

import application.utility.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController {

	private final SceneLoader sceneLoader = new SceneLoader();
	@FXML
	private TextField workoutName;

	public void goToMainView(ActionEvent e) throws IOException{
		sceneLoader.loadMain(e);
	}
	public void goToNewWorkoutModalView(ActionEvent e) throws IOException {
		sceneLoader.loadNewWorkoutModal(e);
	}
	public void goToWorkoutHistoryView(ActionEvent e) throws IOException {
		sceneLoader.loadWorkoutHistory(e);
	}
	public void goToExercisesView(ActionEvent e) throws IOException{
		sceneLoader.loadExercises(e);
	}
	public void goToNewWorkoutView(ActionEvent e) throws IOException {
		NewWorkoutController controller = sceneLoader.loadNewWorkout(e);
		String name = this.workoutName.getText();

		if(name.isEmpty()) name = "Just another workout";
		controller.setWorkoutName(name);
	}
}
