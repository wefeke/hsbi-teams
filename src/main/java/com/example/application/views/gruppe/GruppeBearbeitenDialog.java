package com.example.application.views.gruppe;

import com.example.application.models.Gruppe;
import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.GruppeService;
import com.example.application.services.GruppenarbeitService;
import com.example.application.views.veranstaltungstermin.VeranstaltungsterminView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;

/**
 * Ein Dialog zur Bearbeitung der Gruppen einer Gruppenarbeit.
 * <p>
 * Diese Klasse bietet eine Benutzeroberfläche, um Gruppen in einer Gruppenarbeit zu bearbeiten. Benutzer können
 * Gruppen erstellen, löschen, Teilnehmer zu Gruppen hinzufügen oder entfernen und die Gruppen neu mischen. Der
 * Dialog integriert verschiedene Services und UI-Elemente zur Verwaltung und Anzeige der Gruppen und Teilnehmer.
 * <p>
 * Hauptfunktionen:
 * - Erstellen und Löschen von Gruppen
 * - Hinzufügen und Entfernen von Teilnehmern zu und aus Gruppen
 * - Speichern und Abbrechen von Änderungen
 * - Neu Mischen von Teilnehmern in Gruppen
 * - Hinzufügen aller Teilnehmer zu einer bestimmten Gruppe
 * <p>
 * Die Klasse verwendet verschiedene Vaadin-Komponenten wie Grid, Button, Dialog und Select, um die Benutzeroberfläche
 * zu erstellen und die Interaktionen zu ermöglichen.
 * <p>
 * Transaktionen werden durch die Verwendung von @Transactional verwaltet, um sicherzustellen, dass Datenbankopera-
 * tionen atomar durchgeführt werden.
 *
 * @see Gruppenarbeit
 * @see Gruppe
 * @see Teilnehmer
 * @see GruppenarbeitService
 * @see GruppeService
 * @see AuthenticatedUser
 * @see VeranstaltungsterminView
 *
 * @author Lilli
 */
@SuppressWarnings("SpringTransactionalMethodCallsInspection")
public class GruppeBearbeitenDialog extends Dialog {

    //Data
    private final Gruppenarbeit gruppenarbeit;
    private List<Gruppe> gruppen;
    private Set<Teilnehmer> allTeilnehmer;
    private List<Teilnehmer> gruppenarbeitTeilnehmer;
    private List<Teilnehmer> otherTeilnehmer;
    private final List<Grid<Teilnehmer>> gruppenGrids = new ArrayList<>();
    private final ArrayList<GridListDataView<Teilnehmer>> dataViews = new ArrayList<>();
    private final AuthenticatedUser authenticatedUser;
    private final List<H5> titles = new ArrayList<>();
    private final VeranstaltungsterminView veranstaltungsterminView;
    private final List<Button> deleteButtons = new ArrayList<>();
    private final List<Gruppe> groupsToDelete = new ArrayList<>();
    private Teilnehmer draggedItem;

    //UI Elements
    private final Button cancelBtn = new Button("Abbrechen");
    private final Button saveBtn = new Button("Speichern");
    private final Button addNewGroupBtn = new Button("Eine neue Gruppe hinzufügen");
    private final Button mixBtn = new Button("Neu mischen");
    private final Button addAllToGroupBtn = new Button("Alle Veranstaltungsteilnehmer zu Gruppe 1 hinzufügen");
    private final Div groupsArea = new Div();
    private Grid<Teilnehmer> uebrigeTeilnehmer;
    private final Select<String> groupSize = new Select<>();

    //Services
    private final GruppenarbeitService gruppenarbeitService;
    private final GruppeService gruppeService;

