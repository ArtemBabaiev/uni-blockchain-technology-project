package com.ababaiev.views.profile.models;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFundsModel {
    @Positive
    private Double funds;
}
