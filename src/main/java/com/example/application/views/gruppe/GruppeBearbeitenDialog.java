package com.example.application.views.gruppe;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class GruppeBearbeitenDialog extends Dialog {
    //Data
    private final Gruppenarbeit gruppenarbeit;
    private final List<Gruppe> gruppen;

    //UI Elements
    private final Button cancelBtn = new Button("Abbrechen");
    private final Button saveBtn = new Button("Speichern");
    private final Div groupsArea = new Div();


    public GruppeBearbeitenDialog(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
        this.gruppen = gruppenarbeit.getGruppen();

        configureGroupsArea();
        groupGrids(gruppen.size(), gruppen);

        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButtonFunctionalities();
        add(createLayout());
    }

    private void addButtonFunctionalities(){
        saveBtn.addClickListener(event ->{
            Notification.show(this.gruppenarbeit.getTitel());
        });

        cancelBtn.addClickListener(event -> {
            close();
        });
    }

    private void groupGrids(int numberOfGroups, List<Gruppe> gruppen) {
        for(int i = 0; i< numberOfGroups; i++){
            Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class, false);
            grid.addColumn(Teilnehmer::getId).setHeader("Matrikelnr");
            grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
            grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
            grid.setItems(gruppen.get(i).getTeilnehmer());
            grid.setWidth("400px");
            grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);

            H5 title = new H5("Gruppe " + (i+1) + ": " + gruppen.get(i).getTeilnehmer().size() + " Teilnehmer");
            title.addClassName("gruppen-gruppenarbeit-title");

            Button deleteBtn = new Button("Entfernen");
            Button addBtn = new Button("Hinzufügen");
            HorizontalLayout buttonLayout = new HorizontalLayout(deleteBtn, addBtn);
            Div titleAndGroups = new Div(title, buttonLayout, grid);
            titleAndGroups.addClassName("gruppen-gruppenarbeit");
            groupsArea.add(titleAndGroups);
        }
    }

    private VerticalLayout createLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        setHeaderTitle("Gruppen bearbeiten");
        getFooter().add(cancelBtn);
        getFooter().add(saveBtn);

        mainLayout.add(groupsArea);

        return mainLayout;
    }

    private void configureGroupsArea() {
        groupsArea.setWidth("100%");
        groupsArea.setClassName("gruppen-container-gruppenarbeiten");
    }
}
