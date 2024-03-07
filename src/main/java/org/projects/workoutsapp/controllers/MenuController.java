package org.projects.workoutsapp.controllers;

import org.projects.workoutsapp.utility.Converter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class MenuController {

    public MainController mainController;
    @FXML
    private Label activeWorkoutName;
    @FXML
    public Label activeWorkoutTimer;
    @FXML
    public Label activeWorkoutRest;
    @FXML
    private Label labelDuration;
    @FXML
    private Label labelRest;

    public MenuController(MainController main) {
        this.mainController = main;
    }
    public void goToHomeView() throws IOException {
        if(mainController.activeView.equals("HomeTab")) return;

        showActiveWorkout();
        HomeController controller = mainController.sceneLoader.loadScene("HomeTab");
        controller.setup(mainController);
        mainController.activeView = "HomeTab";
    }
    public void goToExercisesView() throws IOException{
        if(mainController.activeView.equals("ExercisesTab")) return;

        showActiveWorkout();
        mainController.sceneLoader.loadScene("ExercisesTab");
        mainController.activeView = "ExercisesTab";
    }
    public void goToWorkoutHistoryView() throws IOException {
        if(mainController.activeView.equals("WorkoutHistoryTab")) return;

        showActiveWorkout();
        mainController.sceneLoader.loadScene("WorkoutHistoryTab");
        mainController.activeView = "WorkoutHistoryTab";
    }
    public void goToWorkoutView(HomeController controller) throws IOException {
        if(mainController.activeWorkout != null) {
            goToActiveWorkout();
        }
        else
            mainController.sceneLoader.loadModal("/fxml/components/NewWorkoutModal.fxml", controller);
    }
    public void goToActiveWorkout(){
        if(mainController.activeWorkout == null || mainController.activeView.equals("NewWorkoutTab")) return;

        mainController.sceneContainer.getChildren().clear();
        mainController.sceneContainer.getChildren().add(mainController.activeWorkout.scene);
        mainController.activeView = "NewWorkoutTab";
        hideActiveWorkout();
    }
    private void showActiveWorkout(){

        if(mainController.activeWorkout == null) return;

        this.activeWorkoutName.setText(mainController.activeWorkout.workout.getName());
        this.labelDuration.setText("Duration");
        this.activeWorkoutTimer.setText(Converter.secondsToTime(mainController.activeWorkout.duration));
        this.labelRest.setText("Rest");
        this.activeWorkoutRest.setText(Converter.secondsToTime(mainController.activeWorkout.rest));
    }
    public void showTimer(Label label, String time){
        if(mainController.activeWorkout != null) label.setText(time);
    }
    public void hideActiveWorkout(){
        this.activeWorkoutName.setText("");
        this.labelDuration.setText("");
        this.activeWorkoutTimer.setText("");
        this.labelRest.setText("");
        this.activeWorkoutRest.setText("");

    }
}
