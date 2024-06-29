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
 * Diese Klasse repräsentiert einen Dialog zum Löschen von Teilnehmern.
 * Sie erbt von der Dialog-Klasse von Vaadin und bietet eine Benutzeroberfläche zum Anzeigen und Löschen von Teilnehmern.
 * Der Dialog enthält Informationen über den zu löschenden Teilnehmer und Schaltflächen zum endgültigen Löschen oder zum Abbrechen des Vorgangs.
 * Die Klasse verwendet einen TeilnehmerService zum Abrufen und Löschen von Teilnehmern.
 * Sie enthält auch eine Referenz auf eine StudierendeView und einen TeilnehmerAufraeumenDialog, die aktualisiert werden, wenn Teilnehmer gelöscht werden.
 * Darüber hinaus bietet die Klasse die Möglichkeit, Teilnehmer basierend auf der Anzahl der Jahre, die sie registriert sind, oder ob sie eine Veranstaltung haben, anzuzeigen und zu löschen.
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
     * Konstruktor für die Klasse TeilnehmerLoeschenDialog.
     * Initialisiert die Instanzvariablen und setzt die Klick-Listener für die Schaltflächen "deleteBtn" und "cancelBtn".
     * Fügt das erstellte Layout zum Dialog hinzu.
     *
     * @param teilnehmerService Der Service, der für die Verwaltung von Teilnehmern verwendet wird.
     * @param aufraeumen Der Dialog, der zum Aufräumen von Teilnehmern verwendet wird.
     * @param studierendeView Die Ansicht, die die Studierenden anzeigt.
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
     * Setzt den Teilnehmer und aktualisiert die Benutzeroberfläche entsprechend.
     * Wenn der Teilnehmer in einer Veranstaltung ist, wird eine Warnmeldung angezeigt und der "deleteBtn" wird deaktiviert.
     * Wenn der Teilnehmer nicht in einer Veranstaltung ist, wird eine Bestätigungsnachricht angezeigt und der "deleteBtn" wird aktiviert.
     *
     @param teilnehmerSet Das Set von Teilnehmern, das gesetzt werden soll.
      * @author Tobias
     */
    public void setTeilnehmer(Set<Teilnehmer> teilnehmerSet) {
        this.teilnehmerSet = teilnehmerSet;

        infoText.setText("Wollen Sie die " + teilnehmerSet.size() + " Teilnehmer wirklich löschen?");
    }

    /**
     * Erstellt ein VerticalLayout und fügt die Info-, Warn- und NoReturn-Texte hinzu.
     * Setzt die Ausrichtung der Elemente auf Zentrum.
     * Fügt die "cancelBtn" und "deleteBtn" zum Footer hinzu.
     *
     * @return Das erstellte VerticalLayout mit den hinzugefügten Elementen.
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