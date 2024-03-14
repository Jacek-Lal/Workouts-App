package org.projects.workoutsapp.controllers.components;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.projects.workoutsapp.controllers.ExerciseController;
import org.projects.workoutsapp.controllers.scenes.NewWorkoutController;
import org.projects.workoutsapp.utility.LabelManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExerciseListController extends ExerciseController {
	private Stage stage;
	private NewWorkoutController parent;

	public ExerciseListController()  {
		this.gridCols = 4;
	}
	public void setup(Stage stage, NewWorkoutController parent) throws IOException {
		this.stage = stage;
		this.parent = parent;
	}
	@Override
	public void chooseExercise(MouseEvent e) throws IOException{
		Pane exercise = ((Pane)e.getSource());

		List<Label> labels = new ArrayList<>();
		LabelManager.getLabelsWithId(exercise, labels);

		List<String> exerciseData = LabelManager.getData(labels);

		parent.addExercise(exerciseData);
		stage.close();
	}
}
