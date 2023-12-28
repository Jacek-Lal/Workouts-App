package application.controllers;

import application.objects.ExerciseRecord;
import application.objects.SetRecord;
import application.objects.WorkoutRecord;
import application.utility.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewWorkoutController {
	private static final String EXERCISE_LIST_SCENE_PATH = "/application/scenes/" + "ExercisesList.fxml";
	private static final String EXERCISE_COMPONENT_PATH = "/application/components/"+ "Exercise.fxml";
	private static final String MODAL_COMPONENT_PATH = "/application/components/"+ "WorkoutCloseModal.fxml";
	private WorkoutRecord workout;
	private LocalTime duration;
	private List<SingleExerciseController> exerciseControllers;
	@FXML
	private Label workoutNameLabel;
	@FXML
	public TextField durationField;
	@FXML
	public Label totalWeightLabel;
	@FXML
	public Label totalSetsLabel;
	@FXML
	public ScrollPane exercisesScrollPane;
	@FXML
	public VBox exercisesContainer;

	public void setup(String name){
		this.exerciseControllers = new ArrayList<>();
		this.workout = new WorkoutRecord();
		this.duration = LocalTime.of(0,0,0);
		this.workoutNameLabel.setText(name);
		this.workout.setName(name);
		startTimer();
	}
	public void startTimer(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateTimer();
			}
		}, 0, 1000);
	}
	public void updateTimer(){

		StringBuilder formattedTime = new StringBuilder();
		duration = duration.plusSeconds(1);

		if (duration.getHour() > 0) {
			formattedTime.append(duration.getHour()).append("h ");
		}
		if(duration.getMinute() > 0){
			formattedTime.append(duration.getMinute()).append("min ");
		}
		formattedTime.append(duration.getSecond()).append("s");

		durationField.setText(formattedTime.toString());
	}
	public void showExercises(ActionEvent e) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource(EXERCISE_LIST_SCENE_PATH));
		Parent root = loader.load();	
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		ExerciseListController controller = loader.getController();

		stage.setTitle("Exercises List");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(((Node) e.getSource()).getScene().getWindow());
		stage.setScene(scene);
		
		controller.setup(stage, this);	// pass stage and workout controller to new stage
		
		stage.show();	
	}
	public void addExercise(List<String> exerciseData) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource(EXERCISE_COMPONENT_PATH));
		Parent root = loader.load();	
		SingleExerciseController controller = loader.getController();

		VBox.setVgrow(root, Priority.ALWAYS);
		controller.setup(this,(Pane)root, exerciseData);
		
		exerciseControllers.add(controller);
		exercisesContainer.getChildren().add(root);
		
		scrollDown();
	}
	public void saveWorkout(ActionEvent e) throws IOException {
		for (SingleExerciseController exercise : exerciseControllers) {
			exercise.saveExercise();
			workout.addExercise(exercise.exerciseRecord);
		}
		workout.endTime = LocalDateTime.now();
		convertToCsv();

		new SceneLoader().loadMain(e);
	}
	private void convertToCsv() throws IOException{
    	FileWriter outputFile = new FileWriter("src/application/Workouts.csv", true);

		for(ExerciseRecord exercise : workout.getExercises()) {
			for (SetRecord set : exercise.getSets()) {
				writeToCsv(outputFile,
						workout.getName(),
						workout.getStartTime(),
						workout.getEndTime(),
						exercise.getName(),
						exercise.getDescription(),
						set.getId(),
						set.getWeight(),
						set.getReps());
			}
		}
		outputFile.flush();
		outputFile.close();
	}
	private void writeToCsv(FileWriter outputFile, String... values) throws IOException {
		for(String value : values){
			outputFile.append(value);
			outputFile.append(";");
		}
		outputFile.append("\n");
	}
	public void scrollDown() {
		exercisesScrollPane.applyCss();
		exercisesScrollPane.layout();
		exercisesScrollPane.setVvalue(exercisesScrollPane.getVmax());
	}
	public void discardWorkout(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(MODAL_COMPONENT_PATH));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = new Stage();

		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(((Node) e.getSource()).getScene().getWindow());
		stage.setScene(scene);
		stage.show();
	}
	public Stage closeModal(ActionEvent e){
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Stage owner = (Stage) stage.getOwner();

		stage.close();
		return owner;
	}
	public void goToMainView(ActionEvent e) throws IOException {
		Stage owner = closeModal(e);
		new SceneLoader().loadMain(owner);
	}

	public void deleteExercise(SingleExerciseController exercise) {
		exercisesContainer.getChildren().remove(exercise.container);
		exerciseControllers.remove(exercise);
	}
}
