package com.ababaiev.views.bill.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PayBillModel {
    private UUID billId;
    private byte[] privateKey;

}
