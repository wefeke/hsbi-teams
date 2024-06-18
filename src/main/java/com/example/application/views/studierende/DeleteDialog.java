package com.example.application.views.studierende;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RolesAllowed({"ADMIN", "USER"})
public class DeleteDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private AuthenticatedUser authenticatedUser;
    private Aufraeumen aufraeumen;
    private StudierendeView studierendeView;
    private Teilnehmer teilnehmer;

    private final H2 infoText = new H2("");
    private final Span warningText = new Span("Empty");
    private final Button deleteBtn = new Button("Teilnehmer endgültig entfernen");

    public DeleteDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, Aufraeumen aufraeumen, StudierendeView studierendeView) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.aufraeumen = aufraeumen;
        this.studierendeView = studierendeView;
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

        cancelBtn.addClickListener(event -> close());

        add(layout);
    }

    public void setTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;

        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
            List<Veranstaltung> veranstaltungen = teilnehmerService.getVeranstaltungenOfTeilnehmer(teilnehmer);
            String veranstaltungenString = veranstaltungen.stream()
                    .map(Veranstaltung::toString)
                    .collect(Collectors.joining(", "));

            infoText.setText("Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " löschen");
            warningText.getElement().setProperty("innerHTML", "Der Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " <span class='highlight'>kann nicht entfernt werden</span>, da er bereits in folgenden Veranstaltungen ist: " + veranstaltungenString);
            deleteBtn.setEnabled(false);
        } else {
            infoText.setText("Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " löschen");
            warningText.setText("Sind Sie sicher, dass Sie den Teilnehmer " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " entfernen möchten?");
            deleteBtn.setEnabled(true);
        }

    }
//        removeAll();
//
////        if (teilnehmerService.isTeilnehmerInVeranstaltung(teilnehmer)) {
////            add(new Text("Der Studierende " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " ist noch in folgenden Veranstaltungen gespeichert:"));
////
////            Grid<Veranstaltung> grid = new Grid<>();
////            grid.addColumn(Veranstaltung::toString).setHeader("Veranstaltungen");
////            grid.setItems(teilnehmer.getVeranstaltungen());
////            add(grid);
////
////            Button deleteButton = new Button("Löschen", event -> {
////
////                // Entfernen Sie den Teilnehmer aus allen Gruppen
////                for (Gruppe gruppe : new HashSet<>(teilnehmer.getGruppen())) {
////                    gruppe.getTeilnehmer().remove(teilnehmer);
////                    teilnehmer.getGruppen().remove(gruppe);
////                }
////
////                // Entfernen Sie den Teilnehmer aus allen Gruppenarbeiten
////                for (Gruppenarbeit gruppenarbeit : new HashSet<>(teilnehmer.getGruppenarbeiten())) {
////                    gruppenarbeit.getTeilnehmer().remove(teilnehmer);
////                    teilnehmer.getGruppenarbeiten().remove(gruppenarbeit);
////                }
////                // Entfernen Sie den Teilnehmer aus allen Veranstaltungen
////                for (Veranstaltung veranstaltung : new HashSet<>(teilnehmer.getVeranstaltungen())) {
////                    veranstaltung.getTeilnehmer().remove(teilnehmer);
////                    teilnehmer.getVeranstaltungen().remove(veranstaltung);
////                }
////
////                // Speichern Sie die Änderungen
////                Optional<User> maybeUser = authenticatedUser.get();
////                User user = maybeUser.get();
////                teilnehmerService.saveTeilnehmer(teilnehmer, user);
////
////                // Löschen Sie den Teilnehmer
////                //teilnehmerService.deleteTeilnehmer(teilnehmer);
////
////                Notification.show("Studierender aus Veranstaltungen, Gruppen und Gruppenarbeiten entfernt");
////                close();
////            });
////
////            Button cancelButton = new Button("Abbrechen", event -> close());
////
////            add(deleteButton, cancelButton);
////        } else {
//        if(teilnehmerList.size() == 1) {
//            add(new Text("Möchten Sie den Studierenden " + teilnehmerList.get(0).getVorname() + " " + teilnehmerList.get(0).getNachname() + " wirklich löschen?"));
//        }
//        else if(teilnehmerList.size() > 1) {
//            add(new Text("Möchten Sie die " + teilnehmerList.size() + " ausgewählten Studierenden wirklich löschen?"));
//        }
//        Button yesButton = new Button("Ja", event -> {
//            for (Teilnehmer teilnehmer : teilnehmerList) {
//                teilnehmerService.deleteTeilnehmer(teilnehmer);
//            }
//            Notification.show("Studierende gelöscht");
//            close();
//            aufraeumen.close();
//            studierendeView.updateStudierendeView();
//            aufraeumen.updateGridNoEvent();
//
//        });
//
//
//            Button noButton = new Button("Nein", event -> close());
//
//            add(yesButton, noButton);
//            open();
//        }
//

}
