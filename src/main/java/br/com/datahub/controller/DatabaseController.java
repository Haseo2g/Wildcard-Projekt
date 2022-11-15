package br.com.datahub.controller;

import java.util.List;
import java.util.Optional;

import javax.management.InstanceAlreadyExistsException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.datahub.entity.Database;
import br.com.datahub.entity.enums.Action;
import br.com.datahub.service.AuditService;
import br.com.datahub.service.DatabaseService;
import br.com.datahub.util.Util;
import javassist.NotFoundException;

@RestController
@RequestMapping(value = "/api/database")
public class DatabaseController {
    private static final Logger log = LogManager.getLogger(DatabaseController.class);

	private final String AUDIT_OBJECT = "base de dados";

	@Autowired
	DatabaseService databaseService;

	@Autowired
	ExceptionHandler exceptionHandler;

	@Autowired
	AuditService auditService;

	/**
	 * Method responsible for inserting a new database
	 * 
	 * @param database
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insert(@RequestBody @ModelAttribute Database database) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			List<Database> databases = databaseService.findAll();
			log.info("Databases encontradas");
			for (Database tempDatabase : databases) {
				if (tempDatabase.equals(database)) {
					throw new InstanceAlreadyExistsException("Não foi possível adicionar a Database.");
				}
			}

			String dbAlias = database.getAlias();

			databaseService.save(database);
			log.info(String.format("Banco [%s] inserido", dbAlias));

			String message = String.format("A database [%s] foi inserida com sucesso!", dbAlias);
			auditService.save(Action.CREATE, AUDIT_OBJECT, dbAlias);

			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}

	}

	/**
	 * Method responsible for updating a existing database
	 * 
	 * @param database
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody @ModelAttribute Database database) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			log.info("Database recebido: " + database.toString());

			Optional<Database> databaseTemp = databaseService.findById(database.getId());
			if (!databaseTemp.isPresent()) {
				throw new NotFoundException("Não foi possivel atualizar a Database");
			}
			String alias = databaseTemp.get().getAlias();
			if (database.getPassword().equals(""))
				database.setPassword(databaseTemp.get().getPassword());
			Database auditDatabase = new Database(databaseTemp.get());

			databaseService.save(database);
			log.info(String.format("A database [%s] foi atualizada com sucesso", alias));

			String message = String.format("A database [%s] foi atualizado com sucesso!", alias);

			try {
				Util util = new Util();
				String auditMessage = util.getUpdateString(auditDatabase, database);
				if (auditMessage != null)
					auditService.save(Action.UPDATE, AUDIT_OBJECT, auditDatabase.getAlias(), auditMessage);
				else
					throw new Exception();
			} catch (Exception e) {
				log.error("Falha ao registrar auditoria de atualização para a query. Alterações realizadas: "
						+ databaseTemp.get().toString() + " -> " + database.toString());
			}

			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}

	}

	/**
	 * Method responsible for deleting a existing database
	 * 
	 * @param databaseId
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@RequestParam(value = "databaseId") long databaseId) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			Optional<Database> database = databaseService.findById(databaseId);
			if (!database.isPresent()) {
				throw new NotFoundException("Não foi possivel remover a database");
			}

			log.info("Database recebida para remoção: " + database.get().toString());

			String alias = database.get().getAlias();

			log.info(String.format("A database [%s] foi removida com sucesso", alias));
			databaseService.deleteById(databaseId);

			String message = String.format("A database [%s] foi removida com sucesso!", alias);

			auditService.save(Action.DELETE, AUDIT_OBJECT, alias);
			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for listing all databases available
	 * 
	 * @return A ResponseEntity with a list of databases
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list() {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			List<Database> databases = databaseService.findAll();
			log.info("Databases encontradas com sucesso");

			return ResponseEntity.ok().body(databases);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for testing the database connection
	 * 
	 * @param database
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/testConnection", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> testDatabaseConnection(@RequestBody @ModelAttribute Database database) {
		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			log.info("Database recebida para teste: " + database.toString());

			boolean b = databaseService.validateConnection(database);
			if (b)
				return ResponseEntity.ok("Conexão realizada com sucesso");
			else
				return ResponseEntity.badRequest()
						.body("Falha ao realizar a conexão. Verifique os campos e tente novamente");
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	@RequestMapping(value = "/testConnection", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> testDatabaseConnection(
			@RequestParam(required = true, value = "idDatabase") long idDatabase) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			Optional<Database> database = databaseService.findById(idDatabase);
			if (!database.isPresent())
				throw new NotFoundException("Não foi localizado a Database informada");

			log.info("Database recebida para teste: " + database.toString());

			boolean b = databaseService.validateConnection(database.get());
			if (b)
				return ResponseEntity.ok("Conexão realizada com sucesso");
			else
				return ResponseEntity.badRequest()
						.body("Falha ao realizar a conexão. Verifique os campos e tente novamente");
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}
}
