package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppenarbeit;
import com.example.application.services.GruppenarbeitService;
import com.vaadin.flow.component.dialog.Dialog;
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

    //UI Elements
    TextField titleField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");


    public GruppenarbeitBearbeitenDialog(GruppenarbeitService gruppenarbeitService) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeit = null;
        add(createLayout());
        bindFields();
    }

    private void bindFields(){
        binder.forField(titleField)
                .asRequired("Titel muss gefüllt sein")
                .bind(Gruppenarbeit::getTitel, Gruppenarbeit::setTitel);
        binder.forField(descriptionArea)
                .bind(Gruppenarbeit::getBeschreibung, Gruppenarbeit::setBeschreibung);
    }

    private VerticalLayout createLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(titleField, descriptionArea);
        return mainLayout;
    }

    public void readBean() {
        binder.readBean(gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId()));
    }

    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }

}
