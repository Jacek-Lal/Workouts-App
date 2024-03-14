package org.projects.workoutsapp.utility;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

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
    public static void getLabelsWithId(Parent root, List<Label> list){
        ObservableList<Node> children =  root.getChildrenUnmodifiable();
        for(Node child : children){
            if(child instanceof Parent) getLabelsWithId((Parent)child, list);
            if(child instanceof Label && child.getId() != null) list.add((Label)child);
        }
    }
}
