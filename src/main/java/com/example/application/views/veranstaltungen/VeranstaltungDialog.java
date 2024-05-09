package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

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

    public VeranstaltungDialog(VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, UserService userService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;

        add(createLayout());
        configureElements();
    }

    private FlexLayout createLayout() {
        setHeaderTitle("Veranstaltung hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        setWidth("100vh");

        return (
                new FlexLayout(
                        new HorizontalLayout(
                                new VerticalLayout(
                                        titelField,
                                        datePicker,
                                        comboBox
                                ),
                                new VerticalLayout(
                                        grid
                                )
                        )
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
        grid.addColumn(Teilnehmer::getId).setHeader("ID");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        /*
            grid.asSingleSelect().addValueChangeListener(event -> {
                // Get the selected student
                Teilnehmer selectedStudent = event.getValue();

                // Add the selected student to the combobox
                if (selectedStudent != null) {
                    comboBox.setValue(selectedStudent);
                }
            });
         */

        //Buttons
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            persistVeranstaltung();
            clearFields();
            close();
            UI.getCurrent().getPage().reload();
        });

        cancelButton.addClickListener(e -> {
            close();
        });
    }

    private void persistVeranstaltung() {

        Veranstaltung veranstaltung = new Veranstaltung();
        veranstaltung.setTitel(titelField.getValue());
        veranstaltung.setSemester(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        veranstaltung.setTeilnehmer(new ArrayList<>(comboBox.getSelectedItems()));
        veranstaltung.setUser(userService.findAdmin());

        veranstaltungenService.saveVeranstaltung(veranstaltung);

        Notification.show("Veranstaltung angelegt!");

        titelField.clear();
        datePicker.clear();
        comboBox.clear();
    }

    public void clearFields(){
        titelField.clear();
        datePicker.clear();
        comboBox.clear();
    }

}
