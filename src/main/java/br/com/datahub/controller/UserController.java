package br.com.datahub.controller;

import java.util.List;
import java.util.Optional;

import javax.management.InstanceAlreadyExistsException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.datahub.entity.User;
import br.com.datahub.entity.enums.Action;
import br.com.datahub.service.AuditService;
import br.com.datahub.service.RoleService;
import br.com.datahub.service.UserService;
import br.com.datahub.util.Util;
import javassist.NotFoundException;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LogManager.getLogger(UserController.class);
	private final String AUDIT_OBJECT = "usuário";

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	ExceptionHandler exceptionHandler;

	@Autowired
	AuditService auditService;

	/**
	 * Method responsible for inserting a new user
	 * 
	 * @param user
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insert(User user) {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			log.info("Usuario recebido: " + user.toString());

			List<User> users = userService.findAll();
			for (User tempUsuario : users) {
				if (tempUsuario.equals(user)) {
					throw new InstanceAlreadyExistsException(
							"Não foi possível adicionar o usuário. Usuário já existente");
				}
			}

			userService.save(user);
			log.info(String.format("Usuario [%s] foi adicionado com sucesso", user.getName()));

			String message = String.format("O usuário [%s] foi adicionado com sucesso!", user.getName());
			auditService.save(Action.CREATE, AUDIT_OBJECT, user.toString());

			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for updating a existing user
	 * 
	 * @param user
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(User user) {

		try {
			if (user.getId() == 1) {
				throw new AccessException("O usuário não pode ser alterado");
			}

			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			log.info("Usuario recebido: " + user.toString());

			long id = user.getId();
			User tempUser = new User();
			tempUser = userService.findById(id).get();

			if (tempUser == null) {
				throw new NotFoundException("Não foi possivel encontrar o usuário");
			}
			User auditUser = new User(tempUser);

			user = userService.save(user);
			String message = String.format("O usuário [%s] foi atualizado com sucesso!", tempUser.getName());

			log.info(message);
			try {
				Util util = new Util();
				String auditMessage = util.getUpdateString(auditUser, user);
				if (auditMessage != null)
					auditService.save(Action.UPDATE, AUDIT_OBJECT, auditUser.getName(), auditMessage);
				else
					throw new Exception();
			} catch (Exception e) {
				log.error("Falha ao registrar auditoria de atualização para o usuário. Alterações realizadas: "
						+ auditUser.toString() + " -> " + user.toString());
			}

			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for deleting a existing user
	 * 
	 * @param idUser
	 * @return A ResponseEntity with a status code and a message
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@RequestParam(value = "idUser") long idUser) {

		try {
			if (idUser == 1) {
				throw new AccessException("O usuário não pode ser excluído");
			}

			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			Optional<User> user = userService.findById(idUser);
			if (!user.isPresent()) {
				throw new NotFoundException("Não foi possivel deletar o usuário");
			}

			log.info("Usuário recebido para remoção: " + user.get().toString());

			userService.deleteById(idUser);
			log.info(String.format("O usuário [%s] foi removido com sucesso!", user.get().getName()));

			String message = String.format("O usuário [%s] foi removido com sucesso!", user.get().getName());
			auditService.save(Action.DELETE, AUDIT_OBJECT, user.get().toString());

			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}

	/**
	 * Method responsible for listing all users available
	 * 
	 * @return A ResponseEntity with a status code and a list of users
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list() {

		try {
			// Nome do método
			String metodo = new Object() {
			}.getClass().getEnclosingMethod().getName();

			log.info(String.format("Iniciada a execução do método [%s]", metodo));

			List<User> users = userService.findAll();
			log.info(String.format("Usuários encontrados com sucesso"));

			log.info("Lista encontrada: " + users.toString());

			return ResponseEntity.ok().body(users);
		} catch (Exception e) {
			return exceptionHandler.getExceptionResponse(e);
		}
	}
}
