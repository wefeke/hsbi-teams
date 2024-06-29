package com.example.application.views.kalender;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// LEON
@Route(value = "kalender", layout = MainLayout.class)
@PageTitle("Kalender")
@RolesAllowed({"ADMIN", "USER"})
public class KalenderView extends VerticalLayout {
    private final VeranstaltungsterminService veranstaltungsterminService;
    // Kalender
    FullCalendar fullCalendar;
    // Services
    VeranstaltungenService veranstaltungenService;
    TeilnehmerService teilnehmerService;
    GruppenarbeitService gruppenarbeitService;
    GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    SuperService superService;
    UserService userService;
    // Fields
    AuthenticatedUser authenticatedUser;
    Optional<User> maybeUser;
    User user;
    // Ranges
    LocalDate currentDate = getCurrentDate();
    // Buttons
    Button zuvor = new Button("Zuvor");
    Button naechste = new Button("Nächste");
    // Auswahl der Ansichten
    ComboBox<KalenderAuswahl> kalenderAuswahlComboBox = new ComboBox<>();
    // H1
    H1 anzeige = new H1(); // Anzeige des Datum oder KW oder Monat
    // DateTime Formatter - Optionen sind Aktuelles Datum mit dd.mm.yyyy, MMM für Monat
    DateTimeFormatter dateFormatterMonat = DateTimeFormatter.ofPattern("MMM").withLocale( Locale.GERMAN );
    DateTimeFormatter dateFormatterStandard = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale( Locale.GERMAN );

    /**
     * Konstruktor für die KalenderView Klasse.
     * Initialisiert die Services und die benötigten Felder, erstellt das Layout und konfiguriert die Elemente.
     *
     * @param veranstaltungenService der Service für die Veranstaltungen
     * @param teilnehmerService der Service für die Teilnehmer
     * @param gruppenarbeitService der Service für die Gruppenarbeiten
     * @param gruppenarbeitTeilnehmerService der Service für die Gruppenarbeitsteilnehmer
     * @param userService der Service für die Benutzer
     * @param authenticatedUser der aktuell authentifizierte Benutzer
     * @param superService der Service für übergeordnete Funktionen
     * @param veranstaltungsterminService der Service für die Veranstaltungstermine
     *
     * @author Leon
     */
    public KalenderView(
            VeranstaltungenService veranstaltungenService,
            TeilnehmerService teilnehmerService,
            GruppenarbeitService gruppenarbeitService,
            GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService,
            UserService userService,
            AuthenticatedUser authenticatedUser,
            SuperService superService, VeranstaltungsterminService veranstaltungsterminService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.superService = superService;
        this.veranstaltungsterminService = veranstaltungsterminService;
        addClassName("kalender-view");
        maybeUser = authenticatedUser.get();
        user = validateUser(maybeUser);
        fullCalendar = getCalendar();
        configureButtons();
        configureKalenderAuswahl();
        add(getToolbar(), fullCalendar);
        updateCalendar();
        setSizeFull();
    }

    /**
     * Aktualisiert den Kalender mit den Veranstaltungsterminen.
     * Erstellt für jeden Termin einen Kalendereintrag und fügt ihn zum Kalender hinzu.
     *
     * @author Leon
     */
    private void updateCalendar() {
        List<Veranstaltungstermin> veranstaltungstermine = veranstaltungsterminService.findAllVeranstaltungstermine(user);
        for (Veranstaltungstermin veranstaltungstermin : veranstaltungstermine) {
            Entry entry = new Entry();
            entry.setTitle(veranstaltungstermin.getTitel());
            entry.setColor(randomColor());
            entry.setConstraint(veranstaltungstermin.getVeranstaltung().getId().toString());
            if (veranstaltungstermin.getStartZeit() == null) {
                entry.setStart(dateTimeConverter(veranstaltungstermin.getDatum()));
                entry.setEnd(entry.getStart().plusHours(23));
            } else {
                entry.setStart(dateTimeConverter(veranstaltungstermin.getDatum(), veranstaltungstermin.getStartZeit()));
                entry.setEnd(entry.getStart().plusHours(veranstaltungstermin.getEndZeit().getHour()));
            }
            fullCalendar.getEntryProvider().asInMemory().addEntries(entry);
        }
    }

