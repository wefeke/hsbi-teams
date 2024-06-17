package com.example.application.services;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.TeilnehmerRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class TeilnehmerService {
    private final TeilnehmerRepository teilnehmerRepository;
    private final VeranstaltungenService veranstaltungenService;

    public TeilnehmerService(TeilnehmerRepository teilnehmerRepository , VeranstaltungenService veranstaltungenService) {
        this.teilnehmerRepository = teilnehmerRepository;
        this.veranstaltungenService = veranstaltungenService;
    }

    public List<Teilnehmer> findAllTeilnehmer2() {
        return teilnehmerRepository.findAll();
    }

    //    public List<Teilnehmer> findAllTeilnehmer(String filterText) {
//        if (filterText == null || filterText.isEmpty()) {
//            return teilnehmerRepository.findAll();
//        } else {
//            return teilnehmerRepository.search(filterText);
//        }
//    }
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
    public void updateTeilnehmer(Teilnehmer teilnehmer) {
        if (teilnehmer != null) {
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

    public boolean isTeilnehmerInVeranstaltung(Teilnehmer teilnehmer) {
        return !teilnehmer.getVeranstaltungen().isEmpty();
    }

    @Transactional
    public List<Teilnehmer> findTeilnehmerByVeranstaltungId(Long id) {
        return teilnehmerRepository.findByVeranstaltungId(id);
    }

    public List<Teilnehmer> findAllTeilnehmerByUserAndFilter(User user, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return teilnehmerRepository.findByUser(user);
        } else {
            return teilnehmerRepository.searchByUser(user, filterText);
        }
    }

    public Veranstaltung findTeilnehmerById(Long id, User user) {
        return teilnehmerRepository.findByIdAndUser(id, user);
    }

    public List<Teilnehmer> findAllTeilnehmerByUser(User user) {
        return teilnehmerRepository.findByUser(user);
    }

    public List<Teilnehmer> findStudierendeOhneVeranstaltung(User user) {
        return teilnehmerRepository.findStudierendeOhneVeranstaltung(user);
    }

    public boolean isTeilnehmerInGruppenarbeit(Teilnehmer teilnehmer, Long veranstaltungId) {
        return teilnehmerRepository.isTeilnehmerInGruppenarbeit(teilnehmer.getId(), veranstaltungId);
    }

    public List<Teilnehmer> findStudierendeVorJahren(int years, User user) {
        return teilnehmerRepository.findStudierendeVorJahren(LocalDateTime.now().minusYears(years), user);
    }

    public List<Teilnehmer> findAllTeilnehmerNotInVeranstaltung(Long veranstaltungId, User user) {
        return teilnehmerRepository.findAllTeilnehmerNotInVeranstaltung(veranstaltungId, user);
    }

    @Transactional
    public void addTeilnehmerToVeranstaltung(Teilnehmer teilnehmer, Long veranstaltungId, User user) {
        Veranstaltung veranstaltung = veranstaltungenService.findVeranstaltungById(veranstaltungId, user);
        if (veranstaltung != null) {
            veranstaltung.getTeilnehmer().add(teilnehmer);
            teilnehmer.getVeranstaltungen().add(veranstaltung); // Update the participant's list of events
            veranstaltungenService.saveVeranstaltung(veranstaltung);
            teilnehmerRepository.save(teilnehmer); // Save the participant
        } else {
            throw new IllegalArgumentException("Invalid Veranstaltung Id:" + veranstaltungId);
        }
    }

    public Optional<Teilnehmer> findTeilnehmerByIdAndVornameAndNachname(Long id, String vorname, String nachname, User user) {
        return teilnehmerRepository.findTeilnehmerByIdAndVornameAndNachnameAndUser(id, vorname, nachname, user);
}

    public Optional<Teilnehmer> findByMatrikelNrAndUserId(Long matrikelNr, Long userId) {
        return teilnehmerRepository.findByMatrikelNrAndUserId(matrikelNr, userId);
    }

    public Optional<Teilnehmer> findTeilnehmerByVornameAndNachname(String vorname, String nachname, User user) {
        return teilnehmerRepository.findTeilnehmerByVornameAndNachnameAndUser(vorname, nachname, user);
    }

    public List<Teilnehmer> findAllTeilnehmerByVornameAndNachname(String vorname, String nachname, User user) {
        return teilnehmerRepository.findAllByVornameAndNachnameAndUser(vorname, nachname, user);
    }

    public Optional<Teilnehmer> findTeilnehmerByNachname(String nachname, User user) {
        return teilnehmerRepository.findTeilnehmerByNachnameAndUser(nachname, user);
    }

}



