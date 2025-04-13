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
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String hash;
    private int nonce;
    @Column(columnDefinition = "TEXT")
    private String previousHash;
    private LocalDateTime timestamp;


    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<BlockTransaction> transactions;
}
