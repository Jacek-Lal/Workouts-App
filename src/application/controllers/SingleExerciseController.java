package application.controllers;

import application.objects.ExerciseRecord;
import application.objects.SetRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleExerciseController {
	private static final String SET_COMPONENT_PATH = "/application/components/Set.fxml";
	public ExerciseRecord exerciseRecord;
	public List<SetController> allSets; 
	public NewWorkoutController parent;
	private Pane container;
	@FXML
	private VBox setsContainer;
	@FXML
	private TextField descriptionField;
	@FXML
	private Label exerciseName;

    public void setup(NewWorkoutController parent, Pane container, String exerciseName) throws IOException {
		this.parent = parent;
		this.container = container;

		this.allSets = new ArrayList<>();
		this.exerciseRecord = new ExerciseRecord(exerciseName);
		this.exerciseName.setText(exerciseName);
		
		addSet();
	}
	public void addSet() throws IOException{		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(SET_COMPONENT_PATH));
		Parent root = loader.load();
		SetController controller = loader.getController();
		controller.setup(this, (HBox)root,allSets.size() + 1);
		
		this.allSets.add(controller);	

		VBox.setVgrow(root, Priority.ALWAYS);
		
		setsContainer.getChildren().add(controller.getId(),root);

		int currentSets = Integer.parseInt(parent.totalSetsLabel.getText());
		parent.totalSetsLabel.setText(String.valueOf(currentSets + 1));

		parent.scrollDown();
	}
	public void saveExercise() {
		exerciseRecord.setDescription(descriptionField.getText());
		
		for(SetController set: allSets) {
			
			SetRecord setRecord = new SetRecord(set.getId(), set.getWeight(), set.getReps());
			exerciseRecord.addSet(setRecord);
		}
	}
	public void removeSet(SetController set) {
		setsContainer.getChildren().remove(set.container);
		this.allSets.remove(set);

		int currentSets = Integer.parseInt(parent.totalSetsLabel.getText());
		parent.totalSetsLabel.setText((currentSets - 1)+"");
		updateSetsId();
	}
	private void updateSetsId(){
		for(int i = 0; i < allSets.size(); i++){
			SetController set = allSets.get(i);
			set.setId(i+1);
			((Label)set.container.getChildren().getFirst()).setText(i+1+"");
		}
	}
}
