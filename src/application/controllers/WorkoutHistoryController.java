package application.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutHistoryController extends Controller<Object> {
	private static final String WRAPUP_COMPONENT_PATH = COMPONENTS_PATH + "WorkoutWrapUp.fxml";
	private static final String EXERCISE_COMPONENT_PATH = COMPONENTS_PATH + "ExerciseFromHistory.fxml";
	private static final String SET_COMPONENT_PATH = COMPONENTS_PATH + "SetFromHistory.fxml";

	private final List<HashMap<String, String>> workoutHistory;
	
	@FXML
	private VBox exercisesWrapUpContainer;
	@FXML
	private VBox exercisesContainer;
	
	public WorkoutHistoryController() {
		this.workoutHistory = loadWorkouts();
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
		String workoutDate = this.workoutHistory.getFirst().get("Time");
		double workoutVolume = 0;
		int workoutSets = 0;
		
		int workoutsAdded = 0;
		
		for (HashMap<String, String> record : this.workoutHistory) {
			String currDate = record.get("Time");
			
			if(!currDate.equals(workoutDate)) {
				
				if(workoutsAdded % 2 == 0) 
					root.setStyle("-fx-background-color: #333");
				
				workoutsAdded += addWrapUp(root, workoutName, workoutDate, workoutVolume, workoutSets);
				
				
				loader = new FXMLLoader(getClass().getResource(WRAPUP_COMPONENT_PATH));
				root = loader.load();
				
				workoutName = record.get("Name");
				workoutDate = currDate;
				workoutVolume = 0;
				workoutSets = 0;
			}
			
			double setVolume = Double.parseDouble(record.get("Weight")) * Double.parseDouble(record.get("Reps"));
			
			workoutVolume += setVolume;
			workoutSets += 1;

		}
		addWrapUp(root, workoutName, workoutDate, workoutVolume, workoutSets);
		
	}
	
	private int addWrapUp(Parent root, String name, String date, double volume, int sets) {
			
		ObservableList<Node> labels = ((Pane)root).getChildren();
		
		((Label)labels.get(0)).setText(name);	// set workout name
		((Label)labels.get(1)).setText(date);	// set workout date
		((Label)labels.get(2)).setText("None");	// set workout duration
		((Label)labels.get(3)).setText(volume +"kg");	// set workout volume
		((Label)labels.get(4)).setText(sets +" sets");	// set workout sets number
		
		root.setOnMouseClicked(e -> {
            List<HashMap<String, String>> workoutRecords = getWorkoutRecords(date);
            try {
                openWorkout(workoutRecords);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
		
		exercisesWrapUpContainer.getChildren().addFirst(root);
		
		return 1;
	}
	
	private void openWorkout(List<HashMap<String, String>> workoutRecords) throws IOException {
		exercisesContainer.getChildren().clear();
		
		FXMLLoader loader;
		Parent root;	
		String currExercise = "";
		VBox setsContainer = null;

		for (HashMap<String, String> record : workoutRecords) {
			
			if(!currExercise.equals(record.get("Exercise"))) {
				currExercise = record.get("Exercise");
				
				loader = new FXMLLoader(getClass().getResource(EXERCISE_COMPONENT_PATH));
				root = loader.load();
				
				ObservableList<Node> labels = ((Pane)root).getChildren();
				
				((Label)labels.get(0)).setText(record.get("Name"));	// set workout name
				((Label)labels.get(1)).setText(record.get("Time"));	// set workout date
				((Label)labels.get(2)).setText(record.get("Description"));	// set exercise description
				((Label)labels.get(3)).setText(record.get("Exercise"));	// set exercise name
				setsContainer = (VBox)((ScrollPane)labels.get(4)).getContent();
				
				exercisesContainer.getChildren().add(root);
			}
					
			if(currExercise.equals(record.get("Exercise"))) {
				FXMLLoader setLoader = new FXMLLoader(getClass().getResource(SET_COMPONENT_PATH));
				Parent set = setLoader.load();
				
				ObservableList<Node> labels = ((HBox)set).getChildren();
				
				String id = record.get("SetNumber");
				String weight = record.get("Weight");
				String reps = record.get("Reps");
				
				((Label)labels.get(0)).setText(id);
				((Label)labels.get(1)).setText(weight+"kg x "+reps+" reps");

                assert setsContainer != null;
                setsContainer.getChildren().add(set);
			}	
		}
	}
	
	private List<HashMap<String, String>> getWorkoutRecords(String date){
		return this.workoutHistory.stream()
				.filter(r -> r.get("Time").equals(date))
				.collect(Collectors.toList());
	}
}
