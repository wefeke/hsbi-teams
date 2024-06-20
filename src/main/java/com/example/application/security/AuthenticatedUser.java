package com.example.application.security;

import com.example.application.models.User;
import com.example.application.repositories.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Eine Klasse, die den aktuell authentifizierten Benutzer repräsentiert.
 * Sie bietet Methoden zum Abrufen des authentifizierten Benutzers und zum Abmelden.
 *
 * @author Kennet
 */
@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    /**
     * Konstruktor für die AuthenticatedUser Klasse.
     *
     * @author Kennet
     * @param authenticationContext Ein AuthenticationContext-Objekt, das Informationen über den Authentifizierungsstatus enthält.
     * @param userRepository Ein UserRepository-Objekt, das Methoden zur Interaktion mit User-Objekten in der Datenbank bereitstellt.
     */
    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Gibt den aktuell authentifizierten Benutzer zurück.
     * Wenn kein Benutzer authentifiziert ist, wird ein leeres Optional zurückgegeben.
     *
     * @author Kennet
     * @return Ein Optional, das den aktuell authentifizierten Benutzer enthält, oder ein leeres Optional, wenn kein Benutzer authentifiziert ist.
     */
    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
    }

    /**
     * Meldet den aktuell authentifizierten Benutzer ab.
     *
     * @author Kennet
     */
    public void logout() {
        authenticationContext.logout();
    }

}
