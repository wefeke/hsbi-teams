package com.example.application.views.studierende;

import com.example.application.DoubleToLongConverter;
import com.example.application.models.Teilnehmer;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
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

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

@Route(value = "studierende", layout = MainLayout.class)
@PageTitle(value = "Studierende")
@RolesAllowed({"ADMIN", "USER"})
public class StudierendeView extends VerticalLayout {
    private final TeilnehmerService teilnehmerService;
    private final Grid<Teilnehmer> grid = new Grid<>();
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
    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);

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
        DeleteDialog deleteDialog = new DeleteDialog(teilnehmerService);
        Aufraeumen aufraeumenDialog = new Aufraeumen(teilnehmerService);
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
            Teilnehmer selectedTeilnehmer = grid.asSingleSelect().getValue();
            if (selectedTeilnehmer != null) {
                deleteDialog.openDeleteDialog(selectedTeilnehmer);
            }
        });
        // Click-Listener für den Ändern-Button
        aendern.addClickListener(event -> {
            Teilnehmer selectedTeilnehmer = grid.asSingleSelect().getValue();
            if (selectedTeilnehmer != null) {
                aendernDiolog(selectedTeilnehmer);
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
       /* UI.getCurrent().getPage().addBrowserWindowResizeListener(event -> {
            if (event.getWidth() > 500) {
                restoreButtons();
            } else {
                makeButtonsSmall();
            }
        });
*/
    }

    public void updateStudierendeView() {
        grid.setItems(teilnehmerService.findAllTeilnehmer(filterText.getValue()));
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
        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname").setSortable(true);;
        grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname").setSortable(true);;
        grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr").setSortable(true);;
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            delete.setEnabled(size != 0);
            aendern.setEnabled(size != 0);
        });
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
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


    private void setTeilnehmer(Teilnehmer teilnehmer) {
        vorname.setValue(teilnehmer.getVorname());
        nachname.setValue(teilnehmer.getNachname());
        matrikelNr.setValue(teilnehmer.getId().doubleValue());
    }

    private void aendernDiolog (Teilnehmer teilnehmer) {
        FormLayout form = new FormLayout();
        Dialog aendernDiolog = new Dialog(form);

        //bindFields();
        setTeilnehmer(teilnehmer);

        form.add(vorname, nachname, matrikelNr, save, cancel);
        aendernDiolog.add(form);

        aendernDiolog.open();
        aendernDiolog.setWidth("450px");
        aendernDiolog.setHeight("350px");

        save.addClickListener(event -> {
            Teilnehmer selectedTeilnehmer = grid.asSingleSelect().getValue();
            if (selectedTeilnehmer != null) {


                selectedTeilnehmer.setVorname(vorname.getValue());
                selectedTeilnehmer.setNachname(nachname.getValue());
                selectedTeilnehmer.setId(matrikelNr.getValue().longValue());
                teilnehmerService.saveTeilnehmer(selectedTeilnehmer);
                Notification.show("Daten erfolgreich aktualisiert");
                updateStudierendeView();
                aendernDiolog.close();
            }
        });
        cancel.addClickListener(event -> {
            aendernDiolog.close();
        });
    }
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
}
/*
    private void bindFields(){
        binder.forField(vorname)
                .bind(Teilnehmer::getVorname, Teilnehmer::setVorname);
        binder.forField(nachname)
                .bind(Teilnehmer::getNachname, Teilnehmer::setNachname);
        binder.forField(matrikelNr)
                .withConverter(new DoubleToLongConverter())
                .bind(Teilnehmer::getId, Teilnehmer::setId);
    }
*/
