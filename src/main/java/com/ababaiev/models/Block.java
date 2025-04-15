package com.ababaiev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String hash;
    private long nonce;
    @Column(columnDefinition = "TEXT")
    private String previousHash;
    private LocalDateTime timestamp;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] signature;

    @OneToMany(mappedBy = "block")
    private List<BlockTransaction> transactions;

    @Transient
    private boolean genesisBlock = false;
}
