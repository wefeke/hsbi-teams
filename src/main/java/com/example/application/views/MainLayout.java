//Author: Joris
package com.example.application.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

//MainLayout ist die Hauptansicht der Anwendung, die die Navigationsleiste enthält
public class MainLayout extends AppLayout {

    public MainLayout() {
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
        logoButton.getStyle().set("padding-left", "20px");

        // Navigationsbutton für die Veranstaltungen
        Button veranstaltungenButton = new Button("Veranstaltungen", e -> getUI().ifPresent(ui -> ui.navigate("")));
        configureButton(veranstaltungenButton, "16px", true);

        // Navigationsbutton für die Studierenden
        Button studierendeButton = new Button("Studierende", e -> getUI().ifPresent(ui -> ui.navigate("studierende")));
        configureButton(studierendeButton, "16px", true);

        header.add(logoButton, veranstaltungenButton, studierendeButton);
        addToNavbar(header);
    }

    private void configureButton(Button button, String fontSize, boolean colorControl) {
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.getStyle().set("font-size", fontSize);
        button.getStyle().set("cursor", "pointer");
        //hier wieder (!colorControl): also! ergänzen, um die Farbe zu ändern
        if (colorControl) {
            button.getStyle().set("color", "black");
        }
    }

    private void updateButtonStyles() {
        //hier noch eine Methode einfügen, um die Farben für die Buttons zu konfigurieren.
        //muss irgendwie über die Route laufen, aber ich weiß gerade nicht wie ich die Route bekomme.
    }
}
