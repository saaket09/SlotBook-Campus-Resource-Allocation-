package com.slotbook.service;

import com.slotbook.entity.User;
import com.slotbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new user. Checks for duplicate email.
     */
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already registered: " + user.getEmail());
        }
        // Default role to STUDENT if not specified
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }
        return userRepository.save(user);
    }

    /**
     * Login with email and password.
     * Returns the user if credentials match, throws exception otherwise.
     * (Plain-text password comparison — sufficient for a course project.)
     */
    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalStateException("No account found with email: " + email);
        }
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            throw new IllegalStateException("Invalid password");
        }
        return user;
    }

    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
