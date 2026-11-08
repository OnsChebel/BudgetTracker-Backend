package com.ruse.budgettracker.controller;

import com.ruse.budgettracker.dto.ProfileUpdateRequest;
import com.ruse.budgettracker.model.User;
import com.ruse.budgettracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new-user")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/my-profile")
    public User getMyProfile() {
        return userService.getLoggedInUser();
    }

    @PutMapping("/update-user")
    public User updateProfile(@RequestBody ProfileUpdateRequest request) {
        User currentUser = userService.getLoggedInUser();
        return userService.updateProfile(currentUser, request);
    }
}