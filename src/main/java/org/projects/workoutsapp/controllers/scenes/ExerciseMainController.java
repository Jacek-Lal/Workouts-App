package org.projects.workoutsapp.controllers.scenes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.projects.workoutsapp.controllers.ExerciseController;
import org.projects.workoutsapp.controllers.components.ExerciseDetailsController;

import java.io.IOException;

public class ExerciseMainController extends ExerciseController {

	@FXML
	private Pane exerciseDetailsContainer;

	public ExerciseMainController() {
		super();
		this.gridCols = 2;
	}

	@Override
	public void chooseExercise(MouseEvent e) throws IOException{
		Pane exercise = ((Pane)e.getSource());
		String exerciseName = ((Label)exercise.getChildren().getFirst()).getText();

		viewDetails(exerciseName);
	}
	private void viewDetails(String exerciseName) throws IOException{
		exerciseDetailsContainer.getChildren().clear();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ExerciseDetails.fxml"));
		Parent root = loader.load();
		ExerciseDetailsController controller = loader.getController();
		controller.setup(exerciseName);

		exerciseDetailsContainer.getChildren().add(root);
	}
}

