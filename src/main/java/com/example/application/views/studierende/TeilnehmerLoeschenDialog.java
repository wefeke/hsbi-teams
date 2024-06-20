package com.example.application.views.studierende;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@RolesAllowed({"ADMIN", "USER"})
public class TeilnehmerLoeschenDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private AuthenticatedUser authenticatedUser;
    private TeilnehmerAufraeumenDialog teilnehmerAufraeumenDialog;
    private StudierendeView studierendeView;

    public TeilnehmerLoeschenDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, TeilnehmerAufraeumenDialog teilnehmerAufraeumenDialog, StudierendeView studierendeView) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerAufraeumenDialog = teilnehmerAufraeumenDialog;
        this.studierendeView = studierendeView;
    }

    public void openDeleteDialog(List<Teilnehmer> teilnehmerList) {
        removeAll();

//        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
//            add(new Text("Der Studierende " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " ist noch in folgenden Veranstaltungen gespeichert:"));
//
//            Grid<Veranstaltung> grid = new Grid<>();
//            grid.addColumn(Veranstaltung::toString).setHeader("Veranstaltungen");
//            grid.setItems(teilnehmer.getVeranstaltungen());
//            add(grid);
//
//            Button deleteButton = new Button("Löschen", event -> {
//
//                // Entfernen Sie den Teilnehmer aus allen Gruppen
//                for (Gruppe gruppe : new HashSet<>(teilnehmer.getGruppen())) {
//                    gruppe.getTeilnehmer().remove(teilnehmer);
//                    teilnehmer.getGruppen().remove(gruppe);
//                }
//
//                // Entfernen Sie den Teilnehmer aus allen Gruppenarbeiten
//                for (Gruppenarbeit gruppenarbeit : new HashSet<>(teilnehmer.getGruppenarbeiten())) {
//                    gruppenarbeit.getTeilnehmer().remove(teilnehmer);
//                    teilnehmer.getGruppenarbeiten().remove(gruppenarbeit);
//                }
//                // Entfernen Sie den Teilnehmer aus allen Veranstaltungen
//                for (Veranstaltung veranstaltung : new HashSet<>(teilnehmer.getVeranstaltungen())) {
//                    veranstaltung.getTeilnehmer().remove(teilnehmer);
//                    teilnehmer.getVeranstaltungen().remove(veranstaltung);
//                }
//
//                // Speichern Sie die Änderungen
//                Optional<User> maybeUser = authenticatedUser.get();
//                User user = maybeUser.get();
//                teilnehmerService.saveTeilnehmer(teilnehmer, user);
//
//                // Löschen Sie den Teilnehmer
//                //teilnehmerService.deleteTeilnehmer(teilnehmer);
//
//                Notification.show("Studierender aus Veranstaltungen, Gruppen und Gruppenarbeiten entfernt");
//                close();
//            });
//
//            Button cancelButton = new Button("Abbrechen", event -> close());
//
//            add(deleteButton, cancelButton);
//        } else {
        if(teilnehmerList.size() == 1) {
            add(new Text("Möchten Sie den Studierenden " + teilnehmerList.get(0).getVorname() + " " + teilnehmerList.get(0).getNachname() + " wirklich löschen?"));
        }
        else if(teilnehmerList.size() > 1) {
            add(new Text("Möchten Sie die " + teilnehmerList.size() + " ausgewählten Studierenden wirklich löschen?"));
        }
        Button yesButton = new Button("Ja", event -> {
            for (Teilnehmer teilnehmer : teilnehmerList) {
                teilnehmerService.deleteTeilnehmer(teilnehmer);
            }
            Notification.show("Studierende gelöscht");
            close();
            teilnehmerAufraeumenDialog.close();
            studierendeView.updateStudierendeView();
            teilnehmerAufraeumenDialog.updateGridNoEvent();

        });


            Button noButton = new Button("Nein", event -> close());

            add(yesButton, noButton);
            open();
        }

    }
