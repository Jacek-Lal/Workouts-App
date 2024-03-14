package org.projects.workoutsapp.utility;

import org.projects.workoutsapp.objects.ExerciseRecord;
import org.projects.workoutsapp.objects.SetRecord;
import org.projects.workoutsapp.objects.WorkoutRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBConnector {
    private static final List<String> EXERCISE_KEYS = List.of("Id","Name","PrimaryMuscle","SecondaryMuscle","Equipment","Type");
    private static final List<String> WORKOUT_KEYS = List.of("Name","StartTime","EndTime","Exercise","Description","SetNumber","Weight","Reps");
    private static final String URL = "jdbc:sqlite:src/main/resources/sql/data.sqlite";
    private static List<HashMap<String, String>> parseQuery(String query, List<String> keys) {

        List<List<String>> data = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(URL)){
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
        String query = buildQuery(WORKOUT_KEYS, "Workouts");
        return parseQuery(query, WORKOUT_KEYS);
    }
    public static List<HashMap<String,String>> loadExerciseRecords(String exerciseName){
        String query = buildQuery(WORKOUT_KEYS, "Workouts");
        query += " WHERE Exercise= '"+exerciseName+"'";

        return parseQuery(query, WORKOUT_KEYS);
    }
    public static List<HashMap<String, String>> loadExercises(){
        String query = buildQuery(EXERCISE_KEYS, "Exercises");
        return parseQuery(query, EXERCISE_KEYS);
    }
    public static void saveWorkout(WorkoutRecord workout){

        try(Connection connection = DriverManager.getConnection(URL)){
            for (ExerciseRecord exercise : workout.getExercises()){
                for (SetRecord set : exercise.getSets()){
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO WORKOUTS VALUES (?,?,?,?,?,?,?,?)");

                    List<String> data = List.of(workout.getName(), workout.getStartTime(), workout.getEndTime(), exercise.getName(), exercise.getDescription(), set.getId(), set.getWeight(), set.getReps());
                    for (int i = 1; i < data.size() + 1; i++)
                        statement.setString(i, data.get(i-1));

                    statement.executeUpdate();
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static String buildQuery(List<String> keys, String table){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");

        keys.forEach(k -> {
            sb.append(k).append(",");
        });

        sb.deleteCharAt(sb.length()-1);
        sb.append(" FROM ");
        sb.append(table);

        return sb.toString();
    }
}
