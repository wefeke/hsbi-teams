package com.example.application.views.studierende;

import com.example.application.DoubleToLongConverter;
import com.example.application.ExcelReader.TeilnehmerExcelExporter;
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


import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.hibernate.bytecode.enhance.internal.tracker.NoopCollectionTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;


import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Route(value = "studierende", layout = MainLayout.class)
@PageTitle(value = "Studierende")
@RolesAllowed({"ADMIN", "USER"})
public class StudierendeView extends VerticalLayout {
    private final TeilnehmerService teilnehmerService;
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final Editor<Teilnehmer> editor =grid.getEditor();
    private final TextField filterText = new TextField();
    private final Button addStudiernedenButton = new Button("Studierenden hinzufügen");
    private final StudierendeHinzufuegen dialog;
    private H2 users = new H2("Studierende");
    private final Button delete = new Button("Studierenden löschen");
    //private final Button aendern = new Button ("Studierende ändern");
    private final Component addStudiernedenButtonIcon;
    private final Component deleteIcon;
    //private final Component aendernIcon;
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
    public StudierendeView(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerService = teilnehmerService;
        Aufraeumen aufraeumenDialog = new Aufraeumen(teilnehmerService, authenticatedUser, this);
        DeleteDialog deleteDialog = new DeleteDialog(teilnehmerService, authenticatedUser, aufraeumenDialog, this);
        StudierendeHinzufuegen studierendeHinzufuegen = new StudierendeHinzufuegen(teilnehmerService, authenticatedUser, this);
        addStudiernedenButtonIcon = addStudiernedenButton.getIcon();
        deleteIcon = delete.getIcon();
        //aendernIcon = aendern.getIcon();

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

//        aendern.setEnabled(false);
//        aendern.addThemeVariants(ButtonVariant.LUMO_ERROR);
//        aendern.getStyle().set("margin-inline-start", "auto");

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
            //aendern.setEnabled(size != 0);
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
    }

    private void restoreButtons() {
        addStudiernedenButton.setIcon(addStudiernedenButtonIcon);
        addStudiernedenButton.setText("Studierenden hinzufügen");

        delete.setIcon(deleteIcon);
        delete.setText("Studierenden löschen");

    }

    private File createTempFile() {
        try {
            // Create a temporary file in the system's default temporary-file directory
            File tempFile = File.createTempFile("export", ".xlsx");
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private StreamResource offerDownload(File file) {
        return new StreamResource(file.getName(), () -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        });
    }


}