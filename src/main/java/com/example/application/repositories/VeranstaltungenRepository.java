
package com.example.application.repositories;

import com.example.application.models.Test;
import com.example.application.models.Veranstaltung;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VeranstaltungenRepository extends JpaRepository<Veranstaltung, Long> {

}
