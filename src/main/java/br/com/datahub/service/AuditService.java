package br.com.datahub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datahub.controller.ProfileController;
import br.com.datahub.entity.Audit;
import br.com.datahub.entity.enums.Action;
import br.com.datahub.repository.AuditRepository;

@Service
public class AuditService {

	@Autowired
	private AuditRepository auditRepository;

	@Autowired
	ProfileController profileController;

	public List<Audit> findAll() {
		return auditRepository.findAll();
	}

	public void save(Action action, String objectType, String initialObject, String updateValues) {
		String user = profileController.getLoggedUsername();
		Audit audit = new Audit(user, action, objectType, initialObject, updateValues);
		auditRepository.save(audit);
	}

	public void save(Action action, String objectType, String initialObject) {
		save(action, objectType, initialObject, null);
	}

}
