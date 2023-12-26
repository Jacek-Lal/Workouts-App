package application.controllers;

import application.utility.CsvLoader;
import application.utility.LabelManager;
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

public class ExerciseDetailsController{
	private static final String EXERCISE_SUMMARY_COMPONENT_PATH = "/application/components/" + "ExerciseSummary.fxml";
	private static final String EXERCISE_HISTORY_COMPONENT_PATH = "/application/components/" + "ExerciseFromHistory.fxml";
	private static final String SET_HISTORY_COMPONENT_PATH = "/application/components/" + "SetFromHistory.fxml";
	private static final String WORKOUT_HEADER_COMPONENT_PATH = "/application/components/" + "WorkoutHeader.fxml";
	private String exerciseName;
	private boolean summaryView;
	private List<HashMap<String, String>> currentExerciseRecords;
	private List<String> summaryData;

	@FXML
	private Label exerciseNameLabel;
	@FXML
	private VBox detailsContainer;
	
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
		
		List<Label> labels = LabelManager.getLabelsWithId(root);
		LabelManager.addDataToLabels(labels, this.summaryData);

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
			return;
		}

		FXMLLoader exerciseLoader;
		Parent exercise;
		String currDate = "";
		VBox setsContainer = null;

		for (HashMap<String, String> record : this.currentExerciseRecords) {
			
			if(!currDate.equals(record.get("EndTime"))) {
				currDate = record.get("EndTime");


				FXMLLoader headerLoader = new FXMLLoader(getClass().getResource(WORKOUT_HEADER_COMPONENT_PATH));
				Parent header = headerLoader.load();
				List<Label> headerLabels = LabelManager.getLabelsWithId(header);
				LabelManager.addDataToLabels(headerLabels, List.of(record.get("Name"),record.get("StartTime")));

				exerciseLoader = new FXMLLoader(getClass().getResource(EXERCISE_HISTORY_COMPONENT_PATH));
				exercise = exerciseLoader.load();

				VBox box = (VBox) ((Pane) exercise).getChildren().getLast();
				Label descLabel = (Label) box.getChildren().getFirst();
				setsContainer = (VBox) ((ScrollPane) box.getChildren().getLast()).getContent();
				List<Label> exerciseLabels = LabelManager.getLabelsWithId(exercise);

				if(record.get("Description").isEmpty())
					box.getChildren().remove(descLabel);
				else
					exerciseLabels.add(descLabel);

				LabelManager.addDataToLabels(exerciseLabels, List.of(record.get("Exercise"),record.get("Description")));

				detailsContainer.getChildren().addFirst(exercise);
				detailsContainer.getChildren().addFirst(header);
			}
			if(currDate.equals(record.get("EndTime"))) {
				FXMLLoader setLoader = new FXMLLoader(getClass().getResource(SET_HISTORY_COMPONENT_PATH));
				Parent set = setLoader.load();
				
				List<Label> labels = LabelManager.getLabelsWithId(set);
				LabelManager.addDataToLabels(labels, List.of(record.get("SetNumber"), record.get("Weight")+"kg x " + record.get("Reps")+" reps"));

                assert setsContainer != null;
                setsContainer.getChildren().add(set);
			}
		}
		
	}
	private List<HashMap<String, String>> getCurrentExerciseRecords(){
		List<HashMap<String, String>> workoutHistory = CsvLoader.loadWorkouts();

		return workoutHistory.stream()
				.filter(record -> record.get("Exercise").equals(this.exerciseName))
				.collect(Collectors.toList());
	}
	private List<String> getSummaryData(){

        double heaviestWeight = 0;
		double bestRM = 0;
		double bestSetVol = 0;
		double bestSessVol = 0;
		double sessionVolume = 0;
		String currDate = "";
		
		for (HashMap<String, String> record : this.currentExerciseRecords) {
			
			if(currDate.isEmpty()) currDate = record.get("EndTime");
			
			double weight = Double.parseDouble(record.get("Weight"));
			int reps = Integer.parseInt(record.get("Reps"));
			double rm = Math.round(heaviestWeight * (1 + (0.0333 * reps))*100.0)/100.0;

			double volume = weight * reps;		

			heaviestWeight = Math.max(weight, heaviestWeight);
			bestRM = Math.max(rm, bestRM);
			bestSetVol = Math.max(volume, bestSetVol);


			if(currDate.equals(record.get("EndTime")))
				sessionVolume += volume;
			else {
				bestSessVol = Math.max(sessionVolume, bestSessVol);

				sessionVolume = volume;
				currDate = record.get("EndTime");
			}		
		}
		bestSessVol = Math.max(sessionVolume, bestSessVol);

        return List.of(heaviestWeight+"kg", bestRM+"kg", bestSetVol+"kg", bestSessVol+"kg");
	}
}
