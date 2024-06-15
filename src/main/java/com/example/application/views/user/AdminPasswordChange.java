package com.example.application.views.user;

import com.example.application.models.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AdminPasswordChange extends Dialog {

    PasswordField new_password = new PasswordField("Neues Password");
    PasswordField password_check = new PasswordField("Password wiederholen");

    UserService userService;

    PasswordEncoder passwordEncoder;
    User user;

    public AdminPasswordChange (PasswordEncoder passwordEncoder, UserService userService, User user) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.user = user;
        createElements();
        configureElements();

        setHeaderTitle("Change Password");
    }

    private void createElements() {
        add(new_password, password_check);
    }

    private void configureElements() {
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
            if (!password_check.isInvalid() && !new_password.isInvalid()) {
                user.setPassword(passwordEncoder.encode(new_password.getValue()));
                userService.saveUser(user);
                Notification.show("Passwort für [" + user.getUsername() + "] auf [" + new_password.getValue() + "] geändert");
                close();
                clearFields();
            }
        }));
    }

    private void clearFields () {
        new_password.clear();
        password_check.clear();
    }
}
