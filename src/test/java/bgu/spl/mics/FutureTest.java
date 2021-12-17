package src.test.java.bgu.spl.mics;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import src.main.java.bgu.spl.mics.Future;

class FutureTest {
	
	private static String s;
	
	@Test
	public void testIsDone() {
		Future<String> f = new Future<>();
		assertFalse(f.isDone());
		s = "hi";
		Thread t = new Thread(()-> f.resolve(s));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(f.isDone());
		assertEquals(s, f.get());
	}
	
	@Test
	public void testBlockingGet() {
		Future<String> f = new Future<>();
		s = null;
		String result = "";
		Thread t=new Thread(()->{
			s=f.get();
			assertEquals(result, s);
		});
		t.start();
		f.resolve(result);
		try {
			t.join();
		}catch(InterruptedException e) {}
	}
	
	@Test
	public void testResolve() {
		Future<String> f = new Future<>();
		assertFalse(f.isDone());
		assertNull(f.get(0,TimeUnit.MILLISECONDS));
		s="";
		Thread t = new Thread(()->f.resolve(s));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(s, f.get());
		assertTrue(f.isDone());
	}
	
	@Test
	public void testGet() {
		s = null;
		Future<String> f = new Future<>();
		TimeUnit unit=TimeUnit.SECONDS;
		Thread t=new Thread(()->{
		s=f.get(3, unit);
		assertNull(s);
		});
		t.start();
		try {
			t.join();
		}catch(InterruptedException e) {}
		t = new Thread(()->{
			s = f.get(3, unit);
			assertEquals("", s);
		});
		t.start();
		f.resolve("");
		try {
			Thread.sleep(unit.toMillis(1));
		}catch(InterruptedException e) {}
		
	}
	

}
