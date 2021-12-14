package src.main.java.bgu.spl.mics.application.messages;

import src.main.java.bgu.spl.mics.Broadcast;
import java.util.*;

public class PublishConferenceBroadcast implements Broadcast{
	
	private Set<String> successfulModels;
	
	public PublishConferenceBroadcast(Set<String> _successfulModels) {
		successfulModels = _successfulModels;
	}

	public Set<String> getSuccessfulModels() {
		return successfulModels;
	}
	
}
