package com.example.application.services;

import com.example.application.models.Veranstaltungstermin;
import com.example.application.repositories.VeranstaltungsterminRepository;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeranstaltungsterminService {

    private final VeranstaltungsterminRepository veranstaltungsterminRepository;

    public VeranstaltungsterminService(VeranstaltungsterminRepository veranstaltungsterminRepository) {
        this.veranstaltungsterminRepository = veranstaltungsterminRepository;
    }

    public List<Veranstaltungstermin> findAllVeranstaltungstermine() {
        return veranstaltungsterminRepository.findAll();
    }

    public List<Veranstaltungstermin> findVeranstaltungstermineByVeranstaltungId(Long veranstaltungsId) {
        return veranstaltungsterminRepository.findVeranstaltungstermineByVeranstaltungVeranstaltungsId(veranstaltungsId);
    }

    @Transactional
    public void saveVeranstaltungstermin(Veranstaltungstermin veranstaltungstermin) {
        if (veranstaltungstermin != null) {
            veranstaltungsterminRepository.save(veranstaltungstermin);
        } else {
            System.err.println("Veranstaltungstermin is null. Are you sure you have connected your form to the application?");
        }
    }

}
