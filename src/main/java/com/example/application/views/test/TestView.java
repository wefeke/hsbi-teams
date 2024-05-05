package com.example.application.views.test;

import com.example.application.models.Test;
import com.example.application.services.TestService;
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
import java.util.Collections;
import java.util.stream.Stream;


@Route(value = "test")
@PageTitle("Tests | Vaadin CRM")
public class TestView extends VerticalLayout {
    Grid<Test> grid = new Grid<>(Test.class);
    TextField filterText = new TextField();
    TestContactForm form;
    TestService testService;

    public TestView(TestService testService) {
        this.testService = testService;
        addClassName("test-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        updateList();
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
        form = new TestContactForm(testService.findAllTests());
        form.setWidth("25em");
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("testid", "testname");
        grid.addColumn(Test::gettestid).setHeader("Test ID");
        grid.addColumn(Test::gettestname).setHeader("Test Name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add test");

        Button csvExportButton = new Button("CSV Export");

        var streamResource = new StreamResource(
                "test.csv",
                () -> {

                    try {
                        Stream<Test> tests = grid.getGenericDataView().getItems();
                        StringWriter writer = new StringWriter();
                        var beanToCSV = new StatefulBeanToCsvBuilder<Test>(writer).build();
                        beanToCSV.write(tests);
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



        var toolbar = new HorizontalLayout(filterText, addContactButton,download);
        toolbar.addClassName("toolbar");

        return toolbar;
    }

    private void updateList() {
        grid.setItems(testService.findAllTests(filterText.getValue()));
    }
}

