package org.example.backend.DTOs.AlphaVantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AlphaVantageResponseDTO {

    @JsonProperty("Meta Data")
    private MetaData metaData;

//    @JsonProperty("Time Series (Daily)")
    @JsonProperty("Weekly Time Series")
    private Map<String, DailyPrice> timeSeries;
}
