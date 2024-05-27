package com.example.application.views.gruppenarbeit;

import com.example.application.models.Teilnehmer;
import com.example.application.models.TeilnehmerGruppenarbeitId;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route(value = "gruppeauswertungsdialog")
public class GruppeAuswertungDialog extends Dialog {

    private Teilnehmer teilnehmer;

    // Dialog Elemente
    private final NumberField auswertungsWert = new NumberField();

    private final Button incrementButton = new Button("+");
    private final Button decrementButton = new Button("-");
    //Dialog Items
    private final Button cancelButton= new Button("Cancel");
    private final Button saveButton= new Button("Save");

    Binder<TeilnehmerGruppenarbeitId> binder = new Binder<>(TeilnehmerGruppenarbeitId.class);


    public GruppeAuswertungDialog(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;
        add(createLayout());
        configureElements();
    }


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

    private void configureElements(){



        //Footer Button Implementation
        saveButton.addClickListener( event -> {




                close();
                clearFields();
                UI.getCurrent().getPage().reload();


        });

        incrementButton.addClickListener( event -> {





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
