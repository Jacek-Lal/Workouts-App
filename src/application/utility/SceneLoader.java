package application.utility;

import application.controllers.NewWorkoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneLoader {
    private <T> T loadScene(ActionEvent e, String sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/scenes/" + sceneName + ".fxml"));
        Parent root = loader.load();
        T controller = loader.getController();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        return controller;
    }
    public void loadMain(ActionEvent e) throws IOException {
        this.loadScene(e, "Main");
    }
    public void loadNewWorkoutModal(ActionEvent e) throws IOException {
        loadScene(e, "NewWorkoutModal");
    }
    public void loadWorkoutHistory(ActionEvent e) throws IOException {
        loadScene(e, "WorkoutHistoryTab");
    }
    public void loadExercises(ActionEvent e) throws IOException{
        loadScene(e, "ExercisesTab");
    }
    public NewWorkoutController loadNewWorkout(ActionEvent e) throws IOException {
        return loadScene(e,"NewWorkoutTab");
    }
}
