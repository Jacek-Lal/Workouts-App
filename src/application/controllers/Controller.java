package application.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public abstract class Controller<T> {
	protected String sceneName;
	protected Parent root;
	protected Stage stage;
	protected Scene scene;
	protected static final String COMPONENTS_PATH = "/application/components/";
	protected static final String SCENES_PATH = "/application/scenes/";

	public void goToMainView(ActionEvent e) throws IOException {
		this.sceneName = "Main";
		this.loadScene(e);
	}
	
	protected T loadScene(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(SCENES_PATH+this.sceneName+".fxml"));
		root = loader.load();
		T controller = loader.getController();
				
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		return controller;
	}
	
	protected List<List<String>> loadCsv(String file) {
		List<List<String>> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        String[] record = line.split(",")[0].split(";");
		        records.add(Arrays.asList(record));
		    }
		}
		catch (Exception e){
			e.getSuppressed();
			File file1 = new File(file);
			System.out.println(file1 + " " + file1.exists());
		}
		return records;
	}
	
	protected List<HashMap<String, String>> convertCsvToHashmap(List<List<String>> csv){
		List<String> keys = csv.getFirst();
		csv.removeFirst();
		List<HashMap<String, String>> convertedList = new ArrayList<>();
		
		for(List<String> record : csv) {
			HashMap<String, String> map = new HashMap<>();
			for (int i = 0; i < record.size(); i++) {
				map.put(keys.get(i), record.get(i));
			}
			convertedList.add(map);
		}
		
		return convertedList;
	}
	
	protected List<HashMap<String, String>> loadExercises() {
		List<List<String>> csv = loadCsv("src/application/Exercises.csv");

        return convertCsvToHashmap(csv);
	}
	
	protected List<HashMap<String, String>> loadWorkouts() {
		List<List<String>> csv = loadCsv("src/application/Workouts.csv");

        return convertCsvToHashmap(csv);
	}
	
	protected void scrollDown(ScrollPane scrollPane) {
		scrollPane.applyCss();
		scrollPane.layout();
		scrollPane.setVvalue(scrollPane.getVmax());
	}
}
