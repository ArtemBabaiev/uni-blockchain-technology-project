package com.ababaiev.views.bill.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PayBillModel {
    private List<UUID> billIds;
    private byte[] privateKey;

}
