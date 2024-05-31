package com.example.application.views.studierende;

import com.example.application.models.Gruppe;
import com.example.application.models.Teilnehmer;
import com.example.application.models.Gruppenarbeit;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.example.application.models.Veranstaltung;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;

@RolesAllowed({"ADMIN"})

public class DeleteDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;

    public DeleteDialog(TeilnehmerService teilnehmerService) {
        this.teilnehmerService = teilnehmerService;
    }

    public void openDeleteDialog(Teilnehmer teilnehmer) {
        removeAll();

        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
            add(new Text("Der Studierende " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " ist noch in folgenden Veranstaltungen gespeichert:"));

            Grid<Veranstaltung> grid = new Grid<>();
            grid.addColumn(Veranstaltung::toString).setHeader("Veranstaltungen");
            grid.setItems(teilnehmer.getVeranstaltungen());
            add(grid);

            Button deleteButton = new Button("Löschen", event -> {

                // Entfernen Sie den Teilnehmer aus allen Gruppen
                for (Gruppe gruppe : new HashSet<>(teilnehmer.getGruppen())) {
                    gruppe.getTeilnehmer().remove(teilnehmer);
                    teilnehmer.getGruppen().remove(gruppe);
                }

                // Entfernen Sie den Teilnehmer aus allen Gruppenarbeiten
                for (Gruppenarbeit gruppenarbeit : new HashSet<>(teilnehmer.getGruppenarbeiten())) {
                    gruppenarbeit.getTeilnehmer().remove(teilnehmer);
                    teilnehmer.getGruppenarbeiten().remove(gruppenarbeit);
                }
                // Entfernen Sie den Teilnehmer aus allen Veranstaltungen
                for (Veranstaltung veranstaltung : new HashSet<>(teilnehmer.getVeranstaltungen())) {
                    veranstaltung.getTeilnehmer().remove(teilnehmer);
                    teilnehmer.getVeranstaltungen().remove(veranstaltung);
                }


                // Speichern Sie die Änderungen
                teilnehmerService.saveTeilnehmer(teilnehmer);

                // Löschen Sie den Teilnehmer
                //teilnehmerService.deleteTeilnehmer(teilnehmer);

                Notification.show("Studierender aus Veranstaltungen, Gruppen und Gruppenarbeiten entfernt");
                close();
            });

            Button cancelButton = new Button("Abbrechen", event -> close());

            add(deleteButton, cancelButton);
        } else {
            add(new Text("Möchten Sie den Studierenden " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " wirklich löschen?"));

            Button yesButton = new Button("Ja", event -> {
                teilnehmerService.deleteTeilnehmer(teilnehmer);
                Notification.show("Studierender gelöscht");
                close();
            });

            Button noButton = new Button("Nein", event -> close());

            add(yesButton, noButton);
        }
        open();
    }
//    public void openDeleteDialog(Teilnehmer teilnehmer) {
//    removeAll(); // Clear previous content
//
//    add(new Text("Möchten Sie den Studierenden " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " wirklich löschen?"));
//
//    Button yesButton = new Button("Ja", event -> {
//        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
//            Notification.show("Der Studierende ist noch in einer Veranstaltung gespeichert und kann nicht gelöscht werden.");
//        } else {
//            teilnehmerService.deleteTeilnehmer(teilnehmer);
//            Notification.show("Studierender gelöscht");
//            close();
//        }
//    });
//
//    Button noButton = new Button("Nein", event -> close());
//
//    add(yesButton, noButton);
//    open();
//}
}