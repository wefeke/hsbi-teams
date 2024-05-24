package com.example.application.views.gruppenarbeit;

import com.example.application.models.Teilnehmer;
import com.example.application.models.TeilnehmerGruppenarbeitId;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@Route(value = "gruppeauswertungsdialog")

public class GruppeAuswertungDialog extends Dialog {
    //Services


    // Dialog Elemente
    private final NumberField auswertungsWert = new NumberField();

    private final Button incrementButton = new Button("+");
    private final Button decrementButton = new Button("-");
    //Dialog Items
    private final Button cancelButton= new Button("Cancel");
    private final Button saveButton= new Button("Save");

    Binder<TeilnehmerGruppenarbeitId> binder = new Binder<>(TeilnehmerGruppenarbeitId.class);


    public GruppeAuswertungDialog() {
        add(createLayout());
        configureElements();
    }


    private VerticalLayout createLayout() {
        setHeaderTitle("Auswertung vornehmen");
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

        cancelButton.addClickListener( e -> {
            close();
            clearFields();
        });
    }






    public void clearFields(){
        //Clear all Fields after saving
        auswertungsWert.clear();

    }

}
