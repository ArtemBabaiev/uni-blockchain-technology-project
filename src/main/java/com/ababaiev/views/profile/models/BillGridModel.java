package com.ababaiev.views.profile.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class BillGridModel {
    private UUID id;
    private Double amountDue;

    private LocalDateTime billingDate;

    private BillStatus status;
}
