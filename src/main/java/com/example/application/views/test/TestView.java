package com.example.application.views.test;

import com.example.application.models.Auswertung;
import com.example.application.services.AuswertungService;
import com.example.application.views.gruppenarbeit.GruppeAuswertungDialog;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.stream.Stream;

//LEON
@Route(value = "test")
@PageTitle("Tests | Vaadin CRM")
public class TestView extends VerticalLayout {
    Grid<Auswertung> grid = new Grid<>(Auswertung.class);
    TextField filterText = new TextField();
    TestContactForm form;
    AuswertungService auswertungService;


    private GruppeAuswertungDialog gruppeAuswertungDialog;


    public TestView(AuswertungService auswertungService) {
        this.auswertungService = auswertungService;
        addClassName("test-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        updateList();

        createAuswertungsDialog();
        //gruppeAuswertungDialog.open();
    }

    // auskommentiert da auch ein Teilnehmer jetzt übergeben werden muss
    private void createAuswertungsDialog() {
        //gruppeAuswertungDialog = new GruppeAuswertungDialog();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1,form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new TestContactForm(auswertungService.findAllAuswertungen());
        form.setWidth("25em");
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("name","titelVeranstaltung","titelGruppenarbeit");
        //grid.addColumn(Test::gettestid).setHeader("Test ID");
        //grid.addColumn(Test::gettestname).setHeader("Test Name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());


        Button csvExportButton = new Button("CSV Export");

        var streamResource = new StreamResource(
                "veranstaltungen.csv",
                () -> {

                    try {
                        Stream<Auswertung> auswertungen = grid.getGenericDataView().getItems();
                        StringWriter writer = new StringWriter();
                        var beanToCSV = new StatefulBeanToCsvBuilder<Auswertung>(writer).build();
                        beanToCSV.write(auswertungen);
                        var contents = writer.toString();
                        return new ByteArrayInputStream(contents.getBytes());
                    } catch (CsvDataTypeMismatchException e) {
                        throw new RuntimeException(e);
                    } catch (CsvRequiredFieldEmptyException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        var download = new Anchor(streamResource, "Download");

        var toolbar = new HorizontalLayout(filterText,download);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(auswertungService.findAllAuswertungen());
    }
}

