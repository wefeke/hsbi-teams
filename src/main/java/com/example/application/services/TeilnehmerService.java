package com.example.application.services;

import com.example.application.models.Teilnehmer;
import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.TeilnehmerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class TeilnehmerService {
    private final TeilnehmerRepository teilnehmerRepository;

    public TeilnehmerService(TeilnehmerRepository teilnehmerRepository) {
        this.teilnehmerRepository = teilnehmerRepository;
    }
public List<Teilnehmer> findAllTeilnehmer2(){return teilnehmerRepository.findAll();}

//    public List<Teilnehmer> findAllTeilnehmer(String filterText) {
//        if (filterText == null || filterText.isEmpty()) {
//            return teilnehmerRepository.findAll();
//        } else {
//            return teilnehmerRepository.search(filterText);
//        }
//    }
    public Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr) {
        return teilnehmerRepository.findByMatrikelNr(matrikelNr);
    }

    public void saveTeilnehmer(Teilnehmer teilnehmer, User user) {
        if (teilnehmer != null) {
            teilnehmer.setUser(user);
            teilnehmerRepository.save(teilnehmer);
        }
        else
            System.err.println("Teilnehmer is null. Are you sure you have connected your form to the application?");
    }

    @Transactional
    public void deleteTeilnehmer(Teilnehmer teilnehmer) {
        if (teilnehmer != null) {
            teilnehmerRepository.delete(teilnehmer);
        }
        else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }
    public boolean isTeilnehmerInVeranstaltung(Teilnehmer teilnehmer) {
        return !teilnehmer.getVeranstaltungen().isEmpty();
    }
    @Transactional
    public List<Teilnehmer> findTeilnehmerByVeranstaltungId(Long id) {
        return teilnehmerRepository.findByVeranstaltungId(id);
    }
    public List<Teilnehmer> findStudierendeVorVierJahren() {
        LocalDateTime vierJahreZurueck = LocalDateTime.now().minusYears(4);
        return teilnehmerRepository.findStudierendeVorVierJahren(vierJahreZurueck);
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

}


