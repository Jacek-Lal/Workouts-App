package org.projects.workoutsapp.controllers;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.projects.workoutsapp.utility.DataLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ExerciseController {

    protected List<HashMap<String, String>> exerciseList;
    protected String activeMuscleFilter;
    protected String activeEquipmentFilter;
    protected int gridCols;
    @FXML
    protected MenuButton musclesFilter;
    @FXML
    protected MenuButton equipmentFilter;
    @FXML
    protected TextField searchBar;
    @FXML
    protected GridPane exercisesGrid;

    public ExerciseController() {
        this.exerciseList = DataLoader.loadExercises();
        this.activeEquipmentFilter = "";
        this.activeMuscleFilter = "";
        /*
		CompletableFuture.supplyAsync(DataLoader::loadExercises).thenAccept(exercises->{
			this.exerciseList = exercises;
        }).thenRun(()->{
			try {
				this.setFilters();
				this.showExercises();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		 */
    }

    public abstract void chooseExercise(MouseEvent e) throws IOException;

    public void setup() throws IOException {
        System.out.println(musclesFilter + " " + equipmentFilter);

        setFilters();
        showExercises();
    }
    protected void showExercises() throws IOException {
        int counter = 0;
        List<HashMap<String, String>> filteredExercises = getFilteredExercises();

        for(HashMap<String,String> exercise : filteredExercises) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ExerciseFromDB.fxml"));
            Pane root = loader.load();
            root.setId(Integer.toString(counter));

            root.setOnMouseClicked(e -> {
                try { chooseExercise(e); }
                catch (IOException err) { err.printStackTrace(); }
            });

            ObservableList<Node> labels = root.getChildren();
            Label exerciseNameLabel = ((Label) labels.get(0));
            HBox box = ((HBox) labels.get(3));
            Label musclePrimaryLabel = (Label) box.getChildren().getFirst();
            Label muscleSecondaryLabel = (Label) box.getChildren().getLast();
            Label typeLabel = ((Label) labels.get(4));

            exerciseNameLabel.setText(exercise.get("Name"));
            musclePrimaryLabel.setText(exercise.get("Primary Muscle"));
            muscleSecondaryLabel.setText(exercise.get("Secondary Muscle"));
            typeLabel.setText(exercise.get("Equipment"));

            this.exercisesGrid.add(root, counter % gridCols, counter / gridCols);
            counter += 1;
        }
    }
    protected void setFilters() {
        Set<String> equipmentNames = exerciseList.stream().map(map -> map.get("Equipment")).collect(Collectors.toSet());
        Set<String> muscleNames = exerciseList.stream().map(map -> map.get("Primary Muscle")).collect(Collectors.toSet());
        Set<String> secondaryMuscleNames = exerciseList.stream().map(map -> map.get("Secondary Muscle")).collect(Collectors.toSet());
        secondaryMuscleNames.remove(null);
        muscleNames.addAll(secondaryMuscleNames);
        equipmentNames.add("");

        this.musclesFilter.setId("activeMuscle");
        this.equipmentFilter.setId("activeEquipment");

        setFilterItems(this.musclesFilter, muscleNames);
        setFilterItems(this.equipmentFilter, equipmentNames);
    }
    private void setFilterItems(MenuButton menu, Set<String> names) {

        MenuItem emptyItem = new MenuItem();
        emptyItem.setText("");
        emptyItem.setId(menu.getId());

        emptyItem.setOnAction(e -> {
            try { handleFilter(e, menu, emptyItem); }
            catch (IOException err) { err.printStackTrace(); }
        });

        menu.getItems().add(emptyItem);
        names.remove("");

        for(String name : names) {
            MenuItem menuItem = new MenuItem();
            menuItem.setText(name);
            menuItem.setId(menu.getId());

            menuItem.setOnAction(e -> {
                try { handleFilter(e, menu, menuItem); }
                catch (IOException err) { err.printStackTrace(); }
            });
            menu.getItems().add(menuItem);
        }
    }
    private void handleFilter(Event e, MenuButton menu, MenuItem menuItem) throws IOException {
        String clickedItem = ((MenuItem)e.getTarget()).getText();
        if(menu.getText().equals(clickedItem)) return;

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
        return this.exerciseList.stream()
                .filter(r -> this.activeMuscleFilter.isEmpty() ||
                        r.get("Primary Muscle") == null ||
                        r.get("Primary Muscle").equals(this.activeMuscleFilter) ||
                        r.get("Secondary Muscle") == null ||
                        r.get("Secondary Muscle").equals(this.activeMuscleFilter))
                .filter(r -> this.activeEquipmentFilter.isEmpty() ||
                        r.get("Equipment") == null ||
                        r.get("Equipment").equals(this.activeEquipmentFilter))
                .filter(r -> this.searchBar == null ||
                        r.get("Name").toLowerCase().contains(this.searchBar.getText().toLowerCase()))
                .collect(Collectors.toList());
    }
}