    /**
     * Konstruktor für den GruppeBearbeitenDialog.
     * <p>
     * Dieser Konstruktor initialisiert den Dialog zur Bearbeitung der Gruppen einer Gruppenarbeit.
     * Er lädt die relevanten Daten, konfiguriert die Benutzeroberfläche und stellt die notwendigen Event-Listener
     * bereit, um Benutzerinteraktionen zu handhaben.
     *
     * @param gruppenarbeit Die Gruppenarbeit, deren Gruppen bearbeitet werden sollen.
     * @param gruppenarbeitService Service für die Verwaltung von Gruppenarbeiten.
     * @param gruppeService Service für die Verwaltung von Gruppen.
     * @param authenticatedUser Der authentifizierte Benutzer, der den Dialog verwendet.
     * @param veranstaltungsterminView Die Ansicht des Veranstaltungstermins, zu dem die Gruppenarbeit gehört.
     *
     * @author Lilli
     */
    public GruppeBearbeitenDialog(Gruppenarbeit gruppenarbeit, GruppenarbeitService gruppenarbeitService, GruppeService gruppeService, AuthenticatedUser authenticatedUser, VeranstaltungsterminView veranstaltungsterminView) {
        //Initialisierung der Daten
        this.gruppenarbeit = gruppenarbeit;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;
        this.authenticatedUser = authenticatedUser;
        this.veranstaltungsterminView = veranstaltungsterminView;
        this.gruppen = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId()).getGruppen();
        this.allTeilnehmer = gruppenarbeit.getVeranstaltungstermin().getVeranstaltung().getTeilnehmer();
        this.gruppenarbeitTeilnehmer = gruppenarbeit.getTeilnehmer();
        this.otherTeilnehmer = new ArrayList<>(allTeilnehmer);
        otherTeilnehmer.removeAll(gruppenarbeitTeilnehmer);

        //UI-Element-Konfigurationen
        configureUebrigeTeilnehmerGrid();
        configureGroupsArea();
        groupGrids(gruppen.size(), gruppen);

        //Funktionen zu Buttons hinzufügen
        deleteBtnsFunctionality();
        addButtonFunctionalities();

