package com.example.application.login;

import com.example.application.models.Role;
import com.example.application.models.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private PasswordField password_check = new PasswordField("Password bestätigen");
    private Button submitButton = new Button("Bestätigen");
    private Button cancelButton = new Button("Abbrechen");

    //Image
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private Upload upload = new Upload(buffer);
    private byte[] uploadedImage;

    //Services
    private final UserService userService;

    //Data Binder
    private Binder<User> binder = new Binder<>(User.class);

    //Hashing of Password
    private final PasswordEncoder passwordEncoder;

    public RegistrationView(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        mainLayout = new Div(createElements());
        mainLayout.getStyle()
                .setBoxShadow("0 0 10px 0 rgba(100, 100, 100, 0.3)")
                .setBorderRadius("10px")
                .setPadding("20px");
        setAlignItems(Alignment.CENTER);

        configureElements();
        add(mainLayout);
        bindFields();
    }

    private VerticalLayout createElements(){

        VerticalLayout verticalLayout = new VerticalLayout(submitButton, cancelButton);

        return new VerticalLayout(
                header,
                name,
                username,
                password,
                password_check,
                upload,
                verticalLayout
        );
    }

    private void configureElements(){
        name.setWidthFull();
        username.setWidthFull();
        password.setWidthFull();
        password_check.setWidthFull();
        upload.setWidthFull();
        submitButton.setWidthFull();
        submitButton.setThemeName("primary");

        submitButton.addClickListener(event -> {
            User user = new User();
            if (binder.writeBeanIfValid(user)) {
                user.setRoles(Set.of(Role.USER));
                user.setAdmin(false);
                user.setProfilePicture(uploadedImage);
                user.setLocked(true);
                if (user.getPassword() != null) {
                    user.setPassword(passwordEncoder.encode(user.getPassword())); // encode the password
                    userService.saveUser(user); //Angemeldeten User holen
                }
                else {
                    Notification.show("Please enter a password" + user.getPassword());
                }

                Notification.show("User " + user.getName() + " angelegt!");
                clearFields();
                String previousLocation = (String) VaadinSession.getCurrent().getAttribute("previousLocation");
                if (previousLocation != null) {
                    getUI().ifPresent(ui -> ui.navigate(previousLocation));
                }
                else {
                    getUI().ifPresent(ui -> ui.navigate("login"));
                }
            }
        });

        cancelButton.setWidthFull();
        cancelButton.setThemeName("error");
        cancelButton.addClickListener(event -> {
            String previousLocation = (String) VaadinSession.getCurrent().getAttribute("previousLocation");
            if (previousLocation != null) {
                getUI().ifPresent(ui -> ui.navigate(previousLocation));
            }
            else {
                getUI().ifPresent(ui -> ui.navigate("login"));
            }
        });

        password.addValueChangeListener(event -> checkPasswordsMatch());
        password_check.addValueChangeListener(event -> checkPasswordsMatch());

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

        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFiles(1);
    }

    private void checkPasswordsMatch() {
        if(!password.getValue().equals(password_check.getValue())){
            password_check.setInvalid(true);
            password_check.setErrorMessage("Passwörter stimmen nicht überein");
        }
        else{
            password_check.setInvalid(false);
        }
    }

    private void bindFields(){
        //binder.forField(ID).bind(User::getId, User::setId);
        binder.forField(name)
                .asRequired("Name muss gefüllt sein")
                .bind(User::getName, User::setName);
        binder.forField(username)
                .asRequired("Username muss gefüllt sein")
                .withValidator(username -> userService.isUsernameAvailable(username), "Username bereits vergeben")
                .withValidator(username -> username.equals(username.toLowerCase()), "Username muss klein geschrieben sein")
                .bind(User::getUsername, User::setUsername);
        binder.forField(password)
                .asRequired("Password muss gefüllt sein")
                .bind(User::getPassword, User::setPassword);

        password_check.setRequired(true);
    }

    private void clearFields(){
        name.clear();
        username.clear();
        password.clear();
        password_check.clear();
    }


}
