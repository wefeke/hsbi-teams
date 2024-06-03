package com.example.application.views.gruppenarbeit;

import com.example.application.models.*;
import com.example.application.repositories.TeilnehmerGruppenarbeitRepository;
import com.example.application.services.TeilnehmerGruppenarbeitService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@Route(value = "gruppeauswertungsdialog")
public class GruppeAuswertungDialog extends Dialog {

    private final Teilnehmer teilnehmer;
    private final Gruppenarbeit gruppenarbeit;

    private TeilnehmerGruppenarbeit teilnehmerGruppenarbeit;
    private TeilnehmerGruppenarbeitId teilnehmerGruppenarbeitId;
    private Optional<TeilnehmerGruppenarbeit> teilnehmerGruppenarbeitResult;

    // Dialog Elemente
    private final NumberField auswertungsWert = new NumberField();

    private final Button incrementButton = new Button("+");
    private final Button decrementButton = new Button("-");
    //Dialog Items
    private final Button cancelButton= new Button("Cancel");
    private final Button saveButton= new Button("Save");

    Binder<Auswertung> binder = new Binder<>(Auswertung.class);

    TeilnehmerGruppenarbeitService teilnehmerGruppenarbeitService;


    public GruppeAuswertungDialog(Teilnehmer teilnehmer, Gruppenarbeit gruppenarbeit, TeilnehmerGruppenarbeitService teilnehmerGruppenarbeitService) {
        this.teilnehmer = teilnehmer;
        this.gruppenarbeit = gruppenarbeit;
        this.teilnehmerGruppenarbeitService = teilnehmerGruppenarbeitService;
        this.teilnehmerGruppenarbeit = new TeilnehmerGruppenarbeit();
        teilnehmerGruppenarbeit.setId(new TeilnehmerGruppenarbeitId(teilnehmer.getId(),gruppenarbeit.getId()));
        teilnehmerGruppenarbeitResult = teilnehmerGruppenarbeitService.findByID(teilnehmerGruppenarbeit.getId()
        );
       /* teilnehmerGruppenarbeit = teilnehmerGruppenarbeitService.findTeilnehmerGruppenarbeitByWithTeilnehmerAndGruppe(teilnehmer.getId(),gruppenarbeit.getId());
        if (teilnehmerGruppenarbeit != null) {
            auswertungsWert.setValue(teilnehmerGruppenarbeit.getPunkte().doubleValue());
        }*/
        add(createLayout());
        configureElements();
    }



    private VerticalLayout createLayout() {
        setHeaderTitle("Punkte für " + teilnehmer.getVorname() + " " + teilnehmer.getNachname() + " in " + gruppenarbeit.getTitel() + " " + teilnehmer.getId() + " " + gruppenarbeit.getId());
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

    private void incrementValueByHalf() {
        if (auswertungsWert.getValue() != null) {
            Double value = auswertungsWert.getValue();
            value += 0.5;
            auswertungsWert.setValue(value);
        } else {
            auswertungsWert.setValue(0.0);
        }

        teilnehmerGruppenarbeit.getId().setPunkte(auswertungsWert.getValue().floatValue());
    }

    private void decrementValueByHalf() {
        if (auswertungsWert.getValue() != null) {
            Double value = auswertungsWert.getValue();
            value -= 0.5;
            auswertungsWert.setValue(value);
        } else {
            auswertungsWert.setValue(0.0);
        }
        teilnehmerGruppenarbeit.getId().setPunkte(auswertungsWert.getValue().floatValue());

    }

    private void configureElements(){
        if (teilnehmerGruppenarbeitResult.isPresent()) {
            auswertungsWert.setValue(1.0);
        } else {
            auswertungsWert.setValue(0.0);
        }

        incrementButton.addClickListener(event -> incrementValueByHalf());
        decrementButton.addClickListener(event -> decrementValueByHalf());
        //Footer Button Implementation



        saveButton.addClickListener( event -> {
            if (teilnehmerGruppenarbeitService.exists(teilnehmerGruppenarbeit)) {
                teilnehmerGruppenarbeitService.update(teilnehmerGruppenarbeit);
            } else {
                teilnehmerGruppenarbeitService.save(teilnehmerGruppenarbeit);
            }
                close();
                clearFields();

                UI.getCurrent().getPage().reload();
        });


        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

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
