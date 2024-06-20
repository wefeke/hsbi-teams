package com.example.application.views.studierende;

import com.example.application.ExcelReader.ExcelImporter;
import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.InputStream;
import java.util.*;

public class StudierendeImportDialog extends Dialog {


    Button importButton = new Button("Importieren");
    Button closeButton = new Button("Schließen");

    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private final Upload upload = new Upload(buffer);
    ExcelImporter excelImporter;
    Set<Teilnehmer> newTeilnehmerListe = new HashSet<>();


    public StudierendeImportDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser,StudierendeView studierendeView) {
    this.excelImporter = new ExcelImporter(teilnehmerService, authenticatedUser);

    H2 headerTitle = new H2("Studierende Importieren");
    add(headerTitle);

    importButton.addClickListener(event -> {



        Dialog dialog = new Dialog();
        dialog.setMaxHeight(getHeight());
        dialog.setHeaderTitle("Neue Teilnehmer");
        dialog.getFooter().add(new Button("OK", e -> {
                studierendeView.updateStudierendeView();
                dialog.close();
        }));
        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        for (Teilnehmer teilnehmer : newTeilnehmerListe) {
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                teilnehmerService.saveTeilnehmer(teilnehmer, user);
                dialogLayout.add(new Span("Teilnehmer :" + teilnehmer.toString() + " angelegt"));

            }
        }
        if (!newTeilnehmerListe.isEmpty())
            dialog.open();

        close();

    });

    closeButton.addClickListener(event -> {
        this.close();
    });

    upload.addSucceededListener(event -> {
        try {
            InputStream inputStream = buffer.getInputStream(event.getFileName());
            newTeilnehmerListe.addAll(excelImporter.readTeilnehmerFromExcel(inputStream));

            List<Teilnehmer> combinedItems= new ArrayList<>();
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                combinedItems.addAll(teilnehmerService.findAllTeilnehmerByUserAndFilter(user, ""));
                combinedItems.addAll(newTeilnehmerListe);
            }

        } catch (Exception e) {
            Notification.show("Error reading Excel file: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    });
    upload.setUploadButton(new Button(LineAwesomeIcon.UPLOAD_SOLID.create()));
    upload.setDropLabelIcon(LineAwesomeIcon.ID_CARD.create());
    upload.setDropLabel(new Span("Teilnehmer Excel-Datei"));
    upload.setAcceptedFileTypes(".xlsx");

    // Create a VerticalLayout and add the upload component to it
    VerticalLayout layout = new VerticalLayout(upload);
    add(layout);

    this.getFooter().add(importButton, closeButton);
}
}