        //Layout erstellen
        add(createLayout());
    }

    /**
     * Konfiguriert das Grid für die übrigen Teilnehmer, die nicht in einer Gruppe, aber der Veranstaltung sind.
     * Fügt Spalten für Matrikelnummer, Vorname und Nachname hinzu und ermöglicht Drag and Drop von Teilnehmern.
     * Die Teilnehmerdaten werden aus der Liste {@code otherTeilnehmer} geladen und in {@code uebrigeTeilnehmer}
     * angezeigt.
     *
     * @author Lilli
     */
    private void configureUebrigeTeilnehmerGrid() {
        uebrigeTeilnehmer = new Grid<>(Teilnehmer.class, false);
        uebrigeTeilnehmer.addColumn(Teilnehmer::getId).setHeader("Matrikelnr");
        uebrigeTeilnehmer.addColumn(Teilnehmer::getVorname).setHeader("Vorname");
        uebrigeTeilnehmer.addColumn(Teilnehmer::getNachname).setHeader("Nachname");
        uebrigeTeilnehmer.setRowsDraggable(true);
        dataViews.add(uebrigeTeilnehmer.setItems(otherTeilnehmer));
        gruppenGrids.add(uebrigeTeilnehmer);
    }

    /**
     * Fügt die Löschfunktionalitäten für die Buttons in der Liste {@code deleteButtons} hinzu.
     * Jeder Button wird konfiguriert, um die entsprechende Gruppe zu löschen und die UI entsprechend zu aktualisieren.
     *
     * @author Lilli
     */
    private void deleteBtnsFunctionality() {
        for(Button btn: deleteButtons) {
            btn.addClickListener(event -> {
                int gruppenNr = deleteButtons.indexOf(btn);
                deleteButtons.clear();
                if(dataViews.get(gruppenNr+1) != null) {
                    otherTeilnehmer.addAll(dataViews.get(gruppenNr + 1).getItems().toList());
                    dataViews.getFirst().addItems(dataViews.get(gruppenNr + 1).getItems().toList());
                }
                groupsToDelete.add((gruppen.get(gruppenNr)));

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

    /**
     * Fügt den Buttons Funktionalitäten hinzu, wenn sie geklickt werden.
     * - Der {@code saveBtn} öffnet einen Bestätigungsdialog zum Speichern der Änderungen.
     * - Der {@code cancelBtn} ruft die Methode {@code cancelBtnFunctionality} auf.
     * - Der {@code addNewGroupBtn} fügt eine neue Gruppe hinzu und aktualisiert die UI entsprechend.
     * - Der {@code mixBtn} zeigt einen Dialog an, in dem der User die Form des Mischens wählen kann.
     * - Der {@code addAllToGroupBtn} fügt alle übrigen Teilnehmer zur ersten Gruppe hinzu.
     *
     * @author Lilli
     */
    private void addButtonFunctionalities(){
        saveBtn.addClickListener(event ->{
            ConfirmDialog confirmDialog = getConfirmDialogSave();
            confirmDialog.open();
        });

        cancelBtn.addClickListener(event -> cancelBtnFunctionality());

        addNewGroupBtn.addClickListener(event -> addNewGroupBtnFunctionality());

        mixBtn.addClickListener(event -> {
            List<Teilnehmer> participantsToMix = new ArrayList<>();
            for(GridListDataView<Teilnehmer> dataView: dataViews.subList(1, dataViews.size())){
                participantsToMix.addAll(dataView.getItems().toList());
            }

            confirmDialogMixBtn(participantsToMix);
        });

        addAllToGroupBtn.addClickListener(event -> {
            if(dataViews.size()>1){
                dataViews.get(1).addItems(dataViews.get(0).getItems().toList());
                dataViews.get(0).removeItems(dataViews.get(1).getItems().toList());
                titles.getFirst().setText("Gruppe 1: " + dataViews.get(1).getItems().toList().size() + " Teilnehmer");
            }
            else {
                Notification.show("Erst eine neue Gruppe hinzufügen!");
            }
        });
    }

    /**
     * Zeigt Auswahldialog zum Mischen der Teilnehmer an, in dem der User wählen kann, ob in den bereits vorhandenen
     * Gruppen gemischt werden soll oder ob neue Gruppen erstellt werden sollen.
     * Entscheidet der User sich für neue Gruppen, öffnet sich ein Dialog zum Auswählen einer neuen Gruppenanzahl.
     *
     * @param participantsToMix Die Liste der Teilnehmer, die gemischt werden sollen.
     * @author Lilli
     */
    private void confirmDialogMixBtn(List<Teilnehmer> participantsToMix) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Gruppenanzahl auswählen");
        Paragraph question = new Paragraph();
        question.setText("Möchtest du eine neue Gruppenanzahl wählen oder in den vorhandenen Gruppen mischen?");
        confirmDialog.add(question);
        question.addClassName("warning-text-delete");
        question.getStyle().set("white-space", "pre-line");

        confirmDialog.setConfirmText("Gruppen neu bestimmen");
        confirmDialog.addConfirmListener(e -> {
            Dialog chooseNewGroups = getChooseNewGroupsDialog(participantsToMix);
            chooseNewGroups.open();
        });
        confirmDialog.setCancelable(true);

        confirmDialog.setCancelButton(new Button("Gruppen beibehalten"));
        confirmDialog.addCancelListener(cancel -> {
            int groupNumber = gruppen.size();
            for(Gruppe gruppe: gruppen){
                gruppeService.deleteGruppe(gruppe);
            }
            dataViews.subList(1, dataViews.size()).clear();
            gruppenGrids.subList(1, gruppenGrids.size()).clear();
            titles.clear();
            deleteButtons.clear();
            groupsArea.removeAll();
            gruppen.clear();

            makeGroups(groupNumber, gruppen);
            int[] sizes = groupSizes(groupNumber, participantsToMix.size());
            randomizeParticipants(sizes, groupNumber, gruppen, participantsToMix);
            groupGrids(gruppen.size(), gruppen);
            deleteBtnsFunctionality();
        });
        confirmDialog.open();
    }

    /**
     * Erzeugt einen Dialog zum Auswählen einer neuen Gruppenanzahl und -größe basierend auf den Teilnehmern.
     * Zeigt eine Bestätigungs- und Abbruchschaltfläche an.
     *
     * @param participantsToMix Die Liste der Teilnehmer, die zur Auswahl der neuen Gruppen verwendet werden.
     * @return Der erzeugte Dialog zum Auswählen einer neuen Gruppenanzahl und -größe.
     *
     * @author Lilli
     */
    private Dialog getChooseNewGroupsDialog(List<Teilnehmer> participantsToMix) {
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


        cancelBtnDialog.addClickListener(cancel -> chooseNewGroups.close());

        confirmBtn.addClickListener(confirm -> {
            if(groupSize.getOptionalValue().isEmpty()){
                Notification.show("Bitte wähle eine Gruppenverteilung aus");
            }
            else if(Objects.equals(groupSize.getValue(), "Keine Teilnehmer ausgewählt.")){
                Notification.show("Es gibt keine Teilnehmer, die an der Gruppenarbeit teilnehmen. " +
                        "So kann keine neue Verteilung generiert werden.");
                chooseNewGroups.close();
            }
            else{
                groupsToDelete.addAll(gruppen);
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
                deleteBtnsFunctionality();

                chooseNewGroups.close();

            }
                });
        return chooseNewGroups;
    }

    /**
     * Fügt eine neue Gruppe hinzu, wenn der entsprechende Button geklickt wird.
     * Aktualisiert dabei die UI für alle existierenden Gruppen, Titel und Löschtasten und fügt eine neue Gruppe hinzu.
     * Wenn der aktuell angemeldete Benutzer vorhanden ist, wird die neue Gruppe mit diesem Benutzer verknüpft.
     * Zeigt eine Benachrichtigung an, wenn ein Fehler auftritt und der Benutzer nicht vorhanden ist.
     *
     * @author Lilli
     */
    private void addNewGroupBtnFunctionality() {
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
    }

    /**
     * Setzt die bearbeiteten Gruppen zurück auf den ursprünglichen Zustand der Gruppenarbeit
     * und aktualisiert die Benutzeroberfläche entsprechend.
     * <p>
     * Diese Methode wird aufgerufen, wenn der Abbrechen-Button geklickt wird.
     *
     * @author Lilli
     */
    private void cancelBtnFunctionality() {
        gruppen.clear();
        gruppen.addAll(gruppenarbeit.getGruppen());
        dataViews.clear();

        allTeilnehmer = gruppenarbeit.getVeranstaltungstermin().getVeranstaltung().getTeilnehmer();
        gruppenarbeitTeilnehmer = gruppenarbeit.getTeilnehmer();
        otherTeilnehmer = new ArrayList<>(allTeilnehmer);
        otherTeilnehmer.removeAll(gruppenarbeitTeilnehmer);
        dataViews.add(uebrigeTeilnehmer.setItems(otherTeilnehmer));

        gruppenGrids.subList(1, gruppenGrids.size()).clear();
        titles.clear();
        groupsArea.removeAll();
        groupGrids(gruppen.size(), gruppen);
        deleteBtnsFunctionality();
        close();
    }

    /**
     * Erzeugt einen Bestätigungsdialog für das Speichern der aktualisierten Gruppenarbeit.
     * Der Dialog enthält eine Warnung über mögliche Überschreibungen von bereits vergebenen Punkten.
     *
     * @return Der konfigurierte Bestätigungsdialog für das Speichern.
     *
     * @author Lilli
     */
    private ConfirmDialog getConfirmDialogSave() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Updates zur Gruppenarbeit speichern");
        confirmDialog.setConfirmText("Speichern");
        confirmDialog.addConfirmListener(confirm -> {
            saveUpdatesToGruppenarbeit();

            gruppen.clear();
            deleteButtons.clear();
            gruppen.addAll(gruppenarbeit.getGruppen());
            dataViews.subList(1, dataViews.size()).clear();
            gruppenGrids.subList(1, gruppenGrids.size()).clear();
            titles.clear();
            groupsArea.removeAll();
            groupGrids(gruppen.size(), gruppen);
            deleteBtnsFunctionality();

            if (gruppenarbeit.getVeranstaltungstermin() != null) {
                veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());
                veranstaltungsterminView.setAktiveKachelGruppenarbeit(gruppenarbeit);
            }
            veranstaltungsterminView.update();

            close();
        });
        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Abbrechen");
        confirmDialog.addCancelListener(cancel -> confirmDialog.close());

        Paragraph info = new Paragraph("Wenn du die Updates zu den Gruppen speicherst, werden eventuell bereits" +
                " vergebene Punkte überschrieben.");
        info.addClassName("warning-text-delete");
        Paragraph areYouSure = new Paragraph("Bist du sicher, dass du die Updates speichern möchtest?");
        areYouSure.addClassName("no-return-text-delete");
        confirmDialog.add(info, areYouSure);
        return confirmDialog;
    }

    /**
     * Speichert die aktualisierten Daten zur Gruppenarbeit.
     * - Entfernt leere Gruppen und löscht diese aus der Datenbank.
     * - Entfernt die Gruppenarbeit von allen Teilnehmern und fügt ihnen die aktualisierte Version wieder hinzu.
     * - Aktualisiert die Teilnehmerzuordnung zu den Gruppen.
     * - Speichert die aktualisierte Gruppenarbeit und die dazugehörigen Gruppen in der Datenbank.
     * <p>
     * {@code @transactional} Diese Methode wird innerhalb eines transaktionalen Kontexts ausgeführt.
     *
     * @author Lilli
     */
    @Transactional
    protected void saveUpdatesToGruppenarbeit() {
        List<Gruppe> emptyGroups = new ArrayList<>();
        for(Gruppe gruppe: gruppen){
            if(dataViews.get(gruppen.indexOf(gruppe)+1).getItems().toList().isEmpty()){
                emptyGroups.add(gruppe);
                groupsToDelete.add(gruppe);
            }
        }
        gruppen.removeAll(emptyGroups);
        for(Gruppe gruppe: groupsToDelete){
            gruppeService.deleteGruppe(gruppe);
        }
        for(Teilnehmer teilnehmer:allTeilnehmer){
            teilnehmer.removeGruppenarbeit(gruppenarbeit);
        }
        for(Gruppe gruppe:gruppen){
            gruppe.removeAllTeilnehmer();
            gruppe.addAllTeilnehmer(dataViews.get(gruppen.indexOf(gruppe)+1).getItems().toList());
            gruppe.setGruppenarbeit(gruppenarbeit);
            gruppe.setNummer((long) gruppen.indexOf(gruppe)+1);
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                gruppe.setUser(user);
            }
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

    /**
     * Erstellt Grids für jede Gruppe in der Gruppenarbeit.
     * Jedes Grid zeigt die Teilnehmer einer Gruppe an und erlaubt Drag-and-Drop-Operationen.
     * Die Methode fügt auch Drag-and-Drop-Listener hinzu, um Teilnehmer zwischen den Grids zu verschieben.
     *
     * @param numberOfGroups Anzahl der Gruppen
     * @param gruppen        Liste der Gruppen
     *
     * @author Lilli
     */

    private void groupGrids(int numberOfGroups, List<Gruppe> gruppen) {
        for(int i = 0; i< numberOfGroups; i++){
            Grid<Teilnehmer> grid = getTeilnehmerGridAndDataView(gruppen, i);
            createGroupGridsLayout(gruppen, i, grid);
        }
        dragAndDrop();
    }

    /**
     * Fügt Drag-and-Drop-Listener zu allen Grids hinzu, um Teilnehmer zwischen den Gruppen zu verschieben.
     * Jedes Grid wird für Drag-and-Drop-Operationen konfiguriert.
     *
     * @author Lilli
     */
    private void dragAndDrop() {
        for(Grid<Teilnehmer> grid:gruppenGrids){
            grid.addDragStartListener(e -> dragStartListener(grid, e));
            grid.addDropListener(e -> dragDropListener(grid));
            grid.addDragEndListener(e -> dragEndListener(grid));
        }
    }

    /**
     * Beendet den Drag-Vorgang in einem Grid und aktualisiert die Anzeige der Gruppenzusammenstellung.
     *
     * @param grid Das Grid, in dem der Drag-Vorgang beendet wird.
     *
     * @author Lilli
     */
    private void dragEndListener(Grid<Teilnehmer> grid) {
        draggedItem = null;
        grid.setDropMode(null);
        int num = gruppenGrids.indexOf(grid);
        if(num!=0&&num!=-1) {
            titles.get(num - 1).setText("Gruppe " + (num) + ": " + dataViews.get(num).getItems().toList().size() + " Teilnehmer");
        }
    }

    /**
     * Behandelt das Ereignis, wenn ein Teilnehmer von einem Bereich in ein anderes gezogen wird.
     * Der Teilnehmer wird aus der ursprünglichen Datenansicht entfernt und zur Ziel-Grid-Datenansicht hinzugefügt.
     *
     * @param grid Das Grid, in das der Teilnehmer gezogen wird
     *
     * @author Lilli
     */
    private void dragDropListener(Grid<Teilnehmer> grid) {
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
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Drag-Vorgang in einem Grid gestartet wird.
     * Sie initialisiert den Drag-Vorgang und aktualisiert die UI entsprechend.
     *
     * @param grid Das Grid, aus dem der Drag-Vorgang gestartet wurde. Es sollte nicht null sein.
     * @param e Das Event-Objekt, das den Drag-Start ausgelöst hat. Es sollte nicht null sein.
     *
     * @author Lilli
     */
    private void dragStartListener(Grid<Teilnehmer> grid, GridDragStartEvent<Teilnehmer> e) {
        int num = gruppenGrids.indexOf(grid);
        draggedItem = e.getDraggedItems().getFirst();
        grid.setDropMode(GridDropMode.ON_GRID);
        ArrayList<Grid<Teilnehmer>> otherGrids = new ArrayList<>(gruppenGrids);
        otherGrids.remove(grid);
        for(Grid<Teilnehmer> otherGrid:otherGrids){
            otherGrid.setDropMode(GridDropMode.ON_GRID);
        }
        gruppenGrids.set(num, grid);
    }

    /**
     * Erstellt ein Gridlayout für Gruppen basierend auf den übergebenen Gruppen und den Grids.
     * Jede Gruppe wird in einem Grid dargestellt, wobei die Teilnehmer der Gruppen im Grid dargestellt werden.
     * <p>
     * Diese Methode ermöglicht es, eine dynamische Gridansicht von Gruppen und ihren Teilnehmern zu erstellen.
     *
     * @param gruppen Eine Liste von Gruppen-Objekten, die für das Gridlayout verwendet werden sollen.
     * @param i Die Position der Gruppe.
     * @param grid Das Grid-Objekt, das für die Darstellung der Teilnehmer verwendet wird.
     *             Das Grids sollten bereits initialisiert und konfiguriert sein.
     *
     * @author Lilli
     */
    private void createGroupGridsLayout(List<Gruppe> gruppen, int i, Grid<Teilnehmer> grid) {
        H5 title = new H5("Gruppe " + (i +1) + ": " + gruppen.get(i).getTeilnehmer().size() + " Teilnehmer");
        titles.add(title);
        title.addClassName("gruppen-gruppenarbeit-title");

        Button deleteBtn = new Button(LineAwesomeIcon.TRASH_ALT.create());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButtons.add(deleteBtn);

        HorizontalLayout topLayout = new HorizontalLayout(title, deleteBtn);
        topLayout.setWidthFull();
        topLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        topLayout.setFlexGrow(1, title);

        Div titleAndGroups = new Div(topLayout, grid);
        titleAndGroups.addClassName("gruppen-gruppenarbeit");
        groupsArea.add(titleAndGroups);
    }

    /**
     * Erstellt und liefert eine Grid-Komponente, die mit Teilnehmerdaten aus einer bestimmten Gruppe gefüllt ist.
     * <p>
     * Diese Methode erstellt eine Grid-Komponente, die konfiguriert ist, um Details von Teilnehmern
     * anzuzeigen, die zu der Gruppe an einem bestimmten Index innerhalb der bereitgestellten Liste
     * von Gruppen gehören. Jede Grid-Instanz wird mit spezifischen Spalten wie Matrikelnr (ID),
     * Vorname und Nachname konfiguriert.
     * <p>
     * Das resultierende Grid und seine zugehörige DataView werden in den Listen gruppenGrids und
     * dataViews gespeichert, um sie später weiter zu bearbeiten oder abzurufen.
     *
     * @param gruppen Die Liste von Gruppen-Objekten, die Teilnehmerdaten enthalten.
     * @param i       Der Index der Gruppe innerhalb der gruppen-Liste, für die das Grid erstellt wird.
     * @return Eine Grid-Komponente, die Teilnehmerdaten aus der angegebenen Gruppe mit angepassten Spalten
     *         und Interaktionsmöglichkeiten anzeigt.
     *
     * @author Lilli
     */
    private Grid<Teilnehmer> getTeilnehmerGridAndDataView(List<Gruppe> gruppen, int i) {
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
        return grid;
    }

    /**
     * Erstellt und liefert das Hauptlayout für die Bearbeitung von Gruppen.
     * <p>
     * Diese Methode erstellt ein vertikales Layout, das für die Bearbeitung von Gruppen
     * konfiguriert ist. Sie setzt den Titel der Kopfzeile auf "Gruppen bearbeiten", konfiguriert
     * den Speichern-Button mit einem primären Theme und fügt weitere Buttons zum Fußbereich hinzu.
     * Außerdem integriert sie eine Überschrift ("Veranstaltungsteilnehmer, die nicht an der Gruppenarbeit teilnehmen"),
     * eine Liste von übrigen Teilnehmern und den Bereich für Gruppen.
     *
     * @return Ein vertikales Layout, das für die Bearbeitung von Gruppen konfiguriert ist und die
     *         erforderlichen UI-Elemente enthält.
     *
     * @author Lilli
     */
    private VerticalLayout createLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        setHeaderTitle("Gruppen bearbeiten");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButtonsToFooter();
        mainLayout.add(new H4("Veranstaltungsteilnehmer, die nicht an der Gruppenarbeit teilnehmen"), uebrigeTeilnehmer, groupsArea);

        return mainLayout;
    }

    /**
     * Fügt die Buttons zum Fußbereich des Layouts hinzu.
     * <p>
     * Diese Methode fügt die folgenden Buttons zum Fußbereich des Layouts hinzu:
     * - `addAllToGroupBtn`: Ein Button, der alle übrigen Veranstaltungsteilnehmer zur ersten Gruppe hinzufügt.
     * - `addNewGroupBtn`: Ein Button, um eine neue Gruppe hinzuzufügen.
     * - `mixBtn`: Ein Button, um die Verteilung der Teilnehmer auf die Gruppen zu mischen.
     * - `cancelBtn`: Ein Button, um die Änderungen abzubrechen.
     * - `saveBtn`: Der primäre Button zum Speichern der Änderungen.
     *
     * @author Lilli
     */
    private void addButtonsToFooter() {
        getFooter().add(addAllToGroupBtn);
        getFooter().add(addNewGroupBtn);
        getFooter().add(mixBtn);
        getFooter().add(cancelBtn);
        getFooter().add(saveBtn);
    }

    /**
     * Konfiguriert den Bereich für Gruppen.
     * <p>
     * Diese Methode setzt die Breite der `groupsArea` auf 100% und weist ihm die CSS-Klasse
     * "gruppen-container-gruppenarbeiten" zu.
     *
     * @author Lilli
     */
    private void configureGroupsArea() {
        groupsArea.setWidth("100%");
        groupsArea.setClassName("gruppen-container-gruppenarbeiten");
    }

    /**
     * Diese Methode erstellt eine Liste von Gruppenanzahlen- und Gruppengrößeninformationen basierend auf der Anzahl
     * der Teilnehmer.
     *
     * @param participants Die Liste der Teilnehmer, für die Gruppeninformationen erstellt werden sollen.
     * @return Eine Liste von Strings, die die Gruppeninformationen enthält.
     * @author Lilli
     */
    private List<String> getGroups(List<Teilnehmer> participants) {
        List<String> groups = new ArrayList<>();
        if(participants.isEmpty()){
            groups.add("Keine Teilnehmer ausgewählt.");
            return groups;
        }
        groups = groupNumbersAndSizes(participants.size());
        return groups;
    }

    /**
     * Berechnet die maximale Anzahl von Gruppen basierend auf der Anzahl der Teilnehmer.
     *
     * @param participants Die Anzahl der Teilnehmer, für die die maximale Anzahl von Gruppen berechnet werden soll.
     * @return Die maximale Anzahl von Gruppen, die gebildet werden können.
     *
     * @author Lilli
     */
    private int groupMax(int participants){
        return participants/2;
    }

    /**
     * Diese Methode berechnet die Anzahlen der Gruppen basierend auf der Anzahl der Teilnehmer.
     * Das sind dann eine mit allen Teilnehmern, 2 bis zur maximalen Anzahl und dann noch eine Gruppengröße, um alle
     * Teilnehmer in eine eigene Gruppe zu packen.
     *
     * @param participants Die Anzahl der Teilnehmer, für die die Gruppen berechnet werden sollen.
     * @return Ein Array von ganzen Zahlen, das die Anzahl der Gruppen und die Anzahl der Teilnehmer enthält.
     *
     * @author Lilli
     */
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

    /**
     * Diese Methode berechnet die Größen der Gruppen basierend auf der Anzahl der Gruppen und der Teilnehmerzahl.
     *
     * @param groups      Die Anzahl der Gruppen, in die die Teilnehmer aufgeteilt werden sollen.
     * @param participants Die Gesamtanzahl der Teilnehmer, die auf die Gruppen aufgeteilt werden sollen.
     * @return Ein Array von ganzen Zahlen, das die Größen der Gruppen enthält.
     *
     * @author Lilli
     */
    private int[] groupSizes(int groups, int participants){
        if(participants%groups == 0){
            return new int[]{participants/groups};
        }
        else{
            return new int[]{participants/groups, participants/groups+1};
        }
    }

    /**
     * Erstellt eine Liste von Strings, die die Gruppenanzahlen und deren Größen basierend auf der Anzahl der
     * Teilnehmer enthält.
     *
     * @param participants Die Anzahl der Teilnehmer.
     * @return Eine Liste von Strings, die die Gruppennummern und deren Größen darstellen.
     *
     * @author Lilli
     */
    private List<String> groupNumbersAndSizes(int participants){
        List<String> groupStrings = new ArrayList<>();
        for(int i:groupNumbers(participants))
        {
            StringBuilder str = new StringBuilder();
            str.append(i);
            str.append(" x ");

            for(Iterator<Integer> it = Arrays.stream(groupSizes(i, participants)).iterator(); it.hasNext();){
                String nextSize = it.next().toString();
                if(it.hasNext()){
                    str.append(nextSize);
                    str.append(" und ");
                }
                else{
                    str.append(nextSize);
                    str.append(" Teilnehmer");
                }
            }
            groupStrings.add(str.toString());
        }
        return groupStrings;
    }

    /**
     * Gibt die Anzahl der Gruppen zurück, die aus der ausgewählten Gruppengröße extrahiert wird.
     *
     * @return Die Anzahl der Gruppen als Ganzzahl.
     *
     * @author Lilli
     */
    private int getNumberOfGroups() {
        String num = groupSize.getValue();
        String[] splitString = num.split(" ");
        return Integer.parseInt(splitString[0]);
    }

    /**
     * Fügt der Liste {@code gruppen} die angegebene Anzahl von Gruppen hinzu.
     *
     * @param numberOfGroups Die Anzahl der zu erstellenden Gruppen.
     * @param gruppen        Die Liste, zu der die neuen Gruppen hinzugefügt werden sollen.
     *
     * @author Lilli
     */
    private void makeGroups(int numberOfGroups, List<Gruppe> gruppen) {
        for(int i = 0; i< numberOfGroups; i++){
            gruppen.add(new Gruppe((long) i+1));
        }
    }

    /**
     * Mischung der Teilnehmer zufällig in die Gruppen basierend auf den angegebenen Gruppengrößen.
     *
     * @param sizes          Ein Array von Größen der Gruppen, das angibt, wie viele Teilnehmer in jeder Gruppe sein
     *                       sollen.
     * @param numberOfGroups Die Anzahl der Gruppen, die erstellt wurden.
     * @param gruppen        Eine Liste von Gruppen, in die die Teilnehmer zufällig aufgeteilt werden.
     * @param participants   Eine Liste von Teilnehmern, die zufällig in die Gruppen aufgeteilt werden sollen.
     *
     * @author Lilli
     */
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

    /**
     * Aktualisiert die Ansicht der Gruppenarbeit basierend auf den aktuellen Daten.
     * Die Methode aktualisiert die Gruppen, Teilnehmer und andere relevante Daten, um die Ansicht
     * entsprechend den aktuellen Zuständen zu rendern.
     *
     * @author Lilli
     */
    public void update(){
        this.gruppen = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId()).getGruppen();
        this.allTeilnehmer = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId()).getVeranstaltungstermin().getVeranstaltung().getTeilnehmer();
        this.gruppenarbeitTeilnehmer = gruppenarbeit.getTeilnehmer();
        this.otherTeilnehmer = new ArrayList<>(allTeilnehmer);
        otherTeilnehmer.removeAll(gruppenarbeitTeilnehmer);
        dataViews.clear();
        gruppenGrids.clear();
        groupsArea.removeAll();
        deleteButtons.clear();
        titles.clear();
        removeAll();
        configureUebrigeTeilnehmerGrid();
        groupGrids(gruppen.size(), gruppen);
        deleteBtnsFunctionality();
        add(createLayout());
    }
}
