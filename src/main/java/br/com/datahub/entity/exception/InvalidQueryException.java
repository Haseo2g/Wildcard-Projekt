package br.com.datahub.entity.exception;

public class InvalidQueryException extends Exception {

	private static final long serialVersionUID = 6363884427632320374L;

	public InvalidQueryException() {
		super("A query enviada não é válida");
	}

	public InvalidQueryException(String message) {
		super(message);
	}

	public InvalidQueryException(String query, String message) {
		super("Query informada: [" + query + "] - " + message);
	}

}
