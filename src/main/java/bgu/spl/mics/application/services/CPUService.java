package src.main.java.bgu.spl.mics.application.services;

import src.main.java.bgu.spl.mics.application.objects.*;
import src.main.java.bgu.spl.mics.application.messages.*;
import src.main.java.bgu.spl.mics.*;

/**
 * CPU service is responsible for handling the DataPreProcessEvent.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;
    private int time=0;

    public CPUService(String name, CPU _cpu) {
        super(name+"Svc");
        cpu= _cpu;
        initialize();
    }

    //this callback handles getting a tickBroadcast and updates the cpu accordingly.
    private Callback<TickBroadcast> tickUpdateCallBack= k->{
        cpu.getCurrentTicksNumber().set(++time);
        cpu.process();
    };
    
    private Callback<LastTickBroadcast> lastTickCallback = k ->{
    	terminate();
    };

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        subscribeBroadcast(TickBroadcast.class,tickUpdateCallBack);
        subscribeBroadcast(LastTickBroadcast.class,lastTickCallback);
    }
}
