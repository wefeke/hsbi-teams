package com.example.application.views.studierende;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import jakarta.annotation.security.RolesAllowed;

import java.util.*;

/**
 * Diese Klasse repräsentiert einen Dialog zum Aufräumen von Teilnehmern.
 * Sie erbt von der Dialog-Klasse von Vaadin und bietet eine Benutzeroberfläche zum Anzeigen und Löschen von Teilnehmern.
 * Der Dialog enthält ein Grid zur Anzeige der Teilnehmer und Schaltflächen zum Löschen ausgewählter Teilnehmer und zum Schließen des Dialogs.
 * Die Klasse verwendet einen TeilnehmerService zum Abrufen und Löschen von Teilnehmern und einen AuthenticatedUser zur Authentifizierung.
 * Sie enthält auch eine Referenz auf eine StudierendeView, die aktualisiert wird, wenn Teilnehmer gelöscht werden.
 * Darüber hinaus bietet die Klasse die Möglichkeit, Teilnehmer basierend auf der Anzahl der Jahre, die sie registriert sind, oder ob sie keine Veranstaltung haben, anzuzeigen.
 *
 * @author Tobias
 */
@RolesAllowed({"ADMIN", "USER"})
public class TeilnehmerAufraeumenDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private final Button deleteButton = new Button("Löschen");
    private final Button closeButton = new Button("Schließen");
    private final Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class);
    private final NumberField yearsField = new NumberField();

    private final AuthenticatedUser authenticatedUser;

    /**
     * Konstruktor für die TeilnehmerAufraeumenDialog Klasse.
     * Initialisiert den TeilnehmerService, den authentifizierten Benutzer und die StudierendeView.
     * Setzt den Platzhalter für das yearsField und konfiguriert die Aktionen der "closeButton" und "deleteButton".
     * Setzt die Breite und Höhe des Dialogs und konfiguriert das Grid.
     * Aktualisiert das Grid und fügt das erstellte Layout hinzu.
     *
     * @param teilnehmerService Der Service, der für die Verwaltung der Teilnehmer benötigt wird.
     * @param authenticatedUser Der aktuell authentifizierte Benutzer.
     * @param studierendeView Die StudierendeView, die aktualisiert wird, wenn ein Teilnehmer gelöscht wird.
     * @author Tobias
     */
    public TeilnehmerAufraeumenDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, StudierendeView studierendeView) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;

        closeButton.addClickListener(event ->
                close());

        deleteButton.addClickListener(event -> {
            Set<Teilnehmer> selectedTeilnehmer = grid.getSelectedItems();
            if (!selectedTeilnehmer.isEmpty()) {
                for (Teilnehmer teilnehmer : selectedTeilnehmer) {
                    TeilnehmerLoeschenDialog deleteDialogForSelectedTeilnehmer = new TeilnehmerLoeschenDialog(teilnehmerService, this, studierendeView);
                    deleteDialogForSelectedTeilnehmer.setTeilnehmer(teilnehmer);
                    deleteDialogForSelectedTeilnehmer.open();
                }
            }
        });

        this.setWidth("80vw");
        this.setHeight("80vh");
        this.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                updateGrid();
            }
        });
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setColumns("vorname", "nachname", "id");
        grid.setSizeFull();
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            deleteButton.setEnabled(size != 0);
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.setEnabled(false);

        updateGrid();

        yearsField.setClearButtonVisible(true);
        yearsField.setValueChangeMode(ValueChangeMode.LAZY);
        yearsField.addValueChangeListener(e ->  updateGrid());
        yearsField.setPlaceholder("Älter als … Jahre");
        add(
                createLayout()
        );
    }

    /**
     * Erstellt und gibt ein VerticalLayout zurück, das das Grid enthält.
     * Setzt den Titel des Headers auf "Studierende ohne Veranstaltung" und fügt das "yearsField" zum Header hinzu.
     * Fügt die Buttons "closeButton" und "deleteButton" zum Footer hinzu.
     * Das erstellte Layout nimmt die volle verfügbare Größe ein.
     *
     * @return Das erstellte VerticalLayout mit dem hinzugefügten Grid.
     * @author Tobias
     */
    private VerticalLayout createLayout() {
        setHeaderTitle("Studierende ohne Veranstaltung");
        getHeader().add(yearsField);
        getFooter().add(closeButton);
        getFooter().add(deleteButton);
        VerticalLayout layout = new VerticalLayout(grid);
        layout.setSizeFull();

        return layout;
    }

    /**
     * Aktualisiert das Grid mit Teilnehmern, die seit einer bestimmten Anzahl von Jahren registriert sind.
     * Holt den authentifizierten Benutzer und, wenn vorhanden, holt es die Liste der Teilnehmer, die seit der angegebenen Anzahl von Jahren registriert sind.
     * Setzt die Elemente des Grids auf diese Liste von Teilnehmern.
     *
     * @param years Die Anzahl der Jahre, die die Teilnehmer registriert sein sollen.
     * @author Tobias
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
     *
     * @author Tobias
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
     * @author Tobias
     */
    public Double getYearsFieldValue() {
        return yearsField.getValue();
    }

    /**
     * Aktualisiert das Grid basierend auf dem Wert des "yearsField".
     * Wenn ein Wert im "yearsField" vorhanden ist, aktualisiert es das Grid mit Teilnehmern, die seit einer bestimmten Anzahl von Jahren registriert sind.
     * Wenn kein Wert im "yearsField" vorhanden ist, aktualisiert es das Grid mit Teilnehmern, die keine Veranstaltung haben.
     *
     * @author Tobias
     */
    public void updateGrid() {
        Double yearsValue = getYearsFieldValue();
        if (yearsValue != null) {
            updateGridOld(yearsValue.intValue());
        }
        else {
            updateGridNoEvent();
        }
    }
}


