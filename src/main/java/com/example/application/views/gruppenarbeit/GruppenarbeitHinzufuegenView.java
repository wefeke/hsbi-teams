package com.example.application.views.gruppenarbeit;

import com.example.application.services.GruppenarbeitService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Gruppenarbeiten")
@Route(value = "gruppenarbeiten", layout = MainLayout.class)
public class GruppenarbeitHinzufuegenView extends VerticalLayout {
    private final GruppenarbeitService gruppenarbeitService;

    TextField titelField = new TextField("Titel");
    TextArea descriptionArea = new TextArea("Beschreibung");
    H2 infoText = new H2("Gruppenarbeit anlegen");



    @Autowired
    public GruppenarbeitHinzufuegenView(GruppenarbeitService gruppenarbeitService) {
        this.gruppenarbeitService = gruppenarbeitService;
        descriptionArea.setWidth("80%");
        descriptionArea.setHeight("150px");
        titelField.setWidth("80%");
        add(infoText, titelField, descriptionArea);

    }

}
