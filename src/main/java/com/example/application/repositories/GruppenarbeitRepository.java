package com.example.application.repositories;

import com.example.application.models.Gruppenarbeit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Das GruppenarbeitRepository Interface bietet Methoden zur Interaktion mit Gruppenarbeit-Daten in der Datenbank.
 * Es erweitert JpaRepository und definiert eine zusätzliche Methode zum Abrufen einer Gruppenarbeit und ihrer zugehörigen Gruppen basierend auf der Gruppenarbeit-ID.
 *
 * @author Lilli
 */
public interface GruppenarbeitRepository extends JpaRepository<Gruppenarbeit, Long> {
    @Query("SELECT g FROM Gruppenarbeit g LEFT JOIN FETCH g.gruppen WHERE g.id = :id")
    Gruppenarbeit findByIdWithGruppen(@Param("id") Long id);
}
