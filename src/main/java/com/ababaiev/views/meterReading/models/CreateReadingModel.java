package com.ababaiev.views.meterReading.models;

import com.ababaiev.models.UtilityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReadingModel {
    @NotNull
    private UtilityType utilityType;
    @Positive
    private int reading;
}
