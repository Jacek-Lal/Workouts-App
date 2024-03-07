package org.projects.workoutsapp.objects;

import java.util.ArrayList;

public class ExerciseRecord {
	private final String name;
	private String description;
	private final ArrayList<SetRecord> sets;
	
	public ExerciseRecord(String name){
		this.name = name;
		this.description = "";
		this.sets = new ArrayList<>();
	}
	
	public void addSet(SetRecord set) {
		this.sets.add(set);
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return this.name;
	}
	public String getDescription() {
		return this.description;
	}
	public ArrayList<SetRecord> getSets(){
		return this.sets;
	}
	@Override
	public String toString() {
		return "Name: "+this.name+" Description: "+this.description+"\nSets: "+this.sets;
	}
}
