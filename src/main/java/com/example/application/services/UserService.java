package com.example.application.services;

import com.example.application.models.Test;
import com.example.application.models.User;
import com.example.application.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return userRepository.findAll(filter, pageable);
    }

    public void saveUser(User user) {
        if (user != null) {
            userRepository.save(user);
        }
        else
            System.err.println("User is null. Are you sure you have connected your form to the application?");
    }
}
