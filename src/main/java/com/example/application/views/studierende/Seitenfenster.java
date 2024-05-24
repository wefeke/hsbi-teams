package com.example.application.views.studierende;

import com.example.application.models.Teilnehmer;
import com.example.application.services.TeilnehmerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;

@Route(value = "seitenfenster", layout = MainLayout.class)
public class Seitenfenster extends VerticalLayout {
    private final TeilnehmerService teilnehmerService;
    private final Grid<Teilnehmer> grid = new Grid<>();
    private final TextField filterText = new TextField();
    private final Button addStudiernedenButton = new Button("+");
    private final Dialog dialog = new Dialog();



    public Seitenfenster(TeilnehmerService teilnehmerService) {
        this.teilnehmerService = teilnehmerService;

        configureGrid();
        updateStudierendeView();
        addStudiernedenButton.addClickListener(event -> openDialog());
        configureDialog();
        add(
                getSuchen(),
                getContent(),
                getButton()

        );

    }
    private void updateStudierendeView() {
        grid.setItems(teilnehmerService.findAllTeilnehmer(filterText.getValue()));
    }
    private void configureGrid() {
        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
        grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
        grid.addColumn(Teilnehmer::getId).setHeader("MatrikelNr");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setAllRowsVisible(true);
    }
    private void openDialog() {
        dialog.open();
    }

    private void configureDialog() {
        dialog.add(new StudierendeHinzufuegen(teilnehmerService));
        dialog.setWidth("400px");
        dialog.setHeight("300px");
    }
    private Component getSuchen(){
        filterText.setPlaceholder("Name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateStudierendeView());

        HorizontalLayout tsearch = new HorizontalLayout(filterText);

        return tsearch;
    }
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(1, grid);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }
    private Component getButton(){
        HorizontalLayout Button = new HorizontalLayout(addStudiernedenButton);
        return Button;
    }
}