    /**
     * Gibt das aktuelle Datum zurück.
     *
     * @return das aktuelle Datum
     *
     * @author Leon
     */
    private LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Konfiguriert die Auswahlmöglichkeiten im Kalender.
     * Setzt die verfügbaren Optionen und definiert das Verhalten bei Auswahländerungen.
     *
     * @author Leon
     */
    private void configureKalenderAuswahl() {
        kalenderAuswahlComboBox.setItems(KalenderAuswahl.getAllWerte());
        kalenderAuswahlComboBox.setValue(KalenderAuswahl.getAllWerte().getFirst());
        kalenderAuswahlComboBox.setItemLabelGenerator(KalenderAuswahl::toString);
        kalenderAuswahlComboBox.addValueChangeListener(event -> validateKalenderAuswahl());
    }

    /**
     * Konvertiert ein LocalDate und LocalTime in ein LocalDateTime.
     *
     * @param localDate das lokale Datum
     * @param localTime die lokale Zeit
     * @return die kombinierte LocalDateTime
     *
     * @author Leon
     */
    private LocalDateTime dateTimeConverter(LocalDate localDate, LocalTime localTime) {
        return localDate.atTime(localTime);
    }

    /**
     * Konvertiert ein LocalDate in ein LocalDateTime.
     *
     * @param localDate das lokale Datum
     * @return die kombinierte LocalDateTime
     *
     * @author Leon
     */
    private LocalDateTime dateTimeConverter(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    /**
     * Generiert eine zufällige Farbe im Hexadezimalformat.
     *
     * @return eine zufällige Farbe als String
     *
     * @author Leon
     */
    private String randomColor() {
        Random rand = new Random();
        int myRandomNumber = rand.nextInt(0xFFFFFF + 1);
        return String.format("#%06x", myRandomNumber);
    }

    /**
     * Erstellt und konfiguriert den Kalender.
     * Setzt die Größe des Kalenders und definiert das Verhalten bei Klicks auf Einträge.
     *
     * @return der konfigurierte FullCalendar
     *
     * @author Leon
     */
    private FullCalendar getCalendar() {
        FullCalendar calendar = FullCalendarBuilder.create().build();
        calendar.setLocale(Locale.GERMAN);
        calendar.addEntryClickedListener((event) -> {
            Entry entry = event.getEntry();
            long veranstaltungId = Long.parseLong(entry.getConstraint());
            getUI().ifPresent(ui -> ui.navigate("veranstaltung-detail/" + veranstaltungId));
        });
        calendar.setSizeFull();
        return calendar;
    }

    /**
     * Ändert das Datum basierend auf der aktuellen Auswahl im Kalender.
     * Inkrementiert oder dekrementiert das Datum, abhängig von der übergebenen Konfiguration.
     *
     * @param increment gibt an, ob das Datum inkrementiert oder dekrementiert werden soll
     *
     * @author Leon
     */
    private void changeDateByConfiguration(boolean increment) {
        if (increment) {
            if (kalenderAuswahlComboBox.getValue() == null) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.plusMonths(1)),
                        currentDate.plusMonths(1)
                );
                // Es wird der Monat ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt, wenn keine Auswahl getroffen wurde
                anzeige.removeAll();
                anzeige.add(currentDate.format(dateFormatterMonat));

            } else if (kalenderAuswahlComboBox.getValue().istMonat()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.plusMonths(1)),
                        currentDate.plusMonths(1)
                );
                // Es wird der Monat ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt
                anzeige.removeAll();
                anzeige.add(currentDate.format(dateFormatterMonat));

            } else if (kalenderAuswahlComboBox.getValue().istTag()) {
                fullCalendar.setValidRange(
                        currentDate = currentDate.plusDays(1),
                        currentDate.plusDays(2)
                );
                // Es wird der Monat ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt
                anzeige.removeAll();
                anzeige.add(currentDate.format(dateFormatterStandard));

            } else if (kalenderAuswahlComboBox.getValue().istWoche()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfWeek(currentDate.plusWeeks(1)),
                        currentDate.plusWeeks(1)
                );
                // Zunächst wird jedes Datum davor aus der anzeige entfernt
                // Dann wird die KW berechnet
                anzeige.removeAll();
                anzeige.add("KW "+ getKW(currentDate.getDayOfYear()));
            }
        } else {
            if (kalenderAuswahlComboBox.getValue() == null) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.minusMonths(1)),
                        currentDate.plusMonths(1)
                );
                // Es wird der Monat ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt
                anzeige.removeAll();
                anzeige.add(currentDate.format(dateFormatterMonat));
            } else if (kalenderAuswahlComboBox.getValue().istMonat()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfMonth(currentDate.minusMonths(1)),
                        currentDate.plusMonths(1)
                );
                // Es wird der Monat ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt
                anzeige.removeAll();
                anzeige.add(currentDate.format(dateFormatterMonat));
            } else if (kalenderAuswahlComboBox.getValue().istTag()) {
                fullCalendar.setValidRange(
                        currentDate = currentDate.minusDays(1), // Ein Tag wird abgezogen
                        currentDate.plusDays(1) // Eine Tag wird zum vorherigen Datum hinzugefügt
                );
                // Es wird das Datum ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt
                anzeige.removeAll();
                anzeige.add(currentDate.format(dateFormatterStandard));
            } else if (kalenderAuswahlComboBox.getValue().istWoche()) {
                fullCalendar.setValidRange(
                        currentDate = getStartOfWeek(currentDate.minusWeeks(1)), // Eine Woche wird abgezogen und sichergestellt, dass es der Montag ist
                        getStartOfWeek(currentDate.plusDays(7)) // Eine Woche wird zur vorherigen Operation hinzugefügt
                );
                // Zunächst wird jedes Datum davor aus der anzeige entfernt
                // Dann wird die KW berechnet
                anzeige.removeAll();
                anzeige.add("KW "+ getKW(currentDate.getDayOfYear()));
            }
        }
    }
    //

    /**
     * Konfiguriert die Schaltflächen für die Navigation im Kalender.
     * Definiert das Verhalten bei Klicks auf die "Zuvor" und "Nächste" Schaltflächen.
     *
     * @author Leon
     */
    private void configureButtons() {
        naechste.addClickListener(event -> changeDateByConfiguration(true));

        zuvor.addClickListener(event -> changeDateByConfiguration(false));
    }

    /**
     * Validiert den authentifizierten Benutzer.
     * Wenn der Benutzer vorhanden ist, wird er zurückgegeben. Andernfalls wird ein neuer Benutzer erstellt und zurückgegeben.
     *
     * @param maybeUser der optionale authentifizierte Benutzer
     * @return der validierte Benutzer
     *
     * @author Leon
     */
    private User validateUser(Optional<User> maybeUser) {
        return maybeUser.orElseGet(User::new);
    }

    /**
     * Erstellt die Toolbar für die Kalenderansicht.
     * Die Toolbar enthält die Überschrift, Navigationsschaltflächen und die Kalenderauswahl.
     *
     * @return das erstellte HorizontalLayout für die Toolbar
     *
     * @author Leon
     */
    private HorizontalLayout getToolbar() {
        H1 username = new H1("Kalender für " + user.getName());
        username.getStyle().set("font-size", "28px");

        if (maybeUser.isPresent()) {
            getUI().ifPresent(ui -> ui.navigate("login"));
        }
        // Initial wird der Monat ausgewählt
        anzeige.add(currentDate.format(dateFormatterMonat));

        var toolbar = new HorizontalLayout(username, zuvor, anzeige, naechste, kalenderAuswahlComboBox);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    /**
     * Validiert die Auswahl im Kalender und ändert die Ansicht entsprechend.
     * Passt die Kalenderansicht basierend auf der Auswahl an (Tag, Woche, Monat).
     *
     * @author Leon
     */
    public void validateKalenderAuswahl() {
        if (kalenderAuswahlComboBox.getValue() == null || kalenderAuswahlComboBox.getValue().istMonat()) { // Die neuen Grenzen bilden nur einen Rahmen, daher kann hier auch
            // mit zwei Monaten gerechnet werden
            fullCalendar.setValidRange(getStartOfMonth(currentDate).minusWeeks(1), getStartOfMonth(currentDate).plusMonths(2));

            // Es wird der Monat ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt
            anzeige.removeAll();
            anzeige.add(currentDate.format(dateFormatterMonat));

            fullCalendar.changeView(CalendarViewImpl.DAY_GRID_MONTH);
        } else if (kalenderAuswahlComboBox.getValue().istTag()) {
            naechste.click();
            zuvor.click();

            // Es wird das Datum ausgewählt und zunächst jedes Datum davor aus der anzeige entfernt
            anzeige.removeAll();
            anzeige.add(currentDate.format(dateFormatterStandard));

            fullCalendar.changeView(CalendarViewImpl.TIME_GRID_DAY);
        } else if (kalenderAuswahlComboBox.getValue().istWoche()) { // Beim Ändern auf die Wochenansicht passiert es, dass
            // bei Tag-Ansicht die Valid Range zu klein für eine ganze Woche ist, also kann sie auch einfach auf zwei Wochen gesetzt werden

            fullCalendar.setValidRange(getStartOfWeek(currentDate).minusWeeks(1), getStartOfWeek(currentDate).plusWeeks(1));

            // Zunächst wird jedes Datum davor aus der anzeige entfernt
            // Dann wird die KW berechnet
            anzeige.removeAll();
            anzeige.add("KW "+ getKW(currentDate.getDayOfYear()));

            fullCalendar.changeView(CalendarViewImpl.TIME_GRID_WEEK);
        }
        fullCalendar.setSizeFull();
    }

    /**
     * Gibt das Startdatum der Woche (Montag) für das gegebene LocalDate zurück.
     * Wenn das gegebene Datum bereits ein Montag ist, wird es unverändert zurückgegeben.
     * Andernfalls wird rekursiv der entsprechende Tag subtrahiert, bis der Montag erreicht ist.
     *
     * @param localDate das Datum, von dem aus das Startdatum der Woche ermittelt werden soll
     * @return das Startdatum der Woche (Montag) für das gegebene LocalDate
     * @throws IllegalArgumentException wenn localDate null ist
     * @author Leon
     */
    private LocalDate getStartOfWeek(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("Das angegebene Datum darf nicht null sein");
        }

        return switch (localDate.getDayOfWeek()) {
            case MONDAY -> localDate;
            case TUESDAY -> getStartOfWeek(localDate.minusDays(1));
            case WEDNESDAY -> getStartOfWeek(localDate.minusDays(2));
            case THURSDAY -> getStartOfWeek(localDate.minusDays(3));
            case FRIDAY -> getStartOfWeek(localDate.minusDays(4));
            case SATURDAY -> getStartOfWeek(localDate.minusDays(5));
            case SUNDAY -> getStartOfWeek(localDate.minusDays(6));
        };
    }


    /**
     * Gibt das Startdatum des Monats für das gegebene LocalDate zurück.
     * Wenn das gegebene Datum bereits der erste Tag des Monats ist, wird es unverändert zurückgegeben.
     * Andernfalls wird rekursiv ein Tag subtrahiert, bis der erste Tag des Monats erreicht ist.
     *
     * @param localDate das Datum, von dem aus das Startdatum des Monats ermittelt werden soll
     * @return das Startdatum des Monats für das gegebene LocalDate
     * @throws IllegalArgumentException wenn localDate null ist
     * @author Leon
     */
    private LocalDate getStartOfMonth(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("Das angegebene Datum darf nicht null sein");
        }

        if (localDate.getDayOfMonth() == 1) {
            return localDate;
        } else {
            return getStartOfMonth(localDate.minusDays(1));
        }
    }

    /**
     * Gibt die Kalenderwoche (KW) aus der aktuellen Anzahl der Tage eines Jahres zurück
     *
     * @param tage die Tage, von dem aus die KW ermittelt werden soll
     * @return die KW
     * @author Leon
     */
    private int getKW(int tage) { //
        return (tage / 7) + 1;  // Wir teilen durch 7, damit haben wir die KW ermittelt
        // Zudem muss eine 1 noch aufgrund des Scopes hinzugefügt werden
    }
}
