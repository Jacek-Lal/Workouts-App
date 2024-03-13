package org.projects.workoutsapp.controllers.scenes;

import org.projects.workoutsapp.controllers.MainController;
import org.projects.workoutsapp.objects.WorkoutRecord;
import org.projects.workoutsapp.utility.Converter;
import org.projects.workoutsapp.utility.DataLoader;
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
        List<HashMap<String, String>> workouts = DataLoader.loadWorkouts();
        if(workouts.isEmpty()) return;

        Map<String, Integer> frequencyMap = new HashMap<>();
        int workoutsNumber = 0;
        int setsNumber = workouts.size();
        double volume = 0;
        long duration = 0;
        String currDate = "";

        for (HashMap<String, String> map : workouts) {
            String value = map.get("Exercise");
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);

            if(!map.get("StartTime").equals(currDate)){
                currDate = map.get("StartTime");
                workoutsNumber += 1;

                LocalDateTime start = LocalDateTime.parse(map.get("StartTime"), WorkoutRecord.dtf);
                LocalDateTime end = LocalDateTime.parse(map.get("EndTime"), WorkoutRecord.dtf);
                duration += ChronoUnit.SECONDS.between(start, end);
            }

            volume += Double.parseDouble(map.get("Weight")) * Double.parseDouble(map.get("Reps"));
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        showFavoriteExercises(sortedEntries.subList(0, 5));
        List<String> stats = List.of(workoutsNumber +" workouts", setsNumber +" sets", Converter.doubleToString(volume) +" kg", Converter.secondsToHours(duration));
        showStats(stats);
        showPlot();
    }
    private void showFavoriteExercises(List<Map.Entry<String, Integer>> exercises) throws IOException {

        for (Map.Entry<String, Integer> ex : exercises){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/FavExercise.fxml"));
            Pane root = loader.load();
            ((Label)root.getChildren().getFirst()).setText(ex.getKey());
            ((Label)root.getChildren().getLast()).setText(ex.getValue()+" sets");

            favExercisesContainer.getChildren().add(root);
        }
    }
    private void showStats(List<String> stats){
        ObservableList<Node> children = statsGrid.getChildren();

        for(int i = 0; i < children.size(); i++)
            ((Label)((Pane)children.get(i)).getChildren().getLast()).setText(stats.get(i));
    }
    private void showPlot(){}
}
