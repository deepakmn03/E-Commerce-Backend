package com.e_commerce_backend.user_service.util;

import com.e_commerce_backend.user_service.entity.User;
import com.e_commerce_backend.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PasswordEncoderUtil implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        // Fetch all users from database
        List<User> users = userRepository.findAll();
        
        boolean hasUnencoded = false;
        
        for (User user : users) {
            // Check if password is already encoded (BCrypt hashes start with $2a$, $2b$, or $2y$)
            if (!user.getPassword().startsWith("$2a$") && 
                !user.getPassword().startsWith("$2b$") && 
                !user.getPassword().startsWith("$2y$")) {
                
                System.out.println("Encoding password for user: " + user.getEmail());
                String encodedPassword = encoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                userRepository.save(user);
                hasUnencoded = true;
            }
        }
        
        if (hasUnencoded) {
            System.out.println("✓ All plain text passwords have been encoded with BCrypt!");
        } else {
            System.out.println("✓ All passwords are already BCrypt encoded.");
        }
    }
}
