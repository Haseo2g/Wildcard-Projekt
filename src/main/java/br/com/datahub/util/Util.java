package br.com.datahub.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.datahub.entity.connection.DatabaseConnection;
import br.com.datahub.entity.connection.DefaultConnection;

public class Util {

	/**
	 * Método responsável por validar o tipo de conexão recebido no momento do
	 * cadastro da database
	 * 
	 * @param connectionType nome da conexão
	 * @return true caso exista uma conexão que extenda do database connection
	 */
	public boolean connectionTypeIsValid(String connectionType) {
		List<String> connections = getAllConnectionType();

		for (String cls : connections) {
			if (cls.equals(connectionType))
				return true;
		}
		return false;
	}

	/**
	 * This method is responsible for getting all classes that implements
	 * {@link DatabaseConnection}
	 * 
	 * @return all classes implementing {@link DatabaseConnection}
	 */
	public List<String> getAllConnectionType() {
		String packageName = DatabaseConnection.class.getPackage().getName();
		List<String> connections = new ArrayList<>();

		Reflections reflections = new Reflections(packageName);

		Set<Class<? extends DatabaseConnection>> allClasses = reflections.getSubTypesOf(DatabaseConnection.class);

		for (Class<? extends DatabaseConnection> cls : allClasses) {
			if (cls != DefaultConnection.class)
				connections.add(cls.getSimpleName());
		}
		return connections;
	}

	/**
	 * Method responsible for creating a alert with a message and the alert color
	 * 
	 * @param redirectAttributes
	 * @param response
	 * @return a RedirectAttributes with message and alert attributes
	 */
	public RedirectAttributes returnAlert(RedirectAttributes redirectAttributes, ResponseEntity<?> response) {
		if (response.getStatusCode().is2xxSuccessful())
			redirectAttributes.addFlashAttribute("message", response.getBody()).addFlashAttribute("status_alert",
					"alert alert-success alert-dismissible");
		else
			redirectAttributes.addFlashAttribute("message", response.getBody()).addFlashAttribute("status_alert",
					"alert alert-danger alert-dismissible");
		return redirectAttributes;
	}

	public String getUpdateString(Object object1, Object object2)
			throws IllegalArgumentException, IllegalAccessException {
		
		if (!object1.getClass().equals(object2.getClass()))
			return null;

		Field[] fields = object1.getClass().getDeclaredFields();

		String dif = "[";

		// Fields
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (!field.getName().toLowerCase().equals("password")) {
					Object value1 = field.get(object1);
					Object value2 = field.get(object2);
					if (!value1.equals(value2))
						dif += String.format(" {%s => %s} ", value1, value2);
				}
				field.setAccessible(false);
			} catch (Exception e) {
			}
		}
		if (dif.equals("["))
			return null;
		return dif += "]";
	}
}