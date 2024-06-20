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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TeilnehmerHinzufuegenDialog extends Dialog {

    private final TeilnehmerService teilnehmerService;
    private final Long veranstaltungId;
    private final Button hinzufuegenButton = new Button("Hinzufügen");
    private final Button anlegenButton = new Button("Teilnehmer anlegen");
    private final Button importButton = new Button("importieren");
    private final TextField filterText = new TextField();
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final TeilnehmerErstellenDialog dialog;
    private final AuthenticatedUser authenticatedUser;

    public TeilnehmerHinzufuegenDialog(VeranstaltungenService veranstaltungService, TeilnehmerService teilnehmerService, Long veranstaltungId, AuthenticatedUser authenticatedUser, VeranstaltungDetailView veranstaltungDetailView, Veranstaltungstermin veranstaltungstermin, Gruppenarbeit gruppenarbeit) {
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungId = veranstaltungId;
        this.authenticatedUser = authenticatedUser;

        this.setWidth("80vw");
        this.setHeight("80vh");

        hinzufuegenButton.setEnabled(false);
        hinzufuegenButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        hinzufuegenButton.getStyle().set("margin-inline-start", "auto");

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
                        veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(veranstaltungstermin);

                        if (gruppenarbeit != null) {
                            veranstaltungDetailView.setAktiveKachelGruppenarbeit(gruppenarbeit);
                        }
                    }
                    veranstaltungDetailView.update();

                    updateGrid();
                    close();
                }
            } else {
                Notification.show("Keine Teilnehmer ausgewählt", 3000, Notification.Position.MIDDLE);
            }
        });


        configureGrid();

        Button cancelButton = new Button("Abbrechen", e -> close());
        this.setHeaderTitle("Teilnehmer hinzufügen");
        getFooter().add(cancelButton);
        add(
                getToolbar(),
                getContent()
        );

        updateGrid();

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

    public void updateGrid() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            String searchText = filterText.getValue().toLowerCase();
            List<Teilnehmer> teilnehmerList = teilnehmerService.findAllTeilnehmerNotInVeranstaltung(veranstaltungId, user);
            List<Teilnehmer> filteredTeilnehmerList = teilnehmerList.stream()
                    .filter(teilnehmer -> teilnehmer.getVorname().toLowerCase().contains(searchText)
                            || teilnehmer.getNachname().toLowerCase().contains(searchText)
                            || Long.toString(teilnehmer.getId()).contains(searchText)) // Vergleicht den Suchtext mit der Matrikelnummer
                    .collect(Collectors.toList());
            grid.setItems(filteredTeilnehmerList);
        }
    }

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

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(1, grid);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

}
