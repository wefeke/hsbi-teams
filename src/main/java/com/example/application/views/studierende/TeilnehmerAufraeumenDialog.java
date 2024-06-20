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
public class TeilnehmerAufraeumenDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private final Button deleteOldButton = new Button("Älter als ... Jahre");
    private final Button deleteNoEventButton = new Button("Ohne Veranstaltung");
    private final Button deleteButton = new Button("Löschen");
    private final Button closeButton = new Button("Schließen");
    private final Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class);
    private final NumberField yearsField = new NumberField();

   private AuthenticatedUser authenticatedUser;


    public TeilnehmerAufraeumenDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, StudierendeView studierendeView) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        TeilnehmerLoeschenDialog teilnehmerLoeschenDialog = new TeilnehmerLoeschenDialog(teilnehmerService, authenticatedUser, this, studierendeView);
        yearsField.setPlaceholder("Jahre");

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
            updateGridNoEvent();
            setHeaderTitle("Studierende ohne Veranstaltung");
        });
        deleteButton.addClickListener(event -> {
            Set<Teilnehmer> selectedTeilnehmer = grid.getSelectedItems();
            if (!selectedTeilnehmer.isEmpty()) {
                for (Teilnehmer teilnehmer : selectedTeilnehmer) {
                    TeilnehmerLoeschenDialog deleteDialogForSelectedTeilnehmer = new TeilnehmerLoeschenDialog(teilnehmerService, authenticatedUser, this, studierendeView);
                    deleteDialogForSelectedTeilnehmer.setTeilnehmer(teilnehmer);
                    deleteDialogForSelectedTeilnehmer.open();
                }
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
    public Double getYearsFieldValue() {
        return yearsField.getValue();
    }
}


