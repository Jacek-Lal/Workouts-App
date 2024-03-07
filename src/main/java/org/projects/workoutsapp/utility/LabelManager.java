package org.projects.workoutsapp.utility;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LabelManager {
    public static void addData(List<Label> labels, List<String> data){
        for(int i = 0; i < labels.size(); i++){
            labels.get(i).setText(data.get(i));
        }
    }
    public static List<String> getData(List<Label> labels){
        List<String> data = new ArrayList<>();

        for (Label label : labels) {
            data.add(label.getText());
        }
        return data;
    }
    public static List<Label> getLabelsWithId(Parent root){
        return ((Pane)root).getChildren()
                .stream()
                .filter(l-> l.getId() != null)
                .map(l -> (Label)l)
                .collect(Collectors.toList());
    }
}
