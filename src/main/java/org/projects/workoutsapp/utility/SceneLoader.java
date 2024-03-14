package org.projects.workoutsapp.utility;

import javafx.scene.control.Label;
import org.projects.workoutsapp.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneLoader {
    private final MainController main;
    public SceneLoader(MainController main){
        this.main = main;
    }
    public <T> T loadScene(String sceneName) throws IOException {
        main.sceneContainer.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scenes/" + sceneName + ".fxml"));
        Parent root = loader.load();
        T controller = loader.getController();
        main.sceneContainer.getChildren().add(root);

        return controller;
    }
    public void loadModal(String path, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loader.setController(controller);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(main.stage);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public void loadMenu(AnchorPane container) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/Menu.fxml"));
        loader.setController(this.main.menuController);

        Pane root = loader.load();
        container.getChildren().add(root);
    }
    public static Parent createComponent(String path, List<String> data) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(path));
        Parent component = loader.load();

        List<Label> labels = new ArrayList<>();
        LabelManager.getLabelsWithId(component, labels);
        LabelManager.addData(labels, data);

        return component;
    }
}
