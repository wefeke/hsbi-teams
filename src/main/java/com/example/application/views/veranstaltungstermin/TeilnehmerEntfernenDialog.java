package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;

public class TeilnehmerEntfernenDialog extends Dialog {

    private final TeilnehmerService teilnehmerService;
    private final Long veranstaltungId;
    private Teilnehmer teilnehmer;

    private final H2 infoText = new H2("");
    private final Span warningText = new Span("Empty");
    private final Button deleteBtn = new Button("Teilnehmer endgültig entfernen");

    /**
     * Erstellt einen Dialog zum Entfernen eines Teilnehmers.
     * Der Dialog enthält eine Benutzeroberfläche mit Informationen und Warnungen, sowie Schaltflächen zum Bestätigen oder Abbrechen der Aktion.
     * Wenn der "Löschen"-Button geklickt wird, wird der Teilnehmer aus der Veranstaltung entfernt und die Ansicht aktualisiert.
     * Wenn der "Abbrechen"-Button geklickt wird, wird der Dialog geschlossen.
     *
     * @param veranstaltungService der Service zur Verwaltung von Veranstaltungen
     * @param teilnehmerService der Service zur Verwaltung von Teilnehmern
     * @param veranstaltungId die ID der Veranstaltung, aus der der Teilnehmer entfernt werden soll
     * @param authenticatedUser der authentifizierte Benutzer, der die Aktion ausführt
     * @param veranstaltungDetailView die Detailansicht der Veranstaltung, die aktualisiert wird
     * @param veranstaltungstermin der Veranstaltungstermin, der aktualisiert wird
     * @param gruppenarbeit die Gruppenarbeit, die aktualisiert wird
     *
     * @autor Joris
     */
    public TeilnehmerEntfernenDialog(VeranstaltungenService veranstaltungService, TeilnehmerService teilnehmerService, Long veranstaltungId, AuthenticatedUser authenticatedUser, VeranstaltungDetailView veranstaltungDetailView, Veranstaltungstermin veranstaltungstermin, Gruppenarbeit gruppenarbeit) {
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungId = veranstaltungId;

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(infoText);
        layout.add(warningText);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button cancelBtn = new Button("Abbrechen");
        buttonLayout.add(deleteBtn, cancelBtn);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        getFooter().add(buttonLayout);


        deleteBtn.addClickListener(event -> {

            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                veranstaltungService.removeTeilnehmerFromVeranstaltung(teilnehmer, veranstaltungId, user);
            }

            if (veranstaltungstermin != null) {
                veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(veranstaltungstermin);

                if (gruppenarbeit != null) {
                    veranstaltungDetailView.setAktiveKachelGruppenarbeit(gruppenarbeit);
                }
            }
            veranstaltungDetailView.update();

            close();
        });

        cancelBtn.addClickListener(event -> close());

        add(layout);
    }

    /**
     * Legt den Teilnehmer für diesen Dialog fest und aktualisiert den Text und den Zustand des Buttons des Dialogs, abhängig davon, ob der Teilnehmer in einer Gruppenarbeit ist.
     * Wenn der Teilnehmer in einer Gruppenarbeit ist, wird der Text des Dialogs anzeigen, dass der Teilnehmer nicht entfernt werden kann und der Löschen-Button wird deaktiviert.
     * Wenn der Teilnehmer nicht in einer Gruppenarbeit ist, wird der Text des Dialogs um Bestätigung bitten, den Teilnehmer zu entfernen und der Löschen-Button wird aktiviert.
     *
     * @param teilnehmer der Teilnehmer, der für diesen Dialog festgelegt werden soll
     *
     * @autor Joris */
    public void setTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;

        if (teilnehmerService.isTeilnehmerInGruppenarbeit(teilnehmer, veranstaltungId)) {
            infoText.setText("Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " löschen");
            warningText.getElement().setProperty("innerHTML", "Der Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " <span class='highlight'>kann nicht entfernt werden</span>, da er bereits in einer aktiven Gruppenarbeit ist.");
            deleteBtn.setEnabled(false);
        } else {
            infoText.setText("Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " löschen");
            warningText.setText("Sind Sie sicher, dass Sie den Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " entfernen möchten?");
            deleteBtn.setEnabled(true);
        }
    }
}
