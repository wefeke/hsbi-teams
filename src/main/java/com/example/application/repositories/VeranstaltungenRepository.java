
package com.example.application.repositories;

import com.example.application.models.User;
import com.example.application.models.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VeranstaltungenRepository extends JpaRepository<Veranstaltung, Long> {
    List<Veranstaltung> findByUser(User user);
    Veranstaltung findByIdAndUser(Long Id, User user);
}
