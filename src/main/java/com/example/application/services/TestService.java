package com.example.application.services;

import com.example.application.models.Test;
import com.example.application.repositories.TestRepository;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public List<Test> findAllTests(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return testRepository.findAll();
        } else {
            return testRepository.search(stringFilter);
        }
    }

    public long countTests() {
        return testRepository.count();
    }

    public void deleteTest(Test test) {
        testRepository.delete(test);
    }

    public void saveTest(Test test) {
        if (test == null) {
            System.err.println("Test is null. Are you sure you have connected your form to the application?");
            return;
        }
        testRepository.save(test);
    }

    public List<Test> findAllTests() {
        return testRepository.findAll();
    }
}
