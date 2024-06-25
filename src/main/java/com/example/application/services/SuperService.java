package com.example.application.services;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.auswertung.Auswertung;
import com.example.application.views.auswertung.TGGPHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Der SuperService stellt verschiedene Funktionen bereit, die mehrere andere Services kombinieren.
 * Diese Klasse bietet Methoden zur Verwaltung und Auswertung von Veranstaltungen, Teilnehmern und Gruppenarbeiten.
 *
 * @autor Leon
 */
@Service
public class SuperService {

    private final GruppeService gruppeService;
    VeranstaltungenService veranstaltungenService;
    TeilnehmerService teilnehmerService;
    GruppenarbeitService gruppenarbeitService;
    GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    UserService userService;
    AuthenticatedUser authenticatedUser;

    Optional<User> maybeUser;
    User user;

    /**
     * Konstruktor für den SuperService.
     * Initialisiert die verschiedenen Services und den AuthenticatedUser.
     *
     * @param veranstaltungenService der Service für die Veranstaltungen
     * @param teilnehmerService der Service für die Teilnehmer
     * @param gruppenarbeitService der Service für die Gruppenarbeiten
     * @param gruppenarbeitTeilnehmerService der Service für die Gruppenarbeitsteilnehmer
     * @param userService der Service für die Benutzer
     * @param authenticatedUser der aktuell authentifizierte Benutzer
     * @param gruppeService der Service für die Gruppen
     *
     * @autor Leon
     */
    public SuperService(VeranstaltungenService veranstaltungenService,
                        TeilnehmerService teilnehmerService,
                        GruppenarbeitService gruppenarbeitService,
                        GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService,
                        UserService userService, AuthenticatedUser authenticatedUser, GruppeService gruppeService) {
        this.veranstaltungenService = veranstaltungenService;
        this.teilnehmerService = teilnehmerService;
        this.gruppenarbeitService = gruppenarbeitService;
        this.gruppenarbeitTeilnehmerService = gruppenarbeitTeilnehmerService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.gruppeService = gruppeService;
    }

    /**
     * Validiert den authentifizierten Benutzer.
     * Wenn der Benutzer vorhanden ist, wird er zurückgegeben. Andernfalls wird ein neuer Benutzer erstellt und zurückgegeben.
     *
     * @param maybeUser der optionale authentifizierte Benutzer
     * @return der validierte Benutzer
     *
     * @autor Leon
     */
    private User validateUser(Optional<User> maybeUser) {
        if (maybeUser.isPresent()) {
            return maybeUser.get();
        } else {
            return new User();
        }
    }

