//Author: Joris
package com.example.application.views;

import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.RouteConfiguration;

public class MainLayout extends AppLayout {

    private Button veranstaltungenButton;
    private Button studierendeButton;

    private final VeranstaltungenService veranstaltungenService;

    public MainLayout(VeranstaltungenService veranstaltungenService) {
        this.veranstaltungenService = veranstaltungenService;
        createHeader();
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
        Button logoButton = new Button("H.S.B.I TeamBuilder", e -> getUI().ifPresent(ui -> ui.navigate("")));
        configureButton(logoButton, "24px", false);

        // Navigationsbutton für die Veranstaltungen
        veranstaltungenButton = new Button("Veranstaltungen", e -> getUI().ifPresent(ui -> ui.navigate("")));
        configureButton(veranstaltungenButton, "16px", true);

        // Navigationsbutton für die Studierenden
        studierendeButton = new Button("Studierende", e -> getUI().ifPresent(ui -> ui.navigate("studierende")));
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
