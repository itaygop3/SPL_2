package src.main.java.bgu.spl.mics.application.services;

import java.util.*;
import src.main.java.bgu.spl.mics.*;
import src.main.java.bgu.spl.mics.application.messages.*;
import src.main.java.bgu.spl.mics.application.objects.*;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private Cluster cluster = Cluster.getInstance();
	private GPU gpu;
	private Queue<Event<Model>> events = new LinkedList<>();
	private Thread assistant = new Thread(new Assistant());
	
	//All the callbacks this service will use:
	
	private Callback<TrainModelEvent> trainModelCallback = (event->{
		synchronized(events) {
			events.add(event);
			events.notify();
		}
	});
	private Callback<TestModelEvent> testModelCallback = (event->{
		synchronized(events) {
			events.add(event);
			events.notify();
		}
	});
	private Callback<TickBroadcast> tickCallback = (tick -> gpu.updateTime());
	
	private Callback<LastTickBroadcast> lastTickCallback = (tick -> {
		System.out.println("got last tick");
		this.terminate();
		synchronized (gpu.getLock()) {
			gpu.getLock().notifyAll();
		}
		synchronized(events) {
			events.notifyAll();
		}
	});
	 
	public GPUService(String name, GPU _gpu) {
	        super(name+"Svc");
	        gpu = _gpu;
	        initialize();
    }
	
	private void trainNewModel(TrainModelEvent event) {
		System.out.println("start training "+event.getModel().getName());
		gpu.setModel(event.getModel());
		gpu.sendFirstChunk();
		System.out.println("sent first chunk of: "+event.getModel().getName());
		while(event.getModel().getStatus() != Model.Status.Trained&!terminated) {
			if(gpu.getVRAMSize() == 0)
				waitForProcessedData();
			if(!terminated)
				gpu.trainProcessedData();
		}
		if(!terminated) {
			complete(event, event.getModel());
			gpu.modelIsFinished();
		}
	}
	
	private void waitForProcessedData() {
		while(gpu.getVRAMSize() == 0&!terminated) {
			synchronized(gpu.getLock()) {
				try {
					System.out.println(gpu.getName()+" is waiting for processed data");
					gpu.getLock().wait();
				}catch(InterruptedException e) {}
			}
		}
		System.out.println(gpu.getName()+"'s VRAM isn't empty");
	}
	
	public void testModel(TestModelEvent e) {
		Model m = e.getModel();
		gpu.testModel(m);
		MessageBusImpl.getInstance().complete(e, m);
	}
	
    

    @Override
    protected void initialize() {
    	MessageBusImpl.getInstance().register(this);
    	subscribeEvent(TrainModelEvent.class, trainModelCallback);
    	subscribeEvent(TestModelEvent.class, testModelCallback);
    	subscribeBroadcast(TickBroadcast.class, tickCallback);
    	subscribeBroadcast(LastTickBroadcast.class, lastTickCallback);
    	cluster.addToList(gpu);
    	assistant.start();
    }
    
    class Assistant implements Runnable{
    	public void run() {
	    	while(!terminated) {
    			while(events.isEmpty()&!terminated) {
    				System.out.println(gpu.getName()+" empty");
	    			synchronized (events) {
						try {
							events.wait();//Will be notified when GPUService's callback will add to events
						} catch (InterruptedException e1) {System.out.println(gpu.getName()+" was notified");}
					}
	    		}
	    		Event<Model> next = null;
	    		if(terminated)
	    			System.out.println("terminated");
	    		else {
    				System.out.println(gpu.getName()+" is not empty anymore, it has "+events.peek());
		    		for(Event<Model> e : events) {
		    			if(e instanceof TestModelEvent) {
		    				next = e;
		    				break;
		    			}
		    		}
	    		}
		    	if(!terminated) {
	    			if(next!=null) {
		    			testModel((TestModelEvent)next);
		    			events.remove(next);
		    		}
		    		else {
		    			next = events.remove();
		    			System.out.println("train model event in: "+gpu.getName());
		    			trainNewModel((TrainModelEvent)next);
		    		}
	    		}
    		}
    	}
    }
}
