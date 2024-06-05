package com.example.application.views.studierende;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;
import java.util.List;

@RolesAllowed({"ADMIN"})
public class Aufraeumen extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private final Button deleteButton = new Button("löschen");
    private final Button closeButton = new Button("schließen");
    private final Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class);

    public Aufraeumen(TeilnehmerService teilnehmerService) {
        this.teilnehmerService = teilnehmerService;

        closeButton.addClickListener(event ->
                close());

        deleteButton.addClickListener(event -> {
            grid.getSelectedItems().forEach(teilnehmerService::deleteTeilnehmer);
            updateGrid();
        });

        List<Teilnehmer> studierendeVorVierJahren = teilnehmerService.findStudierendeVorVierJahren();
        if (studierendeVorVierJahren.isEmpty()) {
            this.setWidth("30vw");
            this.setHeight("30vh");
            add(
                    new Text("Es gibt keine Studierenden,die vor mehr als 4 Jahren erstellt wurden."),
                    closeButton
            );
        } else {
            this.setWidth("80vw");
            this.setHeight("80vh");
            grid.setSelectionMode(Grid.SelectionMode.MULTI);
            grid.setItems(studierendeVorVierJahren);
            grid.setColumns("vorname", "nachname", "id");
            add(
                    grid,
                    closeButton,
                    deleteButton
            );

        }

    }

    private void updateGrid() {
        List<Teilnehmer> studierendeVorVierJahren = teilnehmerService.findStudierendeVorVierJahren();
        grid.setItems(studierendeVorVierJahren);
    }

//    private void delete (){
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
//
//                // Speichern Sie die Änderungen
//                teilnehmerService.saveTeilnehmer(teilnehmer);
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
//            add(new Text("Möchten Sie den Studierenden " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " wirklich löschen?"));
//
//            Button yesButton = new Button("Ja", event -> {
//                teilnehmerService.deleteTeilnehmer(teilnehmer);
//                Notification.show("Studierender gelöscht");
//                close();
//            });
//
//            Button noButton = new Button("Nein", event -> close());
//
//            add(yesButton, noButton);
//        }
//    }
}
