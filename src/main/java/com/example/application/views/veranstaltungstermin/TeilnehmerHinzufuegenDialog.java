package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Diese Klasse repräsentiert einen Dialog zum Hinzufügen von Teilnehmern.
 * Sie erbt von der Dialog-Klasse von Vaadin und bietet eine Benutzeroberfläche zum Anzeigen und Hinzufügen von Teilnehmern.
 * Der Dialog enthält ein Grid zur Anzeige der Teilnehmer und Schaltflächen zum Hinzufügen, Erstellen und Importieren von Teilnehmern sowie zum Schließen des Dialogs.
 * Die Klasse verwendet einen TeilnehmerService zum Abrufen und Hinzufügen von Teilnehmern und einen AuthenticatedUser zur Authentifizierung.
 * Sie enthält auch eine Referenz auf eine VeranstaltungsterminView, die aktualisiert wird, wenn Teilnehmer hinzugefügt werden.
 * Darüber hinaus bietet die Klasse die Möglichkeit, Teilnehmer basierend auf einem Suchtext zu filtern.
 *
 * @author Tobias
 */
public class TeilnehmerHinzufuegenDialog extends Dialog {

    private final TeilnehmerService teilnehmerService;
    private final Long veranstaltungId;
    private final Button hinzufuegenButton = new Button("Hinzufügen");
    private final Button anlegenButton = new Button(new Icon(VaadinIcon.PLUS));
    private final Button importButton = new Button("Importieren");
    private final TextField filterText = new TextField();
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final TeilnehmerErstellenDialog dialog;
    private final AuthenticatedUser authenticatedUser;

    /**
     * Konstruktor für den Dialog zum Hinzufügen von Teilnehmern.
     * Initialisiert den Dialog und konfiguriert die Interaktionen der Benutzeroberfläche.
     * Der Dialog enthält einen Grid zur Anzeige der Teilnehmer, die hinzugefügt werden können, und Buttons zum Hinzufügen, Importieren und Abbrechen.
     * Der Hinzufügen-Button fügt die ausgewählten Teilnehmer zur Veranstaltung hinzu und aktualisiert die Ansicht.
     * Der Import-Button öffnet einen Dialog zum Importieren von Teilnehmern.
     * Der Abbrechen-Button schließt den Dialog.
     *
     * @param veranstaltungService der Service zur Verwaltung von Veranstaltungen
     * @param teilnehmerService der Service zur Verwaltung von Teilnehmern
     * @param veranstaltungId die ID der Veranstaltung, zu der Teilnehmer hinzugefügt werden sollen
     * @param authenticatedUser der authentifizierte Benutzer
     * @param veranstaltungsterminView die Ansicht des Veranstaltungstermins
     * @param veranstaltungstermin der Veranstaltungstermin
     * @param gruppenarbeit die Gruppenarbeit
     * @author Tobias
     */
    public TeilnehmerHinzufuegenDialog(VeranstaltungenService veranstaltungService, TeilnehmerService teilnehmerService, Long veranstaltungId, AuthenticatedUser authenticatedUser, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin veranstaltungstermin, Gruppenarbeit gruppenarbeit) {
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungId = veranstaltungId;
        this.authenticatedUser = authenticatedUser;

        this.setWidth("80vw");
        this.setHeight("80vh");

        hinzufuegenButton.setEnabled(false);
        hinzufuegenButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        dialog = new TeilnehmerErstellenDialog(teilnehmerService, authenticatedUser,this);
        anlegenButton.addClickListener(event ->
                dialog.open());

        hinzufuegenButton.addClickListener(event -> {
            Set<Teilnehmer> selectedTeilnehmer = new HashSet<>(grid.getSelectedItems());
            if (!selectedTeilnehmer.isEmpty()) {
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    veranstaltungService.addTeilnehmer(veranstaltungId, selectedTeilnehmer, user);

                    Notification.show(selectedTeilnehmer.size() + " Teilnehmer wurden hinzugefügt");

                    if (veranstaltungstermin != null) {
                        veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(veranstaltungstermin);

                        if (gruppenarbeit != null) {
                            veranstaltungsterminView.setAktiveKachelGruppenarbeit(gruppenarbeit);
                        }
                    }
                    veranstaltungsterminView.update();

                    updateGrid();
                    close();
                }
            } else {
                Notification.show("Keine Teilnehmer ausgewählt", 3000, Notification.Position.MIDDLE);
            }
        });
        importButton.addClickListener(event -> {
            TeilnehmerImportDialog teilnehmerImportDialog = new TeilnehmerImportDialog(teilnehmerService, authenticatedUser, veranstaltungService, veranstaltungId, veranstaltungsterminView,this);
            teilnehmerImportDialog.open();
            teilnehmerImportDialog.addDialogCloseActionListener(e -> close());
            this.close();
        });

