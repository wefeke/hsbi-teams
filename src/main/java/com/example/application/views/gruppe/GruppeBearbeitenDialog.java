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
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private List<Button> deleteButtons = new ArrayList<>();

    //UI Elements
    private final Button cancelBtn = new Button("Abbrechen");
    private final Button saveBtn = new Button("Speichern");
    private final Button addNewGroupBtn = new Button("Eine neue Gruppe hinzufügen");
    private final Button mixBtn = new Button("Neu mischen");
    private final Div groupsArea = new Div();
    private Grid<Teilnehmer> uebrigeTeilnehmer;
    private final Select<String> groupSize = new Select<>();

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

        deleteBtnsFunctionality();

        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButtonFunctionalities();

        add(createLayout());
    }

    private void deleteBtnsFunctionality() {
        for(Button btn: deleteButtons) {
            btn.addClickListener(event -> {
                int gruppenNr = deleteButtons.indexOf(btn);
                deleteButtons.clear();
                if(dataViews.get(gruppenNr+1) != null) {
                    otherTeilnehmer.addAll(dataViews.get(gruppenNr + 1).getItems().toList());
                    dataViews.getFirst().addItems(dataViews.get(gruppenNr + 1).getItems().toList());
                }
                gruppeService.deleteGruppe(gruppen.get(gruppenNr));

                gruppen.remove(gruppenNr);
                dataViews.subList(1, dataViews.size()).clear();
                gruppenGrids.subList(1, gruppenGrids.size()).clear();
                titles.clear();
                groupsArea.removeAll();
                groupGrids(gruppen.size(), gruppen);
                deleteBtnsFunctionality();
            });
        }
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
            deleteButtons.clear();
            int newGroupNumber = gruppen.size() + 1;
            groupsArea.removeAll();
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                Gruppe neueGruppe = new Gruppe((long) newGroupNumber, user);
                gruppeService.save(neueGruppe);
                gruppen.add(neueGruppe);
                groupGrids(gruppen.size(), gruppen);
                deleteBtnsFunctionality();
            }
            else {
                Notification.show("Fehler");
            }
        });

        mixBtn.addClickListener(event -> {
            List<Teilnehmer> participantsToMix = new ArrayList<>();
            for(GridListDataView<Teilnehmer> dataView: dataViews.subList(1, dataViews.size())){
                participantsToMix.addAll(dataView.getItems().toList());
            }

            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Gruppenanzahl auswählen");
            confirmDialog.setText("Möchtest du eine neue Gruppenanzahl wählen oder in den vorhandenen Gruppen mischen?");
            confirmDialog.setConfirmText("Gruppen neu bestimmen");
            confirmDialog.addConfirmListener(e -> {
//                dataViews.subList(1, dataViews.size()).clear();
//                gruppenGrids.subList(1, gruppenGrids.size()).clear();
//                titles.clear();
//                deleteButtons.clear();
//                groupsArea.removeAll();
//                gruppen.clear();
                Dialog chooseNewGroups = new Dialog();
                chooseNewGroups.setHeaderTitle("Neue Gruppen wählen");
                Button confirmBtn = new Button("Okay");
                Button cancelBtnDialog = new Button("Abbrechen");

                confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                chooseNewGroups.getFooter().add(cancelBtnDialog);
                chooseNewGroups.getFooter().add(confirmBtn);

                groupSize.setItems(getGroups(participantsToMix));
                groupSize.setLabel("Gruppenanzahl und -größe wählen");
                groupSize.setWidth("230px");
                chooseNewGroups.add(groupSize);


                cancelBtnDialog.addClickListener(cancel -> {
                    chooseNewGroups.close();
                });

                confirmBtn.addClickListener(confirm -> {
//                    dataViews.subList(1, dataViews.size()).clear();
//                    gruppenGrids.subList(1, gruppenGrids.size()).clear();
//                    titles.clear();
//                    deleteButtons.clear();
//                    groupsArea.removeAll();
//                    gruppen.clear();

                    if(groupSize.getOptionalValue().isEmpty()){
                        Notification.show("Bitte wähle eine Gruppenverteilung aus");
                    }
                    else if(Objects.equals(groupSize.getValue(), "Keine Teilnehmer ausgewählt.")){
                        Notification.show("Es gibt keine Teilnehmer, die an der Gruppenarbeit teilnehmen. " +
                                "So kann keine neue Verteilung generiert werden.");
                        chooseNewGroups.close();
                    }
                    else{
                        for(Gruppe gruppe: gruppen){
                            gruppeService.deleteGruppe(gruppe);
                        }
                        dataViews.subList(1, dataViews.size()).clear();
                        gruppenGrids.subList(1, gruppenGrids.size()).clear();
                        titles.clear();
                        deleteButtons.clear();
                        groupsArea.removeAll();
                        gruppen.clear();

                        int groupNumber = getNumberOfGroups();
                        makeGroups(groupNumber, gruppen);
                        int[] sizes = groupSizes(groupNumber, participantsToMix.size());
                        randomizeParticipants(sizes, groupNumber, gruppen, participantsToMix);
                        groupGrids(gruppen.size(), gruppen);

                        chooseNewGroups.close();

                    }
                        });

                chooseNewGroups.open();
            });
            confirmDialog.open();
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
            gruppe.setNummer((long) gruppen.indexOf(gruppe)+1);
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

            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteButtons.add(deleteBtn);

            HorizontalLayout topLayout = new HorizontalLayout(title, deleteBtn);
            topLayout.setWidthFull();
            topLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            topLayout.setFlexGrow(1, title);

            Div titleAndGroups = new Div(topLayout, grid);
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
        getFooter().add(mixBtn);
        getFooter().add(cancelBtn);
        getFooter().add(saveBtn);

        mainLayout.add(uebrigeTeilnehmer, groupsArea);

        return mainLayout;
    }

    private void configureGroupsArea() {
        groupsArea.setWidth("100%");
        groupsArea.setClassName("gruppen-container-gruppenarbeiten");
    }

    //Für die Select-Box der Gruppengrößen
    private List<String> getGroups(List<Teilnehmer> participants) {
        List<String> groups = new ArrayList<>();
        if(participants.isEmpty()){
            groups.add("Keine Teilnehmer ausgewählt.");
            return groups;
        }
        groups = groupNumbersAndSizes(participants.size());
        return groups;
    }

    //Berechnet die maximale Anzahl an Gruppen bei gegebener Teilnehmergröße
    private int groupMax(int participants){
        return participants/2;
    }

    //Berechnet alle möglichen Gruppenanzahlen
    //Das sind dann eine mit allen Teilnehmern, 2 bis zur maximalen Anzahl und dann noch eine Gruppengröße, um alle
    //Teilnehmer in eine eigene Gruppe zu packen
    private int[] groupNumbers(int participants){
        int groupMax = groupMax(participants);
        int[] groupNumbers = new int[groupMax+1];
        //Gruppen von 1 bis zum Maximum
        for(int i=0; i<groupMax; i++){
            groupNumbers[i] = i+1;
        }
        //Gruppenanzahl in der Anzahl der Teilnehmer, s.d. man jeden Teilnehmer in eigene Gruppe packen kann
        groupNumbers[groupMax] = participants;
        return groupNumbers;
    }

    //Berechnet die Gruppengröße(n) bei gegebener Gruppen- und Gesamtteilnehmeranzahl
    private int[] groupSizes(int groups, int participants){
        if(participants%groups == 0){
            return new int[]{participants/groups};
        }
        else{
            return new int[]{participants/groups, participants/groups+1};
        }
    }

    //Gibt alle möglichen Gruppengrößen und zugehörige Teilnehmeranzahlen als Strings in einer Liste zurück
    private List<String> groupNumbersAndSizes(int participants){
        List<String> groupStrings = new ArrayList<String>();
        for(int i:groupNumbers(participants))
        {
            String str = "";
            str += i;
            str += " x ";

            for(Iterator<Integer> it = Arrays.stream(groupSizes(i, participants)).iterator(); it.hasNext();){
                String nextSize = it.next().toString();
                if(it.hasNext()){
                    str += nextSize;
                    str += " und ";
                }
                else{
                    str += nextSize;
                    str += " Teilnehmer";
                }
            }
            groupStrings.add(str);
        }
        return groupStrings;
    }

    private int getNumberOfGroups() {
        String num = groupSize.getValue();
        String[] splitString = num.split(" ");
        return Integer.parseInt(splitString[0]);
    }

    private void makeGroups(int numberOfGroups, List<Gruppe> gruppen) {
        for(int i = 0; i< numberOfGroups; i++){
            gruppen.add(new Gruppe((long) i+1));
        }
    }

    private void randomizeParticipants(int[] sizes, int numberOfGroups, List<Gruppe> gruppen, List<Teilnehmer> participants) {
        Collections.shuffle(participants);
        Iterator<Teilnehmer> teilnehmerIterator = participants.iterator();

        for(int j = 0; j< sizes[sizes.length-1]; j++) {
            for (int i = 0; i < numberOfGroups; i++) {
                if(teilnehmerIterator.hasNext()){
                    gruppen.get(i).addTeilnehmer(teilnehmerIterator.next());
                }
                else{
                    break;
                }
            }
        }
    }
}
