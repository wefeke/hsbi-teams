package com.example.application.services;

import com.example.application.models.Gruppe;
import com.example.application.repositories.GruppenRepository;

import org.springframework.stereotype.Service;

@Service
public class GruppeService {

    private final GruppenRepository gruppenRepository;

    public GruppeService(GruppenRepository gruppenRepository) {
        this.gruppenRepository = gruppenRepository;
    }

    public void save(Gruppe gruppe) {
        gruppenRepository.save(gruppe);
    }

    public Gruppe findGruppeByIdWithTeilnehmer(Long id) {
        return gruppenRepository.findByIdWithTeilnehmer(id);
    }
}
