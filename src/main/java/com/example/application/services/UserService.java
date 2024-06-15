package com.example.application.services;

import com.example.application.models.Role;
import com.example.application.models.User;
import com.example.application.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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

    public User findUserById(Long id) {
        return userRepository.findUserById(id);
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

    public Boolean isUsernameAvailable (String username) {
       if (userRepository.findByUsername(username) == null)
           return true;
       else
              return false;
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

    public Boolean isUsernameAvailableExcept (String username, String exception) {

        if (userRepository.findByUsername(username) == null) {
            //Notification.show("Found no User with this Username");
            return true;
        }
        else if (username.equals(exception)) {
            //Notification.show("Found only this User with this Username");
            return true;
        }
        else {
            //Notification.show("User is already taken" +username + exception);
            return false;
        }
    }

    @Transactional
    public List<User> findAllUserByRole (Role role) {
        return userRepository.findAllByRolesContains(role);
    }

}
