//Lilli
package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.services.VeranstaltungsterminService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;

import java.util.ArrayList;
import java.util.List;

public class GruppenarbeitLoeschenDialog extends Dialog {
    //Data
    private Gruppenarbeit gruppenarbeit;
    private Veranstaltungstermin veranstaltungstermin;
    private VeranstaltungsterminService veranstaltungsterminService;

    //Services
    private GruppenarbeitService gruppenarbeitService;
    private GruppeService gruppeService;

    //UI-Elemente
    Button deleteBtn = new Button("Gruppenarbeit löschen");

    public GruppenarbeitLoeschenDialog(GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, VeranstaltungsterminService veranstaltungsterminService) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeit = null;
        this.gruppeService = gruppeService;
        this.veranstaltungstermin = null;
        this.veranstaltungsterminService = veranstaltungsterminService;

        deleteBtn.addClickListener(event -> {
            List<Gruppe> gruppen = gruppenarbeit.getGruppen();
            gruppenarbeit.removeAllGruppen();
            gruppenarbeitService.save(gruppenarbeit);

            for (Gruppe gruppe : gruppen) {
                gruppeService.deleteGruppe(gruppe);
            }
            this.veranstaltungstermin.removeGruppenarbeit(gruppenarbeit);
            veranstaltungsterminService.saveVeranstaltungstermin(veranstaltungstermin);

            gruppenarbeitService.deleteGruppenarbeit(gruppenarbeit);
        });

        add(deleteBtn);

    }

    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

    public void setVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        this.veranstaltungstermin = veranstaltungstermin;
    }

}
