package application.controllers;

import application.objects.WorkoutRecord;
import application.utility.CsvLoader;
import application.utility.LabelManager;
import application.utility.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutHistoryController {
	private static final String COMPONENTS_PATH = "/application/components/";
	private static final String WRAPUP_COMPONENT_PATH = COMPONENTS_PATH + "WorkoutWrapUp.fxml";
	private static final String EXERCISE_COMPONENT_PATH = COMPONENTS_PATH + "ExerciseFromHistory.fxml";
	private static final String SET_COMPONENT_PATH = COMPONENTS_PATH + "SetFromHistory.fxml";
	private static final String WORKOUT_HEADER_COMPONENT_PATH = COMPONENTS_PATH + "WorkoutHeader.fxml";

	private final List<HashMap<String, String>> workoutHistory;
	
	@FXML
	private VBox exercisesWrapUpContainer;
	@FXML
	private VBox exercisesContainer;
	
	public WorkoutHistoryController() {
		this.workoutHistory = CsvLoader.loadWorkouts();
	}
	@FXML
	public void initialize() throws IOException {
		if(!workoutHistory.isEmpty())
			loadExercisesWrapUp();
	}
	private void loadExercisesWrapUp() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(WRAPUP_COMPONENT_PATH));
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

				loader = new FXMLLoader(getClass().getResource(WRAPUP_COMPONENT_PATH));
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
			
		List<Label> labels = LabelManager.getLabelsWithId(root);
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
	private void openWorkout(List<HashMap<String, String>> workoutRecords) throws IOException {
		exercisesContainer.getChildren().clear();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(WORKOUT_HEADER_COMPONENT_PATH));
		Parent root = loader.load();
		List<Label> headerLabels = LabelManager.getLabelsWithId(root);
		LabelManager.addData(headerLabels, List.of(workoutRecords.getFirst().get("Name"),workoutRecords.getFirst().get("StartTime")));
		exercisesContainer.getChildren().add(root);

		String currExercise = "";
		VBox setsContainer = null;

		for (HashMap<String, String> record : workoutRecords) {
			
			if(!currExercise.equals(record.get("Exercise"))) {
				currExercise = record.get("Exercise");
				
				loader = new FXMLLoader(getClass().getResource(EXERCISE_COMPONENT_PATH));
				root = loader.load();

				VBox box = (VBox) ((Pane) root).getChildren().getLast();
				Label descLabel = (Label) box.getChildren().getFirst();
				setsContainer =(VBox) ((ScrollPane) box.getChildren().getLast()).getContent();

				List<Label> labels = LabelManager.getLabelsWithId(root);
				if(record.get("Description").isEmpty())
					box.getChildren().remove(descLabel);
				else
					labels.add(descLabel);

				LabelManager.addData(labels, List.of(record.get("Exercise"), record.get("Description")));

				exercisesContainer.getChildren().add(root);
			}
					
			if(currExercise.equals(record.get("Exercise"))) {
				FXMLLoader setLoader = new FXMLLoader(getClass().getResource(SET_COMPONENT_PATH));
				Parent set = setLoader.load();
				
				List<Label> labels = LabelManager.getLabelsWithId(set);
				LabelManager.addData(labels, List.of(record.get("SetNumber"), record.get("Weight")+"kg x "+record.get("Reps")+" reps"));

                assert setsContainer != null;
                setsContainer.getChildren().add(set);
			}	
		}
	}
	private List<HashMap<String, String>> getWorkoutRecords(String date){
		return this.workoutHistory.stream()
				.filter(r -> r.get("StartTime").equals(date))
				.collect(Collectors.toList());
	}
	public void goToMainView(ActionEvent e) throws IOException{
		new SceneLoader().loadMain(e);
	}
}
