package src.main.java.bgu.spl.mics.application;

import java.util.LinkedList;
import java.util.*;
import src.main.java.bgu.spl.mics.*;
import src.main.java.bgu.spl.mics.application.objects.CPU;
import src.main.java.bgu.spl.mics.application.objects.ConfrenceInformation;
import src.main.java.bgu.spl.mics.application.objects.Data;
import src.main.java.bgu.spl.mics.application.objects.GPU;
import src.main.java.bgu.spl.mics.application.objects.Model;
import src.main.java.bgu.spl.mics.application.objects.Student;
import src.main.java.bgu.spl.mics.application.services.TimeService;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
	
	public static int tickTime = 0;
	
    public static void main(String[] args) {
    	
    	Student student1 = new Student("s1", "dep", Student.Degree.PhD, new LinkedList<>());
    	Student student2 = new Student("s2", "dep", Student.Degree.MSc, new LinkedList<>());
    	
    	Data d1 = new Data(Data.Type.Images, 1000*20);
    	Data d2 = new Data(Data.Type.Text, 1000*20);
    	Data d3 = new Data(Data.Type.Tabular, 1000*32);
    	Data d4 = new Data(Data.Type.Images, 1000*20);
    	Data d5 = new Data(Data.Type.Text, 1000*20);
    	Data d6 = new Data(Data.Type.Tabular, 1000*32);
    	
    	Model m1 = new Model("m1", d1, student1);
    	Model m2 = new Model("m2", d2, student1);
    	Model m3 = new Model("m3", d3, student1);
    	Model m4 = new Model("m4", d4, student2);
    	Model m5 = new Model("m5", d5, student2);
    	Model m6 = new Model("m6", d6, student2);
    	
    		student1.getmodelsToTrain().add(m1);
    		student1.getmodelsToTrain().add(m2);
    		student1.getmodelsToTrain().add(m3);
    		student2.getmodelsToTrain().add(m4);
    		student2.getmodelsToTrain().add(m5);
    		student2.getmodelsToTrain().add(m6);
    		
    		GPU g1 = new GPU(GPU.Type.RTX2080, "g1");
    		GPU g2 = new GPU(GPU.Type.RTX3090, "g2");
    		
    		CPU c1 = new CPU(16);
    		CPU c2 = new CPU(8);
    		CPU c3 = new CPU(16);
    		CPU c4 = new CPU(32);
    		
    		TimeService t = new TimeService(10, 500);
    		ConfrenceInformation ci = new ConfrenceInformation("c", 499);
    		
    		List<Thread> threads = new LinkedList<Thread>();
    		threads.add(new Thread(student1.getService()));
    		threads.add(new Thread(student2.getService()));
    		threads.add(new Thread(g1.getGPUService()));
    		threads.add(new Thread(g2.getGPUService()));
    		threads.add(new Thread(c1.getService()));
    		threads.add(new Thread(c2.getService()));
    		threads.add(new Thread(c3.getService()));
    		threads.add(new Thread(c4.getService()));
    		threads.add(new Thread(ci.getService()));
    		
    		for(Thread th : threads)
    			th.start();
    		t.initialize();
    		for(Thread th : threads) {
				try {
					th.join();
				} catch (InterruptedException e) {}
    		}
    		
    		System.out.println("\n");
    		System.out.println("student1: \npapers read: "+student1.getPapersRead()+"\npapers published: "+student1.getPublications()+"\n");
    		for(Model m : student1.getmodelsToTrain())
    			System.out.println(m.toString());
    		System.out.println();
    		System.out.println("student2: \npapers read: "+student2.getPapersRead()+"\npapers published: "+student2.getPublications()+"\n");
    		for(Model m : student2.getmodelsToTrain())
    			System.out.println(m.toString());
    }
}
