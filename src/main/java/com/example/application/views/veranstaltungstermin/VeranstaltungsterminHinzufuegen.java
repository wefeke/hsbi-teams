package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@PageTitle("Veranstaltungstermin hinzufügen")
@Route(value = "add-veranstaltungstermin/:veranstaltungId", layout = MainLayout.class)
public class VeranstaltungsterminHinzufuegen extends VerticalLayout implements HasUrlParameter<String> {

    private final VeranstaltungsterminService veranstaltungsterminService;
    private final VeranstaltungenService veranstaltungService;
    private String veranstaltungId;

    H1 header = new H1("Veranstaltungstermin hinzufügen");
    DatePicker startDatePicker = new DatePicker("Termin Datum");
    DatePicker endDatePicker = new DatePicker("Ende Terminserie");
    TimePicker startTimePicker = new TimePicker("Startzeit");
    TimePicker endTimePicker = new TimePicker("Endzeit");

    TextField ort = new TextField("Ort");
    TextField notizen = new TextField("Notizen");
    Button save= new Button("Save");
    RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();


    public VeranstaltungsterminHinzufuegen(VeranstaltungenService veranstaltungService , VeranstaltungsterminService veranstaltungsterminService ) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;

        save.addClickListener(e -> {
            saveVeranstaltungstermin();
        });

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

        endDatePicker.setVisible(false);

        add(
                header,
                new HorizontalLayout(
                        new VerticalLayout(
                                notizen,
                                startTimePicker,
                                startDatePicker,
                                radioGroup,
                                save
                        ),

                        new VerticalLayout(
                                ort,
                                endTimePicker,
                                endDatePicker
                        )
                )

        );

        //Center all Elements in the Main Vertical Layout
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

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
        //Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        //Extract values from the TimePicker Fields
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        //"Einmalig" Save Event
        if (radioGroup.getValue().equals("Einmalig")) {
            persistInstance(startDate, endDate);
        }
        else if (radioGroup.getValue().equals("Wöchentlich")) {
            persistInstance(startDate, endDate);
            startDate = startDate.plusDays(7);

            while (!startDate.isAfter(endDate)) {
                // If the date is a Saturday or Sunday, adjust it to the next Monday
                if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    startDate = startDate.plusDays(2);
                } else if (startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    startDate = startDate.plusDays(1);
                }

                persistInstance(startDate, endDate);

                // Increment the date by 7 days
                startDate = startDate.plusDays(7);
            }
        }
        else if (radioGroup.getValue().equals("Monatlich")) {
            persistInstance(startDate, endDate);
            startDate = startDate.plusMonths(1);

            while (!startDate.isAfter(endDate)) {
                if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    startDate = startDate.plusDays(2);
                } else if (startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    startDate = startDate.plusDays(1);
                }
                persistInstance(startDate, endDate);
                startDate = startDate.plusMonths(1);
            }
        }

        clearAllFields();
    }

    public void clearAllFields(){
        //Clear all Fields after saving
        startDatePicker.clear();
        endDatePicker.clear();
        startTimePicker.clear();
        endTimePicker.clear();
        ort.clear();
        notizen.clear();
        radioGroup.setValue("Einmalig");
    }

    public void persistInstance(LocalDate startDate, LocalDate endDate){
        Veranstaltungstermin veranstaltungstermin = new Veranstaltungstermin();
        veranstaltungstermin.setDatum(startDate);
        veranstaltungstermin.setUhrzeit(startTimePicker.getValue());
        veranstaltungstermin.setOrt(ort.getValue());
        veranstaltungstermin.setNotizen(notizen.getValue());
        veranstaltungstermin.setVeranstaltung(veranstaltungService.findVeranstaltungById(Long.parseLong(veranstaltungId)));
        veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

        Notification.show("You saved an entry");
    }
}