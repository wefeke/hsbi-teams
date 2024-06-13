package com.example.application.views.studierende;

import com.example.application.DoubleToLongConverter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RolesAllowed({"ADMIN", "USER"})
public class StudierendeHinzufuegen extends FormLayout{

    private final TeilnehmerService teilnehmerService;
    private AuthenticatedUser authenticatedUser;

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button save = new Button("Speichern");
    private Teilnehmer teilnehmer;
    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);

    public StudierendeHinzufuegen(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;

        // Layout erstellen und Komponenten hinzufügen
        FormLayout formLayout = createLayout();
        add(formLayout); // Sicherstellen, dass das Layout hinzugefügt wird
        add(new HorizontalLayout(save)); // Button hinzufügen

        configureSaveButton();
        bindFields();
    }

    private FormLayout createLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, matrikelNr);
        return formLayout;
    }

    private void configureSaveButton() {
        save.addClickListener(event -> {
            if (isValidInput()) {
                if (isDuplicateMatrikelNr()) {
                    Notification.show("Matrikelnummer existiert bereits", 3000, Notification.Position.MIDDLE);
                } else {
                    saveTeilnehmer();
                    Notification.show("Teilnehmer gespeichert", 3000, Notification.Position.MIDDLE);
                    //close();
                }
            } else {
                Notification.show("Bitte füllen Sie alle Felder aus", 3000, Notification.Position.MIDDLE);
            }
        });
    }

    private boolean isValidInput() {
        return !firstName.isEmpty() && !lastName.isEmpty() && matrikelNr.getValue() != null;
    }

    private boolean isDuplicateMatrikelNr() {
        Long matrikelNrValue = matrikelNr.getValue().longValue();
        Optional<Teilnehmer> existingTeilnehmer = teilnehmerService.findByMatrikelNr(matrikelNrValue);
        return existingTeilnehmer.isPresent() && (teilnehmer == null || !existingTeilnehmer.get().getId().equals(teilnehmer.getId()));
    }

    private void saveTeilnehmer() {
        Teilnehmer teilnehmer = new Teilnehmer();

        if (binder.writeBeanIfValid(teilnehmer)) {
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                teilnehmerService.saveTeilnehmer(teilnehmer, user);
            }
            Notification.show("Teilnehmer wurde angelegt");
        } else {
            Notification.show("Fehler beim Speichern");
        }
    }

    private void bindFields() {
        binder.forField(firstName)
                .asRequired("Vorname muss gefüllt sein")
                .bind(Teilnehmer::getVorname, Teilnehmer::setVorname);

        binder.forField(lastName)
                .asRequired("Nachname muss gefüllt sein")
                .bind(Teilnehmer::getNachname, Teilnehmer::setNachname);

        binder.forField(matrikelNr)
                .asRequired("Matrikelnummer muss gefüllt sein")
                .withConverter(new DoubleToLongConverter())
                .bind(Teilnehmer::getId, Teilnehmer::setId);
    }
}

//        if (teilnehmer == null) {
//            teilnehmer = new Teilnehmer();
//        }
//        teilnehmer.setVorname(firstName.getValue());
//        teilnehmer.setNachname(lastName.getValue());
//        teilnehmer.setId(matrikelNr.getValue().longValue());
//
//        Optional<User> maybeUser = authenticatedUser.get();
//        User user = maybeUser.get();
//        teilnehmerService.saveTeilnehmer(teilnehmer,user);
//public class StudierendeHinzufuegen extends FormLayout {
//
//    private final TeilnehmerService teilnehmerService;
//
//    //User
//    private AuthenticatedUser authenticatedUser;
//
//    TextField firstName = new TextField("Vorname");
//    TextField lastName = new TextField("Nachname");
//    NumberField matrikelNr = new NumberField("Matrikelnummer");
//    Button save = new Button("Save");
//    private Teilnehmer teilnehmer;
//    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);
//
//    public StudierendeHinzufuegen(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
//        this.teilnehmerService = teilnehmerService;
//        this.authenticatedUser = authenticatedUser;
//
//
//        add(
//                createLayout(),
//                new HorizontalLayout(save)
//        );
//
//        configureSaveButton();
//        bindFields();
//    }

//    public void setTeilnehmer(Teilnehmer teilnehmer) {
//        this.teilnehmer = teilnehmer;
//        if (teilnehmer != null) {
//            firstName.setValue(teilnehmer.getVorname());
//            lastName.setValue(teilnehmer.getNachname());
//            matrikelNr.setValue(teilnehmer.getId().doubleValue());
//        }
//    }
