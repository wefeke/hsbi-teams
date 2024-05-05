package com.example.application.services;

import com.example.application.models.Test;
import com.example.application.models.User;
import com.example.application.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public User findAdmin(){
        return userRepository.findByUsername("Admin");
    }
}
