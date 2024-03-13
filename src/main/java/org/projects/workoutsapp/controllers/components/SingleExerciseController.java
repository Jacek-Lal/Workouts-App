package org.projects.workoutsapp.controllers.components;

import org.projects.workoutsapp.controllers.scenes.NewWorkoutController;
import org.projects.workoutsapp.objects.ExerciseRecord;
import org.projects.workoutsapp.objects.SetRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SingleExerciseController {
	public List<SetController> allSets;
	public NewWorkoutController parent;
	private List<String> exerciseData;
	public Pane container;
	public String exerciseType;

	@FXML
	public VBox setsContainer;
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
		this.exerciseName.setText(exerciseData.getFirst());
		this.exerciseType = exerciseData.getLast();

		if(exerciseType.equals("Bodyweight"))
			setHeader.getChildren().remove(2);

		addSet();
	}
	public void addSet() throws IOException{
        String SET_COMPONENT = "/fxml/components/";
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
	public ExerciseRecord saveExercise() {
		ExerciseRecord exerciseRecord = new ExerciseRecord(exerciseData.getFirst());
		exerciseRecord.setDescription(descriptionField.getText());
		
		for(SetController set: allSets) {
			Optional<SetRecord> optSetRecord = set.saveSet();
			optSetRecord.ifPresent(exerciseRecord::addSet);
		}
		return exerciseRecord;
	}
	public void removeSet(SetController set) {
		this.allSets.remove(set);
		setsContainer.getChildren().remove(set.container);

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
		int totalSets = Integer.parseInt(parent.totalSetsLabel.getText());
		int exerciseSets = this.allSets.size();
		parent.totalSetsLabel.setText(String.valueOf(totalSets - exerciseSets));
		parent.deleteExercise(this);
	}
	public void replaceExercise() throws IOException {
		parent.showExercises();
		deleteExercise();
	}

}
