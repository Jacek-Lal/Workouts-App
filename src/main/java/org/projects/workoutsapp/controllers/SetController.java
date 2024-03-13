package org.projects.workoutsapp.controllers;

import org.projects.workoutsapp.objects.SetRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class SetController {
	private int id;
	private double weight;
	private int reps;
	private Label totalVolumeLabel;
	private SingleExerciseController parent;
	public HBox container;
	@FXML
	private Label setIdLabel;
	@FXML
	private TextField weightLabel;
	@FXML
	private TextField repsLabel;
	
	public void setup(SingleExerciseController parent, HBox root, int id) {
		this.parent = parent;
		this.totalVolumeLabel = parent.parent.totalWeightLabel;
		this.container = root;
		this.id = id;
		this.weight = 0;
		this.reps = 0;

		setIdLabel.setText(Integer.toString(id));
	}
	public void validateInput(KeyEvent e) {
		TextField textField = (TextField)e.getSource();
		if (textField.getText().isEmpty()) return;

		if(textField.getId().equals("weightLabel")) {
			if((!e.getCharacter().matches("[0-9\b,.]"))) {
				textField.deletePreviousChar();
				return;
			}
			if(e.getCharacter().charAt(0) == ',') {
				textField.deletePreviousChar();
				textField.appendText(".");
			}
		}
		
		if(textField.getId().equals("repsLabel")) {
			if((!e.getCharacter().matches("[0-9 \b]"))) {
				textField.deletePreviousChar();
				return;
			}
		}
		updateTotalVolume();
	}
	private void updateTotalVolume()  {
		// Get total workout volume excluding this set
		double totalVolume = Double.parseDouble(totalVolumeLabel.getText()) - this.getSetVolume();
		
		// Get updated weight/reps
		String weight_ = this.weightLabel != null ? this.weightLabel.getText() : "";
		String reps_ = this.repsLabel.getText();
		
		if(weight_.isEmpty()) weight_ = "0.0";
		if(reps_.isEmpty()) reps_ = "0";
		
		this.weight = Double.parseDouble(weight_);
		this.reps = Integer.parseInt(reps_);
		
		// Set updated volume
		double newVolume = totalVolume + this.getSetVolume();
		this.totalVolumeLabel.setText(String.valueOf(newVolume));
	}
	public Optional<SetRecord> saveSet(){
		if(this.reps == 0 || (this.weight == 0 && !parent.exerciseType.equals("Bodyweight"))) return Optional.empty();

		return Optional.of(new SetRecord(this.id, this.weight, this.reps));
	}
	public void removeSet(){
		// Update total workout volume
		double totalVolume = Double.parseDouble(totalVolumeLabel.getText());
		double newVolume = totalVolume - this.getSetVolume();
		this.totalVolumeLabel.setText(String.valueOf(newVolume));

		parent.removeSet(this);
	}
	public void setId(int id){this.id = id;}
	public int getId() {return this.id;}
	public double getSetVolume() {return this.weight*this.reps;}
}
