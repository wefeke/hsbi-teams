package com.example.application.security;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class UserIsLockedException extends InternalAuthenticationServiceException {
    public UserIsLockedException(String message) {
        super(message);
    }
}