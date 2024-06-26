/**
 * GruppenarbeitBearbeitenDialog ist ein Dialog zum Bearbeiten einer Gruppenarbeit.
 * Er ermöglicht das Bearbeiten von Titel und Beschreibung einer Gruppenarbeit und
 * speichert die Änderungen in der Datenbank.
 * <p>
 * Dieser Dialog verwendet Vaadin-Komponenten und bindet die Felder an ein
 * {@link Gruppenarbeit} Modell.
 *
 * @author Lilli
 */
package com.example.application.views.gruppenarbeit;

import com.example.application.models.Gruppenarbeit;
import com.example.application.services.GruppenarbeitService;
import com.example.application.views.veranstaltungstermin.VeranstaltungsterminView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class GruppenarbeitBearbeitenDialog extends Dialog {
    //Services
    private final GruppenarbeitService gruppenarbeitService;

    //Binder
    private final Binder<Gruppenarbeit> binder = new Binder<>(Gruppenarbeit.class);

    //Data
    private Gruppenarbeit gruppenarbeit;
    private final VeranstaltungsterminView veranstaltungsterminView;

    //UI Elements
    private final H3 infoText = new H3();
    private final TextField titleField = new TextField("Titel");
    private final TextArea descriptionArea = new TextArea("Beschreibung");
    private final Button saveBtn = new Button("Änderungen speichern");
    private final Button cancelBtn = new Button("Abbrechen");

    /**
     * Konstruktor für GruppenarbeitBearbeitenDialog.
     * Initialisiert den Dialog mit den notwendigen Services und der VeranstaltungsterminView.
     * Bindet die Felder, konfiguriert die Button-Funktionalitäten, passt das Styling an und fügt das Layout hinzu.
     *
     * @param gruppenarbeitService     der Service für Gruppenarbeit, um Daten zu speichern und zu laden
     * @param veranstaltungsterminView die View, in der die Gruppenarbeit angezeigt wird
     * @author Lilli
     */
    public GruppenarbeitBearbeitenDialog(GruppenarbeitService gruppenarbeitService, VeranstaltungsterminView veranstaltungsterminView) {
        this.gruppenarbeitService = gruppenarbeitService;
        this.veranstaltungsterminView = veranstaltungsterminView;
        this.gruppenarbeit = null;

        bindFields();
        configureButtonFunctionalities();
        styling();
        add(createLayout());
    }

    /**
     * Bindet die Felder an die Eigenschaften des {@link Gruppenarbeit} Modells.
     * Stellt sicher, dass die Validierungskriterien für die Felder erfüllt sind.
     * Der Titel muss ausgefüllt sein und darf maximal 255 Zeichen lang sein.
     * Die Beschreibung darf ebenfalls maximal 255 Zeichen lang sein.
     *
     * @author Lilli
     */
    private void bindFields() {
        binder.forField(titleField)
                .asRequired("Titel muss gefüllt sein")
                .withValidator(titel -> titel.length() <= 255, "Der Titel darf maximal 255 Zeichen lang sein")
                .bind(Gruppenarbeit::getTitel, Gruppenarbeit::setTitel);
        titleField.setMaxLength(255);
        binder.forField(descriptionArea)
                .withValidator(beschreibung -> beschreibung.length() <= 255, "Die Beschreibung darf maximal 255 Zeichen lang sein")
                .bind(Gruppenarbeit::getBeschreibung, Gruppenarbeit::setBeschreibung);
        descriptionArea.setMaxLength(255);
    }

    /**
     * Erstellt das Layout für den Dialog.
     * Fügt die UI-Elemente (Titel, Beschreibung) und die Buttons (Speichern, Abbrechen) hinzu.
     *
     * @return das erstellte vertikale Layout für den Dialog
     * @author Lilli
     */
    private VerticalLayout createLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(infoText, titleField, descriptionArea);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(cancelBtn);
        getFooter().add(saveBtn);
        return mainLayout;
    }

    /**
     * Speichert die Änderungen der Gruppenarbeit, sofern die Validierung erfolgreich ist.
     *
     * @author Lilli
     */
    private void saveChanges() {
        if (binder.writeBeanIfValid(gruppenarbeit)) {
            gruppenarbeitService.save(gruppenarbeit);
        }
    }

    /**
     * Konfiguriert die Funktionalitäten der Speichern- und Abbrechen-Buttons.
     * Der Speichern-Button speichert die Änderungen und aktualisiert die Ansicht des Veranstaltungstermins, falls
     * vorhanden. Der Abbrechen-Button schließt den Dialog.
     *
     * @author Lilli
     */
    private void configureButtonFunctionalities() {
        saveBtn.addClickListener(event -> {
            saveChanges();
            if (gruppenarbeit.getVeranstaltungstermin() != null) {
                veranstaltungsterminView.setAktiveKachelVeranstaltungstermin(gruppenarbeit.getVeranstaltungstermin());
                if (gruppenarbeit != null) {
                    veranstaltungsterminView.setAktiveKachelGruppenarbeit(gruppenarbeit);
                }
            }
            veranstaltungsterminView.update();
            close();
        });
        cancelBtn.addClickListener(event -> close());
    }

    /**
     * Passt das Styling der UI-Elemente an.
     * Der Speichern-Button wird mit dem Primär-Thema versehen.
     * Die Breite von Titel und Beschreibung wird auf 300px gesetzt.
     * Die Höhe der Beschreibungs-Area wird auf 300px festgelegt.
     *
     * @author Lilli
     */
    private void styling() {
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        titleField.setWidth("300px");
        descriptionArea.setWidth("300px");
        descriptionArea.setHeight("300px");
    }

    /**
     * Liest die Daten der Gruppenarbeit aus der Datenbank und aktualisiert die UI.
     * Bindet die gelesenen Daten an das Binder-Objekt und aktualisiert den Info-Text.
     *
     * @author Lilli
     */
    public void readBean() {
        binder.readBean(gruppenarbeitService.findGruppenarbeitByIdWithGruppen(gruppenarbeit.getId()));
        infoText.setText("Gruppenarbeit \"" + this.gruppenarbeit.getTitel() + "\" bearbeiten");
    }

    /**
     * Setzt die Gruppenarbeit, die bearbeitet werden soll.
     * Aktualisiert die interne Referenz auf die Gruppenarbeit.
     *
     * @param gruppenarbeit die Gruppenarbeit, die bearbeitet werden soll
     * @author Lilli
     */
    public void setGruppenarbeit(Gruppenarbeit gruppenarbeit) {
        this.gruppenarbeit = gruppenarbeit;
    }
}