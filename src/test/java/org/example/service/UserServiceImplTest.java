package org.example.service;

import org.example.data.UserRepository;
import org.example.data.UserRepositoryImpl;
import org.example.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    // Arrange


    @Mock
    UserRepositoryImpl userRepository;
    @Mock
    EmailVerificationServiceImpl emailVerificationService;
    @InjectMocks
    UserServiceImpl userService;
    String firstName;
    String lastName;
    String email;
    String password;
    String repeatPassword;
    User user;

    @BeforeEach
    void beforeEach(){
        // Arrange
        user = new User(firstName, lastName, email, password, repeatPassword);
        firstName = "any name";
        lastName = "any last name";
        email = "test@test.com";
        password = "12345678";
        repeatPassword = "12345678";
    }

    @DisplayName("User object created")
    @Test
    void testCreateUser_whenUserDetailsProvided_returnUserObject() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(true);
        // Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert

        assertNotNull(user, "The user not should be null");
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertNotNull(user.getId());
        verify(userRepository, atLeast(1)).save(any(User.class));

    }

    @DisplayName("User should not be created if passwords are not same")
    @Test
    void testCreateUser_whenPasswordsAreNotSame_returnNull() {
        // Arrange
        String repeatPassword = "12345676";

        // Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert
        assertNull(user, "The user should be null");

    }

    @DisplayName("When FirstName is empty should throws Illigal Arguments Exception")
    @Test
    void testCreateUser_whenFirstNameIsEmpty_throwsIlligalArgumentException() {
        // Arrange
        String firstName = "";

        // Act & Assert
        IllegalArgumentException thrown= Assertions.assertThrows(IllegalArgumentException.class, () -> {
            User user = userService.createUser(firstName, lastName, email, password, repeatPassword);
        });

        assertEquals("User's first name should not be null", thrown.getMessage());

    }

    @DisplayName("If save() method causes RuntimeException, a USerServiceException is thrown")
    @Test
    void testCreateUser_whenSaveMethodThrowsException_thenThrowsUserServiceException() {
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        assertThrows(UserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException instead");
    }

    @Test
    void testCreateUser_whenEmailNotificationExceptionThrown_throwsUserServiceException() {
        when(userRepository.save(any(User.class))).thenReturn(true);

        doThrow(EmailVerificationServiceException.class)
                .when(emailVerificationService)
                .scheduleEmailConfirmation(any(User.class));



        assertThrows(EmailVerificationServiceException.class, ()->{
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        });
    }

    @Test
    void testCreateUser_whenUserCreated_schedulesEmailConfirmation() {

        when(userRepository.save(any(User.class))).thenReturn(true);

        doCallRealMethod().when(emailVerificationService).scheduleEmailConfirmation(any(User.class));

        userService.createUser(firstName, lastName, email, password, repeatPassword);

        verify(emailVerificationService, times(1)).scheduleEmailConfirmation(any(User.class));

    }

}