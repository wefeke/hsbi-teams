package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.studierende.StudierendeHinzufuegen;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Optional;
import java.util.Set;

public class TeilnehmerHinzufuegenDialog extends Dialog {

    private final VeranstaltungenService veranstaltungService;
    private final TeilnehmerService teilnehmerService;
    private final Long veranstaltungId;
    private final Button hinzufuegenButton = new Button("Hinzufügen");
    private final Button anlegenButton = new Button("Teilnehmer anlegen");
    private final Button importButton = new Button("importieren");
    private final TextField filterText = new TextField();
    private final Grid<Teilnehmer> grid = new Grid<>();
    private AuthenticatedUser authenticatedUser;
    private final Dialog dialog = new Dialog();

    public TeilnehmerHinzufuegenDialog(VeranstaltungenService veranstaltungService, TeilnehmerService teilnehmerService, Long veranstaltungId, AuthenticatedUser authenticatedUser) {
        this.veranstaltungService = veranstaltungService;
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungId = veranstaltungId;
        this.authenticatedUser = authenticatedUser;

        this.setWidth("80vw");
        this.setHeight("80vh");



        hinzufuegenButton.setEnabled(false);
        hinzufuegenButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        hinzufuegenButton.getStyle().set("margin-inline-start", "auto");
        updateGrid();

        hinzufuegenButton.addClickListener(event -> {
            Set<Teilnehmer> selectedTeilnehmer = grid.getSelectedItems();
            if (!selectedTeilnehmer.isEmpty()) {
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    for (Teilnehmer teilnehmer : selectedTeilnehmer) {
                        teilnehmerService.addTeilnehmerToVeranstaltung(teilnehmer, veranstaltungId, user);
                    }
                    Notification.show(selectedTeilnehmer.size() + " Studierende wurden zur Veranstaltung hinzugefügt");
                    updateGrid();
                }
            }
        });

        anlegenButton.addClickListener(event ->{openDialog();

        });
        configureGrid();
        configureDialog();
        // Erstellen Sie den Abbrechen-Button
        Button cancelButton = new Button("Abbrechen", e -> close());
        add(cancelButton,

                getToolbar(),
                getContent()

        );

    }

    private Component getToolbar() {
        filterText.setPlaceholder("Name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());

        HorizontalLayout toolbar = new HorizontalLayout(filterText,anlegenButton,importButton, hinzufuegenButton);

        toolbar.addClassName("toolbar");

        return toolbar;
    }

    private void updateGrid() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            grid.setItems(teilnehmerService.findAllTeilnehmerNotInVeranstaltung(veranstaltungId,user));
        }
    }

        private void configureGrid() {
            grid.setSizeFull();
            grid.setSelectionMode(Grid.SelectionMode.MULTI);
            grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname").setSortable(true);
            ;
            grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname").setSortable(true);
            ;
            grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr").setSortable(true);
            ;
            grid.addSelectionListener(selection -> {
                int size = selection.getAllSelectedItems().size();
                hinzufuegenButton.setEnabled(size != 0);
            });
            grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
            updateGrid();
        }
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(1, grid);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }
    private void openDialog() {
        dialog.open();
        updateGrid();
    }

    private void configureDialog() {
        dialog.add(new StudierendeHinzufuegen(teilnehmerService, authenticatedUser));
        dialog.setWidth("400px");
        dialog.setHeight("300px");

    }
}
