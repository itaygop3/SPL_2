package src.main.java.bgu.spl.mics.application.objects;

import src.main.java.bgu.spl.mics.application.services.*;
import java.util.LinkedList;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Queue<DataBatch> data=new LinkedList<>();//data the cpu works on-needs to be processed.
    private Cluster cluster = Cluster.getInstance();
    private AtomicInteger currentTicksNumber=new AtomicInteger(0);
    private CPUService srvc;
    private int fullWorktime = 0;//this is the ticks number it takes to finish processing all the batches currently in the queue(or being processed)
    
    public CPU(int _cores){
        cores=_cores;
        srvc=new CPUService("", this);
    }
    
    public int getFullWorkTime() {
    	return fullWorktime;
    }

    public CPUService getService() {
    	return srvc;
    }

    public AtomicInteger getCurrentTicksNumber(){
        return currentTicksNumber;
    }
    
    public void insertData(DataBatch d) {
    	data.add(d);
    	fullWorktime+=timeToWait(d);
    }
    
    private int timeToWait(DataBatch d) {
    	Data.Type type = d.getData().getType();
        int time = 32/cores;
        switch(type){
            case Images:
                return time*4;
            case Text:
                return time*2;
            case Tabular:
                return time;
        }
        return time;
    }

    private int timeToWait(){
        Data.Type type = data.peek().getData().getType();
        int time = 32/cores;
        switch(type){
            case Images:
                return time*4;
            case Text:
                return time*2;
            case Tabular:
                return time;
        }
        return time;
    }
    
    public void tick() {
    	if(fullWorktime>0)
    		fullWorktime--;
    }

    public void process(){
    	if(data.isEmpty()) 
    		return;
        
        DataBatch dataBatch=data.peek();
        dataBatch.setProcessedAt(dataBatch.ProcessedAt()+1);//process when gets time tick
        int t=timeToWait();
        if (dataBatch.ProcessedAt()>=t) {//checks if t ticks have passed since start of process.
            data.remove();
        	dataBatch.setProcessedAt(currentTicksNumber.get());
        	cluster.prioritize(this);
            cluster.sendToTrain(dataBatch); //sends the processed data to the cluster
        	System.out.println("cpu finished with the batch and is sending back to GPU");
            cluster.updateCPUTicks(t);
        }

    }
}
