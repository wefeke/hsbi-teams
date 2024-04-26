package com.example.application.views.studierende;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("studierende")
@PageTitle("Studierende")
public class StudierendeView extends VerticalLayout {

    public StudierendeView() {
        add(new Text("Willkommen auf der Seite für Studierende!"));
    }
}