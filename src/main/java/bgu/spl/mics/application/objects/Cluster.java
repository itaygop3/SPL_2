package src.main.java.bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

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
	private List<CPU> cpus = Collections.synchronizedList(new ArrayList<CPU>());
	//A list of all GPUs
	private List<GPU> gpus = Collections.synchronizedList(new LinkedList<>());
	//An object that holds stats
	private Statistics stats = new Statistics();

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return c;
	}
	
	public void sendToProcess(List<DataBatch> data, GPU sender) {
		System.out.println(data.size()+" batch was sent to process from "+sender.getName());
		int size = data.size();
		for(int i = 0 ; i < size ; i++) {
			sendToProcess(data.remove(0), sender);
		}
	}
	
	public void addToList(GPU gpu) {
		gpus.add(gpu);
	}
	
	public void addToList(CPU cpu) {
		cpus.add(cpu);
	}
	
	public void prioritize(CPU _cpu) {
		//While _cpu isn't first and while the predecessor cpu is less free then _cpu swtich between them
		while(cpus.indexOf(_cpu)>0&&cpus.get(cpus.indexOf(_cpu)-1).getFullWorkTime()>_cpu.getFullWorkTime()) {
			prioritizeCPU(_cpu);
		}
	}
	
	private void prioritizeCPU(CPU cpu) {
		int index = cpus.indexOf(cpu);
		if(index > 0) {
			cpus.remove(cpu);
			cpus.add(index-1,cpu);
		}
	}
	
	public void sendToProcess(DataBatch data, GPU sender) {
		map.putIfAbsent(data.getData(), sender);
		CPU next = cpus.get(0);
		System.out.println("sending data");
		next.insertData(data);
		System.out.println("sent to cpu");
		cpus.remove(next);
		System.out.println(cpus.size());
		cpus.add(next);
		System.out.println(cpus.size());
	}
	
	//There is always a free spot because a batch was sent to process only if gpu has an open spot
	public void sendToTrain(DataBatch db) {
		GPU sender = map.get(db.getData());
		sender.reciveProcessedData(db);
	}
	
	public void updateGPUTicks(int amount) {
		int current;
		do {
			current = stats.gpuTimeUsed.get();
		}while(!stats.gpuTimeUsed.compareAndSet(current, current+amount));
	}
	public void updateCPUTicks(int amount) {
		int current;
		do {
			current = stats.cpuTimeUsed.get();
		}while(!stats.cpuTimeUsed.compareAndSet(current, current+amount));
	}
	
	public void updateProcessedBatches() {
		int current;
		do {
			current = stats.processedBatches.get();
		}while(!stats.processedBatches.compareAndSet(current, current+1));
	}
	
	public void addToList(Model m) {
		if(m.getStatus() == Model.Status.Trained)
			stats.trainedModelList.add(m.getName());
		
	}
	
	
	
	private class Statistics{
		//The names of all fully trained models
		public Set<String> trainedModelList = new ConcurrentSkipListSet<>();
		//The amount of data batches processed so far
		public AtomicInteger processedBatches = new AtomicInteger(0);
		//The number of CPU time units used
		public AtomicInteger cpuTimeUsed = new AtomicInteger(0);
		//The number of GPU time units used
		public AtomicInteger gpuTimeUsed = new AtomicInteger(0);
	}
	
}
