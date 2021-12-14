package src.main.java.bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import src.main.java.bgu.spl.mics.application.objects.*;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	//This is the only instance of the cluster
	private static Cluster c = new Cluster();
	
	//We need this map to know to which GPU to send the processed data
	private ConcurrentHashMap<Data, GPU> map = new ConcurrentHashMap<>();
	//A list of all CPUs
	private List<CPU> cpus;
	//A list of all GPUs
	private List<GPU> gpus;
	//An object that holds stats
	private Statistics stats;

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return c;
	}
	
	private class Statistics{
		//The names of all fully trained models
		public Set<String> trainedModelNames = new HashSet<>();
		//The amount of data batches processed so far
		public int processedBatches = 0;
		//The number of CPU time units used
		public int cpuTimeUsed = 0;
		//The number of GPU time units used
		public int gpuTimeUsed = 0;
	}
	
}
