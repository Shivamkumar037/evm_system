package com.votingsystem.Voting.System;

import com.votingsystem.Voting.System.Dto.Request.SignupRequestDto;
import com.votingsystem.Voting.System.entity.Authority;
import com.votingsystem.Voting.System.entity.User;
import com.votingsystem.Voting.System.entity.type.Role;
import com.votingsystem.Voting.System.reposetory.UserRepo;
import com.votingsystem.Voting.System.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

@SpringBootApplication
public class VotingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingSystemApplication.class, args);
	}
    @Bean
    public CommandLineRunner defaultconfig(UserRepo userService, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. ADMIN Create Karein
            createSystemUser(passwordEncoder,userService, "admin@gmail.com", "System Admin", "admin@123", Role.ROLE_Admin);

            // 2. MEMBER Create Karein
            createSystemUser(passwordEncoder,userService, "member@gmail.com", "Election Member", "member@123", Role.ROLE_Member);

            // 3. VOTER Create Karein
            createSystemUser(passwordEncoder,userService, "voter@gmail.com", "General Voter", "voter@123", Role.ROLE_Voter);

            System.out.println("✅ [System Check]: Default users initialization complete.");
        };
    }





    private void createSystemUser(PasswordEncoder passwordEncoder ,UserRepo userService, String email, String name, String password, Role role) {
        try {
           User dto = new User();
            dto.setEmail(email);
            dto.setName(name);
            Authority auth = new Authority();
            auth.setRole(role);
            dto.setAuthorities(Set.of(auth));
            dto.setDob(LocalDate.of(2000, 1, 1));
dto.setPassword(passwordEncoder.encode(password));
            userService.save(dto);
        } catch (Exception e) {
            System.out.println("ℹ Skip: User " + email + " already present.");
        }
    }
}