package org.example.backend.Entities;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Data
public class Timeseries {

    public String assetId;
    public String sourceId;
    public LocalDate businessDate;
    public LocalDate systemDate;
    public Map<String, Integer> valuesInt;
    public Map<String, Double> valuesDouble;
    public Map<String, String> valuesText;

    public Timeseries(String assetId, String sourceId, LocalDate businessDate, Map<String, Integer> valuesInt, Map<String, Double> valuesDouble) {
        this.assetId = assetId;
        this.sourceId = sourceId;
        this.businessDate = businessDate;
        this.systemDate = LocalDate.now();
        this.valuesInt = valuesInt;
        this.valuesDouble = valuesDouble;
    }
}
