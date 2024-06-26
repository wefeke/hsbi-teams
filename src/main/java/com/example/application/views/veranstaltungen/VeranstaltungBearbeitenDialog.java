package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

/**
 * Dialog zur Bearbeitung einer Veranstaltung.
 *
 * @author Kennet
 */
@Route(value = "editDialog")
@RolesAllowed({"ADMIN", "USER"})
public class VeranstaltungBearbeitenDialog extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;
    private final Veranstaltung veranstaltung;
    private final VeranstaltungenView veranstaltungenView;
    private final Long veranstaltungId;

    //Dialog Items
    private final TextField titelField = new TextField("Titel");
    private final DatePicker datePicker = new DatePicker("Datum");
    private final MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
    private final Button cancelButton= new Button("Abbrechen");
    private final Button saveButton= new Button("Änderungen speichern");

    private final AuthenticatedUser authenticatedUser;

    //Data Binder
    Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);

    /**
     * Konstruktor für die VeranstaltungBearbeiten Klasse.
     * Ruft die Methoden zum Erstellen und Konfigurieren der UI-Elemente auf.
     *
     * @author Kennet
     * @param veranstaltungenService Ein VeranstaltungenService-Objekt, das Methoden zur Interaktion mit Veranstaltungs-Objekten in der Datenbank bereitstellt.
     * @param teilnehmerService Ein TeilnehmerService-Objekt, das Methoden zur Interaktion mit Teilnehmer-Objekten in der Datenbank bereitstellt.
     * @param userService Ein UserService-Objekt, das Methoden zur Interaktion mit User-Objekten in der Datenbank bereitstellt.
     * @param veranstaltung Ein Veranstaltung-Objekt, das die zu bearbeitende Veranstaltung repräsentiert.
     * @param veranstaltungenView Ein VeranstaltungenView-Objekt, das die Ansicht der Veranstaltungen repräsentiert.
     * @param authenticatedUser Ein AuthenticatedUser-Objekt, das Informationen über den authentifizierten Benutzer enthält.
     */
    public VeranstaltungBearbeitenDialog(VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, UserService userService, Veranstaltung veranstaltung, VeranstaltungenView veranstaltungenView, AuthenticatedUser authenticatedUser) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;
        this.veranstaltung = veranstaltung;
        this.veranstaltungId = veranstaltung.getId();
        this.veranstaltungenView = veranstaltungenView;
        this.authenticatedUser = authenticatedUser;

        add(createLayout());
        configureElements();
        bindFields();
        readBean();

    }

    /**
     * Liest die Eigenschaften des Veranstaltung-Objekts und aktualisiert die Eingabefelder entsprechend.
     *
     * @author Kennet
     */
    public void readBean (){
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            binder.readBean(veranstaltungenService.findVeranstaltungById(veranstaltungId, user));
        } else {
            Notification.show("Bitte melden Sie sich an, um Ihre Veranstaltungstermine zu sehen.");
        }
    }

    /**
     * Erstellt das Layout für den Dialog.
     *
     * @author Kennet
     * @return Ein HorizontalLayout-Objekt, das das Layout des Dialogs repräsentiert.
     */
    private HorizontalLayout createLayout() {
        setHeaderTitle("Veranstaltung \"" + veranstaltung.getTitel() +"\" bearbeiten");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return (
                new HorizontalLayout(
                        new VerticalLayout(titelField, datePicker, comboBox)
                ));
    }

    /**
     * Konfiguriert die UI-Elemente für den Dialog.
     * Hier werden die Aktionen für die UI-Elemente festgelegt.
     * Beim Klicken auf den "Speichern" Button wird die Veranstaltung mit den Werten der Eingabefelder aktualisiert.
     * Es wird ein eigener Renderer für das ComboBox-Element verwendet, um die Teilnehmer als Avatar anzuzeigen.
     *
     * @author Kennet
     */
    private void configureElements() {
        //Combobox
        if (authenticatedUser.get().isPresent())
            comboBox.setItems(teilnehmerService.findAllTeilnehmerByUserAndFilter(authenticatedUser.get().get(),""));

        comboBox.setRenderer(new ComponentRenderer<>(teilnehmer -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            Avatar avatar = new Avatar();
            avatar.setName(teilnehmer.getFullName());
            avatar.setImage(null);
            avatar.setColorIndex(teilnehmer.getId().intValue() % 5);

            Span name = new Span(teilnehmer.getFullName());
            Span matrikelnr = new Span(String.valueOf(teilnehmer.getId()));
            matrikelnr.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout column = new VerticalLayout(name, matrikelnr);
            column.setPadding(false);
            column.setSpacing(false);

            row.add(avatar, column);
            row.getStyle().set("line-height", "var(--lumo-line-height-m)");
            return row;
        }));
        comboBox.setReadOnly(true);
        comboBox.setSizeFull();
        datePicker.setSizeFull();
        titelField.setSizeFull();

        //Buttons
        saveButton.addClickListener(event -> {

            Veranstaltung veranstaltung = new Veranstaltung();

            if (binder.writeBeanIfValid(veranstaltung)) {
                veranstaltung.setUser(userService.findAdmin()); //Angemeldeten User holen
                veranstaltung.setId(veranstaltungId); //Sichergehen das auch die richtige Veranstaltung bearbeitet wird
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    veranstaltung.setUser(user);
                }
                veranstaltungenService.saveVeranstaltung(veranstaltung);
                clearFields();
                close();
                veranstaltungenView.updateKachelContainer("");

                Notification.show("Veranstaltung angelegt");
            }
            else {
                Notification.show("Fehler beim Speichern");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.addClickListener(e -> {
            clearFields();
            close();
        });
    }

    /**
     * Bindet die Eingabefelder an die Eigenschaften des Veranstaltung-Objekts.
     *
     * @author Kennet
     */
    private void bindFields() {
        binder.forField(titelField)
                .asRequired("Titel muss gefüllt sein")
                .withValidator(titel -> titel.length() <= 255, "Der Titel darf maximal 255 Zeichen lang sein")
                .bind(Veranstaltung::getTitel, Veranstaltung::setTitel);
        binder.forField(datePicker)
                .asRequired("Datum darf nicht leer sein")
                .bind(Veranstaltung::getSemester, Veranstaltung::setSemester);
        binder.forField(comboBox)
                .bind(Veranstaltung::getTeilnehmer, Veranstaltung::setTeilnehmer);
    }

    /**
     * Leert die Eingabefelder des Dialogs.
     *
     * @author Kennet
     */
    public void clearFields(){
        titelField.clear();
        datePicker.clear();
        comboBox.clear();
    }

}

