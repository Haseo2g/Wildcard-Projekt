package br.com.datahub.util;

import java.util.Optional;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.datahub.entity.Role;
import br.com.datahub.entity.User;
import br.com.datahub.repository.RoleRepository;
import br.com.datahub.repository.UserRepository;

@Configuration
public class InitialConfig {

	// TODO definir nomes das roles para acesso
	// Por ser possível que nomes como "Admin" ou "User" sejam utilizados para outro
	// sistema, possivelmente será uma boa ideia utilizar os nomes seguidos de algum
	// sufixo
	// Exemplo: User_Consultas ou Admin_Consultas

	private final String ROLE1 = "Admin";
	private final String ROLE2 = "User";
	
	/**
	 * This method will create initial setup for this project user and role database
	 * @param usuarioRepository
	 * @param roleRepository
	 * @return
	 */
	@Bean
	public ApplicationRunner initializer(UserRepository usuarioRepository, RoleRepository roleRepository) {
		return args -> {
			if (!roleRepository.findByName(ROLE1).isPresent()) {
				Role role = new Role();
				role.setId(1);
				role.setName(ROLE1);
				role.setDescription("Administrador");
				roleRepository.save(role);
			}

			if (!roleRepository.findByName(ROLE2).isPresent()) {
				Role role = new Role();
				role.setId(2);
				role.setName(ROLE2);
				role.setDescription("Usuário de consultas");
				roleRepository.save(role);
			}

			if (usuarioRepository.findByEmail("admin@sistema") == null) {
				User user = new User();
				user.setEmail("admin@sistema");
				user.setName("Admin");
				user.setEncodedPassword("$2a$10$uL3pUrPe4qfnWeLVO.3K2OA1vyhX.N..u85pN6N58aYbJwJu.po12");
				Optional<Role> role = roleRepository.findById(1L);
				user.setRole(role.get());
				usuarioRepository.save(user);
			}

			if (usuarioRepository.findByEmail("usuario@sistema") == null) {
				User user = new User();
				user.setEmail("usuario@sistema");
				user.setName("Usuario");
				user.setEncodedPassword("$2a$10$sZf3fliq7apVcA2n9E8cvebPJmesY59b1ixG5SIrWr3YD1Jc14faa");
				Optional<Role> role = roleRepository.findById(2L);
				user.setRole(role.get());
				usuarioRepository.save(user);
			}
		};
	}
}
