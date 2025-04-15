package com.ababaiev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "users")
@Entity
public class User {
    @Id
    private String id;
    private String username;
    private String password;

    private double balance;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] publicKey;

    @Enumerated(EnumType.STRING)
    private Role role;
}
