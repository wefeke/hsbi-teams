
package com.example.application.services;

import com.example.application.models.Veranstaltung;
import com.example.application.models.Veranstaltungstermin;
import com.example.application.repositories.VeranstaltungenRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeranstaltungenService {

    private final VeranstaltungenRepository veranstaltungenRepository;

    public VeranstaltungenService(VeranstaltungenRepository veranstaltungenRepository) {
        this.veranstaltungenRepository = veranstaltungenRepository;
    }

    public List<Veranstaltung> findAllVeranstaltungen() {

            return veranstaltungenRepository.findAll();

    }

    public Veranstaltung findVeranstaltungById(Long id) {
        return veranstaltungenRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Veranstaltung Id:" + id));
    }

    public void saveVeranstaltung(Veranstaltung veranstaltung) {
        if (veranstaltung != null) {
            veranstaltungenRepository.save(veranstaltung);
        }
        else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

    public void deleteVeranstaltung(Veranstaltung veranstaltung) {
        if (veranstaltung != null) {
            veranstaltungenRepository.delete(veranstaltung);
        }
        else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

}
