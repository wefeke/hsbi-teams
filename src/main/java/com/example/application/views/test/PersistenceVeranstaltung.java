package com.example.application.views.test;


import com.example.application.models.Veranstaltung;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;

@Route("persistenceVeranstaltung")
public class PersistenceVeranstaltung extends VerticalLayout {

    private VeranstaltungenService veranstaltungenService;
    private UserService userService;

    TextField id = new TextField("Test ID");
    TextField titel = new TextField("Test Titel");
    DatePicker semester = new DatePicker("Test Datum");

    Button save = new Button("Save");

    public PersistenceVeranstaltung(VeranstaltungenService veranstaltungenService, UserService userService){
        this.veranstaltungenService = veranstaltungenService;
        this.userService = userService;

        add(
                id,
                titel,
                semester,
                save
        );

        save.addClickListener(e -> saveTest());
    }

    private void saveTest() {
        String temp_id = id.getValue();
        String temp_itel = titel.getValue();
        LocalDate temp_semester = semester.getValue();

        Veranstaltung veranstaltung = new Veranstaltung();
        veranstaltung.setTitel(titel.getValue());
        veranstaltung.setUser(userService.findAdmin());

            veranstaltungenService.saveVeranstaltung(veranstaltung);

            id.clear();
            titel.clear();
            semester.clear();
        }
    }
