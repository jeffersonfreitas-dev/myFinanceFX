package model.exceptions;

public class RecordAlreadyRecordedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public RecordAlreadyRecordedException(String msg) {
		super(msg);
	}

}
