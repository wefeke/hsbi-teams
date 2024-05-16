package com.example.application.services;

import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.repositories.VeranstaltungsterminRepository;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeranstaltungsterminService {

    private final VeranstaltungsterminRepository veranstaltungsterminRepository;
    private final VeranstaltungenService veranstaltungService;

    public VeranstaltungsterminService(VeranstaltungsterminRepository veranstaltungsterminRepository, VeranstaltungenService veranstaltungService) {
        this.veranstaltungsterminRepository = veranstaltungsterminRepository;
        this.veranstaltungService = veranstaltungService;
    }

    public List<Veranstaltungstermin> findAllVeranstaltungstermine() {
        return veranstaltungsterminRepository.findAll();
    }

    public List<Veranstaltungstermin> findVeranstaltungstermineByVeranstaltungId(Long veranstaltungsId) {
        return veranstaltungsterminRepository.findVeranstaltungstermineByVeranstaltungVeranstaltungsId(veranstaltungsId);
    }

    public Veranstaltungstermin findVeranstaltungsterminById(Long id) {
        return veranstaltungsterminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Veranstaltungstermin Id:" + id));
    }

    @Transactional
    public void saveVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        if (veranstaltungstermin != null) {
            veranstaltungsterminRepository.save(veranstaltungstermin);
        } else {
            System.err.println("Veranstaltungstermin is null. Are you sure you have connected your form to the application?");
        }
    }

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
