//Author: Lilli

package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppenarbeit;
import com.example.application.services.GruppenarbeitService;
import com.example.application.views.veranstaltungstermin.VeranstaltungDetailView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class GruppenarbeitBearbeitenDialog extends Dialog {
    //Services
    private final GruppenarbeitService gruppenarbeitService;

    //Binder
    Binder<Gruppenarbeit> binder = new Binder<>(Gruppenarbeit.class);

    //Data
    private Gruppenarbeit gruppenarbeit;
    private VeranstaltungDetailView veranstaltungDetailView;

    //UI Elements
    H3 infoText = new H3();
    TextField titleField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");
    Button saveBtn = new Button("Änderungen speichern");
    Button cancelBtn = new Button("Abbrechen");


    public GruppenarbeitBearbeitenDialog(GruppenarbeitService gruppenarbeitService, VeranstaltungDetailView veranstaltungDetailView) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.veranstaltungDetailView = veranstaltungDetailView;
        this.gruppenarbeit = null;

        bindFields();
        configureButtonFunctionalities();
        styling();
        add(createLayout());
    }

    //Zum Felder binden
    private void bindFields(){
        binder.forField(titleField)
                .asRequired("Titel muss gefüllt sein")
                .bind(Gruppenarbeit::getTitel, Gruppenarbeit::setTitel);
        binder.forField(descriptionArea)
                .bind(Gruppenarbeit::getBeschreibung, Gruppenarbeit::setBeschreibung);
    }

    //Erstellt das Layout des Fensters
    private VerticalLayout createLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(infoText, titleField, descriptionArea);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(cancelBtn);
        getFooter().add(saveBtn);
        return mainLayout;
    }

    //Speichert die Änderungen, sofern der Titel nicht leer ist
    private void saveChanges(){
        if(binder.writeBeanIfValid(gruppenarbeit)){
            gruppenarbeitService.save(gruppenarbeit);
        }
    }

    //Fügt dem Speicher- bzw. Abbrechen-Button die notwendige Funktionalität hinzu
    private void configureButtonFunctionalities(){
        saveBtn.addClickListener(event -> {
            saveChanges();

            if (gruppenarbeit.getVeranstaltungstermin() != null) {
                veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());

                if (gruppenarbeit != null) {
                    veranstaltungDetailView.setAktiveKachelGruppenarbeit(gruppenarbeit);
                }
            }
            veranstaltungDetailView.update();

            close();
        });
        cancelBtn.addClickListener(event -> {
            close();
        });
    }

    //Passt optische Aspekte an
    private void styling(){
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        titleField.setWidth("300px");
        descriptionArea.setWidth("300px");

        descriptionArea.setHeight("300px");
    }

    //Liest die vorhandenen Daten der Gruppenarbeit aus der Datenbank und passt die Überschrift an
    public void readBean() {
        binder.readBean(gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId()));
        infoText.setText("Gruppenarbeit \"" + this.gruppenarbeit.getTitel() + "\" bearbeiten");
    }

    //Ermöglicht es, die Gruppenarbeit in der VeranstaltungDetailView an geeigneter Stelle zu übergeben
    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

}