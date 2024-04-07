package org.projects.workoutsapp.entities;

import java.util.ArrayList;

public class _ExerciseRecord_old {
	private final String name;
	private String description;
	private final ArrayList<_SetRecord_old> sets;
	
	public _ExerciseRecord_old(String name){
		this.name = name;
		this.description = "";
		this.sets = new ArrayList<>();
	}
	
	public void addSet(_SetRecord_old set) {
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
	public ArrayList<_SetRecord_old> getSets(){
		return this.sets;
	}
	@Override
	public String toString() {
		return "Name: "+this.name+" Description: "+this.description+"\nSets: "+this.sets;
	}
}
