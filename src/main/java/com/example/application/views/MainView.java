//Author: Joris
package com.example.application.views;

import com.example.application.services.VeranstaltungenService;
import com.example.application.views.studierende.StudierendeView;
import com.example.application.views.veranstaltungen.VeranstaltungenView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@PageTitle("TeamBuilder")
public class MainView extends AppLayout {

    private Button veranstaltungenButton;
    private Button studierendeButton;
    private final VeranstaltungenService veranstaltungenService;

    @Autowired
    public MainView(VeranstaltungenService veranstaltungenService) {
        this.veranstaltungenService = veranstaltungenService;
        createHeader();
        updateView(new VeranstaltungenView(veranstaltungenService)); // Initialansicht
    }

    /**
     * Erstellt den Header der Hauptansicht mit Navigationsbuttons.
     * Enthält Buttons für das Hauptlogo, Veranstaltungen und Studierende.
     */
    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.getStyle().set("background", "transparent"); // Transparenter Hintergrund

        // Logo-Button, das als Home-Button fungiert
        Button logoButton = new Button("H.S.B.I TeamBuilder", e -> updateView(new VeranstaltungenView(veranstaltungenService)));
        configureButton(logoButton, "24px", false);

        // Navigationsbutton für die Veranstaltungen
        veranstaltungenButton = new Button("Veranstaltungen", e -> updateView(new VeranstaltungenView(veranstaltungenService)));
        configureButton(veranstaltungenButton, "16px", true);

        // Navigationsbutton für die Studierenden
        studierendeButton = new Button("Studierende", e -> updateView(new StudierendeView()));
        configureButton(studierendeButton, "16px", true);

        header.add(logoButton, veranstaltungenButton, studierendeButton);
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
        if (!colorControl) {
            button.getStyle().set("color", "black");
        }
    }

    /**
     * Aktualisiert die Hauptansicht der Anwendung und ändert den Stil der Navigationsbuttons.
     *
     * @param component Die Komponente, die als neue Hauptansicht gesetzt wird.
     */
    private void updateView(Component component) {
        setContent(component);
        updateButtonStyles(component);
    }

    /**
     * Aktualisiert die Farbe der Navigationsbuttons basierend auf der aktuell angezeigten View.
     *
     * @param component Die aktuell aktive Komponente in der Hauptansicht.
     */
    private void updateButtonStyles(Component component) {
        if (component instanceof VeranstaltungenView) {
            veranstaltungenButton.getStyle().set("color", "blue");
            studierendeButton.getStyle().set("color", "black");
        } else if (component instanceof StudierendeView) {
            studierendeButton.getStyle().set("color", "blue");
            veranstaltungenButton.getStyle().set("color", "black");
        }
    }
}




