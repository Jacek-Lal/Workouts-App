package application.objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WorkoutRecord {
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");;  
	
	private String name;
	private String date;
	private ArrayList<ExerciseRecord> exercises;
	
	private float volume;
	private int sets;
	private int duration;

	
	public WorkoutRecord() {
		this.name = "";
		this.date = LocalDateTime.now().format(dtf);
		this.exercises = new ArrayList<ExerciseRecord>();
		this.volume = 0;
		this.sets = 0;
		this.duration = 0;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	public String getDate() {
		return this.date.toString();
	}
	public void addExercise(ExerciseRecord exercise){
		this.exercises.add(exercise);
	}
	public ArrayList<ExerciseRecord> getExercises(){
		return this.exercises;
	}
	public String getTotalVolume() {
		// do some calculations
		return Float.toString(volume);
	}
	public String getTotalSets() {
		//do some calculations
		return Integer.toString(sets);
	}
	public String getDuration() {
		return Integer.toString(duration);
	}
}