        configureGrid();

        Button cancelButton = new Button("Abbrechen", e -> close());
        this.setHeaderTitle("Teilnehmer hinzufügen");
        this.getHeader().add(getToolbar());
        getFooter().add(cancelButton);
        getFooter().add(hinzufuegenButton);
        add(
                getContent()
        );

        updateGrid();
    }

    /**
     * Erstellt und gibt eine Toolbar-Komponente zurück.
     * Die Toolbar enthält ein Textfeld zum Filtern, einen Button zum Hinzufügen und einen Button zum Importieren.
     * Das Filter-Textfeld hat einen Platzhaltertext, einen Löschen-Button und einen Listener, der bei Änderungen die Grid-Daten aktualisiert.
     * Die Buttons zum Hinzufügen und Importieren werden zur Toolbar hinzugefügt.
     *
     * @return die erstellte Toolbar als Component
     * @author Tobias
     */
    private Component getToolbar() {
        filterText.setPlaceholder("Suche...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());

        HorizontalLayout toolbar = new HorizontalLayout(filterText,anlegenButton,importButton);

        toolbar.addClassName("toolbar");

        return toolbar;
    }

    /**
     * Aktualisiert die Daten im Grid basierend auf dem aktuellen Suchtext.
     * Ruft alle Teilnehmer ab, die nicht in der Veranstaltung sind, und filtert sie basierend auf dem Suchtext.
     * Der Suchtext wird mit dem Vornamen, Nachnamen und der Matrikelnummer der Teilnehmer verglichen.
     * Die gefilterten Teilnehmer werden dann im Grid angezeigt.
     *
     * @author Tobias
     */
    public void updateGrid() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            String searchText = filterText.getValue().toLowerCase();
            List<Teilnehmer> teilnehmerList = teilnehmerService.findAllTeilnehmerNotInVeranstaltung(veranstaltungId, user);
            List<Teilnehmer> filteredTeilnehmerList = teilnehmerList.stream()
                    .filter(teilnehmer -> teilnehmer.getVorname().toLowerCase().contains(searchText)
                            || teilnehmer.getNachname().toLowerCase().contains(searchText)
                            || Long.toString(teilnehmer.getId()).contains(searchText))
                    .collect(Collectors.toList());
            grid.setItems(filteredTeilnehmerList);
        }
    }

    /**
     * Konfiguriert das Grid für die Anzeige der Teilnehmer.
     * Das Grid wird auf volle Größe gesetzt und der Auswahlmodus auf Mehrfachauswahl.
     * Es werden Spalten für den Vornamen, Nachnamen und die Matrikelnummer der Teilnehmer hinzugefügt, die alle sortierbar sind.
     * Ein Auswahl-Listener wird hinzugefügt, der den Hinzufügen-Button aktiviert, wenn mindestens ein Teilnehmer ausgewählt ist.
     * Das Grid wird so eingestellt, dass mehrere Sortierungen gleichzeitig möglich sind.
     * Schließlich wird das Grid mit den aktuellen Daten aktualisiert.
     *
     * @author Tobias
     */
    private void configureGrid() {
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname").setSortable(true);
        grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname").setSortable(true);
        grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr").setSortable(true);
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            hinzufuegenButton.setEnabled(size != 0);
        });
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        updateGrid();
    }

    /**
     * Erstellt und gibt eine Content-Komponente zurück.
     * Die Content-Komponente ist ein HorizontalLayout, das das Grid enthält.
     * Das Grid wird auf volle Größe gesetzt und erhält den FlexGrow-Wert 1, was bedeutet, dass es den verfügbaren Platz im Layout ausfüllt.
     * Der Content erhält die CSS-Klasse "content" und wird auf volle Größe gesetzt.
     *
     * @return die erstellte Content-Komponente
     * @author Tobias
     */
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(1, grid);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }
}