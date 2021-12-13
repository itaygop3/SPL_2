package src.main.java.bgu.spl.mics;

//package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	
	private static MessageBusImpl msgBus = new MessageBusImpl();
	
	private ConcurrentHashMap<MicroService, LinkedList<Message>> mcroSrvcQs;
	private int size;
	private ConcurrentHashMap <Event<?> ,Future<?>> eventsFutures;
	private ConcurrentHashMap<Class<? extends Message>, List<MicroService>> subscribeLog;
	private Object lock1 = new Object();
	private Object lock2 = new Object();

	
	private MessageBusImpl() {
		mcroSrvcQs = new ConcurrentHashMap<MicroService, LinkedList<Message>>();
		size = 0;
		eventsFutures = new ConcurrentHashMap<Event<?>, Future<?>>();
		subscribeLog = new ConcurrentHashMap<Class<? extends Message>, List<MicroService>>();
	}
	
	public static MessageBusImpl getInstance() {
		return msgBus;
	}
	
	public int size() {
		return size;
	}
	
	public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
		List<MicroService> tmp = subscribeLog.get(type);
		if(tmp==null)
			return false;
		return tmp.contains(m);
	}
	public boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m) {
		List<MicroService> tmp = subscribeLog.get(type);
		return tmp.contains(m);
	}
	
	public boolean isInMsgBus(MicroService m) {
		return mcroSrvcQs.containsKey(m);
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(isSubscribedToEvent(type, m))
			return;
		if(subscribeLog.containsKey(type)) {
			List<MicroService> tmp = subscribeLog.get(type);
			tmp.add(m);
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
				mcroSrvcQs.get(m).add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(!subscribeLog.containsKey(e.getClass())) // If there is no suitable Micro-Service, it should return null
			return null;
		List<MicroService> subscribed = subscribeLog.get(e.getClass());
		MicroService first = subscribed.get(0);
		subscribed.remove(first);
		subscribed.add(first);//TODO same as subscribe
		mcroSrvcQs.get(first).add(e);
		Future<T> f = new Future<>();
		eventsFutures.put(e, f);
		return f;
	}

	@Override
	public void register(MicroService m) {
		LinkedList<Message> queue = new LinkedList<Message>();
		mcroSrvcQs.put(m, queue);
		size++;
	}

	@Override
	public synchronized void unregister(MicroService m) {
		List<Message> list = mcroSrvcQs.remove(m);
		if(list!=null) {
			for(Class<? extends Message> c : subscribeLog.keySet())
				subscribeLog.get(c).remove(m);
			size--;
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		List<Message> queue = mcroSrvcQs.get(m);
		while(queue.isEmpty()) {
			try {
				m.wait();
			}catch(InterruptedException e) {}
		}
		Message msg = queue.get(0);
		queue.remove(0);
		return msg;
	}

	

}

