package com.example.application.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;

/**
 * Diese Klasse definiert das Verhalten, das auftritt, wenn die Authentifizierung eines Benutzers fehlschlägt.
 *
 * @author Kennet
 */
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Diese Methode wird aufgerufen, wenn die Authentifizierung eines Benutzers fehlschlägt.
     * Sie leitet den Benutzer zur Login-Seite mit einem entsprechenden Fehlerparameter um.
     * Wenn die Authentifizierung aufgrund einer UserIsLockedException fehlschlägt, wird der Benutzer zur Login-Seite mit einem "lockerror" Parameter umgeleitet.
     * Wenn die Authentifizierung aus einem anderen Grund fehlschlägt, wird der Benutzer zur Login-Seite mit einem allgemeinen "error" Parameter umgeleitet. Dies kann verwendet werden, um eine allgemeine Fehlermeldung anzuzeigen.
     *
     * @author Kennet
     * @param request Das HttpServletRequest-Objekt, das die Anforderung des Clients repräsentiert.
     * @param response Das HttpServletResponse-Objekt, das eine Antwort an den Client sendet.
     * @param exception Die Ausnahme, die geworfen wurde, als die Authentifizierung fehlschlug.
     * @throws IOException Wenn ein Eingabe- oder Ausgabefehler auftritt.
     */
    @Override
    public void onAuthenticationFailure(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException exception) throws IOException {
        if (exception instanceof UserIsLockedException){
            // Redirect to the login page with a lock error parameter
            response.sendRedirect("/login?lockerror");
        } else {
            // Redirect to the login page with a general error parameter
            response.sendRedirect("/login?error");
        }
    }
}