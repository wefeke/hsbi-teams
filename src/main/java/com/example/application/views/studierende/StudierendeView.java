package com.example.application.views.studierende;

import com.example.application.DoubleToLongConverter;
import com.example.application.ExcelReader.ExcelExporter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.ExcelReader.ExcelExporter;


import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.hibernate.bytecode.enhance.internal.tracker.NoopCollectionTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Route(value = "studierende", layout = MainLayout.class)
@PageTitle(value = "Studierende")
@RolesAllowed({"ADMIN", "USER"})
public class StudierendeView extends VerticalLayout {

    private final ExcelExporter excelExporter;
    private final TeilnehmerService teilnehmerService;
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final Editor<Teilnehmer> editor =grid.getEditor();
    private final TextField filterText = new TextField();
    private final Button addStudiernedenButton = new Button("Studierenden hinzufügen");
    private final StudierendeHinzufuegen dialog;
    private H2 users = new H2("Studierende");
    private final Button delete = new Button("Studierenden löschen");
    private final Component addStudiernedenButtonIcon;
    private final Component deleteIcon;
    private final Button importButton = new Button("Importieren");
    private final Button exportButton = new Button("Exportieren");
    private AuthenticatedUser authenticatedUser;

    TextField vorname = new TextField("Vorname");
    TextField nachname = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button save = new Button("Save");
    Button cancel = new Button ("Cancel");
    Button aufraeumenButton = new Button("Aufräumen");

    @Autowired
    public StudierendeView(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser,ExcelExporter excelExporter) {
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerService = teilnehmerService;
        this.excelExporter = excelExporter;
        Aufraeumen aufraeumenDialog = new Aufraeumen(teilnehmerService, authenticatedUser, this);
        DeleteDialog deleteDialog = new DeleteDialog(teilnehmerService, authenticatedUser, aufraeumenDialog, this);
        StudierendeHinzufuegen studierendeHinzufuegen = new StudierendeHinzufuegen(teilnehmerService, authenticatedUser, this);
        addStudiernedenButtonIcon = addStudiernedenButton.getIcon();
        deleteIcon = delete.getIcon();

        addClassName("Studierenden-view");

        setSizeFull();
        configureGrid();
        add(
                getToolbar(),
                getContent(),
                getToolbar2()
        );
        updateStudierendeView();

        dialog = new StudierendeHinzufuegen(teilnehmerService, authenticatedUser, this);
        addStudiernedenButton.addClickListener(event -> {
            dialog.open();
            updateStudierendeView();
        });

        // CSS Befehle
        delete.setEnabled(false);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.getStyle().set("margin-inline-start", "auto");

        // Click-Listener für den Lösch-Button
        delete.addClickListener(event -> {
            Set<Teilnehmer> selectedTeilnehmer = grid.getSelectedItems();
            if (!selectedTeilnehmer.isEmpty()) {
                for (Teilnehmer teilnehmer : selectedTeilnehmer) {
                    DeleteDialog deleteDialogForSelectedTeilnehmer = new DeleteDialog(teilnehmerService, authenticatedUser, aufraeumenDialog, this);
                    deleteDialogForSelectedTeilnehmer.setTeilnehmer(teilnehmer);
                    deleteDialogForSelectedTeilnehmer.open();
                }
            }
        });

        exportButton.addClickListener(event -> {
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                List<Teilnehmer> teilnehmerList = teilnehmerService.findAllTeilnehmerByUserAndFilter(user, filterText.getValue());
                Notification.show(teilnehmerList.toString());
                File tempFile = createTempFile();
                if (tempFile != null) {
                    System.out.println("Temp file created at: " + tempFile.getAbsolutePath()); // Log the file path
                    Notification.show("File created at: " + tempFile.getAbsolutePath());
                    excelExporter.exportTeilnehmerListe(teilnehmerList, tempFile.getAbsolutePath(), user.getUsername());
                    if (tempFile.exists()) {
                        System.out.println("Temp file exists"); // Check if the file exists
                    } else {
                        System.out.println("Temp file does not exist");
                    }
                    StreamResource resource = offerDownload(tempFile);
                    System.out.println("Resource: " + resource.toString()); // Log the resource object
                }
            }
        });

        UI.getCurrent().getPage().addBrowserWindowResizeListener(event -> {
            if (event.getWidth() <= 1000) {
                makeButtonsSmall();
            } else {
                restoreButtons();
            }
        });

        aufraeumenButton.addClickListener(event -> aufraeumenDialog.open());
    }

    public void updateStudierendeView() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            grid.setItems(teilnehmerService.findAllTeilnehmerByUserAndFilter(user, filterText.getValue()));
        }
    }
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(1, grid);
        content.addClassName("content");
        content.setSizeFull();


        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        Grid.Column<Teilnehmer> vornameColumn = grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname").setSortable(true);;
        Grid.Column<Teilnehmer> nachnameColumn = grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname").setSortable(true);;
        Grid.Column<Teilnehmer> matrikelNrColumn = grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr").setSortable(true);;
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            delete.setEnabled(size != 0);
        });
        Grid.Column<Teilnehmer> editColumn = grid.addComponentColumn(teilnehmer -> {
            Button editButton = new Button(LineAwesomeIcon.EDIT.create());
            editButton.addClickListener(click -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.select(teilnehmer); // Zeile auswählen
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
                .withConverter(new DoubleToLongConverter())
                .bind(Teilnehmer::getId, Teilnehmer::setId);
        matrikelNrColumn.setEditorComponent(matrikelNr);
        matrikelNr.setEnabled(false);

        Button saveButton = new Button("Save", e -> {
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

    private Component getToolbar() {
        filterText.setPlaceholder("Name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateStudierendeView());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addStudiernedenButton, delete);

        toolbar.addClassName("toolbar");

        return toolbar;
    }
    private Component getToolbar2() {
        HorizontalLayout toolbar2 = new HorizontalLayout(importButton, exportButton, aufraeumenButton);

        toolbar2.addClassName("toolbar");

        return toolbar2;
    }

    private void makeButtonsSmall() {

        addStudiernedenButton.setText("+");
        delete.setText("-");
    }

    private void restoreButtons() {
        addStudiernedenButton.setIcon(addStudiernedenButtonIcon);
        addStudiernedenButton.setText("Studierenden hinzufügen");

        delete.setIcon(deleteIcon);
        delete.setText("Studierenden löschen");

    }
    private File createTempFile() {
        try {
            // Pfad zum Download-Ordner
            String downloadFolderPath = System.getProperty("user.home") + "/Downloads";

            // Erstellen Sie das Verzeichnis, wenn es noch nicht existiert
            File dir = new File(downloadFolderPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            // Erstellen Sie die Datei im Download-Ordner
            File tempFile = File.createTempFile("export", ".xlsx", dir);
            return tempFile;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private StreamResource offerDownload(File file) {
        System.out.println("File name: " + file.getName());
    return new StreamResource(file.getName(), () -> {
        try {

            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("File could not be offered: " + file.getName());
            e.printStackTrace();

            return null;
        }
    });
    }
}