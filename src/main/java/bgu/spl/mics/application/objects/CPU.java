package src.main.java.bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
	
	private int cores;
	private Collection<DataBatch> data;
	private Cluster cluster;
	private boolean isFree = true;
	
	public CPU(int cores, Cluster cluster) {
		cores = cores;
		data = new ArrayList<DataBatch>();
		cluster = cluster;
	}
	
	public void insertData(DataBatch batch){
		data.add(batch);
		isFree = false;
	}
	
	public Queue<DataBatch> getData(){
		return data;
	}
	
	private DataBatch process() {
		
	}
	
	public void sendProcessedData() {
		
	}
	
	public boolean isFree() {
		return isFree;
	}
	
}
