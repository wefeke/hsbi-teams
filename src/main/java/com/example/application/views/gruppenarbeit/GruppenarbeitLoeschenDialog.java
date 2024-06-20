//Lilli
package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.VeranstaltungsterminService;
import com.example.application.views.veranstaltungstermin.VeranstaltungsterminView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class GruppenarbeitLoeschenDialog extends Dialog {
    //Data
    private Gruppenarbeit gruppenarbeit;
    private Veranstaltungstermin veranstaltungstermin;
    private VeranstaltungsterminService veranstaltungsterminService;
    private VeranstaltungsterminView veranstaltungsterminView;
    private Gruppenarbeit aktiveGruppenarbeit;

    //Services
    private GruppenarbeitService gruppenarbeitService;
    private GruppeService gruppeService;

    //UI-Elemente
    H2 infoText = new H2("Empty");
    Paragraph warningText = new Paragraph("Empty");
    Paragraph noReturn = new Paragraph("Das kann nicht rückgängig gemacht werden!");
    Button deleteBtn = new Button("Gruppenarbeit endgültig löschen");
    Button cancelBtn = new Button("Abbrechen");

    public GruppenarbeitLoeschenDialog(GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungsterminService veranstaltungsterminService, VeranstaltungsterminView veranstaltungsdetailView, Gruppenarbeit aktiveGruppenarbeit) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.veranstaltungsterminView = veranstaltungsdetailView;
        this.aktiveGruppenarbeit = aktiveGruppenarbeit;
        this.gruppenarbeit = null;
        this.gruppeService = gruppeService;
        this.veranstaltungstermin = null;
        this.veranstaltungsterminService = veranstaltungsterminService;

        warningText.addClassName("warning-text-delete");
        warningText.getStyle().set("white-space", "pre-line");
        noReturn.addClassName("no-return-text-delete");
        noReturn.getStyle().set("white-space", "pre-line");

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        deleteBtn.addClickListener(event -> {
            List<Gruppe> gruppen = gruppenarbeit.getGruppen();
            gruppenarbeit.removeAllGruppen();
            gruppenarbeitService.save(gruppenarbeit);

            for (Gruppe gruppe : gruppen) {
                gruppeService.deleteGruppe(gruppe);
            }
            this.veranstaltungstermin.removeGruppenarbeit(gruppenarbeit);
            veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

            if (veranstaltungstermin != null) {
                veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(veranstaltungstermin);

                if (aktiveGruppenarbeit != gruppenarbeit && aktiveGruppenarbeit != null) {
                    veranstaltungsterminView.setAktiveKachelGruppenarbeit(aktiveGruppenarbeit);
                }
            }

            gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);

            close();

            veranstaltungsterminView.update();
        });

        cancelBtn.addClickListener(event -> close());

        add(createLayout());

    }

    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
        infoText.setText("Gruppenarbeit " + this.gruppenarbeit.getTitel() + " löschen");
        warningText.setText("Wenn du die Gruppenarbeit " + this.gruppenarbeit.getTitel() + " löscht,\n werden " +
                "auch alle zugehörigen Gruppen (Anzahl: " + this.gruppenarbeit.getGruppen().size() + ") gelöscht.");

        noReturn.setText("Bist du sicher, dass du die Gruppenarbeit " + this.gruppenarbeit.getTitel() + " löschen " +
                "willst?\nDas kann nicht rückgängig gemacht werden!");
    }

    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermin = veranstaltungstermin;

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
