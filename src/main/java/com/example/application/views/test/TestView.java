package com.example.application.views.test;

import com.example.application.models.Auswertung;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.AuswertungService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.MainLayout;
import com.example.application.views.gruppenarbeit.GruppeAuswertungDialog;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

//LEON
@Route(value = "auswertung/:veranstaltungId", layout = MainLayout.class)
@PageTitle("Auswertungen")
@RolesAllowed({"ADMIN", "USER"})
public class TestView extends VerticalLayout implements HasUrlParameter<String>{
    Grid<Auswertung> grid = new Grid<>(Auswertung.class);
    TextField filterText = new TextField();
    TestContactForm form;
    AuswertungService auswertungService;

    private final AuthenticatedUser authenticatedUser;
    private String veranstaltungIdString;
    private Veranstaltung veranstaltung;
    private VeranstaltungenService veranstaltungService;
    //private GruppeAuswertungDialog gruppeAuswertungDialog;



    public TestView(AuswertungService auswertungService, AuthenticatedUser authenticatedUser, VeranstaltungenService veranstaltungService) {
        this.auswertungService = auswertungService;
        this.authenticatedUser = authenticatedUser;
        this.veranstaltungService = veranstaltungService;
        addClassName("test-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        createAuswertungsDialog();
        //gruppeAuswertungDialog.open();
    }


    public void init() {
        updateList();
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        Location location = event.getLocation();
        List<String> segments = location.getSegments();
        if (!segments.isEmpty()) {
            this.veranstaltungIdString = segments.getLast();
            try {
                long veranstaltungIdLong = Long.parseLong(veranstaltungIdString);
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();

                    veranstaltung = veranstaltungService.findVeranstaltungById(veranstaltungIdLong, user);
                } else {
                    Notification.show("Bitte melden Sie sich an, um Ihre Veranstaltungstermine zu sehen.");
                    getUI().ifPresent(ui -> ui.navigate("login"));
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid veranstaltungId: " + veranstaltungIdString);
            }
        } else {
            System.err.println("No veranstaltungId in URL");
        }
        init();
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
        grid.setColumns("matrikelnummer", "vorname", "nachname", "veranstaltung", "gruppenarbeit", "punkte");
        //grid.addColumn(Test::gettestid).setHeader("Test ID");

        //grid.addColumn(Test::gettestname).setHeader("Test Name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());


        Button downloadBtn = new Button("Download Excel File", new Icon(VaadinIcon.DOWNLOAD));

        var streamResource = new StreamResource(
                "veranstaltungen.excel",
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



        downloadBtn.addClickListener(
                ( ClickEvent< Button > clickEvent ) -> {

                    // Tell user.
                }
        );

        var toolbar = new HorizontalLayout(filterText,downloadBtn);
        toolbar.addClassName("toolbar");
        return toolbar;
    }



    private void updateList() {
        grid.setItems(auswertungService.findAllAuswertungenWithID(veranstaltung.getId()));
    }



}

