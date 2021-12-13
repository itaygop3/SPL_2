package bgu.spl.mics.application.objects;

import java.util.Stack;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    
    private String name;
    private Model model;
    private Cluster cluster;
    private GPUService MS;
    private Data[] VRAM;
    private int VRAMSize = 0;
    private int next = 0;
    private Stack<DataBatch> batches;
    
    public GPU(Type type, Cluster cluster, String name, GPUService gpuSvc) {
    	type = type;
    	cluster = cluster;
    	model = null;
    	MS = gpuSvc; 
    	if(type.equals(Type.RTX3090))
    		VRAM = new Data[32];
    	else if(type.equals(Type.RTX2080))
    		VRAM = new Data[16];
    	else VRAM = new Data[8];
    	batches=devideToBatches(model.getData());
    }
    
    public int getNext() {
    	return next;
    }
    
    public int getVRAMSize() {
    	return VRAMSize;
    }
    
    public int getVRAMCapacity() {
    	return VRAM.length;
    }
    
    public Stack<DataBatch> getBatches(){
    	return batches;
    }
    
    public String getName() {
    	return name;
    }
    
    public boolean isFree() {
    	return model == null;
    }
    
    public boolean isFull() {
    	
    }
    
    private Stack<DataBatch> devideToBatches(Data data){
    	data = model.getData();
    	Stack<DataBatch> s = new Stack<DataBatch>();
    	int dataSize = data.getSize();
    	for(int i=0;i<dataSize;i+=1000) {
    		DataBatch next = new DataBatch(data,i);
    		s.push(next);
    	}
    	return s;
    }
    
    public void setModel(Model model) {
    	model = model;
    }
    
    public void sendData() {
    	
    }
    
    public void reciveProcessedData(DataBatch db) {
    	
    }
    
    public DataBatch trainProcessedData() {
    	
    }
    
    
    
    
}
