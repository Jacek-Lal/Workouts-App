package org.projects.workoutsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.projects.workoutsapp.entities.WorkoutRecord;

@Data
@AllArgsConstructor
public class WorkoutWrapUpInfo {
	private WorkoutRecord workout;
	private double totalWeight;
	private Long totalSets;
}
