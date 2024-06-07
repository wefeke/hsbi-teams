package com.example.application.views.veranstaltungen;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
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

public class VeranstaltungLoeschenDialog extends Dialog {
    //Data
    private Veranstaltung veranstaltung;

    //UI Elements
    H2 infoText = new H2("Empty");
    Button deleteBtn = new Button("Veranstaltung endgültig löschen");
    Button cancelBtn = new Button("Abbrechen");
    Paragraph warningText = new Paragraph("Empty");
    Paragraph noReturn = new Paragraph("Empty");

    public VeranstaltungLoeschenDialog() {
        warningText.addClassName("warning-text-delete");
        warningText.getStyle().set("white-space", "pre-line");
        noReturn.addClassName("no-return-text-delete");
        noReturn.getStyle().set("white-space", "pre-line");

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        deleteBtn.addClickListener(event -> {
            //TODO: implement delete functionality
            Notification.show("Delete will happen here");
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
