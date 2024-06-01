package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppenarbeit;
import com.example.application.services.GruppenarbeitService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.data.binder.Binder;

public class GruppenarbeitBearbeitenDialog extends Dialog {
    //Services
    private final GruppenarbeitService gruppenarbeitService;

    //Binder
    Binder<Gruppenarbeit> binder = new Binder<>(Gruppenarbeit.class);

    //Data
    private Gruppenarbeit gruppenarbeit;

    public GruppenarbeitBearbeitenDialog(GruppenarbeitService gruppenarbeitService) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeit = null;


    }

    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

}
