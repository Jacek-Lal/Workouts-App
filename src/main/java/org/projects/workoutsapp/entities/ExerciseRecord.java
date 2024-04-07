package org.projects.workoutsapp.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ExerciseRecord")
public class ExerciseRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private WorkoutRecord workout;
	
	@NonNull private String name;
	@NonNull private String description;
	
	@OneToMany(mappedBy = "exercise", cascade = CascadeType.PERSIST)
	private List<SetRecord> sets;
	
	public ExerciseRecord(@NonNull String name, @NonNull String description) {
		this.name = name;
		this.description = description;
		this.sets = new ArrayList<>();
	}
	public void addSet(SetRecord s){
		this.sets.add(s);
	}
	@Override
	public String toString() {
		return "ExerciseRecord{" +
					   "id=" + id +
					   ", name='" + name + '\'' +
					   ", description='" + description + '\'' +
					   ", sets='" + sets + '\'' +
					   '}';
	}
}
