package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class SetController extends Controller<Object> {

	private int id;
	private double weight;
	private int reps;

	private Label totalVolumeLabel;
	
	@FXML
	private Label setIdLabel;
	@FXML
	private TextField weightLabel;
	@FXML
	private TextField repsLabel;
	
	public void setup(Label label, int id) {
		this.totalVolumeLabel = label;
		this.id = id;
		this.weight = 0;
		this.reps = 0;
		
		setIdLabel.setText(Integer.toString(id));
	}
	
	public void validateInput(KeyEvent e) {
		TextField textField = (TextField)e.getSource();
		if (textField.getText().isEmpty()) return;
		//System.out.println(textField.getId().equals("weightLabel"));
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
	
	public void updateTotalVolume()  {
				
		// Get total workout volume excluding this set
		double totalVolume = Double.parseDouble(totalVolumeLabel.getText()) - this.getSetVolume();
		
		// Get updated weight/reps
		String weight_ = this.weightLabel.getText();
		String reps_ = this.repsLabel.getText();
		
		if(weight_.isEmpty()) weight_ = "0.0";
		if(reps_.isEmpty()) reps_ = "0";
		
		this.weight = Double.parseDouble(weight_);
		this.reps = Integer.parseInt(reps_);
		
		// Set updated volume
		double newVolume = totalVolume + this.getSetVolume();
		this.totalVolumeLabel.setText(String.valueOf(newVolume));
	}
	

	public int getId() {return this.id;}
	
	public double getWeight() {return this.weight;}
	
	public int getReps() {return this.reps;}
	
	public double getSetVolume() {return this.weight*this.reps;}
}
