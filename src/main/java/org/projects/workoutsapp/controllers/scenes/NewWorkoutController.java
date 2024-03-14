package org.projects.workoutsapp.controllers.scenes;

import org.projects.workoutsapp.controllers.components.ExerciseListController;
import org.projects.workoutsapp.controllers.MainController;
import org.projects.workoutsapp.controllers.components.SingleExerciseController;
import org.projects.workoutsapp.objects.ExerciseRecord;
import org.projects.workoutsapp.objects.WorkoutRecord;
import org.projects.workoutsapp.utility.Converter;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.projects.workoutsapp.utility.DBConnector;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewWorkoutController{

	public WorkoutRecord workout;
	public int duration;
	public int rest;
	public List<SingleExerciseController> exerciseControllers;
	private MainController mainController;
	public AnchorPane scene;
	private GridPane exercisesContainer;

	@FXML
	private Label workoutNameLabel;
	@FXML
	private TextField durationField;
	@FXML
	public Label totalWeightLabel;
	@FXML
	public Label totalSetsLabel;
	@FXML
	private ScrollPane exercisesScrollPane;
	@FXML
	private MenuButton restTimer;

	public void setup(MainController main) throws IOException {
		this.mainController = main;
		this.exerciseControllers = new ArrayList<>();
		this.scene = (AnchorPane) exercisesScrollPane.getParent();
		this.workout = new WorkoutRecord();
		this.duration = 0;
		this.rest = 0;
		this.exercisesContainer = addGrid();
		setTimerMenuItems(10);
		startTimer();
	}
	public void setName(String name){
		this.workoutNameLabel.setText(name);
		this.workout.setName(name);
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

		exercisesScrollPane.setContent(gridPane);

		return gridPane;
	}
	public void showExercises() throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scenes/ExercisesList.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		ExerciseListController controller = loader.getController();

		stage.setTitle("Exercises List");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(mainController.stage);
		stage.setScene(scene);
		stage.setResizable(false);

		controller.setup(stage, this);	// pass stage and workout controller to new stage

		stage.show();
	}
	public void addExercise(List<String> exerciseData) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/Exercise.fxml"));
		Pane root = loader.load();
		SingleExerciseController controller = loader.getController();

		VBox.setVgrow(root, Priority.ALWAYS);
		controller.setup(this,root, exerciseData);

		int counter = this.exerciseControllers.size();

		this.exercisesContainer.add(root,counter % 2, counter / 2);
		this.exerciseControllers.add(controller);

		controller.setsContainer.getChildren().addListener((ListChangeListener<Node>) (change) -> {
			while (change.next()){
				int exerciseNumber = exerciseControllers.size();
				int index = exerciseControllers.indexOf(controller);

				SingleExerciseController siblingEx = null;
				if(index % 2 == 1) 	siblingEx = exerciseControllers.get(index - 1);
				if(index % 2 == 0 && exerciseNumber > index + 1)	siblingEx = exerciseControllers.get(index + 1);

				if (change.wasAdded()) {
                    if (siblingEx == null || controller.allSets.size() > siblingEx.allSets.size())
						controller.container.setPrefHeight(controller.container.getHeight() + 40);

					if((index/2)+1 == exercisesContainer.getRowCount())
						scrollDown();
				}

				if(change.wasRemoved()){
					if (siblingEx == null || controller.allSets.size() >= siblingEx.allSets.size())
						controller.container.setPrefHeight(controller.container.getHeight() - 40);

					if((index/2)+1 == exercisesContainer.getRowCount())
						exercisesContainer.setPrefHeight(exercisesContainer.getHeight() - 40);
				}
			}
		});
		scrollDown();
	}
	public void deleteExercise(SingleExerciseController exercise) {
		//exercisesContainer.getChildren().remove(exercise.container);
		exercisesContainer.getChildren().clear();
		exerciseControllers.remove(exercise);
		int counter = 0;
		for (SingleExerciseController controller : exerciseControllers){
			exercisesContainer.add(controller.container,counter % 2, counter / 2);
			counter += 1;
		}
	}
	public void saveWorkout() throws IOException {
		for (SingleExerciseController exercise : exerciseControllers) {
			ExerciseRecord exerciseRecord = exercise.saveExercise();
			workout.addExercise(exerciseRecord);
		}
		workout.endTime = LocalDateTime.now();
		DBConnector.saveWorkout(this.workout);

		this.mainController.activeWorkout = null;
		this.mainController.menuController.hideActiveWorkout();
		this.mainController.menuController.goToHomeView();
	}
	private void scrollDown() {
		exercisesScrollPane.applyCss();
		exercisesScrollPane.layout();
		exercisesScrollPane.setVvalue(exercisesScrollPane.getVmax());
	}
	private void startTimer(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateTimer(timer);
			}
		}, 0, 1000);
	}
	private void updateTimer(Timer timer){
		duration += 1;
		String durationFormatted = Converter.secondsToTime(duration);
		durationField.setText(durationFormatted);

		if(!this.mainController.activeView.equals("NewWorkoutTab"))
			Platform.runLater(() -> mainController.menuController.showTimer(mainController.menuController.activeWorkoutTimer, durationFormatted));

		if(this.mainController.activeWorkout == null)
			timer.cancel();
	}
	private void startRest(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateRest(timer);
			}
		}, 0, 1000);
	}
	private void updateRest(Timer timer){
		if(rest <= 0) {
			Platform.runLater(() -> restTimer.setText("00:00"));
			timer.cancel();
			rest = 0;
			return;
		}
		rest -= 1;

		String restFormatted = Converter.secondsToTime(rest);
		Platform.runLater(() -> {
			restTimer.setText(restFormatted);
			if(!this.mainController.activeView.equals("NewWorkoutTab"))
				mainController.menuController.showTimer(mainController.menuController.activeWorkoutRest, restFormatted);
			if(this.mainController.activeWorkout == null)
				timer.cancel();

		});
	}
	public void increaseRest(){
		if(rest == 0) {
			rest = 15;
			startRest();
			return;
		}
		rest  += 15;
		restTimer.setText(Converter.secondsToTime(rest));
	}
	public void decreaseRest(){
		if(rest > 0) rest = Math.max(0, rest-15);
		restTimer.setText(Converter.secondsToTime(rest));
	}
	private void setTimerMenuItems(int amount) {

		for(int i = 1; i <= amount; i++) {
			String time = Converter.secondsToTime(i*15);
			MenuItem menuItem = new MenuItem();
			menuItem.setText(time);
			menuItem.setId(String.valueOf(i));

			menuItem.setOnAction(event -> handleTimerMenu(menuItem.getText()));

			restTimer.getItems().add(menuItem);
		}
	}
	private void handleTimerMenu(String time){
		int prevRest = rest;
		rest = Converter.timeToSeconds(time);
		restTimer.setText(time);
		if(prevRest == 0) startRest();
	}
	public void modalOpen() throws IOException {
		this.mainController.sceneLoader.loadModal("/fxml/components/WorkoutModal.fxml", this);
	}
	public void modalClose(ActionEvent e){
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		stage.close();
	}
	public void modalDiscard(ActionEvent e) throws IOException {
		modalClose(e);

		this.mainController.activeWorkout = null;
		this.mainController.menuController.goToHomeView();
		this.mainController.menuController.hideActiveWorkout();
	}
}
