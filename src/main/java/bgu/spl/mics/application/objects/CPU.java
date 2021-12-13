package bgu.spl.mics.application.objects;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
	
	private int cores;
	private Queue<DataBatch> data;
	private Cluster cluster;
	private boolean isFree = true;
	
	public CPU(int cores, Cluster cluster) {
		cores = cores;
		data = new Queue<DataBatch>() {
			
			@Override
			public <T> T[] toArray(T[] arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object[] toArray() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public boolean retainAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean removeAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean remove(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Iterator<DataBatch> iterator() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean containsAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean contains(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean addAll(Collection<? extends DataBatch> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public DataBatch remove() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DataBatch poll() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DataBatch peek() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean offer(DataBatch arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public DataBatch element() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean add(DataBatch arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		cluster = cluster;
	}
	
	public void insertData(DataBatch batch){
		data.add(batch);
		isFree = false;
	}
	
	public Queue<DataBatch> getData(){
		return data;
	}
	
	private DataBatch process() {
		
	}
	
	public void sendProcessedData() {
		
	}
	
	public boolean isFree() {
		return isFree;
	}
	
}
