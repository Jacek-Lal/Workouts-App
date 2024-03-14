package org.projects.workoutsapp.controllers;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.projects.workoutsapp.utility.DBConnector;
import org.projects.workoutsapp.utility.LabelManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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

    @FXML
    public void initialize() {
        this.activeEquipmentFilter = "";
        this.activeMuscleFilter = "";

        CompletableFuture.supplyAsync(DBConnector::loadExercises)
                .thenAcceptAsync(result -> {
                    this.exerciseList = result;
                    Platform.runLater(()->{
                        setFilters();
                        showExercises();
                    });
                });
    }
    public abstract void chooseExercise(MouseEvent e) throws IOException;

    protected void showExercises() {

        int counter = 0;
        List<HashMap<String, String>> filteredExercises = getFilteredExercises();

        for(HashMap<String,String> exercise : filteredExercises) {
            Parent root = createExerciseContainer(counter);

            List<Label> labels = new ArrayList<>();
            LabelManager.getLabelsWithId(root, labels);

            Optional<String> secondaryMuscle = Optional.ofNullable(exercise.get("SecondaryMuscle"));
            List<String> data = List.of(exercise.get("Name"),exercise.get("PrimaryMuscle"), secondaryMuscle.orElse(""), exercise.get("Equipment"));

            LabelManager.addData(labels, data);

            this.exercisesGrid.add(root, counter % gridCols, counter / gridCols);
            counter += 1;

        }
    }
    protected void setFilters() {
        Set<String> equipmentNames = exerciseList.stream().map(map -> map.get("Equipment")).collect(Collectors.toSet());
        Set<String> muscleNames = exerciseList.stream().map(map -> map.get("PrimaryMuscle")).collect(Collectors.toSet());
        Set<String> secondaryMuscleNames = exerciseList.stream().map(map -> map.get("SecondaryMuscle")).collect(Collectors.toSet());
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
    public void handleSearchBar(KeyEvent e) {

        if((!e.getCharacter().matches("^[a-zA-Z\b ]*$")))
            this.searchBar.deletePreviousChar();

        this.exercisesGrid.getChildren().clear();
        this.showExercises();
    }
    private List<HashMap<String, String>> getFilteredExercises() {
        return this.exerciseList.stream()
                .filter(r -> this.activeMuscleFilter.isEmpty() ||
                        r.get("PrimaryMuscle") == null ||
                        r.get("PrimaryMuscle").equals(this.activeMuscleFilter) ||
                        r.get("SecondaryMuscle") == null ||
                        r.get("SecondaryMuscle").equals(this.activeMuscleFilter))
                .filter(r -> this.activeEquipmentFilter.isEmpty() ||
                        r.get("Equipment") == null ||
                        r.get("Equipment").equals(this.activeEquipmentFilter))
                .filter(r -> this.searchBar == null ||
                        r.get("Name").toLowerCase().contains(this.searchBar.getText().toLowerCase()))
                .collect(Collectors.toList());
    }
    private Parent createExerciseContainer(int id){
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ExerciseFromDB.fxml"));
            root = loader.load();
            root.setId(Integer.toString(id));

            root.setOnMouseClicked(e -> {
                try { chooseExercise(e); }
                catch (IOException err) { err.printStackTrace(); }
            });

        } catch (IOException err) {err.printStackTrace();}

        return root;
    }
}
