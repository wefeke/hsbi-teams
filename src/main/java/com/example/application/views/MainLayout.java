//Author: Joris
package com.example.application.views;

import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import java.io.ByteArrayInputStream;
import java.util.Optional;

//MainLayout ist die Hauptansicht der Anwendung, die die Navigationsleiste enthält
public class MainLayout extends AppLayout {

    private Button veranstaltungenButton;
    private Button studierendeButton;

    private final VeranstaltungenService veranstaltungenService;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, VeranstaltungenService veranstaltungenService){
        this.veranstaltungenService = veranstaltungenService;
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        createHeader();
    }

    /**
     * Erstellt den Header der Hauptansicht mit Navigationsbuttons.
     * Enthält Buttons für das Hauptlogo, Veranstaltungen und Studierende.
     */
    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout(); //Navigation-Bar
        HorizontalLayout navItems = new HorizontalLayout(); //Left side of the Navigation-Bar
        HorizontalLayout settingItems = new HorizontalLayout(); //Right side of the Navigation-Bar

        //Configure Header
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle().set("background", "transparent");

        // (First Element) Logo-Button, das als Home-Button fungiert
        Button logoButton = new Button("H.S.B.I TeamBuilder", e -> getUI().ifPresent(ui -> ui.navigate("")));
        configureButton(logoButton, "24px", false);
        logoButton.getStyle().set("padding-left", "20px");

        // (Second Element) Navigationsbutton für die Veranstaltungen
        veranstaltungenButton = new Button("Veranstaltungen", e -> getUI().ifPresent(ui -> ui.navigate("")));
        configureButton(veranstaltungenButton, "16px", true);

        // (Third Element) Navigationsbutton für die Studierenden
        studierendeButton = new Button("Studierende", e -> getUI().ifPresent(ui -> ui.navigate("studierende")));
        configureButton(studierendeButton, "16px", true);

        navItems.add(logoButton, veranstaltungenButton, studierendeButton); //Zur rechten Seite hinzufügens

        // (Fifth Element) Login-Button
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            byte[] profilePicture = user.getProfilePicture();
            if (profilePicture != null) {
                StreamResource resource = new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(profilePicture));
                avatar.setImageResource(resource);
            } else {
                String initials = user.getName().substring(0, 2).toUpperCase();
                avatar.setName(initials);
            }
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            div.getElement().getStyle().set("margin", "0 10px");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e ->
                authenticatedUser.logout()
            );
            settingItems.add(userMenu); //zur rechten Seite hinzufügen
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            settingItems.add(loginLink); //zur rechten Seite hinzufügen
        }

        //Theme Button
        Button themeToggleButton = new Button("Toggle Theme");

        themeToggleButton.addClickListener( event -> {
            UI ui = UI.getCurrent();
            if (ui.getElement().getThemeList().contains("dark")) {
                ui.getElement().getThemeList().remove("dark");
                themeToggleButton.setText("Dark Theme");
            } else {
                ui.getElement().getThemeList().add("dark");
                themeToggleButton.setText("Light Theme");
            }
        });
        themeToggleButton.getStyle().setMarginRight("10px");
        settingItems.add(themeToggleButton); //zur rechten Seite hinzufügen

        header.add(navItems, settingItems);  //Linke und rechte Seite in die Navigationsbar
        addToNavbar(header);
    }

    /**
     * Konfiguriert das Aussehen eines Buttons.
     *
     * @param button Der zu konfigurierende Button.
     * @param fontSize Die Schriftgröße des Buttons.
     * @param colorControl Gibt an, ob die Farbe des Buttons angepasst werden soll.
     */
    private void configureButton(Button button, String fontSize, boolean colorControl) {
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.getStyle().set("font-size", fontSize);
        button.getStyle().set("cursor", "pointer");
        //hier wieder (!colorControl): also ! ergänzen um die Farbe zu ändern
        if (colorControl) {
            button.getStyle().set("color", "black");
        }
    }


    private void updateButtonStyles() {
        //hier noch eine Methode einfügen um die Farben für die Buttons zu konfigurieren.
        //muss irgendwie über die Route laufen, aber ich weiß gerade nciht wir ich die Route bekomme.
    }
}
