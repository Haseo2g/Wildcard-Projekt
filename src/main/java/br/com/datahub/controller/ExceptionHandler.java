package br.com.datahub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 
 * @author José Finco Classe para tratamento de Exception
 *         MaxUploadSizeExceededException
 *
 */
@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

	@org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
	public ResponseEntity<String> getExceptionResponse(Exception e) {
		HttpStatus status = getExceptionStatusCode(e);
		String message = getExceptionMessage(e);

		return ResponseEntity.status(status).body(message);
	}

	public String getExceptionMessage(Exception e) {
		String exceptionName = e.getClass().getSimpleName();
		switch (exceptionName) {
		// @formatter:off
			case "ClassNotFoundException": return handleClassNotFoundException(e); 
			case "DatabaseConnectionException": return handleDatabaseConnectionException(e); 
			case "DataIntegrityViolationException": return handleDataIntegrityViolationException(e); 
			case "IllegalAccessException": return handleIllegalAccessException(e); 
			case "IllegalArgumentException": return handleIllegalArgumentException(e); 
			case "InstanceAlreadyExistsException": return handleInstanceAlreadyExistsException(e); 
			case "InstantiationException": return handleInstantiationException(e); 
			case "InvalidQueryException": return handleInvalidQueryException(e); 
			case "InvocationTargetException": return handleInvocationTargetException(e); 
			case "NoSuchElementException": return handleNoSuchElementException(e); 
			case "NoSuchMethodException": return handleNoSuchMethodException(e); 
			case "SecurityException": return handleSecurityException(e); 
			case "SQLException": return handleSQLException(e);
			case "InvalidConnectionTypeException": return handleClassNotFoundException(e); 
			default: return e.getMessage();
			// @formatter:on
		}
	}

	public HttpStatus getExceptionStatusCode(Exception e) {
		String exceptionName = e.getClass().getSimpleName();
		switch (exceptionName) {
		// @formatter:off
			case "ClassNotFoundException": return getClassNotFoundExceptionStatusCode();
			case "DatabaseConnectionException": return getDatabaseConnectionExceptionStatusCode(); 
			case "DataIntegrityViolationException": return getDataIntegrityViolationExceptionStatusCode(); 
			case "IllegalAccessException": return getIllegalAccessExceptionStatusCode(); 
			case "IllegalArgumentException": return getIllegalArgumentExceptionStatusCode(); 
			case "InstanceAlreadyExistsException": return getInstanceAlreadyExistsExceptionStatusCode(); 
			case "InstantiationException": return getInstantiationExceptionStatusCode(); 
			case "InvalidQueryException": return getInvalidQueryExceptionStatusCode(); 
			case "InvocationTargetException": return getInvocationTargetExceptionStatusCode(); 
			case "NoSuchElementException": return getNoSuchElementExceptionStatusCode(); 
			case "NoSuchMethodException": return getNoSuchMethodExceptionStatusCode(); 
			case "SecurityException": return getSecurityExceptionStatusCode(); 
			case "SQLException": return getSQLExceptionStatusCode(); 
			case "InvalidConnectionTypeException": return getSQLExceptionStatusCode(); 
			default: return HttpStatus.BAD_REQUEST;
			// @formatter:on
		}
	}

	// @formatter:off
	public String handleClassNotFoundException(Exception e) { return "Falha na solicitação da requisição. Verifique os dados informados e tente novamente"; }
	public String handleDatabaseConnectionException(Exception e) { return e.getMessage(); }
	public String handleDataIntegrityViolationException(Exception e) { return "Dados faltando ou inválidos. Verifique as informações e tente novamente"; }
	public String handleIllegalAccessException(Exception e) { return e.getMessage(); }
	public String handleIllegalArgumentException(Exception e) { return e.getMessage(); }
	public String handleInstanceAlreadyExistsException(Exception e) { return "Já existe um objeto criado com estas informações."; }
	public String handleInstantiationException(Exception e) { return e.getMessage(); }
	public String handleInvalidQueryException(Exception e) { return "O tipo de pesquisa informado é inválido. Verifique a busca solicitada e tente novamente"; }
	public String handleInvocationTargetException(Exception e) { return e.getMessage(); }
	public String handleNoSuchElementException(Exception e) { return "O elemento solicitado não existe"; }
	public String handleNoSuchMethodException(Exception e) { return "Ocorreu algum problema na sua requisição. Verifique os valores informados, ou tente entrar em contato com a 365TI"; }
	public String handleNotFoundException(Exception e) { return "Não foi localizado o elemento informado"; }
	public String handleSecurityException(Exception e) { return e.getMessage(); }
	public String handleSQLException(Exception e) { return  (e.getMessage().contains("Access denied")) ? "Falha ao se conectar com a base de dados. Acesso negado" : e.getMessage(); }

	public HttpStatus getClassNotFoundExceptionStatusCode() { return HttpStatus.INTERNAL_SERVER_ERROR; }
	public HttpStatus getDatabaseConnectionExceptionStatusCode() { return HttpStatus.BAD_REQUEST; }
	public HttpStatus getDataIntegrityViolationExceptionStatusCode() { return HttpStatus.BAD_REQUEST; }
	public HttpStatus getIllegalAccessExceptionStatusCode() { return HttpStatus.BAD_REQUEST; }
	public HttpStatus getIllegalArgumentExceptionStatusCode() { return HttpStatus.INTERNAL_SERVER_ERROR; }
	public HttpStatus getInstanceAlreadyExistsExceptionStatusCode() { return HttpStatus.BAD_REQUEST; }
	public HttpStatus getInstantiationExceptionStatusCode() { return HttpStatus.INTERNAL_SERVER_ERROR; }
	public HttpStatus getInvalidQueryExceptionStatusCode() { return HttpStatus.BAD_REQUEST; }
	public HttpStatus getInvocationTargetExceptionStatusCode() { return HttpStatus.INTERNAL_SERVER_ERROR; }
	public HttpStatus getNoSuchElementExceptionStatusCode() { return HttpStatus.NOT_FOUND; }
	public HttpStatus getNoSuchMethodExceptionStatusCode() { return HttpStatus.INTERNAL_SERVER_ERROR; }
	public HttpStatus getSecurityExceptionStatusCode() { return HttpStatus.BAD_REQUEST; }
	public HttpStatus getSQLExceptionStatusCode() { return HttpStatus.BAD_REQUEST; }
	// @formatter:on
}