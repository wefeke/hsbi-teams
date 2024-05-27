package com.example.application.repositories;

import com.example.application.models.Gruppenarbeit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GruppenarbeitRepository extends JpaRepository<Gruppenarbeit, Long> {
    @Query("SELECT g FROM Gruppenarbeit g LEFT JOIN FETCH g.gruppen WHERE g.id = :id")
    Gruppenarbeit findByIdWithGruppen(@Param("id") Long id);
}
