//Autor: Leon
package com.example.application.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity

public class Test {
    @Id
    private int testid;
    private String testname;

    public Test() {

    }

    public long gettestid() {
        return testid;
    }

    public String gettestname() {
        return testname;
    }

    public void settestid(int testid) {
        this.testid = testid;
    }

    public void settestname(String testname) {
        this.testname = testname;
    }
}
