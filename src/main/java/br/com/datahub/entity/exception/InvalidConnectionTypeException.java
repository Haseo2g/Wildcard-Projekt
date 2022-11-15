package br.com.datahub.entity.exception;

public class InvalidConnectionTypeException extends Exception {

	private static final long serialVersionUID = 6363884427632320374L;

	public InvalidConnectionTypeException() {
		super("A query enviada não é válida");
	}

	public InvalidConnectionTypeException(String message) {
		super(message);
	}

	public InvalidConnectionTypeException(String query, String message) {
		super("Query informada: [" + query + "] - " + message);
	}

}
