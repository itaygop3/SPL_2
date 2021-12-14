package src.main.java.bgu.spl.mics.application.messages;

import src.main.java.bgu.spl.mics.Event;
import src.main.java.bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model>{
	
	private Model model;
	
	public TrainModelEvent(Model _model) {
		model = _model;
	}
	
	public Model getModel() {
		return model;
	}
	
}
