package br.com.datahub.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Query {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@Column(nullable = false)
	String name;

	@Column(nullable = false)
	String description;

	@Column(nullable = false, name = "query_line", length = 4000)
	String queryLine;

	@OneToOne
	@JoinColumn(nullable = false, name = "data_source_id")
	Database database;
	
	public Query() {
	}

	public Query(Query other) {
		this.id = other.id;
		this.name = other.name;
		this.description = other.description;
		this.queryLine = other.queryLine;
		this.database = other.database;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQueryLine() {
		return queryLine;
	}

	public void setQueryLine(String queryLine) {
		if (!queryLine.endsWith(";"))
			queryLine += ";";
		this.queryLine = queryLine;
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	@Override
	public String toString() {
		return "Query [id=" + id + ", name=" + name + ", description=" + description + ", queryLine=" + queryLine + ", database="
				+ database.getDatabaseName() + ", alias= " + database.getAlias() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(queryLine, database);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		return Objects.equals(queryLine, other.queryLine) && Objects.equals(database, other.database);
	}

}
