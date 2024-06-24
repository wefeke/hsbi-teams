package com.example.application.security;

import com.example.application.models.User;
import com.example.application.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service-Klasse zur Bereitstellung von Benutzerdetails für die Authentifizierung.
 *
 * @author Kennet
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Konstruktor für die UserDetailsServiceImpl Klasse.
     *
     * @author Kennet
     * @param userRepository Ein UserRepository-Objekt, das Methoden zur Interaktion mit User-Objekten in der Datenbank bereitstellt.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Lädt die Benutzerdetails für den angegebenen Benutzernamen.
     * Wenn kein Benutzer mit dem angegebenen Benutzernamen vorhanden ist, wird eine UsernameNotFoundException ausgelöst.
     * Wenn der Benutzer gesperrt ist, wird ebenfalls eine UsernameNotFoundException ausgelöst.
     *
     * @author Kennet
     * @param username Der Benutzername des Benutzers, dessen Details geladen werden sollen.
     * @return Ein UserDetails-Objekt, das die Details des Benutzers enthält.
     * @throws UsernameNotFoundException Wenn kein Benutzer mit dem angegebenen Benutzernamen vorhanden ist oder der Benutzer gesperrt ist.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Keinen User mit Usernamen : " + username + " gefunden");
        }
        else if (user.isLocked()) { //Implementiert die User locked Funktionalität
            throw new UserIsLockedException("User " + username + " ist nicht freigegeben");
        }
        else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    getAuthorities(user));
        }
    }

    /**
     * Gibt eine Liste der Berechtigungen des angegebenen Benutzers zurück.
     *
     * @author Kennet
     * @param user Das User-Objekt, dessen Berechtigungen abgerufen werden sollen.
     * @return Eine Liste von GrantedAuthority-Objekten, die die Berechtigungen des Benutzers repräsentieren.
     */
    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

}
