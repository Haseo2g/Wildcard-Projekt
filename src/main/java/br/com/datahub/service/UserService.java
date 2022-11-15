package br.com.datahub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.datahub.entity.User;
import br.com.datahub.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository usuarioRepository;

	public Optional<User> findById(long id) {
		return usuarioRepository.findById(id);
	}

	public List<User> findAll() {
		return usuarioRepository.findAll();
	}

	public User findByEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}

	public User save(User usuario) {
		return usuarioRepository.save(usuario);
	}

	public void deleteById(long usuarioId) {
		usuarioRepository.deleteById(usuarioId);
	}
}
