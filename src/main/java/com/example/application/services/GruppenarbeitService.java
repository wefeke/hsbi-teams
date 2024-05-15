package com.example.application.services;

import com.example.application.models.Gruppenarbeit;
import com.example.application.repositories.GruppenarbeitRepository;
import org.springframework.stereotype.Service;

@Service
public class GruppenarbeitService {
    private final GruppenarbeitRepository gruppenarbeitRepository;

    public GruppenarbeitService(GruppenarbeitRepository gruppenarbeitRepository) {
        this.gruppenarbeitRepository = gruppenarbeitRepository;
    }

    public void save(Gruppenarbeit gruppenarbeit) {
        gruppenarbeitRepository.save(gruppenarbeit);
    }
}
