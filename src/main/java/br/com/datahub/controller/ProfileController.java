package br.com.datahub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.com.datahub.entity.Role;
import br.com.datahub.service.UserService;

@Component
public class ProfileController {
	
	@Autowired
	UserService userService;
	
	public String getLoggedUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
	}
	
	public Role getUserRole() {
		String user = getLoggedUsername();
		return userService.findByEmail(user).getRole();
	}
}
