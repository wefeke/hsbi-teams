package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.models.*;
import com.example.application.views.MainLayout;
import com.example.application.views.gruppe.GruppeBearbeitenDialog;
import com.example.application.views.gruppenarbeit.GruppeAuswertungDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitBearbeitenDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitHinzufuegenDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitLoeschenDialog;
import com.example.application.views.studierende.StudierendeView;
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
public class VeranstaltungDetailView extends VerticalLayout implements HasUrlParameter<String> {

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

    //UI Elements
    private final Div veranstaltungsterminContainer;
    private final Div gruppenarbeitContainer;
    private final Div gruppenContainer;
    private final H1 veranstaltungTitle;
    private final Div teilnehmerListe;
    private final Button toggleTeilnehmerListeButton;
    private final Select<String> filterButton;

    //Dialog Instance
    private VeranstaltungsterminDialog veranstaltungsterminDialog;
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

   // private final TeilnehmerErstellenDialog teilnehmerErstellenDialog;

    public VeranstaltungDetailView(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, GruppeService gruppeService, GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService, AuthenticatedUser authenticatedUser) {
        // Initialisierung der Services
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.teilnehmerService = teilnehmerService;

        //this.teilnehmerErstellenDialog= teilnehmerErstellenDialog;


        // Initialisierung der UI-Elemente
        this.teilnehmerListe = new Div();
        //Layout
        VerticalLayout mainLayoutLeft = new VerticalLayout();
        this.veranstaltungTitle = new H1();
        this.toggleTeilnehmerListeButton = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        this.veranstaltungsterminContainer = new Div();
        this.gruppenarbeitContainer = new Div();
        this.gruppenContainer = new Div();
        this.contentLayout = new VerticalLayout();

        // Konfiguration des Hauptlayouts
        mainLayoutLeft.setSizeFull();

        Div leftContainer = new Div();
        leftContainer.addClassName("left-container");
        leftContainer.add(mainLayoutLeft);

        Div rightContainer = new Div();
        rightContainer.addClassName("right-container");
        rightContainer.add(teilnehmerListe);

        HorizontalLayout mainLayout = new HorizontalLayout(leftContainer, rightContainer);
        mainLayout.setSizeFull();
        mainLayout.getStyle().set("overflow", "hidden"); // Hide the overflow content

        VerticalLayout titleContainer = new VerticalLayout();
        Div contentContainer = new Div();
        contentContainer.getStyle().set("width", "calc(100% - 400px)");

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

    public void init() {
        veranstaltungTitle.setText(veranstaltung.getTitel());

        for (Veranstaltungstermin termin : termine) {
            veranstaltungsterminContainer.add(veranstaltungsterminKachel(termin));
        }

        veranstaltungsterminContainer.add(createVeranstaltungsterminKachel());

        teilnehmerListe.add(createTeilnehmerListe());

    }

    public void update() {

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            termine = veranstaltungsterminService.findVeranstaltungstermineByVeranstaltungId(veranstaltung.getId(), user);
        }

        triggerVeranstaltungsterminKachelClick();
        triggerGruppenarbeitKachelClick();

        applyVeranstaltungsterminFilter();
    }

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
        terminNotiz.setText(veranstaltungstermin.getTitel());
        terminNotiz.addClassName("termin-notiz");

        Div kachelContent = new Div(terminDatum, terminZeit, terminNotiz);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        veranstaltungsterminMap.put(kachel, veranstaltungstermin);
        kachel.addClassName("kachel");

        VeranstaltungsterminBearbeiten editDialog = new VeranstaltungsterminBearbeiten(veranstaltungService, veranstaltungsterminService, this, veranstaltungIdString, veranstaltungstermin.getId(), authenticatedUser);

