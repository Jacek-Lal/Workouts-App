package org.projects.workoutsapp.controllers;

import org.projects.workoutsapp.utility.LabelManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ExerciseListController extends ExercisesController{
	private Stage stage;
	private NewWorkoutController parent;

	public ExerciseListController() {
		super();
	}
	public void setup(Stage stage, NewWorkoutController parent) throws IOException {
		this.stage = stage;
		this.parent = parent;
		showExercises();
		setFilters();
	}
	@Override
	public void chooseExercise(MouseEvent e) throws IOException{
		Pane exercise = ((Pane)e.getSource());
		List<String> exerciseData = LabelManager.getData(LabelManager.getLabelsWithId(exercise));

		parent.addExercise(exerciseData);
		stage.close();
	}
	@Override
	protected void showExercises() throws IOException {
		int counter = 0;
		List<HashMap<String, String>> filteredExercises = getFilteredExercises();

		for(HashMap<String,String> exercise : filteredExercises) {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ExerciseFromDB.fxml"));
			Pane root = loader.load();
			root.setId(Integer.toString(counter));

			root.setOnMouseClicked(e -> {
				try { chooseExercise(e); }
				catch (IOException err) { err.printStackTrace(); }
			});

			ObservableList<Node> labels = root.getChildren();
			Label exerciseNameLabel = ((Label) labels.get(0));
			HBox box = ((HBox) labels.get(3));
			Label musclePrimaryLabel = (Label) box.getChildren().getFirst();
			Label muscleSecondaryLabel = (Label) box.getChildren().getLast();
			Label typeLabel = ((Label) labels.get(4));

			exerciseNameLabel.setText(exercise.get("Name"));
			musclePrimaryLabel.setText(exercise.get("Primary Muscle"));
			muscleSecondaryLabel.setText(exercise.get("Secondary Muscle"));
			typeLabel.setText(exercise.get("Equipment"));

			exercisesGrid.add(root, counter % 4, counter / 4);
			counter += 1;
		}
	}
}
