package com.example.application.views.veranstaltungstermin;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
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
import com.vaadin.flow.data.binder.*;

import java.time.LocalDate;
import java.util.Optional;

public class VeranstaltungsterminDialog extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final String veranstaltungId;

    //Dialog Items
    private final DatePicker startDatePicker = new DatePicker("Termin Datum");
    private final DatePicker endDatePicker = new DatePicker("Ende Terminserie");
    private final TimePicker startTimePicker = new TimePicker("Startzeit");
    private final TimePicker endTimePicker = new TimePicker("Endzeit");
    private final TextField ort = new TextField("Ort");
    private final TextField titel = new TextField("Titel");
    private final RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
    private final Button cancelButton= new Button("Abbrechen");
    private final Button saveButton= new Button("Speichern");

    private AuthenticatedUser authenticatedUser;

    private VeranstaltungDetailView veranstaltungDetailView;

    private Veranstaltung veranstaltung;

    //Data Binder
    Binder<Veranstaltungstermin> binder = new Binder<>(Veranstaltungstermin.class);

    public VeranstaltungsterminDialog(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, VeranstaltungDetailView veranstaltungDetailView, String veranstaltungId, AuthenticatedUser authenticatedUser) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.veranstaltungId = veranstaltungId;
        this.authenticatedUser = authenticatedUser;
        this.veranstaltungDetailView = veranstaltungDetailView;

        add(createLayout());
        configureElements();
        bindFields();
    }

    private HorizontalLayout createLayout () {

        setHeaderTitle("Veranstaltungstermin hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return(
                new HorizontalLayout(
                        new VerticalLayout(
                                titel,
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

        //Radiobutton Implementation
        radioGroup.setLabel("Terminart");
        radioGroup.setItems("Einmalig", "Wöchentlich", "Monatlich");
        radioGroup.setValue("Einmalig"); // Default selection
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        radioGroup.addValueChangeListener(e -> { //triggers after the value change in the radioGroup
            if (e.getValue().equals("Wöchentlich") || e.getValue().equals("Monatlich")) {
                endDatePicker.setVisible(true);
            } else {
                endDatePicker.setVisible(false);
            }
        });

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setVisible(false); //Initial is to hide the field

        //Footer Button Implementation
        saveButton.addClickListener( event -> {

            Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();

            if (binder.writeBeanIfValid(veranstaltungstermin)){
                calcPersistVeranstaltungstermin(veranstaltungstermin);

                close();
                clearFields();
                veranstaltungDetailView.update();

            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.addClickListener( e -> {
            close();
            clearFields();
        });

        endDatePicker.addValueChangeListener(event -> {
            LocalDate endDate = event.getValue();
            if (endDate == null || endDate.isBefore(startDatePicker.getValue())) {
                endDatePicker.setErrorMessage("Enddatum darf nicht vor Startdatum sein");
                endDatePicker.setInvalid(true);
            } else {
                endDatePicker.setInvalid(false);
            }
        });

    }

    private void bindFields(){
        binder.forField(titel)
                .asRequired("Titel muss gefüllt sein")
                .bind(Veranstaltungstermin::getTitel, Veranstaltungstermin::setTitel);
        binder.forField(ort)
                .bind(Veranstaltungstermin::getOrt, Veranstaltungstermin::setOrt);
        binder.forField(startTimePicker)
               .asRequired("Startzeit darf nicht leer sein")
               .bind(Veranstaltungstermin::getStartZeit, Veranstaltungstermin::setStartZeit);
        binder.forField(endTimePicker)
               .asRequired("Endzeit darf nicht leer sein")
               .withValidator(endTime -> !endTime.isBefore(startTimePicker.getValue()),
                       "Endzeit darf nicht vor Startzeit sein")
               .bind(Veranstaltungstermin::getEndZeit, Veranstaltungstermin::setEndZeit);
        binder.forField(startDatePicker)
               .asRequired("Datum darf nicht leer sein")
               .bind(Veranstaltungstermin::getDatum, Veranstaltungstermin::setDatum);

    }

    private void calcPersistVeranstaltungstermin (Veranstaltungstermin veranstaltungstermin) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        //"Einmalig" Save Event wird in jedem Fall ausgeführt
        persistVeranstaltungstermin(veranstaltungstermin);

        //Bei "Wöchentlich" werden noch weitere Termine (7-Tage-Abstand) erzeugt die jeweils vorher validiert werden müssen
        if (radioGroup.getValue().equals("Wöchentlich")) {
            while (!startDate.isAfter(endDate)) {

                startDate = startDate.plusWeeks(1);
                startDatePicker.setValue(startDate);

                Veranstaltungstermin folgetermine = new Veranstaltungstermin();
                if (binder.writeBeanIfValid(folgetermine)){ //Validierung der neuen Instanz
                    persistVeranstaltungstermin(folgetermine);
                }
            }
        }
        //Bei "Monatlich" werden noch weitere Termine (1-Monat-Abstand) erzeugt die jeweils vorher validiert werden müssen
        else if (radioGroup.getValue().equals("Monatlich")) {
            while (!startDate.isAfter(endDate)) {
                startDate = startDate.plusMonths(1);
                startDatePicker.setValue(startDate);

                Veranstaltungstermin folgeTermine = new Veranstaltungstermin();
                if (binder.writeBeanIfValid(folgeTermine)){ //Validierung der neuen Instanz
                    persistVeranstaltungstermin(folgeTermine);
                }
            }
        }
    }

    public void persistVeranstaltungstermin (Veranstaltungstermin veranstaltungstermin) {

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            Veranstaltung veranstaltung = veranstaltungService.findVeranstaltungById(Long.parseLong(veranstaltungId), user);
            veranstaltung.addVeranstaltungstermin(veranstaltungstermin); //Lazy Load Problem
            veranstaltungstermin.setVeranstaltung(veranstaltung);
            veranstaltungstermin.setUser(user);
            veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);
            veranstaltungService.saveVeranstaltung(veranstaltung);
            Notification.show("Veranstaltungstermin angelegt!");
        } else {
            Notification.show("Bitte melden Sie sich an, um Ihre Veranstaltungstermine zu sehen.");
        }
    }

    public void clearFields(){
        //Clear all Fields after saving
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.clear();
        startTimePicker.clear();
        endTimePicker.clear();
        ort.clear();
        titel.clear();
        radioGroup.setValue("Einmalig");
    }

}
