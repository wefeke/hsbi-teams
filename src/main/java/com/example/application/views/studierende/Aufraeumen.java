package com.example.application.views.studierende;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import jakarta.annotation.security.RolesAllowed;

import java.util.*;


@RolesAllowed({"ADMIN", "USER"})
public class Aufraeumen extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private final Button deleteOldButton = new Button("Älter als ... Jahre)");
    private final Button deleteNoEventButton = new Button("Ohne Veranstaltung)");
    private final Button deleteButton = new Button("Löschen");
    private final Button closeButton = new Button("Schließen");
    private final Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class);
    private final NumberField yearsField = new NumberField();

   private AuthenticatedUser authenticatedUser;


    public Aufraeumen(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, StudierendeView studierendeView) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        DeleteDialog deleteDialog = new DeleteDialog(teilnehmerService, authenticatedUser, this, studierendeView);


        closeButton.addClickListener(event ->
                close());

        deleteOldButton.addClickListener(event -> {
            Double years = yearsField.getValue();
            if (years != null) {
                grid.getSelectedItems().forEach(teilnehmerService::deleteTeilnehmer);
                updateGridOld(years.intValue());
                setHeaderTitle("Studierende seit " + years.intValue() + " Jahren");
            } else {
                Notification.show("Bitte geben Sie die Anzahl der Jahre ein.");
            }
        });

        deleteNoEventButton.addClickListener(event -> {
            grid.getSelectedItems().forEach(teilnehmerService::deleteTeilnehmer);
            updateGridNoEvent();
            setHeaderTitle("Studierende ohne Veranstaltung");
        });
        deleteButton.addClickListener(event -> {
            Set<Teilnehmer> selectedTeilnehmer = grid.getSelectedItems();
            if (!selectedTeilnehmer.isEmpty()) {
                List<Teilnehmer> teilnehmerList = new ArrayList<>(selectedTeilnehmer);
                deleteDialog.openDeleteDialog(teilnehmerList);
            }
        });

        this.setWidth("80vw");
        this.setHeight("80vh");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setColumns("vorname", "nachname", "id");
        updateGridNoEvent();
        add(
                createLayout()
        );
    }
    private VerticalLayout createLayout() {
        setHeaderTitle("Studierende ohne Veranstaltung");
        getHeader().add(deleteNoEventButton);
        getHeader().add(deleteOldButton);
        getHeader().add(yearsField);
        getFooter().add(closeButton);
        getFooter().add(deleteButton);

        return (
                new VerticalLayout(grid));
    }
    public void updateGridOld(int years) {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Teilnehmer> studierendeVorJahren = teilnehmerService.findStudierendeVorJahren(years,user);
            grid.setItems(studierendeVorJahren);
        }

    }

    public void updateGridNoEvent() {
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

