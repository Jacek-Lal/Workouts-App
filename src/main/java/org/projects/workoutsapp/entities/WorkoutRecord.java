package org.projects.workoutsapp.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "WorkoutRecord")
public class WorkoutRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NonNull private String name;
	@NonNull private LocalDateTime startTime;
	@NonNull private LocalDateTime endTime;
	
	@OneToMany(mappedBy = "workout", cascade = CascadeType.PERSIST)
	private List<ExerciseRecord> exercises;
	
	public WorkoutRecord(){
		this.exercises = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return "WorkoutRecord{" +
					   "id=" + id +
					   ", name='" + name + '\'' +
					   ", startTime=" + startTime +
					   ", endTime=" + endTime +
					   '}';
	}
	public String getDuration(){
		Duration duration = Duration.between(startTime, endTime);
		StringBuilder formattedTime = new StringBuilder();
		//duration = duration.plusSeconds(1);
		
		if(duration.toHours() > 0) formattedTime.append(duration.toHours()).append("h ");
		if(duration.toMinutes() > 0) formattedTime.append(duration.toMinutes()%60).append("m ");
		formattedTime.append(duration.getSeconds()%60).append("s");
		
		return formattedTime.toString();
	}
	public void addExercise(ExerciseRecord ex){
		this.exercises.add(ex);
	}
}
