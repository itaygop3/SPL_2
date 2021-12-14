package src.main.java.bgu.spl.mics;

//package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	
	private static MessageBusImpl msgBus = new MessageBusImpl();
	private HashMap<MicroService,MicroData> registered = new HashMap<>();
	private int size = 0;
	private ConcurrentHashMap <Event<?> ,Future<?>> eventsFutures = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class<? extends Message>, List<MicroService>> subscribeLog = new ConcurrentHashMap<>();
	private Object lock1 = new Object();
	private Object lock2 = new Object();

	
	public static MessageBusImpl getInstance() {
		return msgBus;
	}
	
	public int size() {
		return size;
	}
	
	public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
		return registered.get(m).subscribedTo.contains(type);
	}
	public boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return registered.get(m).subscribedTo.contains(type);
	}
	
	public boolean isInMsgBus(MicroService m) {
		return registered.containsKey(m);
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(isSubscribedToEvent(type, m))
			return;
		if(subscribeLog.containsKey(type)) {
			List<MicroService> tmp = subscribeLog.get(type);
			tmp.add(m);
			registered.get(m).subscribedTo.add(type);
		}
		else subscribeMessage(type, m, lock1);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(isSubscribedToBroadcast(type, m))
			return;
		if(subscribeLog.containsKey(type)) {
			List<MicroService> tmp = subscribeLog.get(type);
			tmp.add(m);
			registered.get(m).subscribedTo.add(type);
		}
		else subscribeMessage(type, m, lock2);
	}
	
	private <T> void subscribeMessage(Class<? extends Message> type, MicroService m , Object lock){
		boolean flag = false;
		synchronized (lock) {
			if(!subscribeLog.containsKey(type)) {
				List<MicroService> tmp = Collections.synchronizedList(new LinkedList<MicroService>());
				tmp.add(m);
				subscribeLog.put(type,tmp);
				registered.get(m).subscribedTo.add(type);
			}
			else flag = true;
		}
		//TODO check if event types can be deleted (if no one is subscribed)
		if(flag) {
			if(lock == lock1)
				subscribeEvent((Class<? extends Event<T>>)type, m);
			else subscribeBroadcast((Class<? extends Broadcast>)type, m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> f = (Future<T>) eventsFutures.get(e);
		f.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(!subscribeLog.containsKey(b.getClass()))
			return;
		List<MicroService> subscribed = subscribeLog.get(b.getClass());
		synchronized(subscribed) {
			for(MicroService m : subscribed)
				registered.get(m).messeges.add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(!subscribeLog.containsKey(e.getClass())) // If there is no suitable Micro-Service, it should return null
			return null;
		boolean flag = false;
		List<MicroService> subscribed = subscribeLog.get(e.getClass());
		while(!flag) {
			MicroService first = subscribed.get(0);
			subscribed.remove(first);
			synchronized(first) {
				if(registered.get(first).registerd.get())
				subscribed.add(first);//TODO same as subscribe
				registered.get(first).messeges.add(e);
				flag=true;
			}
		}
		Future<T> f = new Future<>();
		eventsFutures.put(e, f);
		return f;
	}

	@Override
	public void register(MicroService m) {
		
		LinkedList<Message> queue = new LinkedList<Message>();
		registered.computeIfAbsent(m,k-> new MicroData());
		size++;
	}

	@Override
	public synchronized void unregister(MicroService m) {
		synchronized(m) {
			MicroData mcrodata = registered.get(m);
			if(mcrodata!=null) {
				mcrodata.registerd.compareAndSet(true, false);
				for(Class<? extends Message> c : mcrodata.subscribedTo)
					subscribeLog.get(c).remove(m);
				registered.remove(m);
				size--;
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Queue<Message> queue = registered.get(m).messeges;
		while(queue.isEmpty()) {
			try {
				m.wait();
			}catch(InterruptedException e) {}
		}
		Message msg = queue.remove();
		queue.remove(0);
		return msg;
	}
	
	/**
	 * In order to remove MicroServices from a queue without iterating over 
	 * the entire set we should have the option to mark a MicroService
	 * as no longer registerd. this will also allow us to track the registered 
	 * types for a microservice instead of iterating on the list
	 */
	private static class MicroData{
		
		AtomicBoolean registerd = new AtomicBoolean(true);
		Set<Class<? extends Message>> subscribedTo = new HashSet<>();
		Queue<Message> messeges = new LinkedList<Message>();
	}
	

}


