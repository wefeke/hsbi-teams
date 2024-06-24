package com.example.application.repositories;


import com.example.application.models.User;
import com.example.application.models.Veranstaltungstermin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeranstaltungsterminRepository extends JpaRepository<Veranstaltungstermin, Long> {
    List<Veranstaltungstermin> findVeranstaltungstermineByVeranstaltungIdAndUser(Long id, User user);
    List<Veranstaltungstermin> findAllByUser(User user);
}