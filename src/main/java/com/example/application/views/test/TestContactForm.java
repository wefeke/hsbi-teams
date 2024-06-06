package com.example.application.views.test;

import com.example.application.models.*;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route(value = "testContact")
public class TestContactForm extends FormLayout{
    TextField titelVeranstaltung = new TextField("titelVeranstaltung");
    //TextField veranstaltungstermine = new TextField("Test ID");
    //TextField teilnehmer = new TextField("Test Name");

    ComboBox<Auswertung> auswertung = new ComboBox<>("Auswertung");


    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public TestContactForm(List<Auswertung> auswertungen) {
        addClassName("test-contact-form");

        auswertung.setItems(auswertungen);
        auswertung.setItemLabelGenerator(Auswertung::getVeranstaltung);

        add(
                titelVeranstaltung,
                createButtonsLayout()
        );
    }


    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, delete, close);
    }

}
