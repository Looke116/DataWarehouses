package org.example.backend.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.LocalDate;
import java.util.Map;

@Data
@CompoundIndex(
        name = "asset_businessDate_idx",
        def = "{'assetId':1,'businessDate':1}"
)
public class Timeseries {

    @Id
    private String id;
    private String assetId;
    private String sourceId;
    private LocalDate businessDate;
    private LocalDate systemDate;
    private Map<String, Integer> valuesInt;
    private Map<String, Double> valuesDouble;
    private Map<String, String> valuesText;
    private boolean deleted;

    public Timeseries(String assetId, String sourceId, LocalDate businessDate, Map<String, Integer> valuesInt, Map<String, Double> valuesDouble) {
        this.assetId = assetId;
        this.sourceId = sourceId;
        this.businessDate = businessDate;
        this.systemDate = LocalDate.now();
        this.valuesInt = valuesInt;
        this.valuesDouble = valuesDouble;
        deleted = false;
    }
}
