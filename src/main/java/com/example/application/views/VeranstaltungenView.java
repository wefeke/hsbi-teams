package com.example.application.views;

import com.example.application.data.Veranstaltung;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.HashSet;
import java.util.Set;

@PageTitle("Veranstaltungen")
@Route(value = "")
public class VeranstaltungenView extends VerticalLayout {

    public VeranstaltungenView() {
        // Erstellen des Haupt-Layouts für die Seite
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        // FlexLayout für die Kacheln
        Div kachelContainer = new Div();
        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");

        // Ein Set, um sicherzustellen, dass jede Veranstaltung nur einmal existiert
        Set<Veranstaltung> einzigartigeVeranstaltungen = new HashSet<>();
        einzigartigeVeranstaltungen.add(new Veranstaltung(1L, 1, "Informatik 1"));
        einzigartigeVeranstaltungen.add(new Veranstaltung(2L, 1, "Mathematik für Informatiker"));
        einzigartigeVeranstaltungen.add(new Veranstaltung(3L, 2, "Algorithmen und Datenstrukturen"));
        einzigartigeVeranstaltungen.add(new Veranstaltung(4L, 2, "Datenbanken"));

        // Kacheln erstellen
        for (Veranstaltung veranstaltung : einzigartigeVeranstaltungen) {
            // Verwende ein Div-Element als Kachel
            Div kachel = new Div();
            kachel.addClassNames("veranstaltung-kachel");
            kachel.getStyle().set("width", "200px").set("height", "200px")
                    .set("border", "1px solid lightgray").set("padding", "10px")
                    .set("margin", "5px").set("cursor", "pointer");

            // Titel der Veranstaltung
            Div titel = new Div();
            titel.setText(veranstaltung.getTitel());
            kachel.add(titel);

            // Button oder Div zum Klicken für Details
            Div details = new Div();
            details.addClickListener(e -> {
                // Hier Navigation zur Detailseite der Veranstaltung
                getUI().ifPresent(ui -> ui.navigate("veranstaltung-detail/" + veranstaltung.getVeranstaltungsId()));
            });
            kachel.add(details);

            // Füge die Kachel zum Container hinzu
            kachelContainer.add(kachel);
        }

        mainLayout.add(kachelContainer);
        add(mainLayout);
    }

}


