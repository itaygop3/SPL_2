package src.main.java.bgu.spl.mics.application.objects;

import java.util.*;
import src.main.java.bgu.spl.mics.application.services.StudentService;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }
    private String name;
    private String department;
    private Degree status;
    private int publications = 0;
    private int papersRead = 0;
    private Set<String> modelNames;
    private LinkedList<Model> modelsToTrain = new LinkedList<>();
    private StudentService service = new StudentService("", this);

    public Student(String _name, String _department, Degree _status, LinkedList<Model> models) {
    	name = _name;
    	department = _department;
    	status = _status;
    	modelNames = modelNames();
    	modelsToTrain = models;
    }
    
    private Set<String> modelNames(){
    	Set<String> names = new HashSet<String>();
    	for(Model m : modelsToTrain)
    		names.add(m.getName());
    	return names;
    }
    
    public void read(LinkedList<String> pulishedModels) {
    	for(String model : pulishedModels)
    		if(!modelNames.contains(model))
    			papersRead++;
    }
    
    public void incrementPublished() {
    	publications++;
    }
    
    public LinkedList<Model> getmodelsToTrain(){
    	return modelsToTrain;
    }
    
    public StudentService getService() {
    	return service;
    }

	public String getName() {
		return name;
	}

	public String getDepartment() {
		return department;
	}

	public Degree getStatus() {
		return status;
	}

	public int getPublications() {
		return publications;
	}

	public int getPapersRead() {
		return papersRead;
	}
	
	public void publish() {
		publications++;
	}
    
    
}
