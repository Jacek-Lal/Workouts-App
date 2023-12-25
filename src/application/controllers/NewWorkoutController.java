package application.controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import application.objects.ExerciseRecord;
import application.objects.SetRecord;
import application.objects.WorkoutRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewWorkoutController extends Controller<Object> {
	private static final String EXERCISE_LIST_SCENE_PATH = SCENES_PATH + "ExercisesList.fxml";
	private static final String EXERCISE_COMPONENT_PATH = COMPONENTS_PATH + "Exercise.fxml";
	private WorkoutRecord workout;
	private List<SingleExerciseController> exerciseControllers;
	@FXML
	private Label workoutNameLabel;
	@FXML
	public Label totalWeightLabel;
	@FXML
	public Label totalSetsLabel;
	@FXML
	public ScrollPane exercisesScrollPane;
	@FXML
	private VBox exercisesContainer;
	
	@FXML
	public void initialize() {
		this.exerciseControllers = new ArrayList<>();
		this.workout = new WorkoutRecord();
	}
	
	public void setWorkoutName(String name) {
		this.workoutNameLabel.setText(name);
		this.workout.setName(name);
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
	
	public void addExercise(String exerciseName) throws IOException{
		for(SingleExerciseController exercise : exerciseControllers) {
			if(exercise.exerciseRecord.getName().equals(exerciseName)) return;
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(EXERCISE_COMPONENT_PATH));
		Parent root = loader.load();	
		SingleExerciseController controller = loader.getController();
		
		VBox.setVgrow(root, Priority.ALWAYS);
		controller.setup(this, exerciseName);
		
		exerciseControllers.add(controller);
		exercisesContainer.getChildren().add(root);
		
		scrollDown(exercisesScrollPane);	
	}
	
	public void saveWorkout(ActionEvent e) throws IOException {
		for (SingleExerciseController exercise : exerciseControllers) {
			exercise.saveExercise();
			workout.addExercise(exercise.exerciseRecord);
		}
		this.convertToCsv();
		this.goToMainView(e);
	}
	
	private void convertToCsv() throws IOException{
    	FileWriter outputFile = new FileWriter("src/application/Workouts.csv", true);

		for(ExerciseRecord exercise : workout.getExercises()) {
			for (SetRecord set : exercise.getSets()) {				
				outputFile.append(workout.getName());
				outputFile.append(";");
				outputFile.append(workout.getDate());
				outputFile.append(";");
				outputFile.append(exercise.getName());
				outputFile.append(";");
				outputFile.append(exercise.getDescription());
				outputFile.append(";");
				outputFile.append(set.getId());
				outputFile.append(";");
				outputFile.append(set.getWeight());
				outputFile.append(";");
				outputFile.append(set.getReps());
				outputFile.append("\n");
			}
		}
		outputFile.flush();
		outputFile.close();
	}	

	
	   
}
