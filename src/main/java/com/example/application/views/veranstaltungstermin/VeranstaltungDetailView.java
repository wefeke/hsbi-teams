package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.repositories.VeranstaltungenRepository;
import com.example.application.repositories.VeranstaltungsterminRepository;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.*;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Veranstaltung Detail")
@Route(value = "veranstaltung-detail/:veranstaltungId", layout = MainLayout.class)
public class VeranstaltungDetailView extends VerticalLayout implements HasUrlParameter<String> {

    private final VeranstaltungenService veranstaltungService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private String veranstaltungId;

    //Dialog Instance
    private final Dialog addDialog = new Dialog();;

    //Dialog Items
    private final DatePicker startDatePicker = new DatePicker("Termin Datum");
    private final DatePicker endDatePicker = new DatePicker("Ende Terminserie");
    private final TimePicker startTimePicker = new TimePicker("Startzeit");
    private final TimePicker endTimePicker = new TimePicker("Endzeit");
    private final TextField ort = new TextField("Ort");
    private final TextField notizen = new TextField("Notizen");
    private final RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();


    public VeranstaltungDetailView(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;

        //Hier muss noch ein Fehler behoben werden, da die veranstaltungsId ein String ist und in der Datenbank ein Long
        //List<Veranstaltungstermin> termine = veranstaltungsterminService.findVeranstaltungstermineByVeranstaltungId(Long.parseLong(veranstaltungId));
        List<Veranstaltungstermin> termine = veranstaltungsterminService.findAllVeranstaltungstermine();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        Div kachelContainer = new Div();
        kachelContainer.addClassName("veranstaltungen-container");
        kachelContainer.getStyle().set("display", "flex");
        kachelContainer.getStyle().set("flexWrap", "wrap");

        // Kacheln für vorhandene Veranstaltungstermine erstellen
        for (Veranstaltungstermin termin : termine) {
            kachelContainer.add(createVeranstaltungsterminKachel(termin));
        }

        // Kachel für neue Veranstaltung hinzufügen
        kachelContainer.add(createKachel());

        mainLayout.add(kachelContainer);
        add(mainLayout);

        createAddDialog();
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

    private Div createVeranstaltungsterminKachel(Veranstaltungstermin veranstaltungstermin) {
        Div terminInfo = new Div();
        terminInfo.setText(veranstaltungstermin.getDatum().toString());

        terminInfo.getStyle().set("text-align", "center");
        terminInfo.getStyle().set("margin", "auto");

        Div kachelContent = new Div(terminInfo);
        kachelContent.getStyle().set("display", "flex");
        kachelContent.getStyle().set("flex-direction", "column");

        Div kachel = new Div(kachelContent);
        kachel.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "10px")
                .set("padding", "1em")
                .set("margin", "0.5em")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)");
        kachel.setWidth("150px");
        kachel.setHeight("150px");

        kachel.getElement().addEventListener("mouseover", e -> {
            kachel.getStyle().set("background-color", "lightblue");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            kachel.getStyle().set("background-color", "");
        });

        return kachel;
    }

    /**
     * Erstellt eine Kachel mit einem spezifischen Navigationsziel.
     *
     * Diese Kachel zeigt ein Pluszeichen und navigiert zur angegebenen Route, wenn sie angeklickt wird.
     * Die Kachel hat eine Hover-Effekt, der die Hintergrundfarbe der Kachel ändert, wenn der Mauszeiger darüber schwebt.
     *
     * @return Die erstellte Kachel als {@link Div}-Element, bereit zum Hinzufügen zum Container.
     */
    private Div createKachel() {
        //Ich kann diese Methode nicht als static machen, weil getUi() nicht statisch ist.
        //Dadurch muss ich diese Methode in jeder Klasse neu einbauen, wo ich sie verwenden möchte.
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.getStyle()
                .set("font-size", "40px")
                .set("text-align", "center")
                .set("margin", "auto");

        Div neueVeranstaltungKachel = new Div(plusSymbol);
        neueVeranstaltungKachel.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "10px")
                .set("padding", "1em")
                .set("margin", "0.5em")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        neueVeranstaltungKachel.setWidth("150px");
        neueVeranstaltungKachel.setHeight("150px");

        neueVeranstaltungKachel.getElement().addEventListener("mouseover", e -> {
            neueVeranstaltungKachel.getStyle().set("background-color", "lightblue");
        });

        neueVeranstaltungKachel.getElement().addEventListener("mouseout", e -> {
            neueVeranstaltungKachel.getStyle().set("background-color", "");
        });

        neueVeranstaltungKachel.addClickListener(e -> {
            System.out.println(veranstaltungId);
            addDialog.open();
            //getUI().ifPresent(ui -> ui.navigate("add-veranstaltungstermin/" + veranstaltungId));
        });

        return neueVeranstaltungKachel;
    }

    private void createAddDialog () {

        endDatePicker.setVisible(false);

        //Default buttons for Dialog
        Button saveButton= new Button("Save", e -> {
            calcPersistVeranstaltungstermin();
            clearDialogFields();
        });
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> {
            addDialog.close();
            clearDialogFields();
        });

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

        //Add to Dialog
        addDialog.setHeaderTitle("Veranstaltung hinzufügen");

        addDialog.getFooter().add(cancelButton);
        addDialog.getFooter().add(saveButton);

        addDialog.add(

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

        addDialog.close();
        UI.getCurrent().getPage().reload();
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

    public void clearDialogFields(){
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