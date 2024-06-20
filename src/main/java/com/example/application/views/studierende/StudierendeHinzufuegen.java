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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@RolesAllowed({"ADMIN", "USER"})
public class StudierendeHinzufuegen extends Dialog {

    private final TeilnehmerService teilnehmerService;
    private AuthenticatedUser authenticatedUser;


    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button save = new Button("Speichern");
    Button cancel = new Button("Abbrechen");
    private Teilnehmer teilnehmer;
    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);
    private StudierendeView studierendeView;

    public StudierendeHinzufuegen(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser,StudierendeView studierendeView){
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.studierendeView = studierendeView;

        // Layout erstellen und Komponenten hinzufügen
        setHeaderTitle("Studierenden hinzufügen");
        add(createLayout()); // Sicherstellen, dass das Layout hinzugefügt wird
        getFooter().add(cancel, save); // Button hinzufügen


        configureButtons();
        bindFields();
    }

    private FormLayout createLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, matrikelNr);
        return formLayout;
    }

    private void configureButtons() {
        save.addClickListener(event -> {
            if (isValidInput()) {
                if (isDuplicateMatrikelNr()) {
                    Notification.show("Matrikelnummer existiert bereits", 3000, Notification.Position.MIDDLE);
                } else {
                    saveTeilnehmer();
                    Notification.show("Teilnehmer gespeichert", 3000, Notification.Position.MIDDLE);
                    close();
                }
            } else {
                Notification.show("Bitte füllen Sie alle Felder aus", 3000, Notification.Position.MIDDLE);
            }
        });
        cancel.addClickListener(event -> {
            close();
        });
    }

    private boolean isValidInput() {
        return !firstName.isEmpty() && !lastName.isEmpty() && matrikelNr.getValue() != null;
    }

    private boolean isDuplicateMatrikelNr() {
    Long matrikelNrValue = matrikelNr.getValue().longValue();
    Optional<User> maybeUser = authenticatedUser.get();
    if (maybeUser.isPresent()) {
        Long userId = maybeUser.get().getId();
        Optional<Teilnehmer> existingTeilnehmer = teilnehmerService.findByMatrikelNrAndUserId(matrikelNrValue, userId);
        return existingTeilnehmer.isPresent() && (teilnehmer == null || !existingTeilnehmer.get().getId().equals(teilnehmer.getId()));
    }
    return false;
}

    private void saveTeilnehmer() {
        Teilnehmer teilnehmer = new Teilnehmer();

        if (binder.writeBeanIfValid(teilnehmer)) {
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                teilnehmerService.saveTeilnehmer(teilnehmer, user);
                studierendeView.updateStudierendeView(); // Grid aktualisieren
                clearFields();
                close();
                Notification.show("Teilnehmer wurde angelegt");
            } else {
                Notification.show("Fehler beim Speichern");
            }
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
                .withValidator(matrikelNr -> String.valueOf(matrikelNr.longValue()).matches("\\d{7}"), "Matrikelnummer muss genau 7 Zahlen enthalten")
                .withConverter(new DoubleToLongConverter())
                .bind(Teilnehmer::getId, Teilnehmer::setId);
    }

    private void clearFields() {
        firstName.clear();
        lastName.clear();
        matrikelNr.clear();
    }
}
