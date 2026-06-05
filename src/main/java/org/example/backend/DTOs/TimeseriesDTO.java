package org.example.backend.DTOs;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class TimeseriesDTO {
    public LocalDate businessDate;
    public Map<String, Long> valuesInt;
    public Map<String, Double> valuesDouble;
    public Map<String, String> valuesText;

    public TimeseriesDTO(LocalDate businessDate, Map<String, Long> valuesInt, Map<String, Double> valuesDouble, Map<String, String> valuesText) {
        this.businessDate = businessDate;
        this.valuesInt = valuesInt;
        this.valuesDouble = valuesDouble;
        this.valuesText = valuesText;
    }
}
