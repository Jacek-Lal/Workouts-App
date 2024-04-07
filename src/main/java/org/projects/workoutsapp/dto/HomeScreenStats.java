package org.projects.workoutsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeScreenStats {
	private Long sets;
	private Double volume;
	private Long workouts;
}
