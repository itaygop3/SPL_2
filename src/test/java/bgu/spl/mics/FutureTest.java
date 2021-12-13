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
		Thread t=new Thread(()->s=f.get());
		Thread t2 = new Thread(()->f.resolve(result));
		t.start();
		assertNull(s);
		t2.start();
		try {
			t.join();
			t2.join();
		}catch(InterruptedException e) {}
		assertEquals(result, s);
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
		Thread t=new Thread(()->s=f.get(3, unit));
		t.run();
		assertNull(s);
		try {
			t.join();
		}catch(InterruptedException e) {}
		assertNull(s);
		t.run();
		f.resolve("");
		try {
			wait(unit.toMillis(1));
		}catch(InterruptedException e) {}
		assertEquals("",s);
		
	}
	

}
