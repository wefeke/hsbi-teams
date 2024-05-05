package com.example.application.views.veranstaltungen;

import com.example.application.models.Veranstaltung;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.MainLayout;
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

@Route(value = "add-veranstaltung", layout = MainLayout.class)
@PageTitle("Veranstaltung hinzufügen")
public class VeranstaltungenHinzufuegen extends VerticalLayout {

    private final VeranstaltungenService veranstaltungenService;
    private final UserService userService;

    TextField titelField = new TextField("Titel");
    DatePicker datePicker = new DatePicker("Datum");
    Button save = new Button("Save");

    @Autowired
    public VeranstaltungenHinzufuegen(VeranstaltungenService veranstaltungenService, UserService userService) {

        this.veranstaltungenService = veranstaltungenService;
        this.userService = userService;

        add(
                titelField,
                datePicker,
                save
        );

        save.addClickListener(e -> saveVeranstaltung());
    }

    private void saveVeranstaltung() {
        String titel = titelField.getValue();
        Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Veranstaltung veranstaltung = new Veranstaltung();
        veranstaltung.setTitel(titel);
        veranstaltung.setSemester(date);
        veranstaltung.setUser(userService.findAdmin());

        veranstaltungenService.saveVeranstaltung(veranstaltung);

        Notification.show("Veranstaltung gespeichert");

        titelField.clear();
        datePicker.clear();
    }
}