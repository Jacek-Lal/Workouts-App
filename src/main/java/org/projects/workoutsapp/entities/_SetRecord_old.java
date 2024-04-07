package org.projects.workoutsapp.entities;

public class _SetRecord_old {

	private final int number;
	private final double weight;
	private final int reps;

	public _SetRecord_old(int number, double weight, int reps) {
		this.number = number;
		this.weight = weight;
		this.reps = reps;
	}

	public String getNumber() {
		return Integer.toString(number);
	}
	public String getWeight() {
		return Double.toString(weight);
	}
	public String getReps() {
		return Integer.toString(reps);
	}
}
