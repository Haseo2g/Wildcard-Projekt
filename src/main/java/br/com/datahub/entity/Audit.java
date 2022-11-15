package br.com.datahub.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import br.com.datahub.entity.enums.Action;

@Entity
public class Audit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String user;

	@Enumerated(EnumType.STRING)
	private Action action;

	@Column(nullable = false, name = "object_type")
	private String objectType;

	@Column(nullable = false, name = "object", length = 4000)
	private String initialObject;

	@Column(nullable = true, name = "update_values", length = 8000)
	private String updateValues;

	@Column(nullable = false, name = "date_time")
	private LocalDateTime dateTime;

	@Transient
	private String actionDescription;

	@Transient
	private String dateTimeFormatted;

	public Audit() {
	}

	public Audit(String user, Action action, String objectType, String initialObject, String updateValues) {
		setUser(user);
		setAction(action);
		setObjectType(objectType);
		setInitialObject(initialObject);
		setUpdateValues(updateValues);
		setDateTime();
	}

	public Audit(String user, Action action, String objectType, String object) {
		this(user, action, objectType, object, null);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getInitialObject() {
		return initialObject;
	}

	public void setInitialObject(String initialObject) {
		this.initialObject = initialObject;
	}

	public String getUpdateValues() {
		return updateValues;
	}

	public void setUpdateValues(String updateValues) {
		this.updateValues = updateValues;
	}

	public String getActionDescription() {
		if (Action.UPDATE == action)
			actionDescription = String.format("O usuário [%s] %s o/a [%s] %s conforme: %s", user, action.getAction(),
					objectType, initialObject, updateValues);
		else
			actionDescription = String.format("O usuário [%s] %s o/a [%s]: %s", user, action.getAction(), objectType,
					initialObject);
		return actionDescription;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public String getDateTimeFormatted() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return formatter.format(dateTime);
	}

	public void setDateTime() {
		dateTime = LocalDateTime.now();
	}
}
