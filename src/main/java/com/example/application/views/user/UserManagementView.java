package com.example.application.views.user;
import com.example.application.models.Role;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Verwaltungsklasse zur Verwaltung von Benutzern durch einen Administrator.
 *
 * @author Kennet
 */
@PageTitle("User Management")
@Route(value = "user-management", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class UserManagementView extends VerticalLayout {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private TextField filterText = new TextField();
    private Text noEventsText = new Text("");
    private Grid<User> grid = new Grid<>();
    HorizontalLayout toolbar = new HorizontalLayout();

    /**
     * Konstruktor für die UserManagementView Klasse.
     *
     * @author Kennet
     * @param userService Ein UserService-Objekt, das Methoden zur Interaktion mit User-Objekten in der Datenbank bereitstellt.
     * @param passwordEncoder Ein PasswordEncoder-Objekt, das zum Hashen von Passwörtern verwendet wird.
     */
    public UserManagementView(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        setSizeFull();
        configureElements();
        add(createGrid());
        updateGrid("");
    }

    private void configureElements() {

        filterText.setPlaceholder("Suche...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> {
            String searchText = e.getValue().toLowerCase();
            updateGrid(searchText);
        });

        Button addUserButton = new Button(LineAwesomeIcon.PLUS_SOLID.create(), e -> {
            VaadinSession.getCurrent().setAttribute("previousLocation", "user-management");
            getUI().get().navigate("registration");
        });

        toolbar.add(filterText, addUserButton);
        add(toolbar);
    }

    public void updateGrid(String searchText){
        List<User> userList = userService.findAllUserByRole(Role.USER);

        if (!userList.isEmpty()){
                List<User> filteredVeranstaltungen = userList.stream()
                        .filter(user -> user.getUsername().toLowerCase().contains(searchText) || user.getName().toLowerCase().contains(searchText))
                        .sorted(Comparator.comparing(User::getUsername))
                        .toList();

                grid.setItems(filteredVeranstaltungen);
            }
        }

    /**
     * Erstellt ein Grid zur Anzeige der Benutzerinformationen.
     * Der Administrator kann die Rollen, das Passwort und den Zugriff der Benutzer ändern.
     *
     * @author Kennet
     * @return Ein Grid, das die Benutzerinformationen anzeigt.
     */
    private Grid<User> createGrid(){
        //grid.setItems(userService.findAllUserByRole(Role.USER));
        grid.addColumn(User::getName).setHeader("Name").setSortable(true); // Add a column for the user name
        grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);

        grid.addColumn(new ComponentRenderer<>(user -> {
            Button button = new Button("Passwort ändern");
            button.addClickListener(event -> {
                AdminPasswordChangeDialog adminPasswordChangeDialog = new AdminPasswordChangeDialog(passwordEncoder, userService, user);
                adminPasswordChangeDialog.open();
            });
            return button;
        })).setHeader("Passwort");

        grid.addColumn(new ComponentRenderer<>(user -> {
            MultiSelectComboBox<Role> comboBox = new MultiSelectComboBox<>();
            comboBox.setItems(Role.USER, Role.ADMIN);// Fügen Sie alle Rollen hinzu
            comboBox.setValue(user.getRoles());

            comboBox.addValueChangeListener(event -> {
                if (comboBox.isEmpty())
                    comboBox.setInvalid(true);
                else
                    comboBox.setInvalid(false);

                if (!comboBox.isInvalid()){
                    user.setRoles(event.getValue());
                    userService.saveUser(user); // Aktualisieren Sie den Benutzer in der Datenbank
                }
                else {
                    comboBox.setErrorMessage("Bitte wählen Sie mindestens eine Rolle aus");
                }
            });

            return comboBox;
        })).setHeader("Rollen");



        // Add a column with a button to toggle the isLocked attribute
        grid.addColumn(new ComponentRenderer<>(user -> {
            Button button = new Button(user.isLocked() ? "Freigeben" : "Sperren");
            button.getStyle().set("background-color", user.isLocked() ? "#77dd77" : "#ff6961"); // Update the background color
            button.getStyle().set("color", "white");
            button.addClickListener(event -> {
                user.setLocked(!user.isLocked()); // Toggle the attribute
                button.setText(user.isLocked() ? "Freigeben" : "Sperren");
                button.getStyle().set("background-color", user.isLocked() ? "#77dd77" : "#ff6961"); // Update the background color
                userService.saveUser(user); // Update the user in the database
            });
            return button;
        })).setHeader("Zugriff").setSortable(true).setComparator((user1, user2) -> Boolean.compare(user1.isLocked(), user2.isLocked()));

        grid.setSizeFull();
        grid.setThemeName("row-stripes");

        return grid; // Add the grid to the layout

    }
}
