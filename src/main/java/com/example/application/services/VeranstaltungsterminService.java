package com.example.application.services;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.repositories.VeranstaltungsterminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VeranstaltungsterminService {

    private final VeranstaltungsterminRepository veranstaltungsterminRepository;
    private final VeranstaltungenService veranstaltungService;

    public VeranstaltungsterminService(VeranstaltungsterminRepository veranstaltungsterminRepository, VeranstaltungenService veranstaltungService) {
        this.veranstaltungsterminRepository = veranstaltungsterminRepository;
        this.veranstaltungService = veranstaltungService;
    }

    @Transactional
    public List<Veranstaltungstermin> findAllVeranstaltungstermine(User user) {
        return veranstaltungsterminRepository.findAllByUser(user);
    }

    @Transactional
    public List<Veranstaltungstermin> findVeranstaltungstermineByVeranstaltungId(Long id, User user) {
        return veranstaltungsterminRepository.findVeranstaltungstermineByVeranstaltungIdAndUser(id, user);
    }

    @Transactional
    public Optional<Veranstaltungstermin> findVeranstaltungsterminById(Long id) {
        return veranstaltungsterminRepository.findById(id);
    }

    @Transactional
    public void saveVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        if (veranstaltungstermin != null) {
            veranstaltungsterminRepository.save(veranstaltungstermin);
        } else {
            System.err.println("Veranstaltungstermin is null. Are you sure you have connected your form to the application?");
        }
    }

    @Transactional
    public void deleteVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        if (veranstaltungstermin != null) {
            Veranstaltung veranstaltung = veranstaltungstermin.getVeranstaltung();
            if (veranstaltung != null) {
                veranstaltung.getVeranstaltungstermine().remove(veranstaltungstermin);
                veranstaltungService.saveVeranstaltung(veranstaltung);
            }
            veranstaltungsterminRepository.delete(veranstaltungstermin);
        } else {
            System.err.println("Veranstaltungstermin is null. Are you sure you have connected your form to the application?");
        }
    }


}
