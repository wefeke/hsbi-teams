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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    private final Dialog dialog = new Dialog();
    private H2 users = new H2("Studierende");
    private final Button delete = new Button("Studierenden löschen");
    private final Button aendern = new Button ("Studierende ändern");
    private final Component addStudiernedenButtonIcon;
    private final Component deleteIcon;
    private final Component aendernIcon;
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
        addStudiernedenButtonIcon = addStudiernedenButton.getIcon();
        deleteIcon = delete.getIcon();
        aendernIcon = aendern.getIcon();

        addClassName("Studierenden-view");

        setSizeFull();
        configureGrid();
        add(
                getToolbar(),
                getContent(),
                getToolbar2()
        );
        updateStudierendeView();
        addStudiernedenButton.addClickListener(event -> openDialog());
        configureDialog();

        // CSS Befehle
        delete.setEnabled(false);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.getStyle().set("margin-inline-start", "auto");

        aendern.setEnabled(false);
        aendern.addThemeVariants(ButtonVariant.LUMO_ERROR);
        aendern.getStyle().set("margin-inline-start", "auto");

        // Click-Listener für den Lösch-Button
        delete.addClickListener(event -> {
            List<Teilnehmer> selectedTeilnehmer = new ArrayList<>(grid.getSelectedItems());
            if (!selectedTeilnehmer.isEmpty()) {
                deleteDialog.openDeleteDialog(selectedTeilnehmer);
            }
        });
        // Click-Listener für den Ändern-Button
//        aendern.addClickListener(event -> {
//            Teilnehmer selectedTeilnehmer = grid.asSingleSelect().getValue();
//            if (selectedTeilnehmer != null) {
//                aendernDiolog(selectedTeilnehmer);
//            }
//        });

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
            aendern.setEnabled(size != 0);
        });
        Grid.Column<Teilnehmer> editColumn = grid.addComponentColumn(teilnehmer -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(click -> {
                if (editor.isOpen())
                    editor.cancel();
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

        Button saveButton = new Button("Save", e -> editor.save());
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

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addStudiernedenButton, delete, aendern);

        toolbar.addClassName("toolbar");

        return toolbar;
    }
    private Component getToolbar2() {
        HorizontalLayout toolbar2 = new HorizontalLayout(importButton, exportButton, aufraeumenButton);

        toolbar2.addClassName("toolbar");

        return toolbar2;
    }


    private void openDialog() {
        dialog.open();
        updateStudierendeView();
    }

    private void configureDialog() {
        dialog.add(new StudierendeHinzufuegen(teilnehmerService, authenticatedUser));
        dialog.setWidth("400px");
        dialog.setHeight("300px");

    }

//    private void aendernDiolog (Teilnehmer teilnehmer) {
//        FormLayout form = new FormLayout();
//        Dialog aendernDiolog = new Dialog(form);
//
//        Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);
//
//        binder.forField(vorname)
//                .bind(Teilnehmer::getVorname, Teilnehmer::setVorname).isAsRequiredEnabled();
//        binder.forField(nachname)
//                .bind(Teilnehmer::getNachname, Teilnehmer::setNachname).isAsRequiredEnabled();
//        binder.forField(matrikelNr)
//                .withConverter(new DoubleToLongConverter())
//                .bind(Teilnehmer::getId, Teilnehmer::setId).isAsRequiredEnabled();
//        matrikelNr.setEnabled(false);
//
//        binder.setBean(teilnehmer);
//
//        form.add(vorname, nachname, matrikelNr, save, cancel);
//        aendernDiolog.add(form);
//
//        aendernDiolog.open();
//        aendernDiolog.setWidth("450px");
//        aendernDiolog.setHeight("350px");
//
//        save.addClickListener(event -> {
//            Teilnehmer selectedTeilnehmer = grid.asSingleSelect().getValue();
//            if ((teilnehmer != null)) {
//                Optional<User> maybeUser = authenticatedUser.get();
//                User user = maybeUser.get();
//                binder.writeBeanIfValid(teilnehmer);
//                teilnehmerService.saveTeilnehmer(teilnehmer, user);
//                Notification.show("Daten erfolgreich aktualisiert");
//                updateStudierendeView();
//                aendernDiolog.close();
//            }
//        });
//        cancel.addClickListener(event -> {
//            aendernDiolog.close();
//        });
//
//    }

    private void makeButtonsSmall() {

        addStudiernedenButton.setText("+");
        delete.setText("-");
        aendern.setText("...");
    }

    private void restoreButtons() {
        addStudiernedenButton.setIcon(addStudiernedenButtonIcon);
        addStudiernedenButton.setText("Studierenden hinzufügen");

        delete.setIcon(deleteIcon);
        delete.setText("Studierenden löschen");

        aendern.setIcon(aendernIcon);
        aendern.setText("Studierende ändern");
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