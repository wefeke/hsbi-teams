package com.example.application.repositories;

import com.example.application.models.Gruppe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GruppenRepository extends JpaRepository<Gruppe, Long> {
}
