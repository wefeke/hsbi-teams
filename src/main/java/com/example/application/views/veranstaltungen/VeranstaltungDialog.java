package com.example.application.views.veranstaltungen;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.example.application.services.UserService;
import com.example.application.services.VeranstaltungenService;
import com.example.application.views.veranstaltungstermin.TeilnehmerEntfernenDialog;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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

    //Security
    private AuthenticatedUser authenticatedUser;

    //Data Binder
    Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);

    public VeranstaltungDialog(VeranstaltungenService veranstaltungenService, TeilnehmerService teilnehmerService, UserService userService, VeranstaltungenView veranstaltungenView, AuthenticatedUser authenticatedUser) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.userService = userService;
        this.veranstaltungenView = veranstaltungenView;
        this.authenticatedUser = authenticatedUser;
        add(createLayout());
        configureElements();
        bindFields();
    }

    private VerticalLayout createLayout() {
        setHeaderTitle("Veranstaltung hinzufügen");
        getFooter().add(cancelButton);
        getFooter().add(saveButton);

        return (
                new VerticalLayout(titelField, datePicker, comboBox, upload));
        }

    private void configureElements() {
        //Combobox
        comboBox.setItems(teilnehmerService.findAllTeilnehmerByUserAndFilter(authenticatedUser.get().get(),""));
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
        datePicker.setSizeFull();
        titelField.setSizeFull();
        //Buttons
        saveButton.addClickListener(event -> {

            Veranstaltung veranstaltung = new Veranstaltung();

            if (binder.writeBeanIfValid(veranstaltung)) {
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    veranstaltung.setUser(user);
                }
                veranstaltungenService.saveVeranstaltung(veranstaltung);
                veranstaltungenView.updateKachelContainer("");

                clearFields();
                close();

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

        datePicker.setValue(LocalDate.now());

        //Upload
        upload.addSucceededListener(event -> {
            try {
                InputStream inputStream = buffer.getInputStream(event.getFileName());
                List<Teilnehmer> teilnehmerList = readTeilnehmerFromExcel(inputStream);
                comboBox.setItems(teilnehmerList);
                comboBox.setValue(teilnehmerList);
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

    public void clearFields(){
        titelField.clear();
        datePicker.setValue(LocalDate.now());
        comboBox.clear();
    }

    public List<Teilnehmer> readTeilnehmerFromExcel(InputStream inputStream) throws Exception {
        List<Teilnehmer> teilnehmerList = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        if (rows.hasNext()) { // skip the header row
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            // Assuming the first cell is vorname and the second cell is nachname
            Cell idCell = currentRow.getCell(0);
            Cell vornameCell = currentRow.getCell(1);
            Cell nachnameCell = currentRow.getCell(2);

            Teilnehmer teilnehmer = new Teilnehmer();
            teilnehmer.setId((long) idCell.getNumericCellValue());
            teilnehmer.setVorname(vornameCell.getStringCellValue());
            teilnehmer.setNachname(nachnameCell.getStringCellValue());

            teilnehmerList.add(teilnehmer);
        }

        workbook.close();

        return teilnehmerList;
    }

}

