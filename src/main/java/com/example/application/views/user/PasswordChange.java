package com.example.application.views.user;

import com.example.application.models.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordChange extends Dialog {

    private PasswordField old_password = new PasswordField("Altes Password");
    private PasswordField new_password = new PasswordField("Neues Password");
    private PasswordField password_check = new PasswordField("Password wiederholen");

    private PasswordEncoder passwordEncoder;
    private final UserService userService;
    private User user;

    public PasswordChange (PasswordEncoder passwordEncoder, UserService userService, User user) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.user = user;
        createElements();
        configureElements();

        setHeaderTitle("Change Password");
    }

    private void createElements() {
        add(old_password, new_password, password_check);
    }

    private void configureElements() {
        old_password.setWidthFull();
        old_password.setRequired(true);
        new_password.setWidthFull();
        new_password.setRequired(true);
        password_check.setWidthFull();
        password_check.setRequired(true);

        password_check.addValueChangeListener(event -> {
            if (new_password.getValue().equals(password_check.getValue())) {
                password_check.setInvalid(false);
            }
            else {
                password_check.setInvalid(true);
                password_check.setErrorMessage("Passwords do not match");
            }
        });

        new_password.addValueChangeListener(event -> {
            if (new_password.getValue().equals(password_check.getValue())) {
                password_check.setInvalid(false);
            }
            else {
                password_check.setInvalid(true);
                password_check.setErrorMessage("Passwords do not match");
            }
        });

        getFooter().add(new Button("Abbrechen", event -> {
            close();
            clearFields();
        }));

        getFooter().add(new Button("Änderungen speichern", event -> {
            if (passwordEncoder.matches(old_password.getValue(), user.getPassword())) {
                old_password.setInvalid(false);
            }
            else {
                old_password.setInvalid(true);
                old_password.setErrorMessage("Password is incorrect");
            }

            if (!password_check.isInvalid() && !old_password.isInvalid() && !new_password.isInvalid()) {
                user.setPassword(passwordEncoder.encode(new_password.getValue()));
                userService.saveUser(user);
                Notification.show("Passwort für [" + user.getUsername() + "] auf [" + new_password.getValue() + "] geändert");
                close();
                clearFields();
            }
        }));
    }

    private void clearFields() {
        old_password.clear();
        new_password.clear();
        password_check.clear();
    }
}
