package com.example.application.security;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

/**
 * Die UserIsLockedException Klasse erweitert die InternalAuthenticationServiceException Klasse.
 * Sie repräsentiert eine spezifische Art von Authentifizierungsfehler, der auftritt, wenn ein Benutzerkonto gesperrt ist.
 * Diese Ausnahme wird geworfen, wenn ein "gesperrter" Benutzer versucht, sich zu authentifizieren.
 *
 * @author Kennet
 */
public class UserIsLockedException extends InternalAuthenticationServiceException {

    /**
     * Erstellt eine neue UserIsLockedException mit einer spezifischen Fehlermeldung.
     *
     * @author Kennet
     * @param message Die Fehlermeldung, die die Ausnahme beschreibt.
     */
    public UserIsLockedException(String message) {
        super(message);
    }
}