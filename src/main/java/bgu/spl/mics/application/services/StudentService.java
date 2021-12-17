package src.main.java.bgu.spl.mics.application.services;

import java.util.*;
import src.main.java.bgu.spl.mics.*;
import src.main.java.bgu.spl.mics.application.objects.*;
import src.main.java.bgu.spl.mics.application.messages.*;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    
	private Student student;
	private Assistant task = new Assistant();
	private Thread assistant = new Thread(task);
	
	public StudentService(String name, Student _student) {
        super(name+"Svc");
        student = _student;
        initialize();
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        subscribeBroadcast(LastTickBroadcast.class, k -> {//The callback for lastTickBroadcast
        	terminate();
        	synchronized(task.f) {task.f.notifyAll();}
        	});
        subscribeBroadcast(PublishConferenceBroadcast.class, k -> student.read(k.getSuccessfulModels()));
        assistant.start();
    }
    
    class Assistant implements Runnable{
    	
    	public Future<Model> f= null;
    	
    	public void run() {
    		Iterator<Model> it = student.getmodelsToTrain().iterator();
    		do {
    			Model m = it.next();
    			do{
    				f = MessageBusImpl.getInstance().sendEvent(new TrainModelEvent(m));
    			}while(f == null&!terminated);
    			if(!terminated)
    				f = MessageBusImpl.getInstance().sendEvent(new TestModelEvent(f.get()));
    			if(!terminated) {
    				f = MessageBusImpl.getInstance().sendEvent(new PublishResultsEvent(f.get()));//In our implentation it won't be added if result is bad
    				if(f.get().getResult() ==  Model.Result.GOOD)
    					student.incrementPublished();
    			}
    		}while(!terminated&it.hasNext());
    	}
    }
}
