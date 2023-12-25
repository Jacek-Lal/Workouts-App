package application.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController extends Controller<Object> {
	
	@FXML
	private TextField workoutName;
	
	public void goToNewWorkoutModalView(ActionEvent e) throws IOException {
		this.sceneName = "NewWorkoutModal";
		loadScene(e);
	}
	
	public void goToWorkoutHistoryView(ActionEvent e) throws IOException {
		this.sceneName = "WorkoutHistoryTab";
		loadScene(e);
	}
	
	public void goToExercisesView(ActionEvent e) throws IOException{
		this.sceneName = "ExercisesTab";
		loadScene(e);
	}
	
	public void goToNewWorkoutView(ActionEvent e) throws IOException {	
		this.sceneName = "NewWorkoutTab";
		NewWorkoutController controller = (NewWorkoutController)loadScene(e);
		String name = this.workoutName.getText();
		if(name.isEmpty()) name = "Just another workout";
		controller.setWorkoutName(name);
	}
}
