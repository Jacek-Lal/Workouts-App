package application.controllers;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExercisesController extends Controller<Object> {
	private static final String EXERCISE_DETAILS_SCENE_PATH = SCENES_PATH + "ExerciseDetails.fxml";
	private static final String DATABASE_EXERCISE_COMPONENT_PATH = COMPONENTS_PATH+"ExerciseFromDB.fxml";
	private final List<HashMap<String, String>> exerciseList;
	private String activeMuscleFilter;
	private String activeEquipmentFilter;

	@FXML
	private MenuButton musclesFilter;
	@FXML
	private MenuButton equipmentFilter;
	@FXML
	private TextField searchBar;
	@FXML
	private GridPane exercisesGrid;
	
	public ExercisesController() {
		this.exerciseList = this.loadExercises();
		this.activeEquipmentFilter = "";
		this.activeMuscleFilter = "";
	}
	@FXML
	public void initialize() throws IOException {
		showExercises();
		setFilters();
	}
	
	public void chooseExercise(MouseEvent e) throws IOException{
		Pane exercise = ((Pane)e.getSource());
		String exerciseName = ((Label)exercise.getChildren().getFirst()).getText();
		
		viewDetails(e, exerciseName);
	}
	
	public void viewDetails(MouseEvent e, String exerciseName) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource(EXERCISE_DETAILS_SCENE_PATH));
		Parent root = loader.load();	
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		ExerciseDetailsController controller = loader.getController();	
		
		controller.setup(exerciseName);
		
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(((Node) e.getSource()).getScene().getWindow());
		stage.setScene(scene);
				
		stage.show();	
	}
	
	private void showExercises() throws IOException {
		int counter = 0;
		List<HashMap<String, String>> filteredExercises = getFilteredExercises();

		for(HashMap<String,String> exercise : filteredExercises) {
						
			FXMLLoader loader = new FXMLLoader(getClass().getResource(DATABASE_EXERCISE_COMPONENT_PATH));
			Pane root = loader.load();	
			root.setId(Integer.toString(counter));
			
			root.setOnMouseClicked(e -> {
                try { chooseExercise(e); }
                catch (IOException e1) { e1.printStackTrace(); }
            });
			
			ObservableList<Node> labels = root.getChildren(); 
            Label exerciseNameLabel = ((Label) labels.get(0));
            Label musclePrimaryLabel = ((Label) labels.get(3));
            Label muscleSecondaryLabel = ((Label) labels.get(4));
            Label typeLabel = ((Label) labels.get(5));
            
            exerciseNameLabel.setText(exercise.get("Name"));
            musclePrimaryLabel.setText(exercise.get("Primary Muscle"));
            muscleSecondaryLabel.setText(exercise.get("Secondary Muscle"));
            typeLabel.setText(exercise.get("Equipment"));
            
            this.exercisesGrid.add(root, counter % 2, counter / 2);
            
            counter += 1;
		}

	}
	
	private void setFilters() {	
		Set<String> equipmentNames = exerciseList.stream().map(map -> map.get("Equipment")).collect(Collectors.toSet());
		Set<String> muscleNames = exerciseList.stream().map(map -> map.get("Primary Muscle")).collect(Collectors.toSet());
		Set<String> secondaryMuscleNames = exerciseList.stream().map(map -> map.get("Secondary Muscle")).collect(Collectors.toSet());
		
		muscleNames.addAll(secondaryMuscleNames);
		this.musclesFilter.setId("activeMuscle");
		this.equipmentFilter.setId("activeEquipment");
		
		setMenuItems(this.musclesFilter, muscleNames);
		setMenuItems(this.equipmentFilter, equipmentNames);		
	}
	
	private void setMenuItems(MenuButton menu, Set<String> names) {
		for(String name : names) {			
			MenuItem menuItem = new MenuItem();
			menuItem.setText(name);
			menuItem.setId(menu.getId());
			
			menuItem.setOnAction(event -> {
				try { handleFilterMenu(event, menu, menuItem); } 
				catch (IOException e) { e.printStackTrace(); }
			});
			
			menu.getItems().add(menuItem);
		}
	}

	private void handleFilterMenu(Event event, MenuButton menu, MenuItem menuItem) throws IOException {
		String clickedItem = ((MenuItem)event.getTarget()).getText();
		menu.setText(clickedItem);
		
		if(menuItem.getId().equals("activeMuscle"))
			this.activeMuscleFilter = clickedItem;
		else 
			this.activeEquipmentFilter = clickedItem;
		
		this.exercisesGrid.getChildren().clear();
		this.showExercises();
	}

	public void handleSearchBar(KeyEvent e) throws IOException {	
		
		if((!e.getCharacter().matches("^[a-zA-Z\b ]*$"))) 
			this.searchBar.deletePreviousChar();	
		
		this.exercisesGrid.getChildren().clear();
		this.showExercises();
	}
	
	private List<HashMap<String, String>> getFilteredExercises() {
		String searchBarText = this.searchBar.getText().toLowerCase();

		return this.exerciseList.stream()
				.filter(r -> this.activeMuscleFilter.isEmpty() || r.get("Primary Muscle").equals(this.activeMuscleFilter))
				.filter(r -> this.activeEquipmentFilter.isEmpty() || r.get("Equipment").equals(this.activeEquipmentFilter))
				.filter(r -> r.get("Name").toLowerCase().contains(searchBarText))
				.collect(Collectors.toList());
		
	}
}

