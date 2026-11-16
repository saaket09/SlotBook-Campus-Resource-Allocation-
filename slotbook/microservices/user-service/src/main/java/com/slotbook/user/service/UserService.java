package com.slotbook.user.service;

import com.slotbook.user.entity.User;
import com.slotbook.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already registered: " + user.getEmail());
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }
        return userRepository.save(user);
    }

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

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
