package src.main.java.bgu.spl.mics.application.objects;

import java.util.*;
import src.main.java.bgu.spl.mics.application.services.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
	/**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}
    
    private int ticksNum = 0;
    private Type type;
    private String name;
    private Model model = null;
    private Cluster cluster = Cluster.getInstance();
    private GPUService MS;
    private Queue<DataBatch> VRAM = new LinkedList<>();
    private int VRAMCapacity;
    private List<DataBatch> data = null;
    private Object lock = new Object();
    
    
    public GPU(Type type, String _name) {
    	this.type = type;
    	this.name = _name;
    	switch(type) {
    	case GTX1080:
    		VRAMCapacity = 8;
    		break;
    	case RTX2080:
    		VRAMCapacity = 16;
    		break;
    	case RTX3090:
    		VRAMCapacity = 32;
    	}
    	MS = new GPUService(name, this);
    }
    
    public GPUService getGPUService() {
    	return MS;
    }
    
    public synchronized void updateTime() {
    	ticksNum++;
    	this.notifyAll();
    }
    
    public int getVRAMSize() {
    	return VRAM.size();
    }
    
    public int getVRAMCapacity() {
    	return VRAMCapacity;
    }
    
    public List<DataBatch> getData(){
    	return data;
    }
    
    public String getName() {
    	return name;
    }
    
    public boolean isFree() {
    	return model == null;
    }
    
    public boolean isFull() {
    	return VRAM.size() == VRAMCapacity;
    }
    
    public void modelIsFinished() {
    	model = null;
    }
    
    private List<DataBatch> devideToBatches(Data data){
    	data = model.getData();
    	List<DataBatch> s = new LinkedList<DataBatch>();
    	int dataSize = data.getSize();
    	for(int i=0;i<dataSize;i+=1000) {
    		DataBatch next = new DataBatch(data,i);
    		s.add(next);
    	}
    	return s;
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	data=devideToBatches(model.getData());
    }
    
    /**
     * This method sends {@code num} data batches to the cluster
     * @param num The number of data batches to send to the cluster
     */
    public void sendData(int num) {
    	if(num == 0 | data.isEmpty()) {
    		System.out.println(name+" didnt send data");
    		return;
    	}
    	if(num == 1) {
    		cluster.sendToProcess(data.remove(0), this);
    		System.out.println(name+"sent one batch to process");
    		return;
    	}
    	List<DataBatch> delivery = new LinkedList<>();
    	for(int i = 0 ; i < num & !data.isEmpty() ; i++) {
    		delivery.add(data.remove(data.size()-1));
    	}
    	cluster.sendToProcess(delivery, this);
    }
    
    public void sendFirstChunk() {
    	sendData(VRAMCapacity);
    }
    
    public Object getLock() {
    	return lock;
    }
    
    
    /**
     * @param db the processed data batch to be trained
     * @return false if {@code VRAM} is full
     */
    public boolean reciveProcessedData(DataBatch db) {
    	if(isFull()) {
    		System.out.println("is full");
    		return false;
    	}
    	System.out.println("adding to VRAM");
    	VRAM.add(db);
    	System.out.println("added to VRAM");
    	synchronized(lock) {
    		lock.notifyAll();
    	}
    	return true;
    }
    
    /**
     * @return number of ticks needed for a batch to be trained in this {@code GPU}
     */
    private int numOfTicks() {
    	switch(type) {
    	case GTX1080:
    		return 4;
    	case RTX2080:
    		return 2;
    	case RTX3090:
    		return 1;
    	}
    	return 0;
    }
    
    
    public void trainProcessedData() {
    	if(VRAM.size()<1)
    		return;
    	//If this is the first batch being processed update the status of the model
    	if(model.getStatus() == Model.Status.PreTrained)
    		model.updateStatus();
    	int curTime = VRAM.remove().ProcessedAt();
    	/*
    	 * Wait while the number of ticks from the time(number of ticks) the batch was sent from the cpu
    	 * until current time is enough for the GPU to finish training the model
    	 * (because the batch might not really be sent instantly and we don't want to lose ticks) 
    	 */
	    while(ticksNum-curTime <numOfTicks()){
	    	synchronized(this) {
	    		try {
	    			this.wait();
	    		} catch (InterruptedException e) {}
	    	}
	    }
	    cluster.updateGPUTicks(numOfTicks());
    	model.getData().incrementData();
    	sendData(1);
    	checkProgress();
    }
    
    /**
     * This method checks if {@code model} has complete its training
     */
    private void checkProgress() {
    	if(model.getData().getProcessed() == model.getData().getSize()) {
    		model.updateStatus();
    		cluster.addToList(model);
    	}
    }
    
    public void testModel(Model m) {
    	Integer rendom = (int)(Math.random()*10);
    	if(m.getStudent().getStatus() == Student.Degree.PhD) {
    		if(rendom<8)
    			m.setResult(Model.Result.GOOD);
    		else m.setResult(Model.Result.BAD);
    	}
    	else {
    		if(rendom<6)
    			m.setResult(Model.Result.GOOD);
    		else m.setResult(Model.Result.BAD);
    	}
    }
    
}
