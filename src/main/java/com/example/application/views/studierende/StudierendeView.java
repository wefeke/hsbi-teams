package com.example.application.views.studierende;

import com.example.application.ExcelReader.TeilnehmerExcelExporter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;
import java.util.stream.Collectors;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Diese Klasse repräsentiert die Ansicht der Studierenden in der Anwendung.
 * Sie erbt von der VerticalLayout-Klasse von Vaadin und stellt eine Tabelle (Grid) zur Anzeige der Studierenden bereit.
 * Die Klasse bietet auch eine Werkzeugleiste mit Funktionen zum Hinzufügen, Löschen, Importieren und Exportieren von Studierenden.
 * Darüber hinaus ermöglicht sie das Bearbeiten der Daten der Studierenden direkt in der Tabelle.
 * Die Klasse verwendet einen TeilnehmerService zum Abrufen und Speichern der Studierenden und einen AuthenticatedUser zur Authentifizierung.
 * Sie enthält auch Dialoge zum Hinzufügen, Importieren und Aufräumen von Studierenden.
 *
 * @author Tobias
 */
@Route(value = "studierende", layout = MainLayout.class)
@PageTitle(value = "Studierende")
@RolesAllowed({"ADMIN", "USER"})
public class StudierendeView extends VerticalLayout {

    private final TeilnehmerService teilnehmerService;
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final Editor<Teilnehmer> editor = grid.getEditor();
    private final TextField filterText = new TextField();
    private final Button addStudiernedenButton = new Button(VaadinIcon.PLUS.create());
    private final StudierendeHinzufuegenDialog dialog;
    private final Button delete = new Button(VaadinIcon.TRASH.create());
    private final Button importButton = new Button("Importieren");
    private final Button exportButton = new Button("Exportieren");
    private final AuthenticatedUser authenticatedUser;
    private final StudierendeImportDialog studierendeImportDialog;

    TextField vorname = new TextField("Vorname");
    TextField nachname = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button aufraeumenButton = new Button("Aufräumen");

    /**
     * Konstruktor für die Klasse StudierendeView.
     * Dieser Konstruktor initialisiert den authentifizierten Benutzer, den Service zur Handhabung von Teilnehmern und die Dialoge zum Importieren und Aufräumen von Teilnehmern.
     * Darüber hinaus wird die Ansicht eingerichtet, einschließlich des Grids zur Anzeige der Teilnehmer, der Werkzeugleiste und der Schaltflächen zum Hinzufügen, Löschen, Importieren und Aufräumen von Teilnehmern.
     *
     * @param teilnehmerService Der Service zur Handhabung von Teilnehmern.
     * @param authenticatedUser Der aktuell authentifizierte Benutzer.
     * @author Tobias
     */
    @Autowired
    public StudierendeView(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerService = teilnehmerService;
        TeilnehmerAufraeumenDialog teilnehmerAufraeumenDialogDialog = new TeilnehmerAufraeumenDialog(teilnehmerService, authenticatedUser, this);
        this.studierendeImportDialog = new StudierendeImportDialog(teilnehmerService, authenticatedUser, this);

        TeilnehmerAufraeumenDialog aufraeumenDialog = new TeilnehmerAufraeumenDialog(teilnehmerService, authenticatedUser, this);
        addClassName("Studierenden-view");
        Optional<User> maybeUser = authenticatedUser.get();
        maybeUser.ifPresent(value -> {
        });

        setSizeFull();
        configureGrid();
        add(
                getToolbar(),
                getContent(),
                getToolbar2()
        );
        updateStudierendeView();

        dialog = new StudierendeHinzufuegenDialog(teilnehmerService, authenticatedUser,this);
        addStudiernedenButton.addClickListener(event -> {
            dialog.open();
            updateStudierendeView();
        });

        delete.setEnabled(false);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.getStyle().set("margin-inline-start", "auto");

        delete.addClickListener(event -> {
            Set<Teilnehmer> selectedTeilnehmer = grid.getSelectedItems();
            if (!selectedTeilnehmer.isEmpty()) {
                for (Teilnehmer teilnehmer : selectedTeilnehmer) {
                    TeilnehmerLoeschenDialog deleteDialogForSelectedTeilnehmer = new TeilnehmerLoeschenDialog(teilnehmerService, aufraeumenDialog, this);
                    deleteDialogForSelectedTeilnehmer.setTeilnehmer(teilnehmer);
                    deleteDialogForSelectedTeilnehmer.open();
                }
            }
        });

        importButton.addClickListener(this::onComponentEvent);

        aufraeumenButton.addClickListener(event -> teilnehmerAufraeumenDialogDialog.open());
    }

