package com.example.application.repositories;

import com.example.application.models.User;
import com.example.application.models.Veranstaltungstermin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Das VeranstaltungsterminRepository Interface bietet Methoden zur Interaktion mit Veranstaltungstermin-Daten in der Datenbank.
 * Es erweitert JpaRepository und definiert zusätzliche Methoden zum Abrufen von Veranstaltungsterminen basierend auf Veranstaltungs-ID und Benutzer, sowie zum Abrufen aller Veranstaltungstermine eines bestimmten Benutzers.
 *
 * @author Joris
 */
public interface VeranstaltungsterminRepository extends JpaRepository<Veranstaltungstermin, Long> {
    List<Veranstaltungstermin> findVeranstaltungstermineByVeranstaltungIdAndUser(Long id, User user);
    List<Veranstaltungstermin> findAllByUser(User user);
}