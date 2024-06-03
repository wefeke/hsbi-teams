package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

@Route(value = "editDialog")
@RolesAllowed({"ADMIN"})
public class VeranstaltungBearbeiten extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;
    private final Veranstaltung veranstaltung;
    private final VeranstaltungenView veranstaltungenView;
    private Long veranstaltungId;

    //Dialog Items
    private final TextField titelField = new TextField("Titel");
    private final DatePicker datePicker = new DatePicker("Datum");
    private final MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
    private final MultiSelectListBox<Teilnehmer> teilnehmerListe = new MultiSelectListBox<>();;
    private final Button cancelButton= new Button("Abbrechen");
    private final Button saveButton= new Button("Änderungen speichern");


    //Data Binder
    Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);

    public VeranstaltungBearbeiten(VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, UserService userService, Veranstaltung veranstaltung, VeranstaltungenView veranstaltungenView) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;
        this.veranstaltung = veranstaltung;
        this.veranstaltungId = veranstaltung.getVeranstaltungsId();
        this.veranstaltungenView = veranstaltungenView;

        add(createLayout());
        configureElements();
        bindFields();
        readBean();

    }

    public void readBean (){
        binder.readBean(veranstaltungenService.findVeranstaltungById(veranstaltungId));
    }

    private HorizontalLayout createLayout() {
        setHeaderTitle("Veranstaltung \"" + veranstaltung.getTitel() +"\" bearbeiten");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return (
                new HorizontalLayout(
                        new VerticalLayout(titelField, datePicker, comboBox, teilnehmerListe),
                        new VerticalLayout(teilnehmerListe)
                ));
    }

    private void configureElements() {

        //Combobox
        comboBox.setItems(teilnehmerService.findAllTeilnehmer(""));
        comboBox.setValue(veranstaltung.getTeilnehmer());
        comboBox.setItemLabelGenerator(Teilnehmer::getVorname);

        for (Teilnehmer t : veranstaltung.getTeilnehmer()) {
            comboBox.isSelected(t);
        }

        // Populate the MultiSelectListBox with items
        teilnehmerListe.setItems(teilnehmerService.findAllTeilnehmer(""));
        teilnehmerListe.setValue(veranstaltung.getTeilnehmer());
        teilnehmerListe.setItemLabelGenerator(Teilnehmer::getVorname);
        teilnehmerListe.setMaxHeight("260px");
        for (Teilnehmer t : veranstaltung.getTeilnehmer()) {
            teilnehmerListe.select(t);
        }

        //Buttons
        saveButton.addClickListener(event -> {

            Veranstaltung veranstaltung = new Veranstaltung();

            if (binder.writeBeanIfValid(veranstaltung)) {
                veranstaltung.setUser(userService.findAdmin()); //Angemeldeten User holen
                veranstaltung.setVeranstaltungsId(veranstaltungId); //Sichergehen das auch die richtige Veranstaltung bearbeitet wird
                veranstaltungenService.saveVeranstaltung(veranstaltung);

                clearFields();
                close();
                veranstaltungenView.updateKachelContainer();

                Notification.show("Veranstaltung angelegt");
            }
            else {
                Notification.show("Fehler beim Speichern");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.addClickListener(e -> {
            clearFields();
            close();
        });
    }

    private void bindFields() {
        binder.forField(titelField)
                .asRequired("Titel muss gefüllt sein")
                .bind(Veranstaltung::getTitel, Veranstaltung::setTitel);
        binder.forField(datePicker)
                .bind(Veranstaltung::getSemester, Veranstaltung::setSemester);
        binder.forField(comboBox)
                .bind(Veranstaltung::getTeilnehmer, Veranstaltung::setTeilnehmer);
    }

    public void clearFields(){
        titelField.clear();
        datePicker.clear();
        comboBox.clear();
    }

}

