package application.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CsvLoader {
    private static List<List<String>> loadCsv(String file) {
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
    public static List<HashMap<String, String>> loadExercises() {
        return Converter.toHashmapList(loadCsv("src/application/Exercises.csv"));
    }
    public static List<HashMap<String, String>> loadWorkouts() {
        return Converter.toHashmapList(loadCsv("src/application/Workouts.csv"));

    }
}
