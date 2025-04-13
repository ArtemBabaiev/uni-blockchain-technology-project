package com.ababaiev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BlockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private double amount;

    @Column(columnDefinition = "TEXT")
    private String hash;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] signature;

    @Enumerated(EnumType.STRING)
    private UtilityType utilityType;

    @ManyToOne
    private User user;

    @OneToOne
    private Bill bill;

    @ManyToOne
    private Block block;
}
