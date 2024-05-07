package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

@Route(value = "add-veranstaltung", layout = MainLayout.class)
@PageTitle("Veranstaltung hinzufügen")
public class VeranstaltungenHinzufuegen extends VerticalLayout {

    private final VeranstaltungenService veranstaltungenService;
    private final UserService userService;
    private final TeilnehmerService teilnehmerService;

    H1 header = new H1("Veranstaltung hinzufügen");
    TextField titelField = new TextField("Titel");
    DatePicker datePicker = new DatePicker("Datum");
    Button save = new Button("Save");
    Button close = new Button("Close");
    MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
    Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class, false);

    @Autowired
    public VeranstaltungenHinzufuegen(VeranstaltungenService veranstaltungenService, UserService userService, TeilnehmerService teilnehmerService) {

        this.veranstaltungenService = veranstaltungenService;
        this.userService = userService;
        this.teilnehmerService = teilnehmerService;

        add(
                header,
                createHorizontalLayout()
        );

        save.addClickListener(e -> saveVeranstaltung());
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.AUTO);
    }

    private void saveVeranstaltung() {
        String titel = titelField.getValue();
        Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Veranstaltung veranstaltung = new Veranstaltung();
        veranstaltung.setTitel(titel);
        veranstaltung.setSemester(date);
        veranstaltung.setUser(userService.findAdmin());

        veranstaltung.setTeilnehmer(new ArrayList<>(comboBox.getSelectedItems()));

        veranstaltungenService.saveVeranstaltung(veranstaltung);

        Notification.show("Veranstaltung gespeichert");

        titelField.clear();
        datePicker.clear();

    }

    private HorizontalLayout createHorizontalLayout (){
        VerticalLayout leftSide = new VerticalLayout();
        setFlexGrow(2, leftSide);
        VerticalLayout rightSide = new VerticalLayout();
        setFlexGrow(1, rightSide);

        comboBox.setItems(teilnehmerService.findAllTeilnehmer());
        comboBox.setItemLabelGenerator(Teilnehmer::getVorname);

        grid.setItems(teilnehmerService.findAllTeilnehmer());
        grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
        grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
        grid.addColumn(Teilnehmer::getId).setHeader("ID");

        leftSide.add(
                titelField,
                datePicker,
                comboBox,
                save,
                close

        );

        rightSide.add(
                grid
        );

        return new HorizontalLayout(leftSide, rightSide);
    }
}