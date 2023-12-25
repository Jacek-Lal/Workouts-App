package application.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

public class ExerciseDetailsController extends Controller<Object> {
	private static final String EXERCISE_SUMMARY_COMPONENT_PATH = COMPONENTS_PATH + "ExerciseSummary.fxml";
	private static final String EXERCISE_HISTORY_COMPONENT_PATH = COMPONENTS_PATH + "ExerciseFromHistory.fxml";
	private static final String SET_HISTORY_COMPONENT_PATH = COMPONENTS_PATH + "SetFromHistory.fxml";
	private String exerciseName;
	private boolean summaryView;
	private final List<HashMap<String, String>> workoutHistory;
	private List<HashMap<String, String>> currentExerciseRecords;
	private List<String> summaryData;
	
	@FXML
	private Label exerciseNameLabel;
	@FXML
	private VBox detailsContainer;

    public ExerciseDetailsController() {
		this.workoutHistory = loadWorkouts();
	}
	
	public void setup(String exerciseName) throws IOException {
		this.exerciseName = exerciseName;
		this.currentExerciseRecords = getCurrentExerciseRecords();
		this.summaryData = getSummaryData();
		
		exerciseNameLabel.setText(exerciseName);
					
		showSummary();
	}
	
	public void showSummary() throws IOException {
		if(summaryView) return;
		detailsContainer.getChildren().clear();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(EXERCISE_SUMMARY_COMPONENT_PATH));
		Parent root = loader.load();	
		
		ObservableList<Node> labels = ((Pane)root).getChildren();
		
		((Label)labels.get(2)).setText(this.summaryData.get(0));	// set heaviest weight
		((Label)labels.get(3)).setText(this.summaryData.get(1));	// set 1RM
		((Label)labels.get(6)).setText(this.summaryData.get(2));	// set best set volume
		((Label)labels.get(7)).setText(this.summaryData.get(3));	// set best session volume
				
		detailsContainer.getChildren().add(root);
		
		summaryView = true;
	}
	
	public void showHistory() throws IOException {
		if(!summaryView) return;
		
		summaryView = false;	
		
		detailsContainer.getChildren().clear();
		
		if(this.currentExerciseRecords.isEmpty()) {
			Label label = new Label("No exercise records");
	        label.setStyle("-fx-font-size: 20px; -fx-text-fill: #eee;");

			detailsContainer.getChildren().add(label);
			System.out.println(detailsContainer.getChildren());
			return;
		}
		
		
		FXMLLoader loader;
		Parent root;	
		String currDate = "";
		VBox setsContainer = null;

		for (HashMap<String, String> record : this.currentExerciseRecords) {
			
			if(!currDate.equals(record.get("Time"))) {
				currDate = record.get("Time");
				
				loader = new FXMLLoader(getClass().getResource(EXERCISE_HISTORY_COMPONENT_PATH));
				root = loader.load();
				
				ObservableList<Node> labels = ((Pane)root).getChildren();
				
				((Label)labels.get(0)).setText(record.get("Name"));	// set workout name
				((Label)labels.get(1)).setText(record.get("Time"));	// set workout date
				((Label)labels.get(2)).setText(record.get("Exercise"));	// set exercise name
				((Label)labels.get(3)).setText(record.get("Description"));	// set exercise description
				setsContainer = (VBox)((ScrollPane)labels.get(4)).getContent();
				
				detailsContainer.getChildren().addFirst(root);
			}
					
			if(currDate.equals(record.get("Time"))) {
				FXMLLoader setLoader = new FXMLLoader(getClass().getResource(SET_HISTORY_COMPONENT_PATH));
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
	
	private List<HashMap<String, String>> getCurrentExerciseRecords(){
		return this.workoutHistory.stream()
				.filter(r -> r.get("Exercise").equals(this.exerciseName))
				.collect(Collectors.toList());
	}

	private List<String> getSummaryData(){
		List<String> summaryData = new ArrayList<>();
		
		double heaviestWeight = 0;
		double bestRM = 0;
		double bestSetVol = 0;
		double bestSessVol = 0;
		
		double sessionVolume = 0;
		String currDate = "";
		
		for (HashMap<String, String> record : this.currentExerciseRecords) {
			
			if(currDate.isEmpty()) currDate = record.get("Time");
			
			double weight = Double.parseDouble(record.get("Weight"));
			int reps = Integer.parseInt(record.get("Reps"));
			double rm = Math.round(heaviestWeight * (1 + (0.0333 * reps))*100.0)/100.0;
			double volume = weight * reps;		
		
			if (weight > heaviestWeight) heaviestWeight = weight;
			if(rm > bestRM) bestRM = rm;
			if(volume > bestSetVol) bestSetVol = volume;
			
			if(currDate.equals(record.get("Time"))) sessionVolume += volume;
			else {
				
				if(sessionVolume > bestSessVol) bestSessVol = sessionVolume;
				
				sessionVolume = 0;
				sessionVolume += volume;
				currDate = record.get("Time");
			}		
		}
		
		if(sessionVolume > bestSessVol) bestSessVol = sessionVolume;
		
		summaryData.add(heaviestWeight +"kg");
		summaryData.add(bestRM +"kg");
		summaryData.add(bestSetVol +"kg");
		summaryData.add(bestSessVol +"kg");
		
		return summaryData;
	}
	
}
