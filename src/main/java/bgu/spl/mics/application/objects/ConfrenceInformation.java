package src.main.java.bgu.spl.mics.application.objects;

import java.util.*;
import src.main.java.bgu.spl.mics.application.services.*;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<String> successfulModel = new LinkedList<String>();
    private ConferenceService svc;

    public ConfrenceInformation(String _name, int _date){
        name=_name;
        date=_date;
        svc = new ConferenceService(name, date, this);
    }
    
    public ConferenceService getService() {
    	return svc;
    }
    
    public int getDate(){
    	return date;
    }
    
    public String getName() {
    	return name;
    }
    
    public boolean addModel(Model m) {
    	if(m.getResult() == Model.Result.GOOD) {
    		successfulModel.add(m.getName());
    		return true;
    	}
    	return false;
    }

    public LinkedList<String> getSuccessfulModel() {
        return successfulModel;
    }
}