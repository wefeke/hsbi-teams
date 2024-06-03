package com.example.application.views.studierende;

import com.example.application.models.Teilnehmer;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@RolesAllowed({"ADMIN"})
public class Aufraeumen extends Dialog{
    private final TeilnehmerService teilnehmerService;
    private final Button deleteButton= new Button("löschen");
    private final Button closeButton= new Button("schließen");
    public Aufraeumen(TeilnehmerService teilnehmerService){
        this.teilnehmerService=teilnehmerService;


        closeButton.addClickListener(event->
                close());

        List<Teilnehmer>studierendeVorVierJahren=teilnehmerService.findStudierendeVorVierJahren();
        if(studierendeVorVierJahren.isEmpty()){
            this.setWidth("20vw");
            this.setHeight("20vh");
            add(
                    new Text("Es gibt keine Studierenden,die vor mehr als 4 Jahren erstellt wurden."),
                    closeButton
            );
        }else{
            this.setWidth("80vw");
            this.setHeight("80vh");
            Grid<Teilnehmer>grid= new Grid<>(Teilnehmer.class);
            grid.setItems(studierendeVorVierJahren);
            grid.setColumns("vorname","nachname","id");
            add(
                    grid,
                    closeButton
            );

        }

    }



}
