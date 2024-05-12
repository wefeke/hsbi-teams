package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.binder.Binder;
import jdk.jfr.Event;

import java.util.*;

@Route(value = "addDialog")
public class VeranstaltungDialog extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;

    //Dialog Items
    private final TextField titelField = new TextField("Titel");
    private final DatePicker datePicker = new DatePicker("Datum");
    private final MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
    private final Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class, false);
    private final Button cancelButton= new Button("Cancel");
    private final Button saveButton= new Button("Save");

    //Data Binder
    Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);

    public VeranstaltungDialog(VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, UserService userService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;

        add(createLayout());
        configureElements();
        bindFields();
    }

    private HorizontalLayout createLayout() {
        setHeaderTitle("Veranstaltung hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        setWidth("70vh");
        return (
                new HorizontalLayout(
                        new VerticalLayout(titelField, datePicker, comboBox),
                        new VerticalLayout(grid)
                ));
        }

    private void configureElements() {

        //Combobox
        comboBox.setItems(teilnehmerService.findAllTeilnehmer());
        comboBox.setItemLabelGenerator(Teilnehmer::getVorname);

        //Grid
        grid.setItems(teilnehmerService.findAllTeilnehmer());
        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
        //grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
        // grid.addColumn(Teilnehmer::getId).setHeader("ID");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        //Buttons
        saveButton.addClickListener(event -> {
            try {
                Veranstaltung veranstaltung = new Veranstaltung();
                veranstaltung.setUser(userService.findAdmin()); //Angemeldeten User holen
                binder.writeBean(veranstaltung);
                veranstaltungenService.saveVeranstaltung(veranstaltung);
                Notification.show("Veranstaltung angelegt");
                clearFields();
                close();
                UI.getCurrent().getPage().reload();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.addClickListener(e -> {
            clearFields();
            close();
        });
    }

    private void bindFields() {
        binder.forField(titelField)
                .asRequired("Titel muss gefüllt sein")
                .bind(Veranstaltung::getTitel, Veranstaltung::setTitel);
        binder.forField(datePicker)
                .bind(Veranstaltung::getSemester, Veranstaltung::setSemester);
        binder.forField(comboBox)
                .bind(Veranstaltung::getTeilnehmer, Veranstaltung::setTeilnehmer);
    }

    public void clearFields(){
        titelField.clear();
        datePicker.clear();
        comboBox.clear();
    }

}
