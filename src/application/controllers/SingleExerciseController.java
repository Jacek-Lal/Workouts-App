package application.controllers;

import application.objects.ExerciseRecord;
import application.objects.SetRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleExerciseController extends Controller<Object> {
	private static final String SET_COMPONENT_PATH = "/application/components/Set.fxml";
	public ExerciseRecord exerciseRecord;
	public List<SetController> allSets; 
	public NewWorkoutController parent;
	
	@FXML
	private VBox setsContainer;
	@FXML
	private TextField descriptionField;
	@FXML
	private Label exerciseName;

    public void setup(NewWorkoutController parent, String exerciseName) throws IOException {
		this.parent = parent;
		this.allSets = new ArrayList<>();
		this.exerciseRecord = new ExerciseRecord(exerciseName);
		this.exerciseName.setText(exerciseName);
		
		addSet();
	}
	
	public void addSet() throws IOException{		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(SET_COMPONENT_PATH));
		Parent root = loader.load();
		SetController controller = loader.getController();
		controller.setup(parent.totalWeightLabel, allSets.size() + 1);
		
		this.allSets.add(controller);	
		
		VBox.setVgrow(root, Priority.ALWAYS);
		
		setsContainer.getChildren().add(controller.getId(),root);
		
		scrollDown(parent.exercisesScrollPane);
		
		updateTotalSets();
	}
		
	private void updateTotalSets() {
		int currentSets = Integer.parseInt(parent.totalSetsLabel.getText());
		parent.totalSetsLabel.setText(String.valueOf(currentSets + 1));
	}

	public void saveExercise() {
		exerciseRecord.setDescription(descriptionField.getText());
		
		for(SetController set: allSets) {
			
			SetRecord setRecord = new SetRecord(set.getId(), set.getWeight(), set.getReps());
			exerciseRecord.addSet(setRecord);
		}
	}
}
