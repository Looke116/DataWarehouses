package org.example.backend.DTOs;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrendAnalysisDto {

    private LocalDate startDate;

    private LocalDate endDate;

    private int dataPoints;

    private String assetId;

    private double currentPrice;

    private double averagePrice;

    private double minPrice;

    private double maxPrice;

    private double volatility;

    private double percentChange;

    private String trend;

    private String trendStrength;

    private String risk;

    private double forecast;

    public TrendAnalysisDto() {}
}