    /**
     * Aktualisiert die Ansicht der Studierenden in der Tabelle (Grid).
     * Es werden alle Studierenden, die dem authentifizierten Benutzer zugeordnet sind, abgerufen und basierend auf dem Suchtext gefiltert.
     * Der Suchtext wird mit dem Vornamen, Nachnamen und der ID (Matrikelnummer) der Studierenden verglichen.
     * Die gefilterte Liste der Studierenden wird dann als Elemente der Tabelle gesetzt.
     *
     * @author Tobias
     */
    public void updateStudierendeView() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            String searchText = filterText.getValue().toLowerCase();
            List<Teilnehmer> teilnehmerList = teilnehmerService.findAllTeilnehmer(user);
            List<Teilnehmer> filteredTeilnehmerList = teilnehmerList.stream()
                    .filter(teilnehmer -> teilnehmer.getVorname().toLowerCase().contains(searchText)
                            || teilnehmer.getNachname().toLowerCase().contains(searchText)
                            || Long.toString(teilnehmer.getId()).contains(searchText))
                    .collect(Collectors.toList());
            grid.setItems(filteredTeilnehmerList);
        }
    }

    /**
     * Erstellt und gibt eine Komponente zurück, die das Hauptlayout für die Anzeige der Studierenden enthält.
     * Das Layout ist ein HorizontalLayout, das ein Grid enthält, welches die Studierenden anzeigt.
     * Das Grid nimmt den gesamten verfügbaren Platz im Layout ein.
     *
     * @return Eine Komponente, die das Hauptlayout für die Anzeige der Studierenden enthält.
     * @author Tobias
     */
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(1, grid);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    /**
     * Konfiguriert das Grid für die Anzeige der Studierenden.
     * Es werden Spalten für Vorname, Nachname und Matrikelnummer erstellt und entsprechende Header gesetzt.
     * Die Spalten sind sortierbar.
     * Ein Editor wird konfiguriert, um die Daten der Studierenden zu bearbeiten.
     * Der Editor verwendet einen Binder, um die Daten der Studierenden an die Felder zu binden.
     * Bei Auswahl eines Studierenden im Grid wird der Lösch-Button aktiviert.
     * Ein Button zum Speichern und ein Button zum Abbrechen der Bearbeitung werden hinzugefügt.
     * Bei Klick auf den Speichern-Button wird der aktuell bearbeitete Studierende gespeichert.
     * Bei Klick auf den Abbrechen-Button wird die Bearbeitung abgebrochen.
     *
     * @author Tobias
     */
    private void configureGrid() {
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        Grid.Column<Teilnehmer> vornameColumn = grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname").setSortable(true);
        Grid.Column<Teilnehmer> nachnameColumn = grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname").setSortable(true);
        Grid.Column<Teilnehmer> matrikelNrColumn = grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr").setSortable(true);
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            delete.setEnabled(size != 0);

            if (editor.isOpen()) {
                editor.cancel();
            }
        });
        Grid.Column<Teilnehmer> editColumn = grid.addComponentColumn(teilnehmer -> {
            Button editButton = new Button(LineAwesomeIcon.EDIT.create());
            editButton.addClickListener(click -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.select(teilnehmer);
                grid.getEditor().editItem(teilnehmer);
            });
            return editButton;
        });
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);

        Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        binder.forField(vorname)
                .asRequired("Vorname muss gefüllt sein")
                .bind(Teilnehmer::getVorname, Teilnehmer::setVorname);
        vornameColumn.setEditorComponent(vorname);

        binder.forField(nachname)
                .asRequired("Nachname muss gefüllt sein")
                .bind(Teilnehmer::getNachname, Teilnehmer::setNachname);
        nachnameColumn.setEditorComponent(nachname);

        binder.forField(matrikelNr)
                .asRequired("Matrikelnummer muss gefüllt sein")
                .withConverter(Double::longValue, Long::doubleValue)
                .bind(Teilnehmer::getId, Teilnehmer::setId);
        matrikelNrColumn.setEditorComponent(matrikelNr);
        matrikelNr.setEnabled(false);

        Button saveButton = new Button(VaadinIcon.CHECK.create(), e -> {
            editor.save();
            Teilnehmer updatedTeilnehmer = grid.asSingleSelect().getValue();
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                teilnehmerService.saveTeilnehmer(updatedTeilnehmer, user);
                Notification.show("Teilnehmer wurde aktualisiert");
            } else {
                Notification.show("Fehler beim Speichern");
            }
        });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);
    }

    /**
     * Erstellt und gibt eine Komponente zurück, die die Werkzeugleiste für die Studierendenansicht enthält.
     * Die Werkzeugleiste besteht aus einem Filtertextfeld und zwei Buttons zum Hinzufügen und Löschen von Studierenden.
     * Das Filtertextfeld hat einen Platzhaltertext, einen Löschen-Button und einen Listener, der die Studierendenansicht aktualisiert, wenn sich der Text ändert.
     *
     * @return Eine Komponente, die die Werkzeugleiste für die Studierendenansicht enthält.
     * @author Tobias
     */
    private Component getToolbar() {
        filterText.setPlaceholder("Suche...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateStudierendeView());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addStudiernedenButton, delete);

        toolbar.addClassName("toolbar");

        return toolbar;
    }

    /**
     * Erstellt und gibt eine Komponente zurück, die die zweite Werkzeugleiste für die Studierendenansicht enthält.
     * Die Werkzeugleiste besteht aus einem Import-Button, einem versteckten Download-Link (Anchor), einem Export-Button und einem Aufräumen-Button.
     * Der Download-Link wird mit einer StreamResource verbunden, die eine Excel-Datei mit den Studierendendaten generiert.
     * Der Export-Button löst einen Klick auf den Download-Link aus, um den Download der Excel-Datei zu starten.
     *
     * @return Eine Komponente, die die zweite Werkzeugleiste für die Studierendenansicht enthält.
     * @author Tobias
     */
    private Component getToolbar2() {

        Anchor anchor = new Anchor();
        anchor.setText("Download");
        anchor.getElement().getStyle().set("display", "none");
        anchor.getElement().setAttribute("download", true);

        exportButton.addClickListener(event -> {
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                List<Teilnehmer> teilnehmerList = teilnehmerService.findAllTeilnehmerByUserAndFilter(user, filterText.getValue());

                StreamResource resource = new StreamResource("teilnehmerliste_" + LocalDate.now() + ".xlsx", () -> {
                    byte[] data;
                    try {
                        TeilnehmerExcelExporter teilnehmerExcelExporter = new TeilnehmerExcelExporter();
                        data = teilnehmerExcelExporter.export(teilnehmerList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new ByteArrayInputStream(data);
                });
                anchor.setHref(resource);

                anchor.getElement().callJsFunction("click");
            }
        });


        HorizontalLayout toolbar2 = new HorizontalLayout(importButton, anchor, exportButton, aufraeumenButton);
        toolbar2.setWidthFull();
        toolbar2.addClassName("toolbar");
        toolbar2.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return toolbar2;
    }


    private void onComponentEvent(ClickEvent<Button> event) {
        studierendeImportDialog.open();
    }
}