package com.example.application.services;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.TeilnehmerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Die TeilnehmerService Klasse bietet Methoden zur Interaktion mit Teilnehmerdaten in der Datenbank.
 * Sie enthält Methoden zum Abrufen, Speichern und Löschen von Teilnehmern, sowie zur Überprüfung der Teilnahme an Veranstaltungen.
 * Jede Methode ist als Transaktion gekennzeichnet, um die Datenintegrität zu gewährleisten.
 * Die Klasse verwendet das TeilnehmerRepository zur Interaktion mit der Datenbank.
 *
 * @author Tobias
 */
@Service
public class TeilnehmerService {
    private final TeilnehmerRepository teilnehmerRepository;

    public TeilnehmerService(TeilnehmerRepository teilnehmerRepository) {
        this.teilnehmerRepository = teilnehmerRepository;
    }


    @Transactional
    public Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr, User user) {
        return teilnehmerRepository.findTeilnehmerByMatrikelNrAndUser(matrikelNr, user);
    }

    @Transactional
    public void saveTeilnehmer(Teilnehmer teilnehmer, User user) {
        if (teilnehmer != null) {
            teilnehmer.setUser(user);
            teilnehmerRepository.save(teilnehmer);
        } else
            System.err.println("Teilnehmer is null. Are you sure you have connected your form to the application?");
    }

    @Transactional
    public void deleteTeilnehmer(Teilnehmer teilnehmer) {
        if (teilnehmer != null) {
            teilnehmerRepository.delete(teilnehmer);
        } else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

    @Transactional
    public boolean isTeilnehmerInVeranstaltung(Teilnehmer teilnehmer) {
        return !teilnehmer.getVeranstaltungen().isEmpty();
    }

    @Transactional
    public List<Teilnehmer> findTeilnehmerByVeranstaltungId(Long id) {
        return teilnehmerRepository.findByVeranstaltungId(id);
    }

    @Transactional
    public List<Teilnehmer> findAllTeilnehmerByUserAndFilter(User user, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return teilnehmerRepository.findByUser(user);
        } else {
            return teilnehmerRepository.searchByUser(user, filterText);
        }
    }

    @Transactional
    public List<Teilnehmer> findStudierendeOhneVeranstaltung(User user) {
        return teilnehmerRepository.findStudierendeOhneVeranstaltung(user);
    }

    @Transactional
    public boolean isTeilnehmerInGruppenarbeit(Teilnehmer teilnehmer, Long veranstaltungId) {
        return teilnehmerRepository.isTeilnehmerInGruppenarbeit(teilnehmer.getId(), veranstaltungId);
    }

    @Transactional
    public List<Teilnehmer> findStudierendeVorJahren(int years, User user) {
        List<Teilnehmer> teilnehmerList = teilnehmerRepository.findStudierendeVorJahren(LocalDateTime.now().minusYears(years), user);
        return teilnehmerList.stream()
                .filter(teilnehmer -> teilnehmer.getVeranstaltungen().isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Teilnehmer> findAllTeilnehmerNotInVeranstaltung(Long veranstaltungId, User user) {
        return teilnehmerRepository.findAllTeilnehmerNotInVeranstaltung(veranstaltungId, user);
    }
    @Transactional
    public List<Teilnehmer> findAllTeilnehmer(User user) {
        return teilnehmerRepository.findByUser(user);
    }

    @Transactional
    public Optional<Teilnehmer> findByMatrikelNrAndUserId(Long matrikelNr, Long userId) {
        return teilnehmerRepository.findByMatrikelNrAndUserId(matrikelNr, userId);
    }

    @Transactional
    public Optional<Teilnehmer> findTeilnehmerByVornameAndNachname(String vorname, String nachname, User user) {
        return teilnehmerRepository.findTeilnehmerByVornameAndNachnameAndUser(vorname, nachname, user);
    }

    public List<Veranstaltung> getVeranstaltungenOfTeilnehmer(Teilnehmer teilnehmer) {
        return new ArrayList<>(teilnehmer.getVeranstaltungen());
    }

}