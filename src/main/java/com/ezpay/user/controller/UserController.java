
package com.ezpay.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ezpay.user.entity.User;
import com.ezpay.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. REGISTER USER
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @RequestBody User user) {

        logger.info("POST /api/users/register called");

        User savedUser = userService.registerUser(user);

        return new ResponseEntity<>(
                savedUser,
                HttpStatus.CREATED);
    }

    // 2. VERIFY USER
    @PostMapping("/verify")
    public ResponseEntity<User> verifyUser(
            @RequestParam("email") String email,
            @RequestParam("verificationCode") String verificationCode) {

        logger.info("POST /api/users/verify called");

        User user = userService.verifyUser(
                email,
                verificationCode);

        return ResponseEntity.ok(user);
    }

    // 3. LOGIN USER
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(
            @RequestParam("login") String login,
            @RequestParam("password") String password) {

        logger.info("POST /api/users/login called");

        User user = userService.loginUser(login, password);

        return ResponseEntity.ok(user);
    }

    // 4. GET USER PROFILE
    @GetMapping("/{id}")
    public ResponseEntity<User> getProfile(
            @PathVariable("id") Long id) {

        logger.info("GET /api/users/{} called", id);

        User user = userService.getProfile(id);

        return ResponseEntity.ok(user);
    }

    // 5. UPDATE USER PROFILE
    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(
            @PathVariable("id") Long id,
            @RequestBody User updatedUser) {

        logger.info("PUT /api/users/{} called", id);

        User user = userService.updateProfile(id, updatedUser);

        return ResponseEntity.ok(user);
    }

    // 6. FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestParam("emailOrMobile") String emailOrMobile) {

        logger.info("POST /api/users/forgot-password called");

        String message = userService.forgotPassword(emailOrMobile);

        return ResponseEntity.ok(message);
    }

    // 7. RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<User> resetPassword(
            @RequestParam("emailOrMobile") String emailOrMobile,
            @RequestParam("verificationCode") String verificationCode,
            @RequestParam("newPassword") String newPassword) {

        logger.info("POST /api/users/reset-password called");

        User user = userService.resetPassword(
                emailOrMobile,
                verificationCode,
                newPassword);

        return ResponseEntity.ok(user);
    }

    // 8. GET ALL USERS
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        logger.info("GET /api/users called");

        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }
}
