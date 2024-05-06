package com.example.application.repositories;


import com.example.application.models.Veranstaltungstermin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeranstaltungsterminRepository extends JpaRepository<Veranstaltungstermin, Long> {
    List<Veranstaltungstermin> findByVeranstaltungId(Long veranstaltungId);
}