package com.example.application.views.studierende;

import com.example.application.models.Teilnehmer;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@UIScope
public class StudierendeHinzufuegen extends FormLayout {

    private final TeilnehmerService teilnehmerService;

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");

    Button save = new Button("Save");
    private Teilnehmer teilnehmer;

    @Autowired
    public StudierendeHinzufuegen(TeilnehmerService teilnehmerService) {
        this.teilnehmerService = teilnehmerService;
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
            }
            else {
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
    }
}
