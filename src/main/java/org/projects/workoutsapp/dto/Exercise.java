package org.projects.workoutsapp.dto;

import lombok.Data;

@Data
public class Exercise {
	private int id;
	private String name;
	private String primaryMuscle;
	private String secondaryMuscle;
	private String equipment;
	private String type;
}
