package com.example.application.views.user;

import com.example.application.models.Role;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@PageTitle("User Management")
@Route(value = "user-management", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class UserManagement extends VerticalLayout {

    private final UserService userService;

    public UserManagement(UserService userService) {
        this.userService = userService;

        Grid<User> grid = new Grid<>();
        grid.setItems(userService.findAllUser()); // Fetch all users

        grid.addColumn(User::getName).setHeader("Name"); // Add a column for the user name
        grid.addColumn(User::getUsername).setHeader("Username");

        grid.addColumn(new ComponentRenderer<>(user -> {
            Button button = new Button("Passwort ändern");
            button.addClickListener(event -> {
            });
            return button;
        })).setHeader("Passwort");

        grid.addColumn(new ComponentRenderer<>(user -> {
            MultiSelectComboBox<Role> comboBox = new MultiSelectComboBox<>();
            comboBox.setItems(Role.USER, Role.ADMIN);// Fügen Sie alle Rollen hinzu
            comboBox.setValue(user.getRoles());

            comboBox.addValueChangeListener(event -> {
                user.setRoles(event.getValue());
                userService.saveUser(user); // Aktualisieren Sie den Benutzer in der Datenbank
            });
            return comboBox;
        })).setHeader("Rollen");

        // Add a column with a button to toggle the isLocked attribute
        grid.addColumn(new ComponentRenderer<>(user -> {
            Button button = new Button(user.isLocked() ? "Entsperren" : "Sperren");
            button.getStyle().set("background-color", user.isLocked() ? "#77dd77" : "#ff6961"); // Update the background color
            button.getStyle().set("color", "white");
            button.addClickListener(event -> {
                user.setLocked(!user.isLocked()); // Toggle the attribute
                button.setText(user.isLocked() ? "Entsperren" : "Sperren");
                button.getStyle().set("background-color", user.isLocked() ? "#77dd77" : "#ff6961"); // Update the background color
                userService.saveUser(user); // Update the user in the database
            });
            return button;
        })).setHeader("Zugriff");

        grid.setSizeFull();
        add(grid); // Add the grid to the layout

        setSizeFull();
    }



}
