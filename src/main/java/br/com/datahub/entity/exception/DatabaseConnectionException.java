package br.com.datahub.entity.exception;

public class DatabaseConnectionException extends Exception {

	private static final long serialVersionUID = 6363884427632320374L;

	public DatabaseConnectionException() {
		super("Falha ao conectar com o banco de dados");
	}

	public DatabaseConnectionException(String message) {
		super(message);
	}

}
