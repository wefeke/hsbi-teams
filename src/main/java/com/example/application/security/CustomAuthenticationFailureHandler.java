package com.example.application.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;
import com.example.application.security.UserIsLockedException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException exception) throws IOException, jakarta.servlet.ServletException {
        if (exception instanceof UserIsLockedException){
            // Redirect to the login page with a lock error parameter
            response.sendRedirect("/login?lockerror");
        } else {
            // Redirect to the login page with a general error parameter
            response.sendRedirect("/login?error");
        }
    }
}