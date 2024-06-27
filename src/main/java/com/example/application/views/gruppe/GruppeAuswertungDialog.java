package com.example.application.views.gruppe;

import com.example.application.models.*;
import com.example.application.services.GruppenarbeitTeilnehmerService;
import com.example.application.views.veranstaltungstermin.VeranstaltungsterminView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import java.util.Optional;

/**
 * Dialog zur Bewertung von Teilnehmern in einer Gruppenarbeit.
 * Diese Klasse ermöglicht das Hinzufügen, Bearbeiten und Speichern von Bewertungspunkten
 * für Teilnehmer in einer Gruppenarbeit.
 *
 * @autHor Leon
 */
@Route(value = "gruppeauswertungsdialog")
public class GruppeAuswertungDialog extends Dialog {
    // Entity-Felder
    private final Teilnehmer teilnehmer;
    private final Gruppenarbeit gruppenarbeit;
    private final GruppenarbeitTeilnehmer gruppenarbeitTeilnehmer;
    private final Optional<GruppenarbeitTeilnehmer> gruppenarbeitTeilnehmerResult;

    // Dialog Elemente
    private final NumberField auswertungsWert = new NumberField();
    private final Button incrementButton = new Button("+");
    private final Button decrementButton = new Button("-");
    private final Button cancelButton= new Button("Abbrechen");
    private final Button saveButton= new Button("Speichern");

    Binder<GruppenarbeitTeilnehmer> binder = new Binder<>(GruppenarbeitTeilnehmer.class);

    // Service
    private final GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    // View zum zurücknavigieren
    private final VeranstaltungsterminView veranstaltungsterminView;

    /**
     * Konstruktor für GruppeAuswertungDialog.
     * Initialisiert die notwendigen Felder und konfiguriert die Dialogelemente.
     *
     * @param teilnehmer der Teilnehmer, der bewertet wird
     * @param gruppenarbeit die Gruppenarbeit, die bewertet wird
     * @param gruppenarbeitTeilnehmerService der Service zur Verwaltung der GruppenarbeitTeilnehmer-Daten
     * @param veranstaltungsterminView die View zur Darstellung der Veranstaltungstermine
     */
    public GruppeAuswertungDialog(Teilnehmer teilnehmer,
                                  Gruppenarbeit gruppenarbeit,
                                  GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService,
                                  VeranstaltungsterminView veranstaltungsterminView) { // Er soll auch nicht autowiren, da ein
        // Teilnehmer und eine Gruppenarbeit übergeben werdeb
        this.teilnehmer = teilnehmer;
        this.gruppenarbeit = gruppenarbeit;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.gruppenarbeitTeilnehmer = new GruppenarbeitTeilnehmer();
        this.veranstaltungsterminView = veranstaltungsterminView;
        gruppenarbeitTeilnehmer.setId(new GruppenarbeitTeilnehmerId(teilnehmer.getId(),gruppenarbeit.getId()));
        gruppenarbeitTeilnehmerResult = gruppenarbeitTeilnehmerService.findByID(gruppenarbeitTeilnehmer.getId()
        );
        add(createLayout());
        configureElements();
        bindFields();
    }

    /**
     * Erstellt das Layout des Dialogs.
     * Ordnet die Dialogelemente an und fügt sie dem Layout hinzu.
     *
     * @return das erstellte Layout als VerticalLayout
     */
    private VerticalLayout createLayout() {
        setHeaderTitle("Punkte für " + teilnehmer.getVorname()
                + " " + teilnehmer.getNachname());

        HorizontalLayout buttonsLayoutFooter = new HorizontalLayout(cancelButton, saveButton);
        buttonsLayoutFooter.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(buttonsLayoutFooter);

        HorizontalLayout buttonsLayoutMain = new HorizontalLayout(incrementButton, decrementButton);
        buttonsLayoutMain.setAlignItems(FlexComponent.Alignment.CENTER);

        return new VerticalLayout(auswertungsWert, buttonsLayoutMain);
    }

    /**
     * Bindet die Felder an den Binder.
     * Validiert die Eingabewerte und bindet sie an die entsprechenden Felder der Entität.
     */
    private void bindFields() {
        binder.forField(auswertungsWert)
                .withValidator(auswertungsWert -> auswertungsWert != null && auswertungsWert >= 0.0, "Der Wert darf nicht leer oder negativ sein")
                .withValidator(auswertungsWert -> auswertungsWert % 0.5 == 0, "Der Werte darf nur Ganze und Halbe Punkte enthalten")
                .bind(GruppenarbeitTeilnehmer::getPunkteD, GruppenarbeitTeilnehmer::setPunkteD);
    }

    /**
     * Erhöht den Punktewert um eine Hälfte.
     * Da es auch halbe Punkte gibt, wird der Wert um 0.5 erhöht.
     */
    private void incrementValueByHalf() {
        if (auswertungsWert.getValue() != null && auswertungsWert.getValue() >= 0.0) {
            Double value = auswertungsWert.getValue();
            value += 0.5;
            auswertungsWert.setValue(value);
        } else {
            auswertungsWert.setValue(0.0);
        }
        gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());
    }

    /**
     * Verringert den Punktewert um eine Hälfte.
     * Da es auch halbe Punkte gibt, wird der Wert um 0.5 verringert.
     */
    private void decrementValueByHalf() {
        if (auswertungsWert.getValue() != null && auswertungsWert.getValue() >= 0.0) {
            Double value = auswertungsWert.getValue();
            value -= 0.5;
            auswertungsWert.setValue(value);
        } else {
            auswertungsWert.setValue(0.0);
        }
        gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());
    }

    /**
     * Konfiguriert die Dialogelemente.
     * Setzt die Standardwerte, fügt Event-Listener hinzu und definiert das Verhalten der Elemente.
     */
    private void configureElements(){
        if (gruppenarbeitTeilnehmerResult.isPresent() && gruppenarbeitTeilnehmerResult.get().getPunkte() != null ) {
            auswertungsWert.setValue(Double.valueOf(gruppenarbeitTeilnehmerResult.get().getPunkte()));
        } else {
            auswertungsWert.setValue(0.0);
        }

        incrementButton.addClickListener(event -> incrementValueByHalf());
        decrementButton.addClickListener(event -> decrementValueByHalf());

        // Implementierung des Speichern-Buttons
        saveButton.addClickListener(event -> {

            if (binder.writeBeanIfValid(gruppenarbeitTeilnehmer)){
                if (auswertungsWert.getValue() != null) {
                    gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());
                    gruppenarbeitTeilnehmerService.save(gruppenarbeitTeilnehmer);
                    close();
                    clearFields();
                }
                if (gruppenarbeit.getVeranstaltungstermin() != null) {
                    veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());
                }
                veranstaltungsterminView.setAktiveKachelGruppenarbeit(gruppenarbeit);
                veranstaltungsterminView.update();
            }



        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Implementierung des Abbrechen-Buttons
        cancelButton.addClickListener(e -> {
            close();
            clearFields();
        });
    }

    /**
     * Löscht alle Felder nach dem Speichern.
     * Setzt die Eingabefelder auf ihre Standardwerte zurück.
     */
    public void clearFields(){
        auswertungsWert.clear();
    }
}
