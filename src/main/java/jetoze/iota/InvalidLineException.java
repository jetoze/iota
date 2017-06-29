package jetoze.iota;

public final class InvalidLineException extends RuntimeException {

	public InvalidLineException() {
		this("Not a valid line.");
	}
	
	public InvalidLineException(String message) {
		super(message);
	}
	
	public InvalidLineException(String message, Throwable cause) {
		super(message, cause);
	}

}
