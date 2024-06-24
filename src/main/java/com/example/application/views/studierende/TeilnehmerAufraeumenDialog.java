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

    /**
     * Konstruktor für die TeilnehmerAufraeumenDialog Klasse.
     * Initialisiert den TeilnehmerService, den authentifizierten Benutzer und die StudierendeView.
     * Setzt den Platzhalter für das yearsField und konfiguriert die Aktionen der "closeButton", "deleteOldButton", "deleteNoEventButton" und "deleteButton".
     * Setzt die Breite und Höhe des Dialogs und konfiguriert das Grid.
     * Aktualisiert das Grid und fügt das erstellte Layout hinzu.
     *
     * @param teilnehmerService Der Service, der für die Verwaltung der Teilnehmer benötigt wird.
     * @param authenticatedUser Der aktuell authentifizierte Benutzer.
     * @param studierendeView Die StudierendeView, die aktualisiert wird, wenn ein Teilnehmer gelöscht wird.
     */
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

    /**
     * Erstellt ein VerticalLayout und fügt das Grid hinzu.
     * Setzt den Titel des Headers auf "Studierende ohne Veranstaltung" und fügt die Buttons "deleteNoEventButton", "deleteOldButton" und das "yearsField" zum Header hinzu.
     * Fügt die Buttons "closeButton" und "deleteButton" zum Footer hinzu.
     *
     * @return Das erstellte VerticalLayout mit dem hinzugefügten Grid.
     */
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

    /**
     * Aktualisiert das Grid mit Teilnehmern, die seit einer bestimmten Anzahl von Jahren registriert sind.
     * Holt den authentifizierten Benutzer und, wenn vorhanden, holt es die Liste der Teilnehmer, die seit der angegebenen Anzahl von Jahren registriert sind.
     * Setzt die Elemente des Grids auf diese Liste von Teilnehmern.
     *
     * @param years Die Anzahl der Jahre, die die Teilnehmer registriert sein sollen.
     */
    public void updateGridOld(int years) {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Teilnehmer> studierendeVorJahren = teilnehmerService.findStudierendeVorJahren(years,user);
            grid.setItems(studierendeVorJahren);
        }

    }

    /**
     * Aktualisiert das Grid mit Teilnehmern, die keine Veranstaltung haben.
     * Holt den authentifizierten Benutzer und, wenn vorhanden, holt es die Liste der Teilnehmer, die keine Veranstaltung haben.
     * Setzt die Elemente des Grids auf diese Liste von Teilnehmern.
     */
    public void updateGridNoEvent() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            List<Teilnehmer> studierendeOhneVeranstaltung = teilnehmerService.findStudierendeOhneVeranstaltung(user);
            grid.setItems(studierendeOhneVeranstaltung);
        }
    }

    /**
     * Gibt den Wert des "yearsField" zurück.
     *
     * @return Der Wert des "yearsField" als Double. Kann null sein, wenn kein Wert gesetzt wurde.
     */
    public Double getYearsFieldValue() {
        return yearsField.getValue();
    }
}


