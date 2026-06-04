package org.example.backend.Services;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.expressions.WindowSpec;
import org.apache.spark.sql.functions;
import org.example.backend.DTOs.TrendAnalysisDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AnalyticsService {

    private final SparkSession spark;

    @Autowired
    public AnalyticsService(SparkSession sparkSession) {
        this.spark = sparkSession;
    }

    public TrendAnalysisDto analyzeAsset(String assetId, LocalDate start, LocalDate end) {
        if (start == null) start = LocalDate.EPOCH;
        if (end == null) end = LocalDate.now();

        if (start.isAfter(end)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        Dataset<Row> df = spark.read()
                .format("mongodb")
                .option("database", "test")
                .option("collection", "collection")
                .load();

        WindowSpec versionWindow = Window.partitionBy("businessDate")
                .orderBy(functions.col("version").desc());
        Dataset<Row> filteredDf = df
                .filter(
                        df.col("assetId").equalTo(assetId)
                                .and(df.col("businessDate").geq(start.toString()))
                                .and(df.col("businessDate").leq(end.toString()))
                )
                .withColumn("row_num", functions.row_number().over(versionWindow))
                .filter(functions.col("row_num").equalTo(1))
                .sort(df.col("businessDate").asc())
                .drop("row_num");

        long count = filteredDf.count();
        if (count < 2) {
            throw new IllegalArgumentException("Insufficient historical data points found for analytics.");
        }

        Row stats = filteredDf.select(
                functions.min("valuesDouble.Close").alias("minPrice"),
                functions.max("valuesDouble.Close").alias("maxPrice"),
                functions.avg("valuesDouble.Close").alias("avgPrice"),
                functions.stddev_samp("valuesDouble.Close").alias("volatility")
        ).first();

        double minPrice = stats.isNullAt(0) ? 0.0 : stats.getDouble(0);
        double maxPrice = stats.isNullAt(1) ? 0.0 : stats.getDouble(1);
        double avgPrice = stats.isNullAt(2) ? 0.0 : stats.getDouble(2);
        double volatility = stats.isNullAt(3) ? 0.0 : stats.getDouble(3);

        Row firstRow = filteredDf.first();
        Row[] tailArray = (Row[]) filteredDf.tail(1);
        Row lastRow = tailArray[0];

        double startPrice = firstRow.getStruct(firstRow.fieldIndex("valuesDouble")).getAs("Close");
        double endPrice = lastRow.getStruct(lastRow.fieldIndex("valuesDouble")).getAs("Close");
        double percentChange = ((endPrice - startPrice) / startPrice) * 100.0;

        TrendAnalysisDto trend = new TrendAnalysisDto();
        trend.setAssetId(assetId);
        trend.setMinPrice(minPrice);
        trend.setMaxPrice(maxPrice);
        trend.setAveragePrice(avgPrice);
        trend.setVolatility(volatility);
        trend.setPercentChange(percentChange);
        trend.setStartDate(LocalDate.parse(firstRow.getAs("businessDate")));
        trend.setEndDate(LocalDate.parse(lastRow.getAs("businessDate")));
        trend.setDataPoints((int) count);
        trend.setCurrentPrice(endPrice);
        trend.setTrendStrength(determineTrendStrength(percentChange));
        trend.setRiskClassification(classifyRisk(volatility));
        trend.setForecastedPrice(naiveForecast(filteredDf, endPrice));

        return trend;
    }

    private String classifyRisk(double volatility) {
        if (volatility < 2.0) return "LOW";
        if (volatility < 5.0) return "MEDIUM";
        return "HIGH";
    }

    private String determineTrendStrength(double percentChange) {
        if (percentChange > 5.0) return "STRONG_BULLISH";
        if (percentChange > 1.0) return "BULLISH";
        if (percentChange < -5.0) return "STRONG_BEARISH";
        if (percentChange < -1.0) return "BEARISH";
        return "STABLE";
    }

    private double naiveForecast(Dataset<Row> filteredDf, double currentPrice) {
        Row[] lastTwoRows = (Row[]) filteredDf.tail(2);
        if (lastTwoRows.length < 2) return currentPrice;

        double previousPrice = lastTwoRows[0].getStruct(lastTwoRows[0].fieldIndex("valuesDouble")).getAs("Close");
        double delta = currentPrice - previousPrice;
        return currentPrice + delta;
    }
}