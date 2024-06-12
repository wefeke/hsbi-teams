package com.example.application.views.user;

import com.example.application.models.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.PasswordField;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordChange extends Dialog {

    PasswordField old_password = new PasswordField("Altes Password");
    PasswordField new_password = new PasswordField("Neues Password");
    PasswordField password_check = new PasswordField("Password wiederholen");

    PasswordEncoder passwordEncoder;
    User user;

    public PasswordChange (PasswordEncoder passwordEncoder, User user) {
        this.passwordEncoder = passwordEncoder;
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

        getFooter().add(new Button("Cancel", event -> {
            close();
        }));

        getFooter().add(new Button("Save", event -> {
            if (passwordEncoder.matches(old_password.getValue(), user.getPassword())) {
                old_password.setInvalid(false);
            }
            else {
                old_password.setInvalid(true);
                old_password.setErrorMessage("Password is incorrect");
            }

            if (!password_check.isInvalid() && !old_password.isInvalid() && !new_password.isInvalid()) {
                user.setPassword(passwordEncoder.encode(new_password.getValue()));
                close();
            }
        }));

    }
}
