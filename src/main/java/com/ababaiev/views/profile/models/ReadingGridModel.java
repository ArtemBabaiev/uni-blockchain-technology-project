package com.ababaiev.views.profile.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReadingGridModel {
    private int readingValue;
    private LocalDateTime timestamp;
}
