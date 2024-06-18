package com.example.application.views.gruppe;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.views.veranstaltungstermin.VeranstaltungDetailView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class GruppeBearbeitenDialog extends Dialog {
    //Data
    private final Gruppenarbeit gruppenarbeit;
    private final List<Gruppe> gruppen;
    private final Set<Teilnehmer> allTeilnehmer;
    private final List<Teilnehmer> gruppenarbeitTeilnehmer;
    private List<Teilnehmer> otherTeilnehmer;
    private List<Grid<Teilnehmer>> gruppenGrids = new ArrayList<>();
    ArrayList<GridListDataView<Teilnehmer>> dataViews = new ArrayList<>();
    private final AuthenticatedUser authenticatedUser;
    private List<H5> titles = new ArrayList<>();
    private VeranstaltungDetailView veranstaltungDetailView;

    //UI Elements
    private final Button cancelBtn = new Button("Abbrechen");
    private final Button saveBtn = new Button("Speichern");
    private final Button addNewGroupBtn = new Button("Eine neue Gruppe hinzufügen");
    private final Div groupsArea = new Div();
    private Grid<Teilnehmer> uebrigeTeilnehmer;

    //Services
    private final GruppenarbeitService gruppenarbeitService;
    private final GruppeService gruppeService;

    //Test
    private Teilnehmer draggedItem;

    public GruppeBearbeitenDialog(Gruppenarbeit gruppenarbeit, GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, AuthenticatedUser authenticatedUser, VeranstaltungDetailView veranstaltungDetailView) {
        this.gruppenarbeit = gruppenarbeit;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;
        this.authenticatedUser = authenticatedUser;
        this.veranstaltungDetailView = veranstaltungDetailView;
        this.gruppen = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId()).getGruppen();
        this.allTeilnehmer = gruppenarbeit.getVeranstaltungstermin().getVeranstaltung().getTeilnehmer();
        this.gruppenarbeitTeilnehmer = gruppenarbeit.getTeilnehmer();
        this.otherTeilnehmer = new ArrayList<Teilnehmer>(allTeilnehmer);
        otherTeilnehmer.removeAll(gruppenarbeitTeilnehmer);

        uebrigeTeilnehmer = new Grid<>(Teilnehmer.class, false);
        uebrigeTeilnehmer.addColumn(Teilnehmer::getId).setHeader("Matrikelnr");
        uebrigeTeilnehmer.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
        uebrigeTeilnehmer.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
        uebrigeTeilnehmer.setRowsDraggable(true);
        dataViews.add(uebrigeTeilnehmer.setItems(otherTeilnehmer));
        gruppenGrids.add(uebrigeTeilnehmer);

        configureGroupsArea();
        groupGrids(gruppen.size(), gruppen);

        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButtonFunctionalities();

        add(createLayout());
    }

    private void addButtonFunctionalities(){
        saveBtn.addClickListener(event ->{
            saveUpdatesToGruppenarbeit();

            if (gruppenarbeit.getVeranstaltungstermin() != null) {
                veranstaltungDetailView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());
                veranstaltungDetailView.setAktiveKachelGruppenarbeit(gruppenarbeit);
            }
            veranstaltungDetailView.update();
            close();
        });

        cancelBtn.addClickListener(event -> {
            close();
        });

        addNewGroupBtn.addClickListener(event -> {
            dataViews.subList(1, dataViews.size()).clear();
            gruppenGrids.subList(1, gruppenGrids.size()).clear();
            titles.clear();
            int newGroupNumber = gruppen.size() + 1;
            groupsArea.removeAll();
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                Gruppe neueGruppe = new Gruppe((long) newGroupNumber, user);
                gruppeService.save(neueGruppe);
                gruppen.add(neueGruppe);
                groupGrids(gruppen.size(), gruppen);
            }
            else {
                Notification.show("Fehler");
            }

        });
    }

    @Transactional
    protected void saveUpdatesToGruppenarbeit() {
        for(Teilnehmer teilnehmer:allTeilnehmer){
            teilnehmer.removeGruppenarbeit(gruppenarbeit);
        }
        for(Gruppe gruppe:gruppen){
            gruppe.removeAllTeilnehmer();
            gruppe.addAllTeilnehmer(dataViews.get(gruppen.indexOf(gruppe)+1).getItems().toList());
            gruppe.setGruppenarbeit(gruppenarbeit);
            gruppeService.save(gruppe);
        }
        gruppenarbeit.removeAllTeilnehmer();
        for(Gruppe gruppe:gruppen){
            for(Teilnehmer teilnehmer:gruppe.getTeilnehmer()){
                gruppenarbeit.addTeilnehmer(teilnehmer);
                teilnehmer.addGruppenarbeit(gruppenarbeit);
            }
        }

        gruppenarbeit.removeAllGruppen();
        gruppenarbeit.addAllGruppen(gruppen);
        gruppenarbeitService.save(gruppenarbeit);
    }

    private void groupGrids(int numberOfGroups, List<Gruppe> gruppen) {
//        uebrigeTeilnehmer = new Grid<>(Teilnehmer.class, false);
//        uebrigeTeilnehmer.addColumn(Teilnehmer::getId).setHeader("Matrikelnr");
//        uebrigeTeilnehmer.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
//        uebrigeTeilnehmer.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
//        uebrigeTeilnehmer.setRowsDraggable(true);
//        dataViews.add(uebrigeTeilnehmer.setItems(otherTeilnehmer));
//        gruppenGrids.add(uebrigeTeilnehmer);

        for(int i = 0; i< numberOfGroups; i++){
            Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class, false);
            grid.addColumn(Teilnehmer::getId).setHeader("Matrikelnr");
            grid.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
            grid.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
            grid.setWidth("400px");
            grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
            grid.setRowsDraggable(true);
            gruppenGrids.add(grid);

            GridListDataView<Teilnehmer> dataView = grid.setItems(gruppen.get(i).getTeilnehmer());
            dataViews.add(dataView);

            H5 title = new H5("Gruppe " + (i+1) + ": " + gruppen.get(i).getTeilnehmer().size() + " Teilnehmer");
            titles.add(title);
            title.addClassName("gruppen-gruppenarbeit-title");

            Button deleteBtn = new Button("Entfernen");
            Button addBtn = new Button("Hinzufügen");
            HorizontalLayout buttonLayout = new HorizontalLayout(deleteBtn, addBtn);
            Div titleAndGroups = new Div(title, buttonLayout, grid);
            titleAndGroups.addClassName("gruppen-gruppenarbeit");
            groupsArea.add(titleAndGroups);
        }
        for(Grid<Teilnehmer> grid:gruppenGrids){
            grid.addDragStartListener(e -> {
                int num = gruppenGrids.indexOf(grid);
                draggedItem = e.getDraggedItems().getFirst();
                grid.setDropMode(GridDropMode.ON_GRID);
                ArrayList<Grid<Teilnehmer>> otherGrids = new ArrayList<>(gruppenGrids);
                otherGrids.remove(grid);
                for(Grid<Teilnehmer> otherGrid:otherGrids){
                    otherGrid.setDropMode(GridDropMode.ON_GRID);
                }
                gruppenGrids.set(num, grid);
//                if(num!=0) {
//                    titles.get(num - 1).setText("Gruppe " + (num) + ": " + dataViews.get(num).getItems().toList().size() + " Teilnehmer");
//                }

            });
            grid.addDropListener(e -> {
                int num = gruppenGrids.indexOf(grid);
                for(GridListDataView<Teilnehmer> dataView: dataViews){
                    if(dataView.contains(draggedItem)){
                        dataView.removeItem(draggedItem);
                    }
                }
                dataViews.get(num).addItem(draggedItem);
                gruppenGrids.set(num, grid);
                if(num!=0&&num!=-1) {
                    titles.get(num - 1).setText("Gruppe " + (num) + ": " + dataViews.get(num).getItems().toList().size() + " Teilnehmer");
                }
            });
            grid.addDragEndListener(e -> {
                draggedItem = null;
                grid.setDropMode(null);
                int num = gruppenGrids.indexOf(grid);
                if(num!=0&&num!=-1) {
                    titles.get(num - 1).setText("Gruppe " + (num) + ": " + dataViews.get(num).getItems().toList().size() + " Teilnehmer");
                }
            });
        }
    }

    private VerticalLayout createLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        setHeaderTitle("Gruppen bearbeiten");

        getFooter().add(addNewGroupBtn);
        getFooter().add(cancelBtn);
        getFooter().add(saveBtn);

        mainLayout.add(uebrigeTeilnehmer, groupsArea);

        return mainLayout;
    }

    private void configureGroupsArea() {
        groupsArea.setWidth("100%");
        groupsArea.setClassName("gruppen-container-gruppenarbeiten");
    }
}
