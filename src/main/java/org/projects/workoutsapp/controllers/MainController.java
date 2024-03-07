package org.projects.workoutsapp.controllers;

import org.projects.workoutsapp.utility.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
	public Stage stage;
	public MenuController menuController;
	public SceneLoader sceneLoader;
	public String activeView;
	public NewWorkoutController activeWorkout = null;
	@FXML
	private AnchorPane menuContainer;
	@FXML
	public AnchorPane sceneContainer;

	public void setup(Stage stage) throws IOException {
		this.stage = stage;
		this.activeView = "";

		this.sceneLoader = new SceneLoader(this);
		this.menuController = new MenuController(this);

		this.sceneLoader.loadMenu(menuContainer);
		this.menuController.goToHomeView();
	}
}
