package com.example.application.views.user;

import com.example.application.models.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Dialog zur Änderung des Passworts eines Benutzers durch einen Administrator.
 *
 * @author Kennet
 */
public class AdminPasswordChangeDialog extends Dialog {

    PasswordField new_password = new PasswordField("Neues Password");
    PasswordField password_check = new PasswordField("Password wiederholen");

    UserService userService;

    PasswordEncoder passwordEncoder;
    User user;

    /**
     * Konstruktor für die AdminPasswordChange Klasse.
     *
     * @author Kennet
     * @param passwordEncoder Ein PasswordEncoder-Objekt, das zum Hashen von Passwörtern verwendet wird.
     * @param userService Ein UserService-Objekt, das Methoden zur Interaktion mit User-Objekten in der Datenbank bereitstellt.
     * @param user Das User-Objekt, dessen Passwort geändert werden soll.
     */
    public AdminPasswordChangeDialog(PasswordEncoder passwordEncoder, UserService userService, User user) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.user = user;
        createElements();
        configureElements();

        setHeaderTitle("Change Password");
    }

    /**
     * Erstellt die UI-Elemente für den Dialog.
     *
     * @author Kennet
     */
    private void createElements() {
        add(new_password, password_check);
    }

    /**
     * Konfiguriert die UI-Elemente für den Dialog.
     * Überprüft, ob die eingegebenen Passwörter übereinstimmen und speichert das neue Passwort in der Datenbank.
     *
     * @author Kennet
     */
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
                Notification.show("Passwort erfolgreich geändert");
                close();
                clearFields();
            }
        }));
    }


    /**
     * Leert die Eingabefelder des Dialogs.
     *
     * @author Kennet
     */
    private void clearFields () {
        new_password.clear();
        password_check.clear();
    }
}
