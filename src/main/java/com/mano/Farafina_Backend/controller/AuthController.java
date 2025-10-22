package com.mano.Farafina_Backend.controller;

import com.mano.Farafina_Backend.entity.User;
import com.mano.Farafina_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

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
        user.setPassword(password); // TODO: Hash password later
        user.setCountry(country);

        // Save to database
        userRepository.save(user);

        response.put("status", "success");
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