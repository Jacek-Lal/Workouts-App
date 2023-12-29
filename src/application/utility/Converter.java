package application.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Converter {
    public static List<HashMap<String, String>> toHashmapList(List<List<String>> data, List<String> keys){

        List<HashMap<String, String>> convertedList = new ArrayList<>();

        for(List<String> record : data) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < record.size(); i++) {
                map.put(keys.get(i), record.get(i));
            }
            convertedList.add(map);
        }

        return convertedList;
    }
}
