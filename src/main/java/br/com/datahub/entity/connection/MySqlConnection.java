package br.com.datahub.entity.connection;

import java.sql.Driver;

public class MySqlConnection extends DefaultConnection implements DatabaseConnection {

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
		return String.format("jdbc:mysql://%s:%s/%s", databaseIp, databasePort, databaseName);
	}

	@Override
	void loadDriver() {
		try {
			Class.forName(Driver.class.getName());
		} catch (ClassNotFoundException e) {
		}
	}
}
