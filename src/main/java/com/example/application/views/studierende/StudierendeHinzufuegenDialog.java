package com.example.application.views.studierende;


import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.TeilnehmerService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@RolesAllowed({"ADMIN", "USER"})
public class StudierendeHinzufuegenDialog extends Dialog {

    private final TeilnehmerService teilnehmerService;
    private final AuthenticatedUser authenticatedUser;


    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    NumberField matrikelNr = new NumberField("Matrikelnummer");
    Button save = new Button("Speichern");
    Button cancel = new Button("Abbrechen");
     private Teilnehmer teilnehmer;
    Binder<Teilnehmer> binder = new Binder<>(Teilnehmer.class);
    private final StudierendeView studierendeView;

    /**
     * Konstruktor für die StudierendeHinzufuegenDialog Klasse.
     * Initialisiert den TeilnehmerService, den authentifizierten Benutzer und die StudierendeView.
     * Setzt den Titel des Dialogs und fügt das erstellte Layout hinzu.
     * Fügt die Schaltflächen "Speichern" und "Abbrechen" zum Dialog hinzu.
     * Konfiguriert die Schaltflächen und bindet die Felder an den Teilnehmer.
     *
     * @param teilnehmerService Der Service, der für die Verwaltung der Studierenden benötigt wird.
     * @param authenticatedUser Der aktuell authentifizierte Benutzer.
     * @param studierendeView Die StudierendeView, die aktualisiert wird, wenn ein neuer Studierender hinzugefügt wird.
     */
    public StudierendeHinzufuegenDialog(TeilnehmerService teilnehmerService, AuthenticatedUser authenticatedUser, StudierendeView studierendeView){
        this.teilnehmerService = teilnehmerService;
        this.authenticatedUser = authenticatedUser;
        this.studierendeView = studierendeView;

        // Layout erstellen und Komponenten hinzufügen
        setHeaderTitle("Studierenden hinzufügen");
        add(createLayout()); // Sicherstellen, dass das Layout hinzugefügt wird
        getFooter().add(cancel, save); // Button hinzufügen


        configureButton();
        bindFields();
    }

