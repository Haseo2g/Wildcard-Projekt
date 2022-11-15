package br.com.datahub.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datahub.entity.Database;
import br.com.datahub.entity.connection.DatabaseConnection;
import br.com.datahub.entity.exception.DatabaseConnectionException;
import br.com.datahub.entity.exception.InvalidQueryException;
import br.com.datahub.repository.DatabaseRepository;

@Service
public class DatabaseService {

	@Autowired
	DatabaseRepository databaseRepository;

	public List<List<String>> loadList(String databaseConnection, String query) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DatabaseConnectionException, InvalidQueryException {
		String connectionPackage = DatabaseConnection.class.getPackage().getName();
		Class<?> clazz = Class.forName(connectionPackage + databaseConnection);
		DatabaseConnection connection = (DatabaseConnection) clazz.getDeclaredConstructor().newInstance();
		return loadList(connection, query);
	}

	public List<List<String>> loadList(DatabaseConnection databaseConnection, String query)
			throws DatabaseConnectionException, InvalidQueryException {
		return databaseConnection.getResultList(query, query, query, query, query, query);
	}

	public Database save(Database database) {
		return databaseRepository.save(database);
	}

	public Optional<Database> findById(long id) {
		return databaseRepository.findById(id);
	}

	public List<Database> findAll() {
		return databaseRepository.findAll();
	}

	public void deleteById(long databaseId) {
		databaseRepository.deleteById(databaseId);
	}

	public boolean validateConnection(Database database)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String connectionPackage = DatabaseConnection.class.getPackage().getName() + ".";
		Class<?> clazz = Class.forName(connectionPackage + database.getConnectionType());
		DatabaseConnection databaseConnection = (DatabaseConnection) clazz.getDeclaredConstructor().newInstance();
		return databaseConnection.validateConnection(database.getIp(), database.getPort(), database.getDatabaseName(),
				database.getUserName(), database.getPassword());
	}

}
