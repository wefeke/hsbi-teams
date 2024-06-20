package com.example.application.views.veranstaltungen;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VeranstaltungLoeschenDialog extends Dialog {
    //Data
    private Veranstaltung veranstaltung;

    //Services
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final GruppenarbeitService gruppenarbeitService;
    private final GruppeService gruppeService;
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private AuthenticatedUser authenticatedUser;

    //UI Elements
    H2 infoText = new H2("Empty");
    Button deleteBtn = new Button("Veranstaltung endgültig löschen");
    Button cancelBtn = new Button("Abbrechen");
    Paragraph warningText = new Paragraph("Empty");
    Paragraph noReturn = new Paragraph("Empty");

    public VeranstaltungLoeschenDialog(VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, VeranstaltungenView veranstaltungenView, AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        warningText.addClassName("warning-text-delete");
        warningText.getStyle().set("white-space", "pre-line");
        noReturn.addClassName("no-return-text-delete");
        noReturn.getStyle().set("white-space", "pre-line");

        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        deleteBtn.addClickListener(event -> {

            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                this.veranstaltung = veranstaltungenService.findVeranstaltungById(this.veranstaltung.getId(), user);
            }

            assert veranstaltung != null;
            List<Veranstaltungstermin> termine = veranstaltung.getVeranstaltungstermine();
            veranstaltung.removeAllTermine();
            Set<Teilnehmer> teilnehmer = veranstaltung.getTeilnehmer();
            veranstaltung.removeAllTeilnehmer();
            veranstaltungenService.saveVeranstaltung(veranstaltung);

            for(Veranstaltungstermin termin: termine){
                List<Gruppenarbeit> gruppenarbeiten = termin.getGruppenarbeiten();
                termin.removeAllGruppenarbeiten();
                veranstaltungsterminService.saveVeranstaltungstermin(termin);

                for(Gruppenarbeit gruppenarbeit: gruppenarbeiten){
                    List<Gruppe> gruppen = gruppenarbeit.getGruppen();
                    gruppenarbeit.removeAllGruppen();
                    gruppenarbeitService.save(gruppenarbeit);

                    for (Gruppe gruppe : gruppen) {
                        gruppeService.deleteGruppe(gruppe);
                    }

                    termin.removeGruppenarbeit(gruppenarbeit);
                    veranstaltungsterminService.saveVeranstaltungstermin(termin);

                    gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
                }

                veranstaltungsterminService.deleteVeranstaltungstermin(termin);
            }

            for(Teilnehmer teil: teilnehmer){
                teil.removeVeranstaltung(veranstaltung);
                teilnehmerService.updateTeilnehmer(teil);
            }

            veranstaltungenService.deleteVeranstaltung(veranstaltung);
            veranstaltungenView.updateKachelContainer("");

            close();
        });

        cancelBtn.addClickListener(event -> close());

        add(createLayout());
    }

    public void setVeranstaltung(Veranstaltung veranstaltung) {
        this.veranstaltung = veranstaltung;
        int anzGruppenarbeiten = 0;
        int anzGruppen = 0;
        List<Veranstaltungstermin> termine = this.veranstaltung.getVeranstaltungstermine();
        List<Gruppenarbeit> gruppenarbeiten;
        List<Gruppe> gruppen;
        for(Veranstaltungstermin termin: termine){
            gruppenarbeiten = termin.getGruppenarbeiten();
            anzGruppenarbeiten += gruppenarbeiten.size();
            for(Gruppenarbeit gruppenarbeit:gruppenarbeiten){
                gruppen = gruppenarbeit.getGruppen();
                anzGruppen += gruppen.size();
            }
        }

        infoText.setText("Veranstaltung " + this.veranstaltung.getTitel() + " löschen");
        warningText.setText("Wenn du die Veranstaltung " +
                this.veranstaltung.getTitel() +
                " löscht,\n werden " + "auch alle zugehörigen Termine (Anzahl: " +
                termine.size() + "), \ndie zu den Terminen gehörenden Gruppenarbeiten (Gesamtzahl: " +
                anzGruppenarbeiten + ") \nund die zu den Gruppenarbeiten gehörenden Gruppen (Gesamtzahl: "
                + anzGruppen + ") gelöscht.");
        noReturn.setText("Bist du sicher, dass du den Veranstaltungstermin " + this.veranstaltung.getTitel() +
                " löschen willst?\n" + "Das kann nicht rückgängig gemacht werden!");
    }

    private VerticalLayout createLayout(){
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
