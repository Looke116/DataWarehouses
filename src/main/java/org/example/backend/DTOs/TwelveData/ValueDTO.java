package org.example.backend.DTOs.TwelveData;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ValueDTO {

    private LocalDate datetime;

    private double open;

    private double high;

    private double low;

    private double close;

    private int volume;
}