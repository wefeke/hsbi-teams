package com.example.application.services;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Veranstaltung;
import com.example.application.repositories.TeilnehmerRepository;
import com.example.application.repositories.VeranstaltungenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeilnehmerService {
    private final TeilnehmerRepository teilnehmerRepository;

    public TeilnehmerService(TeilnehmerRepository teilnehmerRepository) {
        this.teilnehmerRepository = teilnehmerRepository;
    }

    public List<Teilnehmer> findAllTeilnehmer() {
        return teilnehmerRepository.findAll();
    }

}
