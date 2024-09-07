package org.example.service;

import org.example.data.UserRepository;
import org.example.data.UserRepositoryImpl;
import org.example.model.User;

public class UserServiceImpl implements UserService {

    EmailVerificationService emailVerificationService;
    UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository,
                           EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
    }


    @Override
    public User createUser(String firstName,
                           String lastName,
                           String email,
                           String password,
                           String repeatPassword) {
System.out.println("aqui");
        if(firstName == null || firstName.trim().length() == 0){
            throw new IllegalArgumentException("User's first name should not be null");
        }

        if(!password.equals(repeatPassword)) {
            return null;
        }
        Boolean  createdUser = false;

        User user = new User(firstName, lastName, email, password, repeatPassword);
        try {
            createdUser = userRepository.save(user);
        } catch (RuntimeException e){
            throw new UserServiceException(e.getMessage());
        }

        if(!createdUser) throw new UserServiceException("Could not create user");


        try {
            emailVerificationService.scheduleEmailConfirmation(user);
        } catch (RuntimeException e) {
            throw new EmailVerificationServiceException(e.getMessage());
        }

        return user;


    }
}
