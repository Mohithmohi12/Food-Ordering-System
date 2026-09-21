package com.springboot.FoodOrderingSystem.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.FoodOrderingSystem.dto.ResponseStructure;
import com.springboot.FoodOrderingSystem.entity.User;
import com.springboot.FoodOrderingSystem.exception.InvalidRequestException;
import com.springboot.FoodOrderingSystem.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<ResponseStructure<User>> registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new InvalidRequestException("username is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new InvalidRequestException("password is required");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidRequestException("username already taken: " + user.getUsername());
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_CUSTOMER");
        }

        User saved = userRepository.save(user);

        ResponseStructure<User> structure = new ResponseStructure<User>()
                .setStatus(HttpStatus.CREATED.value())
                .setMessage("User registered successfully")
                .setData(saved);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }
}
