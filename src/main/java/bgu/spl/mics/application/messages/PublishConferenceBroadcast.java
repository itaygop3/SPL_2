package src.main.java.bgu.spl.mics.application.messages;

import src.main.java.bgu.spl.mics.Broadcast;
import java.util.*;

public class PublishConferenceBroadcast implements Broadcast{
	
	private LinkedList<String> successfulModels;
	
	public PublishConferenceBroadcast(LinkedList<String> _successfulModels) {
		successfulModels = _successfulModels;
	}

	public LinkedList<String> getSuccessfulModels() {
		return successfulModels;
	}
	
}
