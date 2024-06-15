package com.example.application.views.user;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.vaadin.flow.component.Text;
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
import org.aspectj.weaver.ast.Not;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserSettings extends Dialog {

    private final TextField name = new TextField("Name");
    private final TextField username = new TextField("Username");
    private final Button passwordChange = new Button("Passwort ändern");
    private Avatar avatar = new Avatar();
    private final Button saveButton = new Button("Änderungen speichern");
    private final Button cancelButton = new Button("Abbrechen");

    private final PasswordEncoder passwordEncoder;
    private PasswordChange passwordChangeDialog;

    //Image Objects
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private final Upload upload = new Upload(buffer);
    private byte[] uploadedImage;

    private AuthenticatedUser authenticatedUser;

    private User user;
    private final UserService userService;

    private Long userId;

    private final Binder<User> binder = new Binder<>(User.class);

    public UserSettings (AuthenticatedUser authenticatedUser, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticatedUser = authenticatedUser;
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            this.user = maybeUser.get();
        }
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        passwordChangeDialog = new PasswordChange(passwordEncoder, userService, user);
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
        saveButton.setTooltipText("!Achtung! Beim speichern werden Sie ausgeloggt");
        saveButton.setThemeName("primary");

        saveButton.addClickListener(e -> {
            System.out.println(username);
            System.out.println(user.getUsername());
            if (binder.writeBeanIfValid(user)) {
                user.setProfilePicture(uploadedImage);
                userService.saveUser(user);
                authenticatedUser.logout();
                Notification.show("User aktualisiert");
                clearFields();
                close();
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
        //username.setReadOnly(true);
        passwordChange.setWidthFull();
    }

    private void bindFields() {
        binder.forField(name)
                .bind(User::getName, User::setName);
        binder.forField(username)
                .withValidator(username -> userService.isUsernameAvailableExcept(username, this.user.getUsername()), "Username bereits vergeben")
                .withValidator(username -> username.equals(username.toLowerCase()), "Username muss klein geschrieben sein")
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
