
package com.example.application.repositories;

import com.example.application.models.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeranstaltungenRepository extends JpaRepository<Veranstaltung, Long> {
}
