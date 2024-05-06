package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.*;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@PageTitle("Veranstaltungstermin hinzufügen")
@Route(value = "add-veranstaltungstermin/:veranstaltungId", layout = MainLayout.class)
public class VeranstaltungsterminHinzufuegen extends FormLayout implements HasUrlParameter<String> {

    private VeranstaltungsterminService veranstaltungsterminService;
    private VeranstaltungenService veranstaltungService;
    private String veranstaltungId;

    DatePicker datePicker = new DatePicker("Termin Datum");
    Button save= new Button("Save");

    public VeranstaltungsterminHinzufuegen(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;

        save.addClickListener(e -> saveVeranstaltungstermin());

        add(
                datePicker,
                save
        );
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        Location location = event.getLocation();
        List<String> segments = location.getSegments();

        if (!segments.isEmpty()) {
            this.veranstaltungId = segments.get(segments.size() - 1);
            System.out.println("setParameter called with: " + veranstaltungId);
        } else {
            System.out.println("No veranstaltungId in URL");
        }
    }

    private void saveVeranstaltungstermin() {
        Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
        veranstaltungstermin.setDatum(date);
        veranstaltungstermin.setVeranstaltung(veranstaltungService.findVeranstaltungById(Long.parseLong(veranstaltungId)));

        veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

        datePicker.clear();
    }
}