        //Delete Icon
        Div deleteIcon = createDeleteIcon(veranstaltungsterminLoeschenDialog);
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
                aktiveKachelVeranstaltungstermin = null;
            } else {

                if (aktiveKachelVeranstaltungstermin != null) {
                    aktiveKachelVeranstaltungstermin.removeClassName("kachel-active");
                }

                gruppenarbeitContainer.removeAll();


                if (gruppenarbeitLinie == null) {
                    gruppenarbeitLinie = createLineWithText("Gruppenarbeiten");
                    contentLayout.add(gruppenarbeitLinie, gruppenarbeitContainer);
                }

                updateGruppenarbeiten(veranstaltungstermin);

                kachel.addClassName("kachel-active");
                aktiveKachelVeranstaltungstermin = kachel;

            }

            if (gruppenLinie != null) {
                gruppenLinie.setVisible(false);
                gruppenContainer.removeAll();
            }
            gruppenContainer.setVisible(false);
        });

        return kachel;
    }

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
                veranstaltungsterminDialog.open()
        );

        return neueVeranstaltungsterminKachel;
    }


    public void createVeranstaltungsterminDialog () {
        veranstaltungsterminDialog = new VeranstaltungsterminDialog(veranstaltungService, veranstaltungsterminService, this, veranstaltungIdString, authenticatedUser);
    }

    //Lilli
    public GruppenarbeitHinzufuegenDialog createGruppenarbeitHinzufuegenDialog(Veranstaltungstermin veranstaltungstermin) {
        GruppenarbeitHinzufuegenDialog dialog = new GruppenarbeitHinzufuegenDialog(authenticatedUser, veranstaltungIdString, gruppenarbeitService, teilnehmerService, veranstaltungsterminService, gruppeService, this, veranstaltungService, veranstaltungstermin);
        dialog.setWidth("1500px");
        return dialog;
    }

    //Lilli
    public void createGruppenarbeitBearbeitenDialog() {
        gruppenarbeitBearbeitenDialog = new GruppenarbeitBearbeitenDialog(gruppenarbeitService);
    }

    //Lilli
    public void createGruppenarbeitLoeschenDialog() {
        gruppenarbeitLoeschenDialog = new GruppenarbeitLoeschenDialog(gruppenarbeitService, gruppeService, veranstaltungsterminService);
    }

    //Lilli
    private void createVeranstaltungsterminLoeschenDialog() {
        veranstaltungsterminLoeschenDialog = new VeranstaltungsterminLoeschenDialog(veranstaltung, gruppeService, gruppenarbeitService, veranstaltungsterminService, veranstaltungService);
    }

    public void createTeilnehmerHinzufuegenDialog() {
        teilnehmerHinzufuegenDialog = new TeilnehmerHinzufuegenDialog(veranstaltungService, teilnehmerService, veranstaltung.getId(), authenticatedUser);
    }

    public void createTeilnehmerEntfernenDialog() {
        teilnehmerEntfernenDialog = new TeilnehmerEntfernenDialog(veranstaltungService, teilnehmerService, veranstaltung.getId(), authenticatedUser);
    }

    private Div gruppenarbeitKachel(Gruppenarbeit gruppenarbeit) {
        Div gruppenarbeitInfo = new Div();
        gruppenarbeitInfo.setText(gruppenarbeit.getTitel());
        gruppenarbeitInfo.addClassName("text-center");

        Div kachelContent = new Div(gruppenarbeitInfo);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        gruppenarbeitMap.put(kachel, gruppenarbeit);
        kachel.addClassName("kachel");

        //Tooltip
        String tooltipText = "Titel: " + gruppenarbeit.getTitel() + "\nBeschreibung: " + gruppenarbeit.getBeschreibung();
        kachel.getElement().setProperty("title", tooltipText);

        Div deleteIconGruppenarbeit = createDeleteIcon(gruppenarbeitLoeschenDialog);
        Div editIconGruppenarbeit = createEditIcon(gruppenarbeitBearbeitenDialog, deleteIconGruppenarbeit);

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
            } else {

                if (aktiveKachelGruppenarbeit != null) {
                    aktiveKachelGruppenarbeit.removeClassName("kachel-active");
                }

                updateGruppen(gruppenarbeit);

                kachel.addClassName("kachel-active");
                aktiveKachelGruppenarbeit = kachel;

            }
            gruppeBearbeitenDialog = new GruppeBearbeitenDialog(gruppenarbeit, gruppenarbeitService, gruppeService, authenticatedUser);
            gruppeBearbeitenDialog.setWidth("1500px");
        });

        return kachel;
    }

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

    private Div gruppenKachel(Gruppe gruppe) {
        // Laden der Gruppe mit den Teilnehmern
        Gruppe fullGruppe = gruppeService.findGruppeByIdWithTeilnehmer(gruppe.getId());

        Div gruppenInfo = new Div();
        gruppenInfo.setText("Gruppe " + fullGruppe.getNummer());
        gruppenInfo.addClassName("text-center");

        // Erstellen der Teilnehmerliste
        VirtualList<Teilnehmer> teilnehmerList = new VirtualList<>();
        teilnehmerList.setItems(fullGruppe.getTeilnehmer());
        teilnehmerList.setRenderer(new ComponentRenderer<>(teilnehmer -> {

            Div teilnehmerDiv = createTeilnehmerDivGruppe(teilnehmer, fullGruppe.getGruppenarbeit());

            // Klick-Listener für Teilnehmer
            teilnehmerDiv.addClickListener(e -> {
                gruppeAuswertungDialog = new GruppeAuswertungDialog(teilnehmer,fullGruppe.getGruppenarbeit(), gruppenarbeitTeilnehmerService, this);
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

    private Div createDeleteIcon(Dialog confirmationDialog) {
        Div deleteIcon = new Div(LineAwesomeIcon.TRASH_ALT.create());
        deleteIcon.addClassName("delete-icon");
        return deleteIcon;
    }

    //Lilli
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

    private Div createEditIcon(Dialog editDialog, Div deleteIcon) {
        Div editIcon = new Div(LineAwesomeIcon.EDIT.create());
        editIcon.addClassName("edit-icon");
        return editIcon;
    }

    //Lilli
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

        Set<Teilnehmer> teilnehmer = veranstaltung.getTeilnehmer();

        Div teilnehmerItems = new Div();
        for (Teilnehmer t : teilnehmer) {
            Div teilnehmerDiv = createTeilnehmerDiv(t);
            teilnehmerItems.add(teilnehmerDiv);
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
        teilnehmerHinzufuegenButton.setWidthFull();

        teilnehmerHinzufuegenButton.addClickListener(e -> teilnehmerHinzufuegenDialog.open());

        teilnehmerListe.add(searchField, scrollableList, teilnehmerHinzufuegenButton);

        return teilnehmerListe;
    }

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

    private Button createEditButton() {
        Button editButton = new Button();
        editButton.setIcon(LineAwesomeIcon.EDIT.create());
        editButton.addClassName("edit-button");

        editButton.addClickListener(e -> {

        });

        return editButton;
    }

    private void applyVeranstaltungsterminFilter() {
        String value = filterButton.getValue();
        if (value != null) {
            // Sortieren Sie die Veranstaltungstermine basierend auf der ausgewählten Option
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
                    kachel.addClassName("kachel-active");
                }
            }
            veranstaltungsterminContainer.add(createVeranstaltungsterminKachel());
        }
    }

    public void updateGruppenarbeiten(Veranstaltungstermin veranstaltungstermin) {

        gruppenarbeitContainer.removeAll();

        Gruppenarbeit aktiveGruppenarbeit = gruppenarbeitMap.get(aktiveKachelGruppenarbeit);

        List<Gruppenarbeit> gruppenarbeiten = veranstaltungstermin.getGruppenarbeiten();
        gruppenarbeiten.sort(Comparator.comparing(Gruppenarbeit::getTitel));

        for (Gruppenarbeit gruppenarbeit : gruppenarbeiten) {
            Div kachel = gruppenarbeitKachel(gruppenarbeit);
            gruppenarbeitContainer.add(kachel);

            if (gruppenarbeit.equals(aktiveGruppenarbeit)) {
                aktiveKachelGruppenarbeit = kachel;
                aktiveKachelGruppenarbeit.addClassName("kachel-active");
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

    public void updateGruppen (Gruppenarbeit gruppenarbeit) {

        gruppenContainer.removeAll();

        if (gruppenLinie == null) {
            HorizontalLayout lineWithText = createLineWithText("Gruppen");

            Button editButton = createEditButton();
            //Lilli
            editButton.addClickListener(event -> {
                gruppeBearbeitenDialog.open();
            });

            Hr lineAfter = new Hr();
            lineAfter.addClassName("line-after-icon");

            gruppenLinie = new HorizontalLayout(lineWithText, editButton, lineAfter);
            gruppenLinie.setAlignItems(Alignment.CENTER);
            gruppenLinie.setJustifyContentMode(JustifyContentMode.BETWEEN);
            gruppenLinie.setWidthFull();

            contentLayout.add(gruppenLinie, gruppenContainer);
        }

        Gruppenarbeit fullGruppenarbeit = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId());

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

    public void triggerVeranstaltungsterminKachelClick () {
        if (aktiveKachelVeranstaltungstermin != null) {
            Veranstaltungstermin termin = veranstaltungsterminMap.get(aktiveKachelVeranstaltungstermin);
            updateGruppenarbeiten(termin);
        }
    }

    public void triggerGruppenarbeitKachelClick () {
        if (aktiveKachelGruppenarbeit != null) {
            Gruppenarbeit gruppenarbeit = gruppenarbeitMap.get(aktiveKachelGruppenarbeit);
            updateGruppen(gruppenarbeit);
        }
    }

    public void setAktiveKachelVeranstaltungstermin (Veranstaltungstermin termin) {
        for (Map.Entry<Div, Veranstaltungstermin> entry : veranstaltungsterminMap.entrySet()) {
            if (entry.getValue().equals(termin)) {
                aktiveKachelVeranstaltungstermin = entry.getKey();
                aktiveKachelVeranstaltungstermin.addClassName("kachel-active");
                break;
            }
        }
    }

    public void setAktiveKachelGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        for (Map.Entry<Div, Gruppenarbeit> entry : gruppenarbeitMap.entrySet()) {
            if (entry.getValue().equals(gruppenarbeit)) {
                aktiveKachelGruppenarbeit = entry.getKey();
                aktiveKachelGruppenarbeit.addClassName("kachel-active");
                break;
            }
        }
    }
}