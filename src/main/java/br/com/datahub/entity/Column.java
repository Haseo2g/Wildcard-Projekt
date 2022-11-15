package br.com.datahub.entity;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Column {

	String className;
	String label;
	String name;

	public Column(ResultSetMetaData resultSetMetaData, int index) throws SQLException {
		setClassName(resultSetMetaData, index);
		setLabel(resultSetMetaData, index);
		setName(resultSetMetaData, index);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(ResultSetMetaData resultSetMetaData, int index) throws SQLException {
		String className = resultSetMetaData.getColumnClassName(index);
		this.className = className.substring(className.lastIndexOf(".") + 1).toLowerCase();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(ResultSetMetaData resultSetMetaData, int index) throws SQLException {
		this.label = resultSetMetaData.getColumnLabel(index);
	}

	public String getName() {
		return name;
	}

	public void setName(ResultSetMetaData resultSetMetaData, int index) throws SQLException {
		this.name = resultSetMetaData.getColumnName(index);
	}

}
