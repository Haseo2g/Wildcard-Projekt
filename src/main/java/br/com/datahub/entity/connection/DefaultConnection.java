package br.com.datahub.entity.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.datahub.controller.ExceptionHandler;
import br.com.datahub.entity.Column;
import br.com.datahub.entity.exception.DatabaseConnectionException;
import br.com.datahub.entity.exception.InvalidQueryException;

public abstract class DefaultConnection implements DatabaseConnection {

	Logger log = LogManager.getLogger(this.getClass());

	/**
	 * Método responsável por criar uma conexão com a database
	 * 
	 * @param databaseIp
	 * @param databasePort
	 * @param databaseName
	 * @return
	 */
	abstract String getConnectionString(String databaseIp, String databasePort, String databaseName);

	abstract void loadDriver();

	/**
	 * Método responsável por retornar a tabela específicada e seu conteúdo
	 */
	@Override
	public List<List<String>> getResultList(String query, String databaseIp, String databasePort, String databaseName,
			String user, String password) throws DatabaseConnectionException, InvalidQueryException {
		validateSelectQuery(query);
		List<List<String>> lista = new ArrayList<>();

		String connectionString = getConnectionString(databaseIp, databasePort, databaseName);

		try (Connection connection = DriverManager.getConnection(connectionString, user, password);
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			List<String> listaStr = new ArrayList<>();

			// Percorre as linhas da pesquisa
			while (rs.next()) {
				listaStr = new ArrayList<>();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					listaStr.add(rs.getString(i));
				}
				lista.add(listaStr);
			}

		} catch (SQLException e) {
			ExceptionHandler exceptionHandler = new ExceptionHandler();
			throw new DatabaseConnectionException(exceptionHandler.handleSQLException(e));
		}
		return lista;
	}

	@Override
	public Map<String, Column> getHeaders(String query, String databaseIp, String databasePort, String databaseName,
			String user, String password) throws DatabaseConnectionException, InvalidQueryException {
		validateSelectQuery(query);

		String connectionString = getConnectionString(databaseIp, databasePort, databaseName);
		Map<String, Column> listaStr = new LinkedHashMap<>();

		if (!query.toLowerCase().contains(" limit"))
			query = query.replace(";", " limit 1;");

		try (Connection connection = DriverManager.getConnection(connectionString, user, password);
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				String className = resultSetMetaData.getColumnClassName(i);
				className = className.substring(className.lastIndexOf(".") + 1).toLowerCase();
				listaStr.put(resultSetMetaData.getColumnLabel(i), new Column(resultSetMetaData, i));
			}
		} catch (SQLException e) {
			ExceptionHandler exceptionHandler = new ExceptionHandler();
			throw new DatabaseConnectionException(exceptionHandler.handleSQLException(e));
		}
		return listaStr;
	}

	/**
	 * Método responsável por validar se a query é de select. Neste caso, a
	 * aplicação continuará seguindo normalmente.
	 */
	@Override
	public boolean validateSelectQuery(String query) throws InvalidQueryException {
		// Remove os espaços e coloca a query em lower case para evitar problemas com
		// letras em maiúsculo
		query = query.trim().toLowerCase();
		log.info("Query recebida: [" + query + "]");

		// Valida que a query é um Select
		if (query.startsWith("select")) {
			log.info("Query validada com sucesso!");
			return true;
		}

		// Invoca exception caso a query não seja um select
		log.error("A query informada não é válida!");
		throw new InvalidQueryException(query,
				"A query informada não é válida! Apenas é permitido queries com a função de Select.");
	}

	@Override
	public boolean validateConnection(String databaseIp, String databasePort, String databaseName, String user,
			String password) {
		log.info(String.format("Testando a conexão com o banco de dados %s [%s:%s] através do usuário %s", databaseName,
				databaseIp, databasePort, user));
		String connectStr = getConnectionString(databaseIp, databasePort, databaseName);
		try (Connection con = DriverManager.getConnection(connectStr, user, password)) {
			log.info("Conexão estabelecida com sucesso!");
			return true;
		} catch (Exception e) {
			log.error("Não foi possível estabelecer a conexão com o banco de dados");
			log.debug(e.getMessage(), e);
			return false;
		}
	}
}
