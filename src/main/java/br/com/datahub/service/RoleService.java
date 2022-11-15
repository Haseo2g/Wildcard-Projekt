package br.com.datahub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datahub.entity.Role;
import br.com.datahub.repository.RoleRepository;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	public Optional<Role> findById(long id) {
		return roleRepository.findById(id);
	}
	
	public Optional<Role> findByName(String name) {
		return roleRepository.findByName(name);
	}
	
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

}
