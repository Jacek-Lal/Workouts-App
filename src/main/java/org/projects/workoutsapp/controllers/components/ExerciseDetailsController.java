package org.projects.workoutsapp.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.projects.workoutsapp.utility.DatabaseClient;
import org.projects.workoutsapp.utility.LabelManager;
import org.projects.workoutsapp.utility.SceneLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExerciseDetailsController{
    private boolean summaryView;
	private List<HashMap<String, String>> currentExerciseRecords;
	private List<String> summaryData;
	@FXML
	private Label exerciseNameLabel;
	@FXML
	private VBox detailsContainer;
	
	public void setup(String exerciseName) throws IOException {
        this.currentExerciseRecords = DatabaseClient.getExerciseRecords(exerciseName);
		this.summaryData = getSummaryData();

		exerciseNameLabel.setText(exerciseName);
					
		showSummary();
	}
	public void showSummary() throws IOException {
		if(summaryView) return;
		detailsContainer.getChildren().clear();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource( "/fxml/components/ExerciseSummary.fxml"));
		Parent root = loader.load();	

		List<Label> labels = new ArrayList<>();
		LabelManager.getLabelsWithId(root, labels);
		LabelManager.addData(labels, this.summaryData);

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

		String currDate = "";
		VBox setsContainer = null;

		for (HashMap<String, String> record : this.currentExerciseRecords) {
			if(!currDate.equals(record.get("EndTime"))) {
				currDate = record.get("EndTime");

				Parent header = createWorkoutHeader(record);
				Parent exercise = createExercise(record);

				VBox box = (VBox) ((Pane) exercise).getChildren().getLast();
				setsContainer = (VBox) box.getChildren().getLast();

				detailsContainer.getChildren().addFirst(exercise);
				detailsContainer.getChildren().addFirst(header);
			}
			if(currDate.equals(record.get("EndTime"))) {
				Parent set = createSet(record);

                assert setsContainer != null;
                setsContainer.getChildren().add(set);
			}
		}
		
	}
	private Parent createWorkoutHeader(HashMap<String, String> record) throws IOException {
		String name = record.get("Name");
		String date = record.get("StartTime");
		List<String> data = List.of(name, date);

        return SceneLoader.createComponent("/fxml/components/WorkoutHeader.fxml", data);
	}
	private Parent createExercise(HashMap<String, String> record) throws IOException {
		String name = record.get("Exercise");
		String desc = record.get("Description");
		String type = record.get("Weight").equals("0.0") ? "Reps" : "Weight & Reps";
		List<String> data =  List.of(name, desc, type);

		return SceneLoader.createComponent("/fxml/components/ExerciseFromHistory.fxml", data);
	}
	private Parent createSet(HashMap<String, String> record) throws IOException {
		String setId = record.get("SetNumber");
		String weight = record.get("Weight").equals("0.0") ? "" : record.get("Weight")+"kg x ";
		String reps = record.get("Reps") + " reps";
		List<String> data = List.of(setId, weight + reps);

		return SceneLoader.createComponent("/fxml/components/SetFromHistory.fxml", data);
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

			if(currDate.equals(record.get("EndTime"))){
				sessionVolume += volume;}
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