    /**
     * Erstellt ein FormLayout und fügt die Felder "firstName", "lastName" und "matrikelNr" hinzu.
     * Setzt außerdem den ThemeVariant des "save"-Buttons auf LUMO_PRIMARY und fügt einen ClickShortcut hinzu.
     *
     * @return Das erstellte FormLayout mit den hinzugefügten Feldern.
     */
    private FormLayout createLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, matrikelNr);
        return formLayout;
    }

    /**
     * Konfiguriert die Aktionen der "Speichern" und "Abbrechen" Buttons.
     * Wenn der "Speichern"-Button geklickt wird, wird überprüft, ob die Eingabe gültig ist und ob die Matrikelnummer bereits existiert.
     * Wenn die Eingabe ungültig ist, wird eine Benachrichtigung angezeigt.
     * Wenn die Matrikelnummer bereits existiert, wird ebenfalls eine Benachrichtigung angezeigt.
     * Ansonsten wird der Teilnehmer gespeichert.
     * Wenn der "Abbrechen"-Button geklickt wird, wird der Dialog geschlossen.
     */
    private void configureButton() {
        save.addClickListener(event -> {
            if (isValidInput()) {
                if (isDuplicateMatrikelNr()) {
                    Notification.show("Matrikelnummer existiert bereits", 3000, Notification.Position.MIDDLE);
                } else {
                    saveTeilnehmer();
                }
            } else {
                Notification.show("Bitte füllen Sie alle Felder aus", 3000, Notification.Position.MIDDLE);
            }
        });
        cancel.addClickListener(event -> close());
    }

    /**
     * Überprüft, ob die Eingabefelder "firstName", "lastName" und "matrikelNr" gültige Werte enthalten.
     *
     * @return true, wenn "firstName" und "lastName" nicht leer sind und "matrikelNr" nicht null ist, sonst false.
     */
    private boolean isValidInput() {
        return !firstName.isEmpty() && !lastName.isEmpty() && matrikelNr.getValue() != null;
    }

    /**
     * Überprüft, ob die Matrikelnummer bereits existiert.
     * Holt den Wert der Matrikelnummer und den authentifizierten Benutzer.
     * Wenn der Benutzer vorhanden ist, wird die ID des Benutzers geholt und es wird versucht, einen existierenden Teilnehmer mit der gleichen Matrikelnummer und Benutzer-ID zu finden.
     * Wenn ein solcher Teilnehmer gefunden wird und er nicht der aktuell bearbeitete Teilnehmer ist, wird true zurückgegeben.
     * Wenn kein Benutzer vorhanden ist oder kein Teilnehmer gefunden wird, wird false zurückgegeben.
     *
     * @return true, wenn die Matrikelnummer bereits existiert und nicht dem aktuell bearbeiteten Teilnehmer gehört, sonst false.
     */
    private boolean isDuplicateMatrikelNr() {
    Long matrikelNrValue = matrikelNr.getValue().longValue();
    Optional<User> maybeUser = authenticatedUser.get();
    if (maybeUser.isPresent()) {
        Long userId = maybeUser.get().getId();
        Optional<Teilnehmer> existingTeilnehmer = teilnehmerService.findByMatrikelNrAndUserId(matrikelNrValue, userId);
        return existingTeilnehmer.isPresent() && (teilnehmer == null || !existingTeilnehmer.get().getId().equals(teilnehmer.getId()));
    }
    return false;
    }

    /**
     * Speichert den Teilnehmer, wenn die Matrikelnummer gültig ist.
     * Erstellt einen neuen Teilnehmer und versucht, die Werte der Eingabefelder in den Teilnehmer zu schreiben.
     * Holt den authentifizierten Benutzer und speichert den Teilnehmer mit dem Benutzer.
     * Aktualisiert die StudierendeView und schließt den Dialog.
     * Zeigt eine Benachrichtigung an, wenn der Teilnehmer erfolgreich gespeichert wurde.
     */
    private void saveTeilnehmer() {
        Teilnehmer teilnehmer = new Teilnehmer();

        // Check if the Matrikelnummer is valid before saving the Teilnehmer
        if (matrikelNr.getValue() != null && String.valueOf(matrikelNr.getValue().longValue()).matches("\\d{7}")) {
            if (binder.writeBeanIfValid(teilnehmer)) {
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    teilnehmerService.saveTeilnehmer(teilnehmer, user);
                    studierendeView.updateStudierendeView();
                    clearFields();
                    close();
                    Notification.show("Teilnehmer wurde angelegt");
                } else {
                    Notification.show("Fehler beim Speichern");
                }
            }
        }
    }

    /**
     * Bindet die Felder "firstName", "lastName" und "matrikelNr" an den Teilnehmer.
     * Setzt die Felder als erforderlich und bindet sie an die entsprechenden Teilnehmer-Attribute.
     */
    private void bindFields() {
        binder.forField(firstName)
                .asRequired("Vorname muss gefüllt sein")
                .withValidator(vorname -> vorname.length() <= 255, "Der Vorname darf maximal 255 Zeichen lang sein")
                .bind(Teilnehmer::getVorname, Teilnehmer::setVorname);

        binder.forField(lastName)
                .asRequired("Nachname muss gefüllt sein")
                .withValidator(nachname -> nachname.length() <= 255, "Der Nachname darf maximal 255 Zeichen lang sein")
                .bind(Teilnehmer::getNachname, Teilnehmer::setNachname);

        binder.forField(matrikelNr)
                .asRequired("Matrikelnummer muss gefüllt sein")
                .withValidator(matrikelNr -> String.valueOf(matrikelNr.longValue()).matches("\\d{7}"), "Matrikelnummer muss genau 7 Ziffern enthalten")
                .withConverter(Double::longValue, Long::doubleValue)
                .bind(Teilnehmer::getId, Teilnehmer::setId);
    }

    /**
     * Löscht die Werte der Eingabefelder "firstName", "lastName" und "matrikelNr".
     */
    private void clearFields() {
        firstName.clear();
        lastName.clear();
        matrikelNr.clear();
    }

    public void setTeilnehmer(Teilnehmer teilnehmer) {
        this.teilnehmer = teilnehmer;
    }
}
