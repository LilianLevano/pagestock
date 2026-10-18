package com.example.pagestock.user.service;

import com.example.pagestock.exceptions.EmailAlreadyExistsException;
import com.example.pagestock.exceptions.PasswordNotEqualsConfirmPasswordException;
import com.example.pagestock.user.dto.RegisterRequest;
import com.example.pagestock.user.dto.UserResponse;
import com.example.pagestock.user.model.User;
import com.example.pagestock.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserResponse register(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new EmailAlreadyExistsException();
        }

        if(!registerRequest.password().equals(registerRequest.password())) {
            throw new PasswordNotEqualsConfirmPasswordException();
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        User user = new User();
            user.setFirstName(registerRequest.firstname());
            user.setLastName(registerRequest.lastname());
            user.setEmail(registerRequest.email());
            user.setPassword(encodedPassword);
            user.setPhoneNumber(registerRequest.phoneNumber());

        return toResponse(userRepository.save(user));
    }



    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }

}
