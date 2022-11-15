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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.datahub.entity.Database;
import br.com.datahub.entity.Query;
import br.com.datahub.entity.enums.Action;
import br.com.datahub.service.AuditService;
import br.com.datahub.service.DatabaseService;
import br.com.datahub.service.QueryService;
import br.com.datahub.util.Util;
import javassist.NotFoundException;

@RestController
@RequestMapping(value = "/api/query")
public class QueryController {
    private static final Logger log = LogManager.getLogger(QueryController.class);

	private final String AUDIT_OBJECT = "consulta";

	@Autowired
	QueryService queryService;

	@Autowired
	DatabaseService databaseService;

	@Autowired
	ExceptionHandler exceptionHandler;

	@Autowired
	AuditService auditService;

	/**
	 * Method responsible for executing a query, if it works list all data from this
	 * query
	 * 
	 * @param idQuery
	 * @return A ResponseEntity with a status code and a list
	 */
	@RequestMapping(value = "/execute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> execute(@RequestParam(required = true) long idQuery) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			Optional<Query> query = queryService.findById(idQuery);
			if (!query.isPresent())
				throw new NotFoundException("Não foi possivel encontrar a query");

			List<List<String>> lista = queryService.loadList(idQuery);
			log.info(String.format("Lista carregada com sucesso!"));

			log.info("Lista carregada: " + lista);

			return new ResponseEntity<List<List<String>>>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for inserting a new query
	 * 
	 * @param query
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insert(Query query) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			log.info("Query recebida: " + query.toString());

			List<Query> queries = queryService.findAll();
			log.info(String.format("Queries encontradas"));
			for (Query tempQuery : queries) {
				if (tempQuery.equals(query)) {
					throw new InstanceAlreadyExistsException("Não foi possível adicionar a query. Query já existente");
				}
			}

			queryService.save(query);
			String message = "A query foi inserida com sucesso!";

			log.info(message);

			auditService.save(Action.CREATE, AUDIT_OBJECT, query.toString());
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for updating a existing query
	 * 
	 * @param query
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(Query query) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			log.info("Query recebida: " + query.toString());

			Optional<Query> tempQuery = queryService.findById(query.getId());
			if (!tempQuery.isPresent()) {
				throw new NotFoundException("Não foi possivel encontrar a query");
			}
			Query auditQuery = new Query(tempQuery.get());

			queryService.save(query);
			log.info(String.format("A query foi atualizada com sucesso!"));

			String message = "A query foi atualizada com sucesso!";

			try {
				Util util = new Util();
				String auditMessage = util.getUpdateString(auditQuery, query);
				if (auditMessage != null)
					auditService.save(Action.UPDATE, AUDIT_OBJECT, auditQuery.getName(), auditMessage);
				else
					throw new Exception();
			} catch (Exception e) {
				log.error("Falha ao registrar auditoria de atualização para a query. Alterações realizadas: "
						+ tempQuery.get().toString() + " -> " + query.toString());
			}
			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for deleting a existing query
	 * 
	 * @param idQuery
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@RequestParam(value = "idQuery") long idQuery) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			log.info("Id da query recebida para remoção: " + idQuery);

			Optional<Query> query = queryService.findById(idQuery);
			if (!query.isPresent()) {
				throw new NotFoundException("Não foi possivel deletar a query");
			}

			log.info("Query recebida para remoção: " + query.get().toString());

			queryService.deleteById(idQuery);
			log.info(String.format("A query foi removida com sucesso!"));

			String message = "A query foi removida com sucesso!";

			auditService.save(Action.DELETE, AUDIT_OBJECT, query.get().toString());
			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for listing all queries available
	 * 
	 * @return A ResponseEntity with a status code and a list of queries
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list() {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			List<Query> queries = queryService.findAll();
			log.info(String.format("Queries encontradas com sucesso"));

			log.info("Lista encontrada: " + queries.toString());

			return ResponseEntity.ok().body(queries);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * method responsible for validating a existing query, if it works list all data
	 * 
	 * @param idQuery
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	public ResponseEntity<?> validate(@RequestParam(required = true) long idQuery) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			Optional<Query> query = queryService.findById(idQuery);
			if (!query.isPresent())
				return ResponseEntity.badRequest().body("Query não localizada");

			queryService.loadHeaders(idQuery);
			log.info("Query validada com sucesso!");
			log.info("Query: " + query.toString());

			return ResponseEntity.ok("Query realizada com sucesso!");
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for testing a query, if it is a valid one
	 * 
	 * @param idQuery
	 * @return A ResponseEntity with a status code and a list
	 */
	@RequestMapping(value = "/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> test(@ModelAttribute Query query) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			Database database = query.getDatabase();

			boolean b = queryService.testConnection(database, query.getQueryLine());
			if (b)
				return ResponseEntity.ok("Teste executado com sucesso!");
			else
				return ResponseEntity.badRequest()
						.body("Falha ao executar a query. Verifique as informações e tente novamente!");
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

}
