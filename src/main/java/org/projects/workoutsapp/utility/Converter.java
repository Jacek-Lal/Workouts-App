package org.projects.workoutsapp.utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Converter {
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
    public static String secondsToHours(long s){
        int hours = (int) (s / 3600);
        int minutes = (int) ((s/60) - hours*60);

        return hours + " hours " + minutes + " minutes ";
    }
    public static String doubleToString(double number){
        DecimalFormat formatter = new DecimalFormat("#,##0.0");
        String formattedString = formatter.format(number);

        formattedString = formattedString.replace(",", ".");
        return formattedString;
    }
    public static String secondsToTime(int s){
        int minutes = s / 60;
        int seconds = s % 60;

        StringBuilder formattedTime = new StringBuilder();

        if (minutes < 10) formattedTime.append("0");
        formattedTime.append(minutes);
        formattedTime.append(":");

        if (seconds < 10) formattedTime.append("0");
        formattedTime.append(seconds);

        return formattedTime.toString();
    }
    public static int timeToSeconds(String time){
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);

        return minutes * 60 + seconds;
    }
}
