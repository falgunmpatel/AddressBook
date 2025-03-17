package org.example.addressbook.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "users")
public class AuthUser {

    @Setter
    String firstName;
    @Setter
    String lastName;
    @Setter
    String email;
    @Setter
    String password;
    @Setter
    String hashPass;
    @Setter
    String token;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    public AuthUser() {
    }

    public AuthUser(String firstName, String lastName, String email, String password, String hashPass) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.hashPass = hashPass;

        this.token="";
        this.id = null;
    }

}