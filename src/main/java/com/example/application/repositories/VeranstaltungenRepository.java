package com.example.application.repositories;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Das VeranstaltungenRepository Interface bietet Methoden zur Interaktion mit Veranstaltungsdaten in der Datenbank.
 * Es erweitert JpaRepository und definiert zusätzliche Methoden zum Abrufen von Veranstaltungen basierend auf Benutzer und Veranstaltungs-ID.
 *
 * @author Joris
 */
public interface VeranstaltungenRepository extends JpaRepository<Veranstaltung, Long> {
    List<Veranstaltung> findByUser(User user);
    Veranstaltung findByIdAndUser(Long Id, User user);
}
