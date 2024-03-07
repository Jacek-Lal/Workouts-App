package org.projects.workoutsapp.objects;

public class SetRecord {

	private final int id;
	private final double weight;
	private final int reps;
	
	public SetRecord(int id, double weight, int reps) {
		this.id = id;
		this.weight = weight;
		this.reps = reps;
	}
			
	public String getId() {
		return Integer.toString(id);
	}
	public String getWeight() {
		return Double.toString(weight);
	}
	public String getReps() {
		return Integer.toString(reps);
	}
}
