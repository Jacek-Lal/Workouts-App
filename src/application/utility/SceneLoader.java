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
    private <T> T loadScene(Stage stage, String sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/scenes/" + sceneName + ".fxml"));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();

        return controller;
    }
    public void loadMain(ActionEvent e) throws IOException {
        this.loadScene((Stage)((Node)e.getSource()).getScene().getWindow(), "Main");
    }
    public void loadNewWorkoutModal(ActionEvent e) throws IOException {
        loadScene((Stage)((Node)e.getSource()).getScene().getWindow(), "NewWorkoutModal");
    }
    public void loadWorkoutHistory(ActionEvent e) throws IOException {
        loadScene((Stage)((Node)e.getSource()).getScene().getWindow(), "WorkoutHistoryTab");
    }
    public void loadExercises(ActionEvent e) throws IOException{
        loadScene((Stage)((Node)e.getSource()).getScene().getWindow(), "ExercisesTab");
    }
    public NewWorkoutController loadNewWorkout(ActionEvent e) throws IOException {
        return loadScene((Stage)((Node)e.getSource()).getScene().getWindow(),"NewWorkoutTab");
    }
}
