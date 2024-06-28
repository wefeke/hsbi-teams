package com.example.application.views.studierende;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
public class TeilnehmerLoeschenDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;


    private Teilnehmer teilnehmer;

    H2 infoText = new H2("Empty");
    Button deleteBtn = new Button("Studierenden endgültig löschen");
    Button cancelBtn = new Button("Abbrechen");
    Paragraph warningText = new Paragraph("Empty");
    Paragraph noReturn = new Paragraph("Der Studierende ist in keiner Veranstaltung");

    /**
     * Konstruktor für die Klasse TeilnehmerLoeschenDialog.
     * Initialisiert die Instanzvariablen und setzt die Klick-Listener für die Schaltflächen "deleteBtn" und "cancelBtn".
     * Fügt das erstellte Layout zum Dialog hinzu.
     *
     * @param teilnehmerService Der Service, der für die Verwaltung von Teilnehmern verwendet wird.
     * @param authenticatedUser Der authentifizierte Benutzer.
     * @param aufraeumen Der Dialog, der zum Aufräumen von Teilnehmern verwendet wird.
     * @param studierendeView Die Ansicht, die die Studierenden anzeigt.
     * @author Tobias
     */
    public TeilnehmerLoeschenDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, TeilnehmerAufraeumenDialog aufraeumen, StudierendeView studierendeView) {
        this.teilnehmerService = teilnehmerService;


        warningText.addClassName("warning-text-delete");
        warningText.getStyle().set("white-space", "pre-line");
        noReturn.addClassName("no-return-text-delete");
        noReturn.getStyle().set("white-space", "pre-line");

        deleteBtn.addClickListener(event -> {
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
     * @param teilnehmer Der Teilnehmer, der gesetzt werden soll.
     * @author Tobias
     */
    public void setTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;

        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
            List<Veranstaltung> veranstaltungen = teilnehmerService.getVeranstaltungenOfTeilnehmer(teilnehmer);
            String veranstaltungenString = IntStream.range(0, veranstaltungen.size())
                    .mapToObj(i -> (i + 1) + ". " + veranstaltungen.get(i).getTitel())
                    .collect(Collectors.joining("<br>"));

            infoText.setText("Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " löschen");

            noReturn.setText("Bitte entfernen Sie den Teilnehmer zuerst aus den Veranstaltungen.");
            warningText.getElement().setProperty("innerHTML", "Der Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " <span class='highlight'>kann nicht entfernt werden</span>, da er bereits in folgenden Veranstaltungen ist:<br>" + veranstaltungenString);
            deleteBtn.setEnabled(false);

        } else {
            infoText.setText("Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " löschen");
            warningText.setText("Sind Sie sicher, dass Sie den Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " entfernen möchten?");
            deleteBtn.setEnabled(true);
        }
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
        mainLayout.add(warningText);
        mainLayout.add(noReturn);
        getFooter().add(cancelBtn);
        getFooter().add(deleteBtn);
        return mainLayout;
    }

}