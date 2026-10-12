
package com.ezpay.user.service;

import java.security.SecureRandom;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezpay.user.entity.User;
import com.ezpay.user.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. REGISTER USER
    public User registerUser(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.findByMobileNumber(
                user.getMobileNumber()).isPresent()) {
            throw new RuntimeException("Mobile number already registered");
        }

        String verificationCode = generateVerificationCode();

        user.setVerificationCode(verificationCode);
        user.setVerified(false);

        // Local testing only
        logger.info("Registration verification code: {}", verificationCode);

        return userRepository.save(user);
    }

    // 2. VERIFY USER
    public User verifyUser(String email, String verificationCode) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        if (user.getVerificationCode() == null
                || !user.getVerificationCode().equals(verificationCode)) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setVerified(true);
        user.setVerificationCode(null);

        logger.info("User verification successful");

        return userRepository.save(user);
    }

    // 3. LOGIN USER
    public User loginUser(String login, String password) {

        User user = userRepository
                .findByEmailOrMobileNumber(login, login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException(
                    "Please verify your account first");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        logger.info("User login successful");

        return user;
    }

    // 4. GET USER PROFILE
    @Transactional(readOnly = true)
    public User getProfile(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 5. UPDATE USER PROFILE
    public User updateProfile(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }

        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }

        if (updatedUser.getProfilePicture() != null) {
            existingUser.setProfilePicture(
                    updatedUser.getProfilePicture());
        }

        logger.info("User profile updated");

        return userRepository.save(existingUser);
    }

    // 6. FORGOT PASSWORD
    public String forgotPassword(String emailOrMobile) {

        User user = userRepository
                .findByEmailOrMobileNumber(emailOrMobile, emailOrMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String verificationCode = generateVerificationCode();

        user.setVerificationCode(verificationCode);

        userRepository.save(user);

        // Local testing only
        logger.info("Password reset verification code: {}", verificationCode);

        return "Verification code generated. Check Eclipse Console for testing.";
    }

    // 7. RESET PASSWORD
    public User resetPassword(
            String emailOrMobile,
            String verificationCode,
            String newPassword) {

        User user = userRepository
                .findByEmailOrMobileNumber(emailOrMobile, emailOrMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getVerificationCode() == null
                || !user.getVerificationCode().equals(verificationCode)) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setPassword(newPassword);
        user.setVerificationCode(null);

        logger.info("Password reset successful");

        return userRepository.save(user);
    }

    // 8. GET ALL USERS
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // 9. GENERATE SIX-DIGIT VERIFICATION CODE
    private String generateVerificationCode() {

        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000));
    }
}
