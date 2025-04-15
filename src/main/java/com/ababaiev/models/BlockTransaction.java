package com.ababaiev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "blockTransaction")
    private List<Bill> bills;

    @ManyToOne
    private Block block;
}
