//Autor: Lilli

package com.example.application.repositories;

import com.example.application.models.Gruppe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GruppenRepository extends JpaRepository<Gruppe, Long> {
    @Query("SELECT g FROM Gruppe g LEFT JOIN FETCH g.teilnehmer WHERE g.id = :id")
    Gruppe findByIdWithTeilnehmer(@Param("id") Long id);
}
