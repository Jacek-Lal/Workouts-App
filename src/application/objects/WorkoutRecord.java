package application.objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WorkoutRecord {
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");;
	
	private String name;
	private final String date;
	private final ArrayList<ExerciseRecord> exercises;

	public WorkoutRecord() {
		this.name = "";
		this.date = LocalDateTime.now().format(dtf);
		this.exercises = new ArrayList<>();
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	public String getDate() {
		return this.date;
	}
	public void addExercise(ExerciseRecord exercise){
		this.exercises.add(exercise);
	}
	public ArrayList<ExerciseRecord> getExercises(){
		return this.exercises;
	}

}
