package org.projects.workoutsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class FavoriteExercise {
	String name;
	Long totalSets;
}