    /**
     * Findet alle Auswertungen für eine bestimmte Veranstaltung.
     * Diese Methode erstellt eine Liste von Auswertungen für jeden Teilnehmer der Veranstaltung, basierend auf den Gruppenarbeiten und den entsprechenden Punkten.
     *
     * @param id die ID der Veranstaltung
     * @return eine Liste von Auswertungen
     *
     * @autor Leon
     */
    public List<Auswertung> findAllAuswertungenByVeranstaltung(Long id) {
        maybeUser = authenticatedUser.get();
        user = validateUser(maybeUser);
        Veranstaltung veranstaltung = veranstaltungenService.findVeranstaltungById(id, user);
        List<Teilnehmer> teilnehmern = teilnehmerService.findTeilnehmerByVeranstaltungId(id);

        // Jeder Veranstaltungstermin kann eine Gruppenarbeit haben und dann auch bewertet werden
        List<Veranstaltungstermin> veranstaltungstermine = veranstaltung.getVeranstaltungstermine();


        List<TGGPHelper> tggpHelperList = new ArrayList<>();

        // Erstellen der Liste von TGGPHelper für alle Veranstaltungstermine und Gruppenarbeiten
        for (Veranstaltungstermin veranstaltungstermin : veranstaltungstermine) {
            List<Gruppenarbeit> gruppenarbeiten = veranstaltungstermin.getGruppenarbeiten();
            for (Gruppenarbeit gruppenarbeit : gruppenarbeiten) {
                TGGPHelper tggpHelper = new TGGPHelper();
                tggpHelper.setVeranstaltungtermin(veranstaltungstermin);
                tggpHelper.setGruppenarbeit(gruppenarbeit);
                tggpHelperList.add(tggpHelper);
            }
        }

        List<Auswertung> auswertungen = new ArrayList<>();
        // Dies ist die Liste aller Auswertungen, wobei jede
        // einzelne Auswertung für alle Gruppenarbeiten Gruppennummer und Punkte bekommt

        // Für jeden Teilnehmer aus der Veranstaltung wird eine Auswertung angelegt
        for (Teilnehmer teilnehmer : teilnehmern) {
            Auswertung auswertung = new Auswertung(); // Eine neue frische Auswertung wird erzeugt
            // Name und Matrikelnummer werden festgelegt
            auswertung.setNameMatrikelnummer( // Auswertung bekommt ihren Teilnehmer mit Name und Matrikelnummer
                    teilnehmer.getVorname() + " " + teilnehmer.getNachname() + "\n" + "(" + teilnehmer.getId() + ")");
            // Hinzufügen der Gruppen und Punkte zu den Auswertungen
            for (int i = 0;i<=tggpHelperList.size();i++) {
                auswertung.addGruppeNummer(0L);
            }
            for (int i = 0;i<tggpHelperList.size();i++) {// Jeder TggpHelper wird überschrieben mit den nachfolgenden Werten für die spezielle Auswertung des Teilnehmers

                Gruppenarbeit gruppenarbeit =  tggpHelperList.get(i).getGruppenarbeit(); // Zunächst wird die jeweilige Gruppenarbeit aus dem jeweiligen TggpHelper genommen

                List<Gruppe> gruppen2 = gruppenarbeit.getGruppen();
                for (Gruppe gruppe : gruppen2) {
                    Set<Teilnehmer> teilnehmer2 = gruppe.getTeilnehmer();
                    if (teilnehmer2.contains(teilnehmer)) {
                        auswertung.addGruppeNummer(gruppe.getNummer(),i);
                    }
                }

/*
                if (gruppenarbeit != null) { // Check, ob nicht null
                    List<Gruppe> gruppen = gruppenarbeit.getGruppen(); // Alle Gruppen der jeweiligen Gruppenarbeit werden zwischen gespeichert
                    for (Gruppe gruppe : gruppen) {
                        for (Teilnehmer teilnehmerInGruppe : gruppe.getTeilnehmer()) { // Für jeden Teilnehmer in der jeweiligen Gruppe wird geschaut, ob sie der gleiche Teilnehmer sind.
                            if (teilnehmerInGruppe.getId().longValue() == teilnehmer.getId().longValue()) {
                                tggpHelper.setGruppe(gruppe); // Erst dann wird die Gruppe dem TggpHelper hinzugefügt
                                auswertung.addGruppeNummer(gruppe.getNummer()); // Nun wird auch die GruppenNummer hinzugefügt
                                auswertung.incrementAnzahlGruppenarbeiten(); // Ebenso wird die Anzahl der Gruppenarbeiten hochgezählt für den Teilnehmer
                            } else if (gruppenarbeit.equals(gruppe.getGruppenarbeit())){
                                tggpHelper.setGruppe(gruppe);  // Erst dann wird die Gruppe dem TggpHelper hinzugefügt
                                auswertung.addGruppeNummer(0L);
                            }
                        }
                    }

                }
*/
                Float punkt = gruppenarbeitTeilnehmerService.
                        findPunkteByMatrikelNrAndGruppenarbeitId(
                                teilnehmer.getId(), gruppenarbeit.getId());
                auswertung.addToGesamtPunkte(punkt);
                auswertung.addPunkte(punkt);
            }
            auswertung.setTggpHelper(tggpHelperList);
            auswertungen.add(auswertung);
        }
        System.out.println("hallo");
        return auswertungen;
    }
}
