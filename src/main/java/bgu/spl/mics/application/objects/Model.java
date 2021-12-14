package src.main.java.bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
	
	public enum Status{
		PreTrained, Training, Trained, Tested
	}
	public enum Result{
		NONE, GOOD, BAD
	}
	
	private String name;
	private Data data;
	private Student student;
	private Status status = Status.PreTrained;
	private Result result = Result.NONE;
	
	public Model(String _name, Data _data, Student _student) {
		name = _name;
		data = _data;
		student = _student;
	}
	
	public String getName() {
		return name;
	}
	
	public Data getData() {
		return data;
	}
	
	public Student getStudent() {
		return student;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Result getResult() {
		return result;
	}
	
	public void updateStatus(){
		switch(status) {
		case PreTrained:
			status = Status.Training;
			break;
		case Training:
			status = Status.Trained;
			break;
		case Trained:
			status = Status.Tested;
			break;
		case Tested:
			break;
		}
	}
	
	public void setResult(Result _result) {
		result = _result;
	}

}
