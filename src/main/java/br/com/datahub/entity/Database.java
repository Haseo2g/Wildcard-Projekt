package br.com.datahub.entity;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import br.com.datahub.entity.exception.InvalidConnectionTypeException;
import br.com.datahub.util.Util;

@Entity(name = "data_source")
public class Database {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	// Nome visível ao usuário
	@Column(nullable = false)
	String alias;

	// Nome próprio da base de dados
	@Column(nullable = false, name = "database_name")
	String databaseName;

	// Tipo de conexão (Exemplo, SqlConnection, PostgresCollection)
	@Column(nullable = false, name = "connection_type")
	String connectionType;

	// IP do banco
	@Column(nullable = false)
	String ip;

	// Porta do banco
	@Column(nullable = false)
	String port;

	// Usuário de acesso
	@Column(nullable = false, name = "username")
	String userName;

	// Senha de acesso
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(nullable = false)
	String password;

	@JsonIgnore
	@OneToMany(mappedBy = "database", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<Query> queries;

	public Database() {
	}

	public Database(Database other) {
		this.id = other.id;
		this.alias = other.alias;
		this.databaseName = other.databaseName;
		this.connectionType = other.connectionType;
		this.ip = other.ip;
		this.port = other.port;
		this.userName = other.userName;
		this.password = other.password;
		this.queries = other.queries;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getConnectionType() {
		return connectionType;
	}

	/**
	 * Valida se o tipo de conexão passado por parâmetro é válido
	 * 
	 * @param connectionType Nome do tipo de conexão. Exemplo: SqlConnection. Nomes
	 *                       válidos são apenas as classes disponíveis no pacote
	 *                       connection
	 * @throws InvalidConnectionTypeException caso o tipo de conexão seja inválido
	 */
	public void setConnectionType(String connectionType) throws InvalidConnectionTypeException {
		Util util = new Util();
		if (util.connectionTypeIsValid(connectionType))
			this.connectionType = connectionType;
		else
			throw new InvalidConnectionTypeException("O tipo de conexão informado é inválido");
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 
	 * @return Senha descriptografada em base64
	 */
	public String getPassword() {
		try {
			String p = new String(Base64.getDecoder().decode(password.getBytes()));
			StringBuilder builder = new StringBuilder(p);
			for (int i = 0; i < p.length(); i++) {
				builder.setCharAt(i, (char) (p.charAt(i)-i));
			}
			return builder.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Criptografa a senha do banco de dados em base64
	 * 
	 * @param password Senha padrão
	 */
	public void setPassword(String password) {
		StringBuilder builder = new StringBuilder(password);
		for (int i = 0; i < password.length(); i++) {
			builder.setCharAt(i, (char) (password.charAt(i)+i));
		}
		this.password = Base64.getEncoder().encodeToString(builder.toString().getBytes());
	}

	public List<Query> getQueries() {
		return queries;
	}

	public void setQueries(List<Query> queries) {
		this.queries = queries;
	}

	@Override
	public String toString() {
		return "Database [id=" + id + ", alias=" + alias + ", databaseName=" + databaseName + ", connectionType="
				+ connectionType + ", ip=" + ip + ", port=" + port + ", userName=" + userName + ", password=********]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, databaseName, ip, port);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Database other = (Database) obj;
		return Objects.equals(alias, other.alias) && Objects.equals(databaseName, other.databaseName)
				&& Objects.equals(ip, other.ip) && Objects.equals(port, other.port);
	}

}
