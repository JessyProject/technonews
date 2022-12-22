package com.jessy.technonews;

import com.jessy.technonews.domain.Role;
import com.jessy.technonews.domain.User;
import com.jessy.technonews.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class TechnonewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechnonewsApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			Role role_user = new Role(null, "ROLE_USER", new ArrayList<>());
			Role role_admin = new Role(null, "ROLE_ADMIN", new ArrayList<>());
			Role role_super_admin = new Role(null, "ROLE_SUPER_ADMIN", new ArrayList<>());
			userService.saveRole(role_user);
			userService.saveRole(role_admin);
			userService.saveRole(role_super_admin);

			userService.saveUser(new User(null, "che pas", "jpop@ok.fr", "password", false, new ArrayList<>()));
			userService.saveUser(new User(null, "john", "jpp@ok.fr", "password",  true, new ArrayList<>()));
			userService.saveUser(new User(null, "frudo", "jppp@ok.fr", "password",  true, new ArrayList<>()));
			userService.saveUser(new User(null, "maca", "jppppp@ok.fr", "password",  true, new ArrayList<>()));
			userService.saveUser(new User(null, "ezehkiel", "jppppppp@ok.fr", "password",  true, new ArrayList<>()));

			userService.addRoleToUser("che pas", "ROLE_USER");
			userService.addRoleToUser("john", "ROLE_USER");
			userService.addRoleToUser("maca", "ROLE_USER");
			userService.addRoleToUser("frudo", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("ezehkiel", "ROLE_ADMIN");
		};
	}
}
