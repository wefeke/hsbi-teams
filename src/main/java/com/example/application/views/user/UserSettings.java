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
import org.springframework.transaction.annotation.Transactional;

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

    private final PasswordEncoder passwordEncoder;
    private PasswordChange passwordChangeDialog;

    private byte[] uploadedImage;
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private final Upload upload = new Upload(buffer);

    private User user;
    private final UserService userService;

    private final Binder<User> binder = new Binder<>(User.class);

    public UserSettings (User user, UserService userService, PasswordEncoder passwordEncoder) {
        this.user = user;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        passwordChangeDialog = new PasswordChange(passwordEncoder, user);
        createElements();
        configureElements();
        bindFields();
        readBean();
    }

    private void createElements() {
        VerticalLayout verticalLayout= new VerticalLayout(
                avatar,
                name,
                username,
                passwordChange,
                upload

        );
        verticalLayout.setAlignItems(VerticalLayout.Alignment.CENTER);
        add(verticalLayout);
        setHeaderTitle("User Settings");
        getFooter().add(cancelButton, saveButton);
    }

    private void configureElements() {
        cancelButton.addClickListener(e -> close());
        saveButton.setThemeName("primary");
        saveButton.addClickListener(e -> {
            User user = new User();
            if (binder.writeBeanIfValid(user)) {
                user.setId(this.user.getId());
                user.setProfilePicture(uploadedImage);
                this.userService.saveUser(user);
                clearFields();
                close();

                Notification.show("User aktualisiert");
            }
            else {
                Notification.show("Fehler beim Speichern");
            }
        });

        passwordChange.addClickListener(e -> {
            passwordChangeDialog.open();
        });

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
            Notification.show("Datei \"" + event.getFileName() + "\" erfolgreich hochgeladen.");
        });

        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFiles(1);

        name.setWidthFull();
        username.setWidthFull();
        passwordChange.setWidthFull();
    }

    private void bindFields() {
        binder.forField(name)
                .bind(User::getName, User::setName);
        binder.forField(username)
                .withValidator(username -> userService.isUsernameAvailableExcept(username, this.user.getUsername()), "Username bereits vergeben")
                .bind(User::getUsername, User::setUsername);
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
        }
        else {
            String initials = user.getName().substring(0, 2).toUpperCase();
            avatar.setName(initials);
        }

        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");
    }

    private void clearFields () {
        name.clear();
        username.clear();
    }

}
