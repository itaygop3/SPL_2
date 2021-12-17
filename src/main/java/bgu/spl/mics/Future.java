package src.main.java.bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	
	
	private boolean status = false;
	private  AtomicReference<T> result = new AtomicReference<T>(null);
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * @post
     * get() == result, get!=null    
     */
	public synchronized T get() {
		while(!status) {
			try {
				wait();
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		return result.get();
	}
	
	/**
     * Resolves the result of this Future object.
     * @post this.get()==result, this.isDone()==true
     */
	public synchronized void resolve (T result) {
		T currentResult;
		do {
			currentResult = this.result.get();
		}while(!this.result.compareAndSet(currentResult, result));
		status=true;
		notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return status;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		if(timeout==0)
			return result.get();
		try {
			wait(unit.toMillis(timeout));
		}catch(InterruptedException e) {}
		return result.get();
	}
}
