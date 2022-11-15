package br.com.datahub.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datahub.entity.Column;
import br.com.datahub.entity.Database;
import br.com.datahub.entity.Query;
import br.com.datahub.entity.connection.DatabaseConnection;
import br.com.datahub.entity.exception.DatabaseConnectionException;
import br.com.datahub.entity.exception.InvalidQueryException;
import br.com.datahub.repository.DatabaseRepository;
import br.com.datahub.repository.QueryRepository;

@Service
public class QueryService {

	@Autowired
	DatabaseRepository databaseRepository;

	@Autowired
	QueryRepository queryRepository;

	// Find:
	public Optional<Query> findById(long id) {
		return queryRepository.findById(id);
	}

	public List<Query> findAll() {
		return queryRepository.findAll();
	}

	// Insert:
	public Query save(Query query) {
		return queryRepository.save(query);
	}

	// Delete:
	public void deleteById(long queryId) {
		queryRepository.deleteById(queryId);
	}

	// Load lists:
	public List<List<String>> loadList(long idQuery, String params) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DatabaseConnectionException, InvalidQueryException {
		Optional<Query> query = findById(idQuery);
		if (!query.isPresent())
			throw new InvalidQueryException("Query não localizada na base de dados");

		Query q = query.get();
		if (params != null)
			q.setQueryLine(q.getQueryLine().replace(";", " " + params + ";"));
		DatabaseConnection connection = getConnection(query.get().getDatabase().getConnectionType());
		return loadList(connection, q);
	}

	public List<List<String>> loadList(String query, long idDatabase) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DatabaseConnectionException, InvalidQueryException {
		Optional<Database> database = databaseRepository.findById(idDatabase);
		if (!database.isPresent())
			throw new DatabaseConnectionException("Database não localizada");
		return loadList(database.get(), query);
	}

	public List<List<String>> loadList(long idQuery) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, DatabaseConnectionException, InvalidQueryException {
		Optional<Query> query = findById(idQuery);
		if (!query.isPresent())
			throw new InvalidQueryException("Query não localizada na base de dados");

		DatabaseConnection connection = getConnection(query.get().getDatabase().getConnectionType());
		return loadList(connection, query.get());
	}

	public List<List<String>> loadList(Query query) throws DatabaseConnectionException, InvalidQueryException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return loadList(getConnection(query), query);
	}

	private List<List<String>> loadList(DatabaseConnection databaseConnection, Query query)
			throws DatabaseConnectionException, InvalidQueryException {
		Database database = query.getDatabase();
		return databaseConnection.getResultList(query.getQueryLine(), database.getIp(), database.getPort(),
				database.getDatabaseName(), database.getUserName(), database.getPassword());
	}

	private List<List<String>> loadList(Database database, String query) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DatabaseConnectionException, InvalidQueryException {
		DatabaseConnection databaseConnection = getConnection(database.getConnectionType());
		return databaseConnection.getResultList(query, database.getIp(), database.getPort(), database.getDatabaseName(),
				database.getUserName(), database.getPassword());
	}

	public boolean testConnection(Database database, String query) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DatabaseConnectionException, InvalidQueryException {
		DatabaseConnection databaseConnection = getConnection(database.getConnectionType());
		Map<String, Column> map = databaseConnection.getHeaders(query, database.getIp(), database.getPort(), database.getDatabaseName(),
				database.getUserName(), database.getPassword());
		return !map.isEmpty();
	}

	// Create database connection:
	private DatabaseConnection getConnection(String connectionClass)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String connectionPackage = DatabaseConnection.class.getPackage().getName() + ".";
		Class<?> clazz = Class.forName(connectionPackage + connectionClass);
		return (DatabaseConnection) clazz.getDeclaredConstructor().newInstance();
	}

	private DatabaseConnection getConnection(Query query)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return getConnection(query.getDatabase().getConnectionType());
	}

	// Load Headers:
	public Map<String, Column> loadHeaders(long idDatabase, String query) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DatabaseConnectionException, InvalidQueryException {
		Optional<Database> database = databaseRepository.findById(idDatabase);
		if (!database.isPresent())
			throw new DatabaseConnectionException("Database não localizada");
		return loadHeaders(database.get(), query);
	}

	public Map<String, Column> loadHeaders(long idQuery) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, DatabaseConnectionException, InvalidQueryException {
		Optional<Query> query = findById(idQuery);
		if (!query.isPresent())
			throw new InvalidQueryException("Query não localizada na base de dados");

		return loadHeaders(query.get());
	}

	public Map<String, Column> loadHeaders(Query query) throws DatabaseConnectionException, InvalidQueryException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return loadHeaders(getConnection(query), query);
	}

	private Map<String, Column> loadHeaders(DatabaseConnection databaseConnection, Query query)
			throws DatabaseConnectionException, InvalidQueryException {
		Database database = query.getDatabase();
		return databaseConnection.getHeaders(query.getQueryLine(), database.getIp(), database.getPort(),
				database.getDatabaseName(), database.getUserName(), database.getPassword());
	}

	private Map<String, Column> loadHeaders(Database database, String query) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DatabaseConnectionException, InvalidQueryException {
		DatabaseConnection databaseConnection = getConnection(database.getConnectionType());
		return databaseConnection.getHeaders(query, database.getIp(), database.getPort(), database.getDatabaseName(),
				database.getUserName(), database.getPassword());
	}
	
	public String getQueryPreview(String queryLine) {
		if(!queryLine.toLowerCase().contains("limit")) {
			if(!queryLine.endsWith(";")) {
				queryLine = queryLine.concat("limit 10");
			} else {
				queryLine = (queryLine.replace(";", " limit 10;"));
			}
		} else {
			queryLine = queryLine.substring(0, queryLine.indexOf("limit")).concat("limit 10;");
		}
		return queryLine;
	}
	
}
