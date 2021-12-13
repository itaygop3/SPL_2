package src.test.java.bgu.spl.mics;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import src.main.java.bgu.spl.mics.MessageBusImpl;
import src.main.java.bgu.spl.mics.*;

class MessegeBusImplTest {
	
	private MessageBusImpl msgBus;
	
	@Test
	public void testRegister(){
		int orgSize=msgBus.size();
		MicroService m=new MicroService("name") {
			
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub	
			}
		};
		msgBus.register(m);
		assertEquals(orgSize+1,msgBus.size());
		assertTrue(msgBus.isInMsgBus(m));
	}
	
	@Test
	public void testUnregister() {
		MicroService m = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		msgBus.register(m);
		int curSize = msgBus.size();
		msgBus.unregister(m);
		assertEquals(curSize-1, msgBus.size());
		assertFalse(msgBus.isInMsgBus(m));
	}
	
	@Test
	public void testSubscribeEvent() {
		MicroService m = new MicroService("name") {
			
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		Class type=ExampleEvent.class;
		assertFalse(msgBus.isSubscribedToEvent(type, m));
		msgBus.subscribeEvent(type, m);
		assertTrue(msgBus.isSubscribedToEvent(type,m));
	}
	
	@Test
	public void testSbuscribeBroadcast() {
		MicroService m = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		Class type = ExampleBroadcast.class;
		assertFalse(msgBus.isSubscribedToBroadcast(type, m));
		msgBus.subscribeBroadcast(type, m);
		assertTrue(msgBus.isSubscribedToBroadcast(type, m));
	}
	
	@Test
	public void testComplete() {
		Event<String> e = new ExampleEvent("name");
		Future<String> f=msgBus.sendEvent(e);
		String result = "result";
		assertFalse(f.isDone());
		msgBus.complete(e, result);
		assertTrue(f.isDone());
		assertEquals(result, f.get());
	}
	
	@Test
	public void testSendBroadCast() {
		MicroService m1 = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		MicroService m2 = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		MicroService m3 = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		Class type = ExampleBroadcast.class;
		msgBus.subscribeBroadcast(type, m1);
		msgBus.subscribeBroadcast(type, m2);
		msgBus.subscribeEvent(ExampleEvent.class, m3);//to test only the MS subscribed to the broadcast recives it
		Broadcast b = new ExampleBroadcast("id");
		Event e = new ExampleEvent("name");
		msgBus.sendBroadcast(b);
		msgBus.sendEvent(e);// so there will be something in m3's queue
		Message msg1=null;
		Message msg2=null;
		Message msg3=null;
		try{
			msg1 = msgBus.awaitMessage(m1);
			msg2 = msgBus.awaitMessage(m2);
			msg3 = msgBus.awaitMessage(m3);
		}catch(InterruptedException ex) {}
		m3.notify();
		assertEquals(b, msg1);
		assertEquals(b, msg2);
		assertNotEquals(b, msg3);
	}
	
	@Test
	public void testSendEvent() {
		MicroService m1 = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		MicroService m2 = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		Class type = ExampleEvent.class;
		msgBus.subscribeEvent(type, m1);
		msgBus.subscribeEvent(type, m2);
		Event e1 = new ExampleEvent("sender name1");
		Event e2 = new ExampleEvent("sender name2");
		msgBus.sendEvent(e1);
		msgBus.sendEvent(e2);
		Message msg1 = null;
		Message msg2 = null;
		try {
			msgBus.awaitMessage(m1);
			msgBus.awaitMessage(m2);
		}catch(InterruptedException e) {}
		assertEquals(e1, msg1);
		assertEquals(e2, msg2);
	}
	
	@Test
	public void testAwaitMessage() {
		MicroService m = new MicroService("name") {
			@Override
			protected void initialize() {
				// TODO Auto-generated method stub
			}
		};
		Class eventEx = ExampleEvent.class;
		Class broadEx = ExampleBroadcast.class;
		msgBus.subscribeBroadcast(broadEx, m);
		msgBus.subscribeEvent(eventEx, m);
		Event e = new ExampleEvent("sender");
		Broadcast b = new ExampleBroadcast("id");
		msgBus.sendBroadcast(b);
		Future f = msgBus.sendEvent(e);
		Message msg = null;
		try {
			msg = msgBus.awaitMessage(m);
		}catch(InterruptedException ex) {}
		assertEquals(b, msg);
		try {
			msg = msgBus.awaitMessage(m);
		}catch(InterruptedException ex) {}
		assertEquals(e, msg);
		msg = null;
		try {
			msg = msgBus.awaitMessage(m);
			m.notify();
		}catch (InterruptedException ex) {assertNull(msg);}
	}
	
	

}


class ExampleBroadcast implements Broadcast {

    private String senderId;

    public ExampleBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}


class ExampleEvent implements Event<String>{

    private String senderName;

    public ExampleEvent(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
}
