package com.example.application.repositories;

import com.example.application.models.Test;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {


    @Query("select s from Test s " +
            "where lower(s.testname) like lower(concat('%', :searchTerm, '%')) ")
    List<Test> search(@Param("searchTerm") String searchTerm);

}
