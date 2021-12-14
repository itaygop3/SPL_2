package src.main.java.bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
	
	private Data data;
	private int start_index;
	private int timeTicks = 0;
	
	public DataBatch(Data data, int startIndex) {
		this.data = data;
		start_index = startIndex;
	}
	
	public Data getData() {
		return data;
	}
	
	public int getIndex() {
		return start_index;
	}
	public int getTimeTicks() {
		return timeTicks;
	}
	
	public void incrementTimeTicks() {
		timeTicks++;
	}
}
