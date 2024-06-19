package com.example.application.views.veranstaltungen;

import com.example.application.ExcelReader.ExcelImporter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.veranstaltungstermin.TeilnehmerEntfernenDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.security.RolesAllowed;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vaadin.lineawesome.LineAwesomeIcon;


import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * Dialog zur Erstellung einer neuen Veranstaltung.
 *
 * @author Kennet
 */
@Route(value = "addDialog")
@RolesAllowed({"ADMIN", "USER"})
public class VeranstaltungDialog extends Dialog {

    //Services
    private final VeranstaltungenService veranstaltungenService;
    private final TeilnehmerService teilnehmerService;
    private final UserService userService;
    private final VeranstaltungenView veranstaltungenView;

    //Dialog Items
    private final TextField titelField = new TextField("Titel");
    private final DatePicker datePicker = new DatePicker("Datum");
    private final MultiSelectComboBox<Teilnehmer> comboBox = new MultiSelectComboBox<>("Teilnehmer");
    private final Button cancelButton= new Button("Abbrechen");
    private final Button saveButton= new Button("Speichern");

    //Upload Components
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private final Upload upload = new Upload(buffer);
    ExcelImporter excelImporter;
    Set<Teilnehmer> newTeilnehmerListe = new HashSet<>();

    //Security
    private AuthenticatedUser authenticatedUser;
    private User user;

    //Data Binder
    Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);

    /**
     * Konstruktor für die VeranstaltungDialog Klasse.
     * Ruft die Methoden zum Erstellen und Konfigurieren der UI-Elemente auf.
     *
     * @author Kennet
     * @param veranstaltungenService Ein VeranstaltungenService-Objekt, das Methoden zur Interaktion mit Veranstaltungs-Objekten in der Datenbank bereitstellt.
     * @param teilnehmerService Ein TeilnehmerService-Objekt, das Methoden zur Interaktion mit Teilnehmer-Objekten in der Datenbank bereitstellt.
     * @param userService Ein UserService-Objekt, das Methoden zur Interaktion mit User-Objekten in der Datenbank bereitstellt.
     * @param veranstaltungenView Ein VeranstaltungenView-Objekt, das die Ansicht der Veranstaltungen repräsentiert.
     * @param authenticatedUser Ein AuthenticatedUser-Objekt, das Informationen über den authentifizierten Benutzer enthält.
     */
    public VeranstaltungDialog(VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, UserService userService, VeranstaltungenView veranstaltungenView, AuthenticatedUser authenticatedUser) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;
        this.veranstaltungenView = veranstaltungenView;
        this.authenticatedUser = authenticatedUser;
        this.excelImporter = new ExcelImporter(teilnehmerService, authenticatedUser);

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            this.user = maybeUser.get();
        }

        add(createLayout());
        configureElements();
        bindFields();
    }

    /**
     * Erstellt das Layout für den Dialog.
     *
     * @author Kennet
     * @return Ein VerticalLayout-Objekt, das das Layout des Dialogs repräsentiert.
     */
    private VerticalLayout createLayout() {
        setHeaderTitle("Veranstaltung hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return (
                new VerticalLayout(titelField, datePicker, comboBox, upload));
        }

    /**
     * Konfiguriert die UI-Elemente für den Dialog.
     * Durch den Upload-Button können Teilnehmer über eine Excel-Datei hinzugefügt werden, die dann in der Teilnehmerliste angezeigt und direkt ausgewählt werden.
     * Wenn ein Teilnehmer bereits in der Datenbank existiert, wird dieser nicht erneut angelegt. Tut er das nicht, wird er angelegt.
     * Beim Klicken auf den "Speichern" Button wird eine neue Veranstaltung mit den Werten der Eingabefelder gefüllt und mit den neuen Teilnehmern in der Datenbank gespeichert.
     * Es wird ein eigener Renderer für das ComboBox-Element verwendet, um die Teilnehmer als Avatar anzuzeigen.
     *
     * @author Kennet
     */
    private void configureElements() {
        //Combobox
        comboBox.setItems(teilnehmerService.findAllTeilnehmerByUserAndFilter(user,""));
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

        datePicker.setValue(LocalDate.now());
        datePicker.setSizeFull();

        titelField.setSizeFull();

        //Buttons
        saveButton.addClickListener(event -> {
            Veranstaltung veranstaltung = new Veranstaltung();
            if (binder.writeBeanIfValid(veranstaltung)) {
                veranstaltung.setUser(user);

                Set<Teilnehmer> tempNewTeilnehmerSet = new HashSet<>(newTeilnehmerListe);
                tempNewTeilnehmerSet.removeAll(comboBox.getValue()); // Teilnehmer die ich aus der Teilnehmerliste löschen muss
                newTeilnehmerListe.removeAll(tempNewTeilnehmerSet);

                Dialog dialog = new Dialog();
                dialog.setMaxHeight(getHeight());
                dialog.setHeaderTitle("Neue Teilnehmer");
                dialog.getFooter().add(new Button("OK", e -> dialog.close()));
                VerticalLayout dialogLayout = new VerticalLayout();
                dialog.add(dialogLayout);

                for (Teilnehmer teilnehmer : newTeilnehmerListe) {
                    teilnehmerService.saveTeilnehmer(teilnehmer, user);
                    dialogLayout.add(new Span("Teilnehmer :" + teilnehmer.toString() + " angelegt"));
                }

                veranstaltungenService.saveVeranstaltung(veranstaltung);
                veranstaltungenView.updateKachelContainer("");

                if (!newTeilnehmerListe.isEmpty())
                    dialog.open();

                clearFields();
                close();
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

        //Upload
        upload.addSucceededListener(event -> {
            try {
                InputStream inputStream = buffer.getInputStream(event.getFileName());
                newTeilnehmerListe.addAll(excelImporter.readTeilnehmerFromExcel(inputStream));

                List<Teilnehmer> combinedItems= new ArrayList<>();
                combinedItems.addAll(teilnehmerService.findAllTeilnehmerByUserAndFilter(user, ""));
                combinedItems.addAll(newTeilnehmerListe);

                List<Teilnehmer> combinedValue = new ArrayList<>();
                combinedValue.addAll(comboBox.getValue());
                combinedValue.addAll(newTeilnehmerListe);

                comboBox.setItems(combinedItems);
                comboBox.setValue(combinedValue);

            } catch (Exception e) {
                Notification.show("Error reading Excel file: " + e.getMessage());
                System.out.println(e.getMessage());
            }
        });
        upload.setUploadButton(new Button(LineAwesomeIcon.UPLOAD_SOLID.create()));
        upload.setDropLabelIcon(LineAwesomeIcon.ID_CARD.create());
        upload.setDropLabel(new Span("Teilnehmer Excel-Datei"));
        upload.setAcceptedFileTypes(".xlsx");

    }

    /**
     * Bindet die Eingabefelder an die Eigenschaften des Veranstaltung-Objekts.
     *
     * @author Kennet
     */
    private void bindFields() {
        binder.forField(titelField)
                .asRequired("Titel muss gefüllt sein")
                .bind(Veranstaltung::getTitel, Veranstaltung::setTitel);
        binder.forField(datePicker)
                .asRequired("Datum muss gefüllt sein")
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
        datePicker.setValue(LocalDate.now());
        comboBox.clear();
        newTeilnehmerListe.clear();
    }




}

