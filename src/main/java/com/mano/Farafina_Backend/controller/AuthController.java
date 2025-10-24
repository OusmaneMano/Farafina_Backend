package com.mano.Farafina_Backend.controller;

import com.mano.Farafina_Backend.entity.User;
import com.mano.Farafina_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String fullname = request.get("fullname");
        String username = request.get("username");
        String email = request.get("email");
        String phone = request.get("phone");
        String password = request.get("password");
        String country = request.get("country");

        // Check if username exists
        if (userRepository.existsByUsername(username)) {
            response.put("status", "username_taken");
            response.put("suggestions", generateUsernameSuggestions(username));
            return ResponseEntity.ok(response);
        }

        // Check if email exists
        if (userRepository.existsByEmail(email)) {
            response.put("status", "email_taken");
            return ResponseEntity.ok(response);
        }

        // Check if phone exists
        if (userRepository.existsByPhone(phone)) {
            response.put("status", "phone_taken");
            return ResponseEntity.ok(response);
        }

        // Create new user
        User user = new User();
        user.setFullname(fullname);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);

        // Hash the password using BCrypt
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        user.setCountry(country);

        // Save to database
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Account created successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String password = request.get("password");

        // Validate input
        if (username == null || username.trim().isEmpty()) {
            response.put("status", "invalid_input");
            response.put("message", "Username is required");
            return ResponseEntity.ok(response);
        }

        if (password == null || password.trim().isEmpty()) {
            response.put("status", "invalid_input");
            response.put("message", "Password is required");
            return ResponseEntity.ok(response);
        }

        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            response.put("status", "user_not_found");
            response.put("message", "Username not found");
            return ResponseEntity.ok(response);
        }

        User user = userOptional.get();

        // Check password using BCrypt
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("status", "wrong_password");
            response.put("message", "Incorrect password");
            return ResponseEntity.ok(response);
        }

        // Login successful
        response.put("status", "success");
        response.put("message", "Login successful");

        // Return user data (excluding password)
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("fullname", user.getFullname());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("country", user.getCountry());

        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

    // Generate username suggestions
    private List<String> generateUsernameSuggestions(String username) {
        List<String> suggestions = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            String suggestion = username + random.nextInt(1000);
            if (!userRepository.existsByUsername(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }
}