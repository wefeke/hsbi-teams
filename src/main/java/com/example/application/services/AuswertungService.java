package com.example.application.services;

import com.example.application.models.Auswertung;
import com.example.application.repositories.AuswertungRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AuswertungService {
    AuswertungRepository auswertungRepository;

    public AuswertungService(AuswertungRepository auswertungRepository) {
        this.auswertungRepository = auswertungRepository;
    }

    public List<Auswertung> findAllAuswertungen() {
        return auswertungRepository.findAllAuswertungen();
    }

    public List<Auswertung> findAllAuswertungenWithID(Long id) {
        return auswertungRepository.findAllAuswertungenWithID(id);
    }

    public void persistAuswertung(Auswertung auswertung) {
        if (auswertungRepository.existsById(auswertung.getMatrikelnummer())) {
            Auswertung fromDBAuswertung = auswertungRepository.getReferenceById(auswertung.getMatrikelnummer());
            fromDBAuswertung.setPunkte(auswertung.getPunkte());
        } else {
            auswertungRepository.save(auswertung);
        }
    }
}
