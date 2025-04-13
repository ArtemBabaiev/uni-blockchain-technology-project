package com.ababaiev.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double amountDue;

    private LocalDateTime billingDate;

    @OneToOne(mappedBy = "bill")
    private MeterReading meterReading;

    @OneToOne(mappedBy = "bill")
    private PendingTransaction pendingTransaction;

    @OneToOne(mappedBy = "bill")
    private BlockTransaction blockTransaction;

}
