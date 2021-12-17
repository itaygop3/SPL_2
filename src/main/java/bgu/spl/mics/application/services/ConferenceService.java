package src.main.java.bgu.spl.mics.application.services;

import src.main.java.bgu.spl.mics.*;
import src.main.java.bgu.spl.mics.application.messages.*;
import src.main.java.bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    
	private int deathTime;
	private int currentTime = 0;
	private ConfrenceInformation conferenceInformation;
	
	public ConferenceService(String name, int _deathTime, ConfrenceInformation conference) {
        super(name+"Svc");
        deathTime = _deathTime;
        conferenceInformation = conference;
        initialize();
    }
    private Callback<TickBroadcast> tickCallBack = (b ->{
    	//Update the time and check if its time to die
    	if(++currentTime == deathTime) {
    		Broadcast result = new PublishConferenceBroadcast(conferenceInformation.getSuccessfulModel());
    		MessageBusImpl.getInstance().sendBroadcast(result);
    		terminate();
    	}	
    });
    
    private Callback<PublishResultsEvent> publishResultCallback = (result ->{
    	conferenceInformation.addModel(result.getModel());
    	MessageBusImpl.getInstance().complete(result, result.getModel());
    });
    

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        subscribeEvent(PublishResultsEvent.class, publishResultCallback);
        subscribeBroadcast(TickBroadcast.class, tickCallBack);
    }
}
