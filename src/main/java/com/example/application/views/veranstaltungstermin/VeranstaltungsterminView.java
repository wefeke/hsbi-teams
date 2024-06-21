package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.models.*;
import com.example.application.views.MainLayout;
import com.example.application.views.gruppe.GruppeBearbeitenDialog;
import com.example.application.views.gruppe.GruppeAuswertungDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitBearbeitenDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitHinzufuegenDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitLoeschenDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.format.DateTimeFormatter;
import java.util.*;

@PageTitle("Veranstaltung Detail")
@Route(value = "veranstaltung-detail/:veranstaltungId", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class VeranstaltungsterminView extends VerticalLayout implements HasUrlParameter<String> {

    //Services
    private final VeranstaltungenService veranstaltungService;
    private final VeranstaltungsterminService veranstaltungsterminService;
    private final GruppenarbeitService gruppenarbeitService;
    private final TeilnehmerService teilnehmerService;
    private final GruppeService gruppeService;
    private final GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;

    //Data
    private String veranstaltungIdString;
    private Veranstaltung veranstaltung;
    private List<Veranstaltungstermin> termine;
    private Div aktiveKachelVeranstaltungstermin = null;
    private Div aktiveKachelGruppenarbeit = null;
    private int maxListHeight = 0;
    private final AuthenticatedUser authenticatedUser;
    private final Map<Div, Veranstaltungstermin> veranstaltungsterminMap = new HashMap<>();
    private final Map<Div, Gruppenarbeit> gruppenarbeitMap = new HashMap<>();
    private Veranstaltungstermin aktiverVeranstaltungstermin;
    private Gruppenarbeit aktiveGruppenarbeit;
    private final Div rightContainer;

    //UI Elements
    private final Div veranstaltungsterminContainer;
    private final Div gruppenarbeitContainer;
    private final Div gruppenContainer;
    private final H1 veranstaltungTitle;
    private final Div teilnehmerListe;
    private final Button toggleTeilnehmerListeButton;
    private final Select<String> filterButton;

    //Dialog Instance
    private VeranstaltungsterminHinzufuegenDialog veranstaltungsterminHinzufuegenDialog;
    private GruppenarbeitHinzufuegenDialog gruppenarbeitHinzufuegenDialog;
    private GruppenarbeitBearbeitenDialog gruppenarbeitBearbeitenDialog;
    private GruppenarbeitLoeschenDialog gruppenarbeitLoeschenDialog;
    private GruppeAuswertungDialog gruppeAuswertungDialog;
    private TeilnehmerHinzufuegenDialog teilnehmerHinzufuegenDialog;
    private TeilnehmerEntfernenDialog teilnehmerEntfernenDialog;
    private VeranstaltungsterminLoeschenDialog veranstaltungsterminLoeschenDialog;
    private GruppeBearbeitenDialog gruppeBearbeitenDialog;

    private final VerticalLayout contentLayout;
    private HorizontalLayout gruppenarbeitLinie;
    private HorizontalLayout gruppenLinie;

    /**
     * Erstellt eine neue VeranstaltungDetailView.
     * Diese Ansicht zeigt detaillierte Informationen über eine Veranstaltung an.
     * Sie enthält eine Liste von Veranstaltungsterminen, Gruppenarbeiten und Teilnehmern.
     * Die Ansicht bietet auch Funktionen zum Filtern von Veranstaltungsterminen, Umschalten der Sichtbarkeit der Teilnehmerliste,
     * und zur Navigation zur Auswertungsseite.
     *
     * @param veranstaltungService der Service zur Verwaltung von Veranstaltungsdaten
     * @param veranstaltungsterminService der Service zur Verwaltung von Veranstaltungstermin-Daten
     * @param gruppenarbeitService der Service zur Verwaltung von Gruppenarbeit-Daten
     * @param teilnehmerService der Service zur Verwaltung von Teilnehmer-Daten
     * @param gruppeService der Service zur Verwaltung von Gruppen-Daten
     * @param gruppenarbeitTeilnehmerService der Service zur Verwaltung von GruppenarbeitTeilnehmer-Daten
     * @param authenticatedUser der aktuell authentifizierte Benutzer
     *
     * @author Joris
     */
    public VeranstaltungsterminView(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, GruppeService gruppeService, GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService, AuthenticatedUser authenticatedUser) {
        // Initialisierung der Services
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerService = teilnehmerService;
        this.teilnehmerListe = new Div();
        this.veranstaltungTitle = new H1();
        this.toggleTeilnehmerListeButton = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        this.veranstaltungsterminContainer = new Div();
        this.gruppenarbeitContainer = new Div();
        this.gruppenContainer = new Div();
        this.contentLayout = new VerticalLayout();

        VerticalLayout mainLayoutLeft = new VerticalLayout();
        mainLayoutLeft.setSizeFull();

        Div leftContainer = new Div();
        leftContainer.addClassName("left-container");
        leftContainer.add(mainLayoutLeft);

        rightContainer = new Div();
        rightContainer.addClassName("right-container");
        rightContainer.add(teilnehmerListe);

        HorizontalLayout mainLayout = new HorizontalLayout(leftContainer, rightContainer);
        mainLayout.setSizeFull();
        mainLayout.getStyle().set("overflow", "hidden");

        VerticalLayout titleContainer = new VerticalLayout();
        Div contentContainer = new Div();
        contentContainer.getStyle().set("width", "calc(100% - 400px)");
        contentContainer.getStyle().set("height", "80vh");
        contentContainer.getStyle().set("border-top", "1px solid #ccc");

        // Konfiguration des Titels und des Umschaltbuttons
        veranstaltungTitle.addClassName("veranstaltung-title");

        Button auswertungButton = new Button("Auswertung");
        auswertungButton.addClassName("auswertung-button");
        // Navigation zur Auswertung
        auswertungButton.addClickListener(e -> {
            String route = "auswertung/" + veranstaltung.getId();
            UI.getCurrent().navigate(route);
        });

        toggleTeilnehmerListeButton.addClickListener(e -> {
            // Umschalten der Sichtbarkeit der Teilnehmerliste
            teilnehmerListe.setVisible(!teilnehmerListe.isVisible());

            String display = teilnehmerListe.getStyle().get("display");
            boolean isVisible = display != null && display.equals("none");
            teilnehmerListe.getStyle().set("display", isVisible ? "block" : "none");

            if (isVisible) {
                toggleTeilnehmerListeButton.setIcon(new Icon(VaadinIcon.ANGLE_RIGHT));
                leftContainer.getStyle().set("width", "calc(100% - 325px)");
                rightContainer.getStyle().set("width", "320px");
                contentContainer.getStyle().set("width", "calc(100% - 400px)");
            } else {
                toggleTeilnehmerListeButton.setIcon(new Icon(VaadinIcon.ANGLE_LEFT));
                leftContainer.getStyle().set("width", "calc(100% - 35px)");
                rightContainer.getStyle().set("width", "0");
                contentContainer.getStyle().set("width", "calc(100% - 35px)");
            }
        });

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(Alignment.CENTER);
        titleLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        titleLayout.setWidthFull();
        titleLayout.getStyle().set("margin-top", "-10px");
        titleLayout.getStyle().set("height", "40px");

        titleLayout.add(veranstaltungTitle);
        Div spacer = new Div();
        titleLayout.add(spacer);
        titleLayout.setFlexGrow(1, spacer);
        titleLayout.add(auswertungButton, toggleTeilnehmerListeButton);

        HorizontalLayout lineWithText = createLineWithText("Veranstaltungstermine");

        filterButton = new Select<>();
        filterButton.setItems("Datum aufsteigend", "Datum absteigend", "Titel A-Z", "Titel Z-A");
        filterButton.setValue("Datum aufsteigend");

        filterButton.addValueChangeListener(event -> applyVeranstaltungsterminFilter());

        Hr lineAfter = new Hr();
        lineAfter.addClassName("line-after-icon");

        HorizontalLayout veranstaltungsterminLinie = new HorizontalLayout(lineWithText, filterButton, lineAfter);
        veranstaltungsterminLinie.setAlignItems(Alignment.CENTER);
        veranstaltungsterminLinie.setJustifyContentMode(JustifyContentMode.BETWEEN);
        veranstaltungsterminLinie.setWidthFull();

        // Konfiguration der Container
        veranstaltungsterminContainer.addClassName("veranstaltungen-container");
        gruppenarbeitContainer.addClassName("gruppenarbeiten-container");
        gruppenContainer.addClassName("gruppen-container");

        contentContainer.addClassName("content-container");
        contentLayout.setSizeFull();

        contentContainer.add(contentLayout);
        titleContainer.add(titleLayout);
        contentLayout.add(veranstaltungsterminLinie, veranstaltungsterminContainer);

        mainLayoutLeft.add(titleContainer, contentContainer);
        mainLayoutLeft.getStyle().set("overflow", "hidden");

        // Hinzufügen des Hauptlayouts zum View
        add(mainLayout);
    }

    /**
     * Setzt den Parameter für die VeranstaltungDetailView.
     * Diese Methode wird aufgerufen, wenn die URL-Parameter der Ansicht geändert werden.
     * Sie extrahiert die Veranstaltungs-ID aus der URL und verwendet sie, um die Veranstaltung und ihre Termine aus dem Service zu laden.
     * Sie erstellt auch Dialoge für verschiedene Aktionen wie das Hinzufügen und Entfernen von Teilnehmern und das Bearbeiten und Löschen von Gruppenarbeiten und Veranstaltungsterminen.
     * Schließlich wird die init-Methode aufgerufen, um die Ansicht zu initialisieren.
     *
     * @param event das BeforeEvent, das die Änderung der URL-Parameter ausgelöst hat
     * @param parameter der Wildcard-Parameter aus der URL
     *
     * @autor Joris
     */
    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        Location location = event.getLocation();
        List<String> segments = location.getSegments();

        if (!segments.isEmpty()) {
            this.veranstaltungIdString = segments.getLast();
            try {
                long veranstaltungIdLong = Long.parseLong(veranstaltungIdString);
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();

                    termine = veranstaltungsterminService.findVeranstaltungstermineByVeranstaltungId(veranstaltungIdLong, user);
                    veranstaltung = veranstaltungService.findVeranstaltungById(veranstaltungIdLong, user);
                } else {
                    Notification.show("Bitte melden Sie sich an, um Ihre Veranstaltungstermine zu sehen.");
                    getUI().ifPresent(ui -> ui.navigate("login"));
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid veranstaltungId: " + veranstaltungIdString);
            }
        } else {
            System.err.println("No veranstaltungId in URL");
        }

        createVeranstaltungsterminDialog();
        createGruppenarbeitBearbeitenDialog();
        createGruppenarbeitLoeschenDialog();
        createVeranstaltungsterminLoeschenDialog();
        createTeilnehmerHinzufuegenDialog();
        createTeilnehmerEntfernenDialog();

        //init Methode ist wichtig, da erst hier die termine gesetzt werden, weil sonst im Konstruktor die termine noch nicht gesetzt sind,
        // wenn er aufgerufen wird, wodurch es zu einem Fehler kommt.
        init();
    }

    /**
     * Initialisiert die VeranstaltungDetailView.
     * Diese Methode wird aufgerufen, nachdem die Veranstaltungs-ID aus der URL extrahiert und die zugehörige Veranstaltung und ihre Termine geladen wurden.
     * Sie setzt den Titel der Veranstaltung, fügt Kacheln für jeden Veranstaltungstermin hinzu und erstellt eine Kachel zum Hinzufügen neuer Veranstaltungstermine.
     * Schließlich wird die Teilnehmerliste erstellt und hinzugefügt.
     *
     * @autor Joris
     */
    public void init() {
        veranstaltungTitle.setText(veranstaltung.getTitel());

        for (Veranstaltungstermin termin : termine) {
            veranstaltungsterminContainer.add(veranstaltungsterminKachel(termin));
        }

        veranstaltungsterminContainer.add(createVeranstaltungsterminKachel());

        teilnehmerListe.add(createTeilnehmerListe());
    }

    /**
     * Aktualisiert die VeranstaltungDetailView.
     * Diese Methode wird aufgerufen, um die Ansicht zu aktualisieren, indem sie verschiedene Aktionen auslöst.
     * Sie löst Klicks auf die Kacheln für Veranstaltungstermine und Gruppenarbeiten aus, wendet den Filter für Veranstaltungstermine an und aktualisiert die Teilnehmerliste.
     *
     * @autor Joris
     */
    public void update() {
        applyVeranstaltungsterminFilter();

        //if (!(veranstaltungsterminService.findAllVeranstaltungstermine().isEmpty()) ) {
        if ( aktiverVeranstaltungstermin != null && veranstaltungsterminService.findVeranstaltungsterminById(aktiverVeranstaltungstermin.getId()).isPresent()) {
            triggerVeranstaltungsterminKachelClick();
            triggerGruppenarbeitKachelClick();
        } else {
            gruppenarbeitContainer.removeAll();
            if (gruppenarbeitLinie != null) {
                gruppenarbeitLinie.setVisible(false);
            }
            gruppenContainer.removeAll();
            if (gruppenLinie != null) {
                gruppenLinie.setVisible(false);
            }
        }


        updateTeilnehmerListe();
    }

    /**
     * Aktualisiert die Liste der Teilnehmer.
     * Diese Methode entfernt die aktuelle Teilnehmerliste aus dem rechten Container, erstellt eine neue Teilnehmerliste und fügt sie wieder hinzu.
     * Sie wird aufgerufen, wenn eine Änderung an den Teilnehmern vorgenommen wurde und die Ansicht aktualisiert werden muss.
     *
     * @autor Joris
     */
    private void updateTeilnehmerListe() {
        rightContainer.remove(teilnehmerListe);

        teilnehmerListe.removeAll();
        teilnehmerListe.add(createTeilnehmerListe());

        rightContainer.add(teilnehmerListe);
    }

    /**
     * Fügt einen Veranstaltungstermin zur Liste der Termine hinzu.
     * Diese Methode wird aufgerufen, um einen neuen Veranstaltungstermin zur Liste der Termine hinzuzufügen.
     * Sie überprüft, ob der übergebene Veranstaltungstermin nicht null ist, bevor sie ihn zur Liste hinzufügt.
     *
     * @param veranstaltungstermin der Veranstaltungstermin, der zur Liste hinzugefügt werden soll
     *
     * @autor Joris
     */
    public void addTerminToTermine (Veranstaltungstermin veranstaltungstermin){
        if (veranstaltungstermin != null){
            termine.add(veranstaltungstermin);
        }
    }

    /**
     * Ersetzt einen vorhandenen Veranstaltungstermin in der Liste der Termine durch einen neuen.
     * Diese Methode wird aufgerufen, um einen bestehenden Veranstaltungstermin in der Liste der Termine durch einen neuen zu ersetzen.
     * Sie sucht den zu ersetzenden Veranstaltungstermin in der Liste, entfernt ihn und fügt den neuen Veranstaltungstermin hinzu.
     * Sie überprüft, ob der übergebene neue Veranstaltungstermin und die ID des zu ersetzenden Veranstaltungstermins nicht null sind, bevor sie die Operation durchführt.
     *
     * @param veranstaltungstermin der neue Veranstaltungstermin, der in die Liste aufgenommen werden soll
     * @param veranstaltungsterminId die ID des Veranstaltungstermins, der aus der Liste entfernt werden soll
     *
     * @autor Joris
     */
    public void removeAndAddTerminToTermine (Veranstaltungstermin veranstaltungstermin, Long veranstaltungsterminId) {
        if (veranstaltungstermin != null && veranstaltungsterminId != null){
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                if (!veranstaltungsterminService.findAllVeranstaltungstermine(user).isEmpty()) {
                    Optional<Veranstaltungstermin> v = veranstaltungsterminService.findVeranstaltungsterminById(veranstaltungsterminId);
                    if (v.isPresent()) {
                        termine.remove(v.get());
                        termine.add(veranstaltungstermin);
                    }
                }
            }
        }
    }

    /**
     * Erstellt eine visuelle Kachel (Div) für einen gegebenen Veranstaltungstermin.
     * Die Kachel enthält das Datum, die Start- und Endzeit und den Titel des Veranstaltungstermins.
     * Sie enthält auch ein Bearbeiten- und ein Löschen-Symbol, die jeweils ihre eigenen Funktionalitäten haben.
     * Wenn die Maus über die Kachel fährt, werden die Bearbeiten- und Löschen-Symbole sichtbar.
     * Ein Klick auf das Bearbeiten-Symbol öffnet einen Bearbeiten-Dialog, während ein Klick auf das Löschen-Symbol einen Löschen-Dialog öffnet.
     * Ein Klick auf die Kachel selbst prüft, ob sie die aktuell aktive Kachel ist. Wenn ja, werden alle Gruppenarbeitskacheln entfernt und sie selbst wird als inaktiv gesetzt.
     * Wenn sie nicht die aktive Kachel ist, werden alle Gruppenarbeitskacheln entfernt, die Gruppenarbeitskacheln für das angeklickte Veranstaltungsdatum aktualisiert und sie selbst wird als aktive Kachel gesetzt.
     *
     * @param veranstaltungstermin der Veranstaltungstermin, für den die Kachel erstellt werden soll
     * @return die erstellte Kachel als Div-Objekt
     *
     * @autor Joris
     */
    private Div veranstaltungsterminKachel(Veranstaltungstermin veranstaltungstermin) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Div terminDatum = new Div();
        terminDatum.setText(veranstaltungstermin.getDatum().format(dateFormatter));
        terminDatum.addClassName("termin-datum");

        Div terminZeit = new Div();
        String startZeit = veranstaltungstermin.getStartZeit().format(timeFormatter);
        String endZeit = veranstaltungstermin.getEndZeit().format(timeFormatter);
        terminZeit.setText(startZeit + "-" + endZeit);
        terminZeit.addClassName("termin-zeit");

        Div terminNotiz = new Div();
        String splitTitle = splitLongWords(veranstaltungstermin.getTitel());
        terminNotiz.setText(splitTitle);
        terminNotiz.addClassName("termin-notiz");

        Div kachelContent = new Div(terminDatum, terminZeit, terminNotiz);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        veranstaltungsterminMap.put(kachel, veranstaltungstermin);
        kachel.addClassName("kachel");

        VeranstaltungsterminBearbeitenDialog editDialog = new VeranstaltungsterminBearbeitenDialog(veranstaltungService, veranstaltungsterminService, this, veranstaltungIdString, veranstaltungstermin.getId(), authenticatedUser, aktiverVeranstaltungstermin, aktiveGruppenarbeit);

        //Delete Icon
        Div deleteIcon = createDeleteIcon();
        deleteIcon.addClassName("delete-icon");

        //Edit Icon
        Div editIcon = new Div(LineAwesomeIcon.EDIT.create());
        editIcon.addClassName("edit-icon");

        editIconFunctionality(editIcon, deleteIcon, editDialog);
        deleteIconFunctionality(deleteIcon, editIcon, veranstaltungsterminLoeschenDialog);

        editIcon.getElement().addEventListener("click", e-> {
            editDialog.open();
            editDialog.readBean();
        }).addEventData("event.stopPropagation()");

        kachel.add(deleteIcon);
        kachel.add(editIcon);

        veranstaltungsterminLoeschenDialog.addOpenedChangeListener(e -> {
               if (!e.isOpened()) {
                kachel.getStyle().setBackgroundColor("");
                deleteIcon.getStyle().set("visibility", "hidden");
                editIcon.getStyle().set("visibility", "hidden");
            }
        });

        //Confirm Dialog Deselect Implementation
        editDialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                kachel.getStyle().setBackgroundColor("");
                deleteIcon.getStyle().set("visibility", "hidden");
                editIcon.getStyle().set("visibility", "hidden");
            }
        });

        kachel.getElement().addEventListener("mouseover", e -> {
            if (!kachel.equals(aktiveKachelVeranstaltungstermin)) {
                kachel.addClassName("kachel-hover");
            }
            editIcon.getStyle().set("visibility", "visible");
            deleteIcon.getStyle().set("visibility", "visible");
            veranstaltungsterminLoeschenDialog.setVeranstaltungstermin(veranstaltungstermin);
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            if (!kachel.equals(aktiveKachelVeranstaltungstermin)) {
                kachel.removeClassName("kachel-hover");
            }
            editIcon.getStyle().set("visibility", "hidden");
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e -> {
            gruppenarbeitLoeschenDialog.setVeranstaltungstermin(veranstaltungstermin);
            maxListHeight = 0;

            if (kachel.equals(aktiveKachelVeranstaltungstermin)) {
                gruppenarbeitContainer.removeAll();
                gruppenarbeitLinie.setVisible(false);
                aktiveKachelVeranstaltungstermin.removeClassName("kachel-active");
                aktiverVeranstaltungstermin = null;
                aktiveKachelVeranstaltungstermin = null;
            } else {

                if (aktiveKachelVeranstaltungstermin != null) {
                    aktiveKachelVeranstaltungstermin.removeClassName("kachel-active");
                    aktiverVeranstaltungstermin = null;
                    if(aktiveKachelGruppenarbeit!=null) {
                        aktiveKachelGruppenarbeit.removeClassName("kachel-active");
                        aktiveKachelGruppenarbeit = null;
                    }
                    aktiveGruppenarbeit = null;
                }

                gruppenarbeitContainer.removeAll();


                if (gruppenarbeitLinie == null) {
                    gruppenarbeitLinie = createLineWithText("Gruppenarbeiten");
                    contentLayout.add(gruppenarbeitLinie, gruppenarbeitContainer);
                }

                updateGruppenarbeiten(veranstaltungstermin);

                kachel.addClassName("kachel-active");
                aktiveKachelVeranstaltungstermin = kachel;
                aktiverVeranstaltungstermin = veranstaltungstermin;

            }

            if (gruppenLinie != null) {
                gruppenLinie.setVisible(false);
                gruppenContainer.removeAll();
            }
            gruppenContainer.setVisible(false);
        });

        return kachel;
    }

    /**
     * Teilt Wörter, die länger als eine bestimmte Länge sind, in kleinere Teile auf.
     * Diese Methode nimmt einen Text und eine maximale Länge als Eingabe. Sie teilt den Text in Wörter auf und überprüft jedes Wort.
     * Wenn ein Wort länger als die maximale Länge ist, teilt es das Wort an der Position der maximalen Länge in zwei Teile und fügt einen Bindestrich zwischen den Teilen ein.
     * Der modifizierte Text wird dann zurückgegeben.
     *
     * @param text der Eingabetext, der möglicherweise lange Wörter enthält, die geteilt werden sollen
     * @return der modifizierte Text mit langen Wörtern, die in kleinere Teile geteilt wurden
     *
     * @autor Joris
     */
    private String splitLongWords(String text) {
        String[] words = text.split(" ");
        StringBuilder newText = new StringBuilder();

        int maxLength = 21;

        for (String word : words) {
            if (word.length() > maxLength) {
                String splitWord = word.substring(0, maxLength - 1) + "-" + word.substring(maxLength - 1);
                newText.append(splitWord).append(" ");
            } else {
                newText.append(word).append(" ");
            }
        }

        return newText.toString().trim();
    }

    /**
     * Erstellt eine visuelle Kachel (Div) zum Hinzufügen eines neuen Veranstaltungstermins.
     * Die Kachel enthält ein Plus-Symbol und wird mit einem Klick-Listener ausgestattet, der einen Dialog zum Hinzufügen eines neuen Veranstaltungstermins öffnet.
     * Sie hat auch Hover-Effekte, die durch Mouseover- und Mouseout-Events gesteuert werden.
     *
     * @return die erstellte Kachel als Div-Objekt
     *
     * @autor Joris
     */
    private Div createVeranstaltungsterminKachel() {
        //Ich kann diese Methode nicht als static machen, weil getUi() nicht statisch ist.
        //Dadurch muss ich diese Methode in jeder Klasse neu einbauen, wo ich sie verwenden möchte.
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.addClassName("plus-symbol");

        Div neueVeranstaltungsterminKachel = new Div(plusSymbol);
        neueVeranstaltungsterminKachel.addClassName("neue-veranstaltungstermin-kachel");

        neueVeranstaltungsterminKachel.getElement().addEventListener("mouseover", e ->
                neueVeranstaltungsterminKachel.addClassName("hover"));

        neueVeranstaltungsterminKachel.getElement().addEventListener("mouseout", e ->
                neueVeranstaltungsterminKachel.removeClassName("hover"));

        neueVeranstaltungsterminKachel.addClickListener(e ->
                veranstaltungsterminHinzufuegenDialog.open()
        );

        return neueVeranstaltungsterminKachel;
    }

    /**
     * Erstellt einen Dialog zur Bearbeitung eines Veranstaltungstermins.
     * Dieser Dialog wird verwendet, um die Details eines Veranstaltungstermins zu bearbeiten oder einen neuen Veranstaltungstermin zu erstellen.
     * Der Dialog erhält alle notwendigen Dienste und Daten, um die Bearbeitung durchzuführen, einschließlich der aktuellen Veranstaltung, des authentifizierten Benutzers und der aktiven Gruppenarbeit.
     *
     * @autor Joris
     */
    public void createVeranstaltungsterminDialog () {
        veranstaltungsterminHinzufuegenDialog = new VeranstaltungsterminHinzufuegenDialog(veranstaltungService, veranstaltungsterminService, this, veranstaltungIdString, authenticatedUser, aktiverVeranstaltungstermin, aktiveGruppenarbeit);
    }

    /**
     * Erstellt einen Dialog zum Hinzufügen einer Gruppenarbeit.
     * Dieser Dialog wird verwendet, um eine neue Gruppenarbeit zu einem gegebenen Veranstaltungstermin hinzuzufügen.
     * Der Dialog erhält alle notwendigen Dienste und Daten, um die Gruppenarbeit hinzuzufügen, einschließlich des authentifizierten Benutzers, der Veranstaltungs-ID, der verschiedenen Services und des Veranstaltungstermins.
     *
     * @param veranstaltungstermin der Veranstaltungstermin, zu dem die Gruppenarbeit hinzugefügt werden soll
     * @return der erstellte Dialog zum Hinzufügen einer Gruppenarbeit
     *
     * @autor Lilli
     */
    public GruppenarbeitHinzufuegenDialog createGruppenarbeitHinzufuegenDialog(Veranstaltungstermin veranstaltungstermin) {
        GruppenarbeitHinzufuegenDialog dialog = new GruppenarbeitHinzufuegenDialog(authenticatedUser, veranstaltungIdString, gruppenarbeitService, teilnehmerService, veranstaltungsterminService, gruppeService, this, veranstaltungService, veranstaltungstermin);
        dialog.setWidth("1500px");
        return dialog;
    }

    /**
     * Erstellt einen Dialog zur Bearbeitung einer Gruppenarbeit.
     * Dieser Dialog wird verwendet, um die Details einer Gruppenarbeit zu bearbeiten.
     * Der Dialog erhält den GruppenarbeitService zur Verwaltung von Gruppenarbeit-Daten und die aktuelle Instanz dieser Klasse.
     *
     * @autor Lilli
     */
    public void createGruppenarbeitBearbeitenDialog() {
        gruppenarbeitBearbeitenDialog = new GruppenarbeitBearbeitenDialog(gruppenarbeitService, this);
    }

    /**
     * Erstellt einen Dialog zum Löschen einer Gruppenarbeit.
     * Dieser Dialog wird verwendet, um eine bestehende Gruppenarbeit zu löschen.
     * Der Dialog erhält alle notwendigen Dienste zur Verwaltung von Gruppenarbeit-, Gruppe- und Veranstaltungstermin-Daten sowie die aktuelle Instanz dieser Klasse.
     *
     * @autor Lilli
     */
    public void createGruppenarbeitLoeschenDialog() {
        gruppenarbeitLoeschenDialog = new GruppenarbeitLoeschenDialog(gruppenarbeitService, gruppeService, veranstaltungsterminService,this, aktiveGruppenarbeit);
    }

    /**
     * Erstellt einen Dialog zum Löschen eines Veranstaltungstermins.
     * Dieser Dialog wird verwendet, um einen bestehenden Veranstaltungstermin zu löschen.
     * Der Dialog erhält alle notwendigen Dienste und Daten zur Verwaltung von Veranstaltungstermin-, Gruppenarbeit-, Gruppe- und Veranstaltungsdaten sowie die aktuelle Instanz dieser Klasse.
     * Zusätzlich erhält der Dialog die aktuell ausgewählten Veranstaltungstermin und Gruppenarbeit.
     *
     * @autor Lilli
     */
    private void createVeranstaltungsterminLoeschenDialog() {
        veranstaltungsterminLoeschenDialog = new VeranstaltungsterminLoeschenDialog(veranstaltung, gruppeService, gruppenarbeitService, this, aktiverVeranstaltungstermin, aktiveGruppenarbeit, veranstaltungsterminService, veranstaltungService);
    }

    /**
     * Erstellt einen Dialog zum Hinzufügen eines Teilnehmers.
     * Dieser Dialog wird verwendet, um einen neuen Teilnehmer zu einer bestimmten Veranstaltung hinzuzufügen.
     * Der Dialog erhält alle notwendigen Dienste und Daten zur Verwaltung von Teilnehmer-, Veranstaltungs- und Veranstaltungstermin-Daten sowie die aktuelle Instanz dieser Klasse.
     * Zusätzlich erhält der Dialog den aktuell ausgewählten Veranstaltungstermin und die aktive Gruppenarbeit.
     *
     * @autor Joris
     */
    public void createTeilnehmerHinzufuegenDialog() {
        teilnehmerHinzufuegenDialog = new TeilnehmerHinzufuegenDialog(veranstaltungService, teilnehmerService, veranstaltung.getId(), authenticatedUser, this, aktiverVeranstaltungstermin, aktiveGruppenarbeit);
    }

    /**
     * Erstellt einen Dialog zum Entfernen eines Teilnehmers.
     * Dieser Dialog wird verwendet, um einen bestehenden Teilnehmer von einer bestimmten Veranstaltung zu entfernen.
     * Der Dialog erhält alle notwendigen Dienste und Daten zur Verwaltung von Teilnehmer-, Veranstaltungs- und Veranstaltungstermin-Daten sowie die aktuelle Instanz dieser Klasse.
     * Zusätzlich erhält der Dialog den aktuell ausgewählten Veranstaltungstermin und die aktive Gruppenarbeit.
     *
     * @autor Joris
     */
    public void createTeilnehmerEntfernenDialog() {
        teilnehmerEntfernenDialog = new TeilnehmerEntfernenDialog(veranstaltungService, teilnehmerService, veranstaltung.getId(), authenticatedUser, this, aktiverVeranstaltungstermin, aktiveGruppenarbeit);
    }

    /**
     * Erstellt eine visuelle Kachel (Div) für eine gegebene Gruppenarbeit.
     * Die Kachel enthält Informationen über die Gruppenarbeit und Interaktionsmöglichkeiten wie Bearbeiten und Löschen.
     * Bei einem Mouseover-Event werden die Bearbeiten- und Löschen-Symbole sichtbar.
     * Ein Klick auf das Bearbeiten-Symbol öffnet einen Dialog zum Bearbeiten der Gruppenarbeit.
     * Ein Klick auf das Löschen-Symbol ��ffnet einen Dialog zum Löschen der Gruppenarbeit.
     * Ein Klick auf die Kachel selbst aktualisiert die Gruppenarbeitsansicht entsprechend der ausgewählten Gruppenarbeit.
     *
     * @param gruppenarbeit die Gruppenarbeit, für die die Kachel erstellt werden soll
     * @return die erstellte Kachel als Div-Objekt
     *
     * @autor Joris
     */
    private Div gruppenarbeitKachel(Gruppenarbeit gruppenarbeit) {
        Div gruppenarbeitInfo = new Div();
        String splitTitle = splitLongWords(gruppenarbeit.getTitel());
        gruppenarbeitInfo.setText(splitTitle);
        gruppenarbeitInfo.addClassName("text-center");

        Div kachelContent = new Div(gruppenarbeitInfo);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        gruppenarbeitMap.put(kachel, gruppenarbeit);
        kachel.addClassName("kachel");

        //Tooltip
        String tooltipText = "Titel: " + gruppenarbeit.getTitel() + "\nBeschreibung: " + gruppenarbeit.getBeschreibung();
        kachel.getElement().setProperty("title", tooltipText);

        Div deleteIconGruppenarbeit = createDeleteIcon();
        Div editIconGruppenarbeit = createEditIcon();

        deleteIconFunctionality(deleteIconGruppenarbeit, editIconGruppenarbeit, gruppenarbeitLoeschenDialog);
        editIconFunctionality(editIconGruppenarbeit, deleteIconGruppenarbeit, gruppenarbeitBearbeitenDialog);

        kachel.add(deleteIconGruppenarbeit);
        kachel.add(editIconGruppenarbeit);

        kachel.getElement().addEventListener("mouseover", e -> {
            if (!kachel.equals(aktiveKachelGruppenarbeit)) {
                kachel.addClassName("kachel-hover");
            }
            deleteIconGruppenarbeit.getStyle().set("visibility", "visible");
            gruppenarbeitBearbeitenDialog.setGruppenarbeit(gruppenarbeit);
            gruppenarbeitBearbeitenDialog.readBean();
            gruppenarbeitLoeschenDialog.setGruppenarbeit(gruppenarbeit);
            editIconGruppenarbeit.getStyle().set("visibility", "visible");
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            if (!kachel.equals(aktiveKachelGruppenarbeit)) {
                kachel.removeClassName("kachel-hover");
            }
            deleteIconGruppenarbeit.getStyle().set("visibility", "hidden");
            editIconGruppenarbeit.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e -> {
            maxListHeight = 0;

            if (kachel.equals(aktiveKachelGruppenarbeit)) {
                gruppenContainer.removeAll();
                gruppenLinie.setVisible(false);
                aktiveKachelGruppenarbeit.removeClassName("kachel-active");
                aktiveKachelGruppenarbeit = null;
                aktiveGruppenarbeit = null;
            } else {

                if (aktiveKachelGruppenarbeit != null) {
                    aktiveKachelGruppenarbeit.removeClassName("kachel-active");
                    aktiveGruppenarbeit= null;
                }

                updateGruppen(gruppenarbeit);

                kachel.addClassName("kachel-active");
                aktiveKachelGruppenarbeit = kachel;
                aktiveGruppenarbeit = gruppenarbeit;

            }
            gruppeBearbeitenDialog = new GruppeBearbeitenDialog(gruppenarbeit, gruppenarbeitService, gruppeService, authenticatedUser, this);
            gruppeBearbeitenDialog.setWidth("1500px");
        });

        return kachel;
    }

    /**
     * Erstellt eine visuelle Kachel (Div) zum Hinzufügen einer neuen Gruppenarbeit.
     * Die Kachel enthält ein Plus-Symbol und wird mit einem Klick-Listener ausgestattet, der einen Dialog zum Hinzufügen einer neuen Gruppenarbeit öffnet.
     * Sie hat auch Hover-Effekte, die durch Mouseover- und Mouseout-Events gesteuert werden.
     *
     * @return die erstellte Kachel als Div-Objekt
     *
     * @autor Joris
     */
    private Div createGruppenarbeitKachel() {
        Div plusSymbol = new Div();
        plusSymbol.setText("+");
        plusSymbol.addClassName("plus-symbol");

        Div neueGruppenarbeitKachel = new Div(plusSymbol);
        neueGruppenarbeitKachel.addClassName("neue-gruppenarbeit-kachel");

        neueGruppenarbeitKachel.getElement().addEventListener("mouseover", e ->
                neueGruppenarbeitKachel.addClassName("hover"));

        neueGruppenarbeitKachel.getElement().addEventListener("mouseout", e ->
                neueGruppenarbeitKachel.removeClassName("hover"));

        neueGruppenarbeitKachel.addClickListener(e ->
                gruppenarbeitHinzufuegenDialog.open());

        return neueGruppenarbeitKachel;
    }

    /**
     * Erstellt ein HorizontalLayout, das einen Text zwischen zwei horizontalen Linien (Hr-Elementen) enthält.
     * Die Linien und der Text werden mit spezifischen CSS-Klassen gestaltet.
     *
     * @param text der Text, der zwischen den Linien angezeigt werden soll
     * @return ein HorizontalLayout, das den formatierten Text zwischen zwei Linien enthält
     *
     * @autor Joris
     */
    private HorizontalLayout createLineWithText(String text) {
        Hr lineBefore = new Hr();
        lineBefore.addClassName("line-before");

        Hr lineAfter = new Hr();
        lineAfter.addClassName("line-after");

        Text lineText = new Text(text);

        HorizontalLayout lineWithText = new HorizontalLayout(lineBefore, lineText, lineAfter);
        lineWithText.addClassName("line-with-text");

        return lineWithText;
    }

    /**
     * Erstellt eine visuelle Kachel (Div) für eine gegebene Gruppe.
     * Die Kachel enthält Informationen über die Gruppe und eine Liste der Teilnehmer.
     * Jeder Teilnehmer in der Liste ist ein klickbares Element, das einen Dialog zur Auswertung der Gruppe öffnet.
     * Die Höhe der Teilnehmerliste wird dynamisch auf die maximale Höhe aller Gruppen für diese Gruppenarbeit gesetzt.
     *
     * @param gruppe die Gruppe, für die die Kachel erstellt werden soll
     * @return die erstellte Kachel als Div-Objekt
     *
     * @autor Joris
     */
    private Div gruppenKachel(Gruppe gruppe) {
        // Laden der Gruppe mit den Teilnehmern
        Gruppe fullGruppe;
        if (gruppeService.findGruppeByIdWithTeilnehmer(gruppe.getId()) != null) {
            fullGruppe = gruppeService.findGruppeByIdWithTeilnehmer(gruppe.getId());
        } else {
            fullGruppe = null;
        }

        Div gruppenInfo = new Div();
        assert fullGruppe != null;
        gruppenInfo.setText("Gruppe " + fullGruppe.getNummer());
        gruppenInfo.addClassName("text-center");

        // Erstellen der Teilnehmerliste
        VirtualList<Teilnehmer> teilnehmerList = new VirtualList<>();
        teilnehmerList.setItems(fullGruppe.getTeilnehmer());
        teilnehmerList.setRenderer(new ComponentRenderer<>(teilnehmer -> {

            Div teilnehmerDiv = createTeilnehmerDivGruppe(teilnehmer, fullGruppe.getGruppenarbeit());

            // Klick-Listener für Teilnehmer
            teilnehmerDiv.addClickListener(e -> {
                gruppeAuswertungDialog = new GruppeAuswertungDialog(teilnehmer, gruppe.getGruppenarbeit(), gruppenarbeitTeilnehmerService, this);
                gruppeAuswertungDialog.open();
            });

            return teilnehmerDiv;
        }));

        int itemHeight = 48;
        int listHeight = fullGruppe.getTeilnehmer().size() * itemHeight;

        if (listHeight > maxListHeight) {
            maxListHeight = listHeight;
        }
        // Setzen der Höhe der Liste auf die maximale Höhe aller Gruppen für diese Gruppenarbeit
        teilnehmerList.setHeight(maxListHeight + "px");

        // Hinzufügen der Teilnehmerliste zur Kachel
        Div kachelContent = new Div(gruppenInfo, teilnehmerList);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        kachel.addClassName("kachel-gruppen");

        return kachel;
    }

    /**
     * Erstellt ein Div-Element, das ein Löschsymbol (Mülleimer-Icon) darstellt.
     * Das Div-Element wird mit einer spezifischen CSS-Klasse gestaltet.
     *
     * @return das erstellte Div-Element mit dem Löschsymbol
     *
     * @autor Joris
     */
    private Div createDeleteIcon() {
        Div deleteIcon = new Div(LineAwesomeIcon.TRASH_ALT.create());
        deleteIcon.addClassName("delete-icon");
        return deleteIcon;
    }

    /**
     * Fügt dem Löschsymbol Funktionalität hinzu.
     * Wenn das Löschsymbol angeklickt wird, öffnet es einen Löschdialog und verbirgt das Bearbeitungssymbol.
     * Wenn der Löschdialog geschlossen wird, wird das Löschsymbol verborgen.
     *
     * @param deleteIcon das Div-Element, das das Löschsymbol darstellt
     * @param editIcon das Div-Element, das das Bearbeitungssymbol darstellt
     * @param deleteDialog der Dialog, der geöffnet wird, wenn das Löschsymbol angeklickt wird
     *
     * @autor Lilli
     */
    private void deleteIconFunctionality(Div deleteIcon, Div editIcon, Dialog deleteDialog){
        deleteIcon.getElement().addEventListener("click", e -> {
                    deleteDialog.open();
                    editIcon.getStyle().set("visibility", "hidden");
                }
        ).addEventData("event.stopPropagation()");
        deleteDialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                deleteIcon.getStyle().set("visibility", "hidden");
            }
        });
    }

    /**
     * Erstellt ein Div-Element, das ein Bearbeitungssymbol (Stift-Icon) darstellt.
     * Das Div-Element wird mit einer spezifischen CSS-Klasse gestaltet.
     *
     * @return das erstellte Div-Element mit dem Bearbeitungssymbol
     *
     * @autor Joris
     */
    private Div createEditIcon() {
        Div editIcon = new Div(LineAwesomeIcon.EDIT.create());
        editIcon.addClassName("edit-icon");
        return editIcon;
    }

    /**
     * Fügt dem Bearbeitungssymbol Funktionalität hinzu.
     * Wenn das Bearbeitungssymbol angeklickt wird, öffnet es einen Bearbeitungsdialog und verbirgt das Löschsymbol.
     * Wenn der Bearbeitungsdialog geschlossen wird, wird das Bearbeitungssymbol verborgen.
     *
     * @param editIcon das Div-Element, das das Bearbeitungssymbol darstellt
     * @param deleteIcon das Div-Element, das das Löschsymbol darstellt
     * @param editDialog der Dialog, der geöffnet wird, wenn das Bearbeitungssymbol angeklickt wird
     *
     * @autor Lilli
     */
    private void editIconFunctionality(Div editIcon, Div deleteIcon, Dialog editDialog){
        editIcon.getElement().addEventListener("click", e -> {
                    editDialog.open();
                    deleteIcon.getStyle().set("visibility", "hidden");
                }
        ).addEventData("event.stopPropagation()");
        editDialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                editIcon.getStyle().set("visibility", "hidden");
            }
        });
    }

    /**
     * Erstellt eine visuelle Liste (Div) aller Teilnehmer einer Veranstaltung.
     * Die Liste enthält eine Suchfunktion, die es ermöglicht, Teilnehmer nach Vor- und Nachnamen zu filtern.
     * Jeder Teilnehmer in der Liste ist ein klickbares Element, das ein Div-Element für diesen Teilnehmer erstellt.
     * Die Liste enthält auch einen Button zum Hinzufügen neuer Teilnehmer, der einen Dialog zum Hinzufügen von Teilnehmern öffnet.
     *
     * @return die erstellte Teilnehmerliste als Div-Objekt
     *
     * @autor Joris
     */
    private Div createTeilnehmerListe() {
        Div teilnehmerListe = new Div();
        teilnehmerListe.addClassName("teilnehmer-liste");
        teilnehmerListe.getStyle().set("overflow-y", "hidden");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Suche...");
        searchField.setWidthFull();
        searchField.getStyle().set("margin-top", "-10px");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        Veranstaltung v = null;
        Set<Teilnehmer> teilnehmer;

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            v = veranstaltungService.findVeranstaltungById(Long.parseLong(veranstaltungIdString), user);
        }

        assert v != null;
        if (v.getTeilnehmer() != null) {
            teilnehmer = v.getTeilnehmer();
        } else {
            teilnehmer = null;
        }

        Div teilnehmerItems = new Div();
        assert teilnehmer != null;
        if (teilnehmer.isEmpty()) {
            teilnehmerItems.setText("Noch keine Teilnehmer in der Veranstaltung, fügen Sie noch welche hinzu.");
            teilnehmerItems.getStyle().set("text-align", "center");
        } else {
            for (Teilnehmer t : teilnehmer) {
                Div teilnehmerDiv = createTeilnehmerDiv(t);
                teilnehmerItems.add(teilnehmerDiv);
            }
        }
        Div scrollableList = new Div(teilnehmerItems);
        scrollableList.getStyle().set("overflow-y", "auto");
        scrollableList.getStyle().set("height", "calc(100% - 70px)");

        searchField.addValueChangeListener(e -> {
            String searchTerm = e.getValue().toLowerCase();
            teilnehmerItems.removeAll();
            for (Teilnehmer t : teilnehmer) {
                if (t.getVorname().toLowerCase().contains(searchTerm) || t.getNachname().toLowerCase().contains(searchTerm)) {
                    Div teilnehmerDiv = createTeilnehmerDiv(t);
                    teilnehmerItems.add(teilnehmerDiv);
                }
            }
        });

        Button teilnehmerHinzufuegenButton = new Button();
        teilnehmerHinzufuegenButton.setText("Teilnehmer hinzufügen");
        teilnehmerHinzufuegenButton.setId("teilnehmer-hinzufuegen-button");
        teilnehmerHinzufuegenButton.setWidthFull();

        teilnehmerHinzufuegenButton.addClickListener(e -> {
            teilnehmerHinzufuegenDialog.updateGrid();
            teilnehmerHinzufuegenDialog.open();
        });

        teilnehmerListe.add(searchField, scrollableList, teilnehmerHinzufuegenButton);

        return teilnehmerListe;
    }

    /**
     * Erstellt ein Div-Element, das einen Teilnehmer und seine Punkte in einer Gruppenarbeit darstellt.
     * Das Div-Element enthält den Anfangsbuchstaben des Vor- und Nachnamens des Teilnehmers, den vollständigen Namen des Teilnehmers und die Punkte, die der Teilnehmer in der Gruppenarbeit erzielt hat.
     * Wenn die Gruppenarbeit null ist oder der Teilnehmer keine Punkte in der Gruppenarbeit hat, wird kein Punktwert angezeigt.
     *
     * @param t der Teilnehmer, der in dem Div-Element dargestellt werden soll
     * @param gruppenarbeit die Gruppenarbeit, in der die Punkte des Teilnehmers angezeigt werden sollen
     * @return das erstellte Div-Element, das den Teilnehmer und seine Punkte darstellt
     *
     * @autor Joris
     */
    private Div createTeilnehmerDivGruppe(Teilnehmer t, Gruppenarbeit gruppenarbeit) {
        Div teilnehmerDiv = new Div();
        teilnehmerDiv.addClassName("teilnehmer-item-gruppe");

        Span profilbild = new Span();
        profilbild.setText(t.getVorname().charAt(0) + "" + t.getNachname().charAt(0));
        profilbild.addClassName("profilbild");

        Span name = new Span(t.getVorname() + " " + t.getNachname());
        name.addClassName("teilnehmer-name-gruppe");

        Div punkteDiv = new Div();
        punkteDiv.addClassName("punkte-div");

        if (gruppenarbeit != null) {
            Float punkte = gruppenarbeitTeilnehmerService.findPunkteByMatrikelNrAndGruppenarbeitId(t.getId(), gruppenarbeit.getId());

            if (punkte != null) {
                punkteDiv.setText(punkte.toString());
            }
        }

        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setWidthFull();
        layout.add(profilbild, name);

        Div spacer = new Div();
        layout.add(spacer);
        layout.setFlexGrow(1, spacer);
        layout.add(punkteDiv);

        teilnehmerDiv.add(layout);


        return teilnehmerDiv;
    }

    /**
     * Erstellt ein Div-Element, das einen Teilnehmer darstellt.
     * Das Div-Element enthält den Anfangsbuchstaben des Vor- und Nachnamens des Teilnehmers, den vollständigen Namen des Teilnehmers und ein Löschsymbol.
     * Wenn das Div-Element mit der Maus überfahren wird, wird das Löschsymbol sichtbar.
     * Wenn das Löschsymbol angeklickt wird, öffnet es einen Dialog zum Entfernen des Teilnehmers.
     *
     * @param t der Teilnehmer, der in dem Div-Element dargestellt werden soll
     * @return das erstellte Div-Element, das den Teilnehmer darstellt
     *
     * @autor Joris
     */
    private Div createTeilnehmerDiv(Teilnehmer t) {
        Div teilnehmerDiv = new Div();
        teilnehmerDiv.addClassName("teilnehmer-item-liste");

        Span profilbild = new Span();
        profilbild.setText(t.getVorname().charAt(0) + "" + t.getNachname().charAt(0));
        profilbild.addClassName("profilbild");

        Span name = new Span(t.getVorname() + " " + t.getNachname());
        name.addClassName("teilnehmer-name-liste");

        Div deleteIcon = new Div(LineAwesomeIcon.TRASH_ALT.create());
        deleteIcon.getStyle().set("visibility", "hidden");
        deleteIcon.getStyle().set("margin-right", "8px");
        deleteIcon.getStyle().set("cursor", "pointer");
        deleteIcon.getStyle().set("color", "red");

        teilnehmerDiv.getElement().addEventListener("mouseover", e -> deleteIcon.getStyle().set("visibility", "visible"));

        teilnehmerDiv.getElement().addEventListener("mouseout", e -> deleteIcon.getStyle().set("visibility", "hidden"));

        deleteIcon.getElement().addEventListener("click", e -> {
            teilnehmerEntfernenDialog.setTeilnehmer(t);
            teilnehmerEntfernenDialog.open();
            deleteIcon.getStyle().set("visibility", "hidden");
        });


        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setWidthFull();
        layout.add(profilbild, name);

        Div spacer = new Div();
        layout.add(spacer);
        layout.setFlexGrow(1, spacer);
        layout.add(deleteIcon);

        teilnehmerDiv.add(layout);

        return teilnehmerDiv;
    }

    /**
     * Erstellt einen Button, der ein Bearbeitungssymbol (Stift-Icon) darstellt.
     * Der Button wird mit einer spezifischen CSS-Klasse gestaltet.
     * Derzeit führt ein Klick auf den Button zu keiner Aktion, da der Klick-Listener leer ist.
     *
     * @return der erstellte Button mit dem Bearbeitungssymbol
     *
     * @autor Joris
     */
    private Button createEditButton() {
        Button editButton = new Button();
        editButton.setIcon(LineAwesomeIcon.EDIT.create());
        editButton.addClassName("edit-button");

        editButton.addClickListener(e -> {

        });

        return editButton;
    }

    /**
     * Wendet einen Filter auf die Liste der Veranstaltungstermine an, basierend auf der ausgewählten Option des FilterButtons.
     * Der Filter sortiert die Veranstaltungstermine entweder nach Datum oder Titel in aufsteigender oder absteigender Reihenfolge.
     * Nach Anwendung des Filters aktualisiert die Methode die visuelle Darstellung der Veranstaltungstermine im veranstaltungsterminContainer.
     *
     * @autor Joris
     */
    private void applyVeranstaltungsterminFilter() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            termine = veranstaltungsterminService.findVeranstaltungstermineByVeranstaltungId(Long.parseLong(veranstaltungIdString), user);
        }

        String value = filterButton.getValue();
        if (value != null) {
            switch (value) {
                case "Datum aufsteigend" -> termine.sort(Comparator.comparing(Veranstaltungstermin::getDatum));
                case "Datum absteigend" -> termine.sort(Comparator.comparing(Veranstaltungstermin::getDatum).reversed());
                case "Titel A-Z" -> termine.sort(Comparator.comparing(Veranstaltungstermin::getTitel));
                case "Titel Z-A" -> termine.sort(Comparator.comparing(Veranstaltungstermin::getTitel).reversed());
            }

            Veranstaltungstermin aktiverTermin = veranstaltungsterminMap.get(aktiveKachelVeranstaltungstermin);

            veranstaltungsterminContainer.removeAll();
            for (Veranstaltungstermin termin : termine) {
                Div kachel = veranstaltungsterminKachel(termin);
                veranstaltungsterminContainer.add(kachel);

                if (termin.equals(aktiverTermin)) {
                    aktiveKachelVeranstaltungstermin = kachel;
                    aktiveKachelVeranstaltungstermin.addClassName("kachel-active");
                    aktiverVeranstaltungstermin = aktiverTermin;
                }
            }
            veranstaltungsterminContainer.add(createVeranstaltungsterminKachel());
        }
    }

    /**
     * Aktualisiert die Darstellung der Gruppenarbeiten für einen gegebenen Veranstaltungstermin.
     * Die Methode leert zunächst den gruppenarbeitContainer, holt dann alle Gruppenarbeiten für den gegebenen Veranstaltungstermin und sortiert sie nach Titel.
     * Für jede Gruppenarbeit wird eine visuelle Kachel erstellt und zum gruppenarbeitContainer hinzugefügt.
     * Wenn die Gruppenarbeit die aktuell ausgewählte Gruppenarbeit ist, wird die Kachel als aktiv markiert.
     * Nachdem alle Gruppenarbeiten hinzugefügt wurden, wird eine zusätzliche Kachel zum Hinzufügen neuer Gruppenarbeiten erstellt und hinzugefügt.
     * Schließlich wird der gruppenarbeitContainer sichtbar gemacht und ein Dialog zum Hinzufügen neuer Gruppenarbeiten erstellt.
     * Wenn vorhanden, wird die Darstellung der Gruppenlinie und des gruppenContainers ausgeblendet und geleert.
     *
     * @param veranstaltungstermin der Veranstaltungstermin, für den die Gruppenarbeiten aktualisiert werden sollen
     *
     * @autor Joris
     */
    public void updateGruppenarbeiten(Veranstaltungstermin veranstaltungstermin) {
        gruppenarbeitContainer.removeAll();

        Gruppenarbeit aGruppenarbeit = gruppenarbeitMap.get(aktiveKachelGruppenarbeit);

        List<Gruppenarbeit> gruppenarbeiten = veranstaltungstermin.getGruppenarbeiten();
        gruppenarbeiten.sort(Comparator.comparing(Gruppenarbeit::getTitel));

        for (Gruppenarbeit gruppenarbeit : gruppenarbeiten) {
            Div kachel = gruppenarbeitKachel(gruppenarbeit);
            gruppenarbeitContainer.add(kachel);

            if (gruppenarbeit.equals(aGruppenarbeit)) {
                aktiveKachelGruppenarbeit = kachel;
                aktiveKachelGruppenarbeit.addClassName("kachel-active");
                aktiveGruppenarbeit = aGruppenarbeit;
            }
        }

        gruppenarbeitContainer.add(createGruppenarbeitKachel());

        gruppenarbeitLinie.setVisible(true);
        gruppenarbeitContainer.setVisible(true);

        gruppenarbeitHinzufuegenDialog = createGruppenarbeitHinzufuegenDialog(veranstaltungstermin);

        if (gruppenLinie != null) {
            gruppenLinie.setVisible(false);
            gruppenContainer.removeAll();
        }
        gruppenContainer.setVisible(false);

    }

    /**
     * Aktualisiert die Darstellung der Gruppen für eine gegebene Gruppenarbeit.
     * Die Methode leert zunächst den gruppenContainer und erstellt dann eine visuelle Darstellung für jede Gruppe in der Gruppenarbeit.
     * Wenn die Gruppenarbeit keine Gruppen hat, wird eine Nachricht angezeigt, dass keine Gruppen vorhanden sind.
     * Zusätzlich wird eine Linie mit dem Text "Gruppen" und einem Bearbeitungsbutton erstellt, wenn sie noch nicht existiert.
     * Der Bearbeitungsbutton öffnet einen Dialog zum Bearbeiten der Gruppen.
     * Schließlich wird der gruppenContainer und die Linie sichtbar gemacht.
     *
     * @param gruppenarbeit die Gruppenarbeit, für die die Gruppen aktualisiert werden sollen
     *
     * @autor Joris
     */
    public void updateGruppen (Gruppenarbeit gruppenarbeit) {
        gruppenContainer.removeAll();

        if (gruppenLinie == null) {
            HorizontalLayout lineWithText = createLineWithText("Gruppen");

            Button editButton = createEditButton();
            editButton.addClickListener(event -> gruppeBearbeitenDialog.open());

            Hr lineAfter = new Hr();
            lineAfter.addClassName("line-after-icon");

            gruppenLinie = new HorizontalLayout(lineWithText, editButton, lineAfter);
            gruppenLinie.setAlignItems(Alignment.CENTER);
            gruppenLinie.setJustifyContentMode(JustifyContentMode.BETWEEN);
            gruppenLinie.setWidthFull();

            contentLayout.add(gruppenLinie, gruppenContainer);
        }
        Gruppenarbeit fullGruppenarbeit;

        if (gruppenarbeit != null) {
            fullGruppenarbeit = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId());
        } else {
            return;
        }

        assert fullGruppenarbeit != null;
        if (fullGruppenarbeit.getGruppen() != null) {
            for (Gruppe gruppe : fullGruppenarbeit.getGruppen()) {
                gruppenContainer.add(gruppenKachel(gruppe));
            }
        } else {
            Span noGruppen = new Span("Keine Gruppen vorhanden");
            gruppenContainer.add(noGruppen);
        }

        gruppenLinie.setVisible(true);
        gruppenContainer.setVisible(true);
    }

    /**
     * Überprüft, ob eine Veranstaltungstermin-Kachel aktiv ist und führt dann eine Aktion aus.
     * Wenn eine Veranstaltungstermin-Kachel aktiv ist, wird der entsprechende Veranstaltungstermin aus der Map geholt.
     * Anschließend wird der aktualisierte Veranstaltungstermin aus dem VeranstaltungsterminService geholt, indem die ID des geholten Veranstaltungstermins verwendet wird.
     * Schließlich wird die Methode updateGruppenarbeiten mit dem aktualisierten Veranstaltungstermin aufgerufen.
     *
     * @autor Joris
     */
    public void triggerVeranstaltungsterminKachelClick () {
        if (aktiveKachelVeranstaltungstermin != null) {
            Veranstaltungstermin termin = veranstaltungsterminMap.get(aktiveKachelVeranstaltungstermin);

            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                if (!(veranstaltungsterminService.findAllVeranstaltungstermine(user).isEmpty())) {

                    Optional<Veranstaltungstermin> updatedTermin = veranstaltungsterminService.findVeranstaltungsterminById(termin.getId());

                    updatedTermin.ifPresent(this::updateGruppenarbeiten);
                }
            }
        } else {
            gruppenarbeitContainer.removeAll();
            if (gruppenarbeitLinie != null) {
                gruppenarbeitLinie.setVisible(false);
            }
        }
    }

    /**
     * Überprüft, ob eine Gruppenarbeit-Kachel aktiv ist und führt dann eine Aktion aus.
     * Wenn eine Gruppenarbeit-Kachel aktiv ist, wird die entsprechende Gruppenarbeit aus der Map geholt.
     * Anschließend wird die aktualisierte Gruppenarbeit aus dem GruppenarbeitService geholt, indem die ID der geholten Gruppenarbeit verwendet wird.
     * Schließlich wird die Methode updateGruppen mit der aktualisierten Gruppenarbeit aufgerufen.
     *
     * @autor Joris
     */
    public void triggerGruppenarbeitKachelClick () {
        if (aktiveKachelGruppenarbeit != null) {
            Gruppenarbeit gruppenarbeit = gruppenarbeitMap.get(aktiveKachelGruppenarbeit);

            Gruppenarbeit updatedGruppenarbeit = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId());

            if (aktiveGruppenarbeit != null && aktiveGruppenarbeit.getId().equals(gruppenarbeit.getId())) {
                updateGruppen(updatedGruppenarbeit);
            }
        } else {
            gruppenContainer.removeAll();
            if (gruppenLinie != null) {
                gruppenLinie.setVisible(false);
            }
        }
    }

    /**
     * Setzt die aktive Veranstaltungstermin-Kachel basierend auf einem gegebenen Veranstaltungstermin.
     * Die Methode durchläuft alle Einträge in der Map von Veranstaltungstermin-Kacheln und sucht nach dem gegebenen Veranstaltungstermin.
     * Wenn der Veranstaltungstermin gefunden wird, wird die entsprechende Kachel als aktive Kachel gesetzt und erhält die CSS-Klasse "kachel-active".
     * Zusätzlich wird der aktive Veranstaltungstermin auf den gegebenen Veranstaltungstermin gesetzt.
     *
     * @param termin der Veranstaltungstermin, der als aktiver Veranstaltungstermin gesetzt werden soll
     *
     * @autor Joris
     */
    public void setAktiveKachelVeranstaltungstermin (Veranstaltungstermin termin) {
        for (Map.Entry<Div, Veranstaltungstermin> entry : veranstaltungsterminMap.entrySet()) {
            if (entry.getValue().equals(termin)) {
                aktiveKachelVeranstaltungstermin = entry.getKey();
                aktiveKachelVeranstaltungstermin.addClassName("kachel-active");
                aktiverVeranstaltungstermin = termin;
                break;
            }
        }
    }

    /**
     * Setzt die aktive Gruppenarbeit-Kachel basierend auf einer gegebenen Gruppenarbeit.
     * Die Methode holt zunächst den Veranstaltungstermin der gegebenen Gruppenarbeit.
     * Wenn der Veranstaltungstermin nicht null ist, holt sie die Liste der Gruppenarbeiten für diesen Veranstaltungstermin.
     * Sie durchläuft dann jede Gruppenarbeit in der Liste und sucht nach der gegebenen Gruppenarbeit.
     * Wenn die Gruppenarbeit gefunden wird, durchläuft sie die Map von Gruppenarbeit-Kacheln und sucht nach der gefundenen Gruppenarbeit.
     * Wenn die Gruppenarbeit gefunden wird, wird die entsprechende Kachel als aktive Kachel gesetzt und erhält die CSS-Klasse "kachel-active".
     * Zusätzlich wird die aktive Gruppenarbeit auf die gefundenen Gruppenarbeit gesetzt.
     *
     * @param gruppenarbeit die Gruppenarbeit, die als aktive Gruppenarbeit gesetzt werden soll
     *
     * @autor Joris
     */
    public void setAktiveKachelGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        // Überprüfen, ob die übergebene Gruppenarbeit die gleiche ist wie die aktuell aktive Gruppenarbeit
        if (aktiveGruppenarbeit != null && aktiveGruppenarbeit.getId().equals(gruppenarbeit.getId())) {
            return;
        }

        // Ansonsten setzen wir die aktive Gruppenarbeit neu
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            if (!veranstaltungsterminService.findAllVeranstaltungstermine(user).isEmpty()) {
                Optional<Veranstaltungstermin> neuerVeranstaltungstermin = veranstaltungsterminService.findVeranstaltungsterminById(gruppenarbeit.getVeranstaltungstermin().getId());

                if (neuerVeranstaltungstermin.isPresent()) {
                    List<Gruppenarbeit> aktuelleGruppenarbeiten = neuerVeranstaltungstermin.get().getGruppenarbeiten();

                    for (Gruppenarbeit aktuelleGruppenarbeit : aktuelleGruppenarbeiten) {
                        if (aktuelleGruppenarbeit.getId().equals(gruppenarbeit.getId())) {
                            for (Map.Entry<Div, Gruppenarbeit> entry : gruppenarbeitMap.entrySet()) {
                                if (entry.getValue().equals(aktuelleGruppenarbeit)) {
                                    // Entfernen der "kachel-active" Klasse von der vorher aktiven Kachel
                                    if (aktiveKachelGruppenarbeit != null) {
                                        aktiveKachelGruppenarbeit.removeClassName("kachel-active");
                                    }

                                    // Setzen der neuen aktiven Kachel und Hinzufügen der "kachel-active" Klasse
                                    aktiveKachelGruppenarbeit = entry.getKey();
                                    aktiveKachelGruppenarbeit.addClassName("kachel-active");

                                    // Setzen der neuen aktiven Gruppenarbeit
                                    aktiveGruppenarbeit = aktuelleGruppenarbeit;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}