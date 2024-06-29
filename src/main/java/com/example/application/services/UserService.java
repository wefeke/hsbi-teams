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

/**
 * Die UserService Klasse bietet Methoden zur Interaktion mit Benutzerdaten in der Datenbank.
 * Sie enthält Methoden zum Abrufen, Speichern von Benutzern und zur Überprüfung der Verfügbarkeit von Benutzernamen.
 * Jede Methode ist als Transaktion gekennzeichnet, um die Datenintegrität zu gewährleisten.
 * Die Klasse verwendet das UserRepository zur Interaktion mit der Datenbank.
 *
 * @author Kennet
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User findAdmin(){
        return userRepository.findByUsername("Admin");
    }

    @Transactional
    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return userRepository.findAll(filter, pageable);
    }

    @Transactional
    public void saveUser(User user) {
        if (user != null) {
            userRepository.save(user);
        }
        else
            System.err.println("User is null. Are you sure you have connected your form to the application?");
    }

    @Transactional
    public Boolean isUsernameAvailable (String username) {
        return userRepository.findByUsername(username) == null;
    }

    @Transactional
    public Boolean isUsernameAvailableExcept (String username, String exception) {
        if (userRepository.findByUsername(username) == null) {
            return true;
        }
        else return username.equals(exception);
    }

    @Transactional
    public List<User> findAllUserByRole (Role role) {
        return userRepository.findAllByRolesContains(role);
    }

}
