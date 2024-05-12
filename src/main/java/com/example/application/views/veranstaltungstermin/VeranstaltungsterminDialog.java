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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

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

    //Data Binder
    Binder<Veranstaltungstermin> binder = new Binder<>(Veranstaltungstermin.class);

    public VeranstaltungsterminDialog(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, String veranstaltungId) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.veranstaltungId = veranstaltungId;

        add(createLayout());
        configureElements();
        bindFields();
    }

    private HorizontalLayout createLayout () {

        setHeaderTitle("Veranstaltung hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return(
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
        saveButton.addClickListener( event -> {
            try {
                Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
                binder.writeBean(veranstaltungstermin);

                calcPersistVeranstaltungstermin();
                close();
                clearFields();
                UI.getCurrent().getPage().reload();

            } catch (ValidationException e) {
                e.getBeanValidationErrors().forEach(error -> {
                    String fieldName = error.getErrorMessage().toString();
                    System.out.println("Validation error in field '" + fieldName);
                });
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.addClickListener( e -> {
            close();
            clearFields();
        });

    }

    private void bindFields(){
        binder.forField(notizen)
                .bind(Veranstaltungstermin::getNotizen, Veranstaltungstermin::setNotizen);
        binder.forField(ort)
                .bind(Veranstaltungstermin::getOrt, Veranstaltungstermin::setOrt);
       binder.forField(startTimePicker)
               .asRequired("Startzeit darf nicht leer sein")
               .bind(Veranstaltungstermin::getUhrzeit, Veranstaltungstermin::setUhrzeit);
       binder.forField(endTimePicker)
               .withValidator(endTime -> !endTime.isBefore(startTimePicker.getValue()),
                       "Endzeit darf nicht vor Startzeit sein")
               .bind(Veranstaltungstermin::getUhrzeit, Veranstaltungstermin::setUhrzeit);
       binder.forField(startDatePicker)
               .asRequired("Datum darf nicht leer sein")
               .bind(Veranstaltungstermin::getDatum, Veranstaltungstermin::setDatum);
       binder.forField(endDatePicker)
               .withValidator(endDate -> !endDatePicker.isVisible() || !endDate.isBefore(startDatePicker.getValue()),
                       "Enddatum darf nicht vor Startdatum sein");
    }

    private void calcPersistVeranstaltungstermin () {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        //"Einmalig" Save Event
        if (radioGroup.getValue().equals("Einmalig")) {
            persistVeranstaltungstermin();
        }
        else if (radioGroup.getValue().equals("Wöchentlich")) {
            persistVeranstaltungstermin();
            startDate = startDate.plusWeeks(1);
            startDatePicker.setValue(startDate);

            while (!startDate.isAfter(endDate)) {
                persistVeranstaltungstermin();
                startDate = startDate.plusWeeks(1);
                startDatePicker.setValue(startDate);
            }
        }
        else if (radioGroup.getValue().equals("Monatlich")) {
            persistVeranstaltungstermin();
            startDate = startDate.plusMonths(1);
            startDatePicker.setValue(startDate);

            while (!startDate.isAfter(endDate)) {
                persistVeranstaltungstermin();
                startDate = startDate.plusMonths(1);
                startDatePicker.setValue(startDate);
            }
        }
    }

    public void persistVeranstaltungstermin () {
        try {
            Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
            binder.writeBean(veranstaltungstermin);

            Veranstaltung veranstaltung = veranstaltungService.findVeranstaltungById(Long.parseLong(veranstaltungId));
            veranstaltung.addVeranstaltungstermin(veranstaltungstermin); //Lazy Load Problem
            veranstaltungstermin.setVeranstaltung(veranstaltung);

            veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);
            veranstaltungService.saveVeranstaltung(veranstaltung);
            Notification.show("Veranstaltungstermin angelegt!");

        } catch (ValidationException e) {
            e.getBeanValidationErrors().forEach(error -> {
                String fieldName = error.getErrorMessage().toString();
                System.out.println("Validation error in field '" + fieldName);
            });
        }
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
