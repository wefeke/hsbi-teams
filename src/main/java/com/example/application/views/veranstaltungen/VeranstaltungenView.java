package com.example.application.views.veranstaltungen;

import com.example.application.models.Test;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TestService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

@PageTitle("Veranstaltungen")
@Route(value = "ver")
public class VeranstaltungenView extends VerticalLayout {

    public VeranstaltungenView() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        Div kachelContainer = new Div();
        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");

        // Testdaten: Nur übergangsweise, da kein Datenbankzugriff
        Set<Veranstaltung> veranstaltungen = new HashSet<>();
        veranstaltungen.add(new Veranstaltung(1L, new Date(), "Informatik 1"));
        veranstaltungen.add(new Veranstaltung(2L, new Date(), "Mathematik für Informatiker"));
        veranstaltungen.add(new Veranstaltung(3L, new Date(), "Algorithmen und Datenstrukturen"));
        veranstaltungen.add(new Veranstaltung(4L, new Date(), "Datenbanken"));

        // Kacheln für vorhandene Veranstaltungen erstellen
        for (Veranstaltung veranstaltung : veranstaltungen) {
            kachelContainer.add(createVeranstaltungKachel(veranstaltung));
        }

        // Kachel für neue Veranstaltung hinzufügen
        kachelContainer.add(createNeueVeranstaltungKachel());

        mainLayout.add(kachelContainer);
        add(mainLayout);
    }

    /**
     * Erstellt eine Kachel für eine spezifische Veranstaltung.
     *
     * @param veranstaltung Die Veranstaltung, für die eine Kachel erstellt werden soll.
     * @return Die erstellte Kachel als {@link Div}-Element, fertig zum Hinzufügen zum Container.
     */
    private Div createVeranstaltungKachel(Veranstaltung veranstaltung) {
        Div veranstaltungsInfo = new Div();
        veranstaltungsInfo.setText(veranstaltung.getTitel());
        veranstaltungsInfo.getStyle().set("text-align", "center");
        veranstaltungsInfo.getStyle().set("margin", "auto");

        Div kachelContent = new Div(veranstaltungsInfo);
        kachelContent.getStyle().set("display", "flex");
        kachelContent.getStyle().set("flex-direction", "column");

        Div kachel = new Div(kachelContent);
        kachel.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "10px")
                .set("padding", "1em")
                .set("margin", "0.5em")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)");
        kachel.setWidth("150px");
        kachel.setHeight("150px");

        kachel.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("veranstaltung-detail/" + veranstaltung.getVeranstaltungsId()));
        });

        return kachel;
    }

    /**
     * Erstellt eine Kachel, die verwendet wird, um eine neue Veranstaltung hinzuzufügen.
     *
     * Diese Kachel zeigt ein Pluszeichen und reagiert auf Klicks, indem eine Benachrichtigung
     * angezeigt wird, die darauf hinweist, dass eine neue Veranstaltung hinzugefügt wird.
     *
     * @return Die erstellte Kachel als {@link Div}-Element, fertig zum Hinzufügen zum Container.
     */
    private Div createNeueVeranstaltungKachel() {
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.getStyle()
                .set("font-size", "40px")
                .set("text-align", "center")
                .set("margin", "auto");

        Div neueVeranstaltungKachel = new Div(plusSymbol);
        neueVeranstaltungKachel.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "10px")
                .set("padding", "1em")
                .set("margin", "0.5em")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        neueVeranstaltungKachel.setWidth("150px");
        neueVeranstaltungKachel.setHeight("150px");

        neueVeranstaltungKachel.addClickListener(e -> {
            Notification.show("Neue Veranstaltung hinzufügen");
        });

        return neueVeranstaltungKachel;
    }
}



