package application.utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataLoader {
    private static List<HashMap<String, String>> loadData(String table, List<String> keys) {
        String url = "jdbc:sqlite:src/application/data.sqlite";
        String query = String.format("SELECT * FROM %s", table);
        List<List<String>> data = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url)){
            Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery(query);

            while(res.next()) {
                List<String> row = new ArrayList<>();

                for(String key : keys)
                    row.add(res.getString(key));

                data.add(row);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return Converter.toHashmapList(data, keys);
    }

    public static List<HashMap<String, String>> loadWorkouts(){
        List<String> keys = List.of("Name","StartTime","EndTime","Exercise","Description","SetNumber","Weight","Reps");

        return loadData("Workouts", keys);
    }
    public static List<HashMap<String, String>> loadExercises(){
        List<String> keys = List.of("Id","Name","Primary Muscle","Secondary Muscle","Equipment","Type");

        return loadData("Exercises", keys);
    }
}
