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

}
