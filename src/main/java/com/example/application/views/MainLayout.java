//Author: Joris
package com.example.application.views;

import com.example.application.services.UserService;
import com.example.application.views.kalender.KalenderView;
import com.example.application.views.user.PasswordChangeDialog;
import com.example.application.views.user.UserManagementView;
import com.example.application.views.user.UserSettingsDialog;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.studierende.StudierendeView;
import com.example.application.views.veranstaltungen.VeranstaltungenView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.ByteArrayInputStream;
import java.util.Optional;


/**
 * Hauptansicht der Anwendung, die die Navigationsleiste enthält.
 *
 * @author Kennet
 */
public class MainLayout extends AppLayout {

    private final VeranstaltungenService veranstaltungenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    /**
     * Konstruktor für die MainLayout Klasse.
     * Initiiert das Erstellen des Headers.
     *
     * @author Kennet
     * @param authenticatedUser Ein AuthenticatedUser-Objekt, das Informationen über den authentifizierten Benutzer enthält.
     * @param accessChecker Ein AccessAnnotationChecker-Objekt, das die Zugriffsrechte überprüft.
     * @param veranstaltungenService Ein VeranstaltungenService-Objekt, das Methoden zur Interaktion mit Veranstaltungs-Objekten in der Datenbank bereitstellt.
     * @param userService Ein UserService-Objekt, das Methoden zur Interaktion mit User-Objekten in der Datenbank bereitstellt.
     * @param passwordEncoder Ein PasswordEncoder-Objekt, das zum Verschlüsseln von Passwörtern verwendet wird.
     */
    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, VeranstaltungenService veranstaltungenService, UserService userService, PasswordEncoder passwordEncoder){
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.veranstaltungenService = veranstaltungenService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        createHeader();
    }


    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Gap.XSMALL, LumoUtility.Height.MEDIUM, LumoUtility.AlignItems.CENTER, LumoUtility.Padding.Horizontal.SMALL,
                    LumoUtility.TextColor.BODY);
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames(LumoUtility.FontWeight.MEDIUM, LumoUtility.FontSize.MEDIUM, LumoUtility.Whitespace.NOWRAP);

            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }
    }


    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("Veranstaltungen", LineAwesomeIcon.UNIVERSITY_SOLID.create(), VeranstaltungenView.class), //
                new MenuItemInfo("Studierende", LineAwesomeIcon.ID_BADGE.create(), StudierendeView.class),
                new MenuItemInfo("Kalender", LineAwesomeIcon.CALENDAR.create(), KalenderView.class),
                new MenuItemInfo("User Management", LineAwesomeIcon.ID_CARD.create(), UserManagementView.class),
        };
    }

    /**
     * Erstellt den Header der Hauptansicht mit Navigationsbuttons.
     * Enthält Navigationselemente für das Hauptlogo, Veranstaltungen, Studierende und User Management.
     * Der Zugriff des Benutzers auf die verschiedenen Ansichten wird überprüft und diese dementsprechend angezeigt.
     * Enthält auch einen Button zum Wechseln des Themes.
     * Enthält zudem einen Button zum Anzeigen des Benutzermenüs. Über die List-Elemente kann der Benutzer Einstellungen an seinem Profil tätigen, sein Passwort ändern oder sich abmelden.
     *
     * @author Kennet
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
        Button logoButton = new Button("HSBI TeamBuilder", e -> getUI().ifPresent(ui -> ui.navigate("")));
        logoButton.addClassName("logo-button");
        configureButton(logoButton, "24px", false);
        logoButton.getStyle().set("padding-left", "20px");

        // Navigation Items
        UnorderedList list = new UnorderedList();
        list.addClassName("nav-list");
        list.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        navItems.add(list);
        navItems.add(logoButton, list);
        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }
        }

        //Setting Items
        //Theme Button (4th Element)
        Button themeToggleButton = new Button(LineAwesomeIcon.MOON.create());
        themeToggleButton.setText("Dunkel");

        themeToggleButton.addClickListener( event -> {
            UI ui = UI.getCurrent();
            if (ui.getElement().getThemeList().contains("dark")) {
                ui.getElement().getThemeList().remove("dark");
                themeToggleButton.setIcon(LineAwesomeIcon.MOON_SOLID.create());
                themeToggleButton.setText("Dunkel");
            } else {
                ui.getElement().getThemeList().add("dark");
                themeToggleButton.setText("Hell");
                themeToggleButton.setIcon(LineAwesomeIcon.SUN_SOLID.create());
            }
        });
        settingItems.add(themeToggleButton); //zur rechten Seite hinzufügen

        // (5th Element) Login-Button
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
            userName.add(div);

            UserSettingsDialog userSettingsDialog = new UserSettingsDialog(authenticatedUser, userService);
            userSettingsDialog.addOpenedChangeListener(e -> {
                if (e.isOpened()) {
                    userSettingsDialog.readBean();
                }
            });

            PasswordChangeDialog passwordChangeDialog = new PasswordChangeDialog(authenticatedUser, userService, passwordEncoder);

            // Create a HorizontalLayout and add the icon and text to it
            HorizontalLayout settingsItemLayout = new HorizontalLayout(LineAwesomeIcon.COG_SOLID.create(), new Text("Einstellungen"));
            userName.getSubMenu().addItem(settingsItemLayout, e ->
                    userSettingsDialog.open()
            );

            // Create a HorizontalLayout and add the icon and text to it
            HorizontalLayout passwordItemLayout = new HorizontalLayout(LineAwesomeIcon.KEY_SOLID.create(), new Text("Passwort"));
            userName.getSubMenu().addItem(passwordItemLayout, e ->
                    passwordChangeDialog.open()
            );

            HorizontalLayout signOutItemLayout = new HorizontalLayout(LineAwesomeIcon.SIGN_OUT_ALT_SOLID.create(), new Text("Abmelden"));
            userName.getSubMenu().addItem(signOutItemLayout, e ->
                authenticatedUser.logout()
            );

            userMenu.getStyle().setMarginRight("10px");
            settingItems.add(userMenu); //zur rechten Seite hinzufügen
        }
        else {
            Div div = new Div();
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            Anchor loginLink = new Anchor("login", "Sign in");
            div.add(loginLink);
            settingItems.add(div); //zur rechten Seite hinzufügen
        }

        header.add(navItems, settingItems);  //Linke und rechte Seite in die Navigationsbar
        addToNavbar(header);
    }


    /**
     * Konfiguriert das Aussehen eines Buttons.
     *
     * @author Kennet
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
}
