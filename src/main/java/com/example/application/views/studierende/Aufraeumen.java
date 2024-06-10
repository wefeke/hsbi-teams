package com.example.application.views.studierende;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RolesAllowed({"ADMIN", "USER"})
public class Aufraeumen extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private final Button deleteOldButton = new Button("Löschen (älter als 4 Jahre)");
    private final Button deleteNoEventButton = new Button("Löschen (keine Veranstaltung)");
    private final Button closeButton = new Button("Schließen");
    private final Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class);
    private final NumberField yearsField = new NumberField("Jahre");

    private AuthenticatedUser authenticatedUser;


    public Aufraeumen(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;

        closeButton.addClickListener(event ->
                close());

        deleteOldButton.addClickListener(event -> {
            Double years = yearsField.getValue();
            if (years != null) {
                grid.getSelectedItems().forEach(teilnehmerService::deleteTeilnehmer);
                updateGridOld(years.intValue());
            } else {
                Notification.show("Bitte geben Sie die Anzahl der Jahre ein.");
            }
        });

        deleteNoEventButton.addClickListener(event -> {
            grid.getSelectedItems().forEach(teilnehmerService::deleteTeilnehmer);
            updateGridNoEvent();
        });

        this.setWidth("80vw");
        this.setHeight("80vh");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setColumns("vorname", "nachname", "id");
        add(
                grid,
                closeButton,
                deleteOldButton,
                deleteNoEventButton,
                yearsField
        );
    }

    private void updateGridOld(int years) {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            grid.setItems(teilnehmerService.findAllTeilnehmerByUser(user));
        }
        List<Teilnehmer> studierendeVorJahren = teilnehmerService.findStudierendeVorJahren(years);
        grid.setItems(studierendeVorJahren);
    }

    private void updateGridNoEvent() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Teilnehmer> studierendeOhneVeranstaltung = teilnehmerService.findStudierendeOhneVeranstaltung(user);
            grid.setItems(studierendeOhneVeranstaltung);
        }

    }
}

//    private void delete (){
//        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
//            add(new Text("Der Studierende " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " ist noch in folgenden Veranstaltungen gespeichert:"));
//
//            Grid<Veranstaltung> grid = new Grid<>();
//            grid.addColumn(Veranstaltung::toString).setHeader("Veranstaltungen");
//            grid.setItems(teilnehmer.getVeranstaltungen());
//            add(grid);
//
//            Button deleteButton = new Button("Löschen", event -> {
//
//                // Entfernen Sie den Teilnehmer aus allen Gruppen
//                for (Gruppe gruppe : new HashSet<>(teilnehmer.getGruppen())) {
//                    gruppe.getTeilnehmer().remove(teilnehmer);
//                    teilnehmer.getGruppen().remove(gruppe);
//                }
//
//                // Entfernen Sie den Teilnehmer aus allen Gruppenarbeiten
//                for (Gruppenarbeit gruppenarbeit : new HashSet<>(teilnehmer.getGruppenarbeiten())) {
//                    gruppenarbeit.getTeilnehmer().remove(teilnehmer);
//                    teilnehmer.getGruppenarbeiten().remove(gruppenarbeit);
//                }
//                // Entfernen Sie den Teilnehmer aus allen Veranstaltungen
//                for (Veranstaltung veranstaltung : new HashSet<>(teilnehmer.getVeranstaltungen())) {
//                    veranstaltung.getTeilnehmer().remove(teilnehmer);
//                    teilnehmer.getVeranstaltungen().remove(veranstaltung);
//                }
//
//
//                // Speichern Sie die Änderungen
//                teilnehmerService.saveTeilnehmer(teilnehmer);
//
//                // Löschen Sie den Teilnehmer
//                //teilnehmerService.deleteTeilnehmer(teilnehmer);
//
//                Notification.show("Studierender aus Veranstaltungen, Gruppen und Gruppenarbeiten entfernt");
//                close();
//            });
//
//            Button cancelButton = new Button("Abbrechen", event -> close());
//
//            add(deleteButton, cancelButton);
//        } else {
//            add(new Text("Möchten Sie den Studierenden " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " wirklich löschen?"));
//
//            Button yesButton = new Button("Ja", event -> {
//                teilnehmerService.deleteTeilnehmer(teilnehmer);
//                Notification.show("Studierender gelöscht");
//                close();
//            });
//
//            Button noButton = new Button("Nein", event -> close());
//
//            add(yesButton, noButton);
//        }
//    }

