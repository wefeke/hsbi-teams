package com.example.application.views.veranstaltungen;

import com.example.application.models.Veranstaltung;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.Date;

@Route(value = "add-veranstaltung")
@PageTitle("Veranstaltung hinzufügen")
public class VeranstaltungenHinzufuegen extends VerticalLayout {

    private final VeranstaltungenService veranstaltungenService;

    @Autowired
    public VeranstaltungenHinzufuegen(VeranstaltungenService veranstaltungenService) {
        this.veranstaltungenService = veranstaltungenService;

        FormLayout formLayout = new FormLayout();

        TextField titelField = new TextField("Titel");
        DatePicker datePicker = new DatePicker("Datum");

        Button submitButton = new Button("Submit", clickEvent -> {
            Veranstaltung veranstaltung = new Veranstaltung();
            veranstaltung.setTitel(titelField.getValue());
            veranstaltung.setSemester(Date.from(datePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            veranstaltungenService.addVeranstaltung(veranstaltung);
            Notification.show("Veranstaltung hinzugefügt");
        });

        formLayout.add(titelField, datePicker, submitButton);
        add(formLayout);
    }
}