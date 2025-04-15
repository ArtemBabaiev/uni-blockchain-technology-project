package com.ababaiev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class MeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int readingValue;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private UtilityType utilityType;

    @ManyToOne
    private User user;

    @Column(columnDefinition = "TEXT")
    private String hash;

    @OneToOne
    private MeterReading previousReading;

    @OneToOne(cascade = CascadeType.ALL)
    private Bill bill;
}
