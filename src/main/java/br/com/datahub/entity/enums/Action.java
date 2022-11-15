package br.com.datahub.entity.enums;

public enum Action {

	CREATE("adicionou"), UPDATE("atualizou"), DELETE("excluiu");

	private final String action;

	Action(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

}
