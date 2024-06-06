package com.example.application.login;

import com.example.application.models.User;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserSettings extends Dialog {

    private final TextField name = new TextField("Name");
    private final TextField username = new TextField("Username");
    private final PasswordField hashedPassword = new PasswordField("Password");
    private Avatar avatar = new Avatar();
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Upload upload = new Upload();

    private byte[] uploadedImage;
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

    private final Button editButton = new Button("Edit");
    private Boolean isEdited = false;

    private User user;

    private final Binder<User> binder = new Binder<>(User.class);

    public UserSettings (User user) {
        this.user = user;
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
                hashedPassword,
                upload

        );
        //getHeader().add(avatar);
        verticalLayout.setAlignItems(VerticalLayout.Alignment.CENTER);
        add(verticalLayout);
        setHeaderTitle("User Settings");
        getFooter().add(cancelButton, editButton, saveButton);
    }

    private void configureElements() {
        cancelButton.addClickListener(e -> close());
        saveButton.setThemeName("primary");
        editButton.addClickListener(e -> {
            if (isEdited == true) {
                name.setReadOnly(false);
                username.setReadOnly(false);
                hashedPassword.setReadOnly(false);
                isEdited = false;
            }
            else {
                name.setReadOnly(true);
                username.setReadOnly(true);
                hashedPassword.setReadOnly(true);
                isEdited = true;
            }

        });

        name.setWidthFull();
        name.setReadOnly(true);
        username.setWidthFull();
        username.setReadOnly(true);
        hashedPassword.setWidthFull();
        hashedPassword.setReadOnly(true);
    }

    private void bindFields() {
        binder.bind(name, User::getName, User::setName);
        binder.bind(username, User::getUsername, User::setUsername);
        binder.bind(hashedPassword, User::getPassword, User::setPassword);

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

    private void readBean () {
        binder.readBean(user);

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
