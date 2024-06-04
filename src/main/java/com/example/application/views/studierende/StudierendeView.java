package com.example.application.views.studierende;

import com.example.application.models.Teilnehmer;
import com.example.application.services.TeilnehmerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
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

import java.awt.*;

@Route(value = "studierende", layout = MainLayout.class)
@PageTitle("Studierende")
@RolesAllowed({"ADMIN"})
public class StudierendeView extends VerticalLayout {
    private final TeilnehmerService teilnehmerService;
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final TextField filterText = new TextField();
    private final Button addStudiernedenButton = new Button("Studierenden hinzufügen");
    private final Dialog dialog = new Dialog();
    private H2 users = new H2("Studierende");
    private final Button delete = new Button("Studierenden löschen");
    private final Button aendern = new Button ("Studierende ändern");
    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);

    @Autowired
    public StudierendeView(TeilnehmerService teilnehmerService) {
        this.teilnehmerService = teilnehmerService;
        addClassName("Studierenden-view");

        setSizeFull();
        configureGrid();
        add(
                getToolbar(),
                getContent()
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
                deleteDialog(selectedTeilnehmer);
            }
        });

        aendern.addClickListener(event -> {
            Teilnehmer selectedTeilnehmer = grid.asSingleSelect().getValue();
            if (selectedTeilnehmer != null) {
                saveDiolog(selectedTeilnehmer);
            }
        });
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
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
        grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
        grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr");
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            delete.setEnabled(size != 0);
            aendern.setEnabled(size != 0);
        });
        grid.setSizeFull();
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

    private void openDialog() {
        dialog.open();
        updateStudierendeView();
    }

    private void configureDialog() {
        dialog.add(new StudierendeHinzufuegen(teilnehmerService));
        dialog.setWidth("400px");
        dialog.setHeight("300px");

    }

    private void deleteDialog(Teilnehmer teilnehmer) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.add(new Text("Möchten Sie den Studierenden " + teilnehmer.getVorname()+" " + teilnehmer.getNachname()+ " wirklich löschen?"));

        Button yesButton = new Button("Ja", event -> {
            teilnehmerService.deleteTeilnehmer(teilnehmer);
            Notification.show("Studierender gelöscht");
            updateStudierendeView();
            confirmationDialog.close();
        });

        Button noButton = new Button("Nein", event -> {
            confirmationDialog.close();
        });

        confirmationDialog.add(yesButton, noButton);
        confirmationDialog.open();
    }

    private void saveDiolog (Teilnehmer teilnehmer){
        FormLayout form = new FormLayout();
        Dialog saveDialog = new Dialog(form);
        TextField Vorname = new TextField("Vorname");
        TextField Nachname = new TextField("Nachname");
        NumberField matrikelNr = new NumberField("Matrikelnummer");
        Button save = new Button("Save");
        Button cancel = new Button ("Cancel");


        saveDialog.add(Vorname);
        saveDialog.add(Nachname);
        saveDialog.add(matrikelNr);
        saveDialog.add(save);
        saveDialog.add(cancel);

        saveDialog.open();
        saveDialog.setWidth("400px");
        saveDialog.setHeight("300px");
    }

}