//Author: Joris
package com.example.application.views.veranstaltungen;

import com.example.application.models.Veranstaltung;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static javax.swing.UIManager.getUI;

@PageTitle("Veranstaltungen")
    @Route(value = "ver")
    public class VeranstaltungenView extends VerticalLayout {

        private final VeranstaltungenService veranstaltungenService;

        @Autowired
        public VeranstaltungenView(VeranstaltungenService veranstaltungenService) {
            this.veranstaltungenService = veranstaltungenService;

            HorizontalLayout mainLayout = new HorizontalLayout();
            mainLayout.setSizeFull();

            Div kachelContainer = new Div();
            kachelContainer.addClassName("veranstaltungen-container");
            kachelContainer.getStyle().set("display", "flex");
            kachelContainer.getStyle().set("flexWrap", "wrap");

            List<Veranstaltung> veranstaltungen = veranstaltungenService.findAllVeranstaltungen();

            // Kacheln für vorhandene Veranstaltungen erstellen
            for (Veranstaltung veranstaltung : veranstaltungen) {

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
                    UI.getCurrent().navigate("veranstaltung-detail/" + veranstaltung.getVeranstaltungsId());
                });

                kachelContainer.add(kachel);
            }


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
                UI.getCurrent().navigate("add-veranstaltung");
            });

            // Kachel für neue Veranstaltung hinzufügen
            kachelContainer.add(neueVeranstaltungKachel);

            mainLayout.add(kachelContainer);
            add(mainLayout);
        }

    }



