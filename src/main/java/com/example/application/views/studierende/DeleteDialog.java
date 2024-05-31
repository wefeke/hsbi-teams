package com.example.application.views.studierende;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Gruppenarbeit;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.example.application.models.Veranstaltung;

public class DeleteDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;

    public DeleteDialog(TeilnehmerService teilnehmerService) {
        this.teilnehmerService = teilnehmerService;
    }

    public void openDeleteDialog(Teilnehmer teilnehmer) {
        removeAll();

        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
            Dialog veranstaltungenDialog = new Dialog();
            veranstaltungenDialog.add(new Text("Der Studierende ist noch in folgenden Veranstaltungen gespeichert:"));

            Grid<Veranstaltung> grid = new Grid<>(Veranstaltung.class);
            grid.setItems(teilnehmer.getVeranstaltungen());
            veranstaltungenDialog.add(grid);

            Button deleteButton = new Button("Löschen", event -> {
                teilnehmer.getVeranstaltungen().clear();
                teilnehmer.getGruppen().clear();
                teilnehmer.getGruppenarbeit().clear();
                teilnehmerService.saveTeilnehmer(teilnehmer);
                Notification.show("Studierender aus Veranstaltungen, Gruppen und Gruppenarbeiten entfernt");
                veranstaltungenDialog.close();
                close();
            });

            Button cancelButton = new Button("Abbrechen", event -> veranstaltungenDialog.close());

            veranstaltungenDialog.add(deleteButton, cancelButton);
            veranstaltungenDialog.open();
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
}