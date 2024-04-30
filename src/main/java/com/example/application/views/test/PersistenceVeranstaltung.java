/*
package com.example.application.views.test;

import com.example.application.models.Test;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TestService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Date;

@Route("persistenceVeranstaltung")
public class PersistenceVeranstaltung extends VerticalLayout {

    private VeranstaltungenService veranstaltungenService;

    TextField id = new TextField("Test ID");
    TextField titel = new TextField("Test Titel");
    DatePicker semester = new DatePicker("Test Datum");

    Button save = new Button("Save");

    public PersistenceVeranstaltung(VeranstaltungenService veranstaltungenService) {
        this.veranstaltungenService = veranstaltungenService;

        add(
                id,
                titel,
                semester
        );

        save.addClickListener(e -> saveTest());
    }

    private void saveTest() {
        String temp_id = id.getValue();
        String temp_itel = titel.getValue();
        LocalDate temp_semester = semester.getValue();

        if (id != null && !id.isEmpty() && semester != null && !semester.isEmpty()) {
            Veranstaltung veranstaltung = new Veranstaltung();
            veranstaltung.setVeranstaltungsId(Long.parseLong(id.getValue()));
            veranstaltung.setTitel(titel.getValue());
            veranstaltung.setSemester(new Date(temp_semester.toString()));


            veranstaltungenService.saveVeranstaltung(veranstaltung);

            id.clear();
            titel.clear();
            semester.clear();
        }
    }

}
*/