package br.com.datahub.entity.connection;

import java.util.List;
import java.util.Map;

import br.com.datahub.entity.Column;
import br.com.datahub.entity.exception.DatabaseConnectionException;
import br.com.datahub.entity.exception.InvalidQueryException;

public interface DatabaseConnection {

	/**
	 * Method responsible for getting all objects returned from the query. The main list contains lists of String which represents a line.
	 * Example:
	 * List<String> = [x, y, z]
	 * List<List<String>> =
	 * [a, b, c]
	 * [d, e, f]
	 * [x, y, z]
	 * @param query
	 * @param databaseIp
	 * @param databasePort
	 * @param databaseName
	 * @param user
	 * @param password
	 * @return Query response
	 * @throws DatabaseConnectionException in case the connection to the database fails
	 * @throws InvalidQueryException in case the query is invalid.
	 */
	List<List<String>> getResultList(String query, String databaseIp, String databasePort, String databaseName,
			String user, String password) throws DatabaseConnectionException, InvalidQueryException;

	/**
	 * Check if the query is a valid Select Query. If it's not, the query must NOT be executed
	 * @param query
	 * @return true if the query is indeed a select query. Otherwise, it returns false
	 * @throws InvalidQueryException in case the query isn't a select
	 */
	boolean validateSelectQuery(String query) throws InvalidQueryException;

	/**
	 * Method responsible for testing the database connection
	 * @param databaseIp
	 * @param databasePort
	 * @param databaseName
	 * @param user
	 * @param password
	 * @return true if the connection is successful
	 */
	boolean validateConnection(String databaseIp, String databasePort, String databaseName, String user,
			String password);
	
	/**
	 * Method used to get column names from a query
	 * @param query
	 * @param databaseIp
	 * @param databasePort
	 * @param databaseName
	 * @param user
	 * @param password
	 * @return Column headers and it's type
	 * @throws DatabaseConnectionException in case connection to database fails
	 * @throws InvalidQueryException in case the query contains invalid information
	 */
	Map<String, Column> getHeaders(String query, String databaseIp, String databasePort, String databaseName,
			String user, String password) throws DatabaseConnectionException, InvalidQueryException;
}
