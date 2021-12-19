package src.main.java.bgu.spl.mics.application.services;

import src.main.java.bgu.spl.mics.MessageBusImpl;
import src.main.java.bgu.spl.mics.MicroService;
import src.main.java.bgu.spl.mics.application.CRMSRunner;
import src.main.java.bgu.spl.mics.application.messages.FirstTickBroadcast;
import src.main.java.bgu.spl.mics.application.messages.LastTickBroadcast;
import src.main.java.bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	
	private int finalTick;//The time of termination
	private int tickTime;
	int curTick = 0;
	private Timer timer = new Timer();
	
	public TimeService(int _tickTime, int finalTime) {
		super("timeService");
		finalTick = finalTime;
		tickTime = _tickTime;
	}
	
	private void sentTick() {
		TimerTask task =  new TimerTask() {
			public void run() {
				if(curTick == 0) {
					MessageBusImpl.getInstance().sendBroadcast(new FirstTickBroadcast());
					curTick++;
					System.out.println("first tick: "+FirstTickBroadcast.class);
				}
				if (curTick < finalTick&curTick!=0) {
                    MessageBusImpl.getInstance().sendBroadcast(new TickBroadcast());
//                    System.out.println("tick number: "+curTick);
                    curTick++;
                } 
				else {
                    MessageBusImpl.getInstance().sendBroadcast(new LastTickBroadcast());
                    System.out.println("last tick: "+ curTick);
                    timer.cancel();
                }
			}
		};
		timer.schedule(task,0, tickTime);
	}

	@Override
	public void initialize() {
		MessageBusImpl.getInstance().register(this);
		sentTick();
	}
}
