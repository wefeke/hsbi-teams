package com.example.application.views.studierende;


import com.example.application.ExcelReader.TeilnehmerExcelExporter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.views.MainLayout;
import com.example.application.views.veranstaltungstermin.TeilnehmerEntfernenDialog;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
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

@Route(value = "studierende", layout = MainLayout.class)
@PageTitle(value = "Studierende")
@RolesAllowed({"ADMIN", "USER"})
public class StudierendeView extends VerticalLayout {

    private final TeilnehmerExcelExporter teilnehmerExcelExporter;
    private final TeilnehmerService teilnehmerService;
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final Editor<Teilnehmer> editor = grid.getEditor();
    private final TextField filterText = new TextField();
    private final Button addStudiernedenButton = new Button("Studierenden hinzufügen");
    private final StudierendeHinzufuegenDialog dialog;
    private H2 users = new H2("Studierende");
    private final Button delete = new Button("Studierenden löschen");
    private final Component addStudiernedenButtonIcon;
    private final Component deleteIcon;
    private final Button importButton = new Button("Importieren");
    private final Button exportButton = new Button("Exportieren");
    private AuthenticatedUser authenticatedUser;
    private final StudierendeImportDialog studierendeImportDialog;

    TextField vorname = new TextField("Vorname");
    TextField nachname = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button aufraeumenButton = new Button("Aufräumen");


    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private final Upload upload = new Upload(buffer);
//    ExcelImporter excelImporter;
    Set<Teilnehmer> newTeilnehmerListe = new HashSet<>();
    private User user;

    @Autowired
    public StudierendeView(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, TeilnehmerExcelExporter teilnehmerExcelExporter) {
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerService = teilnehmerService;
        TeilnehmerAufraeumenDialog teilnehmerAufraeumenDialogDialog = new TeilnehmerAufraeumenDialog(teilnehmerService, authenticatedUser, this);
        TeilnehmerLoeschenDialog teilnehmerLoeschenDialog = new TeilnehmerLoeschenDialog(teilnehmerService, authenticatedUser, teilnehmerAufraeumenDialogDialog, this);
        StudierendeHinzufuegenDialog studierendeHinzufuegenDialog = new StudierendeHinzufuegenDialog(teilnehmerService, authenticatedUser, this);
        this.teilnehmerExcelExporter = teilnehmerExcelExporter;
//        this.excelImporter = new ExcelImporter(teilnehmerService, authenticatedUser);
        this.studierendeImportDialog = new StudierendeImportDialog(teilnehmerService, authenticatedUser, this);

        TeilnehmerAufraeumenDialog aufraeumenDialog = new TeilnehmerAufraeumenDialog(teilnehmerService, authenticatedUser, this);
        TeilnehmerLoeschenDialog deleteDialog = new TeilnehmerLoeschenDialog(teilnehmerService, authenticatedUser, aufraeumenDialog, this);
        StudierendeHinzufuegenDialog studierendeHinzufuegen = new StudierendeHinzufuegenDialog(teilnehmerService, authenticatedUser, this);
        addStudiernedenButtonIcon = addStudiernedenButton.getIcon();
        deleteIcon = delete.getIcon();
        //aendernIcon = aendern.getIcon();

        addClassName("Studierenden-view");
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            this.user = maybeUser.get();
        }

        setSizeFull();
        configureGrid();
        add(
                getToolbar(),
                getContent(),
                getToolbar2()
        );
        updateStudierendeView();

        dialog = new StudierendeHinzufuegenDialog(teilnehmerService, authenticatedUser, this);
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
                    TeilnehmerLoeschenDialog deleteDialogForSelectedTeilnehmer = new TeilnehmerLoeschenDialog(teilnehmerService, authenticatedUser, aufraeumenDialog, this);
                    deleteDialogForSelectedTeilnehmer.setTeilnehmer(teilnehmer);
                    deleteDialogForSelectedTeilnehmer.open();
                }
            }
        });

        importButton.addClickListener(event -> {
            studierendeImportDialog.open();
        });


        UI.getCurrent().getPage().addBrowserWindowResizeListener(event -> {
            if (event.getWidth() <= 1000) {
                makeButtonsSmall();
            } else {
                restoreButtons();
            }
        });

        aufraeumenButton.addClickListener(event -> teilnehmerAufraeumenDialogDialog.open());
    }

    public void updateStudierendeView() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            String searchText = filterText.getValue().toLowerCase();
            List<Teilnehmer> teilnehmerList = teilnehmerService.findAllTeilnehmer(user);
            List<Teilnehmer> filteredTeilnehmerList = teilnehmerList.stream()
                    .filter(teilnehmer -> teilnehmer.getVorname().toLowerCase().contains(searchText)
                            || teilnehmer.getNachname().toLowerCase().contains(searchText)
                            || Long.toString(teilnehmer.getId()).contains(searchText)) // Vergleicht den Suchtext mit der Matrikelnummer
                    .collect(Collectors.toList());
            grid.setItems(filteredTeilnehmerList);
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
                .withConverter(d -> Double.valueOf(d).longValue(), Long::doubleValue)
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

        // Ein Anchor, unter welchem der Download der Daten möglich ist
        Anchor anchor = new Anchor();
        anchor.setText("Download");
        anchor.getElement().getStyle().set("display", "none");
        anchor.getElement().setAttribute("download", true);
        Optional<User> maybeUser = authenticatedUser.get();

        User user = maybeUser.get();
        List<Teilnehmer> teilnehmerList = teilnehmerService.findAllTeilnehmerByUserAndFilter(user, filterText.getValue());

        // Die eigentlichen Daten werden in diesem Objekt gespeichert und dem Anchor übergeben
        StreamResource resource = new StreamResource("teilnehmerliste_" + LocalDate.now() + ".xlsx", () -> {
            byte[] data = null; // Your method to fetch data
            try {
                TeilnehmerExcelExporter teilnehmerExcelExporter = new TeilnehmerExcelExporter();
                data = teilnehmerExcelExporter.export(teilnehmerList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new ByteArrayInputStream(data);
        });
        anchor.setHref(resource);

        exportButton.addClickListener(event -> {
            anchor.getElement().callJsFunction("click");
        });

        HorizontalLayout toolbar2 = new HorizontalLayout(importButton, anchor, exportButton, aufraeumenButton);

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