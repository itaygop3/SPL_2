package src.main.java.bgu.spl.mics.application.messages;

import src.main.java.bgu.spl.mics.Event;
import src.main.java.bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Model>{
	
	private Model model;
	
	public PublishResultsEvent(Model _model) {
		model = _model;
	}
	
	public Model getModel() {
		return model;
	}
	
}
