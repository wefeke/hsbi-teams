package com.example.application.views.gruppenarbeit;

import com.example.application.models.*;
import com.example.application.services.GruppenarbeitTeilnehmerService;
import com.example.application.views.auswertung.Auswertung;
import com.example.application.views.veranstaltungstermin.VeranstaltungDetailView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import java.util.Optional;

// LEON
@Route(value = "gruppeauswertungsdialog")
public class GruppeAuswertungDialog extends Dialog {
    // Entity-Felder
    private final Teilnehmer teilnehmer;
    private final Gruppenarbeit gruppenarbeit;
    private GruppenarbeitTeilnehmer gruppenarbeitTeilnehmer;
    private GruppenarbeitTeilnehmerId gruppenarbeitTeilnehmerId;
    private Optional<GruppenarbeitTeilnehmer> gruppenarbeitTeilnehmerResult;

    // Dialog Elemente
    private final NumberField auswertungsWert = new NumberField();
    private final Button incrementButton = new Button("+");
    private final Button decrementButton = new Button("-");
    private final Button cancelButton= new Button("Cancel");
    private final Button saveButton= new Button("Save");

    Binder<Auswertung> binder = new Binder<>(Auswertung.class);

    // Service
    private final GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    // View zum zurücknavigieren
    private VeranstaltungDetailView veranstaltungDetailView;


    public GruppeAuswertungDialog(Teilnehmer teilnehmer, Gruppenarbeit gruppenarbeit, GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService, VeranstaltungDetailView veranstaltungDetailView) {
        this.teilnehmer = teilnehmer;
        this.gruppenarbeit = gruppenarbeit;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.gruppenarbeitTeilnehmer = new GruppenarbeitTeilnehmer();
        this.veranstaltungDetailView = veranstaltungDetailView;
        gruppenarbeitTeilnehmer.setId(new GruppenarbeitTeilnehmerId(teilnehmer.getId(),gruppenarbeit.getId()));
        gruppenarbeitTeilnehmerResult = gruppenarbeitTeilnehmerService.findByID(gruppenarbeitTeilnehmer.getId()
        );
        add(createLayout());
        configureElements();
    }


    // Bei der Konfiguration des Layouts werden die Elemente angeordnet und eine dynamische Überschrift hinzugefügt
    private VerticalLayout createLayout() {
        setHeaderTitle("Punkte für " + teilnehmer.getVorname() + " " + teilnehmer.getNachname());
        getHeader().add(incrementButton);
        getHeader().add(decrementButton);
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        setWidth("70vh");
        return (
                new VerticalLayout(
                        auswertungsWert,
                        new HorizontalLayout(incrementButton,decrementButton)
                ));
    }

    // Methode zu Erhöhren des Punkte-Wertes um eine Hälfte, da es auch halbe Punkte gibt
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

    // Methode zu Verringern des Punkte-Wertes um eine Hälfte, da es auch halbe Punkte gibt
    private void decrementValueByHalf() {
        if (auswertungsWert.getValue() != null) {
            Double value = auswertungsWert.getValue();
            value -= 0.5;
            auswertungsWert.setValue(value);
        } else {
            auswertungsWert.setValue(0.0);
        }
        gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());

    }

    // Standard werden Werte von 1.0 genutzt für Punkte.
    private void configureElements(){
        if (gruppenarbeitTeilnehmerResult.isPresent()) {
            auswertungsWert.setValue(1.0);
        } else {
            auswertungsWert.setValue(0.0);
        }

        incrementButton.addClickListener(event -> incrementValueByHalf());
        decrementButton.addClickListener(event -> decrementValueByHalf());
        //Save Button Implementation
        saveButton.addClickListener( event -> {
                gruppenarbeitTeilnehmer.setPunkte(auswertungsWert.getValue().floatValue());
                gruppenarbeitTeilnehmerService.save(gruppenarbeitTeilnehmer);
                close();
                clearFields();

            if (gruppenarbeit.getVeranstaltungstermin() != null) {
                veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());
                veranstaltungDetailView.setAktiveKachelGruppenarbeit(gruppenarbeit);
            }
                veranstaltungDetailView.update();
        });


        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Cancel Button Implementation
        cancelButton.addClickListener( e ->{
            close();
            clearFields();
        });

    }

    public void clearFields(){
        //Clear all Fields after saving
        auswertungsWert.clear();
    }
}
