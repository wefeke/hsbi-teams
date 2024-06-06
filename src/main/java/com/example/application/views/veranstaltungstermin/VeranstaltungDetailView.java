package com.example.application.views.veranstaltungstermin;

import com.example.application.models.Gruppenarbeit;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.models.*;
import com.example.application.views.MainLayout;
import com.example.application.views.gruppenarbeit.GruppeAuswertungDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitBearbeitenDialog;
import com.example.application.views.gruppenarbeit.GruppenarbeitHinzufuegenDialog;
import com.example.application.views.veranstaltungen.VeranstaltungBearbeiten;
import com.example.application.views.gruppenarbeit.GruppenarbeitLoeschenDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
    private final TeilnehmerGruppenarbeitService teilnehmerGruppenarbeitService;

    //Data
    private String veranstaltungIdString;
    private Veranstaltung veranstaltung;
    private List<Veranstaltungstermin> termine;
    private Div aktiveKachelVeranstaltungstermin = null;
    private Div aktiveKachelGruppenarbeit = null;
    private int maxListHeight = 0;
    private final AuthenticatedUser authenticatedUser;

    //UI Elements
    private final Div veranstaltungsterminContainer;
    private final Div gruppenarbeitContainer;
    private final Div gruppenContainer;
    private final H1 veranstaltungTitle;
    private final Div teilnehmerListe;
    private final Button toggleTeilnehmerListeButton;

    //Dialog Instance
    private VeranstaltungsterminDialog veranstaltungsterminDialog;
    private GruppenarbeitHinzufuegenDialog gruppenarbeitHinzufuegenDialog;
    private GruppenarbeitBearbeitenDialog gruppenarbeitBearbeitenDialog;
    private GruppenarbeitLoeschenDialog gruppenarbeitLoeschenDialog;
    private GruppeAuswertungDialog gruppeAuswertungDialog;
    private TeilnehmerHinzufuegenDialog teilnehmerHinzufuegenDialog;

    //Layout
    private final VerticalLayout mainLayoutLeft;
    private HorizontalLayout gruppenarbeitLinie;
    private HorizontalLayout gruppenLinie;

    public VeranstaltungDetailView(VeranstaltungenService veranstaltungService, VeranstaltungsterminService veranstaltungsterminService, GruppenarbeitService gruppenarbeitService, TeilnehmerService teilnehmerService, GruppeService gruppeService,TeilnehmerGruppenarbeitService teilnehmerGruppenarbeitService, AuthenticatedUser authenticatedUser) {
        this.veranstaltungService = veranstaltungService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppeService = gruppeService;
        this.teilnehmerGruppenarbeitService = teilnehmerGruppenarbeitService;
        this.authenticatedUser = authenticatedUser;

        this.teilnehmerListe = new Div();

        this.mainLayoutLeft = new VerticalLayout();
        mainLayoutLeft.setSizeFull();

        Div leftContainer = new Div();
        leftContainer.addClassName("left-container");
        leftContainer.add(mainLayoutLeft);

        Div rightContainer = new Div();
        rightContainer.addClassName("right-container");
        rightContainer.add(teilnehmerListe);

        HorizontalLayout mainLayout = new HorizontalLayout(leftContainer, rightContainer);
        mainLayout.setSizeFull();

        this.veranstaltungTitle = new H1();
        veranstaltungTitle.addClassName("veranstaltung-title");

        Button auswertungButton = new Button();
        auswertungButton.setText("Auswertung");
        auswertungButton.addClassName("auswertung-button");

        auswertungButton.addClickListener(e -> {
            String route = "auswertung/" + veranstaltung.getId();
            UI.getCurrent().navigate(route);
        });

        Icon toggleIcon = new Icon(VaadinIcon.ANGLE_RIGHT);
        this.toggleTeilnehmerListeButton = new Button(toggleIcon);
        this.toggleTeilnehmerListeButton.addClickListener(e -> {
            // Umschalten der Sichtbarkeit der Teilnehmerliste
            teilnehmerListe.setVisible(!teilnehmerListe.isVisible());

            String display = teilnehmerListe.getStyle().get("display");
            boolean isVisible = display != null && display.equals("none");
            teilnehmerListe.getStyle().set("display", isVisible ? "block" : "none");

            if (isVisible) {
                toggleTeilnehmerListeButton.setIcon(new Icon(VaadinIcon.ANGLE_RIGHT));
                leftContainer.getStyle().set("width", "calc(100% - 325px)");
                rightContainer.getStyle().set("width", "320px");
            } else {
                toggleTeilnehmerListeButton.setIcon(new Icon(VaadinIcon.ANGLE_LEFT));
                leftContainer.getStyle().set("width", "100%");
                rightContainer.getStyle().set("width", "0");
            }
        });

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(Alignment.CENTER);
        titleLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        titleLayout.setWidthFull();

        titleLayout.add(veranstaltungTitle);
        Div spacer = new Div();
        titleLayout.add(spacer);
        titleLayout.setFlexGrow(1, spacer);
        titleLayout.add(auswertungButton, toggleTeilnehmerListeButton);

        this.veranstaltungsterminContainer = new Div();
        veranstaltungsterminContainer.addClassName("veranstaltungen-container");

        this.gruppenarbeitContainer = new Div();
        gruppenarbeitContainer.addClassName("gruppenarbeiten-container");

        this.gruppenContainer = new Div();
        gruppenContainer.addClassName("gruppen-container");

        mainLayoutLeft.add(titleLayout, createLineWithText("Veranstaltungstermine"), veranstaltungsterminContainer);
        this.teilnehmerService = teilnehmerService;

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
        createGruppenarbeitHinzufuegenDialog();
        createGruppenarbeitBearbeitenDialog();
        createGruppenarbeitLoeschenDialog();
        createTeilnehmerDialog();

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

        teilnehmerListe.add(createTeilnehmerListe2());

    }

    public void update () {
        veranstaltungsterminContainer.removeAll();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            termine = veranstaltungsterminService.findVeranstaltungstermineByVeranstaltungId(veranstaltung.getId(), user);
        }
        for (Veranstaltungstermin termin : termine) {
            veranstaltungsterminContainer.add(veranstaltungsterminKachel(termin));
        }
        veranstaltungsterminContainer.add(createVeranstaltungsterminKachel());
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
        terminNotiz.setText(veranstaltungstermin.getNotizen());
        terminNotiz.addClassName("termin-notiz");

        Div kachelContent = new Div(terminDatum, terminZeit, terminNotiz);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
        kachel.addClassName("kachel");

        //Initialize Dialogs
        Dialog confirmationDialog = createDeleteConfirmationDialog(
                "Möchten Sie den Veranstaltungstermin für " + veranstaltungstermin.getNotizen() + " am " + veranstaltungstermin.getDatum() + " wirklich löschen?",
                () -> {
                    veranstaltungsterminService.deleteVeranstaltungstermin(veranstaltungstermin);
                    Notification.show("Veranstaltungstermin gelöscht");
                    getUI().ifPresent(ui -> ui.getPage().reload());
                }
        );

        VeranstaltungsterminBearbeiten editDialog = new VeranstaltungsterminBearbeiten(veranstaltungService, veranstaltungsterminService, this, veranstaltungIdString, veranstaltungstermin.getId(), authenticatedUser);


        //Delete Icon
        Div deleteIcon = createDeleteIcon(confirmationDialog);
        deleteIcon.addClassName("delete-icon");

        //Edit Icon
        Div editIcon = new Div(LineAwesomeIcon.EDIT.create());
        editIcon.addClassName("edit-icon");

        editIcon.getElement().addEventListener("click", e-> {
            editDialog.open();
            editDialog.readBean();
        }).addEventData("event.stopPropagation()");

        kachel.add(deleteIcon);
        kachel.add(editIcon);

        //Confirm Dialog Deselect Implementation
        confirmationDialog.addOpenedChangeListener(e -> {
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
        });

        kachel.getElement().addEventListener("mouseout", e -> {
            if (!kachel.equals(aktiveKachelVeranstaltungstermin)) {
                kachel.removeClassName("kachel-hover");
            }
            editIcon.getStyle().set("visibility", "hidden");
            deleteIcon.getStyle().set("visibility", "hidden");
        });

        kachel.addClickListener(e -> {
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
                    mainLayoutLeft.add(gruppenarbeitLinie, gruppenarbeitContainer);
                }

                for (Gruppenarbeit gruppenarbeit : veranstaltungstermin.getGruppenarbeiten()) {
                    gruppenarbeitContainer.add(gruppenarbeitKachel(gruppenarbeit));
                }

                gruppenarbeitContainer.add(createGruppenarbeitKachel());

                gruppenarbeitLinie.setVisible(true);
                gruppenarbeitContainer.setVisible(true);

                gruppenarbeitHinzufuegenDialog.setVeranstaltungstermin(veranstaltungstermin);

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
        veranstaltungsterminDialog = new VeranstaltungsterminDialog(veranstaltungService, veranstaltungsterminService, veranstaltungIdString, authenticatedUser);
    }

    //Lilli
    public void createGruppenarbeitHinzufuegenDialog() {
        gruppenarbeitHinzufuegenDialog = new GruppenarbeitHinzufuegenDialog(authenticatedUser, veranstaltung, gruppenarbeitService, teilnehmerService, veranstaltungsterminService, gruppeService);
        gruppenarbeitHinzufuegenDialog.setWidth("1500px");
    }

    //Lilli
    public void createGruppenarbeitBearbeitenDialog() {
        gruppenarbeitBearbeitenDialog = new GruppenarbeitBearbeitenDialog(gruppenarbeitService);
    }

    //Lilli
    public void createGruppenarbeitLoeschenDialog() {
        gruppenarbeitLoeschenDialog = new GruppenarbeitLoeschenDialog(gruppenarbeitService);
    }

    public void createTeilnehmerDialog() {
        teilnehmerHinzufuegenDialog = new TeilnehmerHinzufuegenDialog(veranstaltungService, teilnehmerService, veranstaltung.getId());
    }

    private Div gruppenarbeitKachel(Gruppenarbeit gruppenarbeit) {
        Div gruppenarbeitInfo = new Div();
        gruppenarbeitInfo.setText(gruppenarbeit.getTitel());
        gruppenarbeitInfo.addClassName("text-center");

        Div kachelContent = new Div(gruppenarbeitInfo);
        kachelContent.addClassName("kachel-content");

        Div kachel = new Div(kachelContent);
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

                gruppenContainer.removeAll();

                if (gruppenLinie == null) {
                    HorizontalLayout lineWithText = createLineWithText("Gruppen");

                    Button editButton = createEditButton();

                    Hr lineAfter = new Hr();
                    lineAfter.addClassName("line-after-icon");

                    gruppenLinie = new HorizontalLayout(lineWithText, editButton, lineAfter);
                    gruppenLinie.setAlignItems(Alignment.CENTER);
                    gruppenLinie.setJustifyContentMode(JustifyContentMode.BETWEEN);
                    gruppenLinie.setWidthFull();

                    mainLayoutLeft.add(gruppenLinie, gruppenContainer);
                }

                Gruppenarbeit fullGruppenarbeit = gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId());

                if (fullGruppenarbeit.getGruppen() != null) {
                    for (Gruppe gruppe : fullGruppenarbeit.getGruppen()) {
                        gruppenContainer.add(gruppenKachel(gruppe));
                    }
                } else {
                    System.out.println("Keine Gruppen gefunden.");
                    //hier was einbauen, um Nutzer anzuzeigen, dass es keine Gruppen gibt
                }

                gruppenLinie.setVisible(true);
                gruppenContainer.setVisible(true);

                kachel.addClassName("kachel-active");
                aktiveKachelGruppenarbeit = kachel;

            }
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
            Div teilnehmerDiv = createTeilnehmerDiv(teilnehmer);

            // Klick-Listener für Teilnehmer
            teilnehmerDiv.addClickListener(e -> {
                gruppeAuswertungDialog = new GruppeAuswertungDialog(teilnehmer,fullGruppe.getGruppenarbeit(),teilnehmerGruppenarbeitService);
                gruppeAuswertungDialog.open();
            });

            return teilnehmerDiv;
        }));

        int itemHeight = 52;
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

    private Dialog createDeleteConfirmationDialog(String confirmationText, Runnable onDelete) {
        Dialog confirmationDialog = new Dialog();
        HorizontalLayout buttonLayout = new HorizontalLayout(
                new Button("Ja", event -> {
                    onDelete.run();
                    confirmationDialog.close();
                }),
                new Button("Nein", event ->
                        confirmationDialog.close()
                )
        );
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        confirmationDialog.add(
                new VerticalLayout(
                        new Text(confirmationText),
                        buttonLayout
                )
        );

        return confirmationDialog;
    }

    private Div createTeilnehmerListe2() {
        //AUTHOR LEON
        final MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
        //Combobox
        comboBox.setItems(teilnehmerService.findAllTeilnehmer(""));
        comboBox.setRenderer(new ComponentRenderer<>(teilnehmer -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            Avatar avatar = new Avatar();
            avatar.setName(teilnehmer.getNachname());
            avatar.setImage(null);
            avatar.setColorIndex(teilnehmer.getId().intValue() % 5);

            Span nachname = new Span(teilnehmer.getNachname());
            Span vorname = new Span(teilnehmer.getVorname());
            vorname.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout column = new VerticalLayout(vorname, nachname);
            column.setPadding(false);
            column.setSpacing(false);

            row.add(avatar, column);
            row.getStyle().set("line-height", "var(--lumo-line-height-m)");
            return row;
        }));
        comboBox.setSizeFull();
       return new Div(comboBox);
    }

    private Div createTeilnehmerListe() {
        Div teilnehmerListe = new Div();
        teilnehmerListe.addClassName("teilnehmer-liste");
        teilnehmerListe.getStyle().set("overflow-y", "hidden");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Suche...");
        searchField.setWidthFull();
        searchField.getStyle().set("margin-top", "-10px");

        List<Teilnehmer> teilnehmer = teilnehmerService.findTeilnehmerByVeranstaltungId(veranstaltung.getId());

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

    private Div createTeilnehmerDiv(Teilnehmer t) {
        Div teilnehmerDiv = new Div();
        teilnehmerDiv.addClassName("teilnehmer-item");

        Span profilbild = new Span();
        profilbild.setText(t.getVorname().charAt(0) + "" + t.getNachname().charAt(0));
        profilbild.addClassName("profilbild");

        Span name = new Span(t.getVorname() + " " + t.getNachname());
        name.addClassName("teilnehmer-name");

        teilnehmerDiv.add(profilbild, name);

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
}