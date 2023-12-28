package application.controllers;

import application.objects.ExerciseRecord;
import application.objects.SetRecord;
import javafx.event.ActionEvent;
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
    public ExerciseRecord exerciseRecord;
	public List<SetController> allSets; 
	public NewWorkoutController parent;
	public List<String> exerciseData;
	public Pane container;
	@FXML
	private VBox setsContainer;
	@FXML
	private TextField descriptionField;
	@FXML
	private Label exerciseName;
	@FXML
	private HBox setHeader;

    public void setup(NewWorkoutController parent, Pane container, List<String> exerciseData) throws IOException {
		this.parent = parent;
		this.container = container;
		this.exerciseData = exerciseData;
		this.allSets = new ArrayList<>();
		this.exerciseRecord = new ExerciseRecord(exerciseData.getFirst());
		this.exerciseName.setText(exerciseData.getFirst());

		if(exerciseData.getLast().equals("Bodyweight"))
			setHeader.getChildren().remove(2);

		addSet();
	}
	public void addSet() throws IOException{
        String SET_COMPONENT = "/application/components/";
		SET_COMPONENT += (exerciseData.getLast().equals("Bodyweight")) ? "BodyweightSet.fxml" : "Set.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(SET_COMPONENT));
		Parent root = loader.load();
		SetController controller = loader.getController();
		controller.setup(this, (HBox)root,allSets.size() + 1);
		
		this.allSets.add(controller);	

		VBox.setVgrow(root, Priority.ALWAYS);
		
		setsContainer.getChildren().add(controller.getId(),root);

		int currentSets = Integer.parseInt(parent.totalSetsLabel.getText());
		parent.totalSetsLabel.setText(String.valueOf(currentSets + 1));

		//parent.scrollDown();
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
	public void deleteExercise(){
		parent.deleteExercise(this);
	}
	public void replaceExercise(ActionEvent e) throws IOException {
		parent.showExercises(e);
		deleteExercise();
	}

}
