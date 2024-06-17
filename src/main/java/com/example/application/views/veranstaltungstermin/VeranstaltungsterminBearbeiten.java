package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.User;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.*;

import java.util.Optional;

public class VeranstaltungsterminBearbeiten extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final String veranstaltungId;

    //Dialog Items
    private final DatePicker datePicker = new DatePicker("Termin Datum");
    private final TimePicker startTimePicker = new TimePicker("Startzeit");
    private final TimePicker endTimePicker = new TimePicker("Endzeit");
    private final TextField ort = new TextField("Ort");
    private final TextField notizen = new TextField("Notizen");
    private final Button cancelButton= new Button("Abbrechen");
    private final Button saveButton= new Button("Änderungen speichern");
    private final VeranstaltungDetailView veranstaltungDetailView;
    private final Long veranstaltungsterminId;

    private final AuthenticatedUser authenticatedUser;

    private final Veranstaltungstermin aktiverVeranstaltungstermin;
    private final Gruppenarbeit aktiveGruppenarbeit;

    //Data Binder
    Binder<Veranstaltungstermin> binder = new Binder<>(Veranstaltungstermin.class);

    public VeranstaltungsterminBearbeiten(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, VeranstaltungDetailView veranstaltungDetailView, String veranstaltungId, Long veranstaltungsterminId, AuthenticatedUser authenticatedUser, Veranstaltungstermin aktiverVeranstaltungstermin, Gruppenarbeit aktiveGruppenarbeit) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.veranstaltungId = veranstaltungId;
        this.veranstaltungDetailView = veranstaltungDetailView;
        this.veranstaltungsterminId = veranstaltungsterminId;
        this.authenticatedUser = authenticatedUser;
        this.aktiverVeranstaltungstermin = aktiverVeranstaltungstermin;
        this.aktiveGruppenarbeit = aktiveGruppenarbeit;

        add(createLayout());
        configureElements();
        bindFields();
        readBean();
    }

    private HorizontalLayout createLayout () {

        setHeaderTitle("Veranstaltungstermin hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return(
                new HorizontalLayout(
                        new VerticalLayout(
                                notizen,
                                startTimePicker,
                                datePicker
                        ),
                        new VerticalLayout(
                                ort,
                                endTimePicker
                        )
                )
        );
    }

    private void configureElements(){
        //Footer Button Implementation
        saveButton.addClickListener( event -> {

            Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();

            if (binder.writeBeanIfValid(veranstaltungstermin)){
                persistVeranstaltungstermin(veranstaltungstermin);

                close();
                clearFields();

                veranstaltungDetailView.removeAndAddTerminToTermine(veranstaltungstermin, veranstaltungsterminId);

                if (aktiverVeranstaltungstermin != null) {
                    veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(aktiverVeranstaltungstermin);
                    if (aktiveGruppenarbeit != null) {
                        veranstaltungDetailView.setAktiveKachelGruppenarbeit(aktiveGruppenarbeit);
                    }
                }
                veranstaltungDetailView.update();

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
        binder.forField(datePicker)
                .asRequired("Datum darf nicht leer sein")
                .bind(Veranstaltungstermin::getDatum, Veranstaltungstermin::setDatum);

    }

    public void readBean (){
        //Read Data from the Binder
        binder.readBean(veranstaltungsterminService.findVeranstaltungsterminById(veranstaltungsterminId));
    }

    public void persistVeranstaltungstermin (Veranstaltungstermin veranstaltungstermin) {
        if (binder.writeBeanIfValid(veranstaltungstermin)){ //Validierung der neuen Instanz
            veranstaltungstermin.setId(veranstaltungsterminId);
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                veranstaltungstermin.setUser(user);
                veranstaltungstermin.setVeranstaltung(veranstaltungService.findVeranstaltungById(Long.parseLong(veranstaltungId), user));
            }
            veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);
            Notification.show("Veranstaltungstermin " + veranstaltungstermin.getTitel() + " bearbeitet!");
        }
    }

    public void clearFields(){
        //Clear all Fields after saving
        datePicker.clear();
        startTimePicker.clear();
        endTimePicker.clear();
        ort.clear();
        notizen.clear();
    }


}
