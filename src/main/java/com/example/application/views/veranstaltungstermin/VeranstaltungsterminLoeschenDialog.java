package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.services.VeranstaltungsterminService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class VeranstaltungsterminLoeschenDialog extends Dialog {
    //Data
    private final Veranstaltung veranstaltung;
    private Veranstaltungstermin veranstaltungstermin;
    private VeranstaltungDetailView veranstaltungDetailView;
    private Veranstaltungstermin aktiverVeranstaltungstermin;
    private Gruppenarbeit aktiveGruppenarbeit;

    //Services
    private GruppeService gruppeService;
    private GruppenarbeitService gruppenarbeitService;
    private VeranstaltungsterminService veranstaltungsterminService;
    private VeranstaltungenService veranstaltungenService;

    //UI-Elements
    H2 infoText = new H2("Empty");
    Button deleteBtn = new Button("Veranstaltungstermin endgültig löschen");
    Button cancelBtn = new Button("Abbrechen");
    Paragraph warningText = new Paragraph("Empty");
    Paragraph noReturn = new Paragraph("Empty");

    public VeranstaltungsterminLoeschenDialog(Veranstaltung veranstaltung, GruppeService gruppeService, GruppenarbeitService gruppenarbeitService, VeranstaltungDetailView veranstaltungDetailView, Veranstaltungstermin aktiverVeranstaltungstermin, Gruppenarbeit aktiveGruppenarbeit, VeranstaltungsterminService veranstaltungsterminService, VeranstaltungenService veranstaltungenService) {
        this.veranstaltungDetailView = veranstaltungDetailView;
        this.aktiverVeranstaltungstermin = aktiverVeranstaltungstermin;
        this.aktiveGruppenarbeit = aktiveGruppenarbeit;
        this.veranstaltungstermin=null;
        this.veranstaltung = veranstaltung;
        this.gruppeService = gruppeService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.veranstaltungenService = veranstaltungenService;

        warningText.addClassName("warning-text-delete");
        warningText.getStyle().set("white-space", "pre-line");
        noReturn.addClassName("no-return-text-delete");
        noReturn.getStyle().set("white-space", "pre-line");

        deleteBtn.addClickListener(event -> {
            List<Gruppenarbeit> gruppenarbeiten = veranstaltungstermin.getGruppenarbeiten();
            veranstaltungstermin.removeAllGruppenarbeiten();
            veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

            for(Gruppenarbeit gruppenarbeit: gruppenarbeiten){
                List<Gruppe> gruppen = gruppenarbeit.getGruppen();
                gruppenarbeit.removeAllGruppen();
                gruppenarbeitService.save(gruppenarbeit);

                for (Gruppe gruppe : gruppen) {
                    gruppeService.deleteGruppe(gruppe);
                }

                this.veranstaltungstermin.removeGruppenarbeit(gruppenarbeit);
                veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

                gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
            }

            veranstaltungsterminService.deleteVeranstaltungstermin(veranstaltungstermin);

            veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(aktiverVeranstaltungstermin);
            veranstaltungDetailView.setAktiveKachelGruppenarbeit(aktiveGruppenarbeit);
            veranstaltungDetailView.update();

            close();
        });
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelBtn.addClickListener(event -> close());

        add(createLayout());

    }
    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermin = veranstaltungstermin;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        infoText.setText("Veranstaltungstermin " + this.veranstaltungstermin.getTitel() + " am " +
                this.veranstaltungstermin.getDatum().format(dateFormatter) + " löschen");
        int anzGruppen = 0;
        for(Gruppenarbeit gruppenarbeit: this.veranstaltungstermin.getGruppenarbeiten()){
            anzGruppen+=gruppenarbeit.getGruppen().size();
        }
        warningText.setText("Wenn du den Veranstaltungstermin " +
                this.veranstaltungstermin.getTitel() + " am " +
                this.veranstaltungstermin.getDatum().format(dateFormatter) +
                " löscht,\n werden " + "auch alle zugehörigen Gruppenarbeiten (Anzahl: " +
                this.veranstaltungstermin.getGruppenarbeiten().size() + ") \nund die zu den Gruppenarbeiten" +
                " gehörenden Gruppen (Gesamtzahl: " + anzGruppen + ") gelöscht.");
        noReturn.setText("Bist du sicher, dass du den Veranstaltungstermin " + this.veranstaltungstermin.getTitel() +
                " am " + this.veranstaltungstermin.getDatum().format(dateFormatter) + " löschen willst?\n" +
                "Das kann nicht rückgängig gemacht werden!");
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
