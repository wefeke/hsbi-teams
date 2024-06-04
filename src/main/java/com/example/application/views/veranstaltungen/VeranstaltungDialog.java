package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "addDialog")
@RolesAllowed({"ADMIN", "USER"})
public class VeranstaltungDialog extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;
    private final VeranstaltungenView veranstaltungenView;

    //Dialog Items
    private final TextField titelField = new TextField("Titel");
    private final DatePicker datePicker = new DatePicker("Datum");
    private final MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
    private final Button cancelButton= new Button("Cancel");
    private final Button saveButton= new Button("Save");

    //Data Binder
    Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);

    public VeranstaltungDialog(VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, UserService userService, VeranstaltungenView veranstaltungenView) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;
        this.veranstaltungenView = veranstaltungenView;
        add(createLayout());
        configureElements();
        bindFields();
    }

    private HorizontalLayout createLayout() {
        setHeaderTitle("Veranstaltung hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return (
                new HorizontalLayout(
                        new VerticalLayout(titelField, datePicker, comboBox)
                ));
        }

    private void configureElements() {

        //Combobox
        comboBox.setItems(teilnehmerService.findAllTeilnehmer(""));
        comboBox.setItemLabelGenerator(Teilnehmer::getVorname);


        //Buttons
        saveButton.addClickListener(event -> {

            Veranstaltung veranstaltung = new Veranstaltung();

            if (binder.writeBeanIfValid(veranstaltung)) {
                veranstaltung.setUser(userService.findAdmin()); //Angemeldeten User holen
                veranstaltungenService.saveVeranstaltung(veranstaltung);
                veranstaltungenView.updateKachelContainer();
                clearFields();
                close();

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
