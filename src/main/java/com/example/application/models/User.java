//Autor: Kennet
package com.example.application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.ibatis.annotations.One;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users") // Ändern Sie den Tabellennamen auf "users"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    private boolean isAdmin;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    //Beziehungen
    @OneToMany(mappedBy = "user")
    private List<Veranstaltung> veranstaltungen = new ArrayList<>();

    public User() {
        //
    }

    public User(String username, String name, String hashedPassword, boolean isAdmin, Set<Role> roles, byte[] profilePicture, List<Veranstaltung> veranstaltungen) {
        this.username = username;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.isAdmin = isAdmin;
        this.roles = roles;
        this.profilePicture = profilePicture;
        this.veranstaltungen = veranstaltungen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return hashedPassword;
    }

    public void setPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Veranstaltung> getVeranstaltungen() {
        return veranstaltungen;
    }

    public void setVeranstaltungen(List<Veranstaltung> veranstaltungen) {
        this.veranstaltungen = veranstaltungen;
    }

    public void addVeranstaltungen (Veranstaltung veranstaltung) {
        this.veranstaltungen.add(veranstaltung);
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User that)) {
            return false; // null or not an AbstractEntity class
        }
        if (getId() != null) {
            return getId().equals(that.getId());
        }
        return super.equals(that);
    }




}
