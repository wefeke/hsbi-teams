package com.example.application.services;

import com.example.application.models.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.auswertung.Auswertung;
import com.example.application.views.auswertung.TGGPHelper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Der SuperService stellt verschiedene Funktionen bereit, die mehrere andere Services kombinieren.
 * Diese Klasse bietet Methoden zur Verwaltung und Auswertung von Veranstaltungen, Teilnehmern und Gruppenarbeiten.
 *
 * @author Leon
 */
@Service
public class SuperService {

    GruppeService gruppeService;
    VeranstaltungenService veranstaltungenService;
    TeilnehmerService teilnehmerService;
    GruppenarbeitService gruppenarbeitService;
    GruppenarbeitTeilnehmerService gruppenarbeitTeilnehmerService;
    UserService userService;
    AuthenticatedUser authenticatedUser;

    Optional<User> maybeUser;
    User user;

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

    private User validateUser(Optional<User> maybeUser) {
        return maybeUser.orElseGet(User::new);
    }

    /**
     * Findet alle Auswertungen für eine bestimmte Veranstaltung.
     * Diese Methode erstellt eine Liste von Auswertungen für jeden Teilnehmer der Veranstaltung, basierend auf den Gruppenarbeiten und den entsprechenden Punkten.
     *
     * @param id die ID der Veranstaltung
     * @return eine Liste von Auswertungen
     *
     * @author Leon
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
            auswertung.setVorname( teilnehmer.getVorname());
            auswertung.setNachname(teilnehmer.getNachname());
            auswertung.setMatrikelnummer(teilnehmer.getId() );
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
                        auswertung.incrementAnzahlGruppenarbeiten();
                    }
                }
                Float punkt = gruppenarbeitTeilnehmerService.findPunkteByMatrikelNrAndGruppenarbeitId(teilnehmer.getId(), gruppenarbeit.getId());
                auswertung.addToGesamtPunkte(punkt);
                auswertung.addPunkte(punkt);
            }
            auswertung.setTggpHelper(tggpHelperList);
            auswertungen.add(auswertung);
        }
        return auswertungen;
    }
}
