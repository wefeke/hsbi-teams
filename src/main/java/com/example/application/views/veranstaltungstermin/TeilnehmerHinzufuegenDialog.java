package com.example.application.views.veranstaltungstermin;

import com.example.application.services.TeilnehmerService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

public class TeilnehmerHinzufuegenDialog extends Dialog {

    private final VeranstaltungenService veranstaltungService;
    private final TeilnehmerService teilnehmerService;
    private final Long veranstaltungId;

    public TeilnehmerHinzufuegenDialog(VeranstaltungenService veranstaltungService, TeilnehmerService teilnehmerService, Long veranstaltungId) {
        this.veranstaltungService = veranstaltungService;
        this.teilnehmerService = teilnehmerService;
        this.veranstaltungId = veranstaltungId;

        // Erstellen Sie den Text für den Dialog
        Text dialogTitle = new Text("Teilnehmer zur Veranstaltung " + veranstaltungId + " hinzufügen");
        add(dialogTitle);

        // Erstellen Sie den Abbrechen-Button
        Button cancelButton = new Button("Abbrechen", e -> close());
        add(cancelButton);
    }
}
