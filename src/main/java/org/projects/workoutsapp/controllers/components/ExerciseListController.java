package org.projects.workoutsapp.controllers.components;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.projects.workoutsapp.controllers.ExerciseController;
import org.projects.workoutsapp.controllers.scenes.NewWorkoutController;
import org.projects.workoutsapp.utility.LabelManager;

import java.io.IOException;
import java.util.List;

public class ExerciseListController extends ExerciseController {
	private Stage stage;
	private NewWorkoutController parent;

	public ExerciseListController(){
		super();
		this.gridCols = 4;
	}
	public void setup(Stage stage, NewWorkoutController parent) throws IOException {
		this.stage = stage;
		this.parent = parent;
		setFilters();
		showExercises();
	}
	@Override
	public void chooseExercise(MouseEvent e) throws IOException{
		Pane exercise = ((Pane)e.getSource());
		List<String> exerciseData = LabelManager.getData(LabelManager.getLabelsWithId(exercise));

		parent.addExercise(exerciseData);
		stage.close();
	}
}
