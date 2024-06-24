package com.example.application.views.gruppenarbeit;

import com.example.application.models.*;
import com.example.application.services.GruppenarbeitTeilnehmerService;
import com.example.application.views.auswertung.Auswertung;
import com.example.application.views.veranstaltungstermin.VeranstaltungDetailView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

// LEON
@Route(value = "gruppeauswertungsdialog")
public class GruppeAuswertungDialog extends Dialog {
    // Entity-Felder
    private final Teilnehmer teilnehmer;
    @NotNull
    private final Gruppenarbeit gruppenarbeit;
    private GruppenarbeitTeilnehmer gruppenarbeitTeilnehmer;
    private GruppenarbeitTeilnehmerId gruppenarbeitTeilnehmerId;
    private Optional<GruppenarbeitTeilnehmer> gruppenarbeitTeilnehmerResult;

    // Dialog Elemente
    private final NumberField auswertungsWert = new NumberField();
    private final Button incrementButton = new Button("+");
    private final Button decrementButton = new Button("-");
    private final Button cancelButton= new Button("Abbrechen");
    private final Button saveButton= new Button("Speichern");

    Binder<GruppenarbeitTeilnehmer> binder = new Binder<>(GruppenarbeitTeilnehmer.class);

    // Service
    private final GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    // View zum Zurücknavigieren
    private VeranstaltungDetailView veranstaltungDetailView;

    /**
     * Konstruktor für den GruppeAuswertungDialog.
     * Initialisiert die benötigten Felder und Services, erstellt das Layout und konfiguriert die Elemente.
     *
     * @param teilnehmer der Teilnehmer
     * @param gruppenarbeit die Gruppenarbeit
     * @param gruppenarbeitTeilnehmerService der Service für GruppenarbeitTeilnehmer
     * @param veranstaltungDetailView die Detailansicht der Veranstaltung
     *
     * @autor Leon
     */
    public GruppeAuswertungDialog(Teilnehmer teilnehmer, Gruppenarbeit gruppenarbeit, GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService, VeranstaltungDetailView veranstaltungDetailView) {
        this.teilnehmer = teilnehmer;
        this.gruppenarbeit = gruppenarbeit;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.veranstaltungDetailView = veranstaltungDetailView;
        this.gruppenarbeitTeilnehmer = new GruppenarbeitTeilnehmer();
        gruppenarbeitTeilnehmer.setId(new GruppenarbeitTeilnehmerId(teilnehmer.getId(), gruppenarbeit.getId()));
        gruppenarbeitTeilnehmerResult = gruppenarbeitTeilnehmerService.findByID(gruppenarbeitTeilnehmer.getId());
        add(createLayout());
        configureElements();
        bindFields();
    }

    /**
     * Erstellt das Layout für den Dialog.
     * Ordnet die Elemente an und fügt eine dynamische Überschrift hinzu.
     *
     * @return das erstellte VerticalLayout
     *
     * @autor Leon
     */
    private VerticalLayout createLayout() {
        setHeaderTitle("Punkte für " + teilnehmer.getVorname() + " " + teilnehmer.getNachname());
        getHeader().add(incrementButton);
        getHeader().add(decrementButton);

        HorizontalLayout horizontalLayoutSaveCancel = new HorizontalLayout(cancelButton, saveButton);
        horizontalLayoutSaveCancel.setAlignItems(FlexComponent.Alignment.CENTER);

        getFooter().add(horizontalLayoutSaveCancel);

        HorizontalLayout horizontalLayout = new HorizontalLayout(incrementButton, decrementButton);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout(auswertungsWert, horizontalLayout);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        return verticalLayout;
    }

    /**
     * Erhöht den Punkte-Wert um eine Hälfte.
     * Diese Methode berücksichtigt auch halbe Punkte.
     *
     * @autor Leon
     */
    private void incrementValueByHalf() {
        if (auswertungsWert.getValue() != null) {
            Double value = auswertungsWert.getValue();
            value += 0.5;
            auswertungsWert.setValue(value);
        } else {
            auswertungsWert.setValue(0.0);
        }

        gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());
    }

    /**
     * Verringert den Punkte-Wert um eine Hälfte.
     * Diese Methode berücksichtigt auch halbe Punkte.
     *
     * @autor Leon
     */
    private void decrementValueByHalf() {
        if (auswertungsWert.getValue() != null && auswertungsWert.getValue() >= 0.5) {
            Double value = auswertungsWert.getValue();
            value -= 0.5;
            auswertungsWert.setValue(value);
        } else {
            auswertungsWert.setValue(0.0);
        }
        gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());
    }

    /**
     * Konfiguriert die Dialog-Elemente und ihre Aktionen.
     * Setzt Standardwerte und definiert die Aktionen für die Buttons.
     *
     * @autor Leon
     */
    private void configureElements() {
        if (gruppenarbeitTeilnehmerResult.isPresent()) {
            try {
                auswertungsWert.setValue(gruppenarbeitTeilnehmerResult.get().getPunkteD());
            } catch (NullPointerException e) {
                auswertungsWert.setValue(0.0);
            }
        } else {
            auswertungsWert.setValue(0.0);
        }

        incrementButton.addClickListener(event -> incrementValueByHalf());
        decrementButton.addClickListener(event -> decrementValueByHalf());

        // Save Button Implementation
        saveButton.addClickListener(event -> {
            gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());
            if (binder.writeBeanIfValid(gruppenarbeitTeilnehmer)) {
                gruppenarbeitTeilnehmerService.save(gruppenarbeitTeilnehmer);
                close();
                clearFields();
            }

            if (gruppenarbeit.getVeranstaltungstermin() != null) {
                veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());
            }

            veranstaltungDetailView.setAktiveKachelGruppenarbeit(gruppenarbeit);
            veranstaltungDetailView.update();
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Cancel Button Implementation
        cancelButton.addClickListener(e -> {
            close();
            clearFields();
        });
    }

    /**
     * Bindet die Felder des Dialogs an die Daten des GruppenarbeitTeilnehmers.
     * Definiert Validierungsregeln für die Eingabefelder.
     *
     * @autor Leon
     */
    private void bindFields() {
        binder.forField(auswertungsWert)
                .asRequired()
                .withValidator(punkte -> punkte >= 0,
                        "Wert darf nicht negativ sein!")
                .bind(GruppenarbeitTeilnehmer::getPunkteD, GruppenarbeitTeilnehmer::setPunkteD);
    }

    /**
     * Löscht die Felder des Dialogs.
     * Wird nach dem Speichern oder Abbrechen aufgerufen.
     *
     * @autor Leon
     */
    public void clearFields() {
        // Clear all fields after saving
        auswertungsWert.clear();
    }
}
