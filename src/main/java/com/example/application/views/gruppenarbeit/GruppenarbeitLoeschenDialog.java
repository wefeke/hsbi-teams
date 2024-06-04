//Lilli
package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppenarbeit;
import com.example.application.services.GruppenarbeitService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

public class GruppenarbeitLoeschenDialog extends Dialog {
    //Data
    private Gruppenarbeit gruppenarbeit;

    //Services
    private GruppenarbeitService gruppenarbeitService;

    //UI-Elemente
    Button deleteBtn = new Button("Gruppenarbeit löschen");

    public GruppenarbeitLoeschenDialog(GruppenarbeitService gruppenarbeitService) {
        this.gruppenarbeitService = gruppenarbeitService;

        add(deleteBtn);

    }

}
