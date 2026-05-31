package org.example.backend;

import org.example.backend.DTOs.TrendAnalysisDto;
import org.example.backend.Entities.Timeseries;
import org.example.backend.Repositories.TimeseriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Service
public class AnalyticsService {

    //    private final ProviderRepository providerRepository;
//    private final AssetRepository assetRepository;
    private final TimeseriesRepository timeseriesRepository;

    @Autowired
    public AnalyticsService(//ProviderRepository providerRepository,
//                         AssetRepository assetRepository,
                            TimeseriesRepository timeseriesRepository) {
//        this.providerRepository = providerRepository;
//        this.assetRepository = assetRepository;
        this.timeseriesRepository = timeseriesRepository;
    }

    public TrendAnalysisDto analyzeAsset(String assetId, LocalDate start, LocalDate end) {

        if (start == null) start = LocalDate.now();
        if (end == null) end = LocalDate.EPOCH;

        if (start.isAfter(end)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        Aggregation aggregation =
                Aggregation.newAggregation(
                        match(Criteria.where("assetId").is(assetId)),
                        group("assetId")
                                .avg("close").as("avgClose")
                                .max("close").as("maxClose")
                                .min("close").as("minClose")
                );
        List<Timeseries> points = timeseriesRepository.findByAssetIdAndDeletedAndBusinessDateBetweenOrderByBusinessDateAsc(assetId, false, start, end);

        DoubleSummaryStatistics stats = points.stream().mapToDouble(x -> x.getValuesDouble().get("Close")).summaryStatistics();

        double first = points.getFirst().getValuesDouble().get("Close");

        double last = points.getLast().getValuesDouble().get("Close");

        double percentChange = ((last - first) / first) * 100;

        TrendAnalysisDto trend = TrendAnalysisDto.builder().assetId(assetId).averagePrice(stats.getAverage()).minPrice(stats.getMin()).maxPrice(stats.getMax()).percentChange(percentChange).trend(percentChange > 0 ? "UPWARD" : "DOWNWARD").build();

        trend.setVolatility(calculateVolatility(points));
        trend.setRisk(classifyRisk(trend.getVolatility()));
        trend.setForecast(naiveForecast(points));
        trend.setStartDate(points.getFirst().getBusinessDate());
        trend.setEndDate(points.getLast().getBusinessDate());
        trend.setDataPoints(points.size());
        trend.setCurrentPrice(points.getLast().getValuesDouble().get("Close"));
        trend.setTrendStrength(trendStrength(trend.getPercentChange()));
        return trend;
    }

    public double calculateVolatility(List<Timeseries> points) {

        double mean = points.stream().mapToDouble(x -> x.getValuesDouble().get("Close")).average().orElse(0);

        double variance = points.stream().mapToDouble(x -> Math.pow(x.getValuesDouble().get("Close") - mean, 2)).average().orElse(0);

        return Math.sqrt(variance);
    }

    public String classifyRisk(double volatility) {

        if (volatility < 2) return "LOW";

        if (volatility < 5) return "MEDIUM";

        return "HIGH";
    }

    public double naiveForecast(List<Timeseries> points) {

        int size = points.size();

        double last = points.get(size - 1).getValuesDouble().get("Close");

        double previous = points.get(size - 2).getValuesDouble().get("Close");

        double delta = last - previous;

        return last + delta;
    }

    public String trendStrength(double percentChange) {
        if (Math.abs(percentChange) < 5)
            return "WEAK";

        if (Math.abs(percentChange) < 20)
            return "MODERATE";

        return "STRONG";
    }
}
