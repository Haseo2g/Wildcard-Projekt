package br.com.datahub.entity.connection;

import org.postgresql.Driver;

public class PostgresConnection extends DefaultConnection implements DatabaseConnection {

	/**
	 * Método responsável por criar uma conexão com a database
	 * 
	 * @param databaseIp
	 * @param databasePort
	 * @param databaseName
	 * @return
	 */
	@Override
	String getConnectionString(String databaseIp, String databasePort, String databaseName) {
		loadDriver();
		return String.format("jdbc:postgresql://%s:%s/%s", databaseIp, databasePort, databaseName);
	}

	@Override
	void loadDriver()  {
		try {
			Class.forName(Driver.class.getName());
		} catch (ClassNotFoundException e) {
		}		
	}
}
