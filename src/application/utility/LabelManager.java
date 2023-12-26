package application.utility;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.stream.Collectors;

public class LabelManager {
    public static void addDataToLabels(List<Label> labels, List<String> data){
        for(int i = 0; i < labels.size(); i++){
            labels.get(i).setText(data.get(i));
        }
    }
    public static List<Label> getLabelsWithId(Parent root){
        return ((Pane)root).getChildren()
                .stream()
                .filter(l-> l.getId() != null)
                .map(l -> (Label)l)
                .collect(Collectors.toList());
    }

}
