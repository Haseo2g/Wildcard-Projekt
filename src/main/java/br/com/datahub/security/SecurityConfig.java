package br.com.datahub.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import br.com.datahub.service.CustomUserDetailService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailService customUserDetailService;

// formatter:off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers("/images/**", "/css/**", "/js/**", "/configuration/ui",
						"/configuration/security", "/webjars/**").permitAll()
				.antMatchers("/home", "/query/filter", "/query/list", "/api/query/validate", "/query/execute").authenticated()
				.antMatchers("/audit/list", "/user/**").hasAnyAuthority("Admin")
				.antMatchers("/api/**", "/database/**", "/query/**").hasAuthority("Admin")
				.anyRequest().authenticated()
			.and()
				.exceptionHandling()
				.accessDeniedHandler(new AccessDeniedHandler() {

					@Override
					public void handle(HttpServletRequest request, HttpServletResponse response,
							AccessDeniedException accessDeniedException) throws IOException, ServletException {

						Authentication auth = SecurityContextHolder.getContext().getAuthentication();
						String returnPage = "/login?notAccess=true";
						if (auth != null) {
							if (auth.isAuthenticated())
								returnPage = "/home?notAccess=true";
						}
						request.getRequestDispatcher(returnPage).forward(request, response);
					}
				})
				.and()
					.formLogin()
					.loginPage("/login")
					.usernameParameter("email")
					.permitAll()
					.defaultSuccessUrl("/home", true)
					.failureUrl("/login?error=true")
				.and()
					.logout()
					.logoutUrl("/logout")
					.logoutSuccessUrl("/login?logout=true")
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID")
					.permitAll()
				.and()
					.httpBasic();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
	}
}