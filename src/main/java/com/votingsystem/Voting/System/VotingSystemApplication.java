package com.votingsystem.Voting.System;

import com.votingsystem.Voting.System.entity.Authority;
import com.votingsystem.Voting.System.entity.User;
import com.votingsystem.Voting.System.entity.type.Role;
import com.votingsystem.Voting.System.reposetory.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class VotingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VotingSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner defaultconfig(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Ek Unique ADMIN Create Karein
            createSystemUser(passwordEncoder, userRepo, "admin@gmail.com", "Main Admin", "admin@123", Role.ROLE_Admin);

            // 2. Ek Unique MEMBER Create Karein
            createSystemUser(passwordEncoder, userRepo, "member@gmail.com", "Election Officer", "member@123", Role.ROLE_Member);

            // 3. 100 VOTERS Create Karein using a loop
            for (int i = 1; i <= 100; i++) {
                String voterEmail = "voter" + i + "@gmail.com";
                String voterName = "Voter " + i;
                // Sabka password 'voter@123' rakha hai Testing asaan karne ke liye
                createSystemUser(passwordEncoder, userRepo, voterEmail, voterName, "voter@123", Role.ROLE_Voter);
            }

            System.out.println("✅ [System Initialization]: 1 Admin, 1 Member and 100 Voters processed successfully.");
        };
    }

    private void createSystemUser(PasswordEncoder passwordEncoder, UserRepo userRepo, String email, String name, String password, Role role) {
        try {
            // Check karein user pehle se hai ya nahi taaki console clean rahe
            if (userRepo.findByEmail(email).isPresent()) {
                return; // Agar hai toh kuch mat karo
            }

            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setDob(LocalDate.of(2000, 1, 1));
            user.setPassword(passwordEncoder.encode(password));
            user.setVerified(true); // Startup users ko by default verify rakhein
            user.setIsvoted(false);

            // Authority set karna
            Authority auth = new Authority();
            auth.setRole(role);
            
            // HashSet use karein taaki collection mutable rahe
            Set<Authority> authorities = new HashSet<>();
            authorities.add(auth);
            user.setAuthorities(authorities);

            userRepo.save(user);
            
            // Sirf Admin/Member ke liye message dikhao, 100 voters ke liye console bhar jayega
            if (role != Role.ROLE_Voter) {
                System.out.println("✓ Created " + role + ": " + email);
            }
        } catch (Exception e) {
            // Error handling agar email unique constraint hit ho
        }
    }
}
