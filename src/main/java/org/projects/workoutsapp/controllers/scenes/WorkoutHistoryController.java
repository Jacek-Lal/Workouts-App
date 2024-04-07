package org.projects.workoutsapp.controllers.scenes;

import javafx.scene.Node;
import org.projects.workoutsapp.dto.WorkoutWrapUpInfo;
import org.projects.workoutsapp.entities.ExerciseRecord;
import org.projects.workoutsapp.entities.SetRecord;
import org.projects.workoutsapp.entities.WorkoutRecord;
import org.projects.workoutsapp.persistence.DatabaseClient;
import org.projects.workoutsapp.utility.LabelManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutHistoryController{
	@FXML
	private VBox exercisesWrapUpContainer;
	@FXML
	private ScrollPane exercisesScrollPane;
	private GridPane exercisesContainer;

	@FXML
	public void initialize() throws IOException {
		this.exercisesContainer = addGrid();
		loadExercisesWrapUp();
	}
	private void loadExercisesWrapUp() throws IOException {
		List<WorkoutWrapUpInfo> wrapUpData = DatabaseClient.getWrapUpData();
		
		Parent root;
		
		for(WorkoutWrapUpInfo wrapUp : wrapUpData){
			root = new FXMLLoader(getClass().getResource("/fxml/components/WorkoutWrapUp.fxml")).load();
			WorkoutRecord workout = wrapUp.getWorkout();
			List<String> data = List.of(workout.getName(),
								String.valueOf(workout.getStartTime()),
								workout.getDuration(),
								String.valueOf(wrapUp.getTotalWeight()),
								String.valueOf(wrapUp.getTotalSets()));
			
			root.setUserData(workout.getId());
			addWrapUp(root, data);
		}
	}
	private void addWrapUp(Parent root, List<String> data) {
		List<Label> labels = new ArrayList<>();
		LabelManager.getLabelsWithId(root, labels);
		LabelManager.addData(labels, data);

		root.setOnMouseClicked(e -> {
            //List<HashMap<String, String>> workoutRecords = getWorkoutRecords(data.get(1));
			//System.out.println(e.getTarget());
            try {
                openWorkout((Integer)((Node)e.getTarget()).getUserData());
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

		exercisesWrapUpContainer.getChildren().addFirst(root);
	}
	private GridPane addGrid(){
		ColumnConstraints columnConstraints1 = new ColumnConstraints();
		ColumnConstraints columnConstraints2 = new ColumnConstraints();

		columnConstraints1.setHgrow(Priority.NEVER);
		columnConstraints2.setHgrow(Priority.NEVER);

		columnConstraints1.setPercentWidth(50.00);
		columnConstraints1.setPercentWidth(50.00);

		GridPane gridPane = new GridPane();
		gridPane.getColumnConstraints().addAll(columnConstraints1, columnConstraints2);
		gridPane.getStyleClass().add("mainColorLight");
		gridPane.setVgap(4);
		gridPane.setHgap(4);

		exercisesScrollPane.setContent(gridPane);

		return gridPane;
	}
	private void openWorkout(Integer workoutId) throws IOException {
		exercisesContainer.getChildren().clear();
		
		List<ExerciseRecord> workoutData = DatabaseClient.getWorkoutDataById(workoutId);
		System.out.println(workoutId);
		int counter = 0;
		
		for(ExerciseRecord exercise : workoutData){
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ExerciseFromHistory.fxml"));
			Parent root = loader.load();
			
			List<Label> labels = new ArrayList<>();
			LabelManager.getLabelsWithId(root, labels);
			
			String exType = "Weight & reps";
			if(exercise.getSets().getFirst().getWeight() == 0.0)
				exType = "Reps";
			
			LabelManager.addData(labels, List.of(exercise.getName(), exercise.getDescription(), exType));
			
			VBox setsContainer = new VBox();
			LabelManager.getElementById(root, setsContainer, "setsContainer");
			
			exercisesContainer.add(root, counter % 2, counter / 2);
			
			for(SetRecord set : exercise.getSets()){
				FXMLLoader setLoader = new FXMLLoader(getClass().getResource("/fxml/components/SetFromHistory.fxml"));
				Parent setRoot = setLoader.load();
				
				labels = new ArrayList<>();
				LabelManager.getLabelsWithId(setRoot, labels);
				
				String weight = String.valueOf(set.getWeight());
				List<String> data = List.of(String.valueOf(set.getNumber()), (weight.equals("0.0") ? "" : weight+"kg x ") + set.getReps()+" reps");
				
				LabelManager.addData(labels, data);
				
				setsContainer.getChildren().add(setRoot);
			}
			counter += 1;
		}
	}
	

}
