package src.main.java.bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
	
	private Data data;
	private int start_index;
	private int processedAt = 0;//The ticks number when this batch was fully processed
	
	public DataBatch(Data data, int startIndex) {
		this.data = data;
		start_index = startIndex;
	}
	
	public int ProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(int processedAt) {
		this.processedAt = processedAt;
	}

	public Data getData() {
		return data;
	}
	
	public int getIndex() {
		return start_index;
	}
//	public int getTimeTicks() {
//		return timeTicks;
//	}
//	
//	public void incrementTimeTicks() {
//		timeTicks++;
//	}
}
