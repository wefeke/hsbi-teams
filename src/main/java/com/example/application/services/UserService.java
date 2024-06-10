package com.example.application.services;

import com.example.application.models.Test;
import com.example.application.models.User;
import com.example.application.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void saveUser(User user) {
        if (user != null) {
            userRepository.save(user);
        }
        else
            System.err.println("User is null. Are you sure you have connected your form to the application?");
    }


    public void lockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setLocked(true);
        userRepository.save(user);
    }

    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setLocked(false);
        userRepository.save(user);
    }

    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(Long userId) {
            super("User with id " + userId + " not found");
        }
    }
}
