package com.example.application.repositories;

import com.example.application.models.Teilnehmer;
import com.example.application.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeilnehmerRepository extends JpaRepository<Teilnehmer, Long> {
}
