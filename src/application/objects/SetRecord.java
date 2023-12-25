package application.objects;

public class SetRecord {

	private int id;
	private double weight;
	private int reps;
	
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
