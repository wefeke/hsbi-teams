package com.example.application.views.studierende;

import com.example.application.models.*;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.security.RolesAllowed;


import java.util.Set;

/**
 * Diese Klasse repräsentiert einen Dialog, der es ermöglicht, mehrere Teilnehmer gleichzeitig zu löschen.
 * Sie erlaubt den Zugriff für die Rollen "ADMIN" und "USER".
 * Der Dialog enthält einen Info-Text und zwei Schaltflächen: "Studierenden endgültig löschen" und "Abbrechen".
 * Der "Löschen"-Button löscht die ausgewählten Teilnehmer und aktualisiert die Ansicht entsprechend.
 * Der "Abbrechen"-Button schließt den Dialog.
 * Der Dialog enthält auch ein Layout, das zentriert ist und den Info-Text sowie die Schaltflächen zum Löschen und Abbrechen enthält.
 *
 * @author Tobias
 */
@RolesAllowed({"ADMIN", "USER"})
public class TeilnehmerMehreLoeschenDialog extends Dialog {
    private Set<Teilnehmer> teilnehmerSet;

    H2 infoText = new H2("Empty");
    Button deleteBtn = new Button("Studierenden endgültig löschen");
    Button cancelBtn = new Button("Abbrechen");

    /**
     * Konstruktor für die TeilnehmerMehreLoeschenDialog Klasse.
     * Initialisiert die Click-Listener für die Schaltflächen "Löschen" und "Abbrechen".
     * Der "Löschen"-Button löscht die Teilnehmer und aktualisiert die Ansicht entsprechend.
     * Der "Abbrechen"-Button schließt den Dialog.
     * Fügt das erstellte Layout zum Dialog hinzu.
     *
     * @param teilnehmerService Der Service, der für die Verwaltung der Teilnehmer benötigt wird.
     * @param aufraeumen Die Instanz von TeilnehmerAufraeumenDialog, die für die Aktualisierung der Ansicht benötigt wird.
     * @param studierendeView Die StudierendeView, die aktualisiert wird, wenn Teilnehmer gelöscht werden.
     * @author Tobias
     */
    public TeilnehmerMehreLoeschenDialog(TeilnehmerService teilnehmerService, TeilnehmerAufraeumenDialog aufraeumen, StudierendeView studierendeView) {


        deleteBtn.addClickListener(event -> {

            for (Teilnehmer teilnehmer : teilnehmerSet) {
                Double years = aufraeumen.getYearsFieldValue();
                teilnehmerService.deleteTeilnehmer(teilnehmer);
                studierendeView.updateStudierendeView();
                aufraeumen.updateGridNoEvent();
                if (years != null) {
                    aufraeumen.updateGridOld(years.intValue());
                close();
                }
                else {
                close();
                }
            }
        });
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelBtn.addClickListener(event -> close());

        add(createLayout());
    }

    /**
     * Setzt das Set von Teilnehmern und aktualisiert den Info-Text entsprechend.
     * Der Info-Text wird auf die Frage gesetzt, ob der Benutzer wirklich die Anzahl der Teilnehmer im Set löschen möchte.
     *
     * @param teilnehmerSet Das Set von Teilnehmern, das gesetzt werden soll.
     * @author Tobias
     */
    public void setTeilnehmer(Set<Teilnehmer> teilnehmerSet) {
        this.teilnehmerSet = teilnehmerSet;

        infoText.setText("Wollen Sie die " + teilnehmerSet.size() + " Teilnehmer wirklich löschen?");
    }

    /**
     * Erstellt ein neues VerticalLayout und fügt die notwendigen Komponenten hinzu.
     * Das erstellte Layout wird zentriert und enthält den Info-Text sowie die Schaltflächen zum Löschen und Abbrechen.
     * Die Schaltflächen zum Löschen und Abbrechen werden auch zur Fußzeile des Dialogs hinzugefügt.
     *
     * @return Ein VerticalLayout, das den Info-Text und die Schaltflächen zum Löschen und Abbrechen enthält.
     * @author Tobias
     */
    public VerticalLayout createLayout(){
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(infoText);
        getFooter().add(cancelBtn);
        getFooter().add(deleteBtn);
        return mainLayout;
    }

}