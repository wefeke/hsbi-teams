package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class VeranstaltungsterminDialog extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private String veranstaltungId;

    //Dialog Items
    private final DatePicker startDatePicker = new DatePicker("Termin Datum");
    private final DatePicker endDatePicker = new DatePicker("Ende Terminserie");
    private final TimePicker startTimePicker = new TimePicker("Startzeit");
    private final TimePicker endTimePicker = new TimePicker("Endzeit");
    private final TextField ort = new TextField("Ort");
    private final TextField notizen = new TextField("Notizen");
    private final RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
    private final Button cancelButton= new Button("Cancel");
    private final Button saveButton= new Button("Save");

    public VeranstaltungsterminDialog(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, String veranstaltungId) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.veranstaltungId = veranstaltungId;

        add(createLayout());
        configureElements();
    }

    private VerticalLayout createLayout () {

        setHeaderTitle("Veranstaltung hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return(
                new VerticalLayout(
                    new HorizontalLayout(
                        new VerticalLayout(
                                notizen,
                                startTimePicker,
                                startDatePicker,
                                radioGroup
                        ),

                        new VerticalLayout(
                                ort,
                                endTimePicker,
                                endDatePicker
                        )
                    )
                )
        );
    }

    private void configureElements(){
        endDatePicker.setVisible(false); //Standard is to hide the field

        //Radiobutton Implementation
        radioGroup.setLabel("Terminart");
        radioGroup.setItems("Einmalig", "Wöchentlich", "Monatlich");
        radioGroup.setValue("Einmalig"); // Default selection
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        radioGroup.addValueChangeListener(e -> {
            if (e.getValue().equals("Wöchentlich") || e.getValue().equals("Monatlich")) {
                endDatePicker.setVisible(true);
            } else {
                endDatePicker.setVisible(false);
            }
        });


        //Footer Button Implementation
        saveButton.addClickListener( e -> {
           calcPersistVeranstaltungstermin();
           close();
           clearFields();
           UI.getCurrent().getPage().reload();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.addClickListener( e -> {
            close();
            clearFields();
        });

    }

    private void calcPersistVeranstaltungstermin () {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        //"Einmalig" Save Event
        if (radioGroup.getValue().equals("Einmalig")) {
            persistVeranstaltungstermin(startDate, endDate);
        }
        else if (radioGroup.getValue().equals("Wöchentlich")) {
            persistVeranstaltungstermin(startDate, endDate);
            startDate = startDate.plusDays(7);

            while (!startDate.isAfter(endDate)) {
                // If the date is a Saturday or Sunday, adjust it to the next Monday
                if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    startDate = startDate.plusDays(2);
                } else if (startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    startDate = startDate.plusDays(1);
                }

                persistVeranstaltungstermin(startDate, endDate);

                // Increment the date by 7 days
                startDate = startDate.plusDays(7);
            }
        }
        else if (radioGroup.getValue().equals("Monatlich")) {
            persistVeranstaltungstermin(startDate, endDate);
            startDate = startDate.plusMonths(1);

            while (!startDate.isAfter(endDate)) {
                if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    startDate = startDate.plusDays(2);
                } else if (startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    startDate = startDate.plusDays(1);
                }
                persistVeranstaltungstermin(startDate, endDate);
                startDate = startDate.plusMonths(1);
            }
        }
    }

    public void persistVeranstaltungstermin (LocalDate startDate, LocalDate endDate) {
        Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
        veranstaltungstermin.setDatum(startDate);
        veranstaltungstermin.setUhrzeit(startTimePicker.getValue());
        veranstaltungstermin.setOrt(ort.getValue());
        veranstaltungstermin.setNotizen(notizen.getValue());

        Veranstaltung veranstaltung = veranstaltungService.findVeranstaltungById(Long.parseLong(veranstaltungId));

        veranstaltungstermin.setVeranstaltung(veranstaltung);

        veranstaltung.addVeranstaltungstermin(veranstaltungstermin); //Lazy Load Problem

        veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);
        veranstaltungService.saveVeranstaltung(veranstaltung);

        Notification.show("Veranstaltungstermin angelegt!");
    }

    public void clearFields(){
        //Clear all Fields after saving
        startDatePicker.clear();
        endDatePicker.clear();
        startTimePicker.clear();
        endTimePicker.clear();
        ort.clear();
        notizen.clear();
        radioGroup.setValue("Einmalig");
    }

}
