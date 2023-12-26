package application.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ExercisesController extends ExerciseListController{
	private static final String EXERCISE_DETAILS_SCENE_PATH = "/application/scenes/ExerciseDetails.fxml";
	
	public ExercisesController() {
		super();
	}
	@FXML
	public void initialize() throws IOException {
		showExercises();
		setFilters();
	}
	@Override
	public void chooseExercise(MouseEvent e) throws IOException{
		Pane exercise = ((Pane)e.getSource());
		String exerciseName = ((Label)exercise.getChildren().getFirst()).getText();

		viewDetails(e, exerciseName);
	}
	public void viewDetails(MouseEvent e, String exerciseName) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource(EXERCISE_DETAILS_SCENE_PATH));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		ExerciseDetailsController controller = loader.getController();

		controller.setup(exerciseName);

		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(((Node) e.getSource()).getScene().getWindow());
		stage.setScene(scene);

		stage.show();
	}
}

