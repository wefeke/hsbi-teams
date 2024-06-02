package com.example.application.login;

import com.example.application.models.Role;
import com.example.application.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.persistence.*;

import java.util.Set;

@AnonymousAllowed
@PageTitle("Registration")
@Route(value = "registration")
public class RegistrationView extends VerticalLayout {
    private Div mainLayout;

    //Elements
    private H1 header = new H1("Registrieren");
    private NumberField ID = new NumberField("ID");
    private TextField name = new TextField("Name");
    private TextField username = new TextField("Username");
    private PasswordField password = new PasswordField("Password");
    private Upload profilePicture = new Upload();
    private Button submitButton = new Button("Submit");
    private Button cancelButton = new Button("Cancel");

    //Data Binder
    Binder<User> binder = new Binder<>(User.class);
    /*
    @Id
    private Long id;
    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    private boolean isAdmin;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;
    */


    public RegistrationView() {
        mainLayout = new Div();
        addElements();
        configureElements();
        add(mainLayout);

        bindFields();
    }

    private void addElements(){
        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(
                header,
                name,
                username,
                password,
                profilePicture,
                new HorizontalLayout(
                        submitButton,
                        cancelButton
                )
        );

        verticalLayout.expand(submitButton, cancelButton);


        mainLayout.add(verticalLayout);

    }

    private void configureElements(){
        name.setWidthFull();
        username.setWidthFull();
        password.setWidthFull();
        profilePicture.setWidthFull();
        submitButton.setWidthFull();
        submitButton.setThemeName("primary");

        cancelButton.setWidthFull();
        cancelButton.setThemeName("error");
        cancelButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        setAlignItems(Alignment.CENTER);
    }

    private void bindFields(){
        //binder.forField(ID).bind(User::getId, User::setId);
        binder.forField(name)
                .asRequired("Name muss gefüllt sein")
                .bind(User::getName, User::setName);
        binder.forField(username).bind(User::getUsername, User::setUsername);
        binder.forField(password).bind(User::getPassword, User::setPassword);
    }

    private void persistUser(){

    }
}
