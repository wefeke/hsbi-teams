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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;

public class TeilnehmerEntfernenDialog extends Dialog {

    private final VeranstaltungenService veranstaltungService;
    private final TeilnehmerService teilnehmerService;
    private final Long veranstaltungId;
    private Teilnehmer teilnehmer;
    private AuthenticatedUser authenticatedUser;
    private final VeranstaltungDetailView veranstaltungDetailView;
    private Veranstaltungstermin veranstaltungstermin;
    private Gruppenarbeit gruppenarbeit;

    private Text infoText = new Text("");
    private Button deleteBtn = new Button("Teilnehmer endgültig entfernen");
    private Button cancelBtn = new Button("Abbrechen");

    public TeilnehmerEntfernenDialog(VeranstaltungenService veranstaltungService, TeilnehmerService teilnehmerService, Long veranstaltungId, AuthenticatedUser authenticatedUser, VeranstaltungDetailView veranstaltungDetailView, Veranstaltungstermin veranstaltungstermin, Gruppenarbeit gruppenarbeit) {
        this.veranstaltungService = veranstaltungService;
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungId = veranstaltungId;
        this.authenticatedUser = authenticatedUser;
        this.veranstaltungDetailView = veranstaltungDetailView;
        this.veranstaltungstermin = veranstaltungstermin;
        this.gruppenarbeit = gruppenarbeit;


        VerticalLayout layout = new VerticalLayout();
        layout.add(infoText);

        HorizontalLayout buttonLayout = new HorizontalLayout();
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
