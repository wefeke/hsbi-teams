package com.example.application.views.studierende;

import com.example.application.DoubleToLongConverter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.vaadin.flow.data.converter.StringToLongConverter;

import java.util.Optional;

@Component
@UIScope
@RolesAllowed({"ADMIN", "USER"})
public class StudierendeHinzufuegen extends FormLayout {

    private final TeilnehmerService teilnehmerService;

    //User
    private AuthenticatedUser authenticatedUser;

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button save = new Button("Save");
    private Teilnehmer teilnehmer;
    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);

    @Autowired
    public StudierendeHinzufuegen(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        addClassName("contact-form");

        add(
                firstName,
                lastName,
                matrikelNr,
                createButtonsLayout()
        );

        configureSaveButton();
    }

    public void setTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;
        if (teilnehmer != null) {
            firstName.setValue(teilnehmer.getVorname());
            lastName.setValue(teilnehmer.getNachname());
            matrikelNr.setValue(teilnehmer.getId().doubleValue());
        }
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        return new HorizontalLayout(save);
    }

    private void configureSaveButton() {
        save.addClickListener(event -> {
            if (isValidInput()) {
                if (isDuplicateMatrikelNr()) {
                    Notification.show("Matrikelnummer already exists", 3000, Notification.Position.MIDDLE);
                } else {
                    saveTeilnehmer();
                    Notification.show("Teilnehmer saved", 3000, Notification.Position.MIDDLE);
                }
            } else {
                Notification.show("Please fill out all fields", 3000, Notification.Position.MIDDLE);
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
        if (teilnehmer == null) {
            teilnehmer = new Teilnehmer();
        }
        teilnehmer.setVorname(firstName.getValue());
        teilnehmer.setNachname(lastName.getValue());
        teilnehmer.setId(matrikelNr.getValue().longValue());
        teilnehmerService.saveTeilnehmer(teilnehmer);
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            AuthenticatedUser authenticatedUser;

        }

//    private void bindFields(){
//        binder.forField(firstName)
//                .bind(Teilnehmer::getVorname, Teilnehmer::setVorname);
//        binder.forField(lastName)
//                .bind(Teilnehmer::getNachname, Teilnehmer::setNachname);
//        binder.forField(matrikelNr)
//                .withConverter(new DoubleToLongConverter())
//                .bind(Teilnehmer::getId, Teilnehmer::setId);
//    }
    }
}

