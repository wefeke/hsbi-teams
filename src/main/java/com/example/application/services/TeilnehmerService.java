package com.example.application.services;

import com.example.application.models.Teilnehmer;
import com.example.application.repositories.TeilnehmerRepository;
import org.springframework.stereotype.Service;
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

    public List<Teilnehmer> findAllTeilnehmer(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return teilnehmerRepository.findAll();
        } else {
            return teilnehmerRepository.search(filterText);
        }
    }
    public Optional<Teilnehmer> findByMatrikelNr(Long matrikelNr) {
        return teilnehmerRepository.findByMatrikelNr(matrikelNr);
    }

    @Transactional
    public void saveTeilnehmer(Teilnehmer teilnehmer) {
        if (teilnehmer != null) {
            teilnehmerRepository.save(teilnehmer);
        }
        else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

    @Transactional
    public void deleteTeilnehmer(Teilnehmer teilnehmer) {
        if (teilnehmer != null) {
            teilnehmerRepository.delete(teilnehmer);
        }
        else
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
    }

}


