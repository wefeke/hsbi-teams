//Autor: Kennet
package com.example.application.models;

import jakarta.persistence.*;
import org.apache.ibatis.annotations.One;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "username", nullable = false)
    private String username;
    private String password;
    private boolean isAdmin;

    //Beziehungen
    @OneToMany(mappedBy = "user")
    private List<Veranstaltung> veranstaltungen = new ArrayList<>();

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
