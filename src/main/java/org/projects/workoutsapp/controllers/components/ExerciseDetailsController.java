package org.projects.workoutsapp.controllers.components;

import org.projects.workoutsapp.utility.DataLoader;
import org.projects.workoutsapp.utility.LabelManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ExerciseDetailsController{
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
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource( "/fxml/components/ExerciseSummary.fxml"));
		Parent root = loader.load();	
		
		List<Label> labels = LabelManager.getLabelsWithId(root);
		LabelManager.addData(labels, this.summaryData);
		//LineChart<Number, Number> chart = createChart();

		detailsContainer.getChildren().add(root);
		//detailsContainer.getChildren().add(chart);

		summaryView = true;
	}
	private LineChart<Number, Number> createChart(){
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();

		// Creating the line chart
		LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setTitle("Line Chart Example");

		// Defining a series
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		List<Double> data = getVolume();

		// Adding data to the series
		for(int i = 0; i < data.size(); i++){
			series.getData().add(new XYChart.Data<>(i, data.get(i)));
		}
		// Adding the series to the chart
		lineChart.getData().add(series);

		return lineChart;
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


				FXMLLoader headerLoader = new FXMLLoader(getClass().getResource("/fxml/components/WorkoutHeader.fxml"));
				Parent header = headerLoader.load();
				List<Label> headerLabels = LabelManager.getLabelsWithId(header);
				LabelManager.addData(headerLabels, List.of(record.get("Name"),record.get("StartTime")));

				exerciseLoader = new FXMLLoader(getClass().getResource("/fxml/components/ExerciseFromHistory.fxml"));
				exercise = exerciseLoader.load();

				VBox box = (VBox) ((Pane) exercise).getChildren().getLast();
				Label descLabel = (Label) box.getChildren().getFirst();
				setsContainer = (VBox) box.getChildren().getLast();

				if(record.get("Weight").equals("0.0")) {
					((Label)((HBox) setsContainer.getChildren().getFirst()).getChildren().getLast()).setText("Reps");
				}

				List<Label> exerciseLabels = LabelManager.getLabelsWithId(exercise);

				if(record.get("Description").isEmpty())
					box.getChildren().remove(descLabel);
				else
					exerciseLabels.add(descLabel);

				LabelManager.addData(exerciseLabels, List.of(record.get("Exercise"),record.get("Description")));

				detailsContainer.getChildren().addFirst(exercise);
				detailsContainer.getChildren().addFirst(header);
			}
			if(currDate.equals(record.get("EndTime"))) {
				FXMLLoader setLoader = new FXMLLoader(getClass().getResource("/fxml/components/SetFromHistory.fxml"));
				Parent set = setLoader.load();
				
				List<Label> labels = LabelManager.getLabelsWithId(set);
				
				String weight = record.get("Weight");
				List<String> data = List.of(record.get("SetNumber"), (weight.equals("0.0") ? "" : weight+"kg x ") + record.get("Reps")+" reps");

				LabelManager.addData(labels, data);

                assert setsContainer != null;
                setsContainer.getChildren().add(set);
			}
		}
		
	}
	private List<HashMap<String, String>> getCurrentExerciseRecords(){
		List<HashMap<String, String>> workoutHistory = DataLoader.loadWorkouts();

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
	private List<Double> getVolume(){
		List<Double> list = new ArrayList<>();

		double sessionVolume = 0;
		String currDate = "";

		for (HashMap<String, String> record : this.currentExerciseRecords) {

			if(currDate.isEmpty()) currDate = record.get("EndTime");

			double weight = Double.parseDouble(record.get("Weight"));
			int reps = Integer.parseInt(record.get("Reps"));
			double volume = weight * reps;

			if(currDate.equals(record.get("EndTime")))
				sessionVolume += volume;
			else {
				list.add(sessionVolume);
				sessionVolume = volume;
				currDate = record.get("EndTime");
			}
		}
		if(sessionVolume != 0) list.add(sessionVolume);
		return list;
	}
}
