package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.VeranstaltungsterminService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("SpringTransactionalMethodCallsInspection")
public class VeranstaltungsterminLoeschenDialog extends Dialog {
    //Data
    private Veranstaltungstermin veranstaltungstermin;

    //UI-Elements
    private final H2 infoText = new H2("Empty");
    private final Button deleteBtn = new Button("Veranstaltungstermin endgültig löschen");
    private final Button cancelBtn = new Button("Abbrechen");
    private final Paragraph warningText = new Paragraph("Empty");
    private final Paragraph noReturn = new Paragraph("Empty");

    public VeranstaltungsterminLoeschenDialog(GruppeService gruppeService, GruppenarbeitService gruppenarbeitService, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin aktiverVeranstaltungstermin, Gruppenarbeit aktiveGruppenarbeit, VeranstaltungsterminService veranstaltungsterminService) {
        this.veranstaltungstermin = null;
        styleElements();
        addButtonsFunctionalities(gruppeService, gruppenarbeitService, veranstaltungsterminView, aktiverVeranstaltungstermin, aktiveGruppenarbeit, veranstaltungsterminService);
        add(createLayout());
    }

    private void addButtonsFunctionalities(GruppeService gruppeService, GruppenarbeitService gruppenarbeitService, VeranstaltungsterminView veranstaltungsterminView, Veranstaltungstermin aktiverVeranstaltungstermin, Gruppenarbeit aktiveGruppenarbeit, VeranstaltungsterminService veranstaltungsterminService) {
        deleteBtn.addClickListener(event -> {
            if (aktiverVeranstaltungstermin != null) {
                veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(aktiverVeranstaltungstermin);

                if (aktiveGruppenarbeit != null) {
                    veranstaltungsterminView.setAktiveKachelGruppenarbeit(aktiveGruppenarbeit);
                }
            }
            deleteEverything(gruppeService, gruppenarbeitService, veranstaltungsterminService);
            veranstaltungsterminView.update();
            close();
        });

        cancelBtn.addClickListener(event -> close());
    }

    private void styleElements() {
        warningText.addClassName("warning-text-delete");
        warningText.getStyle().set("white-space", "pre-line");
        noReturn.addClassName("no-return-text-delete");
        noReturn.getStyle().set("white-space", "pre-line");

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    @Transactional
    protected void deleteEverything(GruppeService gruppeService, GruppenarbeitService gruppenarbeitService, VeranstaltungsterminService veranstaltungsterminService) {
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
