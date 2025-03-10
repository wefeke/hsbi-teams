package com.example.application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;

/**
 * Die User Klasse repräsentiert einen Benutzer in der Anwendung.
 * Sie enthält Informationen wie Benutzername, Name, Passwort, Rollen und Profilbild.
 * Sie hat auch eine Beziehung zur Veranstaltung Klasse, die die Veranstaltungen repräsentiert, an denen der Benutzer teilnimmt.
 * Sie enthält auch Methoden zum Abrufen und Setzen dieser Informationen.
 *
 * @author Kennet
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "generator")
    @SequenceGenerator(name="generator", sequenceName = "GENERATOR", allocationSize = 50, initialValue = 100)
    @Column(name = "id", nullable = false)
    private Long id;
    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    private boolean locked;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    //Beziehungen
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Veranstaltung> veranstaltungen = new ArrayList<>();

    public User() {

    }

    public User(String username, String name, String hashedPassword, boolean locked, Set<Role> roles, byte[] profilePicture, List<Veranstaltung> veranstaltungen) {
        this.username = username;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.locked = locked;
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

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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
