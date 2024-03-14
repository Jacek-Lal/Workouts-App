package org.projects.workoutsapp.controllers.scenes;

import org.projects.workoutsapp.objects.WorkoutRecord;
import org.projects.workoutsapp.utility.DBConnector;
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
    private List<HashMap<String, String>> workoutHistory;
	@FXML
	private VBox exercisesWrapUpContainer;
	@FXML
	private ScrollPane exercisesScrollPane;
	private GridPane exercisesContainer;

	@FXML
	public void initialize() throws IOException {
		this.workoutHistory = DBConnector.loadWorkouts();
		this.exercisesContainer = addGrid();

		if(!workoutHistory.isEmpty())
			loadExercisesWrapUp();
	}
	private void loadExercisesWrapUp() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/WorkoutWrapUp.fxml"));
		Parent root = loader.load();	
		
		String workoutName = this.workoutHistory.getFirst().get("Name");
		String workoutDate = this.workoutHistory.getFirst().get("StartTime");
		String duration = WorkoutRecord.getDuration(this.workoutHistory.getFirst().get("StartTime"),this.workoutHistory.getFirst().get("EndTime"));
		double workoutVolume = 0;
		int workoutSets = 0;

		for (HashMap<String, String> record : this.workoutHistory) {
			String currDate = record.get("StartTime");
			
			if(!currDate.equals(workoutDate)) {
				List<String> wrapUpData = List.of(workoutName, workoutDate, duration, workoutVolume + "kg", workoutSets + " sets");

				addWrapUp(root, wrapUpData);

				loader = new FXMLLoader(getClass().getResource("/fxml/components/WorkoutWrapUp.fxml"));
				root = loader.load();

				workoutName = record.get("Name");
				workoutDate = currDate;
				duration =  WorkoutRecord.getDuration(record.get("StartTime"), record.get("EndTime"));
				workoutVolume = 0;
				workoutSets = 0;
			}
			
			double setVolume = Double.parseDouble(record.get("Weight")) * Double.parseDouble(record.get("Reps"));
			
			workoutVolume += setVolume;
			workoutSets += 1;

		}
		List<String> wrapUpData = List.of(workoutName, workoutDate, duration, workoutVolume + "kg", workoutSets + " sets");
		addWrapUp(root, wrapUpData);
		
	}
	private void addWrapUp(Parent root, List<String> data) {
		List<Label> labels = new ArrayList<>();
				LabelManager.getLabelsWithId(root, labels);
		LabelManager.addData(labels, data);

		root.setOnMouseClicked(e -> {
            List<HashMap<String, String>> workoutRecords = getWorkoutRecords(data.get(1));
            try {
                openWorkout(workoutRecords);
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
	private void openWorkout(List<HashMap<String, String>> workoutRecords) throws IOException {
		exercisesContainer.getChildren().clear();

		String currExercise = "";
		VBox setsContainer = null;
		int counter = 0;

		for (HashMap<String, String> record : workoutRecords) {
			
			if(!currExercise.equals(record.get("Exercise"))) {
				currExercise = record.get("Exercise");
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ExerciseFromHistory.fxml"));
				Parent root = loader.load();

				VBox box = (VBox) ((Pane) root).getChildren().getLast();
				Label descLabel = (Label) box.getChildren().getFirst();
				setsContainer = (VBox) box.getChildren().getLast();

				if(record.get("Weight").equals("0.0")) {
					((Label)((HBox) setsContainer.getChildren().getFirst()).getChildren().getLast()).setText("Reps");
                }

				List<Label> labels = new ArrayList<>();
						LabelManager.getLabelsWithId(root, labels);
				if(record.get("Description").isEmpty())
					box.getChildren().remove(descLabel);
				else
					labels.add(descLabel);

				LabelManager.addData(labels, List.of(record.get("Exercise"), record.get("Description")));

				exercisesContainer.add(root, counter % 2, counter / 2);
				counter += 1;
			}
					
			if(currExercise.equals(record.get("Exercise"))) {
				FXMLLoader setLoader = new FXMLLoader(getClass().getResource("/fxml/components/SetFromHistory.fxml"));
				Parent set = setLoader.load();
				
				List<Label> labels = new ArrayList<>();
						LabelManager.getLabelsWithId(set, labels);

				String weight = record.get("Weight");
				List<String> data = List.of(record.get("SetNumber"), (weight.equals("0.0") ? "" : weight+"kg x ") + record.get("Reps")+" reps");

				LabelManager.addData(labels, data);

                //assert setsContainer != null;
                setsContainer.getChildren().add(set);
			}	
		}
	}
	private List<HashMap<String, String>> getWorkoutRecords(String date){
		return this.workoutHistory.stream()
				.filter(r -> r.get("StartTime").equals(date))
				.collect(Collectors.toList());
	}

}
