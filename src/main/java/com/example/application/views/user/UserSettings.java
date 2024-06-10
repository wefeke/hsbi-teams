package com.example.application.views.user;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.services.UserService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.server.StreamResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class UserSettings extends Dialog {

    private final TextField name = new TextField("Name");
    private final TextField username = new TextField("Username");
    private final Button passwordChange = new Button("Passwort ändern");
    private Avatar avatar = new Avatar();
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Upload upload = new Upload();

    private final PasswordEncoder passwordEncoder;

    private byte[] uploadedImage;
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

    private User user;
    private final UserService userService;

    public UserSettings (User user, UserService userService, PasswordEncoder passwordEncoder) {
        this.user = user;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        createElements();
        configureElements();
        bindFields();
        readBean();
    }

    private final Binder<User> binder = new Binder<>(User.class);

    private void createElements() {
        VerticalLayout verticalLayout= new VerticalLayout(
                avatar,
                name,
                username,
                passwordChange,
                upload

        );
        //getHeader().add(avatar);
        verticalLayout.setAlignItems(VerticalLayout.Alignment.CENTER);
        add(verticalLayout);
        setHeaderTitle("User Settings");
        getFooter().add(cancelButton, saveButton);
    }

    private void configureElements() {
        cancelButton.addClickListener(e -> close());
        saveButton.setThemeName("primary");
        saveButton.addClickListener(e -> {
            if (binder.writeBeanIfValid(user)) {
                this.userService.saveUser(user);
                clearFields();
                close();

                Notification.show("Veranstaltung angelegt");
            }
            else {
                Notification.show("Fehler beim Speichern");
            }
        });

        passwordChange.addClickListener(e -> {
            PasswordField old_password = new PasswordField("Altes Password");
            PasswordField new_password = new PasswordField("Neues Password");
            PasswordField password_check = new PasswordField("Password wiederholen");

            Dialog passwordDialog = new Dialog();
            passwordDialog.add(old_password, new_password, password_check);
            passwordDialog.open();
            old_password.setWidthFull();
            new_password.setWidthFull();
            password_check.setWidthFull();

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

            passwordDialog.getFooter().add(new Button("Cancel", event -> {
                passwordDialog.close();
            }));

            passwordDialog.getFooter().add(new Button("Save", event -> {
                if (passwordEncoder.matches(old_password.getValue(), user.getPassword())) {
                    old_password.setInvalid(false);
                }
                else {
                    old_password.setInvalid(true);
                    old_password.setErrorMessage("Password is incorrect");
                }
            }));

        });

        name.setWidthFull();
        username.setWidthFull();
        passwordChange.setWidthFull();
    }

    private void bindFields() {
        binder.bind(name, User::getName, User::setName);
        binder.bind(username, User::getUsername, User::setUsername);
    }

    public void readBean () {
        binder.readBean(user);

        avatar.setWidth("20vh");
        avatar.setHeight("20vh");
        avatar.setName(user.getName());
        byte[] profilePicture = user.getProfilePicture();
        if (profilePicture != null) {
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(profilePicture));
            avatar.setImageResource(resource);
            Notification.show("Image gefunden");
        }
        else {
            String initials = user.getName().substring(0, 2).toUpperCase();
            avatar.setName(initials);
            Notification.show("Kein Image gefunden");
        }

        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");

        //Image Handling
        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream(event.getFileName());
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadedImage = byteOutputStream.toByteArray();
            Notification.show("File " + event.getFileName() + " successfully uploaded.");
        });
    }

    private void clearFields () {
        name.clear();
        username.clear();
    }


}


//
//Avatar avatar = new Avatar(user.getName());
//byte[] profilePicture = user.getProfilePicture();
//        if (profilePicture != null) {
//StreamResource resource = new StreamResource("profile-pic",
//        () -> new ByteArrayInputStream(profilePicture));
//            avatar.setImageResource(resource);
//        } else {
//String initials = user.getName().substring(0, 2).toUpperCase();
//            avatar.setName(initials);
//        }
//                avatar.setThemeName("xsmall");
//        avatar.getElement().setAttribute("tabindex", "-1");
