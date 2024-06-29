package com.example.application.repositories;

import com.example.application.models.Role;
import com.example.application.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Das UserRepository Interface bietet Methoden zur Interaktion mit Benutzerdaten in der Datenbank.
 * Es erweitert JpaRepository und JpaSpecificationExecutor und definiert zusätzliche Methoden zum Abrufen von Benutzern basierend auf Benutzername und Rolle.
 *
 * @author Kennet
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);
    List<User> findAllByRolesContains(Role role);
}
