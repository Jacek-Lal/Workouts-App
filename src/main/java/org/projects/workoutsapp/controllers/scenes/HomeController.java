package org.projects.workoutsapp.controllers.scenes;

import javafx.scene.Parent;
import org.projects.workoutsapp.controllers.MainController;
import org.projects.workoutsapp.dto.FavoriteExercise;
import org.projects.workoutsapp.dto.HomeScreenStats;
import org.projects.workoutsapp.entities._WorkoutRecord_old;
import org.projects.workoutsapp.persistence.DatabaseClient;
import org.projects.workoutsapp.utility.Converter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.projects.workoutsapp.utility.LabelManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HomeController{
    private MainController mainController;
    @FXML
    private GridPane statsGrid;
    @FXML
    private VBox favExercisesContainer;
    @FXML
    private TextField workoutName;

    public void setup(MainController mainController) throws IOException {
        this.mainController = mainController;
        loadHomeScreenStats();
    }
    public void goToWorkoutView() throws IOException {
        mainController.menuController.goToWorkoutView(this);
    }
    public void modalCancel(ActionEvent e){
        Stage modal = (Stage) ((Node) e.getSource()).getScene().getWindow();
        modal.close();
    }
    public void createNewWorkout(ActionEvent e) throws IOException {
        modalCancel(e);

        NewWorkoutController controller = mainController.sceneLoader.loadScene("NewWorkoutTab");
        String name = workoutName.getText().isEmpty() ? "New Workout" : workoutName.getText();

        controller.setup(mainController);
        controller.setName(name);

        mainController.activeWorkout = controller;
        mainController.activeView = "NewWorkoutTab";
    }
    private void loadHomeScreenStats() throws IOException {
        showFavoriteExercises();
        showStats();
        showPlot();
    }
//    private void loadHomeScreenStats() throws IOException {
//        List<HashMap<String, String>> workouts = DatabaseClient.getWorkouts();
//        if(workouts.isEmpty()) return;
//
//        Map<String, Integer> frequencyMap = new HashMap<>();
//        int workoutsNumber = 0;
//        int setsNumber = workouts.size();
//        double volume = 0;
//        long duration = 0;
//        String currDate = "";
//
//        for (HashMap<String, String> map : workouts) {
//            String value = map.get("Exercise");
//            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
//
//            if(!map.get("StartTime").equals(currDate)){
//                currDate = map.get("StartTime");
//                workoutsNumber += 1;
//
//                LocalDateTime start = LocalDateTime.parse(map.get("StartTime"), _WorkoutRecord_old.dtf);
//                LocalDateTime end = LocalDateTime.parse(map.get("EndTime"), _WorkoutRecord_old.dtf);
//                duration += ChronoUnit.SECONDS.between(start, end);
//            }
//
//            volume += Double.parseDouble(map.get("Weight")) * Double.parseDouble(map.get("Reps"));
//        }
//
//        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
//        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
//
//        showFavoriteExercises(sortedEntries.subList(0, 1));
//        List<String> stats = List.of(workoutsNumber +" workouts", setsNumber +" sets", Converter.doubleToString(volume) +" kg", Converter.secondsToHours(duration));
//        showStats(stats);
//        showPlot();
//    }
    private void showFavoriteExercises() throws IOException {
        List<FavoriteExercise> favExercises = DatabaseClient.getTopExercises(5);
        
        for (FavoriteExercise ex : favExercises){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/FavExercise.fxml"));
            Parent root = loader.load();
            
            List<Label> labels = new ArrayList<>();
            LabelManager.getLabelsWithId(root, labels);
            LabelManager.addData(labels, List.of(ex.getName(), String.valueOf(ex.getTotalSets())));

            favExercisesContainer.getChildren().add(root);
        }
    }
    private void showStats(){
        HomeScreenStats stats = DatabaseClient.getStats();
        List<Label> labels = new ArrayList<>();
        
        LabelManager.getLabelsWithId(statsGrid, labels);
        LabelManager.addData(labels, List.of(stats.getWorkouts()+" workouts", stats.getSets()+" sets", stats.getVolume()+" kg",""));
        
    }
    private void showPlot(){}
}
