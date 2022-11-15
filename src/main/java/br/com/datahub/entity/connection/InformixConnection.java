package br.com.datahub.entity.connection;

import com.informix.jdbc.IfxDriver;

public class InformixConnection extends DefaultConnection implements DatabaseConnection {

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
		return String.format("jdbc:informix-sqli://%s:%s/%s", databaseIp, databasePort, databaseName);
	}

	@Override
	void loadDriver() {
		try {
			Class.forName(IfxDriver.class.getName());
		} catch (ClassNotFoundException e) {
		}
	}

}
