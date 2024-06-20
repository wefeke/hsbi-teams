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

@RolesAllowed({"ADMIN", "USER"})
public class DeleteDialog extends Dialog {
    private final TeilnehmerService teilnehmerService;
    private AuthenticatedUser authenticatedUser;
    private Aufraeumen aufraeumen;
    private StudierendeView studierendeView;
    private Teilnehmer teilnehmer;

    H2 infoText = new H2("Empty");
    Button deleteBtn = new Button("Veranstaltungstermin endgültig löschen");
    Button cancelBtn = new Button("Abbrechen");
    Paragraph warningText = new Paragraph("Empty");
    Paragraph noReturn = new Paragraph("Der Studierende ist in keiner Veranstaltung");

    public DeleteDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, Aufraeumen aufraeumen, StudierendeView studierendeView) {
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.aufraeumen = aufraeumen;
        this.studierendeView = studierendeView;

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