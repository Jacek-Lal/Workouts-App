package org.projects.workoutsapp.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SetRecord")
public class SetRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private ExerciseRecord exercise;
	
	@NonNull private int number;
	@NonNull private double weight;
	@NonNull private int reps;
	
	public SetRecord(@NonNull Integer number, @NonNull Double weight, @NonNull Integer reps) {
		this.number = number;
		this.weight = weight;
		this.reps = reps;
	}
	
	@Override
	public String toString() {
		return "SetRecord{" +
					   "id=" + id +
					   ", number=" + number +
					   ", weight=" + weight +
					   ", reps=" + reps +
					   '}';
	}
}
