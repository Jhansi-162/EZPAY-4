
package com.ezpay.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ezpay.user.entity.User;
import com.ezpay.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();

        user.setId(1L);
        user.setName("Thanuja");
        user.setEmail("thanuja@gmail.com");
        user.setMobileNumber("9876543210");
        user.setPassword("Thanuja@123");
        user.setAddress("Vijayawada");
        user.setProfilePicture("thanuja.jpg");
        user.setVerified(true);
    }

    @Test
    public void testRegisterUser() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        when(userRepository.findByMobileNumber(user.getMobileNumber()))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("Thanuja", result.getName());
        assertEquals("thanuja@gmail.com", result.getEmail());
        assertEquals("9876543210", result.getMobileNumber());
        assertNotNull(result.getVerificationCode());
        assertEquals(6, result.getVerificationCode().length());
        assertFalse(result.isVerified());

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegisterUserDuplicateEmail() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(user));

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUserDuplicateMobile() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        when(userRepository.findByMobileNumber(user.getMobileNumber()))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(user));

        assertEquals("Mobile number already registered",
                exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testVerifyUser() {
        user.setVerified(false);
        user.setVerificationCode("123456");

        when(userRepository.findByEmail("thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.verifyUser(
                "thanuja@gmail.com", "123456");

        assertNotNull(result);
        assertTrue(result.isVerified());
        assertNull(result.getVerificationCode());
        verify(userRepository).save(user);
    }

    @Test
    public void testVerifyUserInvalidCode() {
        user.setVerified(false);
        user.setVerificationCode("123456");

        when(userRepository.findByEmail("thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.verifyUser(
                        "thanuja@gmail.com", "999999"));

        assertEquals("Invalid verification code",
                exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testVerifyUserNotFound() {
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.verifyUser(
                        "unknown@gmail.com", "123456"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testLoginUser() {
        user.setVerified(true);

        when(userRepository.findByEmailOrMobileNumber(
                "thanuja@gmail.com", "thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        User result = userService.loginUser(
                "thanuja@gmail.com", "Thanuja@123");

        assertNotNull(result);
        assertEquals("Thanuja", result.getName());
        assertEquals("thanuja@gmail.com", result.getEmail());
    }

    @Test
    public void testLoginUserInvalidPassword() {
        user.setVerified(true);

        when(userRepository.findByEmailOrMobileNumber(
                "thanuja@gmail.com", "thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(
                        "thanuja@gmail.com", "WrongPassword"));

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    public void testLoginUserNotVerified() {
        user.setVerified(false);

        when(userRepository.findByEmailOrMobileNumber(
                "thanuja@gmail.com", "thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(
                        "thanuja@gmail.com", "Thanuja@123"));

        assertEquals("Please verify your account first",
                exception.getMessage());
    }

    @Test
    public void testLoginUserNotFound() {
        when(userRepository.findByEmailOrMobileNumber(
                "unknown@gmail.com", "unknown@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(
                        "unknown@gmail.com", "Thanuja@123"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetProfile() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getProfile(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Thanuja", result.getName());
        assertEquals("thanuja@gmail.com", result.getEmail());
    }

    @Test
    public void testGetProfileNotFound() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getProfile(100L));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testUpdateProfile() {
        User updatedUser = new User();
        updatedUser.setName("Thanuja Updated");
        updatedUser.setAddress("Hyderabad");
        updatedUser.setProfilePicture("new-picture.jpg");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateProfile(1L, updatedUser);

        assertNotNull(result);
        assertEquals("Thanuja Updated", result.getName());
        assertEquals("Hyderabad", result.getAddress());
        assertEquals("new-picture.jpg", result.getProfilePicture());

        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateProfileNotFound() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        User updatedUser = new User();
        updatedUser.setName("Updated");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.updateProfile(100L, updatedUser));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testForgotPassword() {
        when(userRepository.findByEmailOrMobileNumber(
                "thanuja@gmail.com", "thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = userService.forgotPassword("thanuja@gmail.com");

        assertEquals(
                "Verification code generated. Check Eclipse Console for testing.",
                result);

        assertNotNull(user.getVerificationCode());
        assertEquals(6, user.getVerificationCode().length());

        verify(userRepository).save(user);
    }

    @Test
    public void testForgotPasswordNotFound() {
        when(userRepository.findByEmailOrMobileNumber(
                "unknown@gmail.com", "unknown@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.forgotPassword("unknown@gmail.com"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testResetPassword() {
        user.setVerificationCode("123456");

        when(userRepository.findByEmailOrMobileNumber(
                "thanuja@gmail.com", "thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.resetPassword(
                "thanuja@gmail.com", "123456", "NewPassword@123");

        assertNotNull(result);
        assertEquals("NewPassword@123", result.getPassword());
        assertNull(result.getVerificationCode());

        verify(userRepository).save(user);
    }

    @Test
    public void testResetPasswordInvalidCode() {
        user.setVerificationCode("123456");

        when(userRepository.findByEmailOrMobileNumber(
                "thanuja@gmail.com", "thanuja@gmail.com"))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.resetPassword(
                        "thanuja@gmail.com", "999999",
                        "NewPassword@123"));

        assertEquals("Invalid verification code",
                exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testResetPasswordUserNotFound() {
        when(userRepository.findByEmailOrMobileNumber(
                "unknown@gmail.com", "unknown@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.resetPassword(
                        "unknown@gmail.com", "123456",
                        "NewPassword@123"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Aishwarya");
        user2.setEmail("aishwarya@gmail.com");
        user2.setMobileNumber("9876543211");

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Thanuja", result.get(0).getName());
        assertEquals("Aishwarya", result.get(1).getName());

        verify(userRepository).findAll();
    }
}
