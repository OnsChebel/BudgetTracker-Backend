package com.ruse.budgettracker.service;

import com.ruse.budgettracker.dto.ProfileUpdateRequest;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (isBlank(user.getUsername()) || isBlank(user.getEmail()) || isBlank(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, email and password are required");
        }
        if (user.getPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters");
        }

        user.setId(null);
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateProfile(User current, ProfileUpdateRequest request) {
        if (request.getFirstName() != null) {
            current.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null) {
            current.setLastName(request.getLastName().trim());
        }
        return userRepository.save(current);
    }

    public User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Logged in user not found"));
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}