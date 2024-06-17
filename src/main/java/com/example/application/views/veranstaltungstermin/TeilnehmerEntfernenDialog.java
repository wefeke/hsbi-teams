package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;

public class TeilnehmerEntfernenDialog extends Dialog {

    private final TeilnehmerService teilnehmerService;
    private final Long veranstaltungId;
    private Teilnehmer teilnehmer;

    private final Text infoText = new Text("");
    private final Button deleteBtn = new Button("Teilnehmer endgültig entfernen");

    public TeilnehmerEntfernenDialog(VeranstaltungenService veranstaltungService, TeilnehmerService teilnehmerService, Long veranstaltungId, AuthenticatedUser authenticatedUser, VeranstaltungDetailView veranstaltungDetailView, Veranstaltungstermin veranstaltungstermin, Gruppenarbeit gruppenarbeit) {
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungId = veranstaltungId;


        VerticalLayout layout = new VerticalLayout();
        layout.add(infoText);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button cancelBtn = new Button("Abbrechen");
        buttonLayout.add(deleteBtn, cancelBtn);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        layout.add(buttonLayout);

        deleteBtn.addClickListener(event -> {

            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                veranstaltungService.removeTeilnehmerFromVeranstaltung(teilnehmer, veranstaltungId, user);
            }

            veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(veranstaltungstermin);
            veranstaltungDetailView.setAktiveKachelGruppenarbeit(gruppenarbeit);
            veranstaltungDetailView.update();

            close();
        });

        cancelBtn.addClickListener(event -> close());

        add(layout);
    }

    public void setTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;

        if (teilnehmerService.isTeilnehmerInGruppenarbeit(teilnehmer, veranstaltungId)) {
            infoText.setText("Der Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " kann nicht entfernt werden, da er bereits in einer aktiven Gruppenarbeit ist.");
            deleteBtn.setEnabled(false);
        } else {
            infoText.setText("Sind Sie sicher, dass Sie den Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " entfernen möchten?");
            deleteBtn.setEnabled(true);
        }
    }